/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.schemaframework;

import java.io.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;

public class SPGGenerateAmendmentClassTest extends TestCase {
    private SchemaManager manager;
    private StoredProcedureGenerator generator;
    private Writer myWriter = null;
    private Writer classWriter = null;

    public SPGGenerateAmendmentClassTest() {
    }

    public void setup() {
        setManager(new SchemaManager(getDatabaseSession()));
        setGenerator(new StoredProcedureGenerator(getManager()));
        getGenerator().generateStoredProcedures();
    }

    public void test() {
        getGenerator().generateAmendmentClass(getClassWriter(), "packageName", "className");
    }

    public void verify() {
    }

    public void reset() {
        setManager(null);
        setGenerator(null);
        try {
            getWriter().close();
        } catch (Exception e) {
        }
    }

    // Accessors
    private Writer getClassWriter() {
        if (classWriter == null) {
            setClassWriter(new StringWriter());
        }
        return classWriter;
    }

    private StoredProcedureGenerator getGenerator() {
        return generator;
    }

    private SchemaManager getManager() {
        return manager;
    }

    private Writer getWriter() {
        if (myWriter == null) {
            setWriter(new StringWriter());
        }
        return myWriter;
    }

    private void setClassWriter(Writer newWriter) {
        classWriter = newWriter;
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
