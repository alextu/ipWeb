import java.util.Enumeration;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSComparator;
import com.webobjects.foundation.NSComparator.ComparisonException;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;

/**
 * @author olive
 * Créé le 2 oct. 2006
 *
 *	Objectif : gestion de l'UE pour laquelle on veut gérer les IP...
 */
public class InscUeCtrlr {
	
	private static String COUL_TEXTE_PASDECHOIX = "UeSansChoix"; // noir quand aucun choix d'ECTS à faire !	
	private static String COUL_TEXTE_MANQUE = "UeIncomplete"; // rouge quand il manque des points ECTS
	private static String COUL_TEXTE_TROP = "UeTropEc"; // bleu quand il y a trop de points ECTS
	private static String COUL_TEXTE_OK = "UeComplete"; // vert quand il y a ce qu'il faut de points ECTS
	
	private Session maSession;
	private EOGenericRecord monUe;	// ref à la vue "scolMaqSemestreUE"
	private EOGenericRecord inscUe;	// ref à la vue "scolInscriptionUe" : l'inscription réelle à cette UE	
	private NSArray	listeEc;		// les EC de la maquette pour cette UE (enreg. de "scolMaqUeEc" qui vont bien...)
	
	private NSArray	listeEcCt;		// les ctrlr d'EC pour cette UE et cet étudiant... ensemble des EC
	private NSArray listeEcIpCt;	// liste des ctrlr d'EC pour les EC avec des IP (dans ScolPeda ou IP_WEB)
	
	private InscSemestreCtrlr inscSemCt;	// ref vers le semestre choisi...
	private double cumulEctsUeChoix, cumulEctsUeBloques, pointsUe;
	
	private NSMutableArray valeurChoixCoche;	// liste décroissante des ECTS des EC cochés
	private boolean autoriseDepasse;			// valeur du param de config associé
	
	private double oldCumulEctsUeChoix;
	private boolean comparer = false;
	
	private int numOrdreUe, etatRetour = 0, ecEnPlus;	// n� d'ordre de l'UE dans la liste des UE de ce semestre...
	private int nbreEcAChoix,oldNbreEcAChoix;			// nombre d'EC � choix ratach�es dans la maquette � cette UE
	
	private String mueKey;		// le code interne de l'UE (pour l'acc�s via une Ancre) + msemKey
	private String libUEsansChoix, remUE;
	private Integer msemKey;
	
	private boolean ueDetaillee;	// si vrai on affiche le détail des EC pour cette UE...
	private boolean ueAvecEcFacultatif; 	// Si vrai, cette UE contient au moins un EC facultatif (à 0 ECTS)
	private boolean aLeFocus;		// vrai si on a demander des modifs pour CETTE ue...
	private boolean cumulEctsTemp;	// vrai si on fait un cumul bidon...
	private boolean ueContientEcEnErreur;
	private boolean ueSansChoix;	// vrai si UE déja validée OU (que des UE obligatoires ou avec IP)
	private boolean ueDejaValidee = false; // vrai si l'UE a une dispense...
	
	private boolean modifPossible;	// Vrai si pour ce semestre on est encore dans les clous pour les dates !
	private boolean semestreIntegre; // Vrai si le semestre a déjà été intégré ! 

	private boolean modifLancee;
	
