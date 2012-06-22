/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - April 7/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.events.sessionevents;

import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.internal.jaxb.SessionEventListener;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sessions.Project;

import junit.framework.TestCase;

/**
 * Tests use of custom Session event handling, such as disabling InstantiationPolicy
 * validation during Descriptor initialization.
 */
public class SessionEventTestCases extends TestCase {
    
    public SessionEventTestCases(String name) throws Exception {
        super(name);
    }
    
    /**
     * InstantiationPolicy validation should fail due to recursive constructor calls.
     * Test expects an exception.
     */
    public void testInstantiationPolicyValidationFailure() {
        Class[] classesToBeBound = new Class[] { Subclass.class, Superclass.class };
        
        // ensure instantiation policy validation is enabled during descriptor initialization
        SessionEventListener eventListener = new SessionEventListener();
        eventListener.setShouldValidateInstantiationPolicy(true);
        
        try {
            setupContext(classesToBeBound, eventListener);
        } catch (Exception x) {
            return;
        }
        fail("An exception did not occur as expected");
    }

    /**
     * Tests disabling of InstantiationPolicy validation during descriptor 
     * initialization. Test should pass w/o any exceptions.
     */
    public void testInstantiationPolicyValidationDisabled() {
        Class[] classesToBeBound = new Class[] { Subclass.class, Superclass.class };

        // disable instantiation policy validation during descriptor initialization
        SessionEventListener eventListener = new SessionEventListener();
        eventListener.setShouldValidateInstantiationPolicy(false);
        
        try {
            setupContext(classesToBeBound, eventListener);
        } catch (Exception x) {
            fail("An unexpected exception occurred: [" + x.getMessage() + "]");
        }
    }
    
    protected JAXBContext setupContext(Class[] classesToBeBound, SessionEventListener sel) throws Exception {
        javax.xml.bind.JAXBContext jaxbContext = null;
        XMLContext xmlContext = null;
        JaxbClassLoader loader = new JaxbClassLoader(Thread.currentThread().getContextClassLoader());
        try {
            Generator generator = new Generator(new JavaModelInputImpl(classesToBeBound, new JavaModelImpl(loader)));
                
            Project proj = generator.generateProject();
            ConversionManager conversionManager = null;
            conversionManager = new ConversionManager();
            conversionManager.setLoader(loader);
            
            proj.convertClassNamesToClasses(conversionManager.getLoader());
            for (Iterator<ClassDescriptor> descriptorIt = proj.getOrderedDescriptors().iterator(); descriptorIt.hasNext();) {
                ClassDescriptor descriptor = descriptorIt.next();
                if (descriptor.getJavaClass() == null) {
                    descriptor.setJavaClass(conversionManager.convertClassNameToClass(descriptor.getJavaClassName()));
                }
            }
            
            xmlContext = new XMLContext(proj, loader, sel);
            jaxbContext = new org.eclipse.persistence.jaxb.JAXBContext(xmlContext, generator, classesToBeBound);
        } catch (Exception ex) {
            throw ex;
        }
        return jaxbContext;
    }
}
