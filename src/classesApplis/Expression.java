/*
 * Créé le 9 oct. 2006
 *
 * Objectif : gérer une expression, qui peut être composée d'autres expressions ou de token...
 * 
 */

/**
 * @author olive
 */
public class Expression implements ElementAnalyse {

	protected ElementAnalyse terme1;	// peut-être un token ou une autre expression...
	protected ElementAnalyse terme2;	// peut-être un token ou une autre expression...
	protected Token	operateur;	// le token opérateur pour cette expr.
	
	private boolean estFinale;	// MODIF 
	private boolean estUnaire;	
	
	public Expression(ElementAnalyse t1,ElementAnalyse t2,Token ope) {
		super();
		terme1 = t1;
		terme2 = t2;
		operateur = ope;
		estFinale = false;	// MODIF !
	}
	
	// cas particulier de l'expression "finale" qui contient directement le résultat !
	public Expression(ElementAnalyse t) {
		terme1 = t;
		estFinale = true;	// MODIF 
	}

	// cas particulier de l'expression "opérateur unaire" qui contient directement le résultat !
	public Expression(ElementAnalyse t,Token ope) {
		terme1 = t;
		operateur = ope;
		estUnaire = true;
		estFinale = false;	// MODIF !
	}

	// exemple de méthode d'évaluation "basique" !
	public boolean evaluerExpression() throws Exception {
		if (estFinale) return terme1.evaluerExpression();	// MODIF !
		else {
			if (operateur.operateurUnaire()) return (!terme1.evaluerExpression());
			else if (operateur.operateurEt()) return (terme1.evaluerExpression() & terme2.evaluerExpression());
			else return (terme1.evaluerExpression() ^ terme2.evaluerExpression());
		}
	}

	// représentation du sous-arbre dont cet objet est la racine...
	public String toString() {
		if (estFinale) return terme1.toString();	// MODIF !
		else {
			if (estUnaire) {
				StringBuffer sb = new StringBuffer();
				sb.append(operateur.toString());
				if (!(terme1 instanceof InscEcCtrlr)) {
					sb.append("(");
					sb.append(terme1.toString());
					sb.append(")");
				}
				else sb.append(terme1.toString());
				return sb.toString();
			}
			else {
				StringBuffer sb = new StringBuffer();
				if (!(terme1 instanceof InscEcCtrlr)) {
					sb.append("(");
					sb.append(terme1.toString());
					sb.append(")");
				}
				else sb.append(terme1.toString());
				sb.append(operateur.toString());
				if (!(terme2 instanceof InscEcCtrlr)) {
					sb.append("(");
					sb.append(terme2.toString());
					sb.append(")");
				}
				else sb.append(terme2.toString());
				return sb.toString();
			}
		}
	}
	
}
