package org.cocktail.ipweb.serveur.controlleur;
/*
 * Cr�� le 28 mai 2006
 *
 * Objectif : g�rer la logique m�tier inh�rente aux individu (statut en particulier)
 *	Fait r�f�rence � la classe EOF Individu... � Etudiant et ScolInscriptionEtudiant
 *	Poss�de un controleur :
 *   - g�rant les adresses
 *   - g�rant les inscriptions	
 * 
 *  classe � adapter (moins de manips � faire que dans projet Profil...)
 *  
 */

/**
 * @author olive
 */

import org.cocktail.fwkcktlwebapp.common.CktlUserInfo;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.ipweb.serveur.Application;
import org.cocktail.ipweb.serveur.Session;
import org.cocktail.ipweb.serveur.components.onglets.OngletsCtrlr;
import org.cocktail.ipweb.serveur.metier.IpwIndividuUlr;
import org.cocktail.ipweb.serveur.metier.IpwPersonneTelephone;
import org.cocktail.ipweb.serveur.metier.VSituationsIndividu;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSTimestamp;

/* ATTENTE
import org.cocktail.scolarix.serveur.metier.eos.EOPreEtudiant;
import org.cocktail.scolarix.serveur.metier.eos.EOPreIndividu;
*/

public class IndividuCtrlr {
	
	// les situations possibles : 
	public static int PERSONNEL = 1; 
	public static int ENSEIGNANT = 2; 
	public static int VACATAIRE = 4; 
	public static int ETUDIANT = 8;

	protected int codeSituIndividu;
	
//	public static String[] libSituIndividu = new String[] {
//			"Personnel autoris�",
//			"Personnel enseignant",
//			"Vacataire",
//			"Etudiant(e)"};
	
	protected boolean preInscrit;
	/* ATTENTE
	protected EOPreEtudiant lePreEtudiant;
	*/
	
	protected String laSituIndividu, leMailCandidat;
//	protected EOGenericRecord compteIndividu;

	protected IpwIndividuUlr ind;
	
	/* ATTENTE
	protected EOPreIndividu preInd;
	*/
	
	protected VSituationsIndividu situIndividu;
	protected IpwPersonneTelephone eOTelEtud;

	private InscriptionCtrlr monCInsc;	// le ctrlr d'inscriptions...

	public CktlUserInfo user;
	protected Application criApp = (Application)Application.application();	// Pointeur pour acc�der 
	protected Session maSession;
	
//	protected boolean estUnPersonnel;	// TODO : � mixer avec les droits ScolPedagogie !!!
//	protected boolean estUnEnseignant;
//	protected boolean estUnVacataire;
//	protected boolean estUnEtudiant;

	//	protected boolean estUnAncien;		// etudiant, ens, non_ens, vac 
	
//	public static final int ACTU_ENS = 0;
//	public static final int ACTU_NON_ENS = 1;
//	public static final int ACTU_VAC = 2;
//	public static final int ANCIEN_ENS = 3;
//	public static final int ANCIEN_NON_ENS = 4;
//	public static final int ANCIEN_VAC = 5;
//	public static final int ACTU_ETUD = 6;
//	public static final int ANCIEN_ETUD = 7;
	
	protected String erreur;
	
	// Init de la classe, on charge l"individu courant"....
	// Charger effectivement les infos depuis l'EOModel, en fonction du persid...
	public IndividuCtrlr(Session sess, Number persId) {
	    maSession = sess;
	    user = maSession.connectedUserInfo();
	    preInscrit = false;
	    
	    ind = individuForPersId(persId);
	    
	    if (ind == null) erreur = "Erreur: Aucun individu trouvé pour le persId "+persId;
	    else { 
	    	erreur = "";
	    	situationPresenteIndividu();
	    	// Chargement des inscriptions �ventuelles (si ind = etudiant)
	    	if (estUnEtudiant()) monCInsc = new InscriptionCtrlr(maSession,(IpwIndividuUlr)ind);
	    	else monCInsc = null;
	    }
	}
	
	/* ATTENTE 
	// Init secondaire de la classe, on va travailler avec un IEtudiant ....
	// les infos viendront du framework ScolarixFwk ...
	public IndividuCtrlr(Session sess, EOPreEtudiant preInsc, String beaIne, String candEMail) {
	    maSession = sess;
	    user = null;
	    preInscrit = true;
	    lePreEtudiant = preInsc;
	    leMailCandidat = candEMail;
	    ind = null;
	    
	    if (lePreEtudiant != null && lePreEtudiant.toPreIndividu() != null) {
	    	// Cas ou le pré-inscrit est un réinscrit : il y a déjà les infos existantes chez nous !
	    	// on les retrouve par son n° INE et la row correspondante dans Grhum.etudiant...
	    	Number persId = chercherEtudiantAvecIne(beaIne);
	    	if (persId != null)
	    		ind = individuForPersId(persId);
	    	else preInd = lePreEtudiant.toPreIndividu();
	    }

    	erreur = "";
    	situationPresenteIndividu();
    	monCInsc = new InscriptionCtrlr(maSession,lePreEtudiant);
	}
	*/
	
