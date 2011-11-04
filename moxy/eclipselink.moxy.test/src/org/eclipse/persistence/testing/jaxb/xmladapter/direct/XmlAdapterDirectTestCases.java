/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAdapterDirectTestCases extends JAXBWithJSONTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/direct.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/direct.json";
    private final static int DAY = 12; 
    private final static int MONTH = 4; 
    private final static int YEAR = 1997; 

    public XmlAdapterDirectTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);      
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = MyCalendar.class;
        classes[1] = MyCalendarType.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        MyCalendar myCal = new MyCalendar();
        MyCalendarType mcType = new MyCalendarType();
        mcType.day = DAY;
        mcType.month = MONTH;
        mcType.year = YEAR;
        myCal.date = mcType;
        return myCal;
    }
}
