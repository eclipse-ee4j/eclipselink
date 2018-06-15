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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.helper.DatabaseTable;


//Created by Ian Reid
//Date: Feb 27, 2k3

public class MultipleTablePrimaryKeyNotSpecifiedTest extends ExceptionTest {
    public MultipleTablePrimaryKeyNotSpecifiedTest() {
        super();
        setDescription("This tests Multiple Table Primary Key Not Specified (TL-ERROR 47)");
    }

    protected void setup() {
        expectedException = DescriptorException.multipleTablePrimaryKeyNotSpecified(new RelationalDescriptor());
    }

    public void test() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        ObjectBuilder objectBuilder = descriptor.getObjectBuilder();
        descriptor.addTableName("Dummy_Table"); //first element
        descriptor.addTableName("Dummy_Table2");
        DatabaseTable table = descriptor.getTable("Dummy_Table2");
        //needs to be false getDescriptor().getTables().firstElement().equals(table)
        descriptor.getAdditionalTablePrimaryKeyFields().remove(table); //ensure table is not in HashTable
        //needs to be null Hashtable keyMapping = (Hashtable) getDescriptor().getAdditionalTablePrimaryKeyFields().get(table);

        try {
            objectBuilder.buildPrimaryKeyExpression(table);
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}
