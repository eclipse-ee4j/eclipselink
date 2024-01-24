/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import junit.textui.TestRunner;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveTestCases;

public class SingleValueBaseTypeTestCases extends LoadAndSaveTestCases {
    public SingleValueBaseTypeTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.substitutiongroups.SingleValueBaseTypeTestCases" };
        TestRunner.main(arguments);
    }

    @Override
    protected String getSchemaName() {
        return getSchemaLocation() + "SubstitutionGroup.xsd";
    }

    @Override
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/substitutiongroups/single_value_base.xml");
    }

    @Override
    protected String getControlWriteFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/substitutiongroups/single_value_base.xml");
    }

    @Override
    protected String getNoSchemaControlFileName() {
        return "";
    }

    @Override
    protected String getControlRootURI() {
        return "TEST/NS";
    }

    @Override
    protected String getControlRootName() {
        return "employee-data";
    }
    @Override
    protected String getRootInterfaceName() {
        return "EmployeeType";
    }

    @Override
    public void testNoSchemaLoadFromInputStreamSaveDataObjectToString() throws Exception {
    }

    /*protected List defineTypes() {
        try {
            String location = getSchemaName();

            //FileInputStream fis = new FileInputStream(location);
            URL url = new URL(location);
            InputStream is = url.openStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(is);
            DOMSource ds = new DOMSource(doc);
            DefaultSchemaResolver sr = new DefaultSchemaResolver();
            sr.setBaseSchemaLocation(getSchemaLocation());
            return ((SDOXSDHelper)xsdHelper).define(ds, sr);
            //URL url = new URL(getSchemaLocation() + getSchemaName());
            //InputStream is = url.openStream();
            //return xsdHelper.define(is, getSchemaLocation());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    @Override
    protected String getSchemaLocation() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/substitutiongroups/";
    }

    @Override
    public void registerTypes() {
    }

    @Override
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();
        packages.add("test/ns");
        return packages;
    }
}
