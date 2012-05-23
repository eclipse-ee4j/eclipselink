/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - September 25, 2009
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class LoadAndSaveIncludeWithExtensionTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveIncludeWithExtensionTestCases(String name) {
        super(name);
    }
    
    protected List defineTypes() {
        try {
            
        	String xsd = FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xmlhelper/includeWithExtension/WithInclude.xsd";
        	
            DefaultSchemaResolver sr = new DefaultSchemaResolver();
            sr.setBaseSchemaLocation(getSchemaLocation());            
            
            return ((SDOXSDHelper)xsdHelper).define(new StreamSource(xsd), sr);            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    protected String getUnrelatedSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/OrderBookingPO.xsd";
    }

    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xmlhelper/includeWithExtension/";
    }

    protected String getSchemaName() {
        return "WithInclude.xsd";
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/includeWithExtension/withInclude.xml");
    }

    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/includeWithExtension/withIncludeNoTypes.xml");
    }
    
     protected String getControlWriteFileName() {
            
       return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/includeWithExtension/withInclude.xml");      
    }

    protected String getControlRootURI() {
        return "my.uri";
    }

    protected String getControlRootName() {
        return "root";
    }
    
    protected String getRootInterfaceName() {
        return "ChildType";
    }
    
    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();       
        packages.add("uri/my");
        return packages;
    }
    
    protected void generateClasses(String tmpDirName) throws Exception{
        
    	String xsd = FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xmlhelper/includeWithExtension/WithInclude.xsd";
    	
        DefaultSchemaResolver sr = new DefaultSchemaResolver();
        sr.setBaseSchemaLocation(getSchemaLocation());
        
        SDOClassGenerator classGenerator = new SDOClassGenerator(aHelperContext);
        classGenerator.generate(new StreamSource(xsd), tmpDirName, sr);    
    }
        
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveWithImportsTestCases" };
        TestRunner.main(arguments);
    }

    
    
    public void registerTypes() {
        
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        DataObject rootType = defineType("my.uri", "Root");
        addProperty(rootType, "aaa", stringType, false, false, true);
        addProperty(rootType, "bbb", stringType, false, false, true);
        Type rootSDOType = typeHelper.define(rootType);
     
        DataObject childType = defineType("my.uri", "ChildType");
        addProperty(childType, "ccc", stringType, false, false, true);
        addProperty(childType, "ddd", stringType, false, false, true);

        List baseTypes = new ArrayList();
        baseTypes.add(rootSDOType);
        childType.set("baseType", baseTypes);
        
        Type childSDOType = typeHelper.define(childType);        
        
        DataObject rootPropDO = dataFactory.create(propertyType);
        rootPropDO.set("name", getControlRootName());
        rootPropDO.set("type", rootSDOType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), rootPropDO);
    }
    
    public void verifyAfterLoad(XMLDocument doc){
    	DataObject rootDO = doc.getRootObject();
    	rootDO.getType();
    	
    	
    }
}
