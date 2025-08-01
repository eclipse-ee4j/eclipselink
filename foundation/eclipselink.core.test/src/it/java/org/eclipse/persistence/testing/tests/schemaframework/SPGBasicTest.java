/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.schemaframework;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.StoredProcedureGenerator;

import java.io.FileWriter;
import java.io.Writer;

public class SPGBasicTest extends TestCase {
    private SchemaManager manager;
    private StoredProcedureGenerator generator;
    private Writer myWriter = null;

    public SPGBasicTest() {
    }

    @Override
    public void setup() {
        setManager(new SchemaManager(getDatabaseSession()));
        setGenerator(new StoredProcedureGenerator(getManager()));
    }

    @Override
    public void test() {
        getGenerator().generateStoredProcedures(getWriter());
    }

    @Override
    public void verify() {
    }

    @Override
    public void reset() {
        setManager(null);
        setGenerator(null);
        try {
            getWriter().close();
        } catch (Exception e) {
        }
    }

    // Accessors
    private StoredProcedureGenerator getGenerator() {
        return generator;
    }

    private SchemaManager getManager() {
        return manager;
    }

    private Writer getWriter() {
        if (myWriter == null) {
            try {
                setWriter(new FileWriter("GeneratedStoredProcedure.sql"));
            } catch (java.io.IOException e) {
            }
        }
        return myWriter;
    }

    private void setGenerator(StoredProcedureGenerator newGenerator) {
        generator = newGenerator;
    }

    private void setManager(SchemaManager newManager) {
        manager = newManager;
    }

    private void setWriter(Writer newWriter) {
        myWriter = newWriter;
    }
}
