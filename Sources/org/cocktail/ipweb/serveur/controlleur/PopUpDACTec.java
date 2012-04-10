package org.cocktail.ipweb.serveur.controlleur;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;

/**
 * @author olive
 * sous-classe de PopUpDataAccessCtrlr (produit de la fabrique abstraite PopUpDaFact
 * prevue pour acceder a une entite "ScolMaqSemestreUe", avec les cles "msemKey", "msemKeyPc"
 * 
 */

public class PopUpDACTec extends PopUpDataAccessCtrlr {

	// Creation directe...
	public PopUpDACTec(EOEditingContext unEc, boolean noSelection) {
		super(unEc, noSelection);
		// Initialisation des sous-produits qui vont bien :
		cleParam = new NSArray(new String[] {"msemKey", "msemKeyPc"});

		EOSortOrdering ordre1 = EOSortOrdering.sortOrderingWithKey("mrueOrdre",EOSortOrdering.CompareAscending);
		EOSortOrdering ordre2 = EOSortOrdering.sortOrderingWithKey("mrecOrdre",EOSortOrdering.CompareAscending);
		eoSortOrderings = new NSArray(new Object[] {ordre1, ordre2});

		chaineQualif = "msemKey = %@ or msemKey = %@";
		nomEntite = "VMaqSemestreEc";
	}

	// On doit redefinir la methode displayString, car affichage non trivial !!!
	// (renvoyer pour l'EO en cours de parcours la valeur a afficher dans le PopUp...)
	public String displayString() {
		String res = recupValCle("mecCode");
		res += " (" + recupValCle("mtecCode");
		res += ") : " + recupValCle("mecLibelle");
		return res;
	}


}
