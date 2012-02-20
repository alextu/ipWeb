// EOCompte.java
// Created on Fri Sep 29 03:16:13  2006 by Apple EOModeler Version 5.2

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.math.BigDecimal;
import java.util.*;

public class EOCompte extends EOGenericRecord {

    public EOCompte() {
        super();
    }

/*
    // If you implement the following constructor EOF will use it to
    // create your objects, otherwise it will use the default
    // constructor. For maximum performance, you should only
    // implement this constructor if you depend on the arguments.
    public EOCompte(EOEditingContext context, EOClassDescription classDesc, EOGlobalID gid) {
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

    public Number cptUidGid() {
        return (Number)storedValueForKey("cptUidGid");
    }

    public void setCptUidGid(Number value) {
        takeStoredValueForKey(value, "cptUidGid");
    }

    public String cptLogin() {
        return (String)storedValueForKey("cptLogin");
    }

    public void setCptLogin(String value) {
        takeStoredValueForKey(value, "cptLogin");
    }

    public String cptPasswd() {
        return (String)storedValueForKey("cptPasswd");
    }

    public void setCptPasswd(String value) {
        takeStoredValueForKey(value, "cptPasswd");
    }

    public String cptConnexion() {
        return (String)storedValueForKey("cptConnexion");
    }

    public void setCptConnexion(String value) {
        takeStoredValueForKey(value, "cptConnexion");
    }

    public String cptVlan() {
        return (String)storedValueForKey("cptVlan");
    }

    public void setCptVlan(String value) {
        takeStoredValueForKey(value, "cptVlan");
    }

    public String cptEmail() {
        return (String)storedValueForKey("cptEmail");
    }

    public void setCptEmail(String value) {
        takeStoredValueForKey(value, "cptEmail");
    }

    public String cptDomaine() {
        return (String)storedValueForKey("cptDomaine");
    }

    public void setCptDomaine(String value) {
        takeStoredValueForKey(value, "cptDomaine");
    }

    public String cptCharte() {
        return (String)storedValueForKey("cptCharte");
    }

    public void setCptCharte(String value) {
        takeStoredValueForKey(value, "cptCharte");
    }

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

    public Number vlans_priorite() {
        return (Number)storedValueForKey("vlans_priorite");
    }

    public void setVlans_priorite(Number value) {
        takeStoredValueForKey(value, "vlans_priorite");
    }

    public EOEnterpriseObject vlans() {
        return (EOEnterpriseObject)storedValueForKey("vlans");
    }

    public void setVlans(EOEnterpriseObject value) {
        takeStoredValueForKey(value, "vlans");
    }

    public NSArray toRptCompte() {
        return (NSArray)storedValueForKey("toRptCompte");
    }

    public void setToRptCompte(NSArray value) {
        takeStoredValueForKey(value, "toRptCompte");
    }

    public void addToToRptCompte(EORepartCompte object) {
        includeObjectIntoPropertyWithKey(object, "toRptCompte");
    }

    public void removeFromToRptCompte(EORepartCompte object) {
        excludeObjectFromPropertyWithKey(object, "toRptCompte");
    }
}
