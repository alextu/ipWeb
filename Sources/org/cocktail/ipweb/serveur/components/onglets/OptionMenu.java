package org.cocktail.ipweb.serveur.components.onglets;
/*
 * Cr�� le 30 oct. 2006
 *
 * Il s'agit de g�rer individuellement une option des menu (titre, construction graphique, s�lectionnable...)
 *
 */

/**
 * @author olive

 */
public class OptionMenu {

	private String ref_OM;
	private String titre_OM;
	
	private OptionMenu menuPrec,menuSuiv;	// gestion des options de menu par liste chain�e...
	private MenuCtrlr menuMaitre;
	
	private boolean estSelectionne;
	
	private boolean changeApparence;
	private String urlImageFondOnglet,urlImageBordDOnglet;	// gestion d'un cache d'URL...
	
	public OptionMenu(String ref_option, String titre_option,OptionMenu omPrec) {
		super();
		ref_OM = ref_option;
		titre_OM = titre_option;
		menuPrec = omPrec;
		estSelectionne = false;
		changeApparence = true;
	}
	
	public void setOmSuivant(OptionMenu omSuiv) {
		menuSuiv = omSuiv;
	}
	
	public void setMenuCtrlr(MenuCtrlr monMaitre) {
		menuMaitre = monMaitre;
	}
	
	public String getRefOM() {
		return (ref_OM);
	}

	public String getTitreOM() {
		return titre_OM;
	}
	
	public void deselect() { 
		estSelectionne = false; 
		changeApparence = true;
		}
	
	public void select() { 
		estSelectionne = true;
		changeApparence = true;
	}
	
	
	public boolean estSelectionne() { return estSelectionne; }
	
	public boolean lienInactif() {
//		 tester si le lien de cette option de menu doit �tre inactiv� (inhiber si le focus ne peut �tre pris...
//				et si on n'est sur une option de menu d�j� selectionn�e)		
		if (menuMaitre.bandeauALeFocus() && !estSelectionne) return false; 
		else return true;
	}
	
	
	// ************* LES DECORATIONS *********
	public String srcImgOM() {
		if (changeApparence) {
			if (estSelectionne)
				urlImageFondOnglet = menuMaitre.imageUrl(MenuCtrlr.ONGs_FOND); 
			else	
				urlImageFondOnglet = menuMaitre.imageUrl(MenuCtrlr.ONGns_FOND); 
		}
		return urlImageFondOnglet;
	}
	
	public String imgBtBFiller() {
		// on traite a part le 1er onglet, qui existe necessairement et n'est pas ombre !
		// cas "plus d'onglet � afficher"
		if (changeApparence) {
			changeApparence = true;
			if (menuSuiv==null) {
				if (estSelectionne) urlImageBordDOnglet = menuMaitre.imageUrl(MenuCtrlr.ONGs_DROITE_LAST);
				else urlImageBordDOnglet = menuMaitre.imageUrl(MenuCtrlr.ONGns_DROITE_LAST);
			}
			else {
				// cas ou il y a un onglet ! Est-il s�lectionn� ?
				if (estSelectionne)
					urlImageBordDOnglet = menuMaitre.imageUrl(MenuCtrlr.ONGs_CROISE_BORD_DROIT); 
				else {
					if (menuSuiv.estSelectionne())
						urlImageBordDOnglet =  menuMaitre.imageUrl(MenuCtrlr.ONGs_CROISE_BORD_GAUCHE); 
					else urlImageBordDOnglet =  menuMaitre.imageUrl(MenuCtrlr.ONGns_CROISE); 
				}
			}
		}
		return urlImageBordDOnglet;
	}

	
	
}
