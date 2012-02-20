import java.awt.Image;
import javax.swing.ImageIcon;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSPathUtilities;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSTimestampFormatter;

import fr.univlr.cri.webapp.CRIDataBus;
import fr.univlr.cri.webapp.CRIWebSession;

/*
 * Cr�� le 25 juin 2006
 * Objectif = g�rer tous les aspects li�s au fetch/update de la photo :
 * - r�cup depuis la base de l'image
 * - calcul des coordonn�es d'affichage (petite taille et taille normale)
 * - envoi des donn�es images pour affichage dans la page Web
 * 
 * classe � adapter (moins de manips � faire que dans projet Profil...) 
 */

/**
 * @author olive
 *
 */
public class PhotoCtrlr {

	private Photo laPhoto;
	private Photo laPhotoCandidate;	// buffer temp pour la Photo t�l�charg�e par l'utilisateur
									// pour remplacer myPict s'il valide la modif... 
	
	private EOGenericRecord eoPhoto;	// l'EO d'ou est tir� la photo...

	/**
	 * Liste des tables dans lesquelles aller chercher l'image
	 * (suppose que les tables contiennent les bons champs)
	 */
	private NSArray lookupTables = NSArray.componentsSeparatedByString("PhotosEmployesGRHUM,PhotosEtudiantsGRHUM,PhotosEtudiantsOldGRHUM",",");    	
	/**
	 * Permet de savoir si l'enregistrement contenant la photo : 
	 *  - a �t� fetch�
	 *  - a �t� retrouv�e
	 *  - est en passe d'�tre Upload�
	 *  - a �t� upload�e
	 *  - a �t� stock� dans la base suite � l'upload 
	 */
	private boolean isPictFound=false;
	private boolean isPictFetche=false;
	private boolean isDdeUpload=false;	
	private boolean isPictUpload=false;	
	private boolean isPictStored=false;
	
	private Session laSession;
	
	/**
	 * donn�es renseigner par le composant d'UpLoad d'un fichier...
	 */
	public String aFileName;
	public String aMimeType;
	public NSData aFileContents = null;
	
	
	public PhotoCtrlr(Session uneSession) {
		super();
		// Enregistrer la r�f�rence � la session
		laSession = uneSession;
		eoPhoto = null;
		laPhoto = new Photo();
		laPhotoCandidate = new Photo();
        isPictFetche=false;
        isDdeUpload=false;
    	isPictUpload=false;	
    	isPictStored=false;
	}

	// au prochain affichage, refetche la photo...
	public void changePhoto() {
		isPictFetche = false;
		laPhoto = new Photo();
		laPhotoCandidate = new Photo();
	}
    
    public boolean isPictFound(){
    	if (!isPictFetche) {	// Si l'image n'a jamais �t� fetch�e, le faire now !
    		isPictFound = fetchPict();
    		isPictFetche = true;
    	}
    	return (isPictFound || isPictUpload);
    }

    public boolean isPictPTFound(){  // Pour la photo de Petite taille...
    	if (!isPictFetche) {	// Si l'image n'a jamais �t� fetch�e, le faire now !
    		isPictFound = fetchPict();
    		isPictFetche = true;
    	}
    	return isPictFound;
    }
    
    
    public boolean affSimple() { return (!isDdeUpload && !isPictUpload) ; }
    public boolean ddeUpload() { return (isDdeUpload) ; }
    public boolean valUpload() { return isPictUpload; }
    
    public void passerEnUpload() {
    	if (affSimple()) isDdeUpload=true;
    }
    
