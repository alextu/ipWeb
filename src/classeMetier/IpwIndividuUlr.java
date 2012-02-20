// IndividuUlr.java
// Created on Tue Oct 31 01:08:02  2006 by Apple EOModeler Version 5.2

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.math.BigDecimal;
import java.util.*;

public class IpwIndividuUlr extends EOGenericRecord {

    public IpwIndividuUlr() {
        super();
    }

/*
    // If you implement the following constructor EOF will use it to
    // create your objects, otherwise it will use the default
    // constructor. For maximum performance, you should only
    // implement this constructor if you depend on the arguments.
    public IndividuUlr(EOEditingContext context, EOClassDescription classDesc, EOGlobalID gid) {
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

    public Number persId() {
        return (Number)storedValueForKey("persId");
    }

    public void setPersId(Number value) {
        takeStoredValueForKey(value, "persId");
    }

    public String nomPatronymique() {
        return (String)storedValueForKey("nomPatronymique");
    }

    public void setNomPatronymique(String value) {
        takeStoredValueForKey(value, "nomPatronymique");
    }

    public String prenom() {
        return (String)storedValueForKey("prenom");
    }

    public void setPrenom(String value) {
        takeStoredValueForKey(value, "prenom");
    }

    public String cCivilite() {
        return (String)storedValueForKey("cCivilite");
    }

    public void setCCivilite(String value) {
        takeStoredValueForKey(value, "cCivilite");
    }

    public String nomUsuel() {
        return (String)storedValueForKey("nomUsuel");
    }

    public void setNomUsuel(String value) {
        takeStoredValueForKey(value, "nomUsuel");
    }

    public NSTimestamp dNaissance() {
        return (NSTimestamp)storedValueForKey("dNaissance");
    }

    public void setDNaissance(NSTimestamp value) {
        takeStoredValueForKey(value, "dNaissance");
    }

    public String villeDeNaissance() {
        return (String)storedValueForKey("villeDeNaissance");
    }

    public void setVilleDeNaissance(String value) {
        takeStoredValueForKey(value, "villeDeNaissance");
    }

    public NSTimestamp dDeces() {
        return (NSTimestamp)storedValueForKey("dDeces");
    }

    public void setDDeces(NSTimestamp value) {
        takeStoredValueForKey(value, "dDeces");
    }

    public String indCSituationFamille() {
        return (String)storedValueForKey("indCSituationFamille");
    }

    public void setIndCSituationFamille(String value) {
        takeStoredValueForKey(value, "indCSituationFamille");
    }

    public String indQualite() {
        return (String)storedValueForKey("indQualite");
    }

    public void setIndQualite(String value) {
        takeStoredValueForKey(value, "indQualite");
    }

    public String indPhoto() {
        return (String)storedValueForKey("indPhoto");
    }

    public void setIndPhoto(String value) {
        takeStoredValueForKey(value, "indPhoto");
    }

    public String indActivite() {
        return (String)storedValueForKey("indActivite");
    }

    public void setIndActivite(String value) {
        takeStoredValueForKey(value, "indActivite");
    }

    public String temSsDiplome() {
        return (String)storedValueForKey("temSsDiplome");
    }

    public void setTemSsDiplome(String value) {
        takeStoredValueForKey(value, "temSsDiplome");
    }

    public String temValide() {
        return (String)storedValueForKey("temValide");
    }

    public void setTemValide(String value) {
        takeStoredValueForKey(value, "temValide");
    }

    public String languePref() {
        return (String)storedValueForKey("languePref");
    }

    public void setLanguePref(String value) {
        takeStoredValueForKey(value, "languePref");
    }

    public Number categoriePrinc() {
        return (Number)storedValueForKey("categoriePrinc");
    }

    public void setCategoriePrinc(Number value) {
        takeStoredValueForKey(value, "categoriePrinc");
    }

    public EOEnterpriseObject toEtudiant() {
        return (EOEnterpriseObject)storedValueForKey("toEtudiant");
    }

    public void setToEtudiant(EOEnterpriseObject value) {
        takeStoredValueForKey(value, "toEtudiant");
    }

    public VSituationsIndividu vSituationsIndividu() {
        return (VSituationsIndividu)storedValueForKey("vSituationsIndividu");
    }

    public void setVSituationsIndividu(VSituationsIndividu value) {
        takeStoredValueForKey(value, "vSituationsIndividu");
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
