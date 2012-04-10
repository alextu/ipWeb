package org.cocktail.ipweb.serveur.controlleur;


import org.cocktail.ipweb.serveur.Session;
import org.cocktail.ipweb.serveur.metier.IpChoixEc;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.foundation.*;

/**
* @author olive
* Créé le 2 oct. 2006
*
*	Objectif : gestion de l'EC pour laquelle on veut gérer les IP...
*		c'est aussi une sous-classe de ElementAnalyse car elle va participer 
*		� l'analyse des expressions booleenne de choix d'EC (gestion des contraintes de choix) 
*/


public class InscEcCtrlr implements ElementAnalyse {
	
	private static String COUL_CHOIXBLOQUE = "#D1C0A9"; // gris + fonc� que le fond
//	private static String COUL_CHOIXBLOQUE = "#c2c2c2"; // gris + fonc� que le fond
	
	private static String COUL_CHOIX_CONSULT = "#FFFEC0"; // jaune clair + fonc� que mofid
	private static String COUL_SURB_CONSULT = "#FFF075";	// surbrillance pour la ROW
//	private static String COUL_CHOIX_CONSULT = "#FDE9A3"; // jaune clair + fonc� que mofid
//	private static String COUL_SURB_CONSULT = "#FDC485";	// surbrillance pour la ROW
//	private static String COUL_CHOIX_CONSULT = "#FFFC1F"; // jaune clair + fonc� que mofid
	
	private static String COUL_CHOIX_MODIF = "#FFFEC0"; // jaune clair + clair que consult
	private static String COUL_SURB_MODIF = "#FFF075";	// surbrillance pour la ROW
//	private static String COUL_CHOIX_MODIF = "#FFF8A9"; // jaune clair + clair que consult

//	private static String COUL_ERREUR = "#FA9494"; // ros�... 
//	private static String COUL_SURB_ERREUR = "#FF6565"; // ros� + vif
	private static String COUL_ERREUR = "#FDE9A3"; // ros�... 
	private static String COUL_SURB_ERREUR = "#FDC485"; // ros� + vif
	
	private static String COUL_DEJAVAL = "#9EE595"; // EC d�ja valid� (vert d'eau)
	
	private static String MEF_ERR = "erreur";	// mise en forme d'un message d'erreur
	private static String MEF_REM = "commentaire";	// mise en forme d'une remarque sur l'EC (table sp�cifique)
	private static String MEF_INSC = "validee";	// mise en forme si insc existe d�j�...		
	private static String MEF_NORM = "normal";	// mise en forme normale
	
	
	private EOGenericRecord monEcMaquette;	// enregistrement 
	private InscUeCtrlr ueRattachement;	// pointeur vers le controleur d'UE � laquelle est rattach� cet EC
	private InscSemestreCtrlr semRattachement;	// semestre � la quelle est rattach�e cette EC...
	
	private Session maSession;
	
	private String comment;
	private String mueKey;		// le code interne de l'UE de rattachement (pour l'acc�s via une Ancre)
	
	private String libelleEc,codeEc,libelleLongEc;
	private Integer mrecKey,mecKey,msemKey,mrueKey;
	private double nbhCM,nbhTD,nbhTP;
	private double pointsECTS;
	public	String hCM,hTD,hTP; 
	private String hCmTdTp;
	
	private boolean ecAChoix;			// si VRAI cet EC est un EC à choix OU facultatif (choix sans ects derrière) 
		
	private boolean choixBloque=false;	// si VRAI cet EC est d�j� choisi et ne peut plus �tre modifi�
	private boolean choixBloqueChoisi;	// PO 2009 : un EC bloqué n'est pas toujours choisi (cas des EC "constantes" pour contraintes)
	
	private boolean ipSurEC=false;		// si VRAI cet EC (� choix ou non) fait partie des IP de l'�tudiant...
	private boolean caseCochee=false;	// si VRAI, il s'agit d'une EC � choix non bloqu�e et portant une case � coch�e activ�e...
	private boolean ecEnErreur=false;	// vrai si pb avec cet EC...
	private boolean ecAmasquer=false;	// si vrai cet EC ne doit pas apparaitre � l'�cran (choix provoquerait une erreur)
	private boolean ecDejaValide =false;	// si VRAI cet EC poss�de une dispense (avec ou sans note)
	private boolean ecAvecNote =false;	// si VRAI cet EC poss�de une note
	
