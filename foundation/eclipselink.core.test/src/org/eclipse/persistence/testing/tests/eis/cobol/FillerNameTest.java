/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.eis.cobol;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.internal.eis.cobol.*;
import org.eclipse.persistence.testing.framework.*;

public class FillerNameTest extends CobolTest {
    Vector records;

    public String description() {
        return "This test will parse a record which contains a record with no name and assure it replaces it with filler";
    }

    protected void test() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(CobolTestModel.getFillerCopyBookString().getBytes());
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
        if (!compareFillerRecords(record, CobolTestModel.getFillerRecord())) {
            TestErrorException exception = new TestErrorException("The records do not match.");
            setTestException(exception);
        }
    }

    /**
     * this test must use its own compare method since all fields have the name filler
     */
    private boolean compareFillerRecords(RecordMetaData record1, RecordMetaData record2) {
        Vector record1Fields = record1.getFields();
        Vector record2Fields = record2.getFields();
        if (!CobolTestModel.compareElementaryFields((ElementaryFieldMetaData)record1Fields.firstElement(), (ElementaryFieldMetaData)record2Fields.firstElement())) {
            return false;
        }
        if (!((FieldMetaData)record2Fields.elementAt(1)).getName().equalsIgnoreCase("filler")) {
            return false;
        }
        Vector fillerCompFields1 = ((CompositeFieldMetaData)record1Fields.elementAt(1)).getFields();
        Vector fillerCompFields2 = ((CompositeFieldMetaData)record2Fields.elementAt(1)).getFields();
        if (!CobolTestModel.compareElementaryFields((ElementaryFieldMetaData)fillerCompFields1.firstElement(), (ElementaryFieldMetaData)fillerCompFields2.firstElement())) {
            return false;
        }
        if (!CobolTestModel.compareElementaryFields((ElementaryFieldMetaData)fillerCompFields1.elementAt(1), (ElementaryFieldMetaData)fillerCompFields2.elementAt(1))) {
            return false;
        }

        return true;
    }
}
