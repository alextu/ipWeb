package org.cocktail.ipweb.serveur.controlleur;
import java.util.GregorianCalendar;

import org.cocktail.ipweb.serveur.Session;

import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSTimestamp;


/**
 * 
 * @author olive
 * @version 1.0
 * @category gestion des ctrlr de dates pour un semestre en particulier (autour de l'EO VDiplomeSemDates)
 *
 */

public class DateSemCtrlr {

    private Session maSession;
    private DatesDiplCtrlr ddctPere;
    private EOGenericRecord eoSemDates;		// lien vers l'EO control�...
    
    private Integer fannKey, fspnKey, msemOrdre;
    private String autoriseRdblt;
    
    private NSTimestamp dateDebutIpSem, dateFinIpSem, dateDebutIpDom, dateFinIpDom;  
    
    private boolean isIpActif, isIntegre;
    
    public DateSemCtrlr(Session sess, EOGenericRecord semDatesIp, DatesDiplCtrlr monCtPere) {
    	maSession = sess;
    	eoSemDates = semDatesIp;
    	ddctPere = monCtPere;

    	// s'enregistrer pour les notifs (chgt de dates pour les domaines LMD : rare mais possible dans la foul�e)
    	NSNotificationCenter.defaultCenter().addObserver(this,	// on doit me pr�venir moi-m�me !
    			new NSSelector("changeDateFDom",		// via cette m�thode
    					new Class [] {NSNotification.class}),		// argument obligatoire !!!
    					"chgtDateDom",					// la signature de la notif qui m�plait
    					maSession.monApp);				// instance de celui qui la poste !

    	inits();
    }
    
    // lancement des inits... (evtuellement r�entrant si refresh de l'EO)
    private void inits() {
    	fannKey = (Integer)eoSemDates.valueForKey("fannKey");
    	fspnKey = (Integer)eoSemDates.valueForKey("fspnKey");
    	msemOrdre = (Integer)eoSemDates.valueForKey("msemOrdre");
    	    	
    	autoriseRdblt = (String)eoSemDates.valueForKey("autoriseRedoublant");


    	//--
    	String res = (String)eoSemDates.valueForKey("ipActif");
    	if (res != null && (res.toUpperCase()).compareTo("O")==0) {
    		isIpActif = true;
    	}
    	else isIpActif = false;


    	//--
    	res = (String)eoSemDates.valueForKey("integrationIp");
    	if (res != null && (res.toUpperCase()).compareTo("O")==0) isIntegre= true;
    	else isIntegre= false;

    	//-- toutes les dates incluses...
    	dateDebutIpSem = libDate("ipDatesDebut");
    	dateFinIpSem = libDate("ipDatesFin");
    	dateDebutIpDom = libDate("ipDatesDebutDom");
    	dateFinIpDom = libDate("ipDatesFinDom"); 
    }

    private void reinits() {
	// reload de la row qui va bien pour l'EO support...
	eoSemDates = ddctPere.reloadDatesDiplSem(msemOrdre);
	
	// refaire les inits...
	inits();
    }
    

    // Msg dépend de l'état de autoriseRdblt...
    public String blocageRedoublantActif() {
    	if (autoriseRdblt == null || autoriseRdblt.equalsIgnoreCase("O"))
    		return "bloquer les IP des redoublants ?<br>(après le transfert d'ECTS et de notes, ne pas oublier de les débloquer !)";
    	else return "d&eacute;bloquer les IP des redoublants ?<br>(ATTENTION : envoi automatique d'un mail &agrave; chaque redoublant !)";
    }

    

    // Chgt de dates pour un domaine/parit� semestre � eu lieu...
    public void changeDateFDom(NSNotification laNotif) {
	NSDictionary userInfo = laNotif.userInfo();
	if (userInfo != null) {
	    String fdomCodeChange = (String)userInfo.objectForKey("fdomCode");
	    int pariteSemChange = ((Integer)userInfo.objectForKey("ipSemestre")).intValue();
	    
	    // est-ce que je suis concern� ?
	    String monFdom = (String)eoSemDates.valueForKey("fdomCode");
	    if (monFdom.compareTo(fdomCodeChange) == 0 && msemOrdre.intValue()%2==pariteSemChange) {
		reinits();
	    }
	}
    }
    
