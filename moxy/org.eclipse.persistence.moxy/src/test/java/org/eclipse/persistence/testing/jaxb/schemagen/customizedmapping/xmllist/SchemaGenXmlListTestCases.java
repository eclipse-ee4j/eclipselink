/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - June 2/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmllist;

import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestCases;

/**
 * Tests @XMLList annotation processing.
 *
 */
public class SchemaGenXmlListTestCases extends SchemaGenTestCases {

    /**
     * This is the preferred (and only) constructor.
     *
     */
    public SchemaGenXmlListTestCases(String name) throws Exception {
        super(name);
    }

    /**
     * Exception case - @XmlList must be on collection property
     */
    public void testInvalidXmlList() {
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        boolean exception = false;
        try {
            generateSchema(new Class<?>[]{ MyInvalidClass.class }, outputResolver, null);
        } catch (Exception ex) {
            exception = true;
        }
        assertTrue("An error did not occur as expected", exception);
    }
}
