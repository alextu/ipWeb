package org.cocktail.ipweb.serveur.controlleur;
import java.util.HashMap;

import org.cocktail.ipweb.serveur.Session;
import org.cocktail.ipweb.serveur.components.onglets.Droit;

import com.webobjects.appserver.WOComponent;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSTimestampFormatter;
import com.webobjects.foundation.NSData;

/*
 * Cr�� le 15 nov. 2006
 *
 * @author olive
 * Objectif : g�rer pour un diplome/semestre les infos des stats
 * 	 et la possibilit� ou non de lancer la conversion d'IP
 *
 */

public class StatDiplomeCtrlr {
	private static String COUL_CHOIXBLOQUE = "#D1C0A9"; // gris + fonc� que le fond
	
	private static String COUL_CHOIX_CONSULT = "#D1C0A9"; // gris + fonc� que le fond
	private static String COUL_SURB_CONSULT = "#D1C0A9";	// surbrillance pour la ROW
	
	private static String COUL_CHOIX_MODIF = "#FFFEC0"; // jaune clair + clair que consult
	private static String COUL_SURB_MODIF = "#FFF075";	// surbrillance pour la ROW

	private static String COUL_INTEGRATION_FAITE = "#00FF80"; // Vert
	private static String COUL_SURB_INTEGRATION = "#40FFB0";  // surbrillance pour la ROW

	
	
	/**
	 * on gère les particularités des VDiplomeParcoursSem
	 */
	private Session maSession; 
	
	private String diplome;
	private int semestre;

	private int droitConvertIp;
	private int nbInscrits,nbIpDebutees,nbIpCompletes;
	private String periodeIpWebDipl;
	
	private Integer fspnKey,fannKey;
	
	private boolean ipWebEnCours, integrationIPFaite, annulationDepassee;
	private static 	NSTimestampFormatter tsF = new NSTimestampFormatter("%e %B");
	
	public StatDiplomeCtrlr(Session maSess, EOGenericRecord statDipl, int droitsSurDipl) {
		super();
		maSession = maSess;
		droitConvertIp = droitsSurDipl;
		diplome = statDipl.valueForKey("diplome")+" "+statDipl.valueForKey("anneeDiplome");
		semestre = ((Integer) statDipl.valueForKey("msemOrdre")).intValue();

		fspnKey = (Integer) statDipl.valueForKey("fspnKey");
		fannKey = (Integer) statDipl.valueForKey("fannKey");

		Integer InbInscrits = (Integer) statDipl.valueForKey("nbInscSem");
		nbInscrits = (InbInscrits==null) ? 0 : InbInscrits.intValue();

		Integer InbIpDebutees = (Integer) statDipl.valueForKey("nbIpDebutees");		
		nbIpDebutees = (InbIpDebutees==null) ? 0 : InbIpDebutees.intValue();

		Integer InbIpCompletes = (Integer) statDipl.valueForKey("nbIpCompletes");
		nbIpCompletes = (InbIpCompletes==null) ? 0 : InbIpCompletes.intValue();

		// gestion des aspects dates IP...
		NSTimestamp dateDeb,dateFin;
		NSTimestamp now = new NSTimestamp();
		annulationDepassee = false;

		dateDeb = (NSTimestamp)statDipl.valueForKeyPath("ipDatesDebut");
		if (dateDeb== null) {
			dateDeb = (NSTimestamp)statDipl.valueForKeyPath("ipDatesDebutDom");
		}
		dateFin = (NSTimestamp)statDipl.valueForKeyPath("ipDatesFin");
		if (dateFin== null) {
			dateFin = (NSTimestamp)statDipl.valueForKeyPath("ipDatesFinDom");
		}

		if (dateDeb != null && dateFin != null) {
			if (dateFin.before(dateDeb)) {
				NSTimestamp dateTemp = dateFin;
				dateFin = dateDeb;
				dateDeb = dateTemp;
			}

			// Ajustement des dates au time-zone de l'appli...
			dateDeb = maSession.monApp.conversionDateBDD(dateDeb,0,0);
			dateFin = maSession.monApp.conversionDateBDD(dateFin,23,59);

			if (now.after(dateDeb) && now.before(dateFin)) {
				ipWebEnCours = true;
				periodeIpWebDipl = "ouvert jusqu'au "+tsF.format(dateFin);
			}
			else if (now.after(dateFin)){
				ipWebEnCours = false;
				periodeIpWebDipl = "cloturée depuis le "+tsF.format(dateFin);

				// r�gle de gestion suppl�mentaire : l'annulation de l'int�gration  ne peut se faire 
				// que pdt 15j. apr�s la fin des ip Web...

				NSTimestamp finPlus15 = dateFin.timestampByAddingGregorianUnits(0, 0, 14, 0, 0, 0);
				if (now.after(finPlus15)){
					annulationDepassee = true; 
				}
			}
			else {
				ipWebEnCours = false;
				periodeIpWebDipl = "du "+tsF.format(dateDeb)+" au "+tsF.format(dateFin);
			}
		}
		else { 
			ipWebEnCours = false;
			periodeIpWebDipl = "dates non définies pour ce diplôme...";
		}

		// est-ce que ce semestre a d�j� �t� int�gr� ?
		String integ = (String) statDipl.valueForKeyPath("integrationIp");
		if (integ.equalsIgnoreCase("O")) integrationIPFaite = true;
		else integrationIPFaite = false;

	}

