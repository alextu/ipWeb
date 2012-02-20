
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOComponent;

import fr.univlr.cri.webapp.*;

public class Mod_envoiMail extends CRIWebComponent {

    
    public String nomFormChoix = "formulaireChoixTypeEnvoi";
    public EnvoiMailCtrl monEnvoiMailCtrl;
	   
	private static String DECO_NONSEL = "nonselected"; 
	private static String DECO_SEL = "selected"; 
	
	private boolean redactionPossible;	// Si vrai, alors on peut rédiger un nouveau mail...
 
    public boolean listeInscAuxEc;	// Si vrai, on s'intéresse aux inscrits à des EC (sinon inscrits à 1 diplôme et 1 année)
    public String messageErreur, messageEnvoiOk;
    public boolean affBtContinuer, affBtConfirmer, affBtRevenir, envoiOk, bloqueSaisieMail;
    
	public Mod_envoiMail(WOContext context) {
        super(context);
        
    	monEnvoiMailCtrl = new EnvoiMailCtrl((Session)session());
        listeInscAuxEc = false;	// Par defaut : on s'interesse aux inscrits par dipl/année...       

    }
	
	 // Retourne un submit du formulaire en cours, à la sélection du bouton Radio ...
    public String fctSubmitChoixBR() {
        return "document."+nomFormChoix+".submit();";
    }         
    
    public WOComponent confirmerSelectionDiplAnnee() {

    	// On lance un fetch de la liste des inscrits au diplôme sélectionné...
    	monEnvoiMailCtrl.selectionDiplAnnee();
    	redactionPossible = true;
    	messageErreur = null;
    	return null;
    }

    
    // On vient de choisir une EC : Il faut récupérer la liste des inscrits et l'afficher...
    public WOComponent confirmerSelectionEc() {
    	// On va pouvoir récupérer les inscrits à cet EC, tous diplômes confondus...
    	monEnvoiMailCtrl.selectionEc();
    	
    	// Y-a-til des inscrits à cet EC ? Sinon le signaler par un message d'erreur...
    	if (monEnvoiMailCtrl.nbreInscritsEC()>0) {
    		redactionPossible = true;
        	messageErreur = null;
    	}
    	else {
    		messageErreur = "Aucun inscrit à cet EC... Pas d'envoi de mail possible.";
        	redactionPossible = false;
    		}
    	return null;    	
    }
    
    // A faire systématiquement... 
 /*   public void awake() {
		messageErreur = null;
		bloqueSaisieMail = false;
    }
*/
    //  --------------------------------------------------------    
    //  ------------- Gestion de l'envoi de mail ---------------
    //  --------------------------------------------------------  
    
    public WOComponent validerEnvoiMail() {   
    	affBtRevenir = false;
    	affBtConfirmer = false;
    	affBtContinuer = false;
    	// Avant d'envoyer, il faut contrôler que les champs SUJET et TEXTE du mail sont tous les deux renseignés...
    	if (monEnvoiMailCtrl.texteMail == null || monEnvoiMailCtrl.texteMail.length()==0) {
    		messageErreur = "N'auriez-vous pas cliqué sur 'Envoyer' par erreur ? Vous n'avez rien saisi dans le corps de votre mail...";
        	affBtContinuer = true;
    		bloqueSaisieMail = true;
       	}
    	else if (monEnvoiMailCtrl.sujetMail == null || monEnvoiMailCtrl.sujetMail.length() == 0) {
    		messageErreur = "Merci de bien vouloir préciser le sujet de votre mail";
        	affBtContinuer = true;
    		bloqueSaisieMail = true;
    	}
    	else { 
    		
    		bloqueSaisieMail = true;
        	// redactionPossible = false;
        	boolean envoi = monEnvoiMailCtrl.validerEnvoiMail(listeInscAuxEc);
        	if (envoi) {
        		messageErreur = null;
        		envoiOk = true;
        		messageEnvoiOk = "Votre mail a bien été envoyé...";
        	}
        	else {
        		messageErreur = "Une erreur s'est produite à l'envoi de votre mail. Merci de prévenir le CRI !";
            	affBtContinuer = true;
        	}
    	}
    	return null;    	    
    }

    public WOComponent nouveauMail() {
    	envoiOk = false;
    	monEnvoiMailCtrl.nouveauMail();
		messageErreur = null;
		bloqueSaisieMail = false;
    	return null;    	    
    }
    
    
    public WOComponent annulerEnvoiMail() {   
    	affBtConfirmer = false;
    	affBtContinuer = false;
    	affBtRevenir = false;
    	// redactionPossible = false;
    	
    	// Signaler si un sujet et un message ont été rédigé, avant de supprimer
    	if (monEnvoiMailCtrl.texteMail != null && monEnvoiMailCtrl.sujetMail != null) {
    		messageErreur = "Confirmez-vous l'abandon du mail en cours de rédaction ?";
    		affBtConfirmer = true;
        	affBtRevenir = true;    		
    		bloqueSaisieMail = true;
    	}
    	else {    	
    		monEnvoiMailCtrl.annulerEnvoiMail();
    	}
    	return null;    	    
    } 
    
    // appelé quand le sujet et le corps du mail étaient non nuls...
    public WOComponent confirmerAnnulationEnvoiMail() {
    	affBtConfirmer = false;
    	affBtContinuer = false;
    	affBtRevenir = false;
		messageErreur = null;
		bloqueSaisieMail = false;
		monEnvoiMailCtrl.annulerEnvoiMail();
		return null;    	    
    }
    
    public WOComponent continuerRedaction() {
    	affBtContinuer = false;
    	affBtConfirmer = false;
    	affBtRevenir = false;
		messageErreur = null;
		bloqueSaisieMail = false;
		return null;    	
    }
    

    
    public boolean phaseRedactionMail() {
    	return (monEnvoiMailCtrl.selectionDestinataires(listeInscAuxEc) && redactionPossible);
    }
    
    //  --------------------------------------------------------       
    //  --------------------Valeurs à afficher -----------------
    //  --------------------------------------------------------    

    public String titreFieldSet() {
    	if (listeInscAuxEc) return "un EC";
    	else return "une formation";
    }

    //  --------------------------------------------------------    
    //  ---------------- Valeurs en E/S      -------------------
    //  --------------------------------------------------------    

    // récupérer la valeur d'une case à cocher...
    public String getBrSelected()
    {
    	if (listeInscAuxEc) return "1";
    	else return "0";
    }
    
    // modifier la valeur d'une case à cocher ...
    public void setBrSelected(Integer newBrSelected)
    {
    	if (newBrSelected.intValue() == 0) listeInscAuxEc = false;
    	else listeInscAuxEc = true;
    }

	public String styleRB1() {
		if (listeInscAuxEc) return DECO_NONSEL;
		else return DECO_SEL;
	}

	public String styleRB2() {
		if (listeInscAuxEc) return DECO_SEL;
		else return DECO_NONSEL;
	}

    //  --------------------------------------------------------    
    //  ---------------- Réponse aux tests   -------------------
    //  --------------------------------------------------------    
	
	public boolean existeMessageErreur() {
		return (messageErreur != null && messageErreur.length() > 0);
	}
	
	public boolean existeInscritsEC() {
		return (listeInscAuxEc && monEnvoiMailCtrl.nbreInscritsEC()>0);
	}
	
	
}