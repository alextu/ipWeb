/*
 * Cr�� le 10 oct. 2006
 *
 * @author olive
 *
 *	Objectif :
 *			1) enregistrer toutes les solutions possibles de l'�quation bool�enne 
 *				exprimant des contraintes entre EC 
 *			2) �valuer celles qui sont viables en fonction des IP d�j� prises par l'�tudiant
 *			3) trouver la solution valide qui est la plus proche des choix d'EC effectu�s par l'�tudiant 
 */

import com.webobjects.foundation.*;

import java.util.*;

public class EspaceSolution {

	private static int[] tableNbreBits = {
			0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 
			1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 
			1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 
			2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 
			1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 
			2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 
			2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 
			3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 
			1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 
			2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 
			2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 
			3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 
			2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 
			3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 
			3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 
			4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8};	
	
	private NSArray ecCtConcernes;			// liste des ecCt sur lesquels porte cette �quation bool�enne
	private Expression expRacine;			// noeud principal de cette �quation bool�enne...
	private int nbreVariables;
	private int chaineChoixEtud;			// stockage (temp) de la chaine de bit repr�sentant 
											//	les choix de l'�tudiant pour les EC concern�s par l'�quation
	
	private int nbSoluces,nbSolucesTemp;	// liste des solutions en constitution (soluce = 1 Integer)
	private int[] solutions,solutionsTemp;
	private static int MAX_SOLUCES = 32;
	
	private int[] diffAvecSolucesActuelles;		// tableau de corrections � apporter aux choix actuels de l'�tudiant
												// pour arriver � 1 ou +ieurs solutions justes (m�me nbre de chgts)...
	
	public EspaceSolution(NSArray lesEcCt,Expression racine) throws Exception {
		super();
		ecCtConcernes = lesEcCt;
		expRacine = racine;
		nbreVariables = ecCtConcernes.count();
		if (nbreVariables>8) throw new Exception("Trop de variables (>8) !");
		solutionsTemp = new int[MAX_SOLUCES];
		nbSolucesTemp = 0;
		nbSoluces = 0;
		
	}
	
	
	// Init d'un espace de solution depuis les donn�es stock�es dans la base (�conomie de tps de traitement !)
	public EspaceSolution(String lesVariables,String lesSoluces,NSDictionary dico) throws Exception {
		super();
		if (recupererEcVariables(lesVariables,dico)) {
			nbreVariables = ecCtConcernes.count();
			if (nbreVariables>8) throw new Exception("Trop de variables (>8) !");
			
			if (!recupererSolutions(lesSoluces)) 
				throw new Exception("Pb lecture des solutions stockées dans la base..."); 
		}
		else {
			throw new Exception("Au moins une des variables pour cette contrainte n'est pas un nombre correctement formé !");
			// PO 2009 : différent
			// throw new Exception("Au moins une des variables pour cette contrainte n'est pas dans la maquette de ce semestre !");
		}
		
	}
	
	
	// on reconstruit la liste des Ec de l'espace Solutions à partir d'une version
	// stockée dans la base
	private boolean recupererEcVariables(String exprComp,NSDictionary dico) {
		String[] sousChaines = exprComp.split(",");
		NSMutableArray listeEcCT = new NSMutableArray();
		InscEcCtrlr ecCt;
		
		for (int i=0;i<sousChaines.length;i++) {
			try {
				Integer mrecK = new Integer(Integer.parseInt(sousChaines[i]));	// TODO : v�rifier si pb !!!
				ecCt = (InscEcCtrlr)dico.objectForKey(mrecK);
				
				// PO 2009 :  chgt = tte var non trouvé est réputé valoir FALSE
				//if (ecCt==null) return false; 	// cette variable n'est plus dans la liste des Ec du semestre 
												// la contrainte ne peut pas �tre v�rifi�e...
				if (ecCt != null) listeEcCT.addObject(ecCt);
			}
			catch (Exception e) {
				return false;	/// pb quelconque...
			}
		}
		ecCtConcernes = (NSArray)listeEcCT;
		return true;
	}
	
