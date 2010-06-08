/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.transformation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 *  @version $Header: TransformationMappingErrorTestCases.java 02-nov-2006.10:56:47 gyorke Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class TransformationMappingErrorTestCases extends TestCase {
    XMLMarshaller marshaller;
    XMLContext context;
    XMLUnmarshaller unmarshaller;

    public void setUp() {
        context = new XMLContext("TransformationMappingSession");
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();

    }

    public void testNoClassForFieldTransformation() {
        ClassDescriptor descriptor = context.getSession(0).getDescriptor(Employee.class);
        XMLTransformationMapping mapping = new XMLTransformationMapping();
        mapping.setDescriptor(descriptor);
        //mapping.addFieldTransformerClassName("/normal-hours/start-time", "this.is.a.DummyClass");     
        DescriptorException expected = DescriptorException.fieldTransformerClassNotFound("dummy_class_name", mapping, new Exception());
        try {
            mapping.initialize((AbstractSession)context.getSession(0));
        } catch (DescriptorException ex) {
            assertTrue("The incorrect exception was thrown. [Expected] " + expected + " [Found] " + ex, ex.getErrorCode() == expected.getErrorCode());
        }
    }

    public void testInvalidClassForFieldTransformation() {
        ClassDescriptor descriptor = context.getSession(0).getDescriptor(Employee.class);
        XMLTransformationMapping mapping = new XMLTransformationMapping();
        mapping.setDescriptor(descriptor);        
        mapping.addFieldTransformerClassName("/normal-hours/start-time", "org.eclipse.persistence.testing.oxm.mappings.transformation.Employee");       
        DescriptorException expected = DescriptorException.fieldTransformerClassInvalid("dummy_class_name", mapping, new Exception());
        try {
            mapping.initialize((AbstractSession)context.getSession(0));
        } catch (DescriptorException ex) {
            assertTrue("The incorrect exception was thrown. [Expected] " + expected + " [Found] " + ex, ex.getErrorCode() == expected.getErrorCode());
        }
    }


/* *  Not a valid test since class name methods are internal and should not be used
 
    public void testNoClassForAttributeTransformation() {
        Descriptor descriptor = context.getSession(0).getDescriptor(Employee.class);
        XMLTransformationMapping mapping = new XMLTransformationMapping();
        mapping.setDescriptor(descriptor);
        mapping.addFieldTransformer("/normal-hours/start-time", new org.eclipse.persistence.testing.oxm.mappings.transformation.StartTimeTransformer());
        mapping.setAttributeName("normalHours");
        //mapping.setAttributeTransformerClassName("this.is.a.fake.class");
        //mapping.setAttributeTransformerClass(null);
        DescriptorException expected = DescriptorException.attributeTransformerClassNotFound("dummy_class_name", mapping, new Exception());
        try {
            mapping.initialize((org.eclipse.persistence.publicinterface.Session)context.getSession(0));
        } catch (DescriptorException ex) {
            assertTrue("The incorrect exception was thrown. [Expected] " + expected + " [Found] " + ex, ex.getErrorCode() == expected.getErrorCode());
        }
    }
    */

/*
 *  Not a valid test sint class name methods are internal and should not be used
    public void testInvalidForAttributeTransformation() {
        Descriptor descriptor = context.getSession(0).getDescriptor(Employee.class);
        XMLTransformationMapping mapping = new XMLTransformationMapping();
        mapping.setDescriptor(descriptor);        
        mapping.addFieldTransformer("/normal-hours/start-time", new org.eclipse.persistence.testing.oxm.mappings.transformation.StartTimeTransformer());
        mapping.setAttributeName("normalHours");
        
        mapping.setAttributeTransformerClass(String.class);
        DescriptorException expected = DescriptorException.attributeTransformerClassInvalid("dummy_class_name", mapping, new Exception());
        try {
            mapping.initialize((org.eclipse.persistence.publicinterface.Session)context.getSession(0));
        } catch (DescriptorException ex) {
            assertTrue("The incorrect exception was thrown. [Expected] " + expected + " [Found] " + ex, ex.getErrorCode() == expected.getErrorCode());
        }
    }
    */
}
