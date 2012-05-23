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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate;

import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.oxm.XMLConstants;

public class DefineAndGenerateNillableTestCases extends XSDHelperDefineAndGenerateTestCases {
    public DefineAndGenerateNillableTestCases(String name) {
        super(name);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/schemas/CustomerWithNillable.xsd";
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate.DefineAndGenerateNillableTestCases" };
        TestRunner.main(arguments);
    }

    public String getControlGeneratedFileName() {
        return "org/eclipse/persistence/testing/sdo/schemas/CustomerWithNillableGenerated.xsd";
    }

    public List<Type> getControlTypes() {
        List<Type> types = new ArrayList<Type>();
        String uri = NON_DEFAULT_URI;

        Type intType = typeHelper.getType("commonj.sdo", "Int");

        //DataObject customerTypeDO = defineType(uri, "CustomerType");        
        SDOType customerSDOType = new SDOType(uri, "CustomerType");
        customerSDOType.setInstanceClassName(NON_DEFAULT_JAVA_PACKAGE_NAME + ".CustomerType");
        SDOProperty prop = new SDOProperty(aHelperContext);
        prop.setName("myAttr");
        prop.setType(intType);
        //prop.setAttribute(true);
        //prop.setElement(false);      
        prop.setXsd(true);
        prop.setDefault(new Integer(0));
        prop.setXsdLocalName("myAttr");
        customerSDOType.addDeclaredProperty(prop);

        SDOProperty prop2 = new SDOProperty(aHelperContext);
        prop2.setName("myNonSpecified");
        prop2.setType(intType);
        //prop2.setAttribute(false);
        //prop2.setElement(true);
        prop2.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        prop2.setXsd(true);
        prop2.setDefault(new Integer(0));
        prop2.setXsdLocalName("myNonSpecified");

        prop2.setXsdType(XMLConstants.INT_QNAME);
        prop2.setContainment(true);

        customerSDOType.addDeclaredProperty(prop2);

        SDOProperty prop3 = new SDOProperty(aHelperContext);
        prop3.setName("myNonNillable");

        prop3.setType(intType);
        //prop3.setAttribute(false);
        //prop3.setElement(true);
        prop3.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        prop3.setXsd(true);
        prop3.setXsdLocalName("myNonNillable");
        prop3.setContainment(true);

        prop3.setDefault(new Integer(0));

        customerSDOType.addDeclaredProperty(prop3);

        SDOProperty prop4 = new SDOProperty(aHelperContext);
        prop4.setName("myNillable");
        prop4.setType(intType);
        //prop4.setAttribute(false);
        prop4.setXsd(true);
        prop4.setDefault(new Integer(0));
        
        prop4.setContainment(true);
        //prop4.setElement(true);
        prop4.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        prop4.setNullable(true);
        prop4.setXsdLocalName("myNillable");
        customerSDOType.addDeclaredProperty(prop4);

        /*DataObject attrProp = addProperty(customerTypeDO, "myAttr", intType);

        DataObject elemNonSpecifiedProp = addProperty(customerTypeDO, "myNonSpecified", intType);
        elemNonSpecifiedProp.set("nullable", false);

        DataObject elemNonNillableProp = addProperty(customerTypeDO, "myNonNillable", intType);
        elemNonNillableProp.set("nullable", false);

        DataObject elemNillableProp = addProperty(customerTypeDO, "myNillable", intType);
        elemNillableProp.set("nullable", true);

        SDOType customerSDOType = (SDOType)aHelperContext.getTypeHelper().define(customerTypeDO);
        */
        types.add(customerSDOType);
        return types;
    }
}