	public boolean donneePreCandidat() {
		return (ind == null);
	}
	
	private Number chercherEtudiantAvecIne(String beaIne) {
		Number persid = null;

		NSArray bindings = new NSArray(new Object[] {beaIne});   	
		EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat("etudCodeIne = %@", bindings);
		EOFetchSpecification fetchSpec = new EOFetchSpecification("IpwEtudiant", qualifier, null);

		EOEditingContext ec = maSession.defaultEditingContext();
		NSArray enrEtudiant = ec.objectsWithFetchSpecification(fetchSpec);
		
		if (enrEtudiant != null && enrEtudiant.count()>0) {
			persid = (Number)((EOGenericRecord)enrEtudiant.objectAtIndex(0)).valueForKey("persId");
		}
		
		return persid;		
	}
	
	
	// tente d'initialiser un étudiant avec son N°etud (chercher si insc° dans l'année en cours)
	public IndividuCtrlr(Integer num_etud, Session sess) {
		maSession = sess;
       
		ind = individuForNumEtud(num_etud);
	    if (ind == null) erreur = "Erreur: Aucun étudiant ne possède le n° "+num_etud;
	    else { 
	    	erreur = "";
	    	codeSituIndividu = ETUDIANT;
			// Chargement des inscriptions �ventuelles (si ind = etudiant)
			monCInsc = new InscriptionCtrlr(maSession,(IpwIndividuUlr)ind);
			NSArray lesInsc = monCInsc.getInscriptionsCourantes();
			if (lesInsc == null || lesInsc.count()==0) {
				erreur = "Erreur: l'étudiant n° "+num_etud+" n'a aucune inscription pour l'année en cours !";
			}
	    }
	}
	
	public String getErreur() {
		return erreur;
	}
	
	// d�terminer la situation pr�sente de cet individu !
	// cas d'erreur rajout� : 9/2/07 => dossier du personnel non saisi pour la personne (cas ind�termin� !)
	
	private boolean situationPresenteIndividu() {
		if (!preInscrit) {
			situIndividu = ((IpwIndividuUlr)ind).vSituationsIndividu();
			codeSituIndividu = 0;

			// tester les situations, le code le + bas est le mieux...
			if (situIndividu.actuelNonEns().intValue() == 1) codeSituIndividu |= PERSONNEL;
//			if (situIndividu.actuelEns().intValue() == 1) codeSituIndividu |= (ENSEIGNANT+PERSONNEL) ; 
			if (situIndividu.actuelEns().intValue() == 1) codeSituIndividu |= (ENSEIGNANT) ; 
			if (situIndividu.actuelVac().intValue() == 1) codeSituIndividu |= VACATAIRE;
			if (situIndividu.actuelEtud().intValue() == 1) codeSituIndividu |= ETUDIANT;

			// si on est autre chose qu'un "pur" étudiant...
//			if (codeSituIndividu != ETUDIANT && codeSituIndividu != 0) {

			if (estUnEnseignant()) 
				System.out.println("****************** La personne logguèe est un enseignant ! **************** ");
			
			if (!estUnEtudiant()  && codeSituIndividu != 0) { 
				/// Cr�ation du controleur de droits et d'onglets en fonction du type de user...
				OngletsCtrlr mesOnglets = new OngletsCtrlr(maSession,this,user.login());
				maSession.setMesOnglets(mesOnglets);
			}

			if (codeSituIndividu == 0) return false;
			else return true;
		}
		else {
			codeSituIndividu = ETUDIANT;
			return true;
		}
	} 
	
	public String situationIndividu() {
		return laSituIndividu;
	}
	
	public boolean estUnEtudiant() {
		if (!preInscrit) {
			if (user == null) return false; // Cas ou l'on consulte l'eMail d'un étudiant en étant connecté comme personnel (mais avec un ind etudiant)
			else {
				String vl = user.vLan();
				if (vl != null && vl.compareTo("E") == 0) return true;
				else return false;
			}
		}
		else return true;
//		return ((codeSituIndividu&ETUDIANT)!=0);
	}

	public boolean estUnPersonnel() {
		return ((codeSituIndividu&PERSONNEL)!=0);
	}
	
	public boolean estUnEnseignant() {
		return ((codeSituIndividu&ENSEIGNANT)!=0);
	}

	public boolean estUnVacataire() {
		return ((codeSituIndividu&VACATAIRE)!=0);
	}

	
	public EOEditingContext ecSession() {
		return maSession.defaultEditingContext();
	}

