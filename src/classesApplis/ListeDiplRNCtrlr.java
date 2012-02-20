import java.util.Enumeration;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * @author olive / 14/03/2008
 * @version 1.0
 * @category gestion du ctrlr de diplomes donnant accès aux RN par le Web  
 *
 */


public class ListeDiplRNCtrlr {
//    private static String fonction = "LISTE_DIPL_AUTORN";
//    private static String RN_LISTEDIPL = "RN_LISTEDIPL";
//    private static String[] listeFonctions = new String[] {RN_LISTEDIPL};

    private Session maSession;
//    private FonctionsCtrlr ctlFonctions;
    private String cptLogin;

    private NSArray listeDiplAvecRN, listeDiplSansRN;
 
    private NSMutableDictionary listeEOipDiplSansRn;
    
    public ListeDiplRNCtrlr(Session sess) {
    	maSession = sess;
    	OngletsCtrlr mesOngCt = maSession.getMesOnglets();
//    	ctlFonctions = new FonctionsCtrlr(mesOngCt,listeFonctions);
    	cptLogin = mesOngCt.cptLogin();	// on obtient le code de l'utilisateur dans ScolPeda depuis 
    									// le controleur d'onglet (récupéré lors de son init)
    }

    public NSArray listeDiplomesAvecRN() { return listeDiplAvecRN; }
    public NSArray listeDiplomesSansRN() { return listeDiplSansRN; }

    public Integer getFannKey() { return new Integer(maSession.getAnneeEnCours()); }

    
    public void initListeDiplRN() {
    	chargerDiplRN();
    	
    	// On va se construire un dico des enreg. pour l'année en cours de IP_DIPL_SANS_RN...
    	NSArray bindings = new NSArray(new Object[] {getFannKey()});
    	EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
    			"fannKey = %@", bindings);

    	// la liste des diplomes qui n'autorisent pas les RN par le Web
    	EOFetchSpecification fetchSpec = new EOFetchSpecification("IpDiplSansRn",qualifier, null);
    	fetchSpec.setRefreshesRefetchedObjects(true);

    	EOEditingContext ec = maSession.defaultEditingContext();
    	NSArray res = ec.objectsWithFetchSpecification(fetchSpec);  
    	
		listeEOipDiplSansRn = new NSMutableDictionary();

		if (res != null && res.count()>0) {
    		
    		Enumeration e = res.objectEnumerator();
    		while (e.hasMoreElements())  {
    			IpDiplSansRn eoIpDiplSansRN = (IpDiplSansRn)e.nextElement(); 
    			Integer fspnKey = (Integer)eoIpDiplSansRN.fspnKey();
    			
    			listeEOipDiplSansRn.setObjectForKey(eoIpDiplSansRN, fspnKey);
    		}
    	}
    }
    
    // Préviens de charger la liste des diplomes avec RN ... et celle sans RN, pour l'année en cours
    // à chaque refetch, on raffraichit avec les datas de la base...
    public void chargerDiplRN() {

    	NSArray bindings = new NSArray(new Object[] {cptLogin, getFannKey()});
    	EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
    			"cptLogin = %@ and fannKey = %@", bindings);

    	EOSortOrdering ordreDipl = EOSortOrdering.sortOrderingWithKey("diplome",EOSortOrdering.CompareAscending);
    	NSArray sortOrderings = new NSArray(new Object[] {ordreDipl});

    	// la liste des diplomes qui n'autorisent pas les RN par le Web
    	EOFetchSpecification fetchSpec = new EOFetchSpecification("VDiplomeSansRn",qualifier, sortOrderings);
    	fetchSpec.setRefreshesRefetchedObjects(true);

    	EOEditingContext ec = maSession.defaultEditingContext();
    	listeDiplSansRN = ec.objectsWithFetchSpecification(fetchSpec);  

    	// la liste des diplomes qui autorisent les RN par le Web
    	fetchSpec = new EOFetchSpecification("VDiplomeAvecRn",qualifier, sortOrderings);
    	fetchSpec.setRefreshesRefetchedObjects(true);

    	listeDiplAvecRN = ec.objectsWithFetchSpecification(fetchSpec);
    	
    }

    // ON veut ajouter ces diplome à la liste des diplomes sans RN...
    public void ajouterRestriction(NSArray listeDipl) {
    	EOEditingContext ec = maSession.defaultEditingContext();
    	boolean travail = false;

    	Enumeration e = listeDipl.objectEnumerator();
    	while (e.hasMoreElements()) {
    		EOGenericRecord diplSel = (EOGenericRecord)e.nextElement();
//    		NSLog.out.appendln("A virer : "+diplSel.valueForKey("diplome"));
    		
    		// Pas déjà ?
    		Integer fspnKey = (Integer)diplSel.valueForKey("fspnKey");
    		if (listeEOipDiplSansRn.objectForKey(fspnKey) == null) {

    			IpDiplSansRn eoAAjouter = new IpDiplSansRn();
    			eoAAjouter.setFannKey((Number)diplSel.valueForKey("fannKey"));
    			eoAAjouter.setFspnKey((Number)fspnKey);
    			travail = true;
    			ec.insertObject(eoAAjouter);
    			listeEOipDiplSansRn.setObjectForKey(eoAAjouter, fspnKey);
    		}
    	}
    	if (travail) {
    		ec.saveChanges();
    		chargerDiplRN();
    	}
    }
        
    // ON veut enlever ces diplome de la liste des diplomes sans RN...
    public void enleverRestriction(NSArray listeDipl) {
    	EOEditingContext ec = maSession.defaultEditingContext();
    	boolean travail = false;
    	
    	Enumeration e = listeDipl.objectEnumerator();
    	while (e.hasMoreElements()) {
    		EOGenericRecord diplSel = (EOGenericRecord)e.nextElement();
  //  		NSLog.out.appendln("A ajouter : "+diplSel.valueForKey("diplome"));

    		if (listeEOipDiplSansRn != null) {
    			IpDiplSansRn eoAVirer = (IpDiplSansRn)listeEOipDiplSansRn.removeObjectForKey((Integer)diplSel.valueForKey("fspnKey"));
    			if (eoAVirer != null) {
    				ec.deleteObject(eoAVirer);
    				travail = true;
    			}
    		}
    	}
    	if (travail) {
    		ec.saveChanges();
    		chargerDiplRN();
    	}
    }
    
}
