package org.cocktail.ipweb.serveur.controlleur;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.HashMap;

import org.cocktail.ipweb.serveur.Session;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSLog;

import er.extensions.eof.ERXEC;


/// PO le 27/02/08
//objectif : générer un relevé de notes pour un étudiant...
//voacation à devenir un WebService à part, éventuellement.

public class ReleveNotes {

	protected Session maSession;
	
	private NSArray lignesRN;
	private EOGenericRecord laLigneRN;
	private Enumeration monEnum;

	private EOEditingContext nestedEC;
	
	public ReleveNotes(Session s) {
		super();
		maSession = s;
		
		/// On utilise un nested editing context à ce niveau... pour ne pas interféfer !
		nestedEC = ERXEC.newEditingContext(maSession.defaultEditingContext());
		
	}
	
	
	public NSData imprRN (Integer rnEtudNumero, Integer rnFannKey, Integer rnFspnKey,Integer rnIdiplAnneeSuivie,
			Integer rnMrsemKey, Integer rnMsemKey, Integer rnMsemOrdre,
			int myPeriode, String myVille, String myDiplome) {
		
		NSData res = null;
		
		// lance d'abord la proc stockée + fetch et retraitement des données pour édition du RN
		NSArray myResult = lancerRN ( rnEtudNumero,  rnFannKey,  rnFspnKey, rnIdiplAnneeSuivie, rnMrsemKey,  rnMsemKey,  rnMsemOrdre, myPeriode);
		
		// création du report avec les données produites... s'il y en a !
		if (myResult != null) {
			// constituer liste des parametres...
			String nomEtab = maSession.monApp.nomEtablissement();
			String mySemestre;
			if (rnMsemOrdre == null) mySemestre = "";
			else mySemestre = "du Semestre "+rnMsemOrdre.intValue();
			//String nomEtab = "Université l'ondulèêé";

			// NSLog.out.appendln("************************ Nom etab à imprimer =   |"+ maSession.monApp.nomEtablissement()+"|");

			HashMap parametres = new HashMap();
			parametres.put("lAnnee", ""+rnFannKey);
			parametres.put("leDiplome", myDiplome);
			parametres.put("laVille", myVille);
			parametres.put("laSession",""+myPeriode); 
			parametres.put("laPeriode",mySemestre);
			parametres.put("leNomEtab",nomEtab);
			

			// Si on est en mode BackOffice, imprimer un report avec partie attestation...
			// sinon report des choix pedag habituel...

			String nomReport = "releveDeNotes_modA.jasper";

			lignesRN = myResult;
			
			res = maSession.imprimePDFavecDataSource(nomReport, 
					parametres,
					new JRDataRN(this));
			// créer une source de données Jaser basée sur les objets de ce controleur !

			// Le pdf est créé, on peut faire une revert sur la vue "bidouillée à la mano" dans le nested...
			try {
				nestedEC.lock();
				nestedEC.revert();  
			}
			catch(Exception e) {
				// pb de fetch concurrent (1 autre étudiant essaie déjà de sortir un RN ?!)
				nestedEC.invalidateAllObjects();
			}
			finally {
				nestedEC.unlock();
			}
		}
		return res;
	}
		
