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
package org.eclipse.persistence.testing.tests.eis.cobol;

import java.util.*;
import org.eclipse.persistence.internal.eis.cobol.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.testing.framework.*;

public class RedefineConverterTest extends CobolTest {
    CobolRow resultRow;
    CobolRow row;
    byte[] recordData = new byte[420];

    public String description() {
        return "This test will take a database row, write its contents to a byte array and then " + "read the contents back into another database row, then compare the results to assure " + "the two rows are equal";
    }

    protected void test() {
        RecordMetaData recordMetaData = CobolTestModel.getConversionRecord();
        row = CobolTestModel.getConversionRow();
        row.remove("emp-tasks");
        row.add(new DatabaseField("emp-depart"), "accounting");
        Enumeration fieldEnum = row.getFields().elements();
        resultRow = new CobolRow();
        //write to array
        while (fieldEnum.hasMoreElements()) {
            DatabaseField databaseField = (DatabaseField)fieldEnum.nextElement();
            FieldMetaData field = recordMetaData.getFieldNamed(databaseField.getName());
            field.writeOnArray(row, recordData);
        }

        //write to database row
        fieldEnum = row.getFields().elements();
        while (fieldEnum.hasMoreElements()) {
            DatabaseField databaseField = (DatabaseField)fieldEnum.nextElement();
            FieldMetaData field = recordMetaData.getFieldNamed(databaseField.getName());
            field.writeOnRow(resultRow, recordData);
        }
    }

    protected void verify() throws TestException {
        if (!CobolTestModel.compareCobolRows(row, resultRow)) {
            TestErrorException exception = new TestErrorException("The rows do not match.");
            setTestException(exception);
        }
    }
}
