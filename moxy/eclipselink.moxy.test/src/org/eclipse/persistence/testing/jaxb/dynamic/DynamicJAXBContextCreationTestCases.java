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
 *     rbarkhouse - 2010-03-04 12:22:11 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.dynamic;

import java.math.BigInteger;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.DynamicJAXBContext;

public class DynamicJAXBContextCreationTestCases extends TestCase {

    private static final String DOCWRAPPER_CLASS_NAME = 
        "org.persistence.testing.jaxb.dynamic.xxx.DocWrapper";

    private static final String SESSION_NAMES = 
        "org.eclipse.persistence.testing.jaxb.dynamic:org.eclipse.persistence.testing.jaxb.dynamic.secondproject";
    
    public DynamicJAXBContextCreationTestCases(String name) throws Exception {
        super(name);
    }

    public void testNewInstanceString() throws JAXBException {
        DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance(SESSION_NAMES);
        DynamicEntity docWrapper = jaxbContext.newDynamicEntity(DOCWRAPPER_CLASS_NAME);
        assertNotNull(docWrapper);
    }

    public void testNewInstanceStringLoader() throws JAXBException {
        DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance(SESSION_NAMES, Thread.currentThread().getContextClassLoader());
        DynamicEntity docWrapper = jaxbContext.newDynamicEntity(DOCWRAPPER_CLASS_NAME);
        assertNotNull(docWrapper);
    }

    public void testNewInstanceStringLoaderProps() throws JAXBException {
        DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance(SESSION_NAMES, Thread.currentThread().getContextClassLoader(), new HashMap());
        DynamicEntity docWrapper = jaxbContext.newDynamicEntity(DOCWRAPPER_CLASS_NAME);
        assertNotNull(docWrapper);
    }
    
    public void testNewInstanceClasses() throws JAXBException {
        Class[] classes = new Class[] { FirstFieldTransformer.class, SecondFieldTransformer.class };
        JAXBException ex = null;
        try {
            DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance(classes); 
        } catch (JAXBException e) {
            ex = e;
        }

        assertNotNull("Did not catch exception as expected.", ex);
    }
    
    public void testNewInstanceClassesProps() throws JAXBException {
        Class[] classes = new Class[] { FirstFieldTransformer.class, SecondFieldTransformer.class };
        JAXBException ex = null;
        try {
            DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance(classes, new HashMap());
        } catch (JAXBException e) {
            ex = e;
        }

        assertNotNull("Did not catch exception as expected.", ex);
    }
    
}