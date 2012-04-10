package org.cocktail.ipweb.serveur.controlleur;
import com.webobjects.foundation.*;

/*
 * Cr�� le 6 oct. 2006
 *
 * @author olive
 *
 *	Objectif : classe charg�e de g�rer les relations entre EC (exclusion des EC ayant m�me code EC) 
 *				=> le but est de stocker la liste (2 ou +) de ctrlr d'EC en relation de m�me type
 *				   cette classe va ensuite se charger du contr�le des relations et des csqces � g�rer (mise en erreur...)
 *
 *	
 * */
public class RelationChoixEc {

	protected NSArray relationEc;			// liste des EC surlesquels portent cette relation...
	private InscEcCtrlr ecChoixBloque;		// ref sur ctrlr d'EC � choix bloqu� par une IP appartenant � la relation
	protected InscEcCtrlr dernierEcEnErreur;		// ref sur ctrlr d'EC de la relation ou la derni�re erreur s'est produite
	
	private boolean choixIncoherentCoche;	// tq vrai, il faudra r�guli�rement scanner la liste des choix incoh�rents coch�s 
											//	pour mettre � jour les EC � masquer si choix incoh�rent d�coch�...
	private String lesUeAvecEcChoisisEnDouble;	// censer lister les UE o� des EC avec m�me mec_code ont �t� coch�s
	
	// premier constructeur...
	public RelationChoixEc(NSArray listeEcCT) {
		super();
		relationEc = listeEcCT;
		ecChoixBloque = chercherEcChoixBloque();
		scannerChoixAMasquer();
	}

	public RelationChoixEc() {
		// init pour cette classe et ses sous-classes...
	}
	
	// chercher l'�ventuel ctrlr d'EC � choix bloqu� par une IP appartenant � la relation
	protected InscEcCtrlr chercherEcChoixBloque() {
		java.util.Enumeration enumerator = relationEc.objectEnumerator();
		while (enumerator.hasMoreElements()) {
			InscEcCtrlr ecCt = (InscEcCtrlr)enumerator.nextElement();
			if (ecCt.isEcBloque())
				return ecCt;
		}
		return null;
		
	}
	
	
	// chercher si cette relation n'implique pas des choix ne devant pas �tre propos�s � l'�tudiant
	// par ex. si exclusion et un des choix est d�j� fait (IP ds ScolPeda)
	//			alors les autres Ec doivent �tre masqu�es s'il n'ont pas d�j� �t� choisi par l'�tudiant !
	public void scannerChoixAMasquer() {
		if(ecChoixBloque!= null) {
			// v�rif si ce choix est possible ou doit �tre masqu�...
			choixIncoherentCoche = false;
			java.util.Enumeration enumerator = relationEc.objectEnumerator();
			while (enumerator.hasMoreElements()) {
				InscEcCtrlr ecCt = (InscEcCtrlr)enumerator.nextElement();
				if(!ecCt.isEcBloque()) {
					if (!ecCt.getCaseCochee()) ecCt.masquerEc();
					else choixIncoherentCoche = true;	// faudra repasser lors de la prochaine v�rif... 
				}
			}
		}
	}
	
	public boolean choixIncoherentCoche() { return choixIncoherentCoche; }
	
	public int compteNbreEcCoches() {
		// indique combien d'EC de la relation sont coch�s ou porte une IP
		StringBuffer sb= new StringBuffer();	// lister les UE ou d'autres EC de m�me code sont coch�s...
		boolean premiereUe = true;
		
		java.util.Enumeration enumerator = relationEc.objectEnumerator();
		int nbEcChoisis = 0;
		while (enumerator.hasMoreElements()) {
			InscEcCtrlr ecCt = (InscEcCtrlr)enumerator.nextElement();
			if (ecCt.getCaseCochee() || ecCt.isEcBloque()) {
				nbEcChoisis++;
				if (!premiereUe) sb.append(", ");
				else premiereUe = false;
				sb.append(ecCt.getOrdreUe());
			}
		}
		lesUeAvecEcChoisisEnDouble = sb.toString();
		return nbEcChoisis;
	}
	
//	 v�rifier la relation en cours si une erreur n'existerait pas pour les EC qu'elle contient..
//  MODIF : on signale aussi sur les EC anciennement s�lectionn�es (on sait pas ce que l'�tudiant veut vraiment faire...) 

	public boolean verifierRelation() throws Exception {
		if (compteNbreEcCoches()>1) {	// la relation n'est pas v�rifi�e !
			dernierEcEnErreur = null;
			String errMsg=null;
			java.util.Enumeration enumerator = relationEc.objectEnumerator();
			while (enumerator.hasMoreElements()) {
				InscEcCtrlr ecCt = (InscEcCtrlr)enumerator.nextElement();
				if (!ecCt.isEcBloque()) {	// MAJ msg d'erreur 
					if (ecCt.getCaseCochee()) {	// cas d'erreur
						dernierEcEnErreur = ecCt;
						if (ecChoixBloque!= null) {	// un EC � choix est bloqu� avec une IP, l'autre choix est donc � supprimer !
							errMsg = "Le choix de cet EC est impossible (déjà validé dans l'UE \""+ecChoixBloque.getCodeUePere()
							+"\"). Décochez ce choix !";
							ecCt.setErreur(errMsg);
						}
						else {	// sinon il s'agit de 2 (ou +) choix modifiables ...
							errMsg = "Cet EC est choisi dans plusieurs UE ("+
							lesUeAvecEcChoisisEnDouble+
							")... Corrigez un des choix !";
							
							ecCt.setErreur(errMsg);						
						}
					}
					else {
						ecCt.enleveErreur();
					}
				}
			}
			return false;
		}
		return true;
	}
		
	
	public String derniereUeAvecErreur() {
		return dernierEcEnErreur.getUeKey();
	}
		

}
