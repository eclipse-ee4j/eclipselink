/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.helper.classgen;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.helper.FileCodeWriter;

public class ClassGenWithImportsDontProcessTestCases extends SDOClassGenTestCases {
  
    public ClassGenWithImportsDontProcessTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.classgen.ClassGenWithImportsDontProcessTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();      
        try {
            // File rootDir = new File("./org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/");
            //URL rootURL = rootDir.toURL();
            //TODO: do we need to define types before generating classes???                
            String schemaLocation = FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/";
            xsdHelper.define(new StringReader(xsdString), schemaLocation);        
        } catch (Exception e) {
            e.printStackTrace();
            fail("failed during setup");
        }
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/ImportsWithNamespaces.xsd";
    }

    protected String getSourceFolder() {
        return "./srcImportsDontProcess";
    }

    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();
        for(int i = 0;i < getControlFileNames().size();i++) {
        	packages.add("uri/my");
        }
        return packages;
    }
    
    public void testClassGen() throws Exception {
        StringReader reader = new StringReader(xsdString);
        org.eclipse.persistence.sdo.helper.DefaultSchemaResolver schemaResolver = new org.eclipse.persistence.sdo.helper.DefaultSchemaResolver();

        String schemaLocation = FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/";
        schemaResolver.setBaseSchemaLocation(schemaLocation);
        //classGenerator.generate(reader, getSourceFolder(), schemaResolver);
        FileCodeWriter fileCodeWriter = new FileCodeWriter();
        fileCodeWriter.setSourceDir(getSourceFolder());
        classGenerator.generate(reader, fileCodeWriter, schemaResolver, false);

        int numGenerated = classGenerator.getGeneratedBuffers().size();
        assertEquals(1, numGenerated);
        compareFiles(getControlFiles(), getGeneratedFiles(classGenerator.getGeneratedBuffers()));
    }

    protected String getControlSourceFolder() {
        return "./org/eclipse/persistence/testing/sdo/helper/classgen/srcImportsDontProcess/";
    }

    protected List<String> getControlFileNames() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("PurchaseOrder.java");
        list.add("PurchaseOrderImpl.java");        
        return list;
    }
}
