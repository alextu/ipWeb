

/**
 * @author olive
 * @category classe "produit" (abstraite) pour la fabrique abstraite PopUpDAFact
 * Cet objet donne acc�s � des donn�es d'affichages pour un WOPopUpSelection (composant "utilisateur")
 * (tir�es des EO fetch�s dans la base, qui d�pendent de l'impl�mentation concr�te des sous-classes)
 * et plusieurs WOPopUpSelection doivent pouvoir �tre "chain�s"...
 * 
 * R�les : 
 * + doit pouvoir fetcher des EO (selon des crit�res sp�cifiques, jeux de params des sous-classes)
 * + doit faire r�f�rence � un objet "source de valeur pour ses params de fetch" (impl�mentant le NSKeyValueCoding)
 * + doit renvoyer un NSArray des EO Fetch�s (var d'instance publique)
 * (doit avoir un feedBack sur l'EO couramment s�lectionn� via le WOPopUpSelection "utilisateur")
 * + doit pouvoir �tre pr�venu de refetcher (si chgt dans les valeurs des params d�tenus par la source externe)
 * + doit pouvoir renvoyer les valeurs de param�tres pour les cl�s transmises (en fct de l'EO courant)   
 */

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.*;

public abstract class PopUpDataAccessCtrlr {
    // les s�ries de "sous-produits" a decliner par les sous-classes
    protected NSArray cleParam;		// tableaux de String representant les cles (vers l'objet source externe)
    					// par contre implique de connaitre les noms de cles de l'objet source (#faiblesse#)
    protected NSArray eoSortOrderings;	// liste des EOSortOrdering pour les tri (si necessaire, sinon a mettre a null)
    					// � faire construire par les sous-classes !
    protected String chaineQualif;	// la sélection à utiliser pour le qualifier.... (ex : "cptLogin = %@ and fannKey = %@ and fspnKey = %@")
    protected String nomEntite;		// nom de l'entité dans l'EOModel sur laquelle va porter ce fetch...
    
    protected String displayString;	// clé de la colonne des EO fetchés qui va servir à l'affichage pour la valeur courante de la variable de parcours...

    // Les variables d'instances utiles au fonctionnement du produit...
    protected EOEditingContext monEc;			// a initialiser a la construction...
    protected Object sourceValParams;		// ref de l'objet source qui va d�livrer les valeurs (objets) pour les "cl�s" param�tres
    protected Object[] valParams;		// tableau d'objet avec les valeurs une fois r�cup�r�es...
    
    protected PopUpDataAccessCtrlr dactEsclave;	// pour le chainage vers le PopUpDataAccessCtrlr qui d�pend de moi (chainage !)
    
    public NSArray listeEOFetches;		// le r�sultat du fetch (utilis� par le Dynamic Element WOPopUpSelection du composant utilisateur)
    public EOGenericRecord itemEnCours;		// le EO servant de variable de parcours au WOPopUpSelection
    
    protected EOGenericRecord itemChoisi;	// le EO choisi via le WOPopUpSelection...
    protected boolean estInactive;		// si vrai, la modif du WOPopUpSelection doit �tre inactiv�e 
    
    protected boolean noSelectionPossible;	// si vrai, la premi�re ligne pr�sent�e par le WOPopUpSelection sera vide (no selection)
    
    
    
    public PopUpDataAccessCtrlr(EOEditingContext unEc, boolean noSelection) {
	// constructeur de la super classe abstraite : ne sert qu'� indiquer un param de base � la construction !
	monEc = unEc;
	noSelectionPossible = noSelection;
	
	listeEOFetches = null;
	itemChoisi = null;
	estInactive = false;
    }
    
    // Recevoir le PopUpDataAccessCtrlr "esclave" (ssi chainage) :
    public void setDACTEsclave(PopUpDataAccessCtrlr monEsclave) {
	dactEsclave = monEsclave;
	// en profiter pour dire � mon esclave qui est le maitre (source de donn�es pour ces param�tres !)
	if (monEsclave != null) monEsclave.setObjetSource(this);
    }

    // Recevoir l'objet source pour mes valeurs de parametres :
    // en g�n�ral appel� depuis le ma�tre !)
    public void setObjetSource(Object monObjetSource) {
	sourceValParams = monObjetSource;
    }
    
    // M�thode appel�e directement depuis le composant Utilisateur... coeur de la r�cursion !!!!
    public void setItemChoisi(EOGenericRecord nouvelItem) {
	itemChoisi = nouvelItem;
	// si un esclave existe, le pr�venir de se remettre � jour !!!
	if (dactEsclave != null) dactEsclave.fetcherLesEo();
    }
    
    public EOGenericRecord getItemChoisi() { return itemChoisi; }

    public boolean isItemChoisi() { return (itemChoisi != null); }

