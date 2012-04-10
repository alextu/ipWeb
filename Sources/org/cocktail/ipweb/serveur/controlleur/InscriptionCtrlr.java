package org.cocktail.ipweb.serveur.controlleur;
/*
 * Cr�� le 20 juil. 2006
 *
 * Objectif G�rer les inscriptions �tudiantes,
 * => seulement les insc� administratives pour l'ann�e en cours...
 */

/**
 * @author olive
 *
 */

import org.cocktail.ipweb.serveur.Session;
import org.cocktail.ipweb.serveur.metier.IpwIndividuUlr;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/* ATTENTE
import org.cocktail.scolarix.serveur.metier.eos.EOPreEtudiant;
import org.cocktail.scolarix.serveur.metier.eos.EOPreInscription;
*/

public class InscriptionCtrlr {

	private EOGenericRecord etudiant;	// pointe vers enreg. �tudiant de l'individu en cours...
	/* ATTENTE
	private EOPreEtudiant preEtudiant;
    */
	
	private NSArray inscriptionsCourantes;	//les dernières inscriptions en date de l'étudiant
	private NSArray lesDiplAnneeCtrlr;		// les InscDiplAnneeCtrlr déclarés...

	private NSArray toutesLesInscriptions;
	private NSDictionary tousLesDiplAnneeCtrlParAnneeUniv;
	
//	private int anneeEnCours;
	protected Session maSession;

	/** constructeur de la classe ctrôleur d'inscription...
	 * 
	 */
	public InscriptionCtrlr(Session sess,IpwIndividuUlr monInd) {
		super();
		// lancer le chargement des inscriptions de l'individu en param�tre...
		maSession = sess;
//		anneeEnCours = sess.getAnneeEnCours();

		etudiant = (EOGenericRecord)monInd.toEtudiant();
		if (etudiant != null) {
			Integer leEtudNumero = (Integer)etudiant.valueForKey("etudNumero");
			verifSiIdentificationAnonymeAFaire(leEtudNumero);
			chargerInscriptions(leEtudNumero);
		}
		else {
			inscriptionsCourantes = null;
		}	
	}

	
	/* ATTENTE
	// Cas d'un étudiant pré-inscrit...
	// On vérifie si les données de ScolInscriptionEtudiant existent, sinon on les crée en fonction 
	public InscriptionCtrlr(Session sess,EOPreEtudiant lePreEtudiant) {
		super();
		// lancer le chargement des inscriptions de l'individu en param�tre...
		maSession = sess;
		preEtudiant = lePreEtudiant;
		
		stubPreInscription(lePreEtudiant.etudCodeIne());
	}	
	 */
	
	
	// But : si l'étudiant a fait des choix en tant que pré-inscrit et qu'il a été inscrit administr. depuis
	//       il faut "l'identifier" (idem bouton "identifier" dans ScolPedagogie) pour raccrocher ses choix d'IP antérieurs
	//		 à ses choix "officels" de diplômes...
	private void verifSiIdentificationAnonymeAFaire(Integer leEtudNumero) {
 		NSArray bindings = new NSArray(new Object[] {leEtudNumero, new Integer(maSession.getAnneeEnCours())});
		EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat("etudNumero = %@ and fannKey = %@ ", bindings);
		EOFetchSpecification fetchSpec = new EOFetchSpecification("VAnonymeAIdentifier",qualifier, null);
		EOEditingContext ec = maSession.defaultEditingContext();
		NSArray inscAIdentifier = ec.objectsWithFetchSpecification(fetchSpec);
		
		if (inscAIdentifier != null && inscAIdentifier.count()>0) {
			java.util.Enumeration enumerator = inscAIdentifier.objectEnumerator();
			int offsetNoInsc = 0;
			while (enumerator.hasMoreElements()) {
				EOGenericRecord insc = (EOGenericRecord)enumerator.nextElement();
				// pour chacun des rows renvoyé, il faut procéder à l'identification...
				Integer idiplNumero = (Integer)insc.valueForKey("idiplNumero");
				Integer iEtudNumero = (Integer)insc.valueForKey("iEtudNumero");
				
				// en appelant la procédure stockée qui va bien !
				NSArray clefs = new NSArray(new Object[] {"10_etudNumero","20_idiplNumero","30_ietudNumero","40_fannKey"});
				NSArray vals = new NSArray(new Object[] {leEtudNumero, idiplNumero, iEtudNumero, 
						new Integer(maSession.getAnneeEnCours())});

				NSMutableDictionary dico = new NSMutableDictionary(vals,clefs);
				maSession.execProc("pLmdUpdIetudiant",(NSDictionary)dico);
			}
		}
		
	}
	

