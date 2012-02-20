/*
 * Créé le 8 oct. 2006
 *
 * @author olive
 *
 */
public class Token implements ElementAnalyse {
	public static int NOMBRE = 1;
	public static int PAR_OUVRE = 2;
	public static int PAR_FERME = 3;
	public static int OPERATEUR = 4;
	public static int ERREUR = 0;
	public static int OPE_UNAIRE = 5; // MODIF !

	private boolean operateurEt;
	private boolean operateurUnaire;

	private int valeur;
	private char caractere;
	private int type;

	public Token(int val) {
		super();
		valeur = val;
		type = NOMBRE;
	}

	public Token(char car) throws Exception {
		super();
		initToken(car);
	}
	
	private void initToken(char car) throws Exception {	
		caractere = car;
		operateurEt = false;
		operateurUnaire=false;
		if (car== '(') type = PAR_OUVRE;
		else if (car== ')') type = PAR_FERME;
		// MODIF !
		else if (car== '|' || car== '&' || car== '!') {
			type = OPERATEUR;
			if (car== '&') operateurEt = true;
			else if (car== '!') operateurUnaire = true;	// MODIF !
		}
		else {
			type = ERREUR;
			throw (new Exception("Token : Caractère non reconnu !"));
		}		
	}
	
	public boolean operateurUnaire() { return operateurUnaire; } // MODIF !
	
	// c'est le token qui va décider ce qu'il doit faire avec l'unité lexicale envoyée...
	public Token(String s) throws Exception {
		if (s.length()>0) {
			char car = s.charAt(0);
			if (car<'0' || car>'9') initToken(car);	// 1 caractère non numérique = opérateur ou paranthèses
			else {	// caractères numériques ?
				valeur = Integer.parseInt(s);
				type = NOMBRE;
			}
		}
		else throw (new Exception("Token : chaine vide !"));
	}
	
	public int type() {
		return type;
	}
	
	public String toString() {
		if (type == NOMBRE) return Integer.toString(valeur);
		return ""+caractere;
	}

	/* (non-Javadoc)
	 * @see ElementAnalyse#evaluerExpression()
	 */
	public int valeurNombre() throws Exception {
		if (type==NOMBRE) return valeur;
		else throw new Exception("Token non évaluable !");
	}
	
	public boolean operateurEt() throws Exception {
		if (type==OPERATEUR) return operateurEt;
		else throw new Exception("Ce Token n'est pas un opérateur !");
	}

	/* (non-Javadoc)
	 * @see ElementAnalyse#evaluerExpression()
	 * A priori on ne va jamais évaluer un token directement, mais un InscEcCtrlr (opérande)
	 */
	public boolean evaluerExpression() throws Exception {
		// TODO Raccord de méthode auto-généré
		return false;
	}
}
