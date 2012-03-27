// $LastChangedRevision$ DO NOT EDIT.  Make changes to ScolMaquetteChargesAp.java instead.
package nc.univ.ipweb.metier.scol;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;

public abstract class _ScolMaquetteChargesAp extends  EOGenericRecord {
	public static final String ENTITY_NAME = "ScolMaquetteChargesAp";

	// Attributes

	// Relationships
	public static final String INDIVIDU_KEY = "individu";

  public ScolMaquetteChargesAp localInstanceOfScolMaquetteChargesApIn(EOEditingContext editingContext) {
    ScolMaquetteChargesAp localInstance = (ScolMaquetteChargesAp)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public EOGenericRecord individu() {
    return (EOGenericRecord)storedValueForKey("individu");
  }

  public void setIndividuRelationship(EOGenericRecord value) {
    if (value == null) {
    	EOGenericRecord oldValue = individu();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "individu");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "individu");
    }
  }
  

  public static ScolMaquetteChargesAp createScolMaquetteChargesAp(EOEditingContext editingContext, EOGenericRecord individu) {
    ScolMaquetteChargesAp eo = (ScolMaquetteChargesAp) EOUtilities.createAndInsertInstance(editingContext, _ScolMaquetteChargesAp.ENTITY_NAME);    
    eo.setIndividuRelationship(individu);
    return eo;
  }

  public static NSArray fetchAllScolMaquetteChargesAps(EOEditingContext editingContext) {
    return _ScolMaquetteChargesAp.fetchAllScolMaquetteChargesAps(editingContext, null);
  }

  public static NSArray fetchAllScolMaquetteChargesAps(EOEditingContext editingContext, NSArray sortOrderings) {
    return _ScolMaquetteChargesAp.fetchScolMaquetteChargesAps(editingContext, null, sortOrderings);
  }

  public static NSArray fetchScolMaquetteChargesAps(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_ScolMaquetteChargesAp.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray eoObjects = (NSArray)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static ScolMaquetteChargesAp fetchScolMaquetteChargesAp(EOEditingContext editingContext, String keyName, Object value) {
    return _ScolMaquetteChargesAp.fetchScolMaquetteChargesAp(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static ScolMaquetteChargesAp fetchScolMaquetteChargesAp(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray eoObjects = _ScolMaquetteChargesAp.fetchScolMaquetteChargesAps(editingContext, qualifier, null);
    ScolMaquetteChargesAp eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (ScolMaquetteChargesAp)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ScolMaquetteChargesAp that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ScolMaquetteChargesAp fetchRequiredScolMaquetteChargesAp(EOEditingContext editingContext, String keyName, Object value) {
    return _ScolMaquetteChargesAp.fetchRequiredScolMaquetteChargesAp(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static ScolMaquetteChargesAp fetchRequiredScolMaquetteChargesAp(EOEditingContext editingContext, EOQualifier qualifier) {
    ScolMaquetteChargesAp eoObject = _ScolMaquetteChargesAp.fetchScolMaquetteChargesAp(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ScolMaquetteChargesAp that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ScolMaquetteChargesAp localInstanceOfScolMaquetteChargesApIn(EOEditingContext editingContext, ScolMaquetteChargesAp eo) {
    ScolMaquetteChargesAp localInstance = (eo == null) ? null : (ScolMaquetteChargesAp)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
