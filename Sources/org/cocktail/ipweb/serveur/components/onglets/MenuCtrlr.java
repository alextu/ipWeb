package org.cocktail.ipweb.serveur.components.onglets;
/*
 * Cr�� le 27 mai 2006 (vient du projet Profil)
 *
 * Objectif : g�rer les comportements du menu du bandeau, en fonction du type de population
 * (en particulier gestion du blocage du focus sur action en attente de validation)
 * g�re l'�tats des diff�rentes options du menu + images graphiques � renvoyer...
 */


import java.util.Enumeration;

import org.cocktail.ipweb.serveur.Application;

import com.webobjects.appserver.*;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * @author olive
 *
 */
public class MenuCtrlr {
	
	private Application monApp;
	
	private WOContext monCtxt;
	private boolean focusBandeau;	// TRUE : on peut cliquer sur le lien pour d�clencher l'action de l'option de menu
	private OptionMenu optionSelectionnee;
	private NSArray listeOptions;	// liste des menus...
	private NSDictionary dictOptions;	// pour rep�rer une option par son code...
	
//	private int optionSelectionnee;
	private int nbOnglets;			// ne peut d�passer 9 dans cette version...
	
	// les �l�ments graphiques ...
	public static final String CADRE_BAS="cadreSeul_Bas.gif";
	public static final String CADRE_BAS_DROIT="cadreSeul_BasD.gif";
	public static final String CADRE_BAS_GAUCHE="cadreSeul_basG.gif";
	public static final String CADRE_DROIT="cadreSeul_CoteD.gif";
	public static final String CADRE_GAUCHE="cadreSeul_CoteG.gif";
	public static final String CADRE_HAUT="cadreSeul_FinHaut.gif";
	public static final String CADRE_HAUT_DROIT="cadreSeul_HautD.gif";
	public static final String CADRE_HAUT_DROIT_SUITE="cadreSeul_COINHD.gif";

	// partie "Onglets"
	public static final String ONGns_GAUCHE_FIRST="cadreSeul_ON1CG.gif";
	public static final String ONGns_CROISE="cadreSeul_ONCX.gif";
	public static final String ONGns_FOND="cadreSeul_ONF.gif";
	public static final String ONGns_DROITE_LAST="cadreSeul_ONFCD.gif";
	
	public static final String ONGs_GAUCHE_FIRST="cadreSeul_OS1CG.gif";
	public static final String ONGs_CROISE_BORD_DROIT="cadreSeul_OSCXD.gif";
	public static final String ONGs_CROISE_BORD_GAUCHE="cadreSeul_OSCXG.gif";
	public static final String ONGs_FOND="cadreSeul_OSF.gif";
	public static final String ONGs_DROITE_LAST="cadreSeul_OSFCD.gif";
	
	private String ress;
	
	
	public MenuCtrlr() {
		monApp = (Application)Application.application();
		monCtxt = null;
		focusBandeau = true;
	
		listeOptions = null;
		dictOptions = null;
		optionSelectionnee = null; // rien par d�faut n'est s�lectionn�...
	}

	// init externe faites par la classe Main au chargement de la page backOffice...
	public void setOnglets(NSArray listeOnglets) {

		listeOptions = listeOnglets;
		nbOnglets = listeOptions.count();
		optionSelectionnee = (OptionMenu)listeOptions.objectAtIndex(0);
		optionSelectionnee.select();
		
		NSMutableDictionary tmpDict = new NSMutableDictionary();
		java.util.Enumeration e = listeOptions.objectEnumerator();
		while (e.hasMoreElements()) {
			OptionMenu om = (OptionMenu)e.nextElement();
			String cle = om.getRefOM();
			tmpDict.setObjectForKey(om,cle);
		}
		dictOptions = tmpDict.immutableClone();
	}

	// Demande externe de sélectionner une option, par le nom de son entrée dans le dictionnaire...
	public boolean selectionneMenu(String nomOnglet) {
		if (nomOnglet != null && nomOnglet.length() > 0) {
			OptionMenu opt = (OptionMenu)dictOptions.objectForKey(nomOnglet);
			if (opt != null) {
				choisirOM(opt);
				return true;
			}
		}
		return false;
	}
	
	
	public NSArray listeOptions() { return listeOptions; }
	
	// appel� par BandeauControleur...
	public void donneContext(WOContext ctxt) {
		monCtxt = ctxt;		
		// localisation des ressources !
		ress = monApp.urlImage("images/"+ONGs_FOND,monCtxt);
		ress = ress.substring(0,ress.lastIndexOf("%5C")+3);
		
		Enumeration e = listeOptions.objectEnumerator();
		while (e.hasMoreElements()) {
			OptionMenu om = (OptionMenu)e.nextElement();
			om.setMenuCtrlr(this);
		}
	}

	public void enleverFocus() {
		focusBandeau = false;
	}

	public void rendreFocus() {
		focusBandeau = true;
	}

	public boolean bandeauALeFocus() { return focusBandeau; }
	
	// ---- indiquer au Ctrlr quel est l'option de menu qui vient d'�tre choisie...
	//		ref : IPM_CODE...
	public void choisirNumOM(String menuOption) { 
		OptionMenu tmpOS = (OptionMenu)dictOptions.objectForKey(menuOption);
		choisirOM(tmpOS);
	}
	
	// ---- indiquer au Ctrlr quel est l'option de menu qui vient d'�tre choisie...
	public void choisirOM(OptionMenu tmpOS) { 
		if (tmpOS != null) {
			if (optionSelectionnee != null) optionSelectionnee.deselect();
			optionSelectionnee = tmpOS;
			optionSelectionnee.select();
		}
	}
	
// ---- Savoir si le composant correspondant a été choisi pour visualisation ...
	// appelé de l'extérieur, donc faisant référence au code externe de l'onglet...
	public boolean estChoisi(String csteOnglet) {
		return (csteOnglet.equalsIgnoreCase(optionSelectionnee.getRefOM()));
	}
	

	public String imageUrl(String image) {		
		return monApp.urlImage("images/"+image,monCtxt);
	}
	
	// le premier coin arrondi !
	public String imgBtBFiller() {
		// on traite � part le 1er onglet, qui existe n�cessairement et n'est pas ombr� !
		OptionMenu firstOM = (OptionMenu)listeOptions.objectAtIndex(0);
		if (firstOM.estSelectionne()) return imageUrl(ONGs_GAUCHE_FIRST); 
		else return imageUrl(ONGns_GAUCHE_FIRST); 
	}
		
	
}
