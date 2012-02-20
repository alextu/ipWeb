import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;


/*
 * Créé le 10 janv. 2012
 *
 * @author olive
 *
 * Sous classe de RelationChoixEc pour gérer les EC facultatifs rattachés à un semestre donné...
 *	
 *  
 * */

public class RelationChoixFacultatif extends RelationChoixEc {

	// les controleurs d'EC qui référencent des EC facultatifs sont dans relationEc

	private EOEditingContext ecSession;
	private int anneeUniversitaire;
	private boolean gereGroupeEcFacultatifsIncompatibles;
	private int nbreMaxEcFacultatifs;
	private int nbreEcFacultatifsDejaPris;
	
	// Constructeur de la relation : on lui balance une liste des EC Facultatifs parmi les EC de ce semestre...
	public RelationChoixFacultatif(NSMutableArray listeEcFac,int anneeUniv, EOEditingContext ecSess,
			boolean gereGroupeEcFacultatif,
			int nbreMaxEcFac) {
		relationEc = listeEcFac.immutableClone();
		anneeUniversitaire = anneeUniv;
		ecSession = ecSess;
		gereGroupeEcFacultatifsIncompatibles = gereGroupeEcFacultatif;
		nbreMaxEcFacultatifs = nbreMaxEcFac;
		
		nbreEcFacultatifsDejaPris = 0;
		// TODO : ce nbre devrait être initialisé avec le nbre d'EC facultatifs choisis sur toute l'année
		//        (y compris semestre impair si on est en sem pair), via un passage de paramètre			
	}
	
	// Les méthodes devant être suchargées...
	
//	vérifier la relation en cours si une erreur n'existerait pas pour les EC qu'elle contient..
	public boolean verifierRelation() throws Exception {
		boolean res=true;
		
		int nbreEcFacultatifsChoisis = nbreEcFacultatifsDejaPris;	

		NSMutableDictionary   listeGroupesIncomp;
		NSMutableDictionary   dictGroupesIncomp;
		NSMutableArray		  listeEcConcernees;
		Integer			geciKey;
		String			geciLibelle;			
		
		listeGroupesIncomp = new NSMutableDictionary();
		dictGroupesIncomp = new NSMutableDictionary();
		
		// 1er test : voir s'il n'y a pas d'EC Facultatifs cochés qui ne sont pas incompatibles deux à deux ???
		java.util.Enumeration enumerator = relationEc.objectEnumerator();
		while (enumerator.hasMoreElements()) {
			InscEcCtrlr ecCt = (InscEcCtrlr)enumerator.nextElement();
			if (ecCt.getCaseCochee() || ecCt.ecAvecIp()) { 
				nbreEcFacultatifsChoisis++;
				// TODO : quand la valeur nbreEcFacultatifsDejaPris sera initialisée avec le nbre
				//        d'EC facultatifs pris sur toute l'année, il faudra faire sauter "|| ecCt.ecAvecIp()"
				
				// Est-ce qu'on gère les groupes d'EC facultatifs incompatibles
				if (gereGroupeEcFacultatifsIncompatibles) {

					EOGenericRecord ecGroupeIncompatible = fetcherGroupeEcFacultatif(ecCt); 
					if (ecGroupeIncompatible != null) {
						geciKey = (Integer)ecGroupeIncompatible.valueForKey("geciKey");
						if (!listeGroupesIncomp.containsKey(geciKey)) {
							geciLibelle = (String)ecGroupeIncompatible.valueForKey("geciLibelle");
							listeGroupesIncomp.setObjectForKey(geciLibelle, geciKey);						
							listeEcConcernees = new NSMutableArray();				
						}
						else { // On a déja rencontré ce groupe... Il faut ajouter la nouvelle EC à la liste
							listeEcConcernees = (NSMutableArray)dictGroupesIncomp.objectForKey(geciKey);
						}
						listeEcConcernees.addObject(ecCt);
						dictGroupesIncomp.setObjectForKey(listeEcConcernees, geciKey);					
					}
				}
			}
		}

		if (gereGroupeEcFacultatifsIncompatibles && nbreEcFacultatifsChoisis>1) { // s'il y a au moins 2 choix, vérifier qu'ils ne sont pas incompatibles...
			// Vérifier s'il existe un groupe d'EC facultatifs incompatbiles ayant plus d'un élément.
			NSArray clefs = listeGroupesIncomp.allKeys();
			Integer clef;
			java.util.Enumeration enumerator3 = clefs.objectEnumerator(); 
			while (enumerator3.hasMoreElements()) {
				clef = (Integer)enumerator3.nextElement();
				listeEcConcernees = (NSMutableArray)dictGroupesIncomp.objectForKey(clef);
				if (listeEcConcernees.count()>1) {  // On tient notre candidat !!!
					geciLibelle = (String)listeGroupesIncomp.objectForKey(clef);
					java.util.Enumeration enumerator4 = listeEcConcernees.objectEnumerator();
					while (enumerator4.hasMoreElements()) {
						InscEcCtrlr ecCt = (InscEcCtrlr)enumerator4.nextElement();
						if (ecCt.getCaseCochee() && !ecCt.isEcBloque()) { 
							ecCt.setErreur("Vous ne pouvez choisir qu'une seule EC facultative dans le groupe d'EC "+geciLibelle,
									"décochez les EC facultatives en trop...");
							dernierEcEnErreur = ecCt;
						}
					}
					return false;	

				}
			}
		}
		// 2n test : compter le nbre d'EC facultatifs cochés...
		if (nbreEcFacultatifsChoisis> nbreMaxEcFacultatifs) {	// TODO : le nbre maxi d'EC Facultatifs à l'année doit être paramétré (et non en dur)
			res = false;
			// Indiquer toutes les erreurs...
			// TODO : tester le cas ou une EC corresond à une IP validée dans ScolPéda (par ex, reprise de note redoublants)
			java.util.Enumeration enumerator2 = relationEc.objectEnumerator();
			while (enumerator2.hasMoreElements()) {
				InscEcCtrlr ecCt = (InscEcCtrlr)enumerator2.nextElement();
				if ((ecCt.getCaseCochee() || ecCt.ecAvecIp()) && !ecCt.isEcBloque()) { 
					ecCt.setErreur("VOUS N'AVEZ DROITS QU'A "+nbreMaxEcFacultatifs+" CHOIX FACULTATIFS pour l'année",
							"décochez les EC facultatifs en trop...");
					dernierEcEnErreur = ecCt;
				}
			}
		}


		return res;
	}
	
	private EOGenericRecord fetcherGroupeEcFacultatif(InscEcCtrlr ecFac) {		
/*
		NSArray bindings = new NSArray(new Object[] {ecFac.getMecKey()});
    	EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
    			"mecKey = %@", bindings);
*/
    	NSArray bindings = new NSArray(new Object[] {new Integer(anneeUniversitaire),ecFac.getMecKey()});
    	EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
    			"fannKey = %@ and mecKey = %@", bindings);
    	
    	EOFetchSpecification fetchSpec = new EOFetchSpecification("IpEcIncompatibles",qualifier, null);
  //  	fetchSpec.setPrefetchingRelationshipKeyPaths(new NSArray(new String[] {"toIpGroupeEcIncompatibles"}));
    	
		NSArray res = ecSession.objectsWithFetchSpecification(fetchSpec);
		
		if (res !=null && res.count()>0)
				return (EOGenericRecord)res.objectAtIndex(0);
		else return null;
	}
	
	
	
	public void scannerChoixAMasquer() { }	// pour l'instant on masque juste la méthode héritée...
	public boolean choixIncoherentCoche() { return false; }
}
