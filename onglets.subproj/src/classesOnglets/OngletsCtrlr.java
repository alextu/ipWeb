/*
 * Cr�� le 2 juin 2006 pour Profil : 
 * TODO � adapter completement !
 *
 * Objectif : cette classe travaille avec MenuCtrlr
 * elle lui fournit la liste des onglets � repr�senter (titres et ref. externes)
 * en fonction des droits fetch�s dans la base... 
 * 
 * C'est �galement le point principal pour la validation des droits... 
 *  Objectif2 : G�rer les droits de ScolPeda pour le personnel avec droit connect� actuellement
 * 				 
 * rem : pourrait �tre externe et �tre r�f�renc�e plut�t par P
 */
/**
 * @author olive
 *
 */
import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;

public class OngletsCtrlr {

	private Session maSession;
	protected Application criApp = (Application)Application.application();	// Pointeur pour acc�der 
	private Application monApp;

	private NSArray listeModules;		// les EOGenericRecord de la table IPW_MODULES
	private NSMutableArray lesModules; 
	private NSArray listeOptions;	// liste des menus...

	// lien entre constantes pour désigner les options de menu dans le code et la table DROITS_PROFIL 
	// !!! A synchroniser avec champ IPW_MODULES.IPM_CODE
//	public static final String OM_LISTEDIPL="LISTEDIPL"; 			// liste des diplomes avec nbre inscrits, nbre IP d�marr�e, termin�e
//	public static final String OM_INSCRITSDIPL="INSCRITSDIPL";		// les inscrits � un dipl�me avec leur statut
//	public static final String OM_PARAMDIPL="PARAMDIPL";			// le param�trage du diplome (commentaires, r�gles, dates...)
//	public static final String OM_SIMULIP="SIMULIP";				// test du param�trage (simulation d'inscription)
//	public static final String OM_ECTS_PAR_EC="ECTS_PAR_EC";		// ens.: renseign� la fiche ECTS d'un EC (recherche par EC directe)
//	public static final String OM_ECTS_PAR_DIPL="ECTS_PAR_DIPL";	// ens.: renseign� la fiche ECTS d'un EC (recherche par diplome)
//	public static final String OM_SERVICES="SERVICES";				// ens.: consulter son services dans les diff�rents EC...	
//	public static final String OM_LOGS_IP="LOGS_IP";				// pouvoir consulter l'historique des logs d'un �tudiant !	
//	public static final String OM_IP_ETUD="IP_ETUD";				// voir le d�tail des IP prises par un �tudiant donn� (+possib. chgt)
//	public static final String OM_COORD_ETUD="COORD_ETUD";			// consulter les infos sur un �tudiant donn�e 
//	public static final String OM_INSCRITSEC="INSCRITSEC";			// ens/scol.: liste des inscrits � un EC donn�

	private IndividuCtrlr indCtl;
	private boolean droitsDansScolPeda;
	private NSDictionary dictDroitsScolPeda;
	private String monLogin;
//	private Integer dlogKey;

	/**
	 * droits pour le type d'utilisateur auquel l'utilisateur en cours appartient
	 * (renseigné depuis OngletCtrlr !)
	 */
	protected NSMutableDictionary droitsTypeUtilisateur;
	
	protected NSArray droitsScolPeda;	// si est un personnel, liste des diplomes avec droits pour l'année en cours

	public OngletsCtrlr(Session sess,IndividuCtrlr ind,String login) {
		maSession = sess;
		monApp = sess.monApp;
		indCtl = ind;
		monLogin = login;
		listeOptions = null;
		droitsTypeUtilisateur = new NSMutableDictionary();
		initDroitsMenu();

//		s'enregistrer pour les notifs (chgt d'année en cours)
		NSNotificationCenter.defaultCenter().addObserver(this,    // on doit me prévenir moi-même !
				new NSSelector("initDroitsScolPeda",                           // via cette méthode
						new Class [] {NSNotification.class}),               // argument obligatoire !!!
						"chgtAnneeEnCours",                                   // la signature de la notif qui me plait
						maSession);                    // instance de celui qui la poste !


	}

	//--- Gestion de l'affichage des options de menus en fonction des droits
	//	  liés au type d'utilisateur connecté... 
	//    appelé depuis classe Main
	private void initDroitsMenu() {
		lesModules = new NSMutableArray();
		fetchModules();
//		dlogKey = null;

		if (indCtl.estUnVacataire()) fetchDroits("ipVac");	// un enseignant ne peut être aussi vacataire
		if (indCtl.estUnPersonnel()) fetchDroits("ipPers");
		if (indCtl.estUnEnseignant()) fetchDroits("ipEns"); // un personnel autorisé peut être enseignant
		// fetch éventuel des droits de ScolPeda si personnel autorisé.
		if (indCtl.estUnPersonnel() || indCtl.estUnEnseignant()) fetchDroitsScolPeda(monLogin);

		creerListeModule();
	}

