/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.eis.nosql;

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.nosql.adapters.nosql.OracleNoSQLPlatform;
import org.eclipse.persistence.eis.interactions.*;
import org.eclipse.persistence.internal.nosql.adapters.nosql.OracleNoSQLOperation;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.testing.framework.TestCase;

/**
 * Tests native queries.
 */
public class NativeTest extends TestCase {
    public NativeTest() {
        setName("NativeTest");
        setDescription("Tests native queries.");
    }

    public void test() throws Exception {

        MappedInteraction insertCall = new MappedInteraction();
        insertCall.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.PUT.name());
        insertCall.addArgumentValue("Order/1234", "foo");
        DataModifyQuery insert = new DataModifyQuery(insertCall);
        getSession().executeQuery(insert);

        MappedInteraction readCall = new MappedInteraction();
        readCall.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.GET.name());
        readCall.addArgumentValue("Order/1234", "");       
        DataReadQuery read = new DataReadQuery(readCall);
        List<Record> result = (List<Record>)getSession().executeQuery(read);
        String value = new String((byte[])result.get(0).get("Order/1234"));
        if (!value.equals("foo")) {
            fail("foo expected: " + value);
        }
    }
}
