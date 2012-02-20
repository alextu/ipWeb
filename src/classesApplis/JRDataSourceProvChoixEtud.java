
/**
 * @author olive / samedi 2 d�cembre 2006
 * Impl�menter l'interface avec JasperReport pour lui pr�senter les champs de donn�es...
 * ce n'est pas un pr�-requis !
 */

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignField;

public class JRDataSourceProvChoixEtud implements JRDataSourceProvider {
    private JRDataSource dataSource;
    
    // on passe le JRDataSource � la cr�ation, qui doit avoir �t� initialis� avant...
    public JRDataSourceProvChoixEtud(JRDataSource laDataSource) {
    }

    public boolean supportsGetFieldsOperation() {
    	return true;
    }

    // renvoyer la liste des champs qui peuvent apparaitre dans le report...
    public JRField[] getFields(JasperReport jasperReport) throws
    JRException, UnsupportedOperationException {
    	JRField[] fields = new JRField[3];
    	JRDesignField field1 = new JRDesignField();
    	field1.setName("FILENAME");
    	field1.setValueClass(String.class);
    	field1.setValueClassName(String.class.getName());
    	fields[0] = field1;
    	JRDesignField field2 = new JRDesignField();
    	field2.setName("IS_DIRECTORY");
    	field2.setValueClass(Boolean.class);
    	field2.setValueClassName(Boolean.class.getName());
    	fields[1] = field2;
    	JRDesignField field3 = new JRDesignField(); 

    	return fields;
    }

    // appel� par le framework iReport au d�but de la g�n�ration du rapport
    // permet a l'appli de constituer �ventuellement des ressources (objets temporaires)
    public JRDataSource create(JasperReport arg0) throws JRException {
    	return dataSource;
    }

    // appel� par le framework iReport � la fin de la g�n�ration du rapport
    // permet a l'appli de lib�rer �ventuellement des ressources (objets temporaires)
    public void dispose(JRDataSource jRDataSource) throws
    JRException {
    }

}
