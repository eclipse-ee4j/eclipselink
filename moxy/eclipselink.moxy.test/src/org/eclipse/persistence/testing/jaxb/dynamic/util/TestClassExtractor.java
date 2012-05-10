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
 *     rbarkhouse - 2.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.dynamic.util;

import org.eclipse.persistence.descriptors.ClassExtractor;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class TestClassExtractor extends ClassExtractor {

    public Class extractClassFromRow(Record databaseRow, Session session) {
        Class personClass = session.getProject().getDescriptorForAlias("Person").getJavaClass();
        return personClass;
    }

}