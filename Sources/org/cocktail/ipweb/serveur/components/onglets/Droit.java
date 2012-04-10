package org.cocktail.ipweb.serveur.components.onglets;
import org.cocktail.ipweb.serveur.Session;

import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.foundation.NSLog;

/*
 * Cr�� le 2 nov. 2006
 *
 *	Objectif : g�rer un droit en fonction des infos de la base...
 * @author olive
 *
 */
public class Droit {
	
	public static int DROIT_AUCUN = 1;
	public static int DROIT_CONSULT = 2;
	public static int DROIT_MODIF = 3;

	private EOGenericRecord eoIpwDroits;
	private String typeUser,droitStocke,keyFct;
	private Session maSession;
	private String spdColonne;
	private boolean spdSSIActif;
	
	
	public Droit(Session sess,EOGenericRecord droit,String unTypeUser,String keyFct) {
		super();
		maSession = sess;
		this.keyFct = keyFct;
		spdColonne = null;
		
		enregistreDroit(droit,unTypeUser,((String)droit.valueForKey(unTypeUser)).toUpperCase());
      	NSLog.out.appendln("fct° : "+keyFct+", droit : "+droitStocke);
	}

	public void majDroit(EOGenericRecord droit,String unTypeUser) {
		String droitFct = ((String)droit.valueForKey(unTypeUser)).toUpperCase();
		
		// une cl�e d�ja stock�e
		if (droit2plusGrand(droitStocke,droitFct)) {
			String oldDroit = droitStocke;
			enregistreDroit(droit,unTypeUser,droitFct);
			NSLog.out.appendln("fct� : "+keyFct+", droit : "+droitStocke+" remplace :"+oldDroit);
		}
	}

	private void enregistreDroit(EOGenericRecord droit,String unTypeUser,String droitVal) {
		typeUser = unTypeUser;
		eoIpwDroits = droit;
		droitStocke = droitVal;
		// est-ce un droit "complexe" ? (sur + de 2 caract�res)
		if (droitStocke.length()>2) {
			EOGenericRecord eoColSPD = (EOGenericRecord)eoIpwDroits.valueForKey("ipwDroitsScolpeda");
			spdColonne = (String)eoColSPD.valueForKey("spdColonne");
			String ipTypeRefSpd = (String)eoColSPD.valueForKey("ipTypeRefSpd");
			if (ipTypeRefSpd.equalsIgnoreCase("S")) spdSSIActif = false;
			else spdSSIActif = true;
		}
	}
	
	
	// comparaison entre deux droits : renvoyer "vrai" si le droit 2 est "sup�rieur" au droit 1
	private boolean droit2plusGrand(String droit1,String droit2) {
		// Ordre : null, "" , "A", "M", "Sxx", "Axx" 
		boolean plusGrand=false;
		int poidsDroit1 = evaluerDroit(droit1);
		int poidsDroit2 = evaluerDroit(droit2);
		if (poidsDroit2 > poidsDroit1) plusGrand = true;
		return plusGrand;		
	}
	
	public int evaluerDroit(String droit) {
		if (droit==null || droit.equals("")) return 0;
		else if (droit.equals("A")) return 1;
		else if (droit.charAt(0)=='A') return 2;
		else if (droit.charAt(0)=='S') return 3;
		else if (droit.equals("M")) return 4;
		else return 0;
	}
	
	/** Appel� depuis un des composants pour v�rifier le type de comportement � avoir
	 *   pour une fonction donn�e en ce qui concerne le type d'utilisateur en cours...
	 * 
	 * @return un string, qui est :
	 * - null : cette fonction n'est pas r�f�renc�e !
	 * - "" : sans objet pour ce type d'utilisateur...
	 * - "A" : affichage sans droits de modif
	 * - "M" : affichage + possibilit� de modif
	 * - "Sxx": n�cessite de regarder le droit xx dans ScolPeda 
	 * 			(si positionn� pour diplome/ann�e renvoyer 'M' sinon 'A')
	 * - "Axx" : n�cessite de regarder le droit xx dans ScolPeda (actif ou rien)
	 * 			(si positionn� pour diplome/ann�e renvoyer 'M' sinon null ! )
	 * 
	 */
	public int valeurDroit(EOGenericRecord eoDiplSPD) {
		//		laFonction = laFonction.toUpperCase();
		//		String leDroit = (String)droitsTypeUtilisateur.objectForKey(laFonction);
		//		if (leDroit != null) leDroit = leDroit.toUpperCase();
		if (droitStocke.equals("A")) return DROIT_CONSULT;
		else if (droitStocke.equals("M")) return DROIT_MODIF;
		else if (spdColonne != null) {	// acc�s fin aux droits de ScolPedagogie � faire...(indirection)
			String valColSpd = (String)eoDiplSPD.valueForKey(spdColonne);	// TODO : faire un try...catch !
			if (valColSpd.equalsIgnoreCase("A")) return DROIT_MODIF;
			else {
				if (spdSSIActif) return DROIT_AUCUN;
				else return DROIT_CONSULT;
			}
		}
		return DROIT_AUCUN;	
	}
		
}