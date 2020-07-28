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
//     Matt MacIvor - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlaccessortype;

import java.io.IOException;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestCases;

public class FieldAndPropertyTestCases extends SchemaGenTestCases {
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/xmlaccessortype/";
    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     */
    public FieldAndPropertyTestCases(String name) throws Exception {
        super(name);
    }

    public void testFieldAccess() throws Exception {
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        generateSchema(new Class[]{PersonField.class}, outputResolver, null);
        String result = validateAgainstSchema(PATH + "person_field.xml", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    public void testPropertyAccess() throws Exception {
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        generateSchema(new Class[]{PersonProperty.class}, outputResolver, null);
        String result = validateAgainstSchema(PATH + "person_property.xml", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

}
