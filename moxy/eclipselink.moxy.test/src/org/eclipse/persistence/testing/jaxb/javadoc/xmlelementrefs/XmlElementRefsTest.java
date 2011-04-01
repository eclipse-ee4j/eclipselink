/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelementrefs;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlElementRefsTest extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlelementrefs/xmlelementrefs.xml";

    public XmlElementRefsTest(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
        Class[] classes = new Class[4];

        classes[0] = TransportType.class;
        classes[1] = ViaAir.class;
        classes[2] = ViaLand.class;
        classes[3] = PurchaseOrder.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
    	PurchaseOrder order = new PurchaseOrder();
    	ViaAir byAir = new ViaAir();
    	byAir.airliner = "Air Canada";
    	byAir.transportTypeID = 101;
    	byAir.transportCost = 123.99;
    	order.shipBy = byAir;
        return order;
    }
}