	/* ATTENTE

	// Dans le cas d'une préInscription, générer les stub de SCOL_INSCRIPTION_ETUDIANT s'ils n'existent pas déjà
	// prévoir de faire le ménage si le gus a changé ses choix de pré-inscription ?! 
	
	private void stubPreInscription(String preEtudIne) {
		if (preEtudiant != null && preEtudiant.preHistorique() != null) {
			// vérifier que les inscriptions temporaires existent et les charger si existent...
			chargerInscriptions((Integer)preEtudiant.etudNumero());

			// si pas encore créées, générer des inscriptions temporaires... avec pour chacune une inscription au parcours commun !
			if (inscriptionsCourantes == null || (inscriptionsCourantes != null && inscriptionsCourantes.count()==0)) { 
				NSArray lesFormations = preEtudiant.preHistorique().toPreInscriptions();
				if (lesFormations != null) {
					java.util.Enumeration enumerator = lesFormations.objectEnumerator();
					int offsetNoInsc = 0;
					while (enumerator.hasMoreElements()) {
						EOPreInscription insc = (EOPreInscription)enumerator.nextElement();
						
						// TODO = ALLER voir dans le fwk Scolarite comment ça marche, car la récup du fspnKey foire à présent...
						
						Integer fspnKey = null;
					//	Integer fspnKey = insc.toFwkScolarite_ScolFormationSpecialisation(). fspnKey();
						Integer idiplAnneeSuivie = insc.idiplAnneeSuivie();
						Integer msemOrdre = new Integer(idiplAnneeSuivie.intValue() * 2 - 1);

						// On n'ajoute une row temporaire à SCOL_INSCR°_ETUDIANT que si ça rime à qqchose // IPWeb
						if (maSession.verifDatesDiplSem(fspnKey, msemOrdre)) {

							int noidipl = preEtudiant.preHistorique().etudNumero().intValue()+offsetNoInsc;
							offsetNoInsc += 10000;

							NSArray clefs = new NSArray(new Object[] {"10_histAnneeScol","12_etudNumero","14_histNumero","16_idiplNumero",
									"18_noIndividu", "20_persId", "22_iEtudKey", "24_adrOrdreScol", "26_adrOrdreParent",
									"32_adrNom", "34_adrPrenom", "38_fspnKey",
									"42_idiplAnneeSuivie", "44_idiplTypeInscription", "46_idiplPassageConditionnel",
									"52_resCode"});
							NSArray vals = new NSArray(new Object[] {new Integer(maSession.getAnneeEnCours()),
									preEtudiant.preHistorique().etudNumero(),
									// (EOUtilities.primaryKeyForObject(maSession.defaultEditingContext(), preEtudiant.preHistorique())).valueForKey("histNumero"),
									new Integer(0),
									// (EOUtilities.primaryKeyForObject(maSession.defaultEditingContext(), insc)).valueForKey("idiplNumero") ,
									new Integer(noidipl),
									new Integer(0), new Integer(0), new Integer(noidipl), new Integer(0), new Integer(0), 
									preEtudiant.toPreIndividu().nomPatronymique(), 
									preEtudiant.toPreIndividu().prenom(),
									fspnKey, 
									idiplAnneeSuivie,
									new Integer(9), "N", "#"});

							NSMutableDictionary dico = new NSMutableDictionary(vals,clefs);
							dico.takeValueForKey(preEtudIne, "28_etudCodeIne");	
							dico.takeValueForKey(null, "30_adrCivilite");
							dico.takeValueForKey(null, "35_adrPenom2");
							dico.takeValueForKey(null, "36_etudNomMaritial");
							dico.takeValueForKey(null, "40_mparKey");
							dico.takeValueForKey(null, "48_idiplDateInsc");
							dico.takeValueForKey(null, "50_idiplDateDemission");

							maSession.execProc("pLmdInsIetudiant",(NSDictionary)dico);
						}
					}
					// Charger les INSC nouvellement créées ...
					chargerInscriptions((Integer)preEtudiant.etudNumero());
				}
			}
		}
		/// Voir pour la suite d'autres pistes de recherche (hors du ScolarixFwk... en passant en direct par les tables PRE_XXXX) 
	*/	
		
/*		
		etudiant = (EOGenericRecord)monInd.toEtudiant();
		if (etudiant != null) chargerInscriptions();
		else {
			inscriptionsCourantes = null;
		}
		
	}	
*/			

	
	public int numeroEtudiant()
	{

		/* ATTENTE
		
		if (preEtudiant != null && preEtudiant.preHistorique() != null) 
			return (preEtudiant.preHistorique().etudNumero()).intValue();
		else if (etudiant != null)
			return ((Integer)etudiant.valueForKey("etudNumero")).intValue();
		else 
			return 0;
		*/
		
		if (etudiant != null)
			return ((Integer)etudiant.valueForKey("etudNumero")).intValue();
		else 
			return 0;		
	}
	