	// on reconstruit la liste des solutions de l'espace Solutions � partir d'une version
	// stock�e dans la base
	private boolean recupererSolutions(String listeSoluces) {
		String[] sousChaines = listeSoluces.split(",");
		nbSoluces = sousChaines.length;
		solutions = new int[nbSoluces];
		nbSolucesTemp = nbSoluces;
		
		for (int i=0;i<nbSoluces;i++) {
			try {
				solutions[i]= Integer.parseInt(sousChaines[i]);
			}
			catch (Exception e) {
				return false;	/// pb quelconque...
			}
		}
		return true;
	}

	
	public void changerListeEcCt(NSArray nouvelleListe) {
		ecCtConcernes = nouvelleListe;
	}
	
	public NSArray listeEcParticipantes() {
		return ecCtConcernes;
	}
	
	public String listeVariableString() {
		// extraire la liste ordonnée des variables... pour stockage BdD
		StringBuffer sb = new StringBuffer();
		java.util.Enumeration enumerator = ecCtConcernes.objectEnumerator();
		while (enumerator.hasMoreElements()) {
			InscEcCtrlr ecCt = (InscEcCtrlr)enumerator.nextElement();
			sb.append(ecCt.getMrecKey()+",");
		}
		return sb.substring(0,sb.length()-1);	// TODO : A v�rifier !!!!
	}

