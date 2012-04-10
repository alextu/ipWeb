package org.cocktail.ipweb.serveur.metier;
// IpChoixLog.java
// Created on Tue Oct 24 00:58:47  2006 by Apple EOModeler Version 5.2

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.math.BigDecimal;
import java.util.*;

public class IpChoixLog extends EOGenericRecord {

    public IpChoixLog() {
        super();
    }

/*
    // If you implement the following constructor EOF will use it to
    // create your objects, otherwise it will use the default
    // constructor. For maximum performance, you should only
    // implement this constructor if you depend on the arguments.
    public IpChoixLog(EOEditingContext context, EOClassDescription classDesc, EOGlobalID gid) {
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

    public Number idiplNumero() {
        return (Number)storedValueForKey("idiplNumero");
    }

    public void setIdiplNumero(Number value) {
        takeStoredValueForKey(value, "idiplNumero");
    }

    public Number imrecSemestre() {
        return (Number)storedValueForKey("imrecSemestre");
    }

    public void setImrecSemestre(Number value) {
        takeStoredValueForKey(value, "imrecSemestre");
    }

    public Number mrecKey() {
        return (Number)storedValueForKey("mrecKey");
    }

    public void setMrecKey(Number value) {
        takeStoredValueForKey(value, "mrecKey");
    }

    public String etatChoix() {
        return (String)storedValueForKey("etatChoix");
    }

    public void setEtatChoix(String value) {
        takeStoredValueForKey(value, "etatChoix");
    }

    public NSTimestamp clDateLog() {
        return (NSTimestamp)storedValueForKey("clDateLog");
    }

    public void setClDateLog(NSTimestamp value) {
        takeStoredValueForKey(value, "clDateLog");
    }

    public Number msemKey() {
        return (Number)storedValueForKey("msemKey");
    }

    public void setMsemKey(Number value) {
        takeStoredValueForKey(value, "msemKey");
    }
}
