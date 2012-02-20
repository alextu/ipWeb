// EORepartCompte.java
// Created on Fri Sep 29 01:06:45  2006 by Apple EOModeler Version 5.2

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.math.BigDecimal;
import java.util.*;

public class EORepartCompte extends EOGenericRecord {

    public EORepartCompte() {
        super();
    }

/*
    // If you implement the following constructor EOF will use it to
    // create your objects, otherwise it will use the default
    // constructor. For maximum performance, you should only
    // implement this constructor if you depend on the arguments.
    public EORepartCompte(EOEditingContext context, EOClassDescription classDesc, EOGlobalID gid) {
        super(context, classDesc, gid);
    }

    // If you add instance variables to store property values you
    // should add empty implementions of the Serialization methods
    // to avoid unnecessary overhead (the properties will be
    // serialized for you in the superclass).
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    }
*/

    public NSTimestamp dCreation() {
        return (NSTimestamp)storedValueForKey("dCreation");
    }

    public void setDCreation(NSTimestamp value) {
        takeStoredValueForKey(value, "dCreation");
    }

    public NSTimestamp dModification() {
        return (NSTimestamp)storedValueForKey("dModification");
    }

    public void setDModification(NSTimestamp value) {
        takeStoredValueForKey(value, "dModification");
    }

    public EOCompte toCompte() {
        return (EOCompte)storedValueForKey("toCompte");
    }

    public void setToCompte(EOCompte value) {
        takeStoredValueForKey(value, "toCompte");
    }

    public IpwIndividuUlr toIndividuUlr() {
        return (IpwIndividuUlr)storedValueForKey("toIndividuUlr");
    }

    public void setToIndividuUlr(IpwIndividuUlr value) {
        takeStoredValueForKey(value, "toIndividuUlr");
    }
}
