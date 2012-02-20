import java.util.Enumeration;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

/**
 * 
 * @author olive
 * @version 1.0
 * @category gestion des commentaires sur les EC (selection par le dipl/sem-parcours/UE...)
 *
 */
public class CommentCtrlr {

    private static String fonction = "MODIF_PARAM_COMMENT";
    private static String DATES_IP_DIPL = "PARAM_COMMENT";
    private static String[] listeFonctions = new String[] {DATES_IP_DIPL};
    
    private Session maSession;
    private FonctionsCtrlr ctlFonctions;
    private EOEditingContext monEcSsShared;
    
    public EOGenericRecord eoDiplSelected;
    public NSArray listeEC;
    
    public Integer fspnKey, anneeSuivie, mueKey;
    
    public boolean cacheIsModifAutorise, modifAutorisee;
    
    // Constructeur
    public CommentCtrlr(Session sess) {
	maSession = sess;
	OngletsCtrlr mesOngCt = maSession.getMesOnglets();
	ctlFonctions = new FonctionsCtrlr(mesOngCt,listeFonctions);
	
	// Pb : les commentaires sont associés a des objets read-only, car fetchés dans le sharedEditingContext...
	// Il faut donc créer un editingContext local, sans sharedEditingContext, et y recopier les EC sélectionnées... pour MAJ en cascade du shared
	
//	monEcSsShared = maSession.defaultEditingContext();
	monEcSsShared = new EOEditingContext();
	monEcSsShared.setSharedEditingContext(null);
    }

        
    // remonter les infos sur l'UE actuellement choisie !
    public String getLibCompletUeChoisie() {
	if (eoDiplSelected != null) {
	    return (String)eoDiplSelected.valueForKey("mueCode")+ " " +
	    (String)eoDiplSelected.valueForKey("mueLibelle");
	}
	else return null;
    }
    
    // On veut charger les EC � choix correspondant � l'UE actuellement choisie... dans ScolMaqUeEc
    public void chargerEC() {
//  	y a t'il une UE choisie ?
    	if (mueKey != null) {

    		NSArray bindings = new NSArray(new Object[] {new Integer(maSession.getAnneeEnCours()), mueKey});
    		EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
    				"fannKey = %@ and mueKey = %@", bindings);

    		EOSortOrdering ordre1 = EOSortOrdering.sortOrderingWithKey("mrecOrdre",EOSortOrdering.CompareAscending);
    		EOSortOrdering ordre2 = EOSortOrdering.sortOrderingWithKey("mecCode",EOSortOrdering.CompareAscending);
    		NSArray sortOrderings = new NSArray(new Object[] {ordre1, ordre2});

    		EOFetchSpecification fetchSpec = new EOFetchSpecification("VMaqEcChoix",qualifier, sortOrderings);
    		fetchSpec.setRefreshesRefetchedObjects(true);
    		fetchSpec.setPrefetchingRelationshipKeyPaths(new NSArray(new String[] {"toRepartEcComment"}));
    		// EOEditingContext ec = maSession.defaultEditingContext();

    		listeEC = monEcSsShared.objectsWithFetchSpecification(fetchSpec);  

    	}
    	else listeEC = null;
    	cacheIsModifAutorise = false;
    }

    // On va charger tous les commentaires de l'année en cours pour pouvoir les présenter dans une POP UP...
    public NSArray chargerComment() {

    	NSArray bindings = new NSArray(new Object[] {new Integer(maSession.getAnneeEnCours())});
    	EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
    			"fannKey = %@", bindings);

    	EOSortOrdering ordre1 = EOSortOrdering.sortOrderingWithKey("mrecComment",EOSortOrdering.CompareAscending);
    	NSArray sortOrderings = new NSArray(new Object[] {ordre1});

    	EOFetchSpecification fetchSpec = new EOFetchSpecification("IpUeEcComment",qualifier, sortOrderings);

    	return monEcSsShared.objectsWithFetchSpecification(fetchSpec);  

    }

    
    
    public NSArray getListeEcAChoix() { return listeEC; }
    
    // Indiquer si le user connect� � un droit lui permettant la modif pour ce diplome et cette ann�e... (droit sur MAQUETTES par d�faut)
    public boolean isModifAutorisee() {
	if (!cacheIsModifAutorise) {
	    cacheIsModifAutorise = true;
	    modifAutorisee = false;
	    if (mueKey != null) {
		String diplAnnee = fspnKey + "-" + anneeSuivie;
		int droit = ctlFonctions.droitsPourFonctionEtDiplome(fonction,diplAnnee);
		if (droit == Droit.DROIT_MODIF) modifAutorisee = true;
	    }
	}
	return modifAutorisee;
    }


    // Retourner le commentaire associé à une EC...
    public String getComment(EOGenericRecord ecEnCours, boolean ipcom) {
    	EOGenericRecord ipComment = chercherIpUeComment(ecEnCours);
    	String com = null;
    	if (ipComment != null) {
    		com = "";
    		if (ipcom) com = "("+(Integer)ipComment.valueForKey("ipcomKey")+") ";
    		com += (String)ipComment.valueForKey("mrecComment");
    	}

    	return com;
    }
    
    // Changer dans la base le texte d'un commentaire...
    public void changerComment(String nouvComment,EOGenericRecord ecEnModif) {
    	// retrouver l'entité IpUeComment qui va bien...
    	EOGenericRecord comment = chercherIpUeComment(ecEnModif);
    	if (comment != null) {
    		comment.takeStoredValueForKey(nouvComment, "mrecComment");
    		EOEditingContext ecEnt = comment.editingContext();
    		ecEnt.saveChanges();
    		
    		// Il faut provoquer un refresh des objets dans le shared editing context de l'application...
    		maSession.monApp.InvaliderSharedEC();
    	}
    }
    
    // on veut virer la référence au commentaire (virer aussi le commentaire si c'est le dernier !)
    public void supprComment(EOGenericRecord ecEnModif) {
    	// chercher les pointeurs vers REPART-EC-COMMENT et IP_UE_EC_COMMENT
    	if (ecEnModif != null) {
    		EOGenericRecord repartComment, ipComment;

    		try {
    			// On a une "fausse" relation to-many, qui en fait émule une relation (0,1)
    			NSArray toRepartComment = (NSArray)ecEnModif.valueForKey("toRepartEcComment");
    			if (toRepartComment != null && toRepartComment.count() > 0) {
    				repartComment = (EOGenericRecord)toRepartComment.objectAtIndex(0);
    				if (repartComment != null) { 
    					ipComment = (EOGenericRecord)repartComment.valueForKey("toIpUeEcComment");
    					if (ipComment != null) {
    						/// A ce niveau on a tout !
    						ipComment.removeObjectFromBothSidesOfRelationshipWithKey(repartComment,"toRepartEcComment");
    						ecEnModif.removeObjectFromBothSidesOfRelationshipWithKey(repartComment, "toRepartEcComment");
    						EOEditingContext ec = repartComment.editingContext();
    						ec.deleteObject(repartComment);

    						// Est-ce qu'on doit aussi virer le commentaire ? (plus d'EC qui pointent dessus...)
    						NSArray lesRefAuComment = (NSArray)ipComment.valueForKey("toRepartEcComment");
    						if (lesRefAuComment != null && lesRefAuComment.count() == 0) 
    							ec.deleteObject(ipComment);

    						// MAJ Base
    						ec.saveChanges();
    					}        			
    				}
    			}
    		}
    		catch(Exception e) {
    			System.out.println("exception pour "+ecEnModif.valueForKey("mecCode"));
    		}
    	}
    }

