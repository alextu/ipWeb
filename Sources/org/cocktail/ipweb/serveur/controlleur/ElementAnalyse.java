package org.cocktail.ipweb.serveur.controlleur;
/*
 * Cr�� le 9 oct. 2006
 *
 * Objectif de cette classe abstraite : �tre p�re des classes Token, Expression, InscEcCtrlr
 * 			pour appels par polymorphisme...
 */

/**
 * @author olive
 *
 * TODO Pour changer le mod�le de ce commentaire de type g�n�r�, allez � :
 * Fen�tre - Pr�f�rences - Java - Style de code - Mod�les de code
 */
public abstract interface ElementAnalyse {

	/**
	 * 
	 */
	public abstract boolean evaluerExpression()throws Exception;

}
