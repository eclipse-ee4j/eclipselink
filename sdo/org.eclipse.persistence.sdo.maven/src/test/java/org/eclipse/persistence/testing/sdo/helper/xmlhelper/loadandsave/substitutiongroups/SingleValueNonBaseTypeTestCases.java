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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.substitutiongroups;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.w3c.dom.Document;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveTestCases;

public class SingleValueNonBaseTypeTestCases extends LoadAndSaveTestCases {
    public SingleValueNonBaseTypeTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.substitutiongroups.SingleValueNonBaseTypeTestCases" };
        TestRunner.main(arguments);
    }

    protected String getSchemaName() {
        return getSchemaLocation() + "SubstitutionGroup.xsd";
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/substitutiongroups/single_value_non_base.xml");
    }

    protected String getControlWriteFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/substitutiongroups/single_value_non_base.xml");
    }

    protected String getNoSchemaControlFileName() {
        return "";
    }

    protected String getControlRootURI() {
        return "TEST/NS";
    }

    protected String getControlRootName() {
        return "employee-data";
    }
    protected String getRootInterfaceName() {
        return "EmployeeType";
    }

    public void testNoSchemaLoadFromInputStreamSaveDataObjectToString() throws Exception {
    }


    protected String getSchemaLocation() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/substitutiongroups/";
    }

    public void registerTypes() {
    }

    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();
        packages.add("test/ns");
        return packages;
    }

}
