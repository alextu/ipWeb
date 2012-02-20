import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

/** Objectifs :
 * sous-classer JRPdfExporter pour avoir accés à la méthode d'écriture en mémoire !!!
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
