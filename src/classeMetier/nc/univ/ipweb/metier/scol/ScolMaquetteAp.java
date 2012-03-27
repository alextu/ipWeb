package nc.univ.ipweb.metier.scol;

import org.apache.log4j.Logger;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

public class ScolMaquetteAp extends _ScolMaquetteAp {
  private static Logger log = Logger.getLogger(ScolMaquetteAp.class);
  
  public static NSArray fetchTpsAndTdsForEc(EOEditingContext ec, Integer mecKey) {
	  EOQualifier qual = EOQualifier.qualifierWithQualifierFormat("scolMaquetteRepartitionAps.mecKey = %@ and (mhcoCode = 'TD' or mhcoCode = 'TP')", new NSArray(mecKey));
	  EOFetchSpecification fspec = new EOFetchSpecification(ScolMaquetteAp.ENTITY_NAME, qual, null);
	  fspec.setRefreshesRefetchedObjects(true);
	  fspec.setPrefetchingRelationshipKeyPaths(new NSArray(SCOL_MAQUETTE_CHARGES_AP_KEY));
	  return ec.objectsWithFetchSpecification(fspec);
  }
}
