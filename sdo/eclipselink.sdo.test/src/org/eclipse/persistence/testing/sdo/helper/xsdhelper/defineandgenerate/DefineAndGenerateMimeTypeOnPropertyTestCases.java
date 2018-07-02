/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

/* $Header: DefineAndGenerateMimeTypeOnPropertyTestCases.java 23-nov-2006.14:26:34 dmahar Exp $ */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    dmahar      11/23/06 -
    mfobrien    11/21/06 -
    dmahar      11/17/06 -
    mfobrien    11/01/06 - Creation
 */

/**
 *  @version $Header: DefineAndGenerateMimeTypeOnPropertyTestCases.java 23-nov-2006.14:26:34 dmahar Exp $
 *  @author  mfobrien
 *  @since   11.1
 */
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate;

import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.oxm.XMLConstants;
import commonj.sdo.Type;
import commonj.sdo.impl.HelperProvider;

public class DefineAndGenerateMimeTypeOnPropertyTestCases extends XSDHelperDefineAndGenerateTestCases {
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate.DefineAndGenerateMimeTypeOnPropertyTestCases" };
        TestRunner.main(arguments);
    }

    public DefineAndGenerateMimeTypeOnPropertyTestCases(String name) {
        super(name);
    }

    public List getControlTypes() {
        List types = new ArrayList();
        String uri = "http://www.example.org";

        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type byteType = typeHelper.getType("commonj.sdo", "Bytes");

        SDOType employeeSDOType = new SDOType(uri, "EmployeeType");
        employeeSDOType.setInstanceClassName("defaultPackage.EmployeeType");
        SDOProperty prop = new SDOProperty(aHelperContext);
        prop.setName("id");
        prop.setType(stringType);
        //prop.setAttribute(false);
        //prop.setElement(true);
        prop.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        prop.setXsd(true);
        prop.setXsdLocalName("id");
        prop.setContainment(true);
        employeeSDOType.addDeclaredProperty(prop);

        SDOProperty prop2 = new SDOProperty(aHelperContext);
        prop2.setName("name");
        prop2.setType(stringType);
        //prop2.setAttribute(false);
        //prop2.setElement(true);
        prop2.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        prop2.setXsd(true);
        prop2.setXsdLocalName("name");
        prop2.setContainment(true);
        employeeSDOType.addDeclaredProperty(prop2);

        SDOProperty prop3 = new SDOProperty(aHelperContext);
        prop3.setName("photo");
        prop3.setType(byteType);
        //prop3.setAttribute(false);
        //prop3.setElement(true);
        prop3.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        prop3.setXsd(true);
        prop3.setXsdLocalName("photo");
        prop3.setContainment(true);
        prop3.setXsdType(XMLConstants.BASE_64_BINARY_QNAME);

        //prop3.setInstanceProperty(SDOConstants.MIME_TYPE_PROPERTY, "image/jpeg");
        prop3.setInstanceProperty(SDOConstants.MIME_TYPE_PROPERTY_PROPERTY, "photoMimeType");
        employeeSDOType.addDeclaredProperty(prop3);

        SDOProperty prop4 = new SDOProperty(aHelperContext);
        prop4.setName("photoMimeType");
        prop4.setType(stringType);
        //prop4.setAttribute(false);
        //prop4.setElement(true);
        prop4.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        prop4.setXsd(true);
        prop4.setXsdLocalName("photoMimeType");
        prop4.setContainment(true);
        employeeSDOType.addDeclaredProperty(prop4);

        types.add(employeeSDOType);
        return types;
    }

    // note the generated.xsd contains the header attribute  elementFormDefault="qualified"
    // the non-generated.xsd is used by cases in xsd/xmlhelper - modify with care
    public String getControlGeneratedFileName() {
        return "org/eclipse/persistence/testing/sdo/schemas/EmployeeWithMimeTypeOnPropertyGenerated.xsd";
    }
    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/schemas/EmployeeWithMimeTypeOnProperty.xsd";
    }
}