    public void annulerUpload() {
        isDdeUpload=false;
    	isPictUpload=false;
    	isPictStored=false;
    }
    
    
    /**
     * R�cup�re l'image dans la base de donn�es
     * ==> on prend en compte si la base photo est inaccessible
     * 
     */
    public boolean fetchPict() {
    	boolean vIsFound = false;
    	NSLog.out.appendln("**** deb fetchPict");
    	//on v�rifie si on est bien connect� � la base
    	if (CRIDataBus.isDatabaseConnected()) {
    		//Tenter de r�cup�rer la photo dans les �ventuelles diff�rentes
    		//tables sp�cifi�es dans lookupTables
    		String vCurrentLookupTable = "";

    		EOEditingContext ec = laSession.defaultEditingContext(); 
    		NSMutableDictionary lookup = new NSMutableDictionary();
    		// String vNoIndividu = String.valueOf(this.mySession.userInfo().noIndividu());
    		//   NSLog.out.appendln("**** vNoIndividu" + session().objectForKey("noIndividu"));

    		if (laSession.getICEtudiant() != null && laSession.getICEtudiant().getNoIndividu() != null) {

    			lookup.setObjectForKey(laSession.getICEtudiant().getNoIndividu(),"noIndividu");

    			java.util.Enumeration enumerator = this.lookupTables.objectEnumerator();

    			try {
    				while ((!vIsFound) && (enumerator.hasMoreElements())) {
    					vCurrentLookupTable = (String)enumerator.nextElement();
    					NSArray vResult = EOUtilities.objectsMatchingValues(ec,vCurrentLookupTable ,lookup);
    					if (vResult.count()>=1) {
    						vIsFound = true;
    						// conserver trace de l'EO d'ou vient la photo (MAJ future possible)
    						eoPhoto = (EOGenericRecord)vResult.objectAtIndex(0);
    						// stocke la photo
    						laPhoto.changePhoto((NSData)eoPhoto.valueForKey("datasPhoto"),
    								(NSTimestamp)eoPhoto.valueForKey("datePrise"));    				
    						isPictUpload=false;	
    						isPictStored=false;
    					}                      
    				}    
    			}
    			catch(Exception e) {
    				NSLog.err.appendln("Pb : la base photo n'est pas accessible ... pb de DBLink ?");
    				vIsFound = false;
    			}
    		}
    	} 
    	return vIsFound;
    }
    
    
    /**
     * Supprimer l'enregistrement correspondant dans Blob.photo...
     * 
    public void supprEnrPhoto() {
    	// v�rifier qu'il y a bien un EO � supprimer...
    	NSLog.debug.appendln("Suppr photo demand�e !");
    	if (eoPhoto != null) {
    		laSession.defaultEditingContext().deleteObject(eoPhoto);
    		laSession.defaultEditingContext().saveChanges();
			
			// Changer d'�tat...
			isPictFound = false;			
			isDdeUpload=false;
			isPictUpload=false;	    	
			isPictStored=false;
						
			// laiss� pour plus tard... apects LDAP � �lucider !
			    		
    	}
    }
    */
	   
/*
    // On a chang� d'individu, refetcher nvlle photo...quand on en aura besoin !
    public void changeIndividu() {
    	isPictFetche=false;
    	isPictUpload=false;
    	isPictUpload=false;	
    	isPictStored=false;
    }
 */   
    public int largeurPTPhoto() { return laPhoto.largeurPTPhoto(); }
    public int hauteurPTPhoto() { return laPhoto.hauteurPTPhoto(); }
    
    public int largeurTNPhoto() { 
    	if (isPictUpload && !isPictStored)	return laPhotoCandidate.largeurTNPhoto();
    	else return laPhoto.largeurTNPhoto(); 
    	}
    
    public int hauteurTNPhoto() { 
    	if (isPictUpload && !isPictStored) return laPhotoCandidate.hauteurTNPhoto();	
    	else return laPhoto.hauteurTNPhoto(); 
    	}
    
    public int largeurTBPhoto() { 
    	if (isPictUpload && !isPictStored) return laPhotoCandidate.largeurTBPhoto();	
    	else return laPhoto.largeurTBPhoto(); 
    }
    
    public int hauteurTBPhoto() { 
    	if (isPictUpload && !isPictStored) return laPhotoCandidate.hauteurTBPhoto();	
    	else return laPhoto.hauteurTBPhoto(); 
    }
    
    public NSData getMyPetitePict() {
    	return laPhoto.getMyPict();    	
    }
    
    public NSData getMyPict() {
    	if (isPictUpload && !isPictStored)	return laPhotoCandidate.getMyPict();
    	else return laPhoto.getMyPict();    	
    }
    
