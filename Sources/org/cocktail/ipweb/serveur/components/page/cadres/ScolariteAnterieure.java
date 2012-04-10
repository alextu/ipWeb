package org.cocktail.ipweb.serveur.components.page.cadres;

import java.util.Arrays;
import java.util.Collections;

import org.cocktail.fwkcktlwebapp.server.components.CktlWebComponent;
import org.cocktail.ipweb.serveur.Application;
import org.cocktail.ipweb.serveur.Session;
import org.cocktail.ipweb.serveur.controlleur.DownloadFic;
import org.cocktail.ipweb.serveur.controlleur.IndividuCtrlr;
import org.cocktail.ipweb.serveur.controlleur.InscDiplAnneeCtrlr;
import org.cocktail.ipweb.serveur.controlleur.InscFormationCtrlr;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;

public class ScolariteAnterieure extends CktlWebComponent {
	
	private NSArray lesAnneesUniv;
	private NSDictionary lesInscriptionsParAnnee;
    public InscDiplAnneeCtrlr currentInsc;
    public InscFormationCtrlr currentInscForm;
    private boolean autorisationSortieRN;
    private Integer currentAnnee;
    
    public ScolariteAnterieure(WOContext context) {
        super(context);
        autorisationSortieRN = myApplication().autorisationSortirRN();
    }
    
    @Override
    public boolean synchronizesVariablesWithBindings() {
    	return false;
    }
    
    private Session mySession() {
    	return (Session)session();
    }
    
    private Application myApplication() {
    	return (Application)WOApplication.application();
    }
    
    // Est-ce qu'un RN est disponible ???
    public boolean rnDispo() {
    	return (currentInscForm.getVisibiliteRN()>0 && autorisationSortieRN);
    }
    
    public int rnSessionDispo() {
    	int sessionExam = currentInscForm.getImrsemEtat();
    	if (sessionExam == 0) sessionExam = currentInscForm.getVisibiliteRN();
    	else if (sessionExam > currentInscForm.getVisibiliteRN() ) sessionExam = 1;
    	return sessionExam;
    }
    
	public String getMsgRNSem() {
		StringBuffer sb = new StringBuffer("return overlib('Cliquez sur ce lien pour visualiser votre relevé de notes de session " +rnSessionDispo()+" pour le semestre " + currentInscForm.getMsemOrdre() +"'");
		sb.append(",CAPTION,'Info :');");
		return sb.toString();
	}
	
	public WOActionResults back() {
		return (WOActionResults)valueForBinding("onPrevious");
	}
    
