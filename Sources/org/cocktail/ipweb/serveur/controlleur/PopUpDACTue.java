package org.cocktail.ipweb.serveur.controlleur;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;

/**
 * @author olive
 * sous-classe de PopUpDataAccessCtrlr (produit de la fabrique abstraite PopUpDaFact
 * pr�vue pour acc�der � une entit� "ScolMaqSemestreUe", avec les cl�s "msemKey", "msemKeyPc"
 * 
 */

public class PopUpDACTue extends PopUpDataAccessCtrlr {

    // Cr�ation directe...
    public PopUpDACTue(EOEditingContext unEc, boolean noSelection) {
	super(unEc, noSelection);
	// Initialisation des sous-produits qui vont bien :
	cleParam = new NSArray(new String[] {"msemKey", "msemKeyPc"});

	EOSortOrdering ordre = EOSortOrdering.sortOrderingWithKey("mueCode",EOSortOrdering.CompareAscending);
	eoSortOrderings = new NSArray(new Object[] {ordre});

	chaineQualif = "msemKey = %@ or msemKey = %@";
	nomEntite = "VMaqSemestreUeChoix";
    }

    // On doit red�finir la m�thode displayString, car affichage non trivial !!!
    // (renvoyer pour l'EO en cours de parcours la valeur � afficher dans le PopUp...)
    public String displayString() {
	String res = recupValCle("mueCode");
	res += " : " + recupValCle("mueLibelle");
	return res;
    }


}
