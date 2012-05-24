package org.cocktail.ipweb.serveur.metier.scol;

import org.apache.log4j.Logger;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

public class ScolMaquetteAp extends _ScolMaquetteAp {
  private static Logger log = Logger.getLogger(ScolMaquetteAp.class);
  public static final int MAX_INSC_TD = 45;
  public static final int MAX_INSC_TP = 25;
  
  public static NSArray fetchTpsAndTdsForEc(EOEditingContext ec, Integer mecKey) {
	  EOQualifier qual = EOQualifier.qualifierWithQualifierFormat("scolMaquetteRepartitionAps.mecKey = %@ and (mhcoCode = 'TD' or mhcoCode = 'TP')", new NSArray(mecKey));
	  EOFetchSpecification fspec = new EOFetchSpecification(ScolMaquetteAp.ENTITY_NAME, qual, null);
	  fspec.setRefreshesRefetchedObjects(true);
	  fspec.setPrefetchingRelationshipKeyPaths(new NSArray(SCOL_MAQUETTE_CHARGES_AP_KEY));
	  return ec.objectsWithFetchSpecification(fspec);
  }

  public static int countInscForAp(EOEditingContext ec, Integer mecKey, Integer mapKey) {
	  String sql = "select count(*) as nb_etu from (select distinct NOM_PRENOM, FILIERE from IP_WEB.v_scol_inscription_ap where map_key = $0 and mec_key = $1)";
	  sql = sql.replace("$0", mapKey.toString());
	  sql = sql.replace("$1", mecKey.toString());
	  NSArray rawRows = EOUtilities.rawRowsForSQL(ec, "ipWeb", sql, null);
	  int count = 0;
	  if (rawRows.count() == 1) {
		  count = ((Double) ((NSDictionary)rawRows.objectAtIndex(0)).objectForKey("NB_ETU")).intValue();
	  }
	  return count;
  }
  
  public Integer mapKey() {
  	NSDictionary pk = EOUtilities.primaryKeyForObject(editingContext(), this);
  	return (Integer)pk.objectForKey("mapKey");
  }
  
  public boolean isTD() {
	  return "TD".equals(mhcoCode());
  }
  
  public boolean isTP() {
	  return "TP".equals(mhcoCode());
  }
  
}