	private String chpHiddenUeDetaillee;	
	/**
	 * @throws Exception
	 * 
	 */
	public InscUeCtrlr(Session sess,EOGenericRecord ue,InscSemestreCtrlr inscSem,boolean modifPossible, boolean semestreIntegre) {
		super();
		
		maSession = sess;
		monUe = ue;
		mueKey = ((Integer)monUe.valueForKey("mueKey")).toString();
		msemKey = (Integer)monUe.valueForKey("msemKey");
		inscSemCt = inscSem;
		pointsUe = ((Number)monUe.valueForKey("muePoints")).doubleValue();
		numOrdreUe = ((Number)monUe.valueForKey("mrueOrdre")).intValue();
		
		this.modifPossible = modifPossible;
		this.semestreIntegre = semestreIntegre;
		
		chargeInscUe();	// charger l'inscription réelle à cette UE
		
		ueDetaillee = false;	// On commence avec toutes les UE masqu�es
		aLeFocus= false;		// et aucune n'a le focus
		
		nbreEcAChoix = -1;
		chargerEc();			// chargement des EC associées à cette UE...
		
		cumulEctsTemp=false;
		autoriseDepasse = !maSession.interrogeParamConfig("LIMITE_ECTS_UE");
		
		modifLancee = true;
	}

//	 chargement des EC associ�es � cette UE...en fct� du MUE_KEY et ordonn� sur mrecOrdre / 
	public void chargerEc() {
//	    NSArray bindings = new NSArray(new Object[] {(Integer)monUe.valueForKey("mueKey")});

//	    EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
//	    "mueKey = %@ ", bindings);

//	    EOSortOrdering rueOrdre = EOSortOrdering.sortOrderingWithKey("mrecOrdre",
//	    EOSortOrdering.CompareAscending);

//	    NSArray sortOrderings = new NSArray(new Object[] {rueOrdre});

//	    EOFetchSpecification fetchSpec = new EOFetchSpecification("ScolMaqUeEc",qualifier, sortOrderings);

//	    EOEditingContext ec = maSession.defaultEditingContext();

//	    listeEc = ec.objectsWithFetchSpecification(fetchSpec);
		
		// PO 2009 = On doit afficher les EC à masquer mais pour lesquelles il y a une IP dans ScolPédagogie...
		// Sinon on force des choix inutiles !!!
		
		modifLancee = true;
		
		if (modifPossible) 
			listeEc = maSession.monApp.chargerDesEc((Integer)monUe.valueForKey("mueKey"));
		else 
			listeEc = maSession.monApp.chargerDesEcSansFiltre((Integer)monUe.valueForKey("mueKey"));
	    
		// si des EC ont bien été récupérées... générer les objets Ctrlr qui vont les gérer !
		if (listeEc != null && listeEc.count()>0) {
			Integer mrueKey = (Integer)monUe.valueForKey("mrueKey");
			NSMutableArray lesEcCt = new NSMutableArray();
			java.util.Enumeration enumerator = listeEc.objectEnumerator();
			while (enumerator.hasMoreElements()) {
				EOGenericRecord ecMaq = (EOGenericRecord)enumerator.nextElement();
				if (modifPossible && ((String)ecMaq.valueForKey("aMasquer")).equalsIgnoreCase("O")) {
					// vérifier si IP dessus...
					if (inscSemCt.ipSurEc((Integer)ecMaq.valueForKey("mrecKey"))) {
						InscEcCtrlr ecCt = new InscEcCtrlr(ecMaq,this,maSession,msemKey,mrueKey);
						lesEcCt.addObject(ecCt);
					}
				}
				else {
					InscEcCtrlr ecCt = new InscEcCtrlr(ecMaq,this,maSession,msemKey,mrueKey);
					lesEcCt.addObject(ecCt);
				}
		}
		listeEcCt = new NSArray(lesEcCt);	// liste de ctrlr vers tous les EC accroch�s � l'UE en cours

		// terminer la cr�ation des objets ctrlr d'EC...
		inscSemCt.cataloguer(listeEcCt);

		// enfin �tablir la liste des EC "choisis" � faire apparaitre � l'�cran !
		// on en profite pour virer celles qui ne sont pas correctes (celles dont le MEC_CODE = "DIV%" et qui n'ont pas �t� acquises)
		extraireListeEcIp();
	    }   	

	}
	
	// Appeler pour reforcer le calcul de "etatRetour", inidiquant l'état d'une UE à choix...
	public void ddeRecalculChoix() {
		modifLancee = true;
	}
	
	// forcer le recalcul (en cas d'annulation d'une modif d'UE via l'interface)
	public void recalculChoix() {
		modifLancee = true;
		cumulCasesCochee();
		compareCumulEcts();
	}
	
