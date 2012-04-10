package org.cocktail.ipweb.serveur.controlleur;
import java.util.Enumeration;

import org.cocktail.ipweb.serveur.Session;
import org.cocktail.ipweb.serveur.components.onglets.FonctionsCtrlr;
import org.cocktail.ipweb.serveur.components.onglets.OngletsCtrlr;

import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

/**
 * 
 * @author olive
 * @version 1.0
 * @category Le controleur associé au module d'envoi de mail
 *
 *///

public class EnvoiMailCtrl {

    private static String ENVOI_MAIL = "ENVOI_MAIL";
    private static String[] listeFonctions = new String[] {ENVOI_MAIL};
    private FonctionsCtrlr ctlFonctions;

	private Session maSession;
	private NSArray listeInscritsEC;
	private NSArray listeInscrits;

    // --- Pour les 2 composants PopUpMultiDiplSemUe...
    public Integer fspnKey, msemOrdre, fhabNiveau, anneeSuivie, msemKey;
    
    public EOGenericRecord eoDiplSelected;
    public EOGenericRecord eoEcSelected;
    
    private Integer mecKey;		// le mecKey associé à l'EC que l'on vient de sélectionner...
    
    // --- Elements du mail...
    public String sujetMail, texteMail;
    
    
    
	public EnvoiMailCtrl (Session sess) {

		maSession = sess;
		OngletsCtrlr mesOngCt = maSession.getMesOnglets();
		ctlFonctions = new FonctionsCtrlr(mesOngCt,listeFonctions);
	}
	
	public void selectionDiplAnnee () {
		System.out.println("On vient de sélectionner le dipl "+ fspnKey + ", pour l'année "+ fhabNiveau);
		fetcherListeInscritsDiplAnnee();
	}
	
	public void selectionEc() {
		System.out.println("On vient de sélectionner le dipl "+ fspnKey + ", pour le semestre "+ msemOrdre);
		fetcherListeInscritsEC();
	}

	   // Fetcher les étudiants selon les critères choisis...
    private void fetcherListeInscritsDiplAnnee() {
    	// TODO : AT fetcher en fonction de l'année !
		NSDictionary binding = new NSDictionary(
				new NSArray(new Object[] {fspnKey, fhabNiveau, new Integer(maSession.getAnneeEnCours())}),
				new NSArray(new String[] {"fspnKey", "fhabNiveau", "fannKey"}));
    	EOFetchSpecification fs = EOModelGroup.defaultGroup().fetchSpecificationNamed("etudInscSem", "vEtudInscSemestreResEmail");
		EOFetchSpecification fetchSpec = fs.fetchSpecificationWithQualifierBindings(binding);
		
		fetchSpec.setRefreshesRefetchedObjects(true);
		
		EOEditingContext ec = maSession.defaultEditingContext();
		listeInscrits = ec.objectsWithFetchSpecification(fetchSpec);
    }
	
    // Fetcher les étudiants selon les critères choisis pour les EC...
    private void fetcherListeInscritsEC() {
    	if (eoEcSelected != null) {
    		mecKey = (Integer)eoEcSelected.valueForKey("mecKey");
    		if (mecKey != null) {
    			NSDictionary binding = new NSDictionary(
    					new NSArray(new Object[] {mecKey, new Integer(maSession.getAnneeEnCours())}),
    					new NSArray(new String[] {"mecKey", "fannKey"}));
    			EOFetchSpecification fs = EOModelGroup.defaultGroup().fetchSpecificationNamed("fsListeInscEc", "VListeInscEc");
    			EOFetchSpecification fetchSpec = fs.fetchSpecificationWithQualifierBindings(binding);

    			fetchSpec.setRefreshesRefetchedObjects(true);

    			EOEditingContext ec = maSession.defaultEditingContext();
    			listeInscritsEC = ec.objectsWithFetchSpecification(fetchSpec);
     		}
    	}
    }
    
    private String listeMailInscritsEc() {
    	// concatener le contenu de la colonne "mailComplet" des étudiants inscrits à l'EC s'il y en a...
    	String res = null;
    	if (existeInscritsEC()) {
    		Enumeration e = listeInscritsEC.objectEnumerator();
    		boolean debut = true;

    		while (e.hasMoreElements()) {
    			EOGenericRecord eoEt = (EOGenericRecord)e.nextElement();
    			if (!debut) res += ",";
    			else {
    				debut = false;
    				res = "";
    			}
    			res += (String)eoEt.valueForKey("mailComplet");
    		}    
    	}
    	return res;    	
    }
    
	
    public int nbreInscritsEC() {
    	// renvoit 0 si pas d'inscrits fetchés...
    	if (listeInscritsEC == null) return 0;
    	return listeInscritsEC.count();
    }
    

    //  --------------------------------------------------------    
    //  ---------------- Expr. conditionnelles -----------------
    //  --------------------------------------------------------    
    
// Est-ce qu'on a choisi un dipl/sem ?
    public boolean listeDisponible() {
    	return (eoDiplSelected != null);
    }
    
 // Est-ce qu'on a choisi un EC ?
    public boolean listeDisponibleEC() {
    	return (eoEcSelected != null);
    }
    
    // Y a t-il des inscrits ?
    public boolean existeInscritsEC() {
    	return (nbreInscritsEC()>0);
    }
    
    // Peut-on continuer (quelque chose a été choisi)
    public boolean selectionDestinataires(boolean inscAuxEc) {
    	return ((listeDisponible() && !inscAuxEc) || (listeDisponibleEC() && inscAuxEc)) ;
    }
    
    //  ----- Gestion de l'envoi des mail ----
    public boolean validerEnvoiMail(boolean listeInscAuxEc) {   
    	if (listeInscAuxEc) return maSession.envoitMailListe(sujetMail, listeMailInscritsEc(), texteMail);
    	else return maSession.envoitMailListe(sujetMail, nomListeDiffusion(), texteMail); 
    }
    
    public void annulerEnvoiMail() {
    	nouveauMail();
    }
    
    public void nouveauMail() {
    	sujetMail = null;
    	texteMail = null;
    }
      
    
    //  --------------------------------------------------------    
    //  ------------------ Valeurs retournées ------------------
    //  --------------------------------------------------------    
    
    
    public String nomListeDiffusion() {
    	if (listeDisponible()) {
    		return ("liste-"+(Integer)eoDiplSelected.valueForKey("fspnKey") + "-" + 
    				(Integer)eoDiplSelected.valueForKey("fhabNiveau")+"@univ-nc.nc");
    	}
    	else return ("");
    }
    
    
    public String mailtoListeDiffusion() {
    	return ("mailto:"+nomListeDiffusion());
    }
    
    public NSArray getListeInscrits() {
		return listeInscrits;
	}
    
    public NSArray getListeInscritsEC() {
		return listeInscritsEC;
	}
    
}