    // r�cup�rer les valeurs des param�tres depuis l'objet source : 
    private boolean recupererValParams() {
    	boolean paramsPrets = false;
    	valParams = null;
    	// si l'objet source existe bien, et qu'on a bien une liste de parametres !!!
    	if (sourceValParams != null && cleParam != null && cleParam.count()>0) {
    		// cr�er un tableau d'objet servant � r�cup�rer les valeurs
    		// pour toutes les cl�s pr�sentes dans la liste de params :

    		paramsPrets = true;
    		if (sourceValParams instanceof PopUpDataAccessCtrlr) 
    			paramsPrets = (((PopUpDataAccessCtrlr)sourceValParams).getItemChoisi() != null);

    		if (paramsPrets) {
    			int nbParams = cleParam.count();
    			valParams = new Object[nbParams];
    			try {
    				// essaie de r�cup�rer ses valeurs dans l'objet source...
    				for (int i = 0; i < nbParams; i++) {
    					if (sourceValParams instanceof PopUpDataAccessCtrlr)
    						valParams[i] = ((PopUpDataAccessCtrlr)sourceValParams).valeurDeCle((String)cleParam.objectAtIndex(i));
    					else
    						valParams[i] = ((NSKeyValueCoding)sourceValParams).valueForKey((String)cleParam.objectAtIndex(i));

    					// pour info : autre m�thode qui marche avec tous types d'objets : 
    					// valParams[i] = NSKeyValueCoding.Utility.valueForKey(this,(String)cleParam.objectAtIndex(i));
    				}
    			}
    			catch (Exception e) {
    				NSLog.err.appendln("Pb avec la récupération des paramètres : "+e.getMessage()+"\n"+e.getStackTrace());
    				valParams = null;
    				paramsPrets = false;
    			}
    		}
    	}
    	return paramsPrets; 
    }

    // Je suis l'objet source : je dois renvoyer la valeur pour le paramètre transmis !
    // que je vais chercher dans l'EO fetch� actuellement choisi par mon utilisateur...
    public Object valeurDeCle(String aKey) {
	// un item est-il bien choisi ?
	if (itemChoisi != null) {
	    // renvoyer la valeur de la colonne de l'EO en question !
	    return itemChoisi.valueForKey(aKey);
	}
	else return null;
    }
    
//    // ne fait rien car on n'est pas cens� �crire dans les params !!!
//    // mais le fait de d�clarer l'interface NSKeyValueCoding m'oblige � faire figurer cette m�thode...
//    public void takeValueForKey(Object arg0, String arg1) { }
        
    public void fetcherLesEO(EOEditingContext unEc) {
	monEc = unEc;
	fetcherLesEo();
    }
    
    // Fetcher les EO que l'on doit utiliser
    public void fetcherLesEo() {
	
	//  Commence par fetcher les params ... et si Ok, fetche les EO !
	if (recupererValParams() && valParams != null) {

	    NSArray bindings = new NSArray(valParams);
	    EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(chaineQualif, bindings);
	    EOFetchSpecification fetchSpec = new EOFetchSpecification(nomEntite,qualifier, eoSortOrderings);

	    fetchSpec.setRefreshesRefetchedObjects(true);

	    NSArray res = null;
	    res = monEc.objectsWithFetchSpecification(fetchSpec);
	    listeEOFetches = res;
	    
	    // si on demande � ce qu'� l'init il n'y ait pas de ligne s�lectionn�e par d�faut, alors pas d'Item choisi (en cascade)
	    if (noSelectionPossible) setItemChoisi(null);
	    else {
		if (listeEOFetches != null && listeEOFetches.count() > 0)
		    setItemChoisi((EOGenericRecord)listeEOFetches.objectAtIndex(0));
		else setItemChoisi(null);
	    }
	}
	// les parametres n'ont pu être récupérés : vider le popUp...
	else {
            listeEOFetches = null;
            // pas d'Item choisi (en cascade)
            setItemChoisi(null);
	}
    }
    
    // changer l'�tat d'activit� du PopUp utilisateur...
    public void setEtatActif(boolean nouvEtat) { estInactive = nouvEtat; }
    
    // connaitre l'�tat d'inactivit�...
    public boolean estInactive() { return estInactive; }
    
    // renvoyer pour l'EO en cours de parcours la valeur a afficher dans le PopUp...
    public String displayString() {
	return recupValCle(displayString);
    }

    protected String recupValCle(String cle) {
	if (itemEnCours != null) {
	    try {
		Object res = itemEnCours.valueForKey(cle);
		if (res != null) return res.toString();		// on ne sait pas si le resultat sera un String !
		else return null;
	    }
	    catch (Exception e) {
		// la cle ne correspond pas a une valeur existante pour cet objet !
		NSLog.err.appendln("la cle '"+cle+"' ne correspond pas a une valeur existante pour cet objet !");
		return null;
	    }
	    
	}
	else return null;

    }
    
    public String noSelectionString() {
	if (noSelectionPossible) return "";
	else return null;
    }
}