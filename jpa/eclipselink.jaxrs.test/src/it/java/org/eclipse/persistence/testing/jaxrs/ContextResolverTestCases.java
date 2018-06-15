/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3 - initial implementation
//     Praba Vijayaratnam - 2.3 - test automation
//     Praba Vijayaratnam - 2.4 - added JSON support testing
package org.eclipse.persistence.testing.jaxrs;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.jaxrs.model.Address;
import org.eclipse.persistence.testing.jaxrs.utils.JAXRSPopulator;
import org.eclipse.persistence.testing.jaxrs.utils.JAXRSTableCreator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ContextResolverTestCases extends JUnitTestCase {

    private JAXBContext jc;
    protected DatabaseSession session;

    public ContextResolverTestCases(String name) throws Exception {
        super(name);
        Map<String, Object> props = new HashMap<String, Object>(1);
        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, "META-INF/binding-address.xml");
        jc = JAXBContext.newInstance(new Class[] { Address.class }, props);
    }

    public JAXRSPopulator setup() {
        session = JUnitTestCase.getServerSession();
        //new JAXRSTableCreator().replaceTables(session);
        JAXRSTableCreator tableCreator = new JAXRSTableCreator();
        tableCreator.dropTableConstraints(session);
        tableCreator.replaceTables(session);
        JAXRSPopulator jaxrsPopulator = new JAXRSPopulator();
        return jaxrsPopulator;
    }

    protected Address getControlObject2() {
        Address address = new Address();
        address.setId(2);
        address.setStreet("1111 Moose Rd.");
        address.setCity("Calgary");
        return address;
    }

    protected Address getControlObject11() {
        Address address = new Address();
        address.setId(11);
        address.setStreet("11 Nowhere Drive");
        address.setCity("Orleans");
        return address;
    }

    protected String getID() {
        return "2";
    }

    protected JAXBContext getJAXBContext() {
        return jc;
    }

    /* antbuild.xml will replace %%host:port%% with values provided in {server}.properties
     * sample URLs:
     *     weblogic: "http://localhost:7001/CustomerWAR/rest/address_war"
     *    glassfish: "http://localhost:8080/CustomerWAR/rest/address_war"
     */
    protected String getURL() {
        return "http://%%host:port%%/CustomerWAR/rest/address_war";
    }

    /* READ operation  - Uses Customer 2 */
    public void testGetAddress() throws Exception {
        JAXRSPopulator jaxrsPopulator = setup();
        jaxrsPopulator.buildExamplesCustomer2();
        jaxrsPopulator.persistExample(session);

        URL url = new URL(getURL() + "/" + "2");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml");

        InputStream xml = connection.getInputStream();
        Address testObject = (Address) getJAXBContext().createUnmarshaller().unmarshal(xml);
        int response = connection.getResponseCode();
        connection.disconnect();

        assertTrue (( response < 300) && ( response >= 200));
        assertEquals(getControlObject2(), testObject);
    }

    /* READ operation  - Uses Customer 11 */
    public void testGetAddressJSON() throws Exception {
        JAXRSPopulator jaxrsPopulator = setup();
        jaxrsPopulator.buildExamplesCustomer11();
        jaxrsPopulator.persistExample(session);

        URL url = new URL(getURL() + "/" + "11");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        InputStream inputStream = connection.getInputStream();
        StreamSource json = new StreamSource(inputStream);
        Unmarshaller u = getJAXBContext().createUnmarshaller();
                u.setProperty("eclipselink.media-type", "application/json");
                u.setProperty("eclipselink.json.include-root", false);
        Address testObject = u.unmarshal(json, Address.class).getValue();
        int response = connection.getResponseCode();
        connection.disconnect();

        assertTrue (( response < 300) && ( response >= 200));
        assertEquals(getControlObject11(), testObject);
    }

}
