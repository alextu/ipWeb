import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;

/**
 * @author olive
 * sous-classe de PopUpDataAccessCtrlr (produit de la fabrique abstraite PopUpDaFact
 * pr�vue pour acc�der � une entit� "VDomaineVisibles", avec les cl�s "cptLogin" et "fannKey"
 * 
 */
public class PopUpDACTDomaine extends PopUpDataAccessCtrlr {

    // Cr�ation directe...
    public PopUpDACTDomaine(EOEditingContext unEc, boolean noSelection) {
	super(unEc, noSelection);
	// Initialisation des sous-produits qui vont bien :
	cleParam = new NSArray(new String[] {"cptLogin", "fannKey"});
	
	EOSortOrdering ordre = EOSortOrdering.sortOrderingWithKey("fdomLibelle",EOSortOrdering.CompareAscending);
	eoSortOrderings = new NSArray(new Object[] {ordre});
	
	chaineQualif = "cptLogin = %@ and fannKey = %@";
	nomEntite = "VDomaineVisibles";
	
	displayString = "fdomLibelle";
    }

}
