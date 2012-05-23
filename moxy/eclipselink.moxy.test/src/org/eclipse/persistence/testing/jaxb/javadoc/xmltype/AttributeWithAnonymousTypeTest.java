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
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmltype;

import java.math.BigInteger;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class AttributeWithAnonymousTypeTest extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmltype/AttributeWithAnonymousType.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmltype/AttributeWithAnonymousType.json";

    public AttributeWithAnonymousTypeTest(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Item.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Item getControlObject() {
        Item item = new Item();
        item.name = "Foo";
        USPrice price = new USPrice();
        price.price = new BigInteger("123");
        item.price = price;
        return item;
	}

}