	public InscriptionCtrlr monCInsc() {
		return monCInsc;
	}
	
	public boolean individuValide() { 
		return ((ind != null) || preInscrit); 
		}
	
	// La classe est d�j� initialis�es... mais on veut changer d'individu ! (superUser)
	public boolean changeIndividuForPersId(Number persId) {
		ind = individuForPersId(persId);
		if (ind == null) {
			erreur = "Erreur: Aucun individu trouvé pour le persId "+persId;
			return false;
		}
		else {
			// Quand cette m�thode r�ussit, elle lance une notification pour dire qu'un nouvel individu a �t� charg� !
			// Notification !
			NSNotificationCenter.defaultCenter().postNotification("nouvelIndividu",this);
//	    	NSLog.out.appendln("lancement Chgt individu !");
			}
		erreur = "";
		return true;
	}

	// cette méthode est dangereuse car on sauve tt d'un coup !
	// il faudrait avoir son propre contexte pour éviter de sauver d'autre chgt que ceux pour cet objet !
/*
	public void sauveChgt() {
		// dde de sauver les chgts...
    	maSession.defaultEditingContext().saveChanges();
	}
*/	
	
	private IpwIndividuUlr individuForPersId(Number persId) {
		EOEditingContext ec = maSession.defaultEditingContext();
		IpwIndividuUlr monInd = null;

		if (persId != null) {

			NSArray bPersId = new NSArray(persId);
			// fetch individuUlr...
			EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat("persId = %@)",bPersId );
			NSArray tmp = criApp.dataBus().fetchArray(ec,"IpwIndividuUlr",qualifier,null);
			if(tmp!=null && tmp.count()>0) {
				monInd = (IpwIndividuUlr)tmp.objectAtIndex(0);
			}

			// en profiter pour fetcher le n° de téléphone s'il existe...
			eOTelEtud = fetcherTelEtudiant(monInd);
//			compteIndividu = fetcherCompteEtudiant(monInd);
		}
		
		return monInd;
	}

	// pb ! le code ci dessous foire pour une raison inconnue (pb dans le modèle ?)
	/*
	private EOGenericRecord fetcherCompteEtudiant(IpwIndividuUlr monInd) {
		EOGenericRecord cpt = null;
		if (monInd != null) {
			NSArray arpt = monInd.toRptCompte();
			if (arpt != null && arpt.count() >0) {

				EOGenericRecord rptc = (EOGenericRecord)monInd.toRptCompte().objectAtIndex(0);
				cpt = (EOGenericRecord)rptc.valueForKey("toCompte");
			}
		}
		return cpt;
	}
	*/

	private IpwIndividuUlr individuForNumEtud(Integer num_etud) {
		EOEditingContext ec = maSession.defaultEditingContext();
		EOGenericRecord etu;
		IpwIndividuUlr monInd = null;
		NSArray bNumEtud = new NSArray(num_etud);
		// fetch individuUlr...
		EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat("etudNumero = %@)",bNumEtud );
		NSArray tmp = criApp.dataBus().fetchArray(ec,"IpwEtudiant",qualifier,null);
		if(tmp!=null && tmp.count()>0) {
			etu = (EOGenericRecord)tmp.objectAtIndex(0);
			Integer noIndividu = (Integer)etu.valueForKey("noIndividu");
			NSArray bNoInd = new NSArray(noIndividu);
			// fetch individuUlr...
			qualifier = EOQualifier.qualifierWithQualifierFormat("noIndividu = %@)",bNoInd );
			tmp = criApp.dataBus().fetchArray(ec,"IpwIndividuUlr",qualifier,null);
			if (tmp!=null && tmp.count()>0) {
				monInd = (IpwIndividuUlr)tmp.objectAtIndex(0);
			}
			// en profiter pour fetcher le n� de t�l�phone s'il existe...
			eOTelEtud = fetcherTelEtudiant(monInd);
		} 
		return monInd;
	}
	

	private IpwPersonneTelephone fetcherTelEtudiant(IpwIndividuUlr monInd) {
	    EOEditingContext ec = maSession.defaultEditingContext();
	    // construit les bindings :
	    NSMutableDictionary bindings = new NSMutableDictionary();
	    bindings.setObjectForKey(monInd.persId(), "persId");
	    
	    NSArray resultats = EOUtilities.objectsWithFetchSpecificationAndBindings
	    (ec, "IpwPersonneTelephone", "fsPersTel",bindings);

	    if (resultats!=null && resultats.count()>0) {
		return (IpwPersonneTelephone)resultats.objectAtIndex(0);
	    }
	    else return null;
	}

