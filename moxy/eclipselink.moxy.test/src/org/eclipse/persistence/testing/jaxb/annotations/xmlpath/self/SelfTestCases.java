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
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.self;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class SelfTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/self.xml";

    public SelfTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setTypes(new Class[] {PaymentInf.class});
    }

    @Override
    protected Object getControlObject() {
        PaymentInf p = new PaymentInf();
        p.setId("PAYMENT-ID");

        Creditor c = new Creditor();
        c.setId("CREDITOR-ID");
        p.setCreditor(c);

        return p;
    }

}