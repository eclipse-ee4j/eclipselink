/*******************************************************************************
* Copyright (c) 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - May 19/2010 - 2.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.jaxb.inverse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;

import org.eclipse.persistence.sdo.helper.jaxb.JAXBHelperContext;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.jaxb.Child1;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.jaxb.Child2;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.jaxb.Root;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class InverseTestCases extends SDOTestCase {

    private static final String XML_SCHEMA = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/jaxb/inverse/inverse.xsd";

    private JAXBHelperContext jaxbHelperContext;

    public InverseTestCases(String name) {
        super(name);
    }

    @Override
    public void setUp() {
        try {
            Class[] classes = new Class[1];
            classes[0] = Customer.class;
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            jaxbHelperContext = new JAXBHelperContext(jaxbContext);

            InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA);
            jaxbHelperContext.getXSDHelper().define(xsd, null);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testContainmentSingle1() {
        Customer customer = new Customer();
        DataObject customerDO = jaxbHelperContext.wrap(customer);
        customerDO.createDataObject("address");

        assertNotNull(customer.getAddress().getCustomer());
    }

    public void testContainmentSingle2() {
        Customer customer = new Customer();
        DataObject customerDO = jaxbHelperContext.wrap(customer);

        Type addressType = jaxbHelperContext.getType(Address.class);
        DataObject addressDO = jaxbHelperContext.getDataFactory().create(addressType);
        customerDO.set("address", addressDO);

        assertNotNull(customer.getAddress().getCustomer());
    }

    public void testContainmentMany1() {
        Customer customer = new Customer();
        DataObject customerDO = jaxbHelperContext.wrap(customer);
        customerDO.createDataObject("phoneNumber");

        assertNotNull(customer.getPhoneNumbers().get(0).getCustomer());
    }

    public void testContainmentMany2() {
        Customer customer = new Customer();
        DataObject customerDO = jaxbHelperContext.wrap(customer);

        Type phoneNumberType = jaxbHelperContext.getType(PhoneNumber.class);
        DataObject phoneNumberDO = jaxbHelperContext.getDataFactory().create(phoneNumberType);
        customerDO.getList("phoneNumber").add(phoneNumberDO);

        assertNotNull(customer.getPhoneNumbers().get(0).getCustomer());
    }

    public void testContainmentMany3() {
        Customer customer = new Customer();
        DataObject customerDO = jaxbHelperContext.wrap(customer);

        Type phoneNumberType = jaxbHelperContext.getType(PhoneNumber.class);
        List<DataObject> phoneNumberDOs = new ArrayList<DataObject>();
        phoneNumberDOs.add(jaxbHelperContext.getDataFactory().create(phoneNumberType));
        phoneNumberDOs.add(jaxbHelperContext.getDataFactory().create(phoneNumberType));
        customerDO.getList("phoneNumber").addAll(phoneNumberDOs);

        assertNotNull(customer.getPhoneNumbers().get(0).getCustomer());
        assertNotNull(customer.getPhoneNumbers().get(1).getCustomer());
    }

    public void testContainmentMany4() {
        Customer customer = new Customer();
        DataObject customerDO = jaxbHelperContext.wrap(customer);

        Type phoneNumberType = jaxbHelperContext.getType(PhoneNumber.class);
        List<DataObject> phoneNumberDOs = new ArrayList<DataObject>();
        phoneNumberDOs.add(jaxbHelperContext.getDataFactory().create(phoneNumberType));
        phoneNumberDOs.add(jaxbHelperContext.getDataFactory().create(phoneNumberType));
        customerDO.getList("phoneNumber").addAll(0, phoneNumberDOs);

        assertNotNull(customer.getPhoneNumbers().get(0).getCustomer());
    }

    public void testContainmentMany5() {
        Customer customer = new Customer();
        DataObject customerDO = jaxbHelperContext.wrap(customer);

        Type phoneNumberType = jaxbHelperContext.getType(PhoneNumber.class);
        List<DataObject> phoneNumberDOs = new ArrayList<DataObject>();
        phoneNumberDOs.add(jaxbHelperContext.getDataFactory().create(phoneNumberType));
        phoneNumberDOs.add(jaxbHelperContext.getDataFactory().create(phoneNumberType));
        customerDO.setList("phoneNumber", phoneNumberDOs);

        assertNotNull(customer.getPhoneNumbers().get(0).getCustomer());
    }

    @Override
    public void tearDown() throws Exception {
    }

}