	private EOGenericRecord inscEc;		// �ventuelle inscription � cet EC existant dans ScolPedagogie...
	private String libInscEc;
	private String errConseil,errMsg;	// message d'erreur et eventuellement conseil pour corriger l'erreur...
	private String mefStatut;
	
	private IpChoixEc choixEc;	// �ventuel choix � cet EC existant dans Ip_Web...
	private NSArray ecCtSameCode;		// liste des autres ctrlr d'EC g�rant des EC ayant m�me MEC_CODE...

	public InscEcCtrlr(EOGenericRecord ecMaq,InscUeCtrlr ueRattach,Session sess,Integer msemK,Integer laMrueKey) {
		super();
		monEcMaquette = ecMaq;
		maSession = sess;
		ueRattachement = ueRattach;
		semRattachement = ueRattachement.getInscSemCt();
		mrueKey = laMrueKey;
		msemKey = msemK;

		libelleEc = (String)monEcMaquette.valueForKey("mecLibelleCourt");
		libelleLongEc = (String)monEcMaquette.valueForKey("mecLibelle");
		
		codeEc = (String)monEcMaquette.valueForKey("mecCode");
		pointsECTS = ((Double)monEcMaquette.valueForKey("mecPoints")).doubleValue();
		mueKey = ((Integer)monEcMaquette.valueForKey("mueKey")).toString();

		Double zero = new Double(0.0);
		if (((Double)monEcMaquette.valueForKey("nbhCm")) != null)
			nbhCM = ((Double)monEcMaquette.valueForKey("nbhCm")).doubleValue();
		else nbhCM = 0.0;
		
		if (((Double)monEcMaquette.valueForKey("nbhTd")) != null)
			nbhTD = ((Double)monEcMaquette.valueForKey("nbhTd")).doubleValue();
		else nbhTD = 0.0;
		
		if (((Double)monEcMaquette.valueForKey("nbhTp")) != null)
			nbhTP = ((Double)monEcMaquette.valueForKey("nbhTp")).doubleValue();
		else nbhTP = 0.0;
		
		hCM = maSession.formattedouble(nbhCM,true);
		hTD = maSession.formattedouble(nbhTD,true);
		hTP = maSession.formattedouble(nbhTP,true);
		
		// Chaine � construire... CM/TD/TP
		StringBuffer sb = new StringBuffer();
		boolean prec = false;
		if (nbhCM>0.0) { sb.append(hCM+"h CM"); prec = true; }
		if (nbhTD>0.0) {
			sb.append((prec?", ":"")+hTD+"h TD"); prec = true; }
		if (nbhTP>0.0) {
			sb.append((prec?", ":"")+hTP+"h TP"); prec = true; }
		
		hCmTdTp = sb.toString();
		
		mrecKey = (Integer)monEcMaquette.valueForKey("mrecKey");
		mecKey = (Integer)monEcMaquette.valueForKey("mecKey");
		
		String typeEc = (String)monEcMaquette.valueForKey("mtecCode");
		// Un EC optionnel OU facultatif n'est pas bloquant
		if (typeEc.compareToIgnoreCase("O") == 0) {
			ecAChoix = false;
			choixBloque = true;
			choixBloqueChoisi = true;	// PO 2009
			ipSurEC = true;
		}
		else ecAChoix = true;
		
		inscEc = null; libInscEc = null;
		choixEc = null;
		ecCtSameCode = null;
		
		mefStatut = MEF_NORM;
		
		// init de la remarque : MODIF CAR TROP GOURMAND !
//		EOGenericRecord remEo = (EOGenericRecord)monEcMaquette.valueForKey("ipUeEcComment");
//		if (remEo != null) {
//			comment = (String)remEo.valueForKey("mrecComment");
//			coulTxtStatut = TXT_REM;
//			mefStatut = MEF_REM;
//		}
//		else comment="";
		
		comment = "";
		
	}
	
	// renvoit VRAI si cet EC est Facultatif (pouvant être choisi mais sans ects derrière)
	public boolean ecFacultatif()	{	 
		return (((String)monEcMaquette.valueForKey("mtecCode")).compareToIgnoreCase("F") == 0);
	}
	
	// init externe de la remarque...
	public void setComment(String commentaire) { comment = commentaire; }
	

