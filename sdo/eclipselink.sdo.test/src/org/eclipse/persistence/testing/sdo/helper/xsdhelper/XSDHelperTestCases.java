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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class XSDHelperTestCases extends SDOTestCase {
    public XSDHelperTestCases(String name) {
        super(name);
    }

    public InputStream getSchemaInputStream(String fileName) {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            return is;
        } catch (Exception e) {
            log(getClass().toString() + ": Reading error for : " + fileName + " message: " + e.getMessage());
            return null;
        }
    }

    public List getAllSchemas(List fileNames) {
        List<String> schemas = new ArrayList<String>();
        for (int i = 0; i < fileNames.size(); i++) {
            String fileName = (String)fileNames.get(i);
            String schema = getSchema(fileName);
            schemas.add(schema);
        }
        return schemas;
    }
}