	// Raffraichissement des droits au changement d'année...
	public void initDroitsScolPeda(NSNotification laNotif){
		fetchDroitsScolPeda(monLogin);
	} 
	
	// Objectif : lire au lancement de l'appli les droits des différents type d'utilisateurs
	// 				s'en servir pour calculer les menus accessibles par chaque type...
	private void fetchDroits(String typeInd) {		
	    EOEditingContext ec = maSession.defaultEditingContext();
	    NSArray bTypeDroits = new NSArray(new Object[] {"*"});
	    String leWhere = typeInd+" caseInsensitiveLike %@";

	    EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(leWhere,bTypeDroits );
	    NSArray tmp = monApp.dataBus().fetchArray(ec,"IpwDroits",qualifier,null);
	    // stocke les droits pour ce type d'utilisateur...
	    setDroitsTypeUtilisateur(tmp,typeInd);

	    // A pr�sent : voir quels menus sont vis�s... stocker 
	    java.util.Enumeration enumerator = tmp.objectEnumerator();
	    while (enumerator.hasMoreElements()) {
	    	EOGenericRecord droit = (EOGenericRecord)enumerator.nextElement();
	    	EOGenericRecord module = (EOGenericRecord)droit.valueForKey("ipwModules");
	    	NSLog.out.appendln("Autre fetch unitaire d'un module rataché à un droit");

	    	if (lesModules.indexOfObject(module) == NSArray.NotFound) {
	    		lesModules.addObject(module);
	    	}
	    }
	}


	/**
	 * Appelé pour stoker les droits pour ce type d'utilisateur
	 */
	public void setDroitsTypeUtilisateur(NSArray lesDroits, String typeUser) {
		// Construction du dictionnaire de droits pour ce type d'utilisateur...
		
		java.util.Enumeration enumerator = lesDroits.objectEnumerator();
		while (enumerator.hasMoreElements()) {
			EOGenericRecord droit = (EOGenericRecord)enumerator.nextElement();
			String keyFct = ((String)droit.valueForKey("ipCodeFonction")).toUpperCase();
			
			// r�gle : on ne stocke qu'un seul droit par clef... mais en prenant le droit maxi !
			Droit droitStocke = (Droit)droitsTypeUtilisateur.objectForKey(keyFct);
			if (droitStocke == null) {	// Une nouvelle cl�e, on la stocke d'office..
				droitStocke = new Droit(maSession,droit,typeUser,keyFct);
				droitsTypeUtilisateur.setObjectForKey(droitStocke,keyFct);
			}
			else 
				droitStocke.majDroit(droit,typeUser);
		}
	}
	
	private void fetchDroitsScolPeda(String login) {
		// On est un personnel avec droits sur la gestion d'un ou +ieurs diplomes...
		// *** MODIF PO 21-01-2011 : Gestion "dynaminque" ajoutée à la gestion statique 
		//    (modif de la vue sous-jacente et de la clé de recherche : cptLogin remplace dlogKey)		
		
		droitsDansScolPeda = true;
		
		EOEditingContext ec = maSession.defaultEditingContext();
		// fetch droits login...
		
		NSArray bindings = new NSArray(new Object[] {
				login,
				new Integer(maSession.getAnneeEnCours())});		
		EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat("cptLogin = %@ and fannKey = %@)",bindings);
		droitsScolPeda = criApp.dataBus().fetchArray(ec,"ScolDroitDiplome",qualifier,null);
		
		// organise les droits pour recherche rapide
		// si des formations ont bien été récupérées...
		if (droitsScolPeda != null && droitsScolPeda.count()>0) {
			NSMutableDictionary dictDroits = new NSMutableDictionary();
			java.util.Enumeration enumerator = droitsScolPeda.objectEnumerator();
			while (enumerator.hasMoreElements()) {
				EOGenericRecord ligDroit = (EOGenericRecord)enumerator.nextElement();
				String cle = (Integer)ligDroit.valueForKey("fspnKey")+"-"+
								(Integer)ligDroit.valueForKey("fhabNiveau");
				dictDroits.setObjectForKey(ligDroit,cle);
				}
			dictDroitsScolPeda = dictDroits.immutableClone();
		}
		else droitsDansScolPeda = false;	// finallement ce n'était pas un personnel avec droits pour cette année !
		
/*		
		EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat("dlogLogin = %@)",new NSArray(login) );
		NSArray tmp = criApp.dataBus().fetchArray(ec,"ScolDroitLogin",qualifier,null);
		if(tmp!=null && tmp.count()>0) {
			// fetch droits diplomes pour l'ann�e en cours !
			EOGenericRecord scolDroitLogin = (EOGenericRecord)tmp.objectAtIndex(0);
			dlogKey = (Integer)scolDroitLogin.valueForKey("dlogKey");
			NSArray bindings = new NSArray(new Object[] {
					dlogKey,
					new Integer(maSession.getAnneeEnCours())});
			qualifier = EOQualifier.qualifierWithQualifierFormat("dlogKey = %@ and fannKey = %@)",bindings);
			droitsScolPeda = criApp.dataBus().fetchArray(ec,"ScolDroitDiplome",qualifier,null);
			
			// organise les droits pour recherche rapide
			// si des formations ont bien été récupérées...
			if (droitsScolPeda != null && droitsScolPeda.count()>0) {
				NSMutableDictionary dictDroits = new NSMutableDictionary();
				java.util.Enumeration enumerator = droitsScolPeda.objectEnumerator();
				while (enumerator.hasMoreElements()) {
					EOGenericRecord ligDroit = (EOGenericRecord)enumerator.nextElement();
					String cle = (Integer)ligDroit.valueForKey("fspnKey")+"-"+
									(Integer)ligDroit.valueForKey("fhabNiveau");
					dictDroits.setObjectForKey(ligDroit,cle);
					}
				dictDroitsScolPeda = dictDroits.immutableClone();
			}
			else droitsDansScolPeda = false;	// finallement ce n'�tait pas un personnel avec droits pour cette année !
		}
		else droitsDansScolPeda = false;	// finallement ce n'�tait pas un personnel avec droits !
*/
	
	}
	
