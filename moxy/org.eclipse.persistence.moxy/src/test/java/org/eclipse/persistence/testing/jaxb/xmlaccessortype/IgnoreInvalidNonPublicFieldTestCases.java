/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.jaxb.xmlaccessortype;

import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestCases;

public class IgnoreInvalidNonPublicFieldTestCases extends SchemaGenTestCases {

    private static final String PATH = "org/eclipse/persistence/testing/jaxb/xmlaccessortype/";
    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     */
    public IgnoreInvalidNonPublicFieldTestCases(String name) throws Exception {
        super(name);
    }

    public void testPublicMemberAccess() throws Exception {
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        generateSchema(new Class[]{Root.class}, outputResolver, null);
        String result = validateAgainstSchema(PATH + "root_public.xml", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
}
