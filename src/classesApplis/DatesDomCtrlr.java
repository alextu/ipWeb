import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSTimestamp;

/**
 * 
 * @author olive
 * @version 1.0
 * @category gestion des ctrlr de dates de domaines pour un user donn� (sem. impairs et sem. pairs)
 *
 */


public class DatesDomCtrlr implements NSKeyValueCoding {
    private static String fonction = "MODIF_DATES_IPW_DOM";
    private static String DATES_IP_DOM = "PARAM_DATES_DOM";
    private static String[] listeFonctions = new String[] {DATES_IP_DOM};

    private Session maSession;
    private FonctionsCtrlr ctlFonctions;
    private String cptLogin;

//    private NSArray listeDom;
    private NSArray listePSemDom;
    
    private PopUpDataAccessCtrlr leDACTdomaine;

    public DatesDomCtrlr(Session sess)  {

	maSession = sess;
	OngletsCtrlr mesOngCt = maSession.getMesOnglets();
	ctlFonctions = new FonctionsCtrlr(mesOngCt,listeFonctions);
	cptLogin = mesOngCt.cptLogin();	// on obtient le code de l'utilisateur dans ScolPeda depuis 
					// le controleur d'onglet (r�cup�r� lors de son init)
	
//	chargerListeDomaine();
    }

    // On charge la liste des dipl�mes auquel l'utilisateur courant a acc�s 
    // (via les droits �tablis dans ScolPedaDroits)
//    private void chargerListeDomaine() {
//	NSArray bindings = new NSArray(new Object[] {dlogKey,new Integer(maSession.getAnneeEnCours())});
//	EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
//		"dlogKey = %@ and fannKey = %@", bindings);
//
//	EOSortOrdering ordreDom = EOSortOrdering.sortOrderingWithKey("fdomLibelle",EOSortOrdering.CompareAscending);
//	NSArray sortOrderings = new NSArray(new Object[] {ordreDom});
//
//	EOFetchSpecification fetchSpec = new EOFetchSpecification("VDomaineVisibles",qualifier, sortOrderings);
//	fetchSpec.setRefreshesRefetchedObjects(true);
//
//	EOEditingContext ec = maSession.defaultEditingContext();
//
//	NSArray listeDom = ec.objectsWithFetchSpecification(fetchSpec);  
//    }

  // Obsolète depuis la V2.4...  
  //  public Integer getDlogKey() { return dlogKey; }
	public String getCptLogin() { return cptLogin; }

    
    public Integer getFannKey() { return (new Integer(maSession.getAnneeEnCours())); }
    
    // Creation du controleur pour le composant PopUpSelection des diplomes visibles...
    public PopUpDataAccessCtrlr getPopUpDACTDomaine() {
	if (leDACTdomaine == null) {
	    // Pour que �a marche, on doit pouvoir acc�der de l'ext�rieur aux propri�t�s "dlogKey" et "fannKey" !
	    leDACTdomaine = PopUpDaFact.creerPopUpDACT(PopUpDaFact.DACT_DOMAINE, maSession, false, this);
	    leDACTdomaine.fetcherLesEO(maSession.defaultEditingContext());
	}
//	NSLog.err.appendln("Recup DACT pour WOComp..............");
	return leDACTdomaine;
    }

    
    public NSArray listeDomainesVisibles() { return getPopUpDACTDomaine().listeEOFetches; }

    // 
    // Pr�viens de charger les dates pour le domaine LMD s�lectionn� (parit�e Paire et Impaire)...
    public void chargerDatesDom(String fdomCode) {
    	if (fdomCode != null) {
    		NSArray bindings = new NSArray(new Object[] {fdomCode, new Integer(maSession.getAnneeEnCours())});
    		EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
    				"fdomCode = %@ and fannKey = %@", bindings);

    		EOSortOrdering ordreSem = EOSortOrdering.sortOrderingWithKey("ipSemestre",EOSortOrdering.CompareDescending);
    		NSArray sortOrderings = new NSArray(new Object[] {ordreSem});

    		EOFetchSpecification fetchSpec = new EOFetchSpecification("IpDatesOuvertureDom",qualifier, sortOrderings);
    		fetchSpec.setRefreshesRefetchedObjects(true);

    		EOEditingContext ec = maSession.defaultEditingContext();

    		listePSemDom = ec.objectsWithFetchSpecification(fetchSpec);
    	}

    }
    
    public NSArray listePSemDom() { return listePSemDom; }
    
    ///////////////////////////////////////////////////////////////////////
    // PROCEDURES DE CHANGEMENT-VALIDATION DE DATES....
    ///////////////////////////////////////////////////////////////////////
    
    // Enregistrer les changements de dates sp�cifiques sur l'EO en question...
    public String changerDatesIpPourSem(EOGenericRecord lePSemDomChoisi, NSTimestamp nouvDateDebut,NSTimestamp nouvDateFin) {
	String erreur = null;
	NSTimestamp dateDebut, dateFin;
	
	// PO 2009 : on peut avoir les 2 dates de début et fin nulles : la période d'IP Web est alors indéterminée...
	// (cas du DEG début février 09)
	
	// V�rifier que les dates sont non nulles et l'ann�e bien correcte (>=2000 et <= anneeCourante+1)
	if (nouvDateDebut != null || nouvDateFin != null) {


		if (nouvDateDebut != null) {
			dateDebut = maSession.monApp.verificationDate(nouvDateDebut);
			if (dateDebut == null) return ("Pb avec la date : " +  maSession.monApp.tsFormat(nouvDateDebut));
		}
		else return ("La date de début de période des Ip Web doit être spécifiée !!! (Ou bien laisser vierge la date de fin, pour rendre la période indéfinie)");

		if (nouvDateFin != null) {
			dateFin = maSession.monApp.verificationDate(nouvDateFin);
			if (dateFin == null) return ("Pb avec la date : " +  maSession.monApp.tsFormat(nouvDateFin));
		}
		else return ("La date de fin de période des Ip Web doit être spécifiée !!!  (Ou bien laisser vierge la date de début, pour rendre la période indéfinie)");


		// V�rifier que la date de fin est apr�s la date de d�but ...
		if (dateFin.before(dateDebut)) 
			return ("La date de fin des IP Web (" +  maSession.monApp.tsFormat(dateFin) 
					+ ") ne peut être avant la date de début (" +  maSession.monApp.tsFormat(dateDebut) +") !");
	}
	else {
		dateDebut = null;
		dateFin = null;
	}

	// appel de la MAJ de la session : pour la bonne row, avec les nouvelles valeurs...
	maSession.monApp.majDatesIpDomPSem(lePSemDomChoisi, dateDebut, dateFin);
	
	// r�init des 2 EO correspondants pour le domaine...
	chargerDatesDom((String)lePSemDomChoisi.valueForKey("fdomCode"));
	
	return erreur;
    }

    // Pour le NSKeyValueCoding :
    public void takeValueForKey(Object arg0, String arg1) {
	// on s'en sert pas...
    }

    // on acc�de aux variables d'instances en direct !!!
    public Object valueForKey(String key) {
	return NSKeyValueCoding.DefaultImplementation.valueForKey(this, key);
    }


}
