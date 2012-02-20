/* Objectif :
 * -----------
 * G�rer la cr�ation et la mise � jour d'enregistrement de stats sur les IP
 * en fonction des demandes de l'objet possesseur (InscFormationCtrlr)

*/
import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;

public class IpsemstatCtlr {

    private IpSemStat monIpSemStat;		// lien vers l'eo qui sert de base à cet objet...
    protected Session maSession;
    private boolean needSave;			// gestion en interne d'une alerte pour savoir si un saveChanges est requis...

    protected Integer idiplNumero, msemKey, fannKey;
    
    // Constructeur avec paramétres nécessaires au fetch initial...
    public IpsemstatCtlr(Integer idiplNum, Integer msemK, Integer fannK, Session sess) {
	maSession = sess;
	idiplNumero = idiplNum;
	msemKey = msemK;
	fannKey = fannK;
	
	init();
	needSave = false;
    }
    
    private void init() {
    	monIpSemStat = null;
    	// Préparer le fetch pour la ligne de stat concernée (TOUJOURS parcours COMMUN !)
    	NSArray bindings = new NSArray(new Object[] {idiplNumero,msemKey});
    	EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(
    			"idiplNumero = %@ and msemKey = %@", bindings);
    	EOFetchSpecification fetchSpec = new EOFetchSpecification("IpSemStat",qualifier, null);
    	EOEditingContext ec = maSession.defaultEditingContext();

    	// fetcher le tuple pour les stats au semestre...
    	NSArray res = ec.objectsWithFetchSpecification(fetchSpec);
    	if (res != null && res.count()>0) {
    		monIpSemStat = (IpSemStat)res.objectAtIndex(0);
    	}
    }
    
    public boolean aVide() { return ( monIpSemStat == null); }
    
    private void creationNouvelEO() {
    	if (monIpSemStat == null) {	// a la 1ere connexion, cette var. vaudra NULL ... en tenir compte !
    		monIpSemStat = new IpSemStat();
    		EOEditingContext ec = maSession.defaultEditingContext();
    		ec.insertObject(monIpSemStat);

    		monIpSemStat.setIdiplNumero(idiplNumero);
    		monIpSemStat.setFannKey(fannKey);
    		monIpSemStat.setMsemKey(msemKey);
    		monIpSemStat.setChoixValides("N");
    		needSave = true;
    	}
    }

    // On indique que les choix ont été validés pour ce semestre
    public void valideChoixSemestre() {
    	if (monIpSemStat == null) creationNouvelEO();
    	monIpSemStat.setChoixValides("O");
    	needSave = true;
    	svgde();
    }
    
    // On indique que les choix ont été invalidés pour ce semestre
    public void invalideChoixSemestre() {
    	if (monIpSemStat == null) creationNouvelEO();
    	monIpSemStat.setChoixValides("N");
    	needSave = true;
    	svgde();
    }   
    
    // On récupère l'état des choix validés... 
    public boolean choixSemestreValides() {
    	if (monIpSemStat == null) return false;
    	String choixValides = monIpSemStat.choixValides();
    	if (choixValides == null || choixValides.length() == 0 || choixValides.compareToIgnoreCase("N") == 0)
    		return false;
    	else return true;
    }
    
    
    // Utile pour v�rifier si la sunchro est bonne et sinon raffraichir l'enreg sur ces 2 valeurs...
    public void verifSync(boolean inscPsIncomplete, Integer mrsemK) {
    	setInscPsIncomplete(inscPsIncomplete);
    	setMrsemKey(mrsemK);
    	svgde();	// si n�cessaire !
    }
    
    public void svgde() {
	// Des changements doivent-ils �tre r�percut�s vers l'EC / La base ?
	if (needSave) {
	    EOEditingContext ec = maSession.defaultEditingContext();
	    ec.saveChanges();
	    needSave = false;
	}
    }
    
    
    public boolean inscPsIncomplete() {
	if (monIpSemStat != null && monIpSemStat.inscPsIncomplete() != null 
		&& (monIpSemStat.inscPsIncomplete()).toUpperCase().compareTo("O") == 0) 
	    return true;
	else return false;
    }
    
    public void setInscPsIncomplete(boolean etat) {
	boolean nouveau = false;
	if (monIpSemStat == null) {
	    creationNouvelEO();
	    nouveau = true;
	}
	if (nouveau || inscPsIncomplete() != etat) {
	    needSave = true;
	    if (etat == true) { 
		monIpSemStat.setInscPsIncomplete("O");
		setNbUeIncompletes(null);
	    }
	    else monIpSemStat.setInscPsIncomplete("N");
	}
    }
    
    public Integer nbUeIncompletes() {
	if (monIpSemStat != null) {
	    return (Integer)monIpSemStat.nbUeIncompletes();
	}
	else return null;
    }
    
    public void setNbUeIncompletes(Integer nbUeInc) {
	if (monIpSemStat == null) {
	    creationNouvelEO();
	    monIpSemStat.setNbUeIncompletes(nbUeInc);
	}
	else {
	    Integer oldValue = nbUeIncompletes();
	    if ((oldValue == null && nbUeInc != null) || (oldValue != null && nbUeInc == null)
		    || (oldValue != null && nbUeInc != null && oldValue.compareTo(nbUeInc)!=0)) {
		needSave = true;
		monIpSemStat.setNbUeIncompletes(nbUeInc);
	    }
	}
    }
    
    public void setMrsemKey(Integer mrsemK) {
	if (monIpSemStat == null) {
	    creationNouvelEO();
	    monIpSemStat.setMrsemKeyPs(mrsemK);
	}
	else {
	    Integer oldValue = (Integer)monIpSemStat.mrsemKeyPs();
	    if ((oldValue == null && mrsemK != null) || (oldValue != null && mrsemK == null)
		    || (oldValue != null && mrsemK != null && oldValue.compareTo(mrsemK)!=0)) {
		needSave = true;
		monIpSemStat.setMrsemKeyPs(mrsemK);
	    }
	}
    }
    
    public void setCumulEcts(Double cumECTS) {
	if (monIpSemStat == null) {
	    creationNouvelEO();
	    monIpSemStat.setCumulEcts(cumECTS);
	}
	else {
	    Double oldValue = (Double)monIpSemStat.cumulEcts();
	    if ((oldValue == null && cumECTS != null) || (oldValue != null && cumECTS == null)
		    || (oldValue != null && cumECTS != null && oldValue.compareTo(cumECTS)!=0)) {
		needSave = true;
		monIpSemStat.setCumulEcts(cumECTS);
	    }
	}
    }
    
}
