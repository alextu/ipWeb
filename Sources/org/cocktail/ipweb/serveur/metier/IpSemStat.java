package org.cocktail.ipweb.serveur.metier;
// IpSemStat.java
// Created on Mon Jul 23 22:40:52  2007 by Apple EOModeler Version 5.2

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.math.BigDecimal;
import java.util.*;

public class IpSemStat extends EOGenericRecord {

    public IpSemStat() {
        super();
    }

/*
    // If you implement the following constructor EOF will use it to
    // create your objects, otherwise it will use the default
    // constructor. For maximum performance, you should only
    // implement this constructor if you depend on the arguments.
    public IpSemStat(EOEditingContext context, EOClassDescription classDesc, EOGlobalID gid) {
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

    public Number cumulEcts() {
        return (Number)storedValueForKey("cumulEcts");
    }

    public void setCumulEcts(Number value) {
        takeStoredValueForKey(value, "cumulEcts");
    }

    public Number fannKey() {
        return (Number)storedValueForKey("fannKey");
    }

    public void setFannKey(Number value) {
        takeStoredValueForKey(value, "fannKey");
    }

    public Number idiplNumero() {
        return (Number)storedValueForKey("idiplNumero");
    }

    public void setIdiplNumero(Number value) {
        takeStoredValueForKey(value, "idiplNumero");
    }

    public String inscPsIncomplete() {
        return (String)storedValueForKey("inscPsIncomplete");
    }
    
    public String choixValides() {
        return (String)storedValueForKey("choixValides");
    }

    public void setInscPsIncomplete(String value) {
        takeStoredValueForKey(value, "inscPsIncomplete");
    }

    public void setChoixValides(String value) {
        takeStoredValueForKey(value, "choixValides");
    }
    
    public Number mrsemKeyPs() {
        return (Number)storedValueForKey("mrsemKeyPs");
    }

    public void setMrsemKeyPs(Number value) {
        takeStoredValueForKey(value, "mrsemKeyPs");
    }

    public Number msemKey() {
        return (Number)storedValueForKey("msemKey");
    }

    public void setMsemKey(Number value) {
        takeStoredValueForKey(value, "msemKey");
    }

    public Number nbUeIncompletes() {
        return (Number)storedValueForKey("nbUeIncompletes");
    }

    public void setNbUeIncompletes(Number value) {
        takeStoredValueForKey(value, "nbUeIncompletes");
    }

    public NSTimestamp ssDateModif() {
        return (NSTimestamp)storedValueForKey("ssDateModif");
    }

    public void setSsDateModif(NSTimestamp value) {
        takeStoredValueForKey(value, "ssDateModif");
    }
}
