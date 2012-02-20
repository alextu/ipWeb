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
import com.webobjects.foundation.NSTimestamp;

/**
 * 
 * @author olive
 * @version 1.0
 * @category gestion des ctrlr de dates de semestres (sem. impairs et sem. pairs)
 *
 */
public class DatesDiplCtrlr implements NSKeyValueCoding {
	private static String fonction = "MODIF_DATES_IPW_DIPL";
	private static String DATES_IP_DIPL = "PARAM_DATES_DIPL";
	private static String[] listeFonctions = new String[] {DATES_IP_DIPL};

	private Session maSession;
	private FonctionsCtrlr ctlFonctions;
	private String cptLogin;

	private NSArray listeDipl;
	private NSArray listeDiplSemImpairs, listeDiplSemPairs;

	private Integer fspnKeyEnCours;

//	private PopUpDataAccessCtrlr leDACTdiplome;

	public DatesDiplCtrlr(Session sess) {

		maSession = sess;
		OngletsCtrlr mesOngCt = maSession.getMesOnglets();
		ctlFonctions = new FonctionsCtrlr(mesOngCt,listeFonctions);
		cptLogin = mesOngCt.cptLogin();	// on obtient le code de l'utilisateur dans ScolPeda depuis 
		// le controleur d'onglet (r�cup�r� lors de son init)

	}

	public NSArray listeDiplomesVisibles() { return listeDipl; }

	public Integer getFannKey() { return new Integer(maSession.getAnneeEnCours()); }

	// Obsolète depuis la V2.4...
//	public Integer getDlogKey() { return dlogKey; }	
	public String getCptLogin() { return cptLogin; }
	

	// Pr�viens de charger les dates pour les semestres de ce diplome...
	// g�n�re des sous-controleurs par lignes (par groupes de semestres impairs et pairs)
	public void chargerDatesDipl(Integer fspnKey) {
		fspnKeyEnCours = fspnKey;
		NSArray bindings = new NSArray(new Object[] {cptLogin, getFannKey(), fspnKey});
		EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
				"cptLogin = %@ and fannKey = %@ and fspnKey = %@", bindings);

		EOSortOrdering ordreSem = EOSortOrdering.sortOrderingWithKey("msemOrdre",EOSortOrdering.CompareAscending);
		NSArray sortOrderings = new NSArray(new Object[] {ordreSem});

		EOFetchSpecification fetchSpec = new EOFetchSpecification("VDiplomeSemDates",qualifier, sortOrderings);
		fetchSpec.setRefreshesRefetchedObjects(true);

		EOEditingContext ec = maSession.defaultEditingContext();

		NSArray detailDipl = ec.objectsWithFetchSpecification(fetchSpec);  

		// cr�er 2 listes de ctrlr au niveau semestres avec les r�sultats (les impairs et les pairs)

		if (detailDipl != null && detailDipl.count()>0) {
			NSMutableArray listeSemImp = new NSMutableArray(), listeSemPair = new NSMutableArray();
			Enumeration e = detailDipl.objectEnumerator();
			while (e.hasMoreElements()) {
				EOGenericRecord datesDipl = (EOGenericRecord)e.nextElement();
				DateSemCtrlr nouvDSemCt = new DateSemCtrlr(maSession, datesDipl, this);
				if (nouvDSemCt.estSemPair())
					listeSemPair.addObject(nouvDSemCt);
				else listeSemImp.addObject(nouvDSemCt);
			}
			listeDiplSemImpairs = listeSemImp.immutableClone();
			listeDiplSemPairs = listeSemPair.immutableClone();
		}

	}

	// appel� par un des DateSemCtrlr rattach� pour reloader son EO (VDiplomeSemDates) apr�s modifs...
	public EOGenericRecord reloadDatesDiplSem(Integer msemOrdre) {
		NSArray bindings = new NSArray(new Object[] {cptLogin, getFannKey(), fspnKeyEnCours, msemOrdre});
		EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
				"cptLogin = %@ and fannKey = %@ and fspnKey = %@ and msemOrdre = %@", bindings);

		EOFetchSpecification fetchSpec = new EOFetchSpecification("VDiplomeSemDates",qualifier, null);
		fetchSpec.setRefreshesRefetchedObjects(true);

		EOEditingContext ec = maSession.defaultEditingContext();
		NSArray res = ec.objectsWithFetchSpecification(fetchSpec);  

		if (res != null && res.count() > 0) return (EOGenericRecord)res.objectAtIndex(0);
		else return null;
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

	// Pour le NSKeyValueCoding :
	public void takeValueForKey(Object arg0, String arg1) {
		// on s'en sert pas...
	}

	// on acc�de aux variables d'instances en direct !!!
	public Object valueForKey(String key) {
		return NSKeyValueCoding.DefaultImplementation.valueForKey(this, key);
	}



}
