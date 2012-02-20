import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSKeyValueCoding;

/**
 * @author olive
 * Classe fabrique abstraite de PopUpDataAccessCtrlr (par m�thodes statiques)
 */
public abstract class PopUpDaFact {

    static final int DACT_DIPLOME = 1,  DACT_DOMAINE = 2, DACT_SEMPARCOURS = 3, DACT_UE = 4, DACT_DIPLRN = 5, DACT_EC = 6, DACT_ANNEE = 7;
    

    public PopUpDaFact() {
    	// TODO Auto-generated constructor stub
    }

    static private PopUpDataAccessCtrlr creerPopUpDataAccessCtrlr(int typeDACT, Session maSess, boolean noSelection) {
    	EOEditingContext ec = maSess.defaultEditingContext();
    	switch(typeDACT) {
    	case DACT_DIPLOME :
    		return new PopUpDACTDiplome(ec, noSelection);
    	case DACT_DIPLRN :
    		return new PopUpDACTDiplRn(ec, noSelection);
    	case DACT_DOMAINE :
    		return new PopUpDACTDomaine(ec, noSelection);
    	case DACT_SEMPARCOURS :
    		return new PopUpDACTSemParcours(ec, noSelection);
    	case DACT_UE :
    		return new PopUpDACTue(ec, noSelection);
    	case DACT_EC :
    		return new PopUpDACTec(ec, noSelection);
    	case DACT_ANNEE :
    		return new PopUpDACTAnnee(ec, noSelection);    		
    	default :
    		return null;
    	}
    }

    // Creation + association de l'objet source de donnees
    static public PopUpDataAccessCtrlr creerPopUpDACT(int typeDACT, Session maSess, boolean noSelection,
    		Object objSourceParams) {
    	PopUpDataAccessCtrlr monDACT = creerPopUpDataAccessCtrlr(typeDACT, maSess, noSelection);
    	monDACT.setObjetSource(objSourceParams);
    	return monDACT;
    }

    // Creation + association de l'objet source de donnees  ET du popUp esclave (uniquement pour le premier PopUp d'un enchainement)
    static public PopUpDataAccessCtrlr creer1erPopUpDACTChainage(int typeDACT, Session maSess, boolean noSelection,
    		NSKeyValueCoding objSourceParams, PopUpDataAccessCtrlr popUpEsclave) {
    	PopUpDataAccessCtrlr monDACT = creerPopUpDataAccessCtrlr(typeDACT, maSess, noSelection);
    	monDACT.setDACTEsclave(popUpEsclave);
    	monDACT.setObjetSource(objSourceParams);
    	return monDACT;
    }
    
}
