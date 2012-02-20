import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;

/**
 * @author olive
 * sous-classe de PopUpDataAccessCtrlr (produit de la fabrique abstraite PopUpDaFact
 * prévue pour accéder a une entité "VSemParcoursDroits", avec les cles "cptLogin", "fannKey", "fspnKey"
 * 
 */

public class PopUpDACTSemParcours extends PopUpDataAccessCtrlr {

	// Creation directe...
	public PopUpDACTSemParcours(EOEditingContext unEc, boolean noSelection) {
		super(unEc, noSelection);
		// Initialisation des sous-produits qui vont bien :
		cleParam = new NSArray(new String[] {"cptLogin", "fannKey", "fspnKey"});

		EOSortOrdering ordre1 = EOSortOrdering.sortOrderingWithKey("msemOrdre",EOSortOrdering.CompareAscending);
		EOSortOrdering ordre2 = EOSortOrdering.sortOrderingWithKey("mparLibelle",EOSortOrdering.CompareAscending);
		eoSortOrderings = new NSArray(new Object[] {ordre1, ordre2});

		chaineQualif = "cptLogin = %@ and fannKey = %@ and fspnKey= %@";
		nomEntite = "VSemParcoursDroits";

		displayString = "msemOrdre";
	}

	// On doit redefinir la methode displayString, car affichage non trivial !!!
	// (renvoyer pour l'EO en cours de parcours la valeur a afficher dans le PopUp...)
	public String displayString() {
		String res = recupValCle(displayString);
		if (res != null) {
			res = "sem."+res;
			String resSuite = recupValCle("mparAbreviation");
			if (resSuite == null || resSuite.length() == 0) {
				resSuite = recupValCle("mparLibelle");
				res+= ", "+resSuite;
			}
		}
		return res;
	}

}


