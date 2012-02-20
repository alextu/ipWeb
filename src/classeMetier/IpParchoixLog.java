// IpParchoixLog.java
// Created on Mon Jul 16 05:21:28  2007 by Apple EOModeler Version 5.2

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.math.BigDecimal;
import java.util.*;

public class IpParchoixLog extends EOGenericRecord {

    public IpParchoixLog() {
        super();
    }

/*
    // If you implement the following constructor EOF will use it to
    // create your objects, otherwise it will use the default
    // constructor. For maximum performance, you should only
    // implement this constructor if you depend on the arguments.
    public IpParchoixLog(EOEditingContext context, EOClassDescription classDesc, EOGlobalID gid) {
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

    public Number fannKey() {
        return (Number)storedValueForKey("fannKey");
    }

    public void setFannKey(Number value) {
        takeStoredValueForKey(value, "fannKey");
    }

    public Number msemOrdre() {
        return (Number)storedValueForKey("msemOrdre");
    }
    
    public void setMsemOrdre(Number value) {
        takeStoredValueForKey(value, "msemOrdre");
    }
    
    public Number idiplNumero() {
        return (Number)storedValueForKey("idiplNumero");
    }

    public Number etudNumero() {
        return (Number)storedValueForKey("etudNumero");
    }

    public void setIdiplNumero(Number value) {
        takeStoredValueForKey(value, "idiplNumero");
    }

    public void setEtudNumero(Number value) {
        takeStoredValueForKey(value, "etudNumero");
    }

    public Number mrsemKey() {
        return (Number)storedValueForKey("mrsemKey");
    }

    public String typeAction() {
        return (String)storedValueForKey("typeAction");
    }

    public void setTypeAction(String value) {
        takeStoredValueForKey(value, "typeAction");
    }

    public void setMrsemKey(Number value) {
        takeStoredValueForKey(value, "mrsemKey");
    }

    public NSTimestamp pclDateLog() {
        return (NSTimestamp)storedValueForKey("pclDateLog");
    }

    public void setPclDateLog(NSTimestamp value) {
        takeStoredValueForKey(value, "pclDateLog");
    }
}
