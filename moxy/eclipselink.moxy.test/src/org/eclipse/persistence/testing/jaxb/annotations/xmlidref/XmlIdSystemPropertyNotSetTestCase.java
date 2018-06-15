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
//     Martin Vojtek - 2.6 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlidref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.jaxb.compiler.AnnotationsProcessor;
import org.eclipse.persistence.jaxb.compiler.Property;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaHasAnnotations;
import org.eclipse.persistence.oxm.annotations.XmlIDExtension;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests useXmlIdExtension method when system property org.eclipse.persistence.moxy.annotation.xml-id-extension is not set.
 */
@RunWith(JMockit.class)
public class XmlIdSystemPropertyNotSetTestCase {

    @Test
    public void testSystemXmlIdExtensionNotSet(final @Mocked Property property, final @Mocked Helper helper, final @Mocked JavaClass javaClass) {
        new Expectations(System.class) {{
            property.getActualType(); result = javaClass;
            javaClass.getQualifiedName(); result = "java.lang.Integer";
            System.getProperty("org.eclipse.persistence.moxy.annotation.xml-id-extension"); result = "false";
            helper.isAnnotationPresent((JavaHasAnnotations)any, XmlIDExtension.class); result = false;
            property.isXmlIdExtension(); result = false;
        }};

        AnnotationsProcessor processor = new AnnotationsProcessor(helper);
        try {
            Deencapsulation.invoke(processor, "validateXmlIdStringType", property);
            fail("Expected JAXBException.");
        } catch (JAXBException expected) {
            assertEquals(50016, expected.getErrorCode());
        }

    }

}
