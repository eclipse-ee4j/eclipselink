/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Martin Vojtek - November 14/2014 - 2.6.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlvalue;

import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestCases;

/**
 * Tests @XmlValue annotation processing.
 *
 */
public class SchemaGenXmlValueTestCases  extends SchemaGenTestCases {

    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     */
    public SchemaGenXmlValueTestCases(String name) throws Exception {
        super(name);
    }

    /**
     * Exception case - target class of @XmlValue must not extend types other than @Object type
     */
    public void testInvalidXmlID() {
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        boolean exception = false;
        try {
            generateSchema(new Class[]{ MyInvalidClass.class }, outputResolver, null);
        } catch (Exception ex) {
            exception = true;
        }
        assertTrue("An error did not occur as expected", exception);
    }
}
