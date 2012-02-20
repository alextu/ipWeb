// VSituationsIndividu.java
// Created on Sat Oct 28 02:41:04  2006 by Apple EOModeler Version 5.2

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.math.BigDecimal;
import java.util.*;

public class VSituationsIndividu extends EOGenericRecord {

    public VSituationsIndividu() {
        super();
    }

/*
    // If you implement the following constructor EOF will use it to
    // create your objects, otherwise it will use the default
    // constructor. For maximum performance, you should only
    // implement this constructor if you depend on the arguments.
    public VSituationsIndividu(EOEditingContext context, EOClassDescription classDesc, EOGlobalID gid) {
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

    public Number noIndividu() {
        return (Number)storedValueForKey("noIndividu");
    }

    public void setNoIndividu(Number value) {
        takeStoredValueForKey(value, "noIndividu");
    }

    public Number actuelEns() {
        return (Number)storedValueForKey("actuelEns");
    }

    public void setActuelEns(Number value) {
        takeStoredValueForKey(value, "actuelEns");
    }

    public Number actuelNonEns() {
        return (Number)storedValueForKey("actuelNonEns");
    }

    public void setActuelNonEns(Number value) {
        takeStoredValueForKey(value, "actuelNonEns");
    }

    public Number actuelVac() {
        return (Number)storedValueForKey("actuelVac");
    }

    public void setActuelVac(Number value) {
        takeStoredValueForKey(value, "actuelVac");
    }

    public Number ancienEns() {
        return (Number)storedValueForKey("ancienEns");
    }

    public void setAncienEns(Number value) {
        takeStoredValueForKey(value, "ancienEns");
    }

    public Number ancienNonEns() {
        return (Number)storedValueForKey("ancienNonEns");
    }

    public void setAncienNonEns(Number value) {
        takeStoredValueForKey(value, "ancienNonEns");
    }

    public Number ancienVac() {
        return (Number)storedValueForKey("ancienVac");
    }

    public void setAncienVac(Number value) {
        takeStoredValueForKey(value, "ancienVac");
    }

    public Number actuelEtud() {
        return (Number)storedValueForKey("actuelEtud");
    }

    public void setActuelEtud(Number value) {
        takeStoredValueForKey(value, "actuelEtud");
    }

    public Number ancienEtud() {
        return (Number)storedValueForKey("ancienEtud");
    }

    public void setAncienEtud(Number value) {
        takeStoredValueForKey(value, "ancienEtud");
    }
}
