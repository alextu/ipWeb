import java.text.StringCharacterIterator;
import com.webobjects.foundation.*;

/*
 * Cr�� le 7 oct. 2006
 *
 * @author olive
 *
 */
public class Analyseur {
	private String expression;
	private NSDictionary dicoEc;
	private boolean expressionCorrecte;
	
	private int posExpr;	// en cas d'erreur, indique ou dans l'expression l'analyse "syntaxique" a rep�r� le pb
	private NSArray listeSortie;	// liste exploitable de token (ordonn�e selon dans l'ordre de la notation polonaise inverse)
	private Expression expRacine;	// racine de l'arbre binaire permettant l'�valuation de l'expression 
	
	private NSArray listeEcCtEquation;	// inventaire des diff�rents EC rencontr� dans l'�quation bool�enne...

	private NSMutableDictionary dicoEcCtSimul;	// dico des ecCt fictifs n�cessaires pour la simulation.
	private NSMutableDictionary dicoEcCtConst;	// dico des EC "constantes" (ceux non présent dans la maquette des choix de ce semestre, par ex.)
	
	private NSArray listeEcCtSimul;				// liste finale des ecCt fictifs n�cessaires pour la simulation.
	private EspaceSolution monJeuDEssai;	// les solutions de l'�quation bool�enne...
	
	// on construit tout de A � Z � partir de la formule compl�te...
	public Analyseur(String expr,NSDictionary dico) throws Exception {
		super();
		dicoEc = dico;
		expRacine = null;
		if (lancerAnalyse(expr)) {
			expressionCorrecte=true;	// on a construit en interne une liste exploitable de token...
			expRacine = constructionArbre(false);		// construire un arbre d'objets repr�sentant l'expression pour pouvoir l'�valuer...
			// TODO : stocker les r�sultats de la simulation dans une table de IP_WEB !
			lancerSimulation();		// obtenir la liste des solutions
			monJeuDEssai.changerListeEcCt(listeEcCtEquation);	// pas propre : � revoir !!!
		}
		else {
			expressionCorrecte=false;						// ou erreur dans l'�criture de l'expression !
			throw new Exception("L'analyse a echouee !");
		}
	}
	
	// on initialise l'analyseur � partir d'une version interpr�t�e de l'�quation bool�enne,
	// stock�e dans la base...
	public Analyseur(NSDictionary dico,String exprComp) throws Exception {
		super();
		dicoEc = dico;
		expRacine = null;
		if (recupererAnalyse(exprComp)) {
			expressionCorrecte=true;	// on a construit en interne une liste exploitable de token...
			expRacine = constructionArbre(false);		// construire un arbre d'objets repr�sentant l'expression pour pouvoir l'�valuer...
			// TODO : r�cup�rer les r�sultats de la simulation dans une table de IP_WEB !
			lancerSimulation();		// obtenir la liste des solutions
			
			if (monJeuDEssai == null) { 	// PB avec l'expression à analyser !!!
				System.out.println("ATTENTION !!!!!!! Pb avec l'expression à analyser :"+exprComp);
				expressionCorrecte=false;						// ou erreur dans l'ecriture de l'expression !
				throw new Exception("L'analyse a echouee !");
			}
			else monJeuDEssai.changerListeEcCt(listeEcCtEquation);	// pas propre : � revoir !!!
		}
		else {
			expressionCorrecte=false;						// ou erreur dans l'ecriture de l'expression !
			throw new Exception("L'analyse a echouee !");
		}
	}
	
	public EspaceSolution getSysteme() { return monJeuDEssai; }
	
	// on reconstruit la liste des Tokens � partir d'une version interpr�t�e de l'�quation bool�enne...
	private boolean recupererAnalyse(String exprComp) throws Exception {
		String[] sousChaines = exprComp.split(",");
		NSMutableArray listeToken = new NSMutableArray();
		
		for (int i=0;i<sousChaines.length;i++) 
			listeToken.addObject(new Token(sousChaines[i]));
		
		listeSortie = (NSArray)listeToken;
		
		return true;
	}
	
