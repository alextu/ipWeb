/**
 * 
 */

/**
 * @author olive / 03/03/2008
 *
 * Objectf : fournir des données pour JasperReport sur le Relevé de Note généré
 * 	     afin d'imprimer les données modifiées à la volée !!!
 * 
 * rem : on s'appui trés fortement sur les objets déja existants et instanciés... 
 * 
 *  */

import net.sf.jasperreports.engine.*;
public class JRDataRN implements JRDataSource {

	private ReleveNotes monRN;
	
	/**
	 * 
	 */
	public JRDataRN(ReleveNotes leRN) {
		monRN = leRN;
		monRN.resetBoucle();	// remet à zéro la boucle de parcours...

	}
	
	public boolean next() throws JRException
	{
		return monRN.nextElement();	// vrai si on N'A PAS fini de parcourir les EC de l'�tudiant 
	}

	public Object getFieldValue(JRField jrField) throws JRException
	{
		String jrName = jrField.getName();
		return monRN.fetchJRChamp(jrName);
	}
}
