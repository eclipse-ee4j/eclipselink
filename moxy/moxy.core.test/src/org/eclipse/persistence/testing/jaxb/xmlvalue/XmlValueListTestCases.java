/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlvalue;

import java.util.ArrayList;
import java.util.Calendar;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlValueListTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvalue/phone_number_list.xml";
    private final static String CONTROL_NUMBER = "123-4567";

    public XmlValueListTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
        Class[] classes = new Class[1];
        classes[0] = PhoneNumberList.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        PhoneNumberList pn = new PhoneNumberList();
        ArrayList numbers = new ArrayList();
        numbers.add("123-4567");
        numbers.add("345-6789");
        numbers.add("567-8901");
        pn.numbers = numbers;

        return pn;
    }
}