	private void chargerInscriptions(Integer etudNumero) {
		//fetcher avec n� etudiant et ann�e en cours
		NSArray bindings = new NSArray(new Object[] {etudNumero,
				new Integer(maSession.getAnneeEnCours())});
    	
		EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
				"etudNumero = %@ and fannKey = %@", bindings);

		EOSortOrdering typeInsc = EOSortOrdering.sortOrderingWithKey("idiplTypeInscription",
    			EOSortOrdering.CompareAscending);
    	
		NSArray sortOrderings = new NSArray(new Object[] {typeInsc});
		
		EOFetchSpecification fetchSpec = new EOFetchSpecification("IpwScolInscriptionEtudiant",
				qualifier, sortOrderings);

		EOEditingContext ec = maSession.defaultEditingContext();

		inscriptionsCourantes = ec.objectsWithFetchSpecification(fetchSpec);
		
		// si des formations ont bien été récupérées...génerer les DiplAnneeCtrlr nécessaires
		if (inscriptionsCourantes != null && inscriptionsCourantes.count()>0) {
			NSMutableArray listeDiplAnnee = new NSMutableArray();
			java.util.Enumeration enumerator = inscriptionsCourantes.objectEnumerator();
			while (enumerator.hasMoreElements()) {
				EOGenericRecord insc = (EOGenericRecord)enumerator.nextElement();
				/* ATTENTE
				InscDiplAnneeCtrlr diplAnneeCtlr = new InscDiplAnneeCtrlr(maSession, insc, preEtudiant != null);
				*/
				InscDiplAnneeCtrlr diplAnneeCtlr = new InscDiplAnneeCtrlr(maSession, insc, false);
				listeDiplAnnee.addObject(diplAnneeCtlr);
				}
			lesDiplAnneeCtrlr = new NSArray(listeDiplAnnee);
		}
	}
	
	
	public NSArray getInscriptionsCourantes() {
		return inscriptionsCourantes;
	}
	
	// avoir le semestre par d�faut (mrsemKey) et formation � traiter au lancement de l'appli... 
	// (sauf si le param est non nul = dans ce cas, ramener le InscFormationCtrlr correspondant, s'il existe)
	public NSDictionary getSemParDefaut(Integer msemKey) {
		int ordreICT,ordre = -100; 
		InscFormationCtrlr inscCtPrio = null; 
		if (inscriptionsCourantes != null && inscriptionsCourantes.count()>0) {
			// Algo : parcourir toutes les insc� et choisir celle dont le semestre � la plus haute priorit�

			java.util.Enumeration e = lesDiplAnneeCtrlr.objectEnumerator();
			
			if (msemKey != null) {
				// recherche du bon msemKey
				while (inscCtPrio==null && e.hasMoreElements()) {
					InscDiplAnneeCtrlr courInscCt = (InscDiplAnneeCtrlr)e.nextElement();
					inscCtPrio = courInscCt.getFormCtrlr(msemKey);
				}
			}
			// recherche par ordre de priorité
			else {
				while (e.hasMoreElements()) {
					InscDiplAnneeCtrlr courInscCt = (InscDiplAnneeCtrlr)e.nextElement();
					NSArray poidInscCt = courInscCt.getSemPrioritaire(); 
					if (poidInscCt != null && poidInscCt.count() == 2) {
						ordreICT = ((Integer)poidInscCt.objectAtIndex(0)).intValue();
						if (ordreICT >= ordre) {
							ordre = ordreICT;
							inscCtPrio = (InscFormationCtrlr)poidInscCt.objectAtIndex(1);
						}
					}
				}
			}
			if (inscCtPrio != null) {
				NSDictionary userInfo = new NSDictionary(new Object[] {inscCtPrio}
				, new Object[] {"InscFormCtrlr"});
				return userInfo;
			}

			else return null;
		}

		else return null;
	}

	public NSArray getLesDiplAnneeCtrlr() {
	    return lesDiplAnneeCtrlr;
	}

	private void chargerToutesLesInscriptionsParAnnee(Integer etudNumero) {
		// fetcher avec n� etudiant et ann�e en cours
		NSArray bindings = new NSArray(new Object[] { etudNumero });
		EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
				"etudNumero = %@", bindings);
		EOSortOrdering typeInsc = EOSortOrdering.sortOrderingWithKey(
				"idiplTypeInscription", EOSortOrdering.CompareAscending);
		NSArray sortOrderings = new NSArray(new Object[] { typeInsc });
		EOFetchSpecification fetchSpec = new EOFetchSpecification(
				"IpwScolInscriptionEtudiant", qualifier, sortOrderings);
		EOEditingContext ec = maSession.defaultEditingContext();
		toutesLesInscriptions = ec.objectsWithFetchSpecification(fetchSpec);
		// si des formations ont bien été récupérées...génerer les DiplAnneeCtrlr nécessaires
		if (toutesLesInscriptions != null && toutesLesInscriptions.count()>0) {
			NSMutableDictionary listeDiplAnneeParAnneeUniv = new NSMutableDictionary();
			java.util.Enumeration enumerator = toutesLesInscriptions.objectEnumerator();
			while (enumerator.hasMoreElements()) {
				EOGenericRecord insc = (EOGenericRecord)enumerator.nextElement();
				/* ATTENTE
				InscDiplAnneeCtrlr diplAnneeCtlr = new InscDiplAnneeCtrlr(maSession, insc, preEtudiant != null);
				 */
				InscDiplAnneeCtrlr diplAnneeCtlr = new InscDiplAnneeCtrlr(maSession, insc, false);
				Integer anneeUniv = diplAnneeCtlr.anneeUniv();
				NSMutableArray listeDiplAnnee = (NSMutableArray) listeDiplAnneeParAnneeUniv.objectForKey(anneeUniv);
				if (listeDiplAnnee == null) {
					listeDiplAnnee = new NSMutableArray();
					listeDiplAnneeParAnneeUniv.setObjectForKey(listeDiplAnnee, anneeUniv);
				}
				listeDiplAnnee.addObject(diplAnneeCtlr);
			}
			tousLesDiplAnneeCtrlParAnneeUniv = listeDiplAnneeParAnneeUniv.immutableClone();
		}
	}

	public NSDictionary getTousLesDiplAnneeCtrlParAnneeUniv() {
		if (tousLesDiplAnneeCtrlParAnneeUniv == null) {
			Integer leEtudNumero = (Integer)etudiant.valueForKey("etudNumero");
			chargerToutesLesInscriptionsParAnnee(leEtudNumero);
		}
		return tousLesDiplAnneeCtrlParAnneeUniv;
	}
	
}
