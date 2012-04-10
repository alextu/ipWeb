/*
 * Copyright COCKTAIL (www.cocktail.org), 1995, 2010 This software 
 * is governed by the CeCILL license under French law and abiding by the
 * rules of distribution of free software. You can use, modify and/or 
 * redistribute the software under the terms of the CeCILL license as 
 * circulated by CEA, CNRS and INRIA at the following URL 
 * "http://www.cecill.info". 
 * As a counterpart to the access to the source code and rights to copy, modify 
 * and redistribute granted by the license, users are provided only with a 
 * limited warranty and the software's author, the holder of the economic 
 * rights, and the successive licensors have only limited liability. In this 
 * respect, the user's attention is drawn to the risks associated with loading,
 * using, modifying and/or developing or reproducing the software by the user 
 * in light of its specific status of free software, that may mean that it
 * is complicated to manipulate, and that also therefore means that it is 
 * reserved for developers and experienced professionals having in-depth
 * computer knowledge. Users are therefore encouraged to load and test the 
 * software's suitability as regards their requirements in conditions enabling
 * the security of their systems and/or data to be ensured and, more generally, 
 * to use and operate it in the same conditions as regards security. The
 * fact that you are presently reading this means that you have had knowledge 
 * of the CeCILL license and that you accept its terms.
 */

package org.cocktail.ipweb.serveur;

/**
 * Cette classe ne doit pas heriter d'une classe autre que Object pour être lancee sans classpath. 
 * Logiquement vous ne devez modifier que les constantes statiques  des nº de version, la date et les commentaires
 */
public final class VersionMe extends Object {
	// Nom de l'appli
	static final String APPLICATIONFINALNAME="ipWeb";
	public static final String APPLICATIONINTERNALNAME="ipWeb";
	public static final String APPLICATION_STRID = "IPWEB";
    
	// Version appli
    public static final long SERIALVERSIONUID = 2400;
    
    public static final int VERSIONNUMMAJ =     2;
    public static final int VERSIONNUMMIN =     4;
    public static final int VERSIONNUMPATCH =   0;
    public static final int VERSIONNUMBUILD =   0;
	public static final int	VERSIONNUMRC = 0;
    
    public static final String VERSIONDATE="11/04/2012";
    public static final String COMMENT = null;
    
    
    /***
     * Ne pas modifier cette methode, elle est utilisee pour recuperer le nº de version formate a partir d'une tache ant.
     */
	public static void main(String[] args) {
		System.out.println( ""+VERSIONNUMMAJ+"." + VERSIONNUMMIN+"."+VERSIONNUMPATCH+"."+VERSIONNUMBUILD);
	}
    
	public static String appliVersion() {
		String appliVersion = VERSIONNUMMAJ + "." + VERSIONNUMMIN + "." + VERSIONNUMPATCH + "." + VERSIONNUMBUILD;
		if (VERSIONNUMRC>0) {
			appliVersion += "_rc" + VERSIONNUMRC;
		}
		return appliVersion;
	}
	public static String htmlAppliVersion() {
		String htmlAppliVersion = "<b>Version " + appliVersion();
		htmlAppliVersion += " du " + VERSIONDATE + "</b>";
		return htmlAppliVersion; 
	}

	public static String txtAppliVersion() {
		return "Version " + appliVersion() + " du " + VERSIONDATE;
	}

}
