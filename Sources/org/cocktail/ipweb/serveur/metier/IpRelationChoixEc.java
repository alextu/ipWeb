package org.cocktail.ipweb.serveur.metier;
// IpRelationChoixEc.java
// Created on Wed Oct 18 09:40:21  2006 by Apple EOModeler Version 5.2

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.math.BigDecimal;
import java.util.*;

public class IpRelationChoixEc extends EOGenericRecord {

    public IpRelationChoixEc() {
        super();
    }

/*
    // If you implement the following constructor EOF will use it to
    // create your objects, otherwise it will use the default
    // constructor. For maximum performance, you should only
    // implement this constructor if you depend on the arguments.
    public IpRelationChoixEc(EOEditingContext context, EOClassDescription classDesc, EOGlobalID gid) {
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

    public Number mrecKeyCible() {
        return (Number)storedValueForKey("mrecKeyCible");
    }

    public void setMrecKeyCible(Number value) {
        takeStoredValueForKey(value, "mrecKeyCible");
    }

    public String rceTypeRelation() {
        return (String)storedValueForKey("rceTypeRelation");
    }

    public void setRceTypeRelation(String value) {
        takeStoredValueForKey(value, "rceTypeRelation");
    }

    public Number msemKey() {
        return (Number)storedValueForKey("msemKey");
    }

    public void setMsemKey(Number value) {
        takeStoredValueForKey(value, "msemKey");
    }

    public String rceFormuleContrainte() {
        return (String)storedValueForKey("rceFormuleContrainte");
    }

    public void setRceFormuleContrainte(String value) {
        takeStoredValueForKey(value, "rceFormuleContrainte");
    }

    public String rceCommentaireContrainte() {
        return (String)storedValueForKey("rceCommentaireContrainte");
    }

    public void setRceCommentaireContrainte(String value) {
        takeStoredValueForKey(value, "rceCommentaireContrainte");
    }

    public String rceEspaceSolution() {
        return (String)storedValueForKey("rceEspaceSolution");
    }

    public void setRceEspaceSolution(String value) {
        takeStoredValueForKey(value, "rceEspaceSolution");
    }

    public String rceListeVariables() {
        return (String)storedValueForKey("rceListeVariables");
    }

    public void setRceListeVariables(String value) {
        takeStoredValueForKey(value, "rceListeVariables");
    }

    public EOEnterpriseObject toRepartEcComment() {
        return (EOEnterpriseObject)storedValueForKey("toRepartEcComment");
    }

    public void setToRepartEcComment(EOEnterpriseObject value) {
        takeStoredValueForKey(value, "toRepartEcComment");
    }
}
