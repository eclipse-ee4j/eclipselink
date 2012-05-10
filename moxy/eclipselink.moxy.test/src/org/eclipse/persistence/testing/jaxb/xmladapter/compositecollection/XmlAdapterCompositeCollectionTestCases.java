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
package org.eclipse.persistence.testing.jaxb.xmladapter.compositecollection;

import java.util.ArrayList;
import java.util.Date;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAdapterCompositeCollectionTestCases extends JAXBWithJSONTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/compositecollection.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/compositecollection.json";
    private final static int DAY_1 = 1;
    private final static int DAY_2 = 2;
    private final static int DAY_3 = 6;
    private final static int MONTH_1 = 1;
    private final static int MONTH_2 = 2;
    private final static int MONTH_3 = 6;
    private final static int YEAR_1 = 2001;
    private final static int YEAR_2 = 2002;
    private final static int YEAR_3 = 2006;

    public XmlAdapterCompositeCollectionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);       
        Class[] classes = new Class[2];
        classes[0] = MyDates.class;
        classes[1] = MyDateType.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        MyDates myDates = new MyDates();
        Date date1 = new Date(YEAR_1, MONTH_1, DAY_1);
        Date date2 = new Date(YEAR_2, MONTH_2, DAY_2);
        Date date3 = new Date(YEAR_3, MONTH_3, DAY_3);
        myDates.dateList = new ArrayList();
        myDates.dateList.add(date1);
        myDates.dateList.add(date2);
        myDates.dateList.add(date3);
        return myDates;
    }
}