// Attacher un commentaire déjà existant à un EC....
    public void nouvChoixComment(EOGenericRecord commentChoisi, EOGenericRecord ecEnModif) {
    	if (commentChoisi != null && ecEnModif != null) {

    		// étape 1 : supprimer le commentaire actuel s'il existe
    		supprComment(ecEnModif);

    		// étape 2 : relier le commentaire existant 
    		EOClassDescription descriptionREPCOM = EOClassDescription.classDescriptionForEntityName("RepartEcComment");
    		EOGenericRecord repartComment =(EOGenericRecord)descriptionREPCOM.createInstanceWithEditingContext(null, null);

    		monEcSsShared.insertObject(repartComment);

//  		dder à EOF de mettre à jour les relationShips 
    		repartComment.addObjectToBothSidesOfRelationshipWithKey(commentChoisi,"toIpUeEcComment");
    		repartComment.addObjectToBothSidesOfRelationshipWithKey(ecEnModif, "toVMaqEcChoix");

    		// une sauvegarde...vers la base.
    		monEcSsShared.saveChanges();
    	}

    }

    // Ajouter dans la base le texte d'un commentaire...
    public void nouveauComment(String nouvComment,EOGenericRecord ecEnModif) {
    	// étape 1 : supprimer le commentaire actuel s'il existe
    	supprComment(ecEnModif);
    	
    	// étape 2 : ajouter un nouveau commentaire 
    	EOClassDescription descriptionIPCOM = EOClassDescription.classDescriptionForEntityName("IpUeEcComment");
    	EOGenericRecord nouvIpComment =(EOGenericRecord)descriptionIPCOM.createInstanceWithEditingContext(null, null);
    	    	
    	monEcSsShared.insertObject(nouvIpComment);
    	nouvIpComment.takeValueForKey(nouvComment,"mrecComment");	// normallement la clé primaire est attribuée d'office
    	nouvIpComment.takeValueForKey(new Integer(maSession.getAnneeEnCours()), "fannKey");
    	
    	monEcSsShared.saveChanges();

    	
    	EOClassDescription descriptionREPCOM = EOClassDescription.classDescriptionForEntityName("RepartEcComment");
    	EOGenericRecord repartComment =(EOGenericRecord)descriptionREPCOM.createInstanceWithEditingContext(null, null);
    	
    	monEcSsShared.insertObject(repartComment);
    	
//    	 dder à EOF de mettre à jour les relationShips 
    	repartComment.addObjectToBothSidesOfRelationshipWithKey(nouvIpComment,"toIpUeEcComment");
    	repartComment.addObjectToBothSidesOfRelationshipWithKey(ecEnModif, "toVMaqEcChoix");
    	
    	// une sauvegarde...vers la base.
    	monEcSsShared.saveChanges();
    	
    }
    
    
    public EOGenericRecord chercherIpUeComment(EOGenericRecord ecEnCours) {
    	if (ecEnCours != null) {
    		try {
    			// On a une "fausse" relation to-many, qui en fait émule une relation (0,1)
    			NSArray toRepartComment = (NSArray)ecEnCours.valueForKey("toRepartEcComment");
    			if (toRepartComment != null && toRepartComment.count() > 0) {
    				EOEnterpriseObject entComment = (EOEnterpriseObject)toRepartComment.objectAtIndex(0);
    				if (entComment != null) 
    					return (EOGenericRecord)entComment.storedValueForKey("toIpUeEcComment");
    			}

    		}

    		catch(Exception e) {
    			System.out.println("exception pour "+ecEnCours.valueForKey("mecCode"));
    		}
    	}
    	return null;
    }

}