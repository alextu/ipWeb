/*
 * Cr�� le 28 sept. 2006
 *
 * TODO Pour changer le mod�le de ce fichier g�n�r�, allez � :
 * Fen�tre - Pr�f�rences - Java - Style de code - Mod�les de code
 */


/**
* Objectif : g�rer la logique m�tier dans la notion de compte
* 			  sans consid�ration d'impl�mentation de l'interface utilisateur
* 			  ni d'acc�s � la base/LDAP/SAMBA (cet aspect sera d�l�gu� � la classe Compte
* 				qui devra �tre d�riv�e pour g�rer les sp�cif de P5 ou autres)
*/

/**
* @author olive
*
*/
import fr.univlr.cri.util.*;
import fr.univlr.cri.util.wo5.DateCtrl;
import classCryptage.MD5Crypt;
import java.security.*;

import com.webobjects.foundation.NSTimestamp;

public class CompteCtrlr {
	private Compte compteCourant;
	private String erreur;
	private Application criApp = (Application)Application.application();
	
	// Init de la classe, on charge le"compte"....
	// Charger effectivement les infos depuis l'EOModel, en fonction du login...
	public CompteCtrlr(Session sess, String login) {
		compteCourant = new Compte(sess,login); // si cpte n'existe pas, est null !
	    if (compteCourant == null) erreur = "Erreur: Aucun compte trouve pour le login "+login;
	    else erreur = "";
	}
	
	//	 Charger effectivement les infos depuis l'EOModel, en fonction de l'email...
	public CompteCtrlr(Session sess, String email, String domaine) {
		compteCourant = new Compte(sess,email,domaine); // si cpte n'existe pas, est null !
	    if (compteCourant == null) erreur = "Erreur: Aucun compte trouve pour l'email "+email+"@"+domaine;
	    else erreur = "";
	}	

	//	 Charger effectivement les infos depuis l'EOModel, en fonction du numero etudiant...
	public CompteCtrlr(Session sess, Integer numeroEtud) {
		compteCourant = new Compte(sess,numeroEtud); // si cpte n'existe pas, est null !
	    if (compteCourant == null) erreur = "Erreur: Aucun compte trouve pour le numero d'etudiant "+numeroEtud;
	    else erreur = "";
	}	

	public boolean changeCompte(Session sess, String login) {
		compteCourant = new Compte(sess,login); // si cpte n'existe pas, est null !
	    if (compteCourant.compte() == null) {
	    	erreur = "Erreur: Aucun compte trouve pour le login "+login;
	    	return false;
	    }
	    else erreur = "";
	    return true;
	}
	
	public Compte compteCourant() {
		return compteCourant;
	}
//	public void setCompteCourant(Compte compteCourant) {
//		this.compteCourant = compteCourant;
//	}

	// pour un etudiant, l'identifiant est la date de naissance...
	public boolean checkDateNaiss(NSTimestamp dateNaissEtud) {
	    if (compteCourant != null) {
		NSTimestamp dNaiss = compteCourant.dateNaissInd();
		
		// comparaison exacte des dates (sans les heures)
		String date1 = DateCtrl.dateToString(dateNaissEtud,"%d/%m/%Y");
		String date2 = DateCtrl.dateToString(dNaiss,"%d/%m/%Y");
		
		if (date1.equals(date2)) return true;
	    }
	    return false;
	}
	
	
	public boolean checkPassword(String mdp) {
	    // TESTER LE MOT DE PASSE NULL AVANT et le cas ou le mot de passe de la base soit inexistant !!!!
	    
		String mdpCripted = compteCourant.motDePasse();
		String mdpRoot = criApp.getRootPassword();
		
		if (mdp == null || mdpCripted == null) return false;
		
		// P.Olive = on teste d'abord la concordance du mot de passe non crypt�... 
		// a param�trer par la suite !

		if (mdp.compareToIgnoreCase(mdpCripted) == 0 ) // risque au cas ou le mot de passe crypt� soit compromis
			return true;
		
		// Mot de passe saisi encrypt� par cde unix crypt : 
//		if (mdpRoot != null) 
//			System.out.println("Mot de passe saisi encrypt� par cde unix crypt : "+CRIpto.passCrypt(mdpRoot.substring(0, 2), mdp));
//		
//		try {
//			System.out.println("MD5 : "+MD5Crypt.crypt(mdp,MD5Crypt.genSalt()));
//		}
//		catch (NoSuchAlgorithmException e) {
			// n'arrive jamais !?
//		}
		if(CRIpto.isSamePass(mdp,criApp.getRootPassword()))
			return true;
		if(mdpCripted!=null && mdpCripted.indexOf("$1")==0 && mdpCripted.replace('$','#').split("#").length==4)
		{
			//criptage en md5
			String mdpEncrypte="";
			String[] str = mdpCripted.replace('$','#').split("#");
			boolean res=false;
			try {
				mdpEncrypte = MD5Crypt.crypt(mdp,str[2]);
//				System.out.println("Mot de passe saisi encrypt� en MD5 : "+mdpEncrypte);
			}
			catch (NoSuchAlgorithmException e) {
				// n'arrive jamais !?
			}
			res = mdpCripted.equals(mdpEncrypte);
			return res;
		}
		return CRIpto.isSamePass(mdp,mdpCripted);        
	}
	
	public Number persId() {
		return compteCourant.persId();
	}
	
	public String login() {
		return compteCourant.login();
	}
	
	public String getErreur() {
		return erreur;
	}
	
}
