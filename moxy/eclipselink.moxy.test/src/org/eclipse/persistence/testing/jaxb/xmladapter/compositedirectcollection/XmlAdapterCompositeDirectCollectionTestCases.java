/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.xmladapter.compositedirectcollection;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import org.eclipse.persistence.testing.jaxb.xmladapter.direct.MyCalendarType;

public class XmlAdapterCompositeDirectCollectionTestCases extends JAXBWithJSONTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/compositedirectcollection.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/compositedirectcollection.json";
    private final static int DAY_1 = 12; 
    private final static int MONTH_1 = 4; 
    private final static int YEAR_1 = 1997; 
    private final static int DAY_2 = 11; 
    private final static int MONTH_2 = 6; 
    private final static int YEAR_2 = 2006; 

    public XmlAdapterCompositeDirectCollectionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = MyCalendar.class;
        classes[1] = MyCalendarType.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        MyCalendarType mcType = new MyCalendarType();
        mcType.day = DAY_1;
        mcType.month = MONTH_1;
        mcType.year = YEAR_1;
        
        MyCalendarType mcType2 = new MyCalendarType();
        mcType2.day = DAY_2;
        mcType2.month = MONTH_2;
        mcType2.year = YEAR_2;
        
        MyCalendar myCal = new MyCalendar();
        myCal.date = new ArrayList<MyCalendarType>();
        myCal.date.add(mcType);
        myCal.date.add(mcType2);
        return myCal;
    }
}
