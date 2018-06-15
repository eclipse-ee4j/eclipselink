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
//     dminsky - initial implementation
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.helper.SchemaResolver;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.XSDHelperTestCases;
import org.xml.sax.InputSource;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;

/**
 * Bug 451041
 */
public class TypePropertyIndexTest extends XSDHelperTestCases {

    public TypePropertyIndexTest(String name) {
        super(name);
    }

    public void testPropertyIndexSequence() {
        String schemaLocation = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/propertyindex/";
        String resourceFileName = "ExtendedAttributes.xsd";

        HelperContext helperContext = getHelperContext();
        SDOXSDHelper sdoXSDHelper = (SDOXSDHelper) XSDHelper.INSTANCE;
        sdoXSDHelper.setHelperContext(helperContext);

        StreamSource streamSource = getResourceAsStreamSource(schemaLocation + resourceFileName);
        List definedTypes = sdoXSDHelper.define(streamSource, new TestResolver(schemaLocation));

        SDOType controlType = (SDOType) TypeHelper.INSTANCE.getType("http://www.example.org", "Electronics");
        SDOProperty controlProperty = controlType.getProperty("ItemTen");

        SDOType type = (SDOType) TypeHelper.INSTANCE.getType("http://www.example.org", "Television");
        SDOProperty property1 = type.getProperty("HDTVTuners");
        SDOProperty property2 = type.getProperty("Resolution");

        assertEquals("HDTVTuners property must be +1 from the last property in type: Electronics",
                controlProperty.getIndexInType() + 1, property1.getIndexInType());
        assertEquals("Resolution property must be +2 from the last property in type: Electronics",
                controlProperty.getIndexInType() + 2, property2.getIndexInType());
    }

    protected static class TestResolver implements SchemaResolver {

        protected String schemaLocationBase;

        protected TestResolver(String schemaLocation) {
            this.schemaLocationBase = schemaLocation;
        }

        public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation) {
            return TypePropertyIndexTest.getResourceAsStreamSource(schemaLocationBase + schemaLocation);
        }

        public InputSource resolveEntity(String publicId, String systemId) {
            return null;
        }
    }

    protected static StreamSource getResourceAsStreamSource(String resource) {
        try {
             URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
             // SystemId MUST be set to reproduce issue
             return new StreamSource(url.openStream(), url.toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        TestRunner.run(TypePropertyIndexTest.class);
    }

}
