package org.cocktail.ipweb.serveur.controlleur;
import org.cocktail.fwkcktlwebapp.server.components.CktlWebComponent;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
/*
 * Cr�� le 6 oct. 2006
 *
 * @author olive
 *	Objectif : fournir une super-classe pour la gestion de l'acc�s � des Ancres dans une page (WoComponent)
 *		suit l'exemple trouv� sur http://en.wikibooks.org/wiki/Programming:WebObjects/Web_Applications/Development/Examples/Anchors
 */
public class UncWebComponent extends CktlWebComponent {

	private String anchor;	// variable � initialiser au moment de sauter � l'ancre
	
	public UncWebComponent(WOContext arg0) {
		super(arg0);
	}

	public void appendToResponse(WOResponse response, WOContext context) {
	     if (anchor != null) {
	        response.setHeader(context.componentActionURL() + "#" + anchor, "location");
	        response.setHeader("text/html", "content-type");
	        response.setHeader("0", "content-length");
	        response.setStatus(302);
	        anchor = null;
	     } else {
	        super.appendToResponse(response, context);
	     }
	   } // appendToResponse
	
	   public String getAnchor() {
	       return anchor;
	   }
	 
	   // m�thode qui sera utilis�e dans la sous-classe pour positionner l'appel de l'ancre dans la r�ponse...
	   public void setAnchor(String s) {
	       anchor = s;
	   }
	
	   // Travail � faire ensuite 
	   //		- dans le composant html : rajouter <a name="myanchor"></a>
	   //		- dans la sous-classe, la m�thode action doit ressembler � �a :
	   
//	   protected WOComponent doSometingAndJumpToAnchor() {
//	   	// do something :)
//	   	setAnchor("myanchor");
//	   	return null;
//	   }
	   
}
