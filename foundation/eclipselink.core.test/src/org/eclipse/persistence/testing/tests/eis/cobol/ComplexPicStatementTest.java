/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

public class ComplexPicStatementTest extends CobolTest {
    Vector records;

    public String description() {
        return "This test will parse a record which contains complex picture statements.";
    }

    protected void test() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(CobolTestModel.getComplexPicStatementCopyBookString().getBytes());
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
        if (!CobolTestModel.compareCompositeObjects(record, CobolTestModel.getComplexPicStatementRecord())) {
            TestErrorException exception = new TestErrorException("The records do not match.");
            setTestException(exception);
        }
    }
}
