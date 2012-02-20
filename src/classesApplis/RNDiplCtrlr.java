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


/**
 * @author olive
 * @version 1.0
 * @category gestion du ctrlr d'autorisation des RN par diplome/semestre/parcours...  
 *
 */
public class RNDiplCtrlr implements NSKeyValueCoding {
    private static String fonction = "MODIF_AUTORN_DIPL";
    private static String RN_AUTOR = "RN_AUTORISE";
    private static String[] listeFonctions = new String[] {RN_AUTOR};

    private Session maSession;
    private FonctionsCtrlr ctlFonctions;
    private String cptLogin;

    private NSArray listeDipl;
    private NSArray listeDiplSemImpairs, listeDiplSemPairs;

    private Integer fspnKeyEnCours;

    
    public RNDiplCtrlr(Session sess) {

    	maSession = sess;
    	OngletsCtrlr mesOngCt = maSession.getMesOnglets();
    	ctlFonctions = new FonctionsCtrlr(mesOngCt,listeFonctions);
    	cptLogin = mesOngCt.cptLogin();	// on obtient le code de l'utilisateur dans ScolPeda depuis 
    									// le controleur d'onglet (récupéré lors de son init)
    }

    public NSArray listeDiplomesVisibles() { return listeDipl; }

    public Integer getFannKey() { return new Integer(maSession.getAnneeEnCours()); }
    
 //   public Integer getDlogKey() { return dlogKey; }
	public String getCptLogin() { return cptLogin; }

    // Préviens de charger les états d'autorisation pour les semestres de ce diplome...
    public void chargerAutornDipl(Integer fspnKey) {
    	fspnKeyEnCours = fspnKey;
    	NSArray bindings = new NSArray(new Object[] {cptLogin, getFannKey(), fspnKey});
    	EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
    			"cptLogin = %@ and fannKey = %@ and fspnKey = %@", bindings);

    	EOSortOrdering ordreSem = EOSortOrdering.sortOrderingWithKey("msemOrdre",EOSortOrdering.CompareAscending);
    	EOSortOrdering ordrePar = EOSortOrdering.sortOrderingWithKey("mparAbreviation",EOSortOrdering.CompareDescending);
    	EOSortOrdering ordreParLib = EOSortOrdering.sortOrderingWithKey("mparLibelle",EOSortOrdering.CompareAscending);
    	NSArray sortOrderings = new NSArray(new Object[] {ordreSem,ordrePar,ordreParLib});

    	EOFetchSpecification fetchSpec = new EOFetchSpecification("VipDiplparsem",qualifier, sortOrderings);
    	fetchSpec.setRefreshesRefetchedObjects(true);

    	EOEditingContext ec = maSession.defaultEditingContext();

    	NSArray detailDipl = ec.objectsWithFetchSpecification(fetchSpec);  

    	// créer 2 listes de rows au niveau semestres avec les résultats (les impairs et les pairs)
    	// on enlève les rows "parcours commun" quand elle suivent des rows "parcours specialisé" pour le même msem_ordre

    	if (detailDipl != null && detailDipl.count()>0) {
    		NSMutableArray listeSemImp = new NSMutableArray(), listeSemPair = new NSMutableArray();
    		Enumeration e = detailDipl.objectEnumerator();
    		int oldSemOrdre = -1;
    		while (e.hasMoreElements()) {
    			EOGenericRecord autornDipl = (EOGenericRecord)e.nextElement();
    			int parite = maSession.monApp.recuperer1int(autornDipl, "pariteSem");
    			int semOrdre = maSession.monApp.recuperer1int(autornDipl, "msemOrdre");
    			String typePar = maSession.monApp.recuperer1String(autornDipl,"mparAbreviation");
    			
    			if (typePar.length() == 0 || oldSemOrdre != semOrdre) {
    				if (parite == 0)
    					listeSemPair.addObject(autornDipl);
    				else listeSemImp.addObject(autornDipl);
    			}
    			oldSemOrdre = semOrdre;
    		}
    		listeDiplSemImpairs = listeSemImp.immutableClone();
    		listeDiplSemPairs = listeSemPair.immutableClone();
    	}

    }
 
    // Recharger la vue, mais juste la row particulière associée au mrsemKey de IP_BILANRN_OK qui vient de changer
    public void reloaderVueAutoRN(Integer mrsemKey) {

    	NSArray bindings = new NSArray(new Object[] {cptLogin, getFannKey(), fspnKeyEnCours, mrsemKey});
    	EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
    			"cptLogin = %@ and fannKey = %@ and fspnKey = %@ and mrsemKey = %@", bindings);

    	EOFetchSpecification fetchSpec = new EOFetchSpecification("VipDiplparsem",qualifier, null);
    	fetchSpec.setRefreshesRefetchedObjects(true);

    	EOEditingContext ec = maSession.defaultEditingContext();

    	// ceci raffraichit l'EO, qui est unique pour ce contexte... ou qu'il soit !
    	NSArray res = ec.objectsWithFetchSpecification(fetchSpec);
    }

    public NSArray getListeDiplSemParite(boolean pariteImpaire) {
    	if (pariteImpaire) return listeDiplSemImpairs;
    	else return listeDiplSemPairs;
    }

    public boolean isModifAutorisee(int noSem) {
    	int noAnnee = (noSem+1)/2;
    	String diplAnnee = fspnKeyEnCours + "-" + noAnnee;
    	int droit = ctlFonctions.droitsPourFonctionEtDiplome(fonction,diplAnnee);
    	if (droit == Droit.DROIT_MODIF) return true;
    	else return false;
    }
    
    
    // *******************************
    
    // Pour le NSKeyValueCoding :
    public void takeValueForKey(Object arg0, String arg1) {
	// on s'en sert pas...
    }

    // on accède aux variables d'instances en direct !!!
    public Object valueForKey(String key) {
    	return NSKeyValueCoding.DefaultImplementation.valueForKey(this, key);
    }
}
