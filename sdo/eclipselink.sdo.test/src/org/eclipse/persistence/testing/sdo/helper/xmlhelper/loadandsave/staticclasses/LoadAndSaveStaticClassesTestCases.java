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
 *     Denise Smith - September 17, 2009
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.staticclasses;

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
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveTestCases;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class LoadAndSaveStaticClassesTestCases extends LoadAndSaveTestCases{
	 public LoadAndSaveStaticClassesTestCases(String name) {
	        super(name);
	    }

	    public static void main(String[] args) {
	        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.staticclasses.LoadAndSaveStaticClassesTestCases" };
	        TestRunner.main(arguments);
	    }
	    
	    protected void verifyAfterLoad(XMLDocument document) {
	        super.verifyAfterLoad(document);
	    }

	    protected String getControlFileName() {
	        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/staticclasses/Company.xml";
	    }
	
	    protected String getSchemaName() {
	        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/staticclasses/Company.xsd";
	    }

	    protected String getNoSchemaControlFileName() {
	        return getControlFileName();
	    }

	    protected String getSchemaLocation() {
	        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/staticclasses/";
	    }

	    protected String getControlRootURI() {
	        return "http://theUri";
	    }

	    protected String getControlRootName() {
	        return "company";
	    }

	    protected String getRootInterfaceName() {
	        return "CompanyType";
	    }
	    
	    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
	    protected List<String> getPackages() {
	        List<String> packages = new ArrayList<String>();
	        packages.add(NON_DEFAULT_JAVA_PACKAGE_DIR);
	        return packages;
	    }
	    
	    public void testClassGenerationLoadAndSave(){
	    	 //not applicable since we have static classes there is no need to generate 
 	    }
	    
	      
	    public void registerTypes() {
	        SDOType stringType = (SDOType) typeHelper.getType("commonj.sdo", "String");
	        SDOType booleanType = (SDOType) typeHelper.getType("commonj.sdo", "Boolean");
	        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

	        // create a new Type for Company
	        DataObject compnayType = dataFactory.create("commonj.sdo", "Type");
	        compnayType.set("uri", "http://theUri");
	        compnayType.set("name", "CompanyType");

	        addProperty(compnayType, "companyName", stringType, false, false, true);
	        addProperty(compnayType, "public", booleanType, false, false, true);

	        Type companySDOType = typeHelper.define(compnayType);

	        DataObject propDO = dataFactory.create(propertyType);
	        propDO.set("name", "company");
	        propDO.set("type", companySDOType);
	       // typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);

	    }
	    
	    public void testStaticClasses(){
	    	List types = defineTypes();
	    	assertTrue(types.size() == 1);
	    	SDOType theType = (SDOType)types.get(0);
	    	Class instanceClass = theType.getInstanceClass();
	    	assertTrue(instanceClass != null);
	    	
	    	Class implClass = theType.getImplClass();	    	
	    	assertTrue(implClass != null);
	    }
}