	// cr�ation d'un InscEcCtrlr "light" pour la simulation (recherche heuristique des diff�rentes solutions 
	//	de l'�quation bool�enne exprimant les contraintes de choix d'EC li�s entre eux...)
	public InscEcCtrlr(Integer mrecK) {
		mrecKey = mrecK;
		ecAChoix = true;
		choixBloque = false;
		inscEc = null; libInscEc = null;
		choixEc = null;
		ecCtSameCode = null;
	}

	// creation d'un InscEcCtrlr "CONSTANTE" pour les MREC_KEY non présents dans la liste (de type validation d'ECTS) 
	//	valeur FALSE pour l'equation booleenne exprimant les contraintes de choix d'EC lies entre eux...)
	public InscEcCtrlr(Integer mrecK, boolean valeurInit) {
		mrecKey = mrecK;
		ecAChoix = true;
		choixBloque = true;
		choixBloqueChoisi = valeurInit;
		inscEc = null; libInscEc = null;
		choixEc = null;
		ecCtSameCode = null;
	}
	
	public String toString() {
		return " ["+mrecKey+":"+(evaluerExpression()?"1] ":"0] ");
	}
	
	public String getLibelleEc() {
		return libelleEc;
	}
	
	public String getLibelleLongEc() {
		return libelleLongEc;
	}
	
	// rep�rer un Ec par son libell� court + son n� d'ordre d'UE
	public String getRefEcEtUe() {
		return getLibelleEc()+" (UE"+ueRattachement.getOrdreUe()+")";
	}
	
	// rep�rer le n� d'ordre de l'UE de rattachement de cette EC
	public String getOrdreUe() {
		return "UE"+ueRattachement.getOrdreUe();
	}
	
	
	public String getCodeEc() {
		return codeEc;
	}
	
	public String getCodeUePere() {
		return ueRattachement.getCodeUe();	// le code UE de l'UE � laquelle est rattach�e cette EC...
	}
	
	public double getPointsECTS() {
		return pointsECTS;
	}
	
	public String getStPointsECTS() {
		return maSession.formattedouble(pointsECTS,false);
	}
	
	public String getUeKey() {
		return mueKey;
	}
	
	
	// construction du message pour OVERLIB pour cet EC (d�tails sur comment corriger une erreur )
	public String getMsgOlEc() {
//		"return overlib(' --Pas de Commentaire-- ', CAPTION,'R�union du consortium Cocktail');"
		StringBuffer sb = new StringBuffer("return overlib('");
		sb.append(errConseil+"', CAPTION,'");
		sb.append("Pour corriger cette erreur :');");
		return sb.toString();
	}
	
	// construction du message pour OVERLIB pour ce type d'EC (détails sur les EC bloqués )
	public String getMsgOlTypeEc() {
		String why = "Il s`agit d`un enseignement obligatoire.";
		if (ecAChoix) why ="Vous avez déjà validé cet EC lors de votre inscription précédente... Il est donc acquis définitivement !";
//		"return overlib(' --Pas de Commentaire-- ', CAPTION,'R�union du consortium Cocktail');"
		StringBuffer sb = new StringBuffer("return overlib('");
		sb.append(why+"', CAPTION,'");
		sb.append("L`inscription à cet EC ne peut être changée');");
		return sb.toString();
	}
	
	public double getNbhCM() { return nbhCM; }
	public double getNbhTD() { return nbhTD; }
	public double getNbhTP() { return nbhTP; }
	
	public String getNbHCmTdTp() {
		return hCmTdTp;
	}
	
//	VRAI si cet EC est un EC � choix... 
	public boolean isEcAChoix() {
		return ecAChoix;
	}

	//	VRAI si cet EC est un EC bloqu�... 
	public boolean isEcBloque() {
		return choixBloque;
	}
	
//	VRAI si cet EC est un EC ayant une dispense avec ou sans note... 
	public boolean isEcDejaValide() {
		return ecDejaValide;
	}
	
//	VRAI si cet EC est un EC ayant une dispense avec ou sans note... 
	public boolean isEcAvecNote() {
		return ecAvecNote;
	}
	
	// VRAI si cet EC (à choix ou non) fait partie des IP de l'étudiant..	
	public boolean ecAvecIp() {
		if (ueRattachement.cumulEctsTemp()) return (choixBloque || caseCochee);
			else return ipSurEC;
	}
	
	
	
	// vrai si l'on doit afficher une case � cocher pour cet EC (si isEcAChoix() 
	public boolean afficheCaC() {
		if (!choixBloque && maSession.modifEnCours()) return true;
		return false;
	}
	
