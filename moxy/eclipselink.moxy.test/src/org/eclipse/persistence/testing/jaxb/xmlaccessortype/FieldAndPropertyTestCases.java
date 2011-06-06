/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.3 - Initial implementation
 ******************************************************************************/
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
