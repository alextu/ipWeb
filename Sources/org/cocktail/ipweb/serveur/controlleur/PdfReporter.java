package org.cocktail.ipweb.serveur.controlleur;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

/** Objectifs :
 * sous-classer JRPdfExporter pour avoir acc�s � la m�thode d'�criture en m�moire !!!
 */

/**
 * @author olive
 *
 */
public class PdfReporter extends JRPdfExporter {

    /**
     * 
     */
    public PdfReporter(JasperPrint jprint) {
	super();
	jasperPrint = jprint;
    }
    
    public void exportPdfReportToStream(java.io.OutputStream os) throws JRException {
	super.exportReportToStream(os);
    }

}
