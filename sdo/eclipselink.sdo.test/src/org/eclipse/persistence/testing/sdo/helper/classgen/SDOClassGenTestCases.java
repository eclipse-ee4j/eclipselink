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

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sdo.helper.ClassBuffer;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.SDOXMLHelperTestCases;
import org.eclipse.persistence.testing.sdo.util.CompileUtil;

public abstract class SDOClassGenTestCases extends SDOXMLHelperTestCases {
    protected SDOClassGenerator classGenerator;
    protected String xsdString;
    /** Cache the packageNames ArrayList for memory performance improvement, fileNames should be optimized as well*/
    protected List<String> packageNames;

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
        super.tearDown();
        List<String> packages = getPackages();
        for (int i = 0; i < getFileNamesToCompile().size(); i++) {
            String nextFileName = getFileNamesToCompile().get(i);
            String nextPackageDir = packages.get(i);
            StringBuffer fullJavaName = new StringBuffer(getControlSourceFolder());
            fullJavaName.append("/");
            fullJavaName.append(nextPackageDir);
            fullJavaName.append("/");
            fullJavaName.append(nextFileName);            
            File f = new File(fullJavaName.toString().replace(".java", ".class"));
            if(f.exists()){
              f.delete();
            }            
        }
    }

    protected abstract String getSourceFolder();

    protected abstract String getControlSourceFolder();

    protected abstract String getSchemaName();

    protected abstract List<String> getControlFileNames();

    protected HashMap<String, String> getGeneratedFiles(Map<Object, ClassBuffer> generatedBuffers) {
        HashMap<String, String> generatedFiles = new HashMap<String, String>();

        Iterator<Object> keysIter = generatedBuffers.keySet().iterator();
        while (keysIter.hasNext()) {
            Object nextKey = keysIter.next();
            ClassBuffer next = generatedBuffers.get(nextKey);
            generatedFiles.put(Helper.getShortClassName(next.getSdoType().getInstanceClassName()) + ".java", next.getInterfaceBuffer().toString());
            generatedFiles.put(Helper.getShortClassName(next.getSdoType().getImplClassName()) +".java", next.getClassBuffer().toString());
        }

        return generatedFiles;
    }

    protected HashMap<String, String> getControlFiles() {
        HashMap<String, String> controlFiles = new HashMap<String, String>();
        List<String> controlFileNames = getControlFileNames();
        List<String> packages = getPackages();
        for (int i = 0; i < controlFileNames.size(); i++) {
            String nextFileName = controlFileNames.get(i);
            String nextPackageDir = packages.get(i);
            StringBuffer fullName = new StringBuffer(getControlSourceFolder());
            fullName.append("/");
            fullName.append(nextPackageDir);
            fullName.append("/");
            fullName.append(nextFileName);
            String classContents = getSchema(fullName.toString());
            controlFiles.put(nextFileName, classContents);
        }
        return controlFiles;
    }

    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    protected List<String> getPackages() {
    	if(null != packageNames && packageNames.size() > 0) {
    		return packageNames;
    	} else {
    		packageNames = new ArrayList<String>();
    		for(int i = 0;i < getControlFileNames().size();i++) {
    			packageNames.add(NON_DEFAULT_JAVA_PACKAGE_DIR);
    		}
    	}
        return packageNames;
    }

    protected void compareFiles(HashMap<String, String> controlFiles, HashMap<String, String> generatedFiles) {
        assertEquals(controlFiles.size(), generatedFiles.size());
        Iterator<String> keysIter = controlFiles.keySet().iterator();
        while (keysIter.hasNext()) {
            String nextKey = keysIter.next();
            String nextControlValue = controlFiles.get(nextKey);
            String nextGeneratedValue = generatedFiles.get(nextKey);
            assertNotNull(nextControlValue);
            assertNotNull(nextGeneratedValue);
            // convert ignoreCRLF
            assertStringsEqual(nextControlValue, nextGeneratedValue);
        }
    }
    
    protected List<String> getFileNamesToCompile(){ 
        return getControlFileNames();    
    }
    
    public void compileFiles(){
    	Object[] javaFiles = new Object[getFileNamesToCompile().size()];
        List<String> packages = getPackages();
        for (int i = 0; i < getFileNamesToCompile().size(); i++) {
            String nextFileName = getFileNamesToCompile().get(i);
            String nextPackageDir = packages.get(i);
            StringBuffer fullName = new StringBuffer(getControlSourceFolder());
            fullName.append("/");
            fullName.append(nextPackageDir);
            fullName.append("/");
            fullName.append(nextFileName);
            javaFiles[i] = fullName.toString();
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
