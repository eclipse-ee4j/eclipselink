/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     12/18/2009-2.1 Guy Pelletier
//       - 211323: Add class extractor support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.testing.models.jpa.inheritance;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.ClassExtractor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class AppleExtractor extends ClassExtractor {

    /**
     * Extract/compute the class from the database row and return the class.
     * Map is used as the public interface to database row, the key is the
     * field name, the value is the database value.
     */
    @Override
    public Class extractClassFromRow(Record databaseRow, Session session) {
        if (databaseRow.containsKey("JPA_MACBOOK_PRO.COLOR")) {
            return MacBookPro.class;
        } else if (databaseRow.containsKey("JPA_MACBOOK.RAM")) {
            return MacBook.class;
        } else {
            return Apple.class;
        }
    }

    /**
     * Allow for any initialization. This will be called on the apple descriptor.
     */
    @Override
    public void initialize(ClassDescriptor appleDescriptor, Session session) throws DescriptorException {
        ExpressionBuilder builder = new ExpressionBuilder();

        // Since macbook shares its table with macbookpro, we need to set
        // the only instances expression.
        ClassDescriptor macbookDescriptor = session.getDescriptor(org.eclipse.persistence.testing.models.jpa.inheritance.MacBook.class);
        macbookDescriptor.getInheritancePolicy().setOnlyInstancesExpression(builder.getField("JPA_MACBOOK.RAM").lessThanEqual(4));

        // MacBookPro does not require an expression since it defines a single
        // concrete class.
    }
}
