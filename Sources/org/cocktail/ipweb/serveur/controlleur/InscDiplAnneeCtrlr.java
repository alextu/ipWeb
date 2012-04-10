package org.cocktail.ipweb.serveur.controlleur;
import org.cocktail.ipweb.serveur.Session;

import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/*
 * Créé le 25 juil. 2007 
 *
 * Objectif : gérer le niveau inscription à un "diplome/année suivie" donné...
 * ----------
 * Cet objet recup�re les rows de la vue SCOL_INSCRIPTION_ETUDIANT
 * pour un idiplN�, fspnKey, idipl_annee_suivie (une "inscription � un diplome")
 *  
 */


public class InscDiplAnneeCtrlr {

	protected Session maSession;
	private EOGenericRecord inscriptionEtudiant;
	private NSArray lesFormCtrlr;		// les [0..2] InscFormationCtrlr d�clar�s...

	public InscDiplAnneeCtrlr(Session sess,EOGenericRecord uneInsc, boolean preInscriptionEnCours) {
		super();
		maSession = sess;
		inscriptionEtudiant = uneInsc;	// ScolInscriptionEtudiant
		initInscParcours(preInscriptionEnCours);
	}


	// on gère à présent 2 InscFormationCtrlr par insc° (1 par semestre)
	private void initInscParcours(boolean preInscriptionEnCours) {

		NSMutableArray listeForm = new NSMutableArray();

		int annee = ((Integer)inscriptionEtudiant.valueForKey("idiplAnneeSuivie")).intValue();

		InscFormationCtrlr formCtSemI = new InscFormationCtrlr(maSession,this,annee * 2 -1, preInscriptionEnCours);
		if (formCtSemI.valide())
			listeForm.addObject(formCtSemI);

		InscFormationCtrlr formCtSemP = new InscFormationCtrlr(maSession,this,annee * 2, false);
		if (formCtSemP.valide())
			listeForm.addObject(formCtSemP);

		lesFormCtrlr = new NSArray(listeForm);
	}

	// renvoyer le EO pour l'inscription de l'étudiant...
	public EOGenericRecord eoInscEtud() {
		return inscriptionEtudiant;
	}

	public String libelleInscription() {
		return (String)inscriptionEtudiant.valueForKey("libelleInscription");
	}

	public String diplome() {
		return (String)inscriptionEtudiant.valueForKey("diplome");
	}

	public String diplomeLl() {
		return (String)inscriptionEtudiant.valueForKey("diplomeLl");
	}

	public String anneeDiplome() {
		return (String)inscriptionEtudiant.valueForKey("anneeDiplome");
	}
	public Integer anneeUniv() {
		return (Integer)inscriptionEtudiant.valueForKey("fannKey");
	}

	public Integer fspnKey() {
		return (Integer)inscriptionEtudiant.valueForKey("fspnKey");
	}

	public String prenomNomEtud() {
		String res = (String)inscriptionEtudiant.valueForKey("adrPrenom")+" "+
		(String)inscriptionEtudiant.valueForKey("adrNom");
		return res;
	}

	// fille = "e", garçon = ""
	public String genreEtud() {
		String res = (String)inscriptionEtudiant.valueForKey("adrCivilite");
		if (res == null || res.equalsIgnoreCase("M.")) return "";
		else return "e";
	}

	public Integer etudNumero() {
		return (Integer)inscriptionEtudiant.valueForKey("etudNumero");	
	}	
	
	public Integer idiplNumero() {
		return (Integer)inscriptionEtudiant.valueForKey("idiplNumero");	
	}


	public NSArray getLesFormCtrlr() { return lesFormCtrlr; }

	// retourne une array contenant : 
	//	- le InscFromationCtrlr qui doit �tre ouvert par d�faut (le + prioritaire !)
	//	- son indice de priorit�...
	public NSArray getSemPrioritaire() {
		NSMutableArray res = null;
		if (lesFormCtrlr != null && lesFormCtrlr.count() > 0) {
			res = new NSMutableArray();
			int ordre = -100; 
			InscFormationCtrlr formCtPrio = null;
			java.util.Enumeration e = lesFormCtrlr.objectEnumerator();
			while (e.hasMoreElements()) {
				InscFormationCtrlr iFormCt = (InscFormationCtrlr)e.nextElement();
				int ordreIsem = iFormCt.getOrdrePriorite();
				if (ordreIsem > ordre) {
					ordre = ordreIsem;
					formCtPrio = iFormCt;
				}
			}
			res.addObject(new Integer(ordre));
			res.addObject(formCtPrio);
		}
		return (NSArray)res;
	}

	
	// Retourne le ctrlr de formation correspondant à ce msemKey (ou bien null)
	public InscFormationCtrlr getFormCtrlr(Integer msemKey) {
		InscFormationCtrlr resCtrlr = null;
		if (lesFormCtrlr != null && lesFormCtrlr.count() > 0) {
			java.util.Enumeration e = lesFormCtrlr.objectEnumerator();
			while (resCtrlr == null && e.hasMoreElements()) {
				InscFormationCtrlr iFormCt = (InscFormationCtrlr)e.nextElement();
				if (iFormCt.msemKeyPP().compareTo(msemKey) == 0)
					resCtrlr = iFormCt;
			}
		}
		return resCtrlr;
	}
	
}
