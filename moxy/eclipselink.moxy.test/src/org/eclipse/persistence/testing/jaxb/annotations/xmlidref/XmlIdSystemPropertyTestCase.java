/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Martin Vojtek - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlidref;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.eclipse.persistence.jaxb.compiler.AnnotationsProcessor;
import org.eclipse.persistence.jaxb.compiler.Property;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests useXmlIdExtension method when system property org.eclipse.persistence.moxy.annotation.xml-id-extension is set.
 */
@RunWith(JMockit.class)
public class XmlIdSystemPropertyTestCase {

    @Test
    public void testSystemXmlIdExtensionSet(final @Mocked Property property, final @Mocked({"isAnnotationPresent"}) Helper helper, final @Mocked JavaClass javaClass) {
        new Expectations(System.class) {{
            System.getProperty("org.eclipse.persistence.moxy.annotation.xml-id-extension"); returns("true");
        }};
        new Expectations() {{
            property.getActualType(); returns(javaClass);
            javaClass.getQualifiedName(); returns("java.lang.Integer");
        }};

        AnnotationsProcessor processor = new AnnotationsProcessor(helper);
        Deencapsulation.invoke(processor, "validateXmlIdStringType", property);
    }
}