	public boolean evaluerExpression() {
		//		if (expRacine != null) return expRacine.evaluerExpression();
		//		else throw new Exception("Pb d'evaluation de l'expression : pas de racine g�n�r�e...");
		
		// essai de la nouvelle m�thode :
		return monJeuDEssai.choixEcCorrect();
	}
	
	
	// affichage des tokens dans l'ordre final, s�par� par des virgules !
	public String toString() {
		if (expressionCorrecte && listeSortie != null && listeSortie.count()>0) {
			boolean debutSortie = true;
			StringBuffer sb = new StringBuffer();
			java.util.Enumeration enumerator = listeSortie.objectEnumerator();
			while (enumerator.hasMoreElements()) {

				if (debutSortie) debutSortie = false;
				else sb.append(",");

				Token token = (Token)enumerator.nextElement();
				sb.append(token.toString());
			}
			return sb.toString();
		}
		else return msgErreur();
	}

	public boolean erreurAnalyse() { return (!expressionCorrecte); }
	
	public String msgErreur() {
		// expliquer ou l'analyse de l'expression a foir�e !!!
		StringBuffer sb = new StringBuffer("Erreur de syntaxe : ");
		if (expression == null || expression.length()==0) sb.append("Chaine vide !"); 
		else {
			sb.append(expression.substring(0,posExpr)+"[*]");
			sb.append(expression.substring(posExpr));			
		}
		return sb.toString();
	}
	
	public Expression expRacine() {
		return expRacine;
	}
	
	// pour v�rif : ressort l'expression analys�e !
	public String arbreToString() { 
		if (expRacine != null)
			return expRacine.toString(); 
		else return "erreur d'analyse...";
	}
	
