package org.cocktail.ipweb.serveur.controlleur;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;


/** Récuperer les différents niveaux disponibles pour un diplôme donné
 * 
 */

/**
 * @author olive
 * sous-classe de PopUpDataAccessCtrlr (produit de la fabrique abstraite PopUpDaFact
 * prévue pour accéder a une entité "VSemParcoursDroits", avec les cles "cptLogin", "fannKey", "fspnKey"
 * 
 */

public class PopUpDACTAnnee extends PopUpDataAccessCtrlr {

	// Creation directe...
	public PopUpDACTAnnee(EOEditingContext unEc, boolean noSelection) {
		super(unEc, noSelection);
		// Initialisation des sous-produits qui vont bien :
		cleParam = new NSArray(new String[] {"cptLogin", "fannKey", "fspnKey"});

		EOSortOrdering ordre1 = EOSortOrdering.sortOrderingWithKey("fhabNiveau",EOSortOrdering.CompareAscending);
		eoSortOrderings = new NSArray(new Object[] {ordre1});

		chaineQualif = "cptLogin = %@ and fannKey = %@ and fspnKey= %@";
		nomEntite = "ScolDroitDiplome";

		displayString = "fhabNiveau";
	}

}
