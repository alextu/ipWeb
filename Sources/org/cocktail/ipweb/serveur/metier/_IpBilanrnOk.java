package org.cocktail.ipweb.serveur.metier;

// _IpBilanrnOk.java
// 
// Created by eogenerator
// DO NOT EDIT.  Make changes to IpBilanrnOk.java instead.


import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.math.BigDecimal;
import java.util.*;

public abstract class _IpBilanrnOk extends EOGenericRecord {

    public _IpBilanrnOk() {
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

    public Number fannKey() {
        return (Number)storedValueForKey("fannKey");
    }

    public void setFannKey(Number aValue) {
        takeStoredValueForKey(aValue, "fannKey");
    }

    public Number mrsemKey() {
        return (Number)storedValueForKey("mrsemKey");
    }

    public void setMrsemKey(Number aValue) {
        takeStoredValueForKey(aValue, "mrsemKey");
    }

    public Number rnaffEtat() {
        return (Number)storedValueForKey("rnaffEtat");
    }

    public void setRnaffEtat(Number aValue) {
        takeStoredValueForKey(aValue, "rnaffEtat");
    }
}