    // On a demandé le relevé de notes !!!
    // dans la foulée, envoi a l'étudiant de ce document par mail... pour qu'il puisse le consulter + tard !
    public DownloadFic lancerRN()
    {
    	DownloadFic nextPage = (DownloadFic)pageWithName("DownloadFic");
    	// On passe les éléments controleur...
    	String nomFichier = "ReleveDeNotes.pdf";

    	// ETUD_NUMERO : 
    	Integer rnEtudNumero = (Integer)((currentInscForm.eoInscEtud()).valueForKey("etudNumero"));
    	
    	// FANN_KEY : 
    	Integer rnFannKey = currentInscForm.anneeUniv();
    	
    	// FSPN_KEY :
    	Integer rnFspnKey = currentInscForm.fspnKey();
    	
    	// IDIPL_ANNEE_SUIVIE :
    	Integer rnIdiplAnneeSuivie = (Integer)((currentInscForm.eoInscEtud()).valueForKey("idiplAnneeSuivie"));
    	
    	// MRSEM_KEY :
    	Integer rnMrsemKey = currentInscForm.mrsemKeyPS();
    	
    	// MSEM_KEY :
    	Integer rnMsemKey = currentInscForm.msemKeyPC();
    	
    	// MSEM_ORDRE :
    	Integer rnMsemOrdre = currentInscForm.getMsemOrdre();
    	
    	// session :
    	int sessionExam = rnSessionDispo();

    	// Envoie des infos nécessaires pour sortie du RN :
    	// Libellé du diplome :
    	String diplome = currentInscForm.diplomeLl();

    	// génération du PDF... 
    	// TODO : paramétrer la VILLE !
    	// lance d'abord la proc stockée + fetch et retraitement des données pour édition du RN
    	NSData lesDatas = mySession().getReleveNotes().imprRN(rnEtudNumero, rnFannKey, rnFspnKey, rnIdiplAnneeSuivie, 
    			rnMrsemKey, rnMsemKey, rnMsemOrdre, sessionExam,"Nouméa",diplome);

    	// On loggue cette action pour cet étudiant...
    	((Session)session()).logSortieRN(currentInscForm.idiplNumero(), rnEtudNumero, rnFannKey, rnMrsemKey, rnMsemOrdre);
    	

    	// 1) envoi par mail [si c'est autorisé : appli en exploit° seulement !]

    	IndividuCtrlr UserReel = ((Session)session()).getIndividuCtrlr();
    	if (UserReel != null && UserReel.estUnEtudiant()) {
    		String semestre = "le semestre " + rnMsemOrdre + " de " + currentInscForm.diplome();

    		String leMsg = "Vous avez demandé une copie de votre relevé de notes pour "+semestre+", session " + sessionExam + " ("+ currentInscForm.anneeUniv() +").";
    		leMsg = leMsg+"\nVous trouverez en pièce jointe à ce mail le document récapitulatif édité à cette occasion.";
    		leMsg = leMsg+"\nATTENTION : il ne s'agit pas du relevé de notes officiel, qui doit être demandé au service de la Scolarité en cas de besoin !";
    		leMsg = leMsg+"\n\n[Ceci est un message automatique de l'application IP Web - une réponse à cette adresse ne sera pas lue.";
    		leMsg = leMsg+"\nEn cas de problème sur les données de votre inscription, veuillez contacter votre secrétariat pédagogique...]";

    		mySession().envoitMail("IPWeb : extrait de relevé de Notes",leMsg, nomFichier, lesDatas);
    	}    	
    	
    	// 2) download page pour affichage sur Acrobat Reader du poste client...

    	boolean res = nextPage.initDownloadPDF(lesDatas,nomFichier);
    	if (!res) return null;
    	else return nextPage;
    }

    @Override
    public void appendToResponse(WOResponse arg0, WOContext arg1) {
    	setLesInscriptionsParAnnee((NSDictionary)valueForBinding("lesInscriptionsParAnnee"));
    	super.appendToResponse(arg0, arg1);
    }
    
    public NSArray lesInscriptionsForCurrentAnnee() {
    	return (NSArray) getLesInscriptionsParAnnee().objectForKey(getCurrentAnnee());
    }
    
    public NSDictionary getLesInscriptionsParAnnee() {
		return lesInscriptionsParAnnee;
	}
    
    public void setLesInscriptionsParAnnee(NSDictionary lesInscriptionsParAnnee) {
		this.lesInscriptionsParAnnee = lesInscriptionsParAnnee;
		if (lesInscriptionsParAnnee != null) {
			Object[] annees = lesInscriptionsParAnnee.allKeys().objects();
			Arrays.sort(annees, Collections.reverseOrder());
			NSMutableArray lesAnneesUnivTmp = new NSMutableArray(annees);
			// On enlève l'année courante
			lesAnneesUnivTmp.removeObject(Integer.valueOf(myApplication().getAnneeUnivEnCours()));
			lesAnneesUniv = lesAnneesUnivTmp.immutableClone();
		}
	}

    public NSArray getLesAnneesUniv() {
		return lesAnneesUniv;
	}
   
    
    public InscDiplAnneeCtrlr getCurrentInsc() {
		return currentInsc;
	}
    
    public void setCurrentInsc(InscDiplAnneeCtrlr currentInsc) {
		this.currentInsc = currentInsc;
	}
    
    public InscFormationCtrlr getCurrentInscForm() {
		return currentInscForm;
	}
    
    public void setCurrentInscForm(InscFormationCtrlr currentInscForm) {
		this.currentInscForm = currentInscForm;
	}

    public Integer getCurrentAnnee() {
		return currentAnnee;
	}
    
    public void setCurrentAnnee(Integer currentAnnee) {
		this.currentAnnee = currentAnnee;
	}
    
}