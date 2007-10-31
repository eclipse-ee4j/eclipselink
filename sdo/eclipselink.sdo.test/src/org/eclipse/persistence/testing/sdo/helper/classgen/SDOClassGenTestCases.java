/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.classgen;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.helper.ClassBuffer;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.SDOXMLHelperTestCases;
import org.eclipse.persistence.testing.sdo.util.CompileUtil;

public abstract class SDOClassGenTestCases extends SDOXMLHelperTestCases {
    protected SDOClassGenerator classGenerator;
    protected String xsdString;

    public SDOClassGenTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        File f = new File(getSourceFolder());
        emptyAndDeleteDirectory(f);

        xsdString = getSchema(getSchemaName());
        classGenerator = new SDOClassGenerator(aHelperContext);
    }
    
    public void tearDown() throws Exception{      
        for (int i = 0; i < getFileNamesToCompile().size(); i++) {
            String nextFileName = (String)getFileNamesToCompile().get(i);
            String fullJavaName = getControlSourceFolder() + "/" + getPackageDir() + nextFileName;
            fullJavaName = fullJavaName.replace(".java", ".class");
            File f = new File(fullJavaName);
            if(f.exists()){
              f.delete();
            }            
        }
    }

  

    protected abstract String getSourceFolder();

    protected abstract String getControlSourceFolder();

    protected abstract String getSchemaName();

    protected abstract List getControlFileNames();

    protected HashMap getGeneratedFiles(Map generatedBuffers) {
        HashMap generatedFiles = new HashMap();

        Iterator keysIter = generatedBuffers.keySet().iterator();
        while (keysIter.hasNext()) {
            Object nextKey = keysIter.next();
            ClassBuffer next = (ClassBuffer)generatedBuffers.get(nextKey);
            generatedFiles.put(next.getInterfaceName() + ".java", next.getInterfaceBuffer().toString());
            generatedFiles.put(next.getClassName() + ".java", next.getClassBuffer().toString());
        }

        return generatedFiles;
    }

    protected HashMap getControlFiles() {
        HashMap controlFiles = new HashMap();
        List controlFileNames = getControlFileNames();
        for (int i = 0; i < controlFileNames.size(); i++) {
            String nextFileName = (String)controlFileNames.get(i);
            String fullName = getControlSourceFolder() + "/" + getPackageDir() + nextFileName;
            String classContents = getSchema(fullName);
            controlFiles.put(nextFileName, classContents);
        }
        return controlFiles;
    }

    protected String getPackageDir() {
        return "defaultPackage/";
    }    

    protected void compareFiles(HashMap controlFiles, HashMap generatedFiles) {
        assertEquals(controlFiles.size(), generatedFiles.size());
        Iterator keysIter = controlFiles.keySet().iterator();
        while (keysIter.hasNext()) {
            String nextKey = (String)keysIter.next();
            String nextControlValue = (String)controlFiles.get(nextKey);
            String nextGeneratedValue = (String)generatedFiles.get(nextKey);
            assertNotNull(nextControlValue);
            assertNotNull(nextGeneratedValue);
            // convert ignoreCRLF
            assertStringsEqual(nextControlValue, nextGeneratedValue);
        }
    }
    
    protected List getFileNamesToCompile(){ 
        return getControlFileNames();    
    }
    
    public void compileFiles(){
    	Object[] javaFiles = new Object[getFileNamesToCompile().size()];

        for (int i = 0; i < getFileNamesToCompile().size(); i++) {
            String nextFileName = (String) getFileNamesToCompile().get(i);
            String fullName = getControlSourceFolder() + "/" + getPackageDir() + nextFileName;
            javaFiles[i] = fullName;
        }

        int returnVal = CompileUtil.instance().compile(classgenCompilePath, javaFiles);
        assertEquals(0, returnVal);
    }

    
    // The following test case is out of scope for ClassGenElements - we let it fail with a NPE that generates an empty xsdString for this suite
    public void testClassGen() throws Exception {
        //compileFiles(getControlSourceFolder() + "/" + getPackageDir());
        compileFiles();
        StringReader reader = new StringReader(xsdString);

        classGenerator.generate(reader, getSourceFolder());
        int numGenerated = classGenerator.getGeneratedBuffers().size();
        assertEquals(getControlFileNames().size() / 2, numGenerated);

        compareFiles(getControlFiles(), getGeneratedFiles(classGenerator.getGeneratedBuffers()));
    }
}
