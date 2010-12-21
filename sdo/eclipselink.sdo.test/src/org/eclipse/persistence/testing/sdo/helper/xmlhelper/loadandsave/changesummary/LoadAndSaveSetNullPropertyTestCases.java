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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveTestCases;

public class LoadAndSaveSetNullPropertyTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveSetNullPropertyTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.LoadAndSaveSetNullPropertyTestCases" };
        TestRunner.main(arguments);
    }
    
    protected List<String> getPackages() {
		    List<String> packages = new ArrayList<String>();
	    	packages.add(NON_DEFAULT_JAVA_PACKAGE_DIR);
	    	return packages;
	  }

    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        DataObject rootDO = document.getRootObject();
        List empsList = rootDO.getList("Value");
        DataObject empDO = (DataObject)empsList.get(0);
              
        assertTrue(rootDO.getChangeSummary().isLogging());
        
        List oldValues = rootDO.getChangeSummary().getOldValues(empDO);
        assertEquals(4, oldValues.size());
        
        ChangeSummary.Setting oldValueSimpleNonNillable = rootDO.getChangeSummary().getOldValue(empDO, empDO.getInstanceProperty("SimpleNonNillable"));
        assertNotNull(oldValueSimpleNonNillable);
        assertTrue(oldValueSimpleNonNillable.isSet());
        assertNull(oldValueSimpleNonNillable.getValue());
        
        ChangeSummary.Setting oldValueSimpleNillable = rootDO.getChangeSummary().getOldValue(empDO, empDO.getInstanceProperty("SimpleNillable"));
        assertNotNull(oldValueSimpleNillable);
        assertTrue(oldValueSimpleNillable.isSet());
        assertNull(oldValueSimpleNillable.getValue());
        
        ChangeSummary.Setting oldValueComplexNonNillable = rootDO.getChangeSummary().getOldValue(empDO, empDO.getInstanceProperty("ComplexNonNillable"));
        assertNotNull(oldValueComplexNonNillable);
        assertFalse(oldValueComplexNonNillable.isSet());
        assertNull(oldValueComplexNonNillable.getValue());
        
        ChangeSummary.Setting oldValueComplexNillable = rootDO.getChangeSummary().getOldValue(empDO, empDO.getInstanceProperty("ComplexNillable"));
        assertNotNull(oldValueComplexNillable);
        assertTrue(oldValueComplexNillable.isSet());
        assertNull(oldValueComplexNillable.getValue());
    }

    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/SetNullProperty.xml";
    }  

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/SetNullProperty.xsd";
    }

    protected String getControlRootName() {
        return "theRoot";
    }

    protected String getRootInterfaceName() {
        return "RootType";
    }

    public void registerTypes() {
        SDOType changeSummaryType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

      DataObject addressTypeDO = dataFactory.create(typeType);
      addressTypeDO.set("uri",getControlRootURI());
      addressTypeDO.set("name","Address");
      addProperty(addressTypeDO, "street", SDOConstants.SDO_STRING, false, false, true);
      Type addressType = typeHelper.define(addressTypeDO);
      
      DataObject empTypeDO = dataFactory.create(typeType);
      empTypeDO.set("uri",getControlRootURI());
      empTypeDO.set("name","Emp");
      addProperty(empTypeDO, "Empno", SDOConstants.SDO_STRING, false, false, true);
      addProperty(empTypeDO, "Ename", SDOConstants.SDO_STRING, false, false, true);
      addProperty(empTypeDO, "SimpleNonNillable", SDOConstants.SDO_STRING, false, false, true);
      DataObject simpleNillableProperty = addProperty(empTypeDO, "SimpleNillable", SDOConstants.SDO_STRING, false, false, true);
      simpleNillableProperty.set("nullable", true);
      addProperty(empTypeDO, "ComplexNonNillable", addressType, true, false, true);
      DataObject complexNillableProperty = addProperty(empTypeDO, "ComplexNillable", addressType, true, false, true);
      complexNillableProperty.set("nullable", true);
      
      Type empType = typeHelper.define(empTypeDO);
      
      DataObject rootTypeDO = dataFactory.create(typeType);
      rootTypeDO.set("uri",getControlRootURI());
      rootTypeDO.set("name","Root");
      addProperty(rootTypeDO, "Value", dataObjectType, true, true, true);
      addProperty(rootTypeDO, "ChangeSummary", changeSummaryType, true, false, true);
      Type rootType = typeHelper.define(rootTypeDO);
      
      DataObject propDO=  dataFactory.create(propertyType);
      propDO.set("name", "theRoot");
      propDO.set("type", rootType);
      typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
      
    }
}
