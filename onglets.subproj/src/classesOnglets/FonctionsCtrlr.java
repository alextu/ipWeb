import com.webobjects.foundation.NSMutableDictionary;

/*
 * Cr�� le 3 nov. 2006
 * @author olive
 *
 * Objectif : g�rer les droits pour un ensemble de fonctions, par rapport au diplome en cours d'�dition
 * 				en gros chaque module � son controleur de fonction, qui sera updat� ou non selon le chgt de diplome 
 *
 */
public class FonctionsCtrlr {

    private String[] lesFonctions;
    private OngletsCtrlr monCtlOnglets;
    private NSMutableDictionary droitsFonctions;

    public FonctionsCtrlr(OngletsCtrlr ctlOnglets, String[] fonctions) {
    	super();
    	monCtlOnglets = ctlOnglets;
    	lesFonctions = fonctions;
    	droitsFonctions = new NSMutableDictionary();
    }

    // pour chaques fonctions assur�es par ce module, raffraichir les droits par rapport au nouveau dipl�me... 
    public void refreshDroitsFonctions(String diplAnnee) {
	for (int i = 0;i < lesFonctions.length; i++) {
    		String cle = lesFonctions[i];
    		int val = monCtlOnglets.getDroitsTypeUtilisateur(cle,diplAnnee);
    		droitsFonctions.setObjectForKey(new Integer(val),cle);
    	}
    }

	public int droitsPourFonctionEtDiplome(String fonction, String diplAnnee) {
		return monCtlOnglets.getDroitsTypeUtilisateur(fonction,diplAnnee);
	}
    
	// renvoie vrai si le droit pour cette fonction existe et permet la consultation
	public boolean getDroitsConsult(String laFonction) {
		Integer val = (Integer)droitsFonctions.objectForKey(laFonction);
		if (val != null) {
			int valEnt = val.intValue();
			return (valEnt==Droit.DROIT_CONSULT || valEnt==Droit.DROIT_MODIF);
		}
		else return false;
	}
	

	// renvoie vrai si le droit pour cette fonction existe et permet la modification
	public boolean getDroitsModification(String laFonction) {
		Integer val = (Integer)droitsFonctions.objectForKey(laFonction);
		if (val != null) {
			int valEnt = val.intValue();
			return (valEnt==Droit.DROIT_MODIF);
		}
		else return false;
	}


}
