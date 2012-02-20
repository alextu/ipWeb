/*
 * Créé le 9 oct. 2006
 *
 * Objectif de cette classe abstraite : être pêre des classes Token, Expression, InscEcCtrlr
 * 			pour appels par polymorphisme...
 */

/**
 * @author olive
 *
 * TODO Pour changer le modèle de ce commentaire de type généré, allez à :
 * Fenêtre - Préférences - Java - Style de code - Modèles de code
 */
public abstract interface ElementAnalyse {

	/**
	 * 
	 */
	public abstract boolean evaluerExpression()throws Exception;

}
