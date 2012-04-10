// $LastChangedRevision$ DO NOT EDIT.  Make changes to ScolMaquetteResponsableEc.java instead.
package org.cocktail.ipweb.serveur.metier.scol;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;

public abstract class _ScolMaquetteResponsableEc extends  EOGenericRecord {
	public static final String ENTITY_NAME = "ScolMaquetteResponsableEc";

	// Attributes
	public static final String FANN_KEY_KEY = "fannKey";
	public static final String MBEC_TYPE_KEY = "mbecType";
	public static final String MEC_KEY_KEY = "mecKey";

	// Relationships
	public static final String INDIVIDU_KEY = "individu";

  public ScolMaquetteResponsableEc localInstanceOfScolMaquetteResponsableEcIn(EOEditingContext editingContext) {
    ScolMaquetteResponsableEc localInstance = (ScolMaquetteResponsableEc)EOUtilities.localInstanceOfObject(editingContext, this);
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

  public String mbecType() {
    return (String) storedValueForKey("mbecType");
  }

  public void setMbecType(String value) {
    takeStoredValueForKey(value, "mbecType");
  }

  public Integer mecKey() {
    return (Integer) storedValueForKey("mecKey");
  }

  public void setMecKey(Integer value) {
    takeStoredValueForKey(value, "mecKey");
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
  

  public static ScolMaquetteResponsableEc createScolMaquetteResponsableEc(EOEditingContext editingContext, Integer fannKey
, String mbecType
, Integer mecKey
, EOGenericRecord individu) {
    ScolMaquetteResponsableEc eo = (ScolMaquetteResponsableEc) EOUtilities.createAndInsertInstance(editingContext, _ScolMaquetteResponsableEc.ENTITY_NAME);    
		eo.setFannKey(fannKey);
		eo.setMbecType(mbecType);
		eo.setMecKey(mecKey);
    eo.setIndividuRelationship(individu);
    return eo;
  }

  public static NSArray fetchAllScolMaquetteResponsableEcs(EOEditingContext editingContext) {
    return _ScolMaquetteResponsableEc.fetchAllScolMaquetteResponsableEcs(editingContext, null);
  }

  public static NSArray fetchAllScolMaquetteResponsableEcs(EOEditingContext editingContext, NSArray sortOrderings) {
    return _ScolMaquetteResponsableEc.fetchScolMaquetteResponsableEcs(editingContext, null, sortOrderings);
  }

  public static NSArray fetchScolMaquetteResponsableEcs(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_ScolMaquetteResponsableEc.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray eoObjects = (NSArray)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static ScolMaquetteResponsableEc fetchScolMaquetteResponsableEc(EOEditingContext editingContext, String keyName, Object value) {
    return _ScolMaquetteResponsableEc.fetchScolMaquetteResponsableEc(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static ScolMaquetteResponsableEc fetchScolMaquetteResponsableEc(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray eoObjects = _ScolMaquetteResponsableEc.fetchScolMaquetteResponsableEcs(editingContext, qualifier, null);
    ScolMaquetteResponsableEc eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (ScolMaquetteResponsableEc)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ScolMaquetteResponsableEc that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ScolMaquetteResponsableEc fetchRequiredScolMaquetteResponsableEc(EOEditingContext editingContext, String keyName, Object value) {
    return _ScolMaquetteResponsableEc.fetchRequiredScolMaquetteResponsableEc(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static ScolMaquetteResponsableEc fetchRequiredScolMaquetteResponsableEc(EOEditingContext editingContext, EOQualifier qualifier) {
    ScolMaquetteResponsableEc eoObject = _ScolMaquetteResponsableEc.fetchScolMaquetteResponsableEc(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ScolMaquetteResponsableEc that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ScolMaquetteResponsableEc localInstanceOfScolMaquetteResponsableEcIn(EOEditingContext editingContext, ScolMaquetteResponsableEc eo) {
    ScolMaquetteResponsableEc localInstance = (eo == null) ? null : (ScolMaquetteResponsableEc)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
