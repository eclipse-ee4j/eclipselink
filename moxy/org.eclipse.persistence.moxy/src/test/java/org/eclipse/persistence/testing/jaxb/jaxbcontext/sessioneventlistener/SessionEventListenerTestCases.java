/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.jaxbcontext.sessioneventlistener;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.sessions.SessionEventListener;

public class SessionEventListenerTestCases extends TestCase {

    private static final String XML = "<address><id>123</id></address>";

    private SessionEventListener sessionEventListener = null;


    @Override
    protected void setUp() throws Exception {
        sessionEventListener = new TestSessionEventListener();
        Address.INSTANTIATION_COUNTER = 0;
    }

    public void testClassArrayMap() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JAXBContextProperties.SESSION_EVENT_LISTENER, sessionEventListener);
        JAXBContext jc = (JAXBContext) JAXBContextFactory.createContext(new Class[] {Address.class}, properties);
        assertEquals(0, Address.INSTANTIATION_COUNTER);
        unmarshalTest(jc);
    }

    public void testClassArrayMapClassLoader() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JAXBContextProperties.SESSION_EVENT_LISTENER, sessionEventListener);
        JAXBContext jc = (JAXBContext) JAXBContextFactory.createContext(new Class[] {Address.class}, properties, Address.class.getClassLoader());
        assertEquals(0, Address.INSTANTIATION_COUNTER);
        unmarshalTest(jc);
    }

    public void testContextPathClassLoaderMap() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JAXBContextProperties.SESSION_EVENT_LISTENER, sessionEventListener);
        JAXBContext jc = (JAXBContext) JAXBContextFactory.createContext("org.eclipse.persistence.testing.jaxb.jaxbcontext.sessioneventlistener", Address.class.getClassLoader(), properties);
        assertEquals(0, Address.INSTANTIATION_COUNTER);
        unmarshalTest(jc);
    }

    public void testTypeArrayMapClassLoader() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JAXBContextProperties.SESSION_EVENT_LISTENER, sessionEventListener);
        JAXBContext jc = (JAXBContext) JAXBContextFactory.createContext(new Type[] {Address.class}, properties, Address.class.getClassLoader());
        assertEquals(0, Address.INSTANTIATION_COUNTER);
        unmarshalTest(jc);
    }

    public void testTypeMappingInfoArrayMapClassLoader() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JAXBContextProperties.SESSION_EVENT_LISTENER, sessionEventListener);
        TypeMappingInfo[] typeMappingInfos = new TypeMappingInfo[1];
        TypeMappingInfo typeMappingInfo = new TypeMappingInfo();
        typeMappingInfo.setType(Address.class);
        typeMappingInfos[0] = typeMappingInfo;
        JAXBContext jc = (JAXBContext) JAXBContextFactory.createContext(typeMappingInfos, properties, Address.class.getClassLoader());
        assertEquals(0, Address.INSTANTIATION_COUNTER);
        unmarshalTest(jc);
    }

    private void unmarshalTest(JAXBContext jc) throws Exception{
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Object test = unmarshaller.unmarshal(new StringReader(XML));
        assertEquals(getControlObject(), test);
    }

    private AddressAddedByEvent getControlObject() {
        AddressAddedByEvent control = new AddressAddedByEvent();
        control.setId(123);
        return control;
    }

}
