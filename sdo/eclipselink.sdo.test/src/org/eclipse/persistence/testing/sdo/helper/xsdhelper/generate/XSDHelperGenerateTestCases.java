/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.XSDHelperTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public abstract class XSDHelperGenerateTestCases extends XSDHelperTestCases {
    public XSDHelperGenerateTestCases(String name) {
        super(name);
    }

    public void testGenerateSchema() throws Exception {
        //String generatedSchema = xsdHelper.generate(getTypesToGenerateFrom(), getSchemaNamespacesMap());
        DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(getTypesToGenerateFrom(), resolver);

        String controlSchema = getSchema(getControlFileName());
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);

        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);
        reader.close();

        assertSchemaIdentical(getDocument(getControlFileName()), generatedSchemaDoc);

    }

    public java.util.Map getMap() {
        return new HashMap();
    }

    /*
        public void testGenerateAllSchema() {
            List generatedSchemas = ((SDOXSDHelper)xsdHelper).generateAll(getTypesToGenerateFrom(), getSchemaNamespacesMap());
            List controlSchemas = getAllSchemas(getGenerateAllControlFileNames());
            log("EXPECTED: \n" + controlSchemas);
            log("ACTUAL: \n" + generatedSchemas);

            assertXMLIdentical(controlSchemas, generatedSchemas, true);
        }
        */
    protected abstract List getTypesToGenerateFrom();

    protected abstract String getControlFileName();

    protected List getGenerateAllControlFileNames() {
        ArrayList controlFiles = new ArrayList();
        controlFiles.add(getControlFileName());
        return controlFiles;
    }

    protected HashMap getSchemaNamespacesMap() {
        return null;
    }
}
