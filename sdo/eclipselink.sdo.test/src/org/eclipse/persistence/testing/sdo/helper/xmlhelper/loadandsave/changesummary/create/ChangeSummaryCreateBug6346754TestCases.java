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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.create;

import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.FileCodeWriter;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveTestCases;

public class ChangeSummaryCreateBug6346754TestCases extends LoadAndSaveTestCases {
    public ChangeSummaryCreateBug6346754TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.create.ChangeSummaryCreateBug6346754TestCases" };
        TestRunner.main(arguments);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/bug6346754/SupplierService.xsd";
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/bug6346754/bug6346754.xml");
    }

    protected String getSchemaLocation() {
        return (FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/bug6346754/");
    }

    protected String getControlRootName() {
        return "processData";
    }

    protected String getControlRootURI() {
        return "http://example.com/supplier/service/";
    }

    protected String getRootInterfaceName() {
        return "ProcessData";
    }

    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();
        packages.add("mypackage/process");
        packages.add("com/example/supplier/service");
        packages.add("mypackage/address");
        packages.add("mypackage/supplier");
        return packages;
    }

    protected void generateClasses(String tmpDirName) throws Exception {
        String xsdString = getSchema(getSchemaName());
        StringReader reader = new StringReader(xsdString);

        SDOClassGenerator classGenerator = new SDOClassGenerator(aHelperContext);
        DefaultSchemaResolver sr = new DefaultSchemaResolver();
        sr.setBaseSchemaLocation(getSchemaLocation());
        FileCodeWriter cw = new FileCodeWriter();
        cw.setSourceDir(tmpDirName);
        classGenerator.generate(reader, cw, sr, true);

    }

    public void registerTypes() {
    }

    protected List defineTypes() {
        try {
            FileInputStream inputStream = new FileInputStream(getSchemaName());
            StreamSource ss = new StreamSource(inputStream);
            DefaultSchemaResolver dsr = new DefaultSchemaResolver();
            dsr.setBaseSchemaLocation(getSchemaLocation());
            return ((SDOXSDHelper)xsdHelper).define(ss, dsr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred during defineTypes");
        }
        return null;
    }

    public void testNoSchemaLoadFromInputStreamSaveDataObjectToString() {
        //do nothing
        //running this test doesn't really add much and would need to manually create close to 100 types
    }
}