	// Methode qui retourne TRUE si cette UE fait ref. au msemKey passe en parametre et
	// soit qu'elle ait ete acquise prededemment, 
	// soit qu'elle possede des EC pour lesquelles il y a des IP de ScolPedagogie avec une dispense ! 
	public boolean ueAyantDispense(Integer leMsemKey) {
	    boolean res = false;
	    // l'UE a pour parent le semestre qui nous interesse...
	    if (msemKey.compareTo(leMsemKey)==0) {
		// si l'UE est deje validee, alors on a notre reponse :-))
		res = ueDejaValidee;
		// faire le tour des EC ayant des IP...
		java.util.Enumeration e = listeEcIpCt.objectEnumerator();
		while (!res && e.hasMoreElements()) {
		    InscEcCtrlr ecCt = (InscEcCtrlr)e.nextElement();
		    if (ecCt.isEcBloque() && (ecCt.isEcDejaValide() || ecCt.isEcAvecNote()) )
			res = true;
		}
	    }
	    return res;
	}
	
	public InscSemestreCtrlr getInscSemCt() { return inscSemCt; }
	
	public String nbrePointsUe()
	{
	    if (pointsUe > 0.0) return (int)pointsUe + " points ECTS";
	    else return "pas de points ECTS";
	}

	//	 charger l'inscription réelle à cette UE
	private void chargeInscUe() {
		ueSansChoix = false;
		Integer mrueKey = (Integer)monUe.valueForKey("mrueKey");
		NSArray bindings = new NSArray(new Object[] {inscSemCt.getIdiplNumero(),inscSemCt.getSemImpair(),mrueKey});
		
		EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
				"idiplNumero = %@ and imrueSemestre = %@ and mrueKey = %@", bindings);
		
		EOFetchSpecification fetchSpec = new EOFetchSpecification("ScolInscriptionUe",qualifier, null);
		fetchSpec.setRefreshesRefetchedObjects(true);	// fait en sorte de refetcher des EOS déjà fetchés si besoin
		EOEditingContext ec = maSession.defaultEditingContext();
		
		NSArray res = ec.objectsWithFetchSpecification(fetchSpec);
		// le résultat doit être unique ou bien nul (pas encore d'IP pour une UE sans EC obligatoires !)
		
		remUE = "";
		if (res == null || res.count()==0) {
			inscUe = null;
		}
		else {
//			if (res.count()>1) {
//				throw new Exception("Pb avec l'UE "+mrueKey+" de l'�tudiant");
//			}
			inscUe = (EOGenericRecord)res.objectAtIndex(0);
			
			int imrueDispense = ((Integer)inscUe.valueForKey("imrueDispense")).intValue();
			if (dispenseValide(imrueDispense)) {
				libUEsansChoix = "UE déjà validée";
				ueDejaValidee = true;
				if (dispenseAvecNote(imrueDispense)) {
					double imrueSession1 = maSession.convChpDouble(inscUe,"imrueSession1");
					double imrueSession2 = maSession.convChpDouble(inscUe,"imrueSession2");
					if (imrueSession2>imrueSession1) imrueSession1=imrueSession2;
					libUEsansChoix += " ("+imrueSession1+"/20)";
					remUE = " "+libUEsansChoix;
				}
				ueSansChoix = true;
			}
		}
	}
	
	// Dire si un code dispense d'UE revient à ce que l'UE soit déjà validée...
	private boolean dispenseValide(int codeDispense) {
		if ((codeDispense >=4 && codeDispense <=7) || (codeDispense >=12 && codeDispense <=15))
			return true;
		return false;
	}
	
	// Dire si un code dispense d'UE revient à ce que l'UE soit déjà validée AVEC UNE NOTE...
	private boolean dispenseAvecNote(int codeDispense) {
		if (codeDispense ==4 || codeDispense ==12 || codeDispense ==14)
			return true;
		return false;
	}
	
	
	public String getRemUe() { return remUE; 	}
	
	public boolean ueDetaillee() { return ueDetaillee; }
	
	public void masquerDetails() { ueDetaillee = false; }
	public void afficherDetails() { ueDetaillee = true; }
	
	// Appel par les EC dépendant de cette UE, pour vérifier si le semestre a déjà été intégré ?!
	public boolean semestreIntegre() {
		return semestreIntegre;
	}
	
