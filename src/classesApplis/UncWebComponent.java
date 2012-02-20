import com.webobjects.appserver.*;
import fr.univlr.cri.webapp.CRIWebComponent;
/*
 * Créé le 6 oct. 2006
 *
 * @author olive
 *	Objectif : fournir une super-classe pour la gestion de l'accés à des Ancres dans une page (WoComponent)
 *		suit l'exemple trouvé sur http://en.wikibooks.org/wiki/Programming:WebObjects/Web_Applications/Development/Examples/Anchors
 */
public class UncWebComponent extends CRIWebComponent {

	private String anchor;	// variable à initialiser au moment de sauter à l'ancre
	
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
	 
	   // méthode qui sera utilisée dans la sous-classe pour positionner l'appel de l'ancre dans la réponse...
	   public void setAnchor(String s) {
	       anchor = s;
	   }
	
	   // Travail à faire ensuite 
	   //		- dans le composant html : rajouter <a name="myanchor"></a>
	   //		- dans la sous-classe, la méthode action doit ressembler à ça :
	   
//	   protected WOComponent doSometingAndJumpToAnchor() {
//	   	// do something :)
//	   	setAnchor("myanchor");
//	   	return null;
//	   }
	   
}
