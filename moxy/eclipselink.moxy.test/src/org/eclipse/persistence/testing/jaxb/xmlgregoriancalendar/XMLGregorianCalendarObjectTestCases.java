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
 *    Denise Smith - February 20, 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlgregoriancalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XMLGregorianCalendarObjectTestCases extends JAXBWithJSONTestCases{

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlgregoriancalendar/xmlgregorian.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlgregoriancalendar/xmlgregorian.json";

    public XMLGregorianCalendarObjectTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		setClasses(new Class[] {XMLGregorianCalendarHolder.class});
	}

	@Override
	protected Object getControlObject() {
		XMLGregorianCalendarHolder holder = new XMLGregorianCalendarHolder();
		DatatypeFactory factory;
		try {
			factory = DatatypeFactory.newInstance();
			Calendar calendar = Calendar.getInstance();
			calendar.clear();
		    XMLGregorianCalendar cal = factory.newXMLGregorianCalendarDate(1977, 02, 13, DatatypeConstants.FIELD_UNDEFINED);
		    XMLGregorianCalendar cal2 = factory.newXMLGregorianCalendarDate(1982, 5, 30, DatatypeConstants.FIELD_UNDEFINED);
		    XMLGregorianCalendar cal3 = factory.newXMLGregorianCalendarTime(9, 30, 05, DatatypeConstants.FIELD_UNDEFINED);
		    XMLGregorianCalendar cal4 = factory.newXMLGregorianCalendar(1985, 9, 23, 10, 33, 05, 1, DatatypeConstants.FIELD_UNDEFINED);
		    holder.thing = cal;
		    holder.things = new ArrayList();
		    holder.things.add(cal);
		    holder.things.add(cal2);
		    holder.things.add(cal3);
		    holder.things.add(cal4);
		    
		    calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		    calendar.set(Calendar.DAY_OF_MONTH, 20);
		    calendar.set(Calendar.YEAR, 2013);
		    calendar.set(Calendar.HOUR_OF_DAY, 10);
		    calendar.set(Calendar.MINUTE, 29);
		    calendar.set(Calendar.SECOND, 58);
		    holder.things.add(calendar.getTime());
		    
		    holder.gregCal= cal4;
		    holder.gregCalTime = cal3;
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail();
		}
		return holder;
	}
    
	@Override
	public Object getReadControlObject() {
		XMLGregorianCalendarHolder holder = (XMLGregorianCalendarHolder)getControlObject();
		Date removed = (Date)holder.things.remove(4);
		DatatypeFactory factory;		
		try {		 
			factory = DatatypeFactory.newInstance();					
		    XMLGregorianCalendar xmlGrelCal = factory.newXMLGregorianCalendar(2013, DatatypeConstants.FEBRUARY,20, 10,29, 58, 0, DatatypeConstants.FIELD_UNDEFINED);
		    holder.things.add(xmlGrelCal);
		} catch (DatatypeConfigurationException e) {			
			e.printStackTrace();
			fail();
		}
		return holder;
	}
}