    public boolean estSemPair(){
	return (msemOrdre.intValue()%2==0);
    }
    
    /// Renvoie des valeurs de l'EO sous-jacent... ou des valeurs en cache si ne changent pas !
    ///--------------------------------------------
    public Integer getMsemOrdre() { return msemOrdre; }
    public NSTimestamp 	dateDebutIpSem() { return dateDebutIpSem; }
    public NSTimestamp 	dateFinIpSem() { return dateFinIpSem; }
    public NSTimestamp 	dateDebutIpDom() { return dateDebutIpDom; }
    public NSTimestamp 	dateFinIpDom() { return dateFinIpDom; }
   
    public String autoriseRdblt() { 
    	if (autoriseRdblt == null || autoriseRdblt.equalsIgnoreCase("O"))
    		return("OUI");
    	else return("NON");
    }

    
    // Lecture d'un champ date dans un EO...
    public NSTimestamp libDate(String nomChamp) {
	try {
	    NSTimestamp dateBase = (NSTimestamp)eoSemDates.valueForKey(nomChamp);
	    if (dateBase != null)
		// retourner l'heure correcte (selon qu'elle ait �t� rentr�e � la mano via TOAD ou bien via l'appli...)
		return maSession.monApp.conversionDateBDD(dateBase, 0, 0);
	    else return null;
	}
	catch (Exception e) {
	    // Pb d'absence de la colonne de ce nom
	    NSLog.err.appendln("erreur "+e.getMessage());
	    return null;
	}
    }
    
    /// Renvoie des �tats sur l'EO  sous-jacent... calcul�s et cach�s ou bien dynamiques (selon variabilit�)
    ///--------------------------------------------
    // Est-ce que l'EO en param fait partie des dipl/semestres "ip_actif" ?
    public boolean isIpActif() { return isIpActif;   }

    // Est-ce que l'EO en cours fait partie des dipl/semestres d�j� "int�gr�s" ?
    public boolean isIntegre() { return isIntegre;   }
    
    
    ///////////////////////////////////////////////////////////////////////
    // PROCEDURES DE CHANGEMENT-VALIDATION DE DATES....
    ///////////////////////////////////////////////////////////////////////
    
    // Enregistrer les changements de dates sp�cifiques sur l'EO en question...
    public String changerDatesIpPourSem(NSTimestamp nouvDateDebut,NSTimestamp nouvDateFin) {
    	String erreur = null;
    	NSTimestamp dateDebutGlobale, dateFinGlobale;

    	// V�rifier que l'ann�e est bien correcte (>=2000 et <= anneeCourante+1)
    	try {
    		dateDebutGlobale = verifierValiditeDate(nouvDateDebut, dateDebutIpDom);
    	}
    	catch (Exception e) {
    		return ("Pb avec la date : " +  maSession.monApp.tsFormat(nouvDateDebut));
    	}

    	try {
    		dateFinGlobale = verifierValiditeDate(nouvDateFin, dateFinIpDom);
    	}
    	catch (Exception e) {
    		return ("Pb avec la date : " +  maSession.monApp.tsFormat(nouvDateFin));
    	}

    	// V�rifier que la date de fin globale est apr�s la date de d�but globale...
    	if (dateFinGlobale != null && dateDebutGlobale != null) {
    		if (dateFinGlobale.before(dateDebutGlobale)) {
    			return ("La date de fin des IP Web (" +  maSession.monApp.tsFormat(dateFinGlobale) 
    					+ ") ne peut être avant la date de début (" +  maSession.monApp.tsFormat(dateDebutGlobale) +") !");
    		}
    	}
    	// PO 2009 : Soit toutes les dates gobales sont nulles, soit elles ne le sont pas !
    	else if ((dateFinGlobale == null && dateDebutGlobale != null) || (dateDebutGlobale == null && dateFinGlobale != null)) {
			return ("La période d'IP Web ne peut pas être à moitié définie !");
    	}

    	// OK : lancer mise � jour de la row qui va bien dans IP_DIPL_ENMODIF depuis la session
    	majDatesIpDiplEnModif(nouvDateDebut, nouvDateFin);

    	return erreur;
    }

