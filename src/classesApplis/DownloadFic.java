
/**
 * @author olive
 * Classe utilis�e pour forcer un t�l�chargement d'un fichier vers le poste de l'utilisateur, 
 *
 */

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import fr.univlr.cri.webapp.*;


public class DownloadFic extends CRIWebComponent {

    protected String mimeType="";
    // protected String mimeType="application/force-download";
    protected String encoding="binary";
    protected NSData fileData=null;
    protected String fileName;
    protected String fileDownLoad;

    public DownloadFic(WOContext context) {
	super(context);
    }

    
    public boolean initDownload(String typeMime,NSData dataOut,String nomPdf) {
	mimeType=typeMime;
	fileData=dataOut;
	fileDownLoad = nomPdf;
	if (fileData == null) return false;
	else return true;
    }

    // init la page retourn�e (download) avec les donn�es du doc pdf en parametre (implicitement type pdf)
    // retourne faux en cas de bleme avec fichier...
    public boolean initDownloadPDF(NSData dataOut,String nomPdf) {
	return initDownload("application/pdf",dataOut,nomPdf);
    }

    // init la page retournee (download) avec les donnees du doc XLS en parametre (implicitement type excel)
	// retourne faux en cas de bleme avec fichier...
	public boolean initDownloadXLS(NSData dataOut,String nomXLS) {
		return initDownload("application/vnd.ms-excel",dataOut,nomXLS);
	}	
    
    // init la page retourn�e (download) avec les donn�es du doc charg� depuis le fichier pass� en parametre 
    // type mime � pr�ciser !
    // retourne faux en cas de bleme avec fichier...
    public boolean initDownloadFichier(String nomFic, String typeMime) {
	if (loadDataFic(nomFic)) {
	    String nomPdf = "";
	    int ndx = this.fileName.lastIndexOf("\\");
	    if (ndx<0) ndx = this.fileName.lastIndexOf("/");
	    if (ndx>=0) nomPdf = this.fileName.substring(ndx+1);

	    return initDownload(typeMime,fileData,nomPdf);
	}
	else return false;
    }
    
    
    // appel� pour initialiser les donn�es � retourner avec le contenu du fichier pass� en param
    // (path + nom)
    // retourne faux en cas de bleme avec fichier...
    private boolean loadDataFic(String nomFic){
	// test la r�cup d'un fichier ..
	fileName = nomFic;
	FileInputStream fin = null;
	boolean res = false;
	try {
	    fin=new FileInputStream(fileName);
	    fileData = new NSData(fin, 10000);
	    res=true;
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    try {
		fin.close();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	return res;
    }

    /**
     * La m�thode du parent n'est pas appel�e, afin de ne pas afficher les header classiques des pages
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext) {
	//pas d'appel � super pour �viter d'afficher le composant
	//NB: le t�l�chargement ne fonctionne pas sur Mac (il affiche la photo)

	aResponse.setHeader("attachment;filename=\"" + fileDownLoad + "\"",
	"content-disposition");     
	aResponse.setHeader(this.mimeType, "content-type");
	aResponse.setHeader(this.encoding, "content-transfer-encoding");                      
	aResponse.setContent(fileData);
	// bidouille poitiers https et ie
	aResponse.disableClientCaching();
	aResponse.removeHeadersForKey("Cache-Control");
	aResponse.removeHeadersForKey("pragma"); 
    }

//  PB : il cherche un frameWork "ipWeb" au pageWithName("DownloadFic")
//  Pour d�tourner le pb (trouv� sur le net) : 
//    cr�ation bidon d'un bundle Download.wo, � priori inutile ... (pourquoi �a marche ? Myst�re ...)  

    }


	
	