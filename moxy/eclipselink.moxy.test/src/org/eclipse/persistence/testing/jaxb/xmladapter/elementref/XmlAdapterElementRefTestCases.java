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
 * dmccann - December 17/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.elementref;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlAdapterElementRefTestCases extends JAXBTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/transactionadd.xml";
    private final static String TXN_TYPE = "salesOrderAdd"; 

    public XmlAdapterElementRefTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
        Class[] classes = new Class[1];
        classes[0] = TransactionAdd.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        TransactionAdd ta = new TransactionAdd();
        ta.txnType = TXN_TYPE;
        return ta;
    }
}