    public String getDatePrise() {
    	if (isPictUpload && !isPictStored)	return laPhotoCandidate.datePrise;
    	else return laPhoto.datePrise;    	    	
    }
    
    public Integer getPictLengthKB() {
    	if (isPictUpload && !isPictStored)	return laPhotoCandidate.pictLengthKB;
    	else return laPhoto.pictLengthKB;    	    	
    }
    
/*    
    // M�thode appel�e pour Uploader une nouvelle photo...
    // les param�tres sont en fait les variables publiques du controleur
    // qui ont �t� initialis�es depuis le composant WoFileUpload !
    public String uploadPict() {
    	NSLog.out.appendln();
    	NSLog.out.appendln( "======================" );
    	NSLog.out.appendln( "Component Data Upload:" );
    	
    	// Get just the name for the uploaded file from aFileName.
    	String fileName = NSPathUtilities.lastPathComponent(aFileName);
    	
//    	NSLog.out.appendln("le nom renvoye : "+aFileName+"\nle nom calcule : "+fileName);
		
    	// Create the output path for the file on the application server
    	if ((fileName!=null) && (fileName.length()>0)) {
    		NSLog.out.appendln( "MimeType: '" + aMimeType + "'" );
    		
    		// stocker les datas dans le buffer d'affichage temporaire en attendant confirmation utilisateur...
    		String err = laPhotoCandidate.changePhoto(aFileContents,new NSTimestamp());
    		if (err != null) return err;
        	isPictUpload=true;	
    		
    	} else {
    		return "Pas de fichier pr�cis� !";
    	}
    	return null;
    	
    }
    
    // On veut sauver les chgt dans la base � pr�sent (+ LDAP : � voir !!!)
    public void validerChangement() {
    	if (laPhotoCandidate.photoPresente()) {
    		
    		// ATTENTION : code a adapter au cas ou aucun EO n'existe d�j� pour la photo !
    		// ---> Ajout d'une photo pour un individu sans Photo !
    		if (eoPhoto==null) {
    			IndividuCtrlr ic = laSession.getIndividuCtrlr();
    			if (ic.estUnPersonnel()) eoPhoto = new PhotosEmployesGRHUM();
    			else if (ic.estUnEtudiant()) {
    				if (!ic.estUnAncien()) eoPhoto = new PhotosEtudiantsGRHUM();
    				else eoPhoto = new PhotosEtudiantsOldGRHUM();
    			}
    			
    			laSession.defaultEditingContext().insertObject(eoPhoto);
    			eoPhoto.takeValueForKey(ic.getNoIndividu(),"noIndividu");
    		}
    		
    		if (eoPhoto!=null) {
    			eoPhoto.takeValueForKey(new NSTimestamp(),"datePrise");
    			eoPhoto.takeValueForKey(laPhotoCandidate.getMyPict(),"datasPhoto");
    			
    			laSession.defaultEditingContext().saveChanges();
    			isPictStored=true;
    			
    			// Changer la photo principale !
    			laPhoto.changePhoto(laPhotoCandidate.getMyPict(),new NSTimestamp());
    			isPictFound = true;	// pour celui qu'avait pas de photo !!!
    			
    			// Changer d'�tat...
    			isDdeUpload=false;
    			isPictUpload=false;	    	
    			isPictStored=false;
    			
//    			 laiss� pour plus tard... apects LDAP � �lucider !
//    			 sess.defaultEditingContext().insertObject(unePhotosEmployesGRHUM);
//    			 //   	    sess.individuCourant().setPhotosEmployesGRHUM(unePhotosEmployesGRHUM);
//    			  String libelleVlan = "Personnels";
//    			  if (criSession().connectedUserInfo().vLan().endsWith("E"))
//    			  libelleVlan = "Etudiants";
//    			  Personnel userLdap = mod_passwd.getPersonneForLoginAndVlan(criSession().connectedUserInfo().login(),libelleVlan,session().defaultEditingContext());
//    			  if(userLdap!=null)
//    			  userLdap.setJpegPhoto(aFileContents);
//    			      		}
    	}
    }
    */   	
	    
}
