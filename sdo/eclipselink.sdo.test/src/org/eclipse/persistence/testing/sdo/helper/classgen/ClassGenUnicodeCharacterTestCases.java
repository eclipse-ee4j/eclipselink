package org.eclipse.persistence.testing.sdo.helper.classgen;

import static org.eclipse.persistence.sdo.SDOConstants.EMPTY_STRING;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import junit.textui.TestRunner;

public class ClassGenUnicodeCharacterTestCases extends SDOClassGenTestCases {

    public ClassGenUnicodeCharacterTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.classgen.BaseTypeTestCases" };
        TestRunner.main(arguments);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/classgen/DeptView.xsd";
    }

    protected String getSourceFolder() {
        return "./unicode";
    }

    protected String getControlSourceFolder() {
        return "./org/eclipse/persistence/testing/sdo/helper/classgen/unicode";
    }

    protected List getControlFileNames() {
        ArrayList list = new ArrayList();
        list.add("DeptView\u00c9SDO.java");
        list.add("DeptView\u00c9SDOImpl.java");
        return list;
    }
    
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();       
        packages.add("model/mbcs/common");
        packages.add("model/mbcs/common");        
        return packages;
    }
    
    public String getSchema(InputStream is, String fileName) {
        String xsdSchema = EMPTY_STRING;        
        try {            
            InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(isr);
            String nextLine = reader.readLine();
            while(nextLine != null) {
                xsdSchema += nextLine;
                nextLine = reader.readLine();
            }
            log(xsdSchema);
            return xsdSchema;
        } catch (Exception e) {
            log(getClass().toString() + ": Reading error for : " + fileName + " message: " + e.getClass() + " " + e.getMessage());
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return xsdSchema;
    }

}