	public boolean getCaseCochee() { return caseCochee; }
	public void setCaseCochee(boolean cac) { caseCochee=cac; }
	
	
	public Integer getMrecKey() {
		return mrecKey;
	}
	
    public String idCaseACoche() {
        return ("CC-"+mrecKey);
    }
    
    // appel du JavaScript qui va cocher ou d�cocher... en cliquant n'importe ou sur la ligne !
    public String appelJSCocheLigne() {
    	if (!choixBloque && (semRattachement.modeModif())) return ("cocheLigne('"+idCaseACoche()+"');");
    	else return null;
    }
	
	public Integer getMecKey() {
		return mecKey;
	}
	
	// affecter une IP de ScolPedagogie sur cet EC de la maquette... avec par csqt chgt de status.
	public void setInsc (EOGenericRecord inscEcEO) {
		inscEc = inscEcEO;
		libInscEc = libInscEc();
		choixBloque = true;
		choixBloqueChoisi = true;
		ipSurEC = true;	
	}
	
	// indiquer un choix de IP_WEB sur cet EC de la maquette... avec par csqt chgt de status.
	public void setChoix (IpChoixEc choixEcEO) {
		choixEc = choixEcEO;
		ipSurEC = true;
		if (choixEc.choixIntegre().equalsIgnoreCase("O")) {
//			choixBloque = true;	// se pr�munir contre une int�gration d�j� faites dans ScolPedagogie...
			
			// si on arrive ici, c'est qu'il n'y a plus d'IP dans ScolPeda sur un EC pourtant choisi dans IpWeb !
			// on a donc modifié ScolPeda APRES l'intégration : ne plus afficher ce choix étudiant dans IpWeb...
			ecAmasquer = true;
		
		}
		else caseCochee = true;
		
	}
	
//	 liste des autres ctrlr d'EC g�rant des EC ayant m�me MEC_CODE... si appel alors d'autre EC avec m�me code existent
//	public void setListeEcSameCode(NSArray listeEcSameCode) {
//		ecCtSameCode = listeEcSameCode;
//		// v�rif si ce choix est possible ou doit �tre masqu�...
//		boolean autreChoixEstBloque = true;
//		java.util.Enumeration enumerator = ecCtSameCode.objectEnumerator();
//		while (enumerator.hasMoreElements()) {
//			InscEcCtrlr ecCt = (InscEcCtrlr)enumerator.nextElement();
//			if(!ecCt.isEcBloque()) autreChoixEstBloque = false; 
//		}
//		if (autreChoixEstBloque) ecAmasquer=true;
//	}
	
	public boolean ecAmasquer() { return ecAmasquer; }
	public void masquerEc() { ecAmasquer = true; }
	
//	 on scanne si l'EC ne doit pas �tre masqu� des choix possibles ("DIV_xxxx")
	public boolean choixAMasquer() {
	    if (codeEc.substring(0, 3).equals("DIV")) {
		masquerEc();
		return true;
	    }
	    else return false;
	}
	
	// pouvoir colorer dynamiquement la ligne de l'EC en fonction de son statut
	public String getBGColor() {
		if (ecEnErreur) return COUL_ERREUR;
		else if (choixBloque && !ecAChoix) return COUL_CHOIXBLOQUE;
		else if (choixBloque && ecAChoix) return COUL_DEJAVAL;
		else if (ipSurEC) return COUL_CHOIX_CONSULT;
		else return COUL_CHOIX_MODIF;
	}
	
	// l'instruction JavaScript pour changer la couleur de la row quand elle est modifiable...
	public String getCoulSelection() {
		String coulSurbrillance = "";
		if (!choixBloque && maSession.modifEnCours()) {
			if (ecEnErreur) coulSurbrillance = COUL_SURB_ERREUR;
			else if (ipSurEC) coulSurbrillance = COUL_SURB_CONSULT;
			else coulSurbrillance = COUL_SURB_MODIF;
		}
		return ("this.style.backgroundColor='"+coulSurbrillance+"'");
	}
	
	public String typeEc() {
		if (isEcBloque()) {
			return (String)monEcMaquette.valueForKey("mtecCode");
		}
		else return "CE";	// choix en cours...
	}
	
	public String libTypeEc() {
	    if (isEcBloque()) {
		if (ecAChoix) return "Choix Intégré";
		return "Obligatoire";
	    }
	    else return "Choix en cours";	// choix en cours...
	}
	
