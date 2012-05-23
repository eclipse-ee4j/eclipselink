/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmlidref;

//Example 2

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlIdRefContainmentTest extends JAXBWithJSONTestCases{

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlidref/xmlidrefcontainment.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlidref/xmlidrefcontainment.json";
	
	public XmlIdRefContainmentTest(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		Class[] classes = new Class[4];
		classes[0] = Customer.class;
		classes[1] = Invoice.class;
		classes[2] = Shipping.class;
		classes[3] = CustomerData.class;
		setClasses(classes);
	}

	protected Object getControlObject() {

		CustomerData example = new CustomerData();
		Customer customer = new Customer();
		customer.setCustomerID("Alice");
		customer.setName("Alice Jane");
		Invoice invoice = new Invoice();
		invoice.customer = customer;
		Shipping shippingTo = new Shipping();
		shippingTo.customer = customer;
		example.customer = customer;
		example.invoice = invoice;
		example.shipping = shippingTo;
        return example;
	}


}
