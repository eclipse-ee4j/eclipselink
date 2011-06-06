/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-08-13 13:49:00 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.required;

import junit.framework.TestCase;

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
            
            Project proj = generator.generateProject();

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