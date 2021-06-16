/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     rbarkhouse - 2009-08-13 13:49:00 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.required;

import junit.framework.TestCase;

import org.eclipse.persistence.core.sessions.CoreProject;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.sessions.Project;

public class RequiredAnnotationTestCases extends TestCase {

    public void testAnnotationsProcessor() throws Exception {
        try {
            JaxbClassLoader classLoader = new JaxbClassLoader(Thread.currentThread().getContextClassLoader());
            Generator generator = new Generator(new JavaModelInputImpl(new Class[] { RequiredTestObject.class, RequiredTestSubObject.class }, new JavaModelImpl(classLoader)));

            Project proj = (Project)generator.generateProject();


            ClassDescriptor descriptor = proj.getDescriptorForAlias("RequiredTestObject");
            DatabaseMapping mapping = descriptor.getMappingForAttributeName("direct");
            boolean isRequired = ((XMLField) mapping.getField()).isRequired();
            assertTrue("The ('direct') mapping's XMLField's isRequired flag was not set to 'true' despite XML Annotation", isRequired);

            mapping = descriptor.getMappingForAttributeName("directAttribute");
            isRequired = ((XMLField) mapping.getField()).isRequired();
            assertTrue("The ('directAttribute') mapping's XMLField's isRequired flag was not set to 'true' despite XML Annotation", isRequired);

            mapping = descriptor.getMappingForAttributeName("directCollection");
            isRequired = ((XMLField) mapping.getField()).isRequired();
            assertTrue("The ('directCollection') mapping's XMLField's isRequired flag was not set to 'true' despite XML Annotation", isRequired);

            mapping = descriptor.getMappingForAttributeName("compositeObject");
            isRequired = ((XMLField) mapping.getField()).isRequired();
            assertTrue("The ('compositeObject') mapping's XMLField's isRequired flag was not set to 'true' despite XML Annotation", isRequired);

            mapping = descriptor.getMappingForAttributeName("compositeCollection");
            isRequired = ((XMLField) mapping.getField()).isRequired();
            assertTrue("The ('compositeCollection') mapping's XMLField's isRequired flag was not set to 'true' despite XML Annotation", isRequired);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
