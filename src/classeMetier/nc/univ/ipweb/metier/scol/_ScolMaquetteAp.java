// $LastChangedRevision$ DO NOT EDIT.  Make changes to ScolMaquetteAp.java instead.
package nc.univ.ipweb.metier.scol;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;

public abstract class _ScolMaquetteAp extends  EOGenericRecord {
	public static final String ENTITY_NAME = "ScolMaquetteAp";

	// Attributes
	public static final String FANN_KEY_KEY = "fannKey";
	public static final String MAP_GROUPE_PREVU_KEY = "mapGroupePrevu";
	public static final String MAP_GROUPE_REEL_KEY = "mapGroupeReel";
	public static final String MAP_LIBELLE_KEY = "mapLibelle";
	public static final String MAP_SEUIL_KEY = "mapSeuil";
	public static final String MAP_VALEUR_KEY = "mapValeur";
	public static final String MHCO_CODE_KEY = "mhcoCode";

	// Relationships
	public static final String SCOL_MAQUETTE_CHARGES_AP_KEY = "scolMaquetteChargesAp";
	public static final String SCOL_MAQUETTE_REPARTITION_APS_KEY = "scolMaquetteRepartitionAps";

  public ScolMaquetteAp localInstanceOfScolMaquetteApIn(EOEditingContext editingContext) {
    ScolMaquetteAp localInstance = (ScolMaquetteAp)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public Integer fannKey() {
    return (Integer) storedValueForKey("fannKey");
  }

  public void setFannKey(Integer value) {
    takeStoredValueForKey(value, "fannKey");
  }

  public Integer mapGroupePrevu() {
    return (Integer) storedValueForKey("mapGroupePrevu");
  }

  public void setMapGroupePrevu(Integer value) {
    takeStoredValueForKey(value, "mapGroupePrevu");
  }

  public Integer mapGroupeReel() {
    return (Integer) storedValueForKey("mapGroupeReel");
  }

  public void setMapGroupeReel(Integer value) {
    takeStoredValueForKey(value, "mapGroupeReel");
  }

  public String mapLibelle() {
    return (String) storedValueForKey("mapLibelle");
  }

  public void setMapLibelle(String value) {
    takeStoredValueForKey(value, "mapLibelle");
  }

  public Integer mapSeuil() {
    return (Integer) storedValueForKey("mapSeuil");
  }

  public void setMapSeuil(Integer value) {
    takeStoredValueForKey(value, "mapSeuil");
  }

  public java.math.BigDecimal mapValeur() {
    return (java.math.BigDecimal) storedValueForKey("mapValeur");
  }

  public void setMapValeur(java.math.BigDecimal value) {
    takeStoredValueForKey(value, "mapValeur");
  }

  public String mhcoCode() {
    return (String) storedValueForKey("mhcoCode");
  }

  public void setMhcoCode(String value) {
    takeStoredValueForKey(value, "mhcoCode");
  }

  public NSArray scolMaquetteChargesAp() {
    return (NSArray)storedValueForKey("scolMaquetteChargesAp");
  }

  public NSArray scolMaquetteChargesAp(EOQualifier qualifier) {
    return scolMaquetteChargesAp(qualifier, null);
  }

  public NSArray scolMaquetteChargesAp(EOQualifier qualifier, NSArray sortOrderings) {
    NSArray results;
      results = scolMaquetteChargesAp();
      if (qualifier != null) {
        results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    return results;
  }
  
  public void addToScolMaquetteChargesApRelationship(nc.univ.ipweb.metier.scol.ScolMaquetteChargesAp object) {
    addObjectToBothSidesOfRelationshipWithKey(object, "scolMaquetteChargesAp");
  }

  public void removeFromScolMaquetteChargesApRelationship(nc.univ.ipweb.metier.scol.ScolMaquetteChargesAp object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "scolMaquetteChargesAp");
  }

  public nc.univ.ipweb.metier.scol.ScolMaquetteChargesAp createScolMaquetteChargesApRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("ScolMaquetteChargesAp");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "scolMaquetteChargesAp");
    return (nc.univ.ipweb.metier.scol.ScolMaquetteChargesAp) eo;
  }

  public void deleteScolMaquetteChargesApRelationship(nc.univ.ipweb.metier.scol.ScolMaquetteChargesAp object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "scolMaquetteChargesAp");
    editingContext().deleteObject(object);
  }

  public void deleteAllScolMaquetteChargesApRelationships() {
    Enumeration objects = scolMaquetteChargesAp().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteScolMaquetteChargesApRelationship((nc.univ.ipweb.metier.scol.ScolMaquetteChargesAp)objects.nextElement());
    }
  }

  public NSArray scolMaquetteRepartitionAps() {
    return (NSArray)storedValueForKey("scolMaquetteRepartitionAps");
  }

  public NSArray scolMaquetteRepartitionAps(EOQualifier qualifier) {
    return scolMaquetteRepartitionAps(qualifier, null);
  }

  public NSArray scolMaquetteRepartitionAps(EOQualifier qualifier, NSArray sortOrderings) {
    NSArray results;
      results = scolMaquetteRepartitionAps();
      if (qualifier != null) {
        results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    return results;
  }
  
  public void addToScolMaquetteRepartitionApsRelationship(nc.univ.ipweb.metier.scol.ScolMaquetteRepartitionAp object) {
    addObjectToBothSidesOfRelationshipWithKey(object, "scolMaquetteRepartitionAps");
  }

  public void removeFromScolMaquetteRepartitionApsRelationship(nc.univ.ipweb.metier.scol.ScolMaquetteRepartitionAp object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "scolMaquetteRepartitionAps");
  }

  public nc.univ.ipweb.metier.scol.ScolMaquetteRepartitionAp createScolMaquetteRepartitionApsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("ScolMaquetteRepartitionAp");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "scolMaquetteRepartitionAps");
    return (nc.univ.ipweb.metier.scol.ScolMaquetteRepartitionAp) eo;
  }

  public void deleteScolMaquetteRepartitionApsRelationship(nc.univ.ipweb.metier.scol.ScolMaquetteRepartitionAp object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "scolMaquetteRepartitionAps");
    editingContext().deleteObject(object);
  }

  public void deleteAllScolMaquetteRepartitionApsRelationships() {
    Enumeration objects = scolMaquetteRepartitionAps().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteScolMaquetteRepartitionApsRelationship((nc.univ.ipweb.metier.scol.ScolMaquetteRepartitionAp)objects.nextElement());
    }
  }


  public static ScolMaquetteAp createScolMaquetteAp(EOEditingContext editingContext, Integer fannKey
, Integer mapGroupePrevu
, Integer mapGroupeReel
, String mapLibelle
, Integer mapSeuil
, java.math.BigDecimal mapValeur
, String mhcoCode
) {
    ScolMaquetteAp eo = (ScolMaquetteAp) EOUtilities.createAndInsertInstance(editingContext, _ScolMaquetteAp.ENTITY_NAME);    
		eo.setFannKey(fannKey);
		eo.setMapGroupePrevu(mapGroupePrevu);
		eo.setMapGroupeReel(mapGroupeReel);
		eo.setMapLibelle(mapLibelle);
		eo.setMapSeuil(mapSeuil);
		eo.setMapValeur(mapValeur);
		eo.setMhcoCode(mhcoCode);
    return eo;
  }

  public static NSArray fetchAllScolMaquetteAps(EOEditingContext editingContext) {
    return _ScolMaquetteAp.fetchAllScolMaquetteAps(editingContext, null);
  }

  public static NSArray fetchAllScolMaquetteAps(EOEditingContext editingContext, NSArray sortOrderings) {
    return _ScolMaquetteAp.fetchScolMaquetteAps(editingContext, null, sortOrderings);
  }

  public static NSArray fetchScolMaquetteAps(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_ScolMaquetteAp.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray eoObjects = (NSArray)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static ScolMaquetteAp fetchScolMaquetteAp(EOEditingContext editingContext, String keyName, Object value) {
    return _ScolMaquetteAp.fetchScolMaquetteAp(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static ScolMaquetteAp fetchScolMaquetteAp(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray eoObjects = _ScolMaquetteAp.fetchScolMaquetteAps(editingContext, qualifier, null);
    ScolMaquetteAp eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (ScolMaquetteAp)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ScolMaquetteAp that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ScolMaquetteAp fetchRequiredScolMaquetteAp(EOEditingContext editingContext, String keyName, Object value) {
    return _ScolMaquetteAp.fetchRequiredScolMaquetteAp(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static ScolMaquetteAp fetchRequiredScolMaquetteAp(EOEditingContext editingContext, EOQualifier qualifier) {
    ScolMaquetteAp eoObject = _ScolMaquetteAp.fetchScolMaquetteAp(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ScolMaquetteAp that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ScolMaquetteAp localInstanceOfScolMaquetteApIn(EOEditingContext editingContext, ScolMaquetteAp eo) {
    ScolMaquetteAp localInstance = (eo == null) ? null : (ScolMaquetteAp)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