	// accés de l'extérieur à ce dico
	public NSDictionary dictDroitsScolPeda() {
		if (droitsDansScolPeda) return dictDroitsScolPeda;
		else return null;
	}
	
	public boolean droitsDansScolPeda() {
		return droitsDansScolPeda; 
	}
	
	// Supprimé depuis la version 2.4...
	/*
	public Integer dlogKey() {
		return dlogKey;
	}
	*/
	
	// Version 2.4 : on introduit un élémnt dynamique dans la gestion des droits...
	public String cptLogin() {
		return monLogin;
	}
	

	// on veut savoir pour cette fonction ET ce diplome/annee quels sont les droits du user...
	public int getDroitsTypeUtilisateur(String laFonction,String diplAnnee) {
		int valDroit = Droit.DROIT_AUCUN;
		laFonction = laFonction.toUpperCase();
		Droit leDroit = (Droit)droitsTypeUtilisateur.objectForKey(laFonction);
		EOGenericRecord eoDiplSPD = (EOGenericRecord)dictDroitsScolPeda.objectForKey(diplAnnee);
		if (leDroit != null && eoDiplSPD!=null) valDroit = leDroit.valeurDroit(eoDiplSPD);
		return valDroit;
	}
	

	// fetcher les données sur les différents modules valides depuis le user IP_WEB
	private void fetchModules() {
		EOEditingContext ec = maSession.defaultEditingContext();
		NSArray bTypeDroits = new NSArray(new Object[] {"O"});
		String leWhere = "ipmValide = %@";
		EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(leWhere,bTypeDroits );

		EOSortOrdering ordre = EOSortOrdering.sortOrderingWithKey("ipmOrdre",
    			EOSortOrdering.CompareAscending);    	
		NSArray sortOrdering = new NSArray(new Object[] {ordre});

		NSArray tmp = monApp.dataBus().fetchArray(ec,"IpwModules",qualifier,sortOrdering);
		
		if (tmp ==null) tmp = new NSArray();
		listeModules = tmp;
	}
	
	
	// Pièce maitresse !
	private void creerListeModule() {
		OptionMenu omPrec = null;
		OptionMenu omEnCours;
		NSMutableArray tmpOption = new NSMutableArray();
		int nbModules = lesModules.count();
		int ndx = 0;
		
		java.util.Enumeration enumerator = listeModules.objectEnumerator();
		while (enumerator.hasMoreElements()) {
			EOGenericRecord module = (EOGenericRecord)enumerator.nextElement();
			String cle = (String)module.valueForKey("ipmCode");
			String valCh = (String)module.valueForKey("ipmTitreMenu");
			if (lesModules.indexOfObject(module) != NSArray.NotFound) {
				omEnCours = new OptionMenu(cle,valCh,omPrec);
				if (omPrec != null) omPrec.setOmSuivant(omEnCours);
				omPrec = omEnCours;
				tmpOption.addObject(omEnCours);
			}
		}
		omPrec.setOmSuivant(null);
		listeOptions = (NSArray)tmpOption;
	}

	// renvoyer la liste des onglets � ouvrir, une fois construite...
	public NSArray listeOnglets() {
		return listeOptions;
	}

	
}