	// Appel de la proc stockée + fetch et retraitement des données pour édition du RN
	public NSArray lancerRN (Integer rnEtudNumero, Integer rnFannKey, Integer rnFspnKey,Integer rnIdiplAnneeSuivie,
			Integer rnMrsemKey, Integer rnMsemKey, Integer rnMsemOrdre,
			int myPeriode) {

		// Appel de la proc stockée pour calcul du RN dans Scolarite ...
		NSArray clefs = new NSArray(new Object[] {"10_fannkey","20_fspnkey", "30_idiplanneesuivie", 
				"40_mrsemkey", "50_msemkey", "60_msemordre","70_bcaletat", "80_etudnumero", "90_mectype"});
		NSArray vals = new NSArray(new Object[] {rnFannKey ,rnFspnKey , rnIdiplAnneeSuivie ,rnMrsemKey ,
				rnMsemKey, rnMsemOrdre, new Integer(myPeriode), rnEtudNumero, new Integer(1)});

		NSDictionary dico = new NSDictionary(vals,clefs);

		NSArray myResult = null;
		
		if (maSession.execProc("pLmdInsXreleve0",dico)) {
			
		
			// appel a fonctionné... on continue !!!
			// NSLog.out.appendln("On a lancé le relevé de notes pour l'étudiant" + rnEtudNumero +", année " +rnFannKey
			//		+", fspn " + rnFspnKey + " annee suivie " +rnIdiplAnneeSuivie);

			// Préparation du fetch...
			///////////////////////
			NSArray bindings = new NSArray(new Object[] {rnFannKey ,rnFspnKey , rnIdiplAnneeSuivie ,rnMrsemKey,rnEtudNumero});
			EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
					"fannKey = %@ and fspnKey = %@ and idiplAnneeSuivie = %@ and mrsemKey = %@ and etudNumero = %@", bindings);

			EOSortOrdering ordre = EOSortOrdering.sortOrderingWithKey("xresKey",
	    			EOSortOrdering.CompareAscending);    	
			NSArray sortOrdering = new NSArray(new Object[] {ordre});

			EOFetchSpecification fetchSpec = new EOFetchSpecification("ScolExportationRelevesSem",qualifier, sortOrdering);

			try {
				nestedEC.lock();
				myResult = nestedEC.objectsWithFetchSpecification(fetchSpec);  
			}
			catch(Exception e) {
				// pb de fetch concurrent (1 autre étudiant essaie déjà de sortir un RN ?!)
				return null;
			}
			finally {
				nestedEC.unlock();
			}
			
			// retraiter les données issues de l'appel précédent... (traitement dépend des options chaisies dans ScolPeda : ici le minimum !!!
			///////////////////////////////////////////////////////

			// Constantes :
			String myBonification = "Points Jury";
			
			// selon la session d'examen : 
			if (myPeriode == 1) {
				// pour ttes les rows...
				for (int i=0; i < myResult.count(); i++) {

					int vimrecEtat = maSession.monApp.recuperer1int((EOGenericRecord)myResult.objectAtIndex(i), "imrecEtat");
					int vimrecAbsence1 = maSession.monApp.recuperer1int((EOGenericRecord)myResult.objectAtIndex(i), "imrecAbsence1"); 
					int vimrecMention1 = maSession.monApp.recuperer1int((EOGenericRecord)myResult.objectAtIndex(i), "imrecMention1"); 
					double vimrecMoyenne1 = maSession.monApp.recuperer1double((EOGenericRecord)myResult.objectAtIndex(i), "imrecMoyenne1");
					double vimrecBase = maSession.monApp.recuperer1double((EOGenericRecord)myResult.objectAtIndex(i), "imrecBase");

					if (vimrecEtat== 2 || 
							(vimrecEtat == 0 
									&& (vimrecAbsence1 != 0 || vimrecMention1 == 6
											|| (vimrecMention1 == 0 && vimrecMoyenne1*2.0 < vimrecBase ))))
					{
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey(null,"imrecPoints1");
					}

				}

			}
			else if (myPeriode == 2) {
				for (int i=0; i < myResult.count(); i++) {
					int vimrsemEtat = maSession.monApp.recuperer1int((EOGenericRecord)myResult.objectAtIndex(i), "imrsemEtat"); 
					int vimrueEtat = maSession.monApp.recuperer1int((EOGenericRecord)myResult.objectAtIndex(i), "imrueEtat"); 
					int vimrecEtat = maSession.monApp.recuperer1int((EOGenericRecord)myResult.objectAtIndex(i), "imrecEtat"); 

					if (vimrsemEtat != 1) {
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrsemAbsence2")), "imrsemAbsence1");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrsemMoyenne2")), "imrsemMoyenne1");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrsemPoints2")), "imrsemPoints1");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrsemMention2")), "imrsemMention1");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrsemBaseLibelle22")), "imrsemBaseLibelle21");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrsemMentionLibelle2")), "imrsemMentionLibelle1");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrsemMentionLibelleCourt2")), "imrsemMentionLibelleCourt1");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrsemNote2")), "imrsemNote1");
					}

					if (vimrueEtat != 1) {
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrueAbsence2")), "imrueAbsence1");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrueMoyenne2")), "imrueMoyenne1");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imruePoints2")), "imruePoints1");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrueMention2")), "imrueMention1");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrueMentionLibelle2")), "imrueMentionLibelle1");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrueNote2")), "imrueNote1");
					}

					if (vimrecEtat != 1) {
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrecAbsence2")), "imrecAbsence1");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrecMoyenne2")), "imrecMoyenne1");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrecPoints2")), "imrecPoints1");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrecMention2")), "imrecMention1");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrecMentionLibelle2")), "imrecMentionLibelle1");
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrecNote2")), "imrecNote1");
					}
					
					int vimrecAbsence2 = maSession.monApp.recuperer1int((EOGenericRecord)myResult.objectAtIndex(i), "imrecAbsence2");  
					int vimrecMention2 = maSession.monApp.recuperer1int((EOGenericRecord)myResult.objectAtIndex(i), "imrecMention2");  
					double vimrecMoyenne2 =  maSession.monApp.recuperer1double((EOGenericRecord)myResult.objectAtIndex(i), "imrecMoyenne2");
					double vimrecBase = maSession.monApp.recuperer1double((EOGenericRecord)myResult.objectAtIndex(i), "imrecBase"); 

					if (vimrecEtat== 0 
							&& (vimrecAbsence2 != 0 || vimrecMention2 == 6 
									|| (vimrecMention2 == 0 && vimrecMoyenne2*2.0 < vimrecBase )))
					{
						((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey(null,"imrecPoints1");
					}

					
				}
			}
			
			DecimalFormat df1 = new DecimalFormat("0.00");
			
			// Autres traitements pour ttes les rows...
			for (int i=0; i < myResult.count(); i++) {

				// traitement mentions :
				((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey(
						(((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrsemMentionLibelleCourt1")), "imrsemMentionLibelle1");
				((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey(
						(((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrsemMentionLibelleCourt2")), "imrsemMentionLibelle2");
				
				// traitement semestre :
				String myString = "";
				
				BigDecimal vimrsemPointJury = (BigDecimal)(((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrsemPointJury"));
				BigDecimal vimrsemPonderation = (BigDecimal)(((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imrsemPonderation"));
				
				if (vimrsemPointJury != null) {
//					myString = String.format("dont %.2f Points Jury", new Object[] {vimrsemPointJury});
					myString = "dont "+ df1.format(vimrsemPointJury) +" Points Jury";
				}

				if (vimrsemPonderation != null) {
					if (myString.length() > 0) {
//						myString = String.format("- %@ %.2f", new Object[] {myBonification, vimrsemPonderation});
						myString = "- "+myBonification+" "+ df1.format(vimrsemPonderation);
					}
					else {
//						myString = String.format("dont %@ %.2f", new Object[] {myBonification, vimrsemPonderation});
						myString = "dont "+myBonification+" "+ df1.format(vimrsemPonderation);
					}
				}
				
				((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey(myString, "imrsemBonification");

				// traitement UE :
				myString = "";
				
				int vmrecKey = maSession.monApp.recuperer1int((EOGenericRecord)myResult.objectAtIndex(i), "mrecKey"); 
				
				if (vmrecKey == 0) {
					EOGenericRecord lUeDest = (EOGenericRecord)(((EOGenericRecord)((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("toScolMaquetteRepartitionUeX")).valueForKey("toScolMaquetteUe"));
					if (lUeDest != null) ((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey(((Double)lUeDest.valueForKey("muePoints")),"imruePointsUe");
				}
				else {
					EOGenericRecord lUeDest = (EOGenericRecord)(((EOGenericRecord)((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("toScolMaquetteRepartitionUe")).valueForKey("toScolMaquetteUe"));
					// Est-ce que l'UE de destination existe bien ? (cas des reports de notes avec conservation de notes des maquettes antérieures, qui font planter le truc !)
					if (lUeDest != null) ((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey((lUeDest.valueForKey("muePoints")),"imruePointsUe");
				}
				
				BigDecimal vimruePointJury = (BigDecimal)(((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imruePointJury"));
				BigDecimal vimruePonderation = (BigDecimal)(((EOGenericRecord)myResult.objectAtIndex(i)).valueForKey("imruePonderation"));
				
				if (vimruePointJury != null) {
//					myString = String.format("dont %.2f Points Jury", new Object[] {vimruePointJury});
					myString = "dont "+df1.format(vimruePointJury)+" Points Jury";
				}

				if (vimruePonderation != null) {
					if (myString.length() > 0)
//						myString = String.format("- %@ %.2f", new Object[] {myBonification, vimruePonderation});
						myString = "- "+myBonification+" "+ df1.format(vimruePonderation);
					else
//						myString = String.format("dont %@ %.2f", new Object[] {myBonification, vimruePonderation});
						myString = "dont "+myBonification+" "+ df1.format(vimruePonderation);
				}
				
				((EOGenericRecord)myResult.objectAtIndex(i)).takeValueForKey(myString, "imrueBonification");

			}
			
			// a suivre	
			/// tout à la fin : lancer pdf
			for (int i=0; i < myResult.count(); i++) {
				NSLog.out.appendln("Ligne " +i+ ":" +myResult.objectAtIndex(i));
			}
		}
		return myResult;
	}
	
	// **************** PARTIE nécessaire à JasperReports pour inmprimer les données du RN en mémoire...
	
    public void resetBoucle() {
//    	System.out.println("> premiere UE");
    	monEnum = lignesRN.objectEnumerator();
        }
	
    public boolean nextElement() {
    	boolean pasFini = monEnum.hasMoreElements();
    	if (pasFini) laLigneRN = (EOGenericRecord)monEnum.nextElement();
    	return pasFini;
    }

    public Object fetchJRChamp(String jrName) throws com.webobjects.foundation.NSKeyValueCoding.UnknownKeyException {
    	Object res = null;
// On est censé demander des colonnes existantes... sinon prévenir !!!
    	try {
    		res = laLigneRN.valueForKey(jrName);
    		// NSLog.out.appendln("Champ : "+jrName+",valeur : "+res);
    	}
    	catch (com.webobjects.foundation.NSKeyValueCoding.UnknownKeyException e) {
			NSLog.out.appendln("ATTENTION :  le Jasper a besoin d'une valeur pour la colonne INCONNUE : "+jrName+" !!!");
			throw e;
		}
    	
    	return res;
    }

}