//	public void donnerFocus() { aLeFocus = true; }
//	public void eneleverFocus() { aLeFocus = false; }
//	public boolean aLeFocus() { return aLeFocus; }
	
	// contenu en E/S du champ Hidden pour communiquer avec le Javascript
	// afin d'avoir en retour le choix de l'utilisateur (validation ou dde modif)
	public String getChpHiddenUeDetaillee() {
		if (ueDetaillee) return "O";
		else return "N";
	}
	
	public void setChpHiddenUeDetaillee(String nouvVal) {
		if (nouvVal != null && nouvVal.equalsIgnoreCase("O")) 
			ueDetaillee = true;
		else ueDetaillee = false;
	}
	

    public String nameChpHiddenUED() {
        return "H"+getCodeUe();
    }

	
	public String getNomUe() {
		return (String)monUe.valueForKey("mueLibelle");
	}
	
	public String getCodeUe() {
		return (String)monUe.valueForKey("mueCode");
	}
	
	public String getUeKey() {
		return mueKey;
	}
	
	
	public String getPointsUe() {
		return maSession.formatteDouble(new Double(pointsUe));
	}
	
	public int getOrdreUe() { return numOrdreUe; }
	
// annule cumul temporaire des cases coch�es (mais pas encore valid�es)
	public void annuleCumulCasesCochee() {
		if (cumulEctsTemp) {
			cumulEctsTemp = false;
			cumulEctsUeChoix = oldCumulEctsUeChoix;
			nbreEcAChoix = oldNbreEcAChoix;
		}
	}

	public boolean cumulEctsTemp() { return cumulEctsTemp; }
	
	public boolean ueEnErreur() { 
		return (compareCumulEcts() > 0); 
		}
	
	public void calculNbreChoix() {
		nbreEcAChoix = 0;
		if (listeEcCt != null && listeEcCt.count()>0) {
			InscEcCtrlr ecCt;
			java.util.Enumeration enumerator = listeEcCt.objectEnumerator();
			while (enumerator.hasMoreElements()) {
				ecCt = (InscEcCtrlr)enumerator.nextElement();
				if (!ecCt.isEcBloque() && !ecCt.ecAmasquer()) nbreEcAChoix++;
			}
			if (nbreEcAChoix==0) {
				libUEsansChoix = "Pas de choix à faire pour cette UE...";
				ueSansChoix = true;
			}
		}
	}

	
	// cumul temporaire des cases coch�es (mais pas encore valid�es)
	// ATTENTION : si modif ici, modifier également extraireListeEcIp()
	public void cumulCasesCochee() {
		
		valeurChoixCoche = new NSMutableArray();
		
		double ptsEc;
		if (cumulEctsTemp == false) {
			cumulEctsTemp = true;
			oldCumulEctsUeChoix = cumulEctsUeChoix;
			oldNbreEcAChoix = nbreEcAChoix;
		}
		cumulEctsUeChoix = 0.0;
		comparer = true;
		nbreEcAChoix = 0;
		if (listeEcCt != null && listeEcCt.count()>0) {
			InscEcCtrlr ecCt;
			java.util.Enumeration enumerator = listeEcCt.objectEnumerator();
			while (enumerator.hasMoreElements()) {
				ecCt = (InscEcCtrlr)enumerator.nextElement();
				// nbre de choix réellement possibles par l'étudiant (exclu les EC à choix déja validés en N-1)
				// et points que cela repr�sente...
				if (!ecCt.isEcBloque() && !ecCt.ecAmasquer()) nbreEcAChoix++;
				if (ecCt.getCaseCochee() && !ecCt.isEcBloque()) {
					ptsEc = ecCt.getPointsECTS();
					valeurChoixCoche.addObject(new Double(ptsEc));
					cumulEctsUeChoix += ptsEc;
				}
			}

		}
		if (valeurChoixCoche != null && valeurChoixCoche.count()>0) {
			try {
				valeurChoixCoche.sortUsingComparator(NSComparator.DescendingNumberComparator);
			}
			catch (ComparisonException e) {
				NSLog.out.appendln("Pb de comparaison des valeurs ECTS dans l'UE...");
			}
		}
	}
	
	// on parcours la liste de toutes les EC et on met de côté celles qui ont des IP !!! (ScolPeda ou IpWeb)
	// on en profite pour faire un cumul des points ECTS des IP de l'UE... 
	//	et trouver l'EC sélectionnée ayant le + petit nbre d'ECTS...
	// ATTENTION : si modif ici, modifier également cumulCasesCochee()
	public void extraireListeEcIp() {
		ueAvecEcFacultatif = false;
		cumulEctsTemp = false;
		double ptsEc;
		cumulEctsUeChoix = 0.0;
		cumulEctsUeBloques = 0.0;
		comparer = true;
		nbreEcAChoix = 0;
		if (listeEcCt != null && listeEcCt.count()>0) {
			// pour enlever de la liste les EC � masquer...(choix impossibles)
			NSMutableArray copieListe = new NSMutableArray(listeEcCt);
			boolean listeEcChange = false; 
			NSMutableArray lesEcIpCt = new NSMutableArray();
			InscEcCtrlr ecCt;
			java.util.Enumeration enumerator = listeEcCt.objectEnumerator();
			while (enumerator.hasMoreElements()) {
				ecCt = (InscEcCtrlr)enumerator.nextElement();
				// nbre de choix réellement possibles par l'étudiant (exclu les EC à choix déja validés en N-1)
				if (!ecCt.isEcBloque() && !ecCt.ecAmasquer()) nbreEcAChoix++;
				if (ecCt.ecFacultatif()) ueAvecEcFacultatif = true;
				if (ecCt.ecAvecIp() && !ecCt.ecAmasquer()) {
					lesEcIpCt.addObject(ecCt);
					ptsEc = ecCt.getPointsECTS();
					if (!ecCt.isEcBloque()) {	// Un EC � choix cochable par user
					    cumulEctsUeChoix += ptsEc;
					}
					else {	// Un EC � choix d�j� bloqu� dans ScolPedagogie
					    cumulEctsUeBloques += ptsEc;
					}
				}
				// pour enlever de la liste les EC � masquer...(choix impossibles)
				if (ecCt.ecAmasquer()) {
					listeEcChange = true;
					copieListe.removeObject(ecCt);
				}
			}
			listeEcIpCt = new NSArray(lesEcIpCt);	// liste de ctrlr vers tous les EC avec IP, accrochés à l'UE en cours
			if (listeEcChange) listeEcCt = (NSArray)copieListe;
		}
	}
	
	// vrai si pas un points ECTS n'est pris en IP pour cette UE
	// MODIF PO 2012 : on modifie cet indicateur pour prendre en compte les EC facultatives qui sont à 0 ECTS !
	// masque le tableau ssi en consult..
	public boolean aucuneIpAuxEc() {
		if ((listeEcIpCt == null || listeEcIpCt.count()==0) && !maSession.modifEnCours()) return true;
		//if ((cumulEctsUeChoix + cumulEctsUeBloques)==0.0 && !maSession.modifEnCours()) return true;
		return false;
	}
	
	public double cumulEctsUe() {
		return cumulEctsUeChoix + cumulEctsUeBloques;
	}
	
	public String stCumulEctsUe() {
		return maSession.formattedouble(cumulEctsUe(),false);
	}
	
	// une UE est complete si la valeur retournee est >= a 0... 
	public int compareCumulEcts() {

		// Algo : soit r = (pointsUe - cumulEctsUeBloques) les points dispo pour les choix...
		// On parcours la liste descendante des ECTS des EC à choix cochés en cumulant ces ECTS
		
		// si on n'autorise aucun dépassement (param .config LIMITE_ECTS_UE = YES)
		// 		- si le cumul dépasse le nbre de points dispo pour les choix (r) ET qu'il reste encore des ECTS non pris en en compte
		//		  alors TROP DE CHOIX COCHES
		//	   - si le cumul n'atteint pas le nbre de points dispo pour les choix (r) ET qu'il n'y a plus d'ECTS à prendre en compte
		//		  alors PAS ASSEZ DE CHOIX COCHES
		//     - sinon NOMBRE DE CHOIX COCHES CORRECT
		
		// sinon, si on autorise le dépassement pour un EC (param .config LIMITE_ECTS_UE = NO)
		// 		- si le cumul dépasse le nbre de points dispo pour les choix (r) ET qu'il reste encore AU MOINS 2 ECTS non pris en en compte
		//		  alors TROP DE CHOIX COCHES
		// (le reste idem)

		// la valeur précédemment calculée (ou initalisée) de "etatRetour" est renvoyé 
		// tq il n'y a pas nécessitée de rafrachir cet état...
		if (!modifLancee || !ueAvecChoix()) return etatRetour;
		modifLancee = false;
		
		if (estFacultative()) {
			etatRetour = 0; 
			return etatRetour;
		}
		
		
		double cumul = 0.0;
		double ects, r = pointsUe - cumulEctsUeBloques;
		int nbChoixCoche, indiceChoix;
		etatRetour = 0; 

		if (valeurChoixCoche != null && valeurChoixCoche.count()>0) {
			nbChoixCoche = valeurChoixCoche.count();
			indiceChoix = 0; ecEnPlus = 0;

			java.util.Enumeration enumerator = valeurChoixCoche.objectEnumerator();
			while (enumerator.hasMoreElements() && etatRetour==0) {
				ects =  ((Double)enumerator.nextElement()).doubleValue();

				if (cumul >= r && ects > 0) {
					if ((!autoriseDepasse) || (autoriseDepasse && (nbChoixCoche-indiceChoix) > 1)){
						etatRetour = 1;
					}
					else if (autoriseDepasse && (nbChoixCoche-indiceChoix) == 1)
						ecEnPlus = 1;
				}
				else {
					cumul += ects;
					indiceChoix++;
				}
			}
			if (etatRetour ==0 && cumul < r) etatRetour = -1;
		}
		else etatRetour = -1; // aucun choix pris, donc pas assez de choix


		/*
		// Ancien Algo : soit r = (pointsUe - cumulEctsUeBloques) les points dispo pour les choix...
		// 	      soit cumulEctsUeChoix = la somme des ECTS des EC à choix cochés
		//		  soit min2EcAvecIp = le 2nd plus petit nbre d'ECTS parmis les ec à choix cochées
		//		  soit minEcAvecIp  = le plus petit nbre d'ECTS parmis les ec à choix cochées	

		// 	on calcule resultat = (cumulEctsUeChoix - r)		
		// 		si 0 <= resultat < minEcAvecIp alors c'est OK
		// 		si resultat < 0 : pas assez d'EC cochées		
		// 		si resultat > min2EcAvecIp : trop d'EC cochées...
		
         System.out.println("compareCumulEcts : pointsUe =" + pointsUe + ", cumulEctsUeBloques = " 
		    + cumulEctsUeBloques + ", cumulEctsUeChoix = " + cumulEctsUeChoix);

		if (r >= 0) {	// des choix sont encore possibles
			if (cumulEctsUeChoix < r) etatRetour = -1;	// pas assez de choix cochés !
			else if ((cumulEctsUeChoix - r) > min2EcAvecIp) {	// on a pris trop d'EC !
				etatRetour = 1;	// les pt des ec choisis par l'étudiant dépassent
			}
		}

		else {		// plus de choix possibles !
			if (cumulEctsUeChoix > 0) {
				etatRetour = 1;	// si un seul choix est fait, c'est déjà trop !
			}
		}
		
		*/
		
		return etatRetour;
	}
	
	// renvoit 1 s'il y a au moins une EC de plus que le strict nécessaire...
	public int ecEnPlus() {
		return ecEnPlus;
		
		/*	    System.out.println("compareCumulEcts : pointsUe =" + pointsUe + ", cumulEctsUeBloques = " 
		    + cumulEctsUeBloques + ", cumulEctsUeChoix = " + cumulEctsUeChoix);
		 
		double r = pointsUe - cumulEctsUeBloques;
		etatRetour = 0;

		if (r > 0) {	// des choix sont encore possibles
			if (cumulEctsUeChoix > r) {	// les pt des ec choisis par l'�tudiant d�passent
				if ((cumulEctsUeChoix - minEcAvecIp) >= r) {	// on a pris trop d'EC !
					etatRetour = 1;
				}
			}
		}
		return etatRetour;
		*/
	}	
	
	
	// indique si l'UE est incompl�te
	public boolean ueIncomplete() {
		return (compareCumulEcts()==-1); 
	}
	
	// vrai si cette UE comporte trop ou trop peu d'ECTS ou bien si elle comporte des choix erron�s !
	public boolean pbChoixPourUe() {
		return (compareCumulEcts()!=0 || ueContientEcEnErreur);
	}
	
	
	// On retourne une info appropriées si besoin (si compareCumulEcts()>0)
	// REM : il faut parfois autoriser l'étudiant à cumuler + d'ects que nécessaires
	// par exemple en Sciences... 
	// PO 2012 : on paramètre ce dernier comportement dans le .config (booléen LIMITE_ECTS_UE)
	public String msgCumulEcts() {
		if (inscSemCt.isSemestreDejaObtenu()) return null;
		switch (compareCumulEcts()) {
		case -1 : {
			String ectsManque = maSession.formattedouble(pointsUe-cumulEctsUe(),false);
			return ("<b>Il manque "+ectsManque+" ECTS pour compléter l'UE !</b>");
		}
		case 1 :
			String enTrop = maSession.formattedouble(cumulEctsUe()-pointsUe,false);
			return "Trop d'EC s&eacute;lectionn&eacute;s ("+enTrop+
			" ECTS en +).<BR><b>D&eacute;cochez des EC... (voir avec la Scolarit&eacute si besoin)</b>";
		default : 
			if (nbreEcAChoix > 0) {
				if (ecEnPlus()==1)
					return "Ok, l'UE est complète... mais vous avez + d'EC que nécessaire (attention, la moyenne de l'UE comptabilisera toutes les EC, avec leur coef.)";
				else { 
						if (estFacultative()) return "Nombre d'EC facultatifs pouvant être choisi : "+ inscSemCt.getNbreEcFacultatifsAChoisir();
						else return "Ok, l'UE est complète.";
				}
			}
//			else return "(Pas de choix � faire pour cette UE...)";
			else return "";
		}
		
	}
	
	// Indique si l'UE est facultative : compte pour 0 ECTS et comporte des EC Facultatives...
	public boolean estFacultative() {
		return (pointsUe == 0.0 && ueAvecEcFacultatif);
	}
	
	// Indique si l'UE est facultative et s'il y a encore possibilité de faire des choix 
	public boolean estFacultativeEtChoixPossible() {
		return (estFacultative() & !plusDEcFacultatifsPouvantEtreChoisis());
	}
	
	public void signaleErreur() {
		ueContientEcEnErreur = true;
	}
	
	public void resetErreur() {
		ueContientEcEnErreur = false;
		// forcer le recalcul au prochain coup des conditions d'erreurs...
		comparer = true;

	}
	
	// on n'affiche pas la zone si l'UE contient un EC en erreur OU si pas de choix) 
	public boolean masqueMsgCumulECTS() {
		return (ueContientEcEnErreur | ueSansChoix | estFacultative());
	}
	
	// version + courte pour affichage dans cadre UE non d�taill�...
	public String msgEctsUE() {
		if (inscSemCt.isSemestreDejaObtenu()) return null;
		if (ueContientEcEnErreur) return "L'UE contient des choix à revérifier.";
		if (ueSansChoix) return libUEsansChoix;
		else {
			switch (compareCumulEcts()) {
			case -1 : {
				if (!inscSemCt.ipPasEncoreOuvertes()) {
					String ectsManque = maSession.formattedouble(pointsUe-cumulEctsUe(),false);
					if (!ectsManque.equals("0")) {
						ectsManque = "Il manque "+ectsManque+" ECTS !";
					} else {
						return "";
					}
					if (aucuneIpAuxEc()) ectsManque = ectsManque+" Aucun choix fait.";
					return ectsManque; 
				}
				else return "";
			}

			case 1 :
				String enTrop = maSession.formattedouble(cumulEctsUe()-pointsUe,false);
				return "Plus d'EC que n&eacute;cessaires ("+enTrop+" ECTS en +)";
			default : 
				if (nbreEcAChoix > 0) {
					if (ecEnPlus()==1)
						return "Ok, UE complète, mais avec + d'EC que nécessaire (moyenne de l'UE = toutes les EC, avec leur coef.)";
					else {
					    if (pointsUe > 0) return "Ok, l'UE est complète.";
					    else {
					    	if (plusDEcFacultatifsPouvantEtreChoisis())
					    		return "Plus aucun EC facultatif ne peut être choisi...";
					    	else return "Nombre d'EC facultatifs pouvant être choisi : "+ inscSemCt.getNbreEcFacultatifsAChoisir();
					    }
					}
				}
				else return "Pas de choix à faire pour cette UE...";
			}
		}
	}

	public boolean plusDEcFacultatifsPouvantEtreChoisis() {
		return (inscSemCt.getNbreEcFacultatifsAChoisir() == 0);
	}	

	// retourne le nbre de choix r�ellement possibles par l'�tudiant (exclu les EC � choix d�ja valid�s en N-1)
	public int nbreChoixUe() {
		return (nbreEcAChoix);
	}
	
	public boolean ueAvecChoix() {
	    return (!ueSansChoix && (nbreEcAChoix > 0));
	}
	
	public String coulTexteCumulEcts() {
		if (ueContientEcEnErreur) return COUL_TEXTE_TROP;
		if (ueSansChoix) return COUL_TEXTE_PASDECHOIX;
		else {
			switch (compareCumulEcts()) {
			case -1 : return COUL_TEXTE_MANQUE;
			case 1 : return COUL_TEXTE_TROP;
			default : 
				if (nbreEcAChoix>0) return COUL_TEXTE_OK;
				else return COUL_TEXTE_PASDECHOIX;
			}
		}
	}
	
	public NSArray getListeEcCt() {
		return listeEcCt;
	}
	public NSArray getListeEcIpCt() {
		return listeEcIpCt;
	}
	
	
