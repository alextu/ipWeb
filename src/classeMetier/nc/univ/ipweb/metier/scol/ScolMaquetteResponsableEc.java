package nc.univ.ipweb.metier.scol;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

public class ScolMaquetteResponsableEc extends _ScolMaquetteResponsableEc {
	
	public static NSArray fetchResponsablesForEc(EOEditingContext ec, Integer mecKey) {
		EOQualifier qual = new EOKeyValueQualifier(MEC_KEY_KEY, EOKeyValueQualifier.QualifierOperatorEqual, mecKey);
		EOFetchSpecification fspec = new EOFetchSpecification(ENTITY_NAME, qual, null);
		fspec.setPrefetchingRelationshipKeyPaths(new NSArray(INDIVIDU_KEY));
		NSArray res = ec.objectsWithFetchSpecification(fspec);
		return (NSArray)res.valueForKey(INDIVIDU_KEY);
	}
	
}
