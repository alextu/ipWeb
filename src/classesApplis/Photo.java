import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import com.sun.image.codec.jpeg.*;
import javax.swing.ImageIcon;

import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSTimestampFormatter;

/*
 * Créé le 26 juin 2006
 *
 * Objectif : conserver les infos inhérentes à une photo (données, taille réelle et d'affichage, etc...)
 * classe à adapter (moins de manips à faire que dans projet Profil...) 
 */

/**
 * @author olive
 *
 */
public class Photo {

	/**
	 * Msg d'erreur eventuellement renvoyé en cas de pb !
	 */
	public String msgErr;
	/**
	 * Photo éventuellement récupérée dans la base
	 */
	public NSData myPict;
	/**
	 * Taille en octets de l'image récupérée en Ko
	 */
	public Integer pictLengthKB;
	/**
	 * Date de la prise de vue
	 */   
	public String datePrise;

	// Traitement des datas au format image...
	
	private Image imagePhoto;
	private int largeurPTPhoto,hauteurPTPhoto;
	private int largeurTNPhoto,hauteurTNPhoto;
	private int largeurTBPhoto,hauteurTBPhoto;

	// Largeur maxi pour la petite photo
	private static final int largeurPTMaxi = 90;
	private static final int hauteurPTMaxi = (int)Math.round(largeurPTMaxi*1.333333); // rapport 1.33

	// Largeur maxi pour la "grande" photo dans onglet "Photo"
	private static final int largeurTNMaxi = 288;
	private static final int hauteurTNMaxi = (int)Math.round(largeurPTMaxi*1.333333); // rapport 1.33

	// Largeur maxi pour la photo dans la base
	private static final int largeurTBMaxi = 90;
	private static final int hauteurTBMaxi = (int)Math.round(largeurTBMaxi*1.333333); // rapport 1.33
	
	public Photo() {
		super();
		// init de l'objet
		myPict = null;
		pictLengthKB = null;
		datePrise = null;           

	}

	// appelé pour init de l'objet Photo depuis un record de Blob 
	public String changePhoto(NSData donneesPhoto,NSTimestamp dateInsertion) {

		// Commencer par ajuster la photo...
		msgErr = "";
		this.myPict = ajustementTaille(donneesPhoto);
		if (!msgErr.equals("")) return msgErr;
		this.pictLengthKB = new Integer(this.myPict.length() / 1024);
		NSTimestampFormatter formatter=new NSTimestampFormatter("%d/%m/%y");
		this.datePrise = (formatter.format(dateInsertion));
		return null;

	}
	
	// gestion de l'image tirée des données de la base 
	//(pour adapter les dimensions à l'affichage du composant)
    private NSData ajustementTaille(NSData donneesPhoto) {
    	NSData donneeEnRetour = donneesPhoto;
    	ImageIcon monImIcon;
    	monImIcon = new ImageIcon(donneesPhoto.bytes());
		imagePhoto = monImIcon.getImage();
		largeurTBPhoto = imagePhoto.getWidth(null);
		hauteurTBPhoto = imagePhoto.getHeight(null);

		if (largeurTBPhoto <= 0 || hauteurTBPhoto <= 0) {
			msgErr = "Pb avec le fichier Image ! Est-il au bon format ?";
			return myPict;
    	}
	
		double rapportHL = (double)hauteurTBPhoto/(double)largeurTBPhoto;
		
		// si la surface de l'image est < de + de 30% par rapport à la taille maxi, la refuser !!!
		double sufMax = (double)largeurTBMaxi * (double)hauteurTBMaxi;
		double sufPhoto = (double)largeurTBPhoto * (double)hauteurTBPhoto;
		if (sufPhoto < sufMax*0.70) {
			msgErr = "dimensions de la photo insuffisantes !";
			return myPict;
		}
		else if (largeurTBPhoto != largeurTBMaxi || hauteurTBPhoto != hauteurTBMaxi) {	
			// On "normalise" toutes les photos en entrée ou sortie !!!
			// la zoomer/dézoomer et la recoder en Jpeg...	
			int lDeb,lFin,hDeb,hFin;
//			// Retailler pour une petite photo !!!
			
			if (rapportHL>1.334) {	// images + haute que stdr : rogner en haut et en bas
				lDeb = 0; lFin = largeurTBPhoto;
				hDeb = (int)((hauteurTBPhoto-(double)largeurTBPhoto*((double)hauteurTBMaxi/(double)largeurTBMaxi))/2.00);
				hFin = (hauteurTBPhoto) - hDeb;
			}
			else {	// images + larges que stdr : rogner à droite et à gauche
				hDeb = 0; hFin = hauteurTBPhoto;
				lDeb = (int)((largeurTBPhoto-(double)hauteurTBPhoto*((double)largeurTBMaxi/(double)hauteurTBMaxi))/2.00);
				lFin = (largeurTBPhoto) - lDeb;					
			}
			hauteurTBPhoto = hauteurTBMaxi; largeurTBPhoto = largeurTBMaxi;

			// création d'un buffer de sortie pour réduction de taille...
			BufferedImage imTransf = new BufferedImage(largeurTBPhoto,hauteurTBPhoto,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = imTransf.createGraphics();
			// projection vers la nouvelle taille + rognage éventuel
			try {
				g.drawImage(imagePhoto,0,0,largeurTBPhoto,hauteurTBPhoto,
						lDeb,hDeb,lFin,hFin,null);
			}
			catch (Exception e){
				msgErr = "PB avec votre fichier Image !";				
				return myPict;
			}
			// conversion en JPEG
			try {
				ByteArrayOutputStream streamSortie = new ByteArrayOutputStream();
				JPEGImageEncoder monEncodeur = JPEGCodec.createJPEGEncoder(streamSortie);
				monEncodeur.encode(imTransf);
				donneeEnRetour = new NSData(streamSortie.toByteArray());
			}
			catch (Exception e) {
				msgErr = "PB avec l'encodage de votre fichier Image !";				
				return myPict;
			}
			
		}
		
		rapportHL = (double)hauteurTBPhoto/(double)largeurTBPhoto;
		if (rapportHL>1.334) {	// images + haute que stdr
			largeurPTPhoto = (int)((double)largeurTBPhoto/((double)hauteurTBPhoto/(double)hauteurPTMaxi));
			hauteurPTPhoto = hauteurPTMaxi;
			largeurTNPhoto = (int)((double)largeurTBPhoto/((double)hauteurTBPhoto/(double)hauteurTNMaxi));
			hauteurTNPhoto = hauteurTNMaxi;
		}
		else {
			hauteurPTPhoto = (int)((double)hauteurTBPhoto/((double)largeurTBPhoto/(double)largeurPTMaxi));
			largeurPTPhoto = largeurPTMaxi;    					
			hauteurTNPhoto = (int)((double)hauteurTBPhoto/((double)largeurTBPhoto/(double)largeurTNMaxi));
			largeurTNPhoto = largeurTNMaxi;    					
		}
		return donneeEnRetour;
    }
    
    public int largeurPTPhoto() { return largeurPTPhoto; }
    public int hauteurPTPhoto() { return hauteurPTPhoto; }
    
    public int largeurTNPhoto() { return largeurTNPhoto; }
    public int hauteurTNPhoto() { return hauteurTNPhoto; }
    
    public int largeurTBPhoto() { return largeurTBPhoto; }
    public int hauteurTBPhoto() { return hauteurTBPhoto; }
   
    public NSData getMyPict() {
    	return myPict;    	
    }
  
    public boolean photoPresente() {
    	return (myPict != null);
    }
}