// ************** Gestion de l'impression de la fiche IP du semestre via JASPER...
	private Enumeration monEnum;
	private InscEcCtrlr jrEcCt;
	
	public boolean resetBoucle() {
//		System.out.println("reinit liste des EC de l'UE");
	    monEnum = listeEcIpCt.objectEnumerator();
		boolean encoreDesElements = monEnum.hasMoreElements();
		if (encoreDesElements) {
			jrEcCt = (InscEcCtrlr)monEnum.nextElement();
		}
	    return encoreDesElements;

	}
	
	public boolean nextElement() {
//	    System.out.println("==> next EC");
	    boolean encoreDesElements = monEnum.hasMoreElements();
	    if (encoreDesElements) jrEcCt = (InscEcCtrlr)monEnum.nextElement();
	    return encoreDesElements;
	}
	
//	public boolean encoreDesElements() {
//	    return (monEnum.hasMoreElements()); 
//	}

	public Object fetchJRChamp(String jrName) {
	    Object res = null;
	    if (jrEcCt == null) return null;
	    if (jrName.equals("TYPE_EC")) res = jrEcCt.typeEc();
	    else if (jrName.equals("LIB_TYPE_EC")) res = jrEcCt.libTypeEc();
	    else if (jrName.equals("TITRE_EC")) 
		res = jrEcCt.getLibelleLongEc()+" ("+jrEcCt.getNbHCmTdTp()+")";
	    else if (jrName.equals("ECTS_EC")) 
		res = new Double(jrEcCt.getPointsECTS());
	    else if (jrName.equals("REM_EC")) {
		res = jrEcCt.getLibInscEc();
		if(res==null) res = ""; 
	    }
//	    Field not found...
//	    System.out.println("====> "+jrName+" = "+res.toString());
	    return res;	
	}

// ************** Fin Gestion de l'impression de la fiche IP du semestre via JASPER...

}

