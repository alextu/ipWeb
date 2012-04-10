package org.cocktail.ipweb.serveur.controlleur;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;

/**
 * @author olive
 * sous-classe de PopUpDataAccessCtrlr (produit de la fabrique abstraite PopUpDaFact
 * prévue pour accéder à une entité "VDiplomeVisibles", avec les clés "cptLogin" et "fannKey"
 * 
 */
public class PopUpDACTDiplome extends PopUpDataAccessCtrlr {

    // Creation directe...
    public PopUpDACTDiplome(EOEditingContext unEc, boolean noSelection) {
	super(unEc, noSelection);
	// Initialisation des sous-produits qui vont bien :
	cleParam = new NSArray(new String[] {"cptLogin", "fannKey"});
	
	EOSortOrdering ordre = EOSortOrdering.sortOrderingWithKey("diplome",EOSortOrdering.CompareAscending);
	eoSortOrderings = new NSArray(new Object[] {ordre});
	
	chaineQualif = "cptLogin = %@ and fannKey = %@";
	nomEntite = "VDiplomeVisibles";
	
	displayString = "diplome";
    }

}
