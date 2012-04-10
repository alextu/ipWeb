package org.cocktail.ipweb.serveur.controlleur;
/*
 * Cr�� le 9 oct. 2006
 *
 * @author olive
 * Objectif : simuler la gestion d'une pile de stockage d'objets...
 *
 */
public class Pile {

	private Object[] pile;		// niveau de pile d�finit dans l'appel du constructeur
	private int ndxPile,capacitePile ;

	
	public Pile(int capacite) {
		super();
		pile = new Object[capacite];
		capacitePile = capacite;
		ndxPile = 0;				
	}
	
	public void empiler(Object obj) throws Exception {
		if (ndxPile<capacitePile)
			pile[ndxPile++]=obj;
		else throw (new Exception("Depassement de capacite"));
	}

	public Object depiler() {
		if (ndxPile>=1) return pile[--ndxPile];
		else return null;
	}
	
	public boolean estVide() {
		return (ndxPile==0);
	}
}
