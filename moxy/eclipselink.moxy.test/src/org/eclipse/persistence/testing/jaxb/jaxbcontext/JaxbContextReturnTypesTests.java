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
 *     rbarkhouse - 2009-10-19 13:43:05  - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import javax.xml.bind.JAXBContext;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBBinder;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBHelper;
import org.eclipse.persistence.jaxb.JAXBIntrospector;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.JAXBValidator;

public class JaxbContextReturnTypesTests extends TestCase {

    private JAXBContext _jaxbContext;
    private org.eclipse.persistence.jaxb.JAXBContext _elinkContext;

    public JaxbContextReturnTypesTests() throws Exception {
        super();

        _jaxbContext = JAXBContextFactory.createContext("org.eclipse.persistence.testing.jaxb.jaxbcontext", Thread.currentThread().getContextClassLoader());
        _elinkContext = JAXBHelper.unwrap(_jaxbContext, org.eclipse.persistence.jaxb.JAXBContext.class);
    }

    public void testCreateMarshaller() throws Exception {
        // Test all create methods to ensure that a cast is not required
        JAXBMarshaller elinkMarshaller = _elinkContext.createMarshaller();
    }

    public void testCreateUnmarshaller() throws Exception {
        JAXBUnmarshaller elinkUnmarshaller = _elinkContext.createUnmarshaller();
    }

    public void testCreateValidator() throws Exception {
        JAXBValidator elinkValidator = _elinkContext.createValidator();
    }

    public void testCreateBinder() throws Exception {
        JAXBBinder elinkBinder = _elinkContext.createBinder();
    }

    public void testCreateBinderWithNodeClass() throws Exception {
        JAXBBinder elinkBinder = _elinkContext.createBinder(org.w3c.dom.Node.class);
    }

    public void testJAXBIntrospector() throws Exception {
        JAXBIntrospector elinkIntrospector = _elinkContext.createJAXBIntrospector();
    }

}