	public String listeSoluces() {
		// d'abord : est-ce que l'on a notre tableau d�finitif ? sinon le construire
		if (nbSoluces != nbSolucesTemp) {
			nbSoluces = nbSolucesTemp;
			solutions = new int[nbSoluces];
			for (int i=0;i<nbSoluces;i++) solutions[i]=solutionsTemp[i];
			Arrays.sort(solutions);
		}

		// extraire la liste ordonnée des solutions... pour stockage BdD
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<nbSoluces;i++) {
			int soluce = solutions[i];
			sb.append(soluce+",");
		}
		return sb.substring(0,sb.length()-1);	// TODO : A v�rifier !!!!
	}

	
	// Recherche heuristique des différentes solutions de l'équation booléenne
	// exprimant les contraintes de choix d'EC liés entre eux... 
	// --> on conserve les valeurs des variables donnant un résultat positifs 
	//		dans une array de "jeux de simulation"
	public boolean trouveSolutions() {
		boolean auMoinsUne = false;
		int nbJeuxDEssais = (int)(Math.pow((double)2,(double)nbreVariables));
		for (int i=1;i<nbJeuxDEssais;i++) {
			if (evaluerSolution(i)) auMoinsUne = true;
		}
		return auMoinsUne;
	}
	
	
	// Je teste cette combinaison de variables booléennes par rapport à l'expression racine (équation de contrainte)
	// si elle donne un résultat, je l'enregistre dans mon jeu d'essai...
	private boolean evaluerSolution(int numJeu) {
		boolean var;
		int soluce = numJeu;
		for (int i=0;i<nbreVariables;i++) {
			var = ((numJeu&1) == 1);
			numJeu=numJeu>>1;
			((InscEcCtrlr)ecCtConcernes.objectAtIndex(i)).setCaseCochee(var);
		}
		// lance l'éval de l'expression avec les valeurs fixées...
		try {
			if (expRacine.evaluerExpression()) {	// c'est une solution viable !
				ajoutSolution(soluce);
				return true;
			}
		}
		catch (Exception e) { }
		return false;
	}


	// on ajoute un nombre correspondant � une chaine binaire 
	//	ou chaque bit repr�sente un �tat pour une des variables de l'�quation bool�enne RESOLUE !
	private void ajoutSolution(int laSoluce) throws Exception {
		// ATTENTION ! Etre s�r de l'ordre des variables dans la chaine de bit 
		// (doit bien correspondre � l'ordre de la NSArray ecCtConcernes)
		if (nbSoluces<MAX_SOLUCES) {
			solutionsTemp[nbSolucesTemp++] = laSoluce;
		}
		else throw new Exception("Trop de soluces !");
	}
	
	// on demande d'�valuer si l'�tat actuel des cases coch�es pour les EC 
	//	(variables de l'�quation bool�enne dont cette instance repr�sente l'espace de solution)
	// 	correspond bien � une des solutions [v�rifier l'�quation avec des valeurs de variables]
	public boolean choixEcCorrect() {
		// d'abord : est-ce que l'on a notre tableau d�finitif ? sinon le construire
		if (nbSoluces != nbSolucesTemp) {
			nbSoluces = nbSolucesTemp;
			solutions = new int[nbSoluces];
			for (int i=0;i<nbSoluces;i++) solutions[i]=solutionsTemp[i];
			Arrays.sort(solutions);
		}
		int chaineDeBits = calculeChaineAvecEtats(false);
		int ndx = Arrays.binarySearch(solutions,chaineDeBits);
		return (ndx >=0);	// indique que l'on �tait dans une des configs de r�solution de l'�quation bool�enne...
	}

	// retourner les choix � changer par l'�tudiant pour avoir une solution fonctionnelle de choix d'EC
	// en fonction des contraintes !
	// REM : avoir appel� choixEcCorrect() avant est conseill� !
	
	// 		CAS PARTICULIER >> plus d'une solution la plus proche... les stocker en interne pour le cas ou
	//							et renvoyer tous les EC en cause pour les N solutions...
	//						>> Aucune solution possible ! (� cause des reports d'IP par la scol, par exemple) 
	public NSArray listeModifsConseillees() {
		int modifsAapporter;
		diffAvecSolucesActuelles = solutionsLesPlusProches();
		int nbSoluces = diffAvecSolucesActuelles.length;
		if (nbSoluces==0) return null;

		NSMutableArray listeEcCt = new NSMutableArray();	// liste des EC ou des choix doivent �tre modifi�s...
		InscEcCtrlr ecCt;
		for (int s=0;s<nbSoluces;s++) {
			modifsAapporter = diffAvecSolucesActuelles[s];
			for (int i=0;i<nbreVariables;i++) {
				if ((modifsAapporter&1) == 1) {
					ecCt = (InscEcCtrlr)ecCtConcernes.objectAtIndex(i);
					if (!listeEcCt.containsObject(ecCt))
						listeEcCt.addObject(ecCt);
				}
				modifsAapporter >>=1;
			}
			
		}
		return (NSArray)listeEcCt;
	}
	
	
	// A pr�sent calculer la distance entre une solution fausse et la plus proche des solutions possibles 
	//			en nombre de changements � faire dans les choix exprim�s par l'�tudiant !!!
	// REM : il faut aussi prendre en compte les choix bloqu� (IP des redoublants !!!)
	//
	// REM : si aucune solution n'est possible (!!!) renvoie un tableau vide !; 
	//		 sinon tableau de chaines repr�sentant les inversions � faire dans les choix de l'�tudiant pour
	//			   se rapprocher de la (ou les) solution(s) fonctionnelle(s) la/les + proche 
	
	private int[] solutionsLesPlusProches() {
		int[] listeSoluces = new int[10];	// 10 soluces identiques au max...
		int nbSolucesIdentiques = 0; 
		
		chaineChoixEtud = calculeChaineAvecEtats(false);
		int ecBloques = calculeChaineAvecEtats(true);	// sert de filtre pour les solutions possibles !
		int soluce,differences,distance;
		int distanceMini=8;
		// tout passer en revue...
		for (int i=0;i<nbSoluces;i++) {
			soluce = solutions[i];
			if ((soluce & ecBloques)==ecBloques) {		// solution compatible avec les choix d'EC bloqu�s...
				differences = soluce ^ chaineChoixEtud;		// un bon XOR met en valeur les bits non identiques !
				distance = tableNbreBits[differences];	// un moyen simple de compter les bits de diff�rence
				// on est pr�t pour voir qui c'est qu'� la plus courte :))
				if (distance<distanceMini) {
					listeSoluces[0] = differences;	// un nouveau vainqueur...
					nbSolucesIdentiques = 1;
					distanceMini = distance;
				}
				else if (distance==distanceMini) {	// une autre solution identique
					listeSoluces[nbSolucesIdentiques++] = differences;
				}
			}
		}
		// ATTENTION : on ne renvoie pas la solution MAIS la diff�rence avec la solution la + proche
		// (les inversions � apporter !)
		
		int[] listeSolutions = new int[nbSolucesIdentiques];
		for (int i=0;i<nbSolucesIdentiques;i++) listeSolutions[i] = listeSoluces[i];
		return listeSolutions;
	}
	
	
	
	// A partir des cases coch�es ou non, cr�er une chaine de bits (dans un int) pour �valuation...
	// sert aussi � avoir des chaines de bits de choix bloqu�s !
	private int calculeChaineAvecEtats(boolean bloques) {
		InscEcCtrlr ecCt;
		int chaineDeBits = 0;
		int var;
		for (int i=nbreVariables-1;i>=0;i--) {	// ATTENTION A L'ORDRE !!!
			ecCt = (InscEcCtrlr)ecCtConcernes.objectAtIndex(i);
			var = 0;
			if (!bloques) {
				if(ecCt.evaluerExpression()) var = 1;
			}
			else {
				if (ecCt.isEcBloque()) var = 1;
			}
			chaineDeBits = (chaineDeBits<<1)|var; 
		}
		return chaineDeBits;
	}

	
	
	// Sortir des conseils sur les cases � cocher ou � d�cocher dans les choix "erron�s" de l'�tudiant 
	// pour arriver � l'une au moins des solutions les plus proches
	public String modifsConseillees() {
		// on part du tableau des diff�rences avec choix actuels :
		int nbSol = diffAvecSolucesActuelles.length;
		StringBuffer conseil = new StringBuffer();
		for (int i=0;i<nbSol;i++) {
			if (i>0) conseil.append(" OU BIEN ");
			conseil.append(texteModifAFaire(diffAvecSolucesActuelles[i]));
		}
		return conseil.toString();
	}
	
	// pour une chaine de diff�rence, faire les commentaires qui s'imposent...
	private String texteModifAFaire(int modifsAapporter) {
		StringBuffer sb = new StringBuffer();
		boolean premierEc;
		boolean aCocher=false;
		NSMutableArray ecADecocher,ecACocher;
		ecADecocher = new NSMutableArray();
		ecACocher = new NSMutableArray();
		
		// constituer les listes de conseils sur cases � cocher Et � d�cocher...
		InscEcCtrlr ecCt;
		for (int i=0;i<nbreVariables;i++) {
			if ((modifsAapporter&1) == 1) {
				ecCt = (InscEcCtrlr)ecCtConcernes.objectAtIndex(i);
				// on regarcde ce qu'il faut modifier en cochant ou d�cochant...
				if (ecCt.getCaseCochee()) ecADecocher.addObject(ecCt);
				else ecACocher.addObject(ecCt);
			}
			modifsAapporter >>=1;
		}
		
		// les cases � cocher :
		if (ecACocher.count()>0) {
			aCocher=true;
			premierEc = true;
			sb.append("COCHER ");
			Enumeration e = ecACocher.objectEnumerator();
			while (e.hasMoreElements()) {
				ecCt = (InscEcCtrlr)e.nextElement();
				if (!premierEc) sb.append(", ");
				else premierEc=false;
				sb.append(ecCt.getRefEcEtUe());
			}
			
		}
		
		if (ecADecocher.count()>0) {
			if (aCocher) sb.append(" ET ");
			premierEc = true;
			sb.append("DECOCHER ");
			Enumeration e = ecADecocher.objectEnumerator();
			while (e.hasMoreElements()) {
				ecCt = (InscEcCtrlr)e.nextElement();
				if (!premierEc) sb.append(", ");
				else premierEc=false;
				sb.append(ecCt.getRefEcEtUe());
			}
		}
		return sb.toString();
	}
	
}
