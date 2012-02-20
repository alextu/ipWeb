
// _IpDiplSansRn.java
// 
// Created by eogenerator
// DO NOT EDIT.  Make changes to IpDiplSansRn.java instead.


import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.math.BigDecimal;
import java.util.*;

public abstract class _IpDiplSansRn extends EOGenericRecord {

    public _IpDiplSansRn() {
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

    public Number fspnKey() {
        return (Number)storedValueForKey("fspnKey");
    }

    public void setFspnKey(Number aValue) {
        takeStoredValueForKey(aValue, "fspnKey");
    }
}