	// indiquer une erreur sur une v�rif de contrainte...
	public void setErreur(String msgErr) {
		errMsg = msgErr;
		ecEnErreur = true;
		ueRattachement.signaleErreur();		// indiquer � l'UE m�re qu'elle contient au moins 1 EC en erreur
		ueRattachement.afficherDetails();	// �tre s�r que l'on verra cet erreur au refresh �cran !
	}

	// indiquer une erreur sur une vérif de contrainte, et un message de remédiation...
	public void setErreur(String msgErr,String conseil) {
		errConseil = conseil;
		setErreur(msgErr);
	}
	
	public void enleveErreur() {
		errMsg = null;
		errConseil = null;
		ecEnErreur = false;
	}
	
	// valider les chgts pour les reporter dans la base...
	public void validerModif() {
		//		 une nouvelle EC s�lectionn�e
		if (caseCochee && choixEc==null) {	
			choixEc = maSession.ajouteChoixEc(mrecKey,msemKey,mrueKey);
			ipSurEC = true;
		}
		else if (!caseCochee && choixEc!=null) {	
//			 une EC dont le choix est annul� !
			maSession.supprimeChoixEc(choixEc,mrecKey,msemKey);
			choixEc = null;
			ipSurEC = false;
		}
	}
	
	// retour � la situation ant�rieure...
	public void annulerModif() {
		if (choixEc==null) caseCochee=false;
		else caseCochee=true;
		enleveErreur();
	}
	
	public boolean ecEnErreur() { return ecEnErreur; }
	public boolean ecEnErreurAvecConseil() { return (ecEnErreur && errConseil!=null); }

	
	// ce qu'il faut afficher dans la case statut pour cet EC...
	public String msgStatutEc() {
		if (errMsg != null) {
			return errMsg;
		}
		else if (inscEc != null) {
			return libInscEc;
		}
		else return comment;
//		return null;
	}
	
	public String getLibInscEc() {
	    return libInscEc;
	}
	
	
	// Decryptage des EC portant une IP de ScolPedagogie...
	// a lancer une seule fois puis garder en cste
	private String libInscEc() {
	    if (inscEc != null) {
		if (!ecAChoix) {
		    mefStatut = MEF_NORM;
		}
		else mefStatut = MEF_INSC;

		int imrecDispense = ((Integer)inscEc.valueForKey("imrecDispense")).intValue();
//		
		Double imrecSession1D = ((Double)inscEc.valueForKey("imrecSession1"));
		Double imrecSession2D = ((Double)inscEc.valueForKey("imrecSession2"));
		if (imrecSession1D != null && imrecSession1D != null)
		    ecAvecNote = true;
		
		// selon les valeurs dans SCOLARITE.SCOL_CONSTANTE_DISPENSE
		// évolution le 2 avril 2010... 
		if ((imrecDispense >= 4 && imrecDispense <= 7) || (imrecDispense >= 12 && imrecDispense <= 15))
			ecDejaValide = true;
		
		if (imrecDispense == 4 || imrecDispense == 14) {
		    double imrecSession1 = 0.0;
		    if (imrecSession1D!=null) imrecSession1 = imrecSession1D.doubleValue();
		    double imrecSession2 = 0.0;
		    if (imrecSession2D!=null) imrecSession2 = imrecSession2D.doubleValue();

		    if (imrecSession2>imrecSession1) imrecSession1=imrecSession2;
		    //				double imrecNoteBase = maSession.convChpDouble(inscEc,"imrecNoteBase");
		    
		    // cas ou l'UE a ete valide MAIS sans les EC qui sont donc valides sans note
		    if (imrecSession1 == 0.0) return ("EC déjà validé.");
		    else return ("EC déjà validé ("+imrecSession1+"/20)"); 
		}
		else if (ecDejaValide) return "EC déjà validé (scolarité)"; 
	    }
	    return "";
	}
	
	public String miseEnFormeStatut() {
		if (ecEnErreur) return MEF_ERR;
		return mefStatut;
	}

	
	/* (non-Javadoc)
	 * @see ElementAnalyse#evaluerExpression()
	 * Va participer � l'analyse des expressions booleenne de choix d'EC (gestion des contraintes de choix)	 */
	public boolean evaluerExpression() {
		// vrai si la case est cochée OU s'il s'agit d'un choix bloqué (IP dans ScolPeda)
		return (caseCochee | (choixBloque && choixBloqueChoisi));	
	}
}