	public boolean integrationIPFaite() {
	    return integrationIPFaite;
	}
	
    public WOComponent lancerIntegration() {
    	if (!integrationIPFaite && !isIpWebEnCours() && convertIpPossible())
    		integrationIPFaite = maSession.integrerChoixIpSemestre(fspnKey,new Integer(semestre),fannKey,1);
        return null;
    }

    public WOComponent annulerIntegration() {
    	if (integrationIPFaite && !isIpWebEnCours() && convertIpPossible()) {
    		boolean res = maSession.integrerChoixIpSemestre(fspnKey,new Integer(semestre),fannKey,0);
    		if (res) integrationIPFaite = false;
    	}
    	return null;
    }

	public String getDiplome() {
		return diplome;
	}
	public int getDroitConvertIp() {
		return droitConvertIp;
	}
	
	public boolean convertIpPossible() {
		return (droitConvertIp==Droit.DROIT_MODIF);
	}
	
	public boolean isIpWebEnCours() {
		return ipWebEnCours;
	}
	public int getNbInscrits() {
		return nbInscrits;
	}
	public int getNbIpCompletes() {
		return nbIpCompletes;
	}
	public int getNbIpDebutees() {
		return nbIpDebutees;
	}
	
	public boolean ipDebutees() {
		return (nbIpDebutees>0);
	}
	
	public boolean isAnnulationDepassee() {
	    return annulationDepassee;
	}
	
	public float pourcentIpComplete() {
	    if (nbIpCompletes>0)
		return (((float)nbIpCompletes)/((float)nbInscrits))*100.0f;
	    else return 0.0f;
	}

	
	public String getPeriodeIpWebDipl() {
		if (ipWebEnCours == false && integrationIPFaite) return "choix intégrés dans ScolPedagogie.";
		return periodeIpWebDipl;
	}
	public int getSemestre() {
		return semestre;
	}
	
	public String getBGColor() {
		if (droitConvertIp==Droit.DROIT_MODIF) {
			if (ipWebEnCours) return COUL_CHOIXBLOQUE;
			else if (integrationIPFaite) return COUL_INTEGRATION_FAITE;
			else return COUL_CHOIX_MODIF;
		}
		else return COUL_CHOIX_CONSULT;
	}
	
	public String coulSelection() {
		String coulSurbrillance = "";
		if (droitConvertIp==Droit.DROIT_MODIF) {
			if (ipWebEnCours) coulSurbrillance = COUL_CHOIXBLOQUE;
			else if (integrationIPFaite) coulSurbrillance = COUL_SURB_INTEGRATION;
			else coulSurbrillance = COUL_SURB_MODIF;
		}
		else coulSurbrillance = COUL_SURB_CONSULT;
		return ("this.style.backgroundColor='"+coulSurbrillance+"'");
	}
	
	// reponse au clic sur l'icone "PDF"
	public NSData imprListeIPincompletes(){
	// , 
	    HashMap parametres = new HashMap();
	    parametres.put("fannKey", fannKey);
	    parametres.put("fspnKey", fspnKey);
	    parametres.put("msemOrdre", new Integer(semestre));	    
	    return maSession.imprimePDF("choixNonFaits.jasper", parametres);
	}
}
