/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.model.sequence;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public abstract class SDOSequenceTestCases extends SDOTestCase {
    public SDOSequenceTestCases(String name) {
        super(name);
    }

    public abstract String getSchemaToDefine();

    public abstract List getControlTypes();

    protected String getSchemaLocation() {
        return null;
    }

    public java.util.Map getMap() {
        return new HashMap();
    }

    protected List getTypesToGenerateFrom() {
        return getControlTypes();
    }

    protected String getControlGeneratedFileName() {
        return getSchemaToDefine();
    }

    public InputStream getSchemaInputStream(String fileName) {
        try {
            FileInputStream is = new FileInputStream(fileName);
            return is;
        } catch (Exception e) {
            log(getClass().toString() + ": Reading error for : " + fileName + " message: " + e.getMessage());
            return null;
        }
    }

    public List getAllSchemas(List fileNames) {
        List schemas = new ArrayList();
        for (int i = 0; i < fileNames.size(); i++) {
            String fileName = (String)fileNames.get(i);
            String schema = getSchema(fileName);
            schemas.add(schema);
        }
        return schemas;
    }
}
