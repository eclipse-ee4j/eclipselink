/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate;

import commonj.sdo.Property;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class DefineAndGenerateWithSDOImportsTestCases extends XSDHelperDefineAndGenerateTestCases {
    public DefineAndGenerateWithSDOImportsTestCases(String name) {
        super(name);
    }

    public String getControlGeneratedFileName() {
        return "org/eclipse/persistence/testing/sdo/schemas/SimpleWithSDOImports.xsd";
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/schemas/SimpleWithSDOImports.xsd";
    }

    public static void main(String[] args) {
        TestRunner.run(DefineAndGenerateWithSDOImportsTestCases.class);
    }

    public List getControlTypes() {
        List types = new ArrayList();

        String uri = "my.uri";
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        /****Customer TYPE*****/

        //Customer TYPE
        SDOType customerType = new SDOType(uri, "CustomerType");
        customerType.setDataType(false);
        customerType.setInstanceClassName("aPackage.CustomerType");

        SDOProperty fNameProp = new SDOProperty(aHelperContext);
        fNameProp.setName("firstName");
        fNameProp.setXsd(true);
        fNameProp.setXsdLocalName("firstName");
        fNameProp.setType(stringType);

        SDOProperty lNameProp = new SDOProperty(aHelperContext);
        lNameProp.setName("lastName");
        lNameProp.setXsd(true);
        lNameProp.setXsdLocalName("lastName");
        lNameProp.setType(stringType);
        
        SDOProperty idProp = new SDOProperty(aHelperContext);
        idProp.setName("id");
        idProp.setXsd(true);
        idProp.setXsdLocalName("id");                
        idProp.setInstanceProperty(SDOConstants.XMLDATATYPE_PROPERTY, SDOConstants.SDO_INTEGER);
        //idProp.setInstanceProperty(typeHelper.getOpenContentProperty(SDOConstants.SDOXML_URL,SDOConstants.XMLDATATYPE_PROPERTY.getName()), SDOConstants.SDO_INTEGER);
        idProp.setType(SDOConstants.SDO_INTEGER);

        customerType.addDeclaredProperty(fNameProp);
        customerType.addDeclaredProperty(lNameProp);
        customerType.addDeclaredProperty(idProp);
        types.add(customerType);
        return types;
    }
}