	public String getUserTelEtud() {
		// renvoyer le n° de tel fetché ou null si aucun ...

		if (eOTelEtud != null) {
			return (String)eOTelEtud.valueForKey("noTelephone");
		}
		/* ATTENTE
		else if (preInscrit) return lePreEtudiant.noTelephoneUniversitaireFixe();
		*/
		
		else return "( ? )";
	}


	public void setUserTelEtud(String numeroTelEtudiant) {
		// si non préInscrit, 3 cas : 
		// - soit le noTel = null ou est vide : on vire l'enregistrement et c'est tout !
	    // - soit le EO existe : il faut le supprimer d'abord puis cr�ation d'un nouvel EO (cl� primaire contient NO_TEL !!!)
	    // - soit cr�ation d'un nouvel EO

		EOEditingContext ec = maSession.defaultEditingContext();
		NSTimestamp dateCreate;
		Number lePersId;

		if (ind != null) {
			if (eOTelEtud != null) {
				dateCreate = eOTelEtud.dCreation();
				ec.deleteObject(eOTelEtud);
				eOTelEtud = null;
			}
			else {
				dateCreate = new NSTimestamp();
			}

			if (numeroTelEtudiant != null && numeroTelEtudiant.length()>0) {
				eOTelEtud = new IpwPersonneTelephone();
				ec.insertObject(eOTelEtud);
				eOTelEtud.completeInit(ind.persId(),numeroTelEtudiant,"TEL","ETUD",dateCreate);
				numeroTelEtudiant = eOTelEtud.noTelephone();
			}
		}
		maSession.commitChgt();
	}

/*
	// Casse l'encapsulation, mais n�cessaire pour l'instant...
	public IpwIndividuUlr eoindividuCourant() {
		return ind;
	}
*/

    /**
     * Renvoie le pr�nom de l'utilisateur actuellement connect�
     */
     public String getUserFirstName() {
     	if (ind != null) return ind.nomUsuel();
     	/* ATTENTE
   	    else if (preInscrit && lePreEtudiant != null) return lePreEtudiant.prenomNom();
   	    */
     	else return null;
     }

     /**
      * Renvoie le nom de l'utilisateur actuellement connecté (d'aprés userInfo de session)
      */
     public String getUserLastName() {
    	 if (ind != null) return ind.prenom();
    	 /* ATTENTE
    	 else if (preInscrit && lePreEtudiant != null) return "";
    	 */
    	 else return null;
     }

     // retourne le n° d'individu....
     public Number getNoIndividu() {
    	 if (ind != null) return ind.noIndividu();
    	 else return null;
     }

     // retourne le n° PERS_ID....
     public Number getPersId() {
    	 /* ATTENTE
    	 if (preInscrit && lePreEtudiant != null && lePreEtudiant.toPreIndividu() != null) 
    		 return lePreEtudiant.toPreIndividu().persId();
    	 else if (ind != null) return ind.persId();
    	 */
    	 if (ind != null) return ind.persId();
    	 else return null;
     }

     
 /*    
	public String indSituFam() {
		if (ind == null) return null;
		return ind.indCSituationFamille();
	}

	public String indActivite() {
		if (ind == null) return null;
		return ind.indActivite();
	}
*/     
	public String getIndPhoto() {
		if (ind != null) {
			return ind.indPhoto();
		}
		return null;
	}
	
	public NSTimestamp dNaissance() { 
		if (ind == null) return null;
//		else if (preInscrit && leIEtudiant != null && leIEtudiant.individu() != null) return leIEtudiant.
		return ind.dNaissance();
	}

	public String villeDeNaissance() { 
		if (ind != null) return ind.villeDeNaissance();
		else return null;
	}
	
	// la civilité...
	public String cCivilite() { 
		if (ind == null) return null;
		String laCiv=StringCtrl.capitalize(ind.cCivilite());
		return laCiv;
	}

	// fille = true, garçon = false
	public boolean estUneFille() {
		if (cCivilite().equals("M")) return false;
		else return true;
	}
		
	public String nomPatronymique() { 
		if (ind != null) return ind.nomPatronymique();
		/* ATTENTE
		else if (preInd != null) return preInd.nomPatronymique();
		*/
		else return null;
	}

	// On le met en "Init-Cap" directement dans la vue...
	public String prenom() { 
		if (ind != null) return ind.prenom();
		/* ATTENTE
		else if (preInd != null) return preInd.prenomAffichage();
		*/
		else return null;
	}
	
    public String emailEtudiant() {
		/* ATTENTE
    	if (lePreEtudiant != null && leMailCandidat != null) return leMailCandidat;
    	else if (estUnEtudiant()) {
    	*/
    	if (estUnEtudiant()) {
        	String email = maSession.email();
        	String dom = maSession.cptDomaine();
        	return email + "@" + dom;
    	}
    	else return "";
   }

}
