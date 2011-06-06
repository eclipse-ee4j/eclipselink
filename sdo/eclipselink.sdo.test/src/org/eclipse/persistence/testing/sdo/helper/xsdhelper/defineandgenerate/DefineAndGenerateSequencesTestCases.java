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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;

public class DefineAndGenerateSequencesTestCases extends XSDHelperDefineAndGenerateTestCases {
    public DefineAndGenerateSequencesTestCases(String name) {
        super(name);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithMultipleOpenContent.xsd";
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate.DefineAndGenerateSequencesTestCases" };
        TestRunner.main(arguments);
    }

    public String getControlGeneratedFileName() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithMultipleOpenContentGenerated.xsd";
    }
    
    public List<Type> getTypesToGenerateFrom () {
    	return getControlTypes();
    }

    public List<Type> getControlTypes() {
        List<Type> types = new ArrayList<Type>();
        ((SDOTypeHelper)typeHelper).reset();
        //String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithMultipleOpenContent.xsd");
        //List types = xsdHelper.define(xsdSchema);
        
        DataObject myTestType2DO = dataFactory.create("commonj.sdo", "Type");
        myTestType2DO.set("uri", NON_DEFAULT_URI);
        myTestType2DO.set("name", "myTestType2");        
        myTestType2DO.set("open", true);        
        myTestType2DO.set("sequenced", true);        
        
        Type myTestType2 = typeHelper.define(myTestType2DO);
        ((SDOType)myTestType2).setXsd(true);
        ((SDOType)myTestType2).setXsdLocalName("myTestType2");
        
        
        DataObject myTestType5DO = dataFactory.create("commonj.sdo", "Type");
        myTestType5DO.set("uri", NON_DEFAULT_URI);
        myTestType5DO.set("name", "myTestType5");
        myTestType5DO.set("sequenced", true);
        myTestType5DO.set("open", true);
        DataObject  prop = addProperty(myTestType5DO, "test", SDOConstants.SDO_STRING);
        prop.set("many", true);
        prop = addProperty(myTestType5DO, "test2", SDOConstants.SDO_STRING);
        prop.set("many", true);
        Type myTestType5 = typeHelper.define(myTestType5DO);
        ((SDOType)myTestType5).setXsd(true);
        ((SDOType)myTestType5).setXsdLocalName("myTestType5");
        ((SDOProperty)myTestType5.getProperty("test")).setXsd(true);
        ((SDOProperty)myTestType5.getProperty("test")).setXsdLocalName("test");
        ((SDOProperty)myTestType5.getProperty("test2")).setXsd(true);
        ((SDOProperty)myTestType5.getProperty("test2")).setXsdLocalName("test2");
        
        DataObject myTestType3DO = dataFactory.create("commonj.sdo", "Type");
        myTestType3DO.set("uri", NON_DEFAULT_URI);
        myTestType3DO.set("name", "myTestType3");        
        myTestType3DO.set("open", true);
        myTestType3DO.set("sequenced", true);
        Type myTestType3 = typeHelper.define(myTestType3DO);
        ((SDOType)myTestType3).setXsd(true);
        ((SDOType)myTestType3).setXsdLocalName("myTestType3");
        
        DataObject myTestTypeDO = dataFactory.create("commonj.sdo", "Type");
        myTestTypeDO.set("uri", NON_DEFAULT_URI);
        myTestTypeDO.set("name", "myTestType");        
        myTestTypeDO.set("open", true);
        myTestTypeDO.set("sequenced", true);
        Type myTestType = typeHelper.define(myTestTypeDO);
        ((SDOType)myTestType).setXsd(true);
        ((SDOType)myTestType).setXsdLocalName("myTestType");
        
        DataObject myTestType4DO = dataFactory.create("commonj.sdo", "Type");
        myTestType4DO.set("uri", NON_DEFAULT_URI);
        myTestType4DO.set("name", "myTestType4");                
        myTestType4DO.set("sequenced", true);
        myTestType4DO.set("open", true);
        myTestType4DO.set("sequenced", true);
        prop = addProperty(myTestType4DO, "test", SDOConstants.SDO_STRING);
        prop.set("many", true);
        
        prop = addProperty(myTestType4DO, "test2", SDOConstants.SDO_STRING);
        prop.set("many", true);
        
        Type myTestType4 = typeHelper.define(myTestType4DO);
        ((SDOType)myTestType4).setXsd(true);
        ((SDOType)myTestType4).setXsdLocalName("myTestType4");
        ((SDOProperty)myTestType4.getProperty("test")).setXsd(true);
        ((SDOProperty)myTestType4.getProperty("test")).setXsdLocalName("test");
        ((SDOProperty)myTestType4.getProperty("test2")).setXsd(true);
        ((SDOProperty)myTestType4.getProperty("test2")).setXsdLocalName("test2");
        
        types.add(myTestType2);
        types.add(myTestType4);
        types.add(myTestType);
        types.add(myTestType5);
        types.add(myTestType3);
        
        return types;
    }
    
}