    private NSTimestamp verifierValiditeDate(NSTimestamp dateIpSem, NSTimestamp dateIpDom) throws Exception {
    	if (dateIpSem != null) {
    		dateIpSem = maSession.monApp.verificationDate(dateIpSem);
    		if (dateIpSem == null) throw(new Exception("Pb avec cette date !!!"));
    		return dateIpSem;
    	}
    	else return dateIpDom;
    }

    // lancer mise � jour de la row qui va bien dans IP_DIPL_ENMODIF depuis la session (nouvelles dates dipl/sem)
    // REM IMPORTANTE : les nouvelles dates, si non null, correspondent (en temps local) � une date avec heure = 00:00 (saisie via formulaire!)
    //			ou bien d�j� � une date avec heure = 23:59 (non passage par formulaire)
    //			au moment de la sauvegarde, si la date de fin est non nulle ET avec heure = 00:00, passer son heure � 23:59 (heure locale)
    private void majDatesIpDiplEnModif(NSTimestamp nouvDateDebut,NSTimestamp nouvDateFin) {
	// ajustement de l'heure de fin : 
	if (nouvDateFin != null) {
	    if (maSession.monApp.zeroHeureDate(nouvDateFin))
		    nouvDateFin = nouvDateFin.timestampByAddingGregorianUnits(0, 0, 0, 23, 59, 0);
	}
	// appel de la MAJ de la session : pour la bonne row, avec les nouvelles valeurs...
	maSession.monApp.majDatesIpDiplEnModif(fannKey, fspnKey, msemOrdre, nouvDateDebut, nouvDateFin);
	
	// r�init de ce ctrlr...
	reinits();
    }

    public void changerDatesIpPourSem(NSTimestamp laNouvelleDate, boolean dateDebutConcernee) {
	// ajustement de l'heure de fin : non n�c�ssaire car on ne vient pas de passer par le filtre du formulaire
	// appel de la MAJ de la session : pour la bonne row, avec les nouvelles valeurs...
	maSession.monApp.majDatesIpDiplEnModif(fannKey, fspnKey, msemOrdre, laNouvelleDate, dateDebutConcernee);
	
	// r�init de ce ctrlr...
	reinits();
    }
    
    
    // Changer l'�tat IPWeb-actif de ce dipl/sem :
    public void changerActivite(boolean etatActivite) {
    	String etat;
    	if (etatActivite) etat = "O";
    	else etat = "N";

    	maSession.monApp.majActiviteIpDiplEnModif(fannKey, fspnKey, msemOrdre, etat);
    	// réinit de ce ctrlr...
    	reinits();
    }
    
    // On change la valeur dans la table et si passage de 'N' à 'O', envoit d'un mail aux étudiants...
    public void changerAutoriseIPRdblt(String diplSem) {
    	boolean envoitMail = false;
    	if (autoriseRdblt == null || autoriseRdblt.equalsIgnoreCase("O"))
    		autoriseRdblt = "N";
    	else {
    		autoriseRdblt = "O";
    		envoitMail = true;
    	}

    	maSession.monApp.majAutoriseIpRedoublant(fannKey, fspnKey, msemOrdre, autoriseRdblt);
    	
    	// test : 
    	// System.out.println(maSession.emailsRdbltPromo(fannKey, fspnKey, msemOrdre));
    	
    	if (envoitMail) maSession.prevenirParMailGroupRdbl(fannKey, fspnKey, msemOrdre,diplSem);
    	
    	// réinit de ce ctrlr...
    	reinits();
    }
    

}
