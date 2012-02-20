/**
 * Olive le samedi 2/12/06
 * 
 * Objectf : fournir des donn�es pour JasperReport sur le semestre actuellement choisi
 * 	     afin de ne pas avoir � fetcher une expression SQL complexe !!!
 * 
 * rem : on s'appui tr�s fortement sur les objets d�ja existants et instanci�s... 
 *
 */

import net.sf.jasperreports.engine.*;
public class JRDataChoixEtud implements JRDataSource
{
	private InscSemestreCtrlr inscCtSem;

	public JRDataChoixEtud(InscSemestreCtrlr ctrlSem)
	{
		inscCtSem = ctrlSem;
		inscCtSem.resetBoucle();	// remet à zéro la boucle de parcours...
	}

	public boolean next() throws JRException
	{
		return inscCtSem.nextElement();	// vrai si on N'A PAS fini de parcourir les EC de l'�tudiant 
	}

	public Object getFieldValue(JRField jrField) throws JRException
	{
		String jrName = (jrField.getName()).toUpperCase();
		return inscCtSem.fetchJRChamp(jrName);
	}
}

