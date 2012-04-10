package org.cocktail.ipweb.serveur.controlleur;
/*
 * Créé le 29 nov. 2006
 *
 * @author olive
 * Objectif : lancer des impressions via JasperReports, pour toute l'appli...
 * Appel depuis : Application
 *
 */

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import com.lowagie.text.pdf.PdfWriter;
import com.webobjects.foundation.NSData;

public class ImprJasper {
    private String repertoireJasper;
    private String repertoireSortie;
    private java.sql.Connection chaineConnexionJDBC; 
    
    
//  public ImprJasper(String repJasper,java.sql.Connection connexionJDBC) {
    public ImprJasper(String repJasper,String repSortie,java.sql.Connection connexionJDBC) {
    	super();
    	repertoireJasper = repJasper;
    	repertoireSortie = repSortie;
    	chaineConnexionJDBC = connexionJDBC;
    }
    
    // On veut imprimer ce fichier en pdf, avec ces params via la connexion JDBC du mod�le...
    public NSData jasperPdf(String ficJasper,HashMap params) {
	return jasper(ficJasper,params,chaineConnexionJDBC,"Pdf");
    }
	
    // On veut imprimer ce fichier en XLS, avec ces params via la connexion JDBC du modele...
    public NSData jasperXLS(String ficJasper,HashMap params) {
    	return jasper(ficJasper,params,chaineConnexionJDBC,"XLS");
    }
        
    // On veut imprimer ce fichier en pdf, avec ces params et cette dataSource...
    public NSData jasperPdf(String ficJasper,HashMap params,Object datasource) {
	return jasper(ficJasper,params,datasource,"Pdf");
    }
    
    
    // On veut imprimer ce fichier en pdf, avec ces params et cette dataSource...
    public NSData jasper(String ficJasper,HashMap params,Object datasource, String typeDocSortie) {
	String fileName = repertoireJasper+ficJasper;
//	String fileOutPdf = repertoireSortie+"\\test.pdf";
        ByteArrayOutputStream tmpStream = new ByteArrayOutputStream();	// flux de sortie en RAM
        NSData dataSortie =null;
	
	try {
	    JasperPrint print = null;
	    if (datasource instanceof java.sql.Connection) {
		print = JasperFillManager.fillReport(
			fileName,
			params,
			(java.sql.Connection)datasource);

	    }
	    else if (datasource instanceof JRDataSource) {
		print = JasperFillManager.fillReport(
			fileName,
			params,
			(JRDataSource)datasource);
	    }
	    // on a l'objet qui va bosser sur notre report...
	    
	    JRExporter exporter;
	    
	    if (typeDocSortie.compareToIgnoreCase("XLS") == 0)
	    	exporter = exporterXLS(tmpStream, print);
	    else exporter = exporterPDF(tmpStream, print);
	        
	    // Que le bal commence :
	    exporter.exportReport();
	    dataSortie = new NSData(tmpStream.toByteArray());

	    System.out.println("Sortie PDF faites en mémoire : taille = "+dataSortie.length()/1024);
	    // export en m�moire !
	    
	}
	catch (JRException e){
	    e.printStackTrace();
	    dataSortie = null;
	}
	catch (Exception e){
	    e.printStackTrace();
	    dataSortie = null;
	}
    
	return dataSortie;
    }
    
 // Récupérer un objet Exporter de type JRXlsExporter
    private JRExporter exporterXLS(ByteArrayOutputStream tmpStream, JasperPrint print) {
	    JRXlsExporter exporter = new JRXlsExporter();
	    
        // coding For Excel:
        exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, print);
        exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, tmpStream);
        exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
	    
	    return exporter;
    }
    
    private JRExporter exporterPDF(ByteArrayOutputStream tmpStream, JasperPrint print) {
	    JRPdfExporter exporter = new JRPdfExporter();
	    
	    // encryption de tous les PDF sortis par l'appli.... le mot de passe est généré automatiquement par JRPdfExporter
	    exporter.setParameter(JRPdfExporterParameter.IS_ENCRYPTED , new Boolean(true));
	    exporter.setParameter(JRPdfExporterParameter.IS_128_BIT_KEY , new Boolean(true));
	    exporter.setParameter(JRPdfExporterParameter.PERMISSIONS , new Integer(PdfWriter.ALLOW_PRINTING));
	    
	    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, tmpStream);
	    exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
   	    
	    return exporter;
    }
    
    
}
