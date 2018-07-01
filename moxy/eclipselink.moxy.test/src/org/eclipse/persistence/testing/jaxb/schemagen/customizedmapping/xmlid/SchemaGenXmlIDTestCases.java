/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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
// Martin Vojtek - November 14/2014 - 2.6.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlid;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestCases;

/**
 * Tests @XmlID annotation processing.
 *
 */
public class SchemaGenXmlIDTestCases  extends SchemaGenTestCases {

    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     */
    public SchemaGenXmlIDTestCases(String name) throws Exception {
        super(name);
    }

    /**
     * Exception case - target class of @XmlID must be of @String type
     */
    public void testInvalidXmlID() {
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        boolean exception = false;
        try {
            generateSchema(new Class[]{ MyInvalidClass.class }, outputResolver, null);
        } catch (Exception ex) {
            if (ex instanceof JAXBException && ((org.eclipse.persistence.exceptions.JAXBException)((JAXBException) ex).getLinkedException()).getErrorCode() == 50016) {
                exception = true;
            }
        }
        assertTrue("An error did not occur as expected", exception);
    }
}
