package org.cocktail.ipweb.serveur.metier;


// PersonneTelephone.java
// 



import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.math.BigDecimal;
import java.util.*;

public class IpwPersonneTelephone extends _IpwPersonneTelephone
{
    public IpwPersonneTelephone() {
        super();
    }

    public void completeInit(Number persId,String noTel,String typeNo,String typeTel,NSTimestamp dateCreate) {
        
        setPersId(persId);
        
        if (noTel == null || noTel.length()==0) {
            noTel="indefini";
        }
        else if (noTel.length() > 14) noTel = noTel.substring(0,14);
        setNoTelephone(noTel);

        setTypeNo(typeNo);
        setTypeTel(typeTel);
        
        setDCreation(dateCreate);
        setDModification(new NSTimestamp());
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

}
