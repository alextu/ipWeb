/*
 * Cr�� le 28 sept. 2006
 *
 * TODO Pour changer le mod�le de ce fichier g�n�r�, allez � :
 * Fen�tre - Pr�f�rences - Java - Style de code - Mod�les de code
 */


import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

/**
 * @author olive
 *
 * reprise du projet Profil... un peu de m�nage est requis !!!
 * 
 * 
 **/
public class Compte extends Object {
	protected EOCompte cpt;
	protected Application criApp = (Application)Application.application();	// Pointeur pour acc�der 
	protected Session maSession;

	// Charger effectivement les infos depuis l'EOModel, en fonction du login...
	public Compte(Session sess, String login) {
		maSession = sess;
		cpt = compteForLogin(login);
	}

	// Charger effectivement les infos depuis l'EOModel, en fonction du numeroEtud...
	public Compte(Session sess, Integer numeroEtud) {
		maSession = sess;
		cpt = compteForEtud(numeroEtud);
	}

	//	 Charger effectivement les infos depuis l'EOModel, en fonction de l'email...
	public Compte(Session sess,String email, String domaine) {
		maSession = sess;
		cpt = compteForEmail(email,domaine);
	}

	public EOCompte compte() {
		return cpt;
	}

	private EOEditingContext localEContext(){
		return maSession.defaultEditingContext();
	}

	public String motDePasse() {
		if (cpt != null) return cpt.cptPasswd();
		else return null;
	}

	public Number persId() {
		if (cpt == null) return null;
		EORepartCompte rptc = (EORepartCompte)cpt.toRptCompte().lastObject();
		return  rptc.toIndividuUlr().persId();

//		return null;	// Il faudrait plut�t exploiter les infos de LRUserInfo...

	}

	public NSTimestamp dateNaissInd() {
		if (cpt == null) return null;
		EORepartCompte rptc = (EORepartCompte)cpt.toRptCompte().lastObject();
		return  rptc.toIndividuUlr().dNaissance();
	}


	public String login() {
		if (cpt == null) return null;
		return cpt.cptLogin();
	}

	private EOCompte compteForEtud(Integer numeroEtud) {
		// rechercher le compte associ� � ce numero...
		NSArray resultats= EOUtilities.objectsMatchingKeyAndValue(localEContext(),
				"VCompteEtudiant", "etudNumero", numeroEtud);
		if  (resultats != null && resultats.count()>0) {
			String login = (String)((EOGenericRecord)resultats.objectAtIndex(0)).valueForKey("cptLogin");
			return compteForLogin(login);
		}
		else return null; 
	}



	private EOCompte compteForLogin(String login){
		NSMutableArray sort = new NSMutableArray();
		sort.addObject(EOSortOrdering.sortOrderingWithKey("vlans_priorite",EOSortOrdering.CompareDescending));
		NSMutableArray args = new NSMutableArray();
		NSArray comptes;

		args.addObject(EOQualifier.qualifierWithQualifierFormat("cptLogin caseInsensitiveLike %@",new NSArray(login)));
//		args.addObject(EOQualifier.qualifierWithQualifierFormat("toRptCompte.toIndividuUlr.temValide = %@",new NSArray("O")));
		comptes = criApp.dataBus().fetchArray(localEContext(),"IpwCompte",new EOAndQualifier(args),sort);

		System.out.println(comptes.toString());
		if(comptes.count()==0)
		{
			System.out.println("Aucun compte trouvé !!!");
			return null;
		}
		return (EOCompte)comptes.lastObject();
	}

	private EOCompte compteForEmail(String email,String domaine){
		NSMutableArray sort = new NSMutableArray();
		sort.addObject(EOSortOrdering.sortOrderingWithKey("vlans_priorite",EOSortOrdering.CompareDescending));
		NSMutableArray args = new NSMutableArray();
//		NSMutableArray args2 = new NSMutableArray();
		NSArray comptes;

//		args.addObject(EOQualifier.qualifierWithQualifierFormat("toFPersonneAlias.alias caseInsensitiveLike %@",new NSArray(email+"@"+domaine)));
		args.addObject(EOQualifier.qualifierWithQualifierFormat("cptEmail caseInsensitiveLike %@",new NSArray(email)));
		args.addObject(EOQualifier.qualifierWithQualifierFormat("cptDomaine caseInsensitiveLike %@",new NSArray(domaine)));       
//		args.addObject(new EOAndQualifier(args2));

		comptes = criApp.dataBus().fetchArray(localEContext(),"IpwCompte",new EOAndQualifier(args),sort);

		System.out.println(comptes.toString());
		if(comptes.count()==0)
		{
			System.out.println("Aucun compte trouvé !!!");
			return null;
		}
		return (EOCompte)comptes.lastObject();
	}

}