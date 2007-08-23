/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.eis.cobol;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.internal.eis.cobol.*;
import org.eclipse.persistence.testing.framework.*;

public class OccursDependsOnParseTest extends CobolTest {
    Vector records;

    public OccursDependsOnParseTest() {
        super();
    }

    public String description() {
        return "This test will parse a record which contains a field with an occurs statement, and a depends on statement.";
    }

    protected void test() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(CobolTestModel.getOccursDependsCopyBookString().getBytes());
        CopyBookParser parser = new CopyBookParser();
        try {
            records = parser.parse(inputStream);
        } catch (Exception exception) {
            TestErrorException testException = new TestErrorException(exception.getMessage());
            setTestException(testException);
        }
    }

    protected void verify() throws TestException {
        RecordMetaData record = (RecordMetaData)records.firstElement();
        if (!CobolTestModel.compareCompositeObjects(record, CobolTestModel.getOccursDependsRecord())) {
            TestErrorException exception = new TestErrorException("The records do not match.");
            setTestException(exception);
        }
    }
}