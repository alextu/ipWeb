import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

/*
 * Cr�� le 9 oct. 2006
 *
 * @author olive
 *
 * Sous classe de RelationChoixEc pour g�rer les expressions booleennes liants les choix d'EC entre eux...
 *	
 *  
 * */

public class RelationChoixExpression extends RelationChoixEc {

	private Analyseur anaExpr;
	private EspaceSolution monSpace;			// il est l�, le mini syst�me expert; LA !
	private boolean analyseReussie,majExpressionCompacte;
	private InscSemestreCtrlr dicoEcSemestre;	// dictionnaire de toutes les EC du semestre par MREC_KEY...
	
	private boolean choixObligatoire;	// si VRAI alors l'expression NE PEUT ETRE FAUSSE,
										//	m�me si aucun des choix d'EC n'est coch� ! 
	private String commentaireContrainte;

	// si un EO pour une contrainte n'�tait pas renseign�, 
	// l'espace de solution "interpr�t�" a �t� renseign� et il faudra faire une synchro pour l'enregistrer dans la base  
	public RelationChoixExpression(IpRelationChoixEc eoContrainte,NSDictionary dicoEc,EOEditingContext ecSess) throws Exception {
		super();
		String expression = eoContrainte.rceFormuleContrainte();
		String lesSoluces = eoContrainte.rceEspaceSolution();
		String lesVariables = eoContrainte.rceListeVariables();
		boolean rendreObligatoire = (eoContrainte.rceTypeRelation().equalsIgnoreCase("O"));
		commentaireContrainte = eoContrainte.rceCommentaireContrainte();
		
		// Trouver l'EC cible...
		Integer mrecKcible = (Integer)eoContrainte.mrecKeyCible();
		InscEcCtrlr ecCt = (InscEcCtrlr)dicoEc.objectForKey(mrecKcible);
		if (ecCt!= null) {
			// l'EC cible est bien load� !
			ecCt.setComment(commentaireContrainte);
		}
		
		majExpressionCompacte = false;
		choixObligatoire = rendreObligatoire;
		// si la forme d�j� analys�e de l'�quation bool�enne existe,
		// initialiser avec cette forme compress�e et pr�mach�e de la formule ! (gain de temps)
		if ( lesSoluces != null && !lesSoluces.equals("")) {
			monSpace = new EspaceSolution(lesVariables,lesSoluces,dicoEc);
			relationEc = monSpace.listeEcParticipantes();	// d�duit des variables de cette espace...
		}
		else {
			anaExpr = new Analyseur(expression,dicoEc);
			// enregistrer la formule compact�e pour la prochaine fois...
			// TODO : enregistrer la liste des solutions aussi !!!
			relationEc = anaExpr.listeEcParticipant();	// la liste des EC pr�sent dans l'�quation bool�enne..
			analyseReussie=true;
			monSpace = anaExpr.getSysteme();		// TODO : � loader depuis la base !!!
			
			eoContrainte.setRceListeVariables(monSpace.listeVariableString());
			eoContrainte.setRceEspaceSolution(monSpace.listeSoluces());
			
			//				setRceContrainteTraitee(anaExpr.toString());
			majExpressionCompacte = true;
		}
		
	}
	
	public boolean majExpressionCompacte() { return majExpressionCompacte; }
		
//	vérifier la relation en cours si une erreur n'existerait pas pour les EC qu'elle contient..
//  MODIF : on signale aussi sur les EC anciennement sélectionnées (on sait pas ce que l'étudiant veut vraiment faire...) 
//    ===> lance l'évaluation de l'expression sur la racine de l'arbre...

	public boolean verifierRelation() throws Exception {
		boolean res=true;
		// 1) Le resultat de cette expression n'est � prendre en compte 
		//	  QUE (SI au moins un des EC de l'expression bool�enne est coch�...)
		//	  OU  (SI le choix d'un EC (au moins) est obligatoire !)
		if (choixObligatoire || verifierSiConcerne()) {	
			// 2) on peut �valuer cette expression pour voir si les contraintes sont respect�es.
			//		res = anaExpr.evaluerExpression(); --> on n'a plus besoin de l'analyseur pour le faire
			//										   --> laisser le syst�me s'en charger plus directement !
			res = monSpace.choixEcCorrect();
			// 3) si le r�sultat est faux on signale slt sur les EC en cause qu'il y a pb ! 
			if (!res) signalErreur(monSpace.listeModifsConseillees());
		}
		return res;
	}
	
	// l'expression sera � �valuer si au moins un des EC qui la compose est coch�...
	//	REM : RAZ g�n�ral des messages d'erreur fait avant dans InscSemestreCtrlr !!!
	private boolean verifierSiConcerne() {
		java.util.Enumeration enumerator = relationEc.objectEnumerator();
		while (enumerator.hasMoreElements()) {
			InscEcCtrlr ecCt = (InscEcCtrlr)enumerator.nextElement();
			if (ecCt.getCaseCochee() || ecCt.ecAvecIp()) return true;
		}
		return false;
	}

	// on indique sur les Ec impliqu�s dans l'�quation bool�enne (sauf ceux � choix bloqu�s)
	// que la contrainte n'est pas respect�e ! (proposition de solution par le << Syst�me-expert >>)
	private void signalErreur(NSArray lalisteEcCt) {
		String errMsg=commentaireContrainte;;
		String conseil=monSpace.modifsConseillees(); 
		
		InscEcCtrlr ecCt=null;
		// TODO : creuser ce cas d'erreur...
		if (lalisteEcCt != null && lalisteEcCt.count()>0) {
			java.util.Enumeration enumerator = lalisteEcCt.objectEnumerator();
			while (enumerator.hasMoreElements()) {
				ecCt = (InscEcCtrlr)enumerator.nextElement();
				if (!ecCt.isEcBloque()) {
					ecCt.setErreur(errMsg,conseil);
				}
			}
			dernierEcEnErreur = ecCt;
		}
	}
	
	public String visuArbre() {
		if (analyseReussie) return anaExpr.arbreToString();
		else return "L'analyseur � �chou� !";
	}

	public void scannerChoixAMasquer() { }	// pour l'instant on masque juste la méthode héritée...
	public boolean choixIncoherentCoche() { return false; }

}
