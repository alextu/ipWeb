// $LastChangedRevision$ DO NOT EDIT.  Make changes to ScolMaquetteRepartitionAp.java instead.
package nc.univ.ipweb.metier.scol;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;

public abstract class _ScolMaquetteRepartitionAp extends  EOGenericRecord {
	public static final String ENTITY_NAME = "ScolMaquetteRepartitionAp";

	// Attributes
	public static final String FANN_KEY_KEY = "fannKey";
	public static final String MAP_KEY_KEY = "mapKey";
	public static final String MEC_KEY_KEY = "mecKey";

	// Relationships

  public ScolMaquetteRepartitionAp localInstanceOfScolMaquetteRepartitionApIn(EOEditingContext editingContext) {
    ScolMaquetteRepartitionAp localInstance = (ScolMaquetteRepartitionAp)EOUtilities.localInstanceOfObject(editingContext, this);
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

  public Integer mapKey() {
    return (Integer) storedValueForKey("mapKey");
  }

  public void setMapKey(Integer value) {
    takeStoredValueForKey(value, "mapKey");
  }

  public Integer mecKey() {
    return (Integer) storedValueForKey("mecKey");
  }

  public void setMecKey(Integer value) {
    takeStoredValueForKey(value, "mecKey");
  }


  public static ScolMaquetteRepartitionAp createScolMaquetteRepartitionAp(EOEditingContext editingContext, Integer fannKey
, Integer mapKey
, Integer mecKey
) {
    ScolMaquetteRepartitionAp eo = (ScolMaquetteRepartitionAp) EOUtilities.createAndInsertInstance(editingContext, _ScolMaquetteRepartitionAp.ENTITY_NAME);    
		eo.setFannKey(fannKey);
		eo.setMapKey(mapKey);
		eo.setMecKey(mecKey);
    return eo;
  }

  public static NSArray fetchAllScolMaquetteRepartitionAps(EOEditingContext editingContext) {
    return _ScolMaquetteRepartitionAp.fetchAllScolMaquetteRepartitionAps(editingContext, null);
  }

  public static NSArray fetchAllScolMaquetteRepartitionAps(EOEditingContext editingContext, NSArray sortOrderings) {
    return _ScolMaquetteRepartitionAp.fetchScolMaquetteRepartitionAps(editingContext, null, sortOrderings);
  }

  public static NSArray fetchScolMaquetteRepartitionAps(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_ScolMaquetteRepartitionAp.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray eoObjects = (NSArray)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static ScolMaquetteRepartitionAp fetchScolMaquetteRepartitionAp(EOEditingContext editingContext, String keyName, Object value) {
    return _ScolMaquetteRepartitionAp.fetchScolMaquetteRepartitionAp(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static ScolMaquetteRepartitionAp fetchScolMaquetteRepartitionAp(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray eoObjects = _ScolMaquetteRepartitionAp.fetchScolMaquetteRepartitionAps(editingContext, qualifier, null);
    ScolMaquetteRepartitionAp eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (ScolMaquetteRepartitionAp)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ScolMaquetteRepartitionAp that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ScolMaquetteRepartitionAp fetchRequiredScolMaquetteRepartitionAp(EOEditingContext editingContext, String keyName, Object value) {
    return _ScolMaquetteRepartitionAp.fetchRequiredScolMaquetteRepartitionAp(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static ScolMaquetteRepartitionAp fetchRequiredScolMaquetteRepartitionAp(EOEditingContext editingContext, EOQualifier qualifier) {
    ScolMaquetteRepartitionAp eoObject = _ScolMaquetteRepartitionAp.fetchScolMaquetteRepartitionAp(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ScolMaquetteRepartitionAp that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ScolMaquetteRepartitionAp localInstanceOfScolMaquetteRepartitionApIn(EOEditingContext editingContext, ScolMaquetteRepartitionAp eo) {
    ScolMaquetteRepartitionAp localInstance = (eo == null) ? null : (ScolMaquetteRepartitionAp)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