	// PO 2009 : si une variable ne figure pas dans les EC chargées "par défaut", on considère sont résultat comme FAUX (choix pas sélectionné)
	private boolean lancerAnalyse(String expr) {
		expression = expr;
		NSArray listeToken = tokenizationChaine();
		if (listeToken==null || listeToken.count()==0) return false;	// l'analyse ne donnera rien !!!
		try {
			listeSortie = analyseToken(listeToken);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		if (listeSortie==null || listeSortie.count()==0) return false;	// l'analyse ne donnera rien !!!
		return true;
	}

	// on s�pare les composants de la String 
	// et on v�rifie que l'on ne sort pas de l'alphabet autoris�...
	// sans oublier d'appliquer les r�gles de syntaxe !
	private NSArray tokenizationChaine() {
		NSMutableArray fileSortie = new NSMutableArray();
		Token token,tokenPrec;
		token = null;

		boolean lectureNombre=false;
		boolean finAnalyse = false;
		int nombre=0;
		posExpr=0;
		int nbParantheses = 0;

		if (expression == null || expression.length()==0) return null; // chaine vide !
		
		StringCharacterIterator iter = new StringCharacterIterator(expression);
		// on it�re tous les caract�res de l'expression � transformer en Token
		for(char c = iter.first(); !finAnalyse; c = iter.next()) {
			// MODIF !
			if (c==' ' || c=='(' || c==')' || c=='|' || c=='&' || c=='!' || c == StringCharacterIterator.DONE) {
				if (lectureNombre) {
					lectureNombre=false;
//					 teste la syntaxe sur la suite de token, par paires !
					tokenPrec = token;
					token = new Token(nombre);
					nombre=0;
					if (!verifieSyntaxe(token,tokenPrec)) return null;	// ON EST SUR LA FAUTE A SIGNALER EN SORTIE !
					fileSortie.addObject(token);
				}
				if (c!=' ' ) {
					tokenPrec = token;
					// cas particulier de la fin de chaine !
					// tester si on fini bien avec autre chose qu'un op�rateur ou '('
					if (c == StringCharacterIterator.DONE) {
						finAnalyse=true;
						if (tokenPrec.type()==Token.PAR_OUVRE || tokenPrec.type()==Token.OPERATEUR) return null;
					}
					else {
						//	teste la syntaxe sur la suite de token, par paires !
						try { token = new Token(c); }
						catch (Exception e) {	// pb avec l'analyseur bloqu� avant...
						}
						if (!verifieSyntaxe(token,tokenPrec)) return null; // ON EST SUR LA FAUTE A SIGNALER EN SORTIE !
						fileSortie.addObject(token);
						if (c=='(') nbParantheses++;
						if (c==')') nbParantheses--;
					}
				}
			}
			else if (c>='0' && c<='9') {
				if (!lectureNombre) {
					lectureNombre=true;
					nombre = c-'0';
				}
				else nombre = nombre * 10 + c-'0';
			}
			else return null;	// tout autre caract�re provoque une erreur et l'arr�t de l'analyse	
			posExpr++;
		}
		posExpr--;	// on avait avanc� au dela de la fin de chine...
		// Tester le bon nombre de parenth�ses !
		if (nbParantheses != 0)	return null;	// si != 0 on n'a pas autant de fermantes que d'ouvrantes...
		return (NSArray)fileSortie;
	}

	// teste la syntaxe sur la suite de token, par paires !
	private boolean verifieSyntaxe(Token token,Token tokenPrec) {
		int type = token.type();
		if (token.operateurUnaire()) type = Token.OPE_UNAIRE;	// MODIF !
		if (tokenPrec != null) {
			int typePrec = tokenPrec.type();
			if (tokenPrec.operateurUnaire()) typePrec = Token.OPE_UNAIRE; // MODIF !

			if (typePrec==Token.PAR_FERME && (type == Token.OPE_UNAIRE || type==Token.NOMBRE || type==Token.PAR_OUVRE)) return false;  // MODIF !
			if (typePrec==Token.PAR_OUVRE && (type==Token.OPERATEUR || type==Token.PAR_FERME)) return false;
			if (typePrec==Token.OPERATEUR && (type==Token.PAR_FERME || type==Token.OPERATEUR)) return false;
			if (typePrec==Token.NOMBRE && (type == Token.OPE_UNAIRE || type==Token.NOMBRE || type==Token.PAR_OUVRE)) return false; // MODIF !
			if (typePrec==Token.OPE_UNAIRE && (type == Token.OPE_UNAIRE || type==Token.OPERATEUR || type==Token.PAR_FERME)) return false;  // MODIF !
		}
		else if (type==Token.OPERATEUR || type==Token.PAR_FERME) return false;
		return true;
	}
	
	
	// je vais appliquer les r�gles de transformation pour avoir ...
	// en sortie la liste de token remise dans l'ordre de la notation polonaise inverse,
	// ou null si pb
	private NSArray analyseToken(NSArray listeToken) throws Exception {
		NSMutableArray fileSortie = new NSMutableArray();
		Pile pileOperateurs = new Pile(20);		// 20 niveau de pile; �a devrailt le faire !
		Token token,tokenOperande,tokenNot;
		int typeToken;

		tokenOperande = null;
		tokenNot = null;
		java.util.Enumeration enumerator = listeToken.objectEnumerator();
		while (enumerator.hasMoreElements()) {
			token = (Token)enumerator.nextElement();
			typeToken = token.type();
			/// TRANSFORMATION
//			if (typeToken == Token.PAR_OUVRE && tokenOperande != null) {
//				// empiler l'op�rande...
//				try {
//					pileOperateurs.empiler(tokenOperande);
//				}
//				catch(Exception e) {
//					System.out.println(e.getMessage());
//					return null;
//				}
//				tokenOperande = null;
//			}
			if (typeToken == Token.PAR_OUVRE) {
				if(tokenOperande != null) {	// MODIF !
					// empiler l'op�rande...
					pileOperateurs.empiler(tokenOperande);
					tokenOperande = null;
				}
				if(tokenNot != null) {	// MODIF !
					// empiler l'op�rande unaire...
					pileOperateurs.empiler(tokenNot);
					tokenNot = null;
				}
			}
//			else if (typeToken == Token.OPERATEUR) tokenOperande = token;
			else if (typeToken == Token.OPERATEUR) {
				if (token.operateurUnaire()) tokenNot = token;
				else tokenOperande = token;
			}
//			else if (typeToken == Token.NOMBRE) {
//				fileSortie.addObject(token);
//				if (tokenOperande != null) {
//					fileSortie.addObject(tokenOperande);
//					// vider l'op�rande...
//					tokenOperande = null;
//				}
//			}
			else if (typeToken == Token.NOMBRE) {
				fileSortie.addObject(token);
				if (tokenNot != null) {
					fileSortie.addObject(tokenNot);
					// vider l'op�rande...
					tokenNot = null;
				}
				if (tokenOperande != null) {
					fileSortie.addObject(tokenOperande);
					// vider l'op�rande...
					tokenOperande = null;
				}
			}
//			else if (typeToken == Token.PAR_FERME) {
//				if (!pileOperateurs.estVide()) {
//					tokenOperande = (Token)pileOperateurs.depiler();
//					if (tokenOperande != null) {
//						fileSortie.addObject(tokenOperande);
//						tokenOperande = null;
//					}
//				}
//			}
			else if (typeToken == Token.PAR_FERME) {
				if (!pileOperateurs.estVide()) {
					tokenOperande = (Token)pileOperateurs.depiler();
					if (tokenOperande != null) {
						fileSortie.addObject(tokenOperande);
						if (tokenOperande.operateurUnaire()) {	// on a sorti un ! donc continuer si possible
							if (!pileOperateurs.estVide()) {
								tokenOperande = (Token)pileOperateurs.depiler();
								if (!tokenOperande.operateurUnaire()) {	// & ou |, ajouter....							fileSortie.addObject(tokenOperande);
									fileSortie.addObject(tokenOperande);
								}
								else {	// deux ! d'affil�es, remetre le dernier...
									pileOperateurs.empiler(tokenOperande);
								}
							}							
						}
						tokenOperande = null;
					}
				}
			}
		}
		// gestion de la fin de chaine
		// MODIF !
		if (!pileOperateurs.estVide()) {
			tokenOperande = (Token)pileOperateurs.depiler();
			if (tokenOperande != null) {
				fileSortie.addObject(tokenOperande);
				tokenOperande = null;
			}
		}

		return (NSArray)fileSortie;
	}
	
	// Recherche heuristique des diff�rentes solutions de l'�quation bool�enne
	// exprimant les contraintes de choix d'EC li�s entre eux... 
	// --> On effectue la simulation sur des ecCt fictifs.
	// --> on conserve les valeurs d'ecCt fictifs donnant un r�sultat positifs 
	//		dans une array de "jeux de simulation"
	
	public void lancerSimulation() {
		dicoEcCtSimul = new NSMutableDictionary();
		try {
			Expression racine = constructionArbre(true);	// on a une nouvelle instance d'expression
									// qui travaille sp�cifiquement sur des ecCt de simulation !
			monJeuDEssai = new EspaceSolution(listeEcCtSimul, racine);
			monJeuDEssai.trouveSolutions();	// la recherche heuristique est d�l�gu�e...
			
		}
		catch (Exception e) {
			
		}	
	}
	
	
	
	// � partir de la liste ordonn�e de token, construire un arbre d'objets repr�sentant l'expression
	// dans le but de pouvoir l'�valuer... 
	//		==> relation entre les Token.Nombres et les ecCt via le dico transmis !!!
	//		--> Cas de la simulation : On cr� des ecCt fictifs pour la simulation
	// en profiter pour faire un inventaire des diff�rents EC rencontr� dans l'�quation bool�enne...
	
	private Expression constructionArbre(boolean simulation) throws Exception {
		Expression exprEnCours=null;
		Pile stockage =new Pile(20);
		Token token;
		int typeToken;
		InscEcCtrlr ecCtDuToken;
		ElementAnalyse filsGauche,filsDroit;
		
		InscEcCtrlr ecCt;
		NSMutableArray listeEcRencontres = new NSMutableArray();
		
		// parcours de la liste des tokens
		java.util.Enumeration enumerator = listeSortie.objectEnumerator();
		while (enumerator.hasMoreElements()) {
			token = (Token)enumerator.nextElement();
			typeToken = token.type();
			if (typeToken == Token.NOMBRE) {
				if (simulation)	
					ecCt = creerEcCtFictif(new Integer(token.valeurNombre()));	// ecCt fictif pour simul...
				else ecCt = chercherEcCt(token); // le controleur d'EC associ� au mrec_key port� par le token Nombre (exception si non trouv�)
				stockage.empiler(ecCt);		// on l'empile  ...
				if (!listeEcRencontres.containsObject(ecCt))	// et s'il ne l'�tait pas d�j� :
					listeEcRencontres.addObject(ecCt);			// on le r�f�rence dans la liste des EC rencontr�s dans cette �quation...
			}
			else if (typeToken == Token.OPERATEUR) {
//				filsDroit = (ElementAnalyse)stockage.depiler();
//				filsGauche = (ElementAnalyse)stockage.depiler();
//				exprEnCours = new Expression(filsGauche,filsDroit,token);
//				stockage.empiler(exprEnCours);
				filsDroit = (ElementAnalyse)stockage.depiler();
				if (token.operateurUnaire()) {	// MODIF !
					exprEnCours = new Expression(filsDroit,token); // MODIF !
				}
				else {
					filsGauche = (ElementAnalyse)stockage.depiler();
					exprEnCours = new Expression(filsGauche,filsDroit,token);
				}
				stockage.empiler(exprEnCours);
			}
		}
		if (simulation) listeEcCtSimul = (NSArray)listeEcRencontres;
		else listeEcCtEquation = (NSArray)listeEcRencontres;
		
		// le r�sultat final est dans la pile dans tous les cas
		Object resultat = stockage.depiler();
		if (resultat instanceof InscEcCtrlr) {
			// cas ou l'expression consistait juste en 1 nombre !
			return new Expression((InscEcCtrlr)resultat);
		}
		else return (Expression)resultat;
			
	}
	
	// cr�ation d'ecCt ficitf si mrec_key pas d�j� rencontr�...
	private InscEcCtrlr creerEcCtFictif(Integer mrecK) {
		InscEcCtrlr ecCt=null; 
	
		if (dicoEcCtSimul!=null) {
			ecCt = (InscEcCtrlr)dicoEcCtSimul.objectForKey(mrecK);
			if (ecCt == null) {
				ecCt = new InscEcCtrlr(mrecK); 
				dicoEcCtSimul.setObjectForKey(ecCt,mrecK);
			}
		}
		return ecCt;
	}
	

	
	// PO 2009 : On veut changer le comportement=> Les EC non trouvés sont évalués à faux ! (cas de la validation du Sport...)
	// La correspondance entre le porteur du MREC_KEY (token) et le controleur associ� � l'EC !
	private InscEcCtrlr chercherEcCt(Token leToken) throws Exception{
		Integer mrecKey = new Integer(leToken.valeurNombre());
		InscEcCtrlr ecCt = (InscEcCtrlr)dicoEc.objectForKey(mrecKey);
		if (ecCt == null) {
			ecCt = creationEcConst(mrecKey);
			// throw (new Exception("Le semestre ne comporte pas d'EC avec MREC_KEY = "+mrecKey));
		}
		return ecCt;
	}
	
	
	private InscEcCtrlr creationEcConst(Integer mrecKey) {
		InscEcCtrlr ecCt = null;
		if (dicoEcCtConst==null) {
			dicoEcCtConst = new NSMutableDictionary();
		}
		else ecCt = (InscEcCtrlr)dicoEcCtConst.objectForKey(mrecKey);
		
		if (ecCt == null) {
			ecCt = new InscEcCtrlr(mrecKey,false); 
			dicoEcCtConst.setObjectForKey(ecCt,mrecKey);
		}
		return ecCt;
	}
	

	public NSArray listeEcParticipant() {
		return listeEcCtEquation;
	}
}


