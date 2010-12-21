/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;


public class LoadAndSaveImportsElementOrderTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveImportsElementOrderTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveImportsElementOrderTestCases" };
        TestRunner.main(arguments);
    }

    protected String getSchemaName() {
        return "Types.xsd";
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/employee_types.xml");
    }
    
    protected String getControlWriteFileName(){
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/employee_types.xml");
    }

    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/employee_types.xml");
    }

    protected String getControlRootURI() {
        return "http://www.example.org/emp";
    }

    protected String getControlRootName() {
        return "employee";
    }

    protected String getRootInterfaceName() {
        return "EmployeeType";
    }

    protected List defineTypes() {
        try {
            URL url = new URL(getSchemaLocation() + getSchemaName());
            InputStream is = url.openStream();
            return xsdHelper.define(is, getSchemaLocation());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xmlhelper/";
    }
     
    protected List getPackages() {
        List<String> packages = new ArrayList<String>();       
        packages.add("org/example/emp");
        return packages;
    }

    protected void generateClasses(String tmpDirName) throws Exception{
            
        URL url = new URL(getSchemaLocation() + getSchemaName());
        InputStream is = url.openStream();
        
        SDOClassGenerator classGenerator = new SDOClassGenerator(aHelperContext);

        DefaultSchemaResolver schemaResolver = new DefaultSchemaResolver();
        schemaResolver.setBaseSchemaLocation(getSchemaLocation());
        StreamSource ss = new StreamSource(is);
        classGenerator.generate(ss, tmpDirName, schemaResolver);
    }

    public void registerTypes() {

    }
    
    public void testNoSchemaLoadFromInputStreamSaveDataObjectToString() throws Exception {
    }    
}
