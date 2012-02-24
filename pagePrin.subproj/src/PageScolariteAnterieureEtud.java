
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOComponent;
import com.webobjects.foundation.NSDictionary;

public class PageScolariteAnterieureEtud extends WOComponent {
	
	private NSDictionary lesInscriptionsParAnnee;
	private WOComponent previousPage;
	
    public PageScolariteAnterieureEtud(WOContext context) {
        super(context);
    }
    
    public NSDictionary getLesInscriptionsParAnnee() {
		return lesInscriptionsParAnnee;
	}
    
    public void setLesInscriptionsParAnnee(NSDictionary lesInscriptionsParAnnee) {
		this.lesInscriptionsParAnnee = lesInscriptionsParAnnee;
	}
    
    public WOComponent getPreviousPage() {
		return previousPage;
	}
    
    public void setPreviousPage(WOComponent previousPage) {
		this.previousPage = previousPage;
	}
    
}