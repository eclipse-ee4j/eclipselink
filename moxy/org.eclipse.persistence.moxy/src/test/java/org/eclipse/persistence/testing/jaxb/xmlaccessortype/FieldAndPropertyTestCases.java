/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Matt MacIvor - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlaccessortype;

import java.io.IOException;

import jakarta.xml.bind.SchemaOutputResolver;
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
