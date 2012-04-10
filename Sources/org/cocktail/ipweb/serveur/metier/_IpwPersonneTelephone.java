package org.cocktail.ipweb.serveur.metier;

// _PersonneTelephone.java
// 
// Created by eogenerator
// DO NOT EDIT.  Make changes to PersonneTelephone.java instead.


import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.math.BigDecimal;
import java.util.*;

public abstract class _IpwPersonneTelephone extends EOGenericRecord {

    public _IpwPersonneTelephone() {
        super();
    }

/*
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

    public void setDCreation(NSTimestamp aValue) {
        takeStoredValueForKey(aValue, "dCreation");
    }

    public NSTimestamp dModification() {
        return (NSTimestamp)storedValueForKey("dModification");
    }

    public void setDModification(NSTimestamp aValue) {
        takeStoredValueForKey(aValue, "dModification");
    }

    public String noTelephone() {
        return (String)storedValueForKey("noTelephone");
    }

    public void setNoTelephone(String aValue) {
        takeStoredValueForKey(aValue, "noTelephone");
    }

    public Number persId() {
        return (Number)storedValueForKey("persId");
    }

    public void setPersId(Number aValue) {
        takeStoredValueForKey(aValue, "persId");
    }

    public String typeNo() {
        return (String)storedValueForKey("typeNo");
    }

    public void setTypeNo(String aValue) {
        takeStoredValueForKey(aValue, "typeNo");
    }

    public String typeTel() {
        return (String)storedValueForKey("typeTel");
    }

    public void setTypeTel(String aValue) {
        takeStoredValueForKey(aValue, "typeTel");
    }
}
