/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//    Denise Smith - February 20, 2013
package org.eclipse.persistence.testing.jaxb.xmlgregoriancalendar;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XMLGregorianCalendarObjectTestCases extends JAXBWithJSONTestCases{

    private static final String CONTROL_XML_DOCUMENT;

    static {
        CONTROL_XML_DOCUMENT = "<?xml version = '1.0' encoding = 'UTF-8'?>" +
            "<root>" +
            "<thing xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xsd:date\">1977-02-13</thing>" +
            "<things xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xsd:date\">1977-02-13</things>" +
            "<things xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xsd:date\">1982-05-30</things>" +
            "<things xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xsd:time\">09:30:05</things>" +
            "<things xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xsd:dateTime\">1985-09-23T10:33:05.001</things>" +
            "<things xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xsd:dateTime\">1977-02-13T08:30:02.000</things>" +
            "<things xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xsd:dateTime\">1977-02-13T08:30:02</things>" +
            "<things xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xsd:dateTime\">2013-02-20T10:29:58"+TIMEZONE_OFFSET+"</things>" +
            "<gregCal>1985-09-23T10:33:05.001</gregCal>" +
            "<gregCalTime>09:30:05</gregCalTime>" +
            "</root>";
    }

    public XMLGregorianCalendarObjectTestCases(String name) throws Exception {
        super(name);
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
            XMLGregorianCalendar cal5 = factory.newXMLGregorianCalendar(1977, 02, 13, 8, 30, 2, 0, DatatypeConstants.FIELD_UNDEFINED);
            XMLGregorianCalendar cal6 = factory.newXMLGregorianCalendar(1977, 02, 13, 8, 30, 2, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);

            holder.thing = cal;
            holder.things = new ArrayList();
            holder.things.add(cal);
            holder.things.add(cal2);
            holder.things.add(cal3);
            holder.things.add(cal4);
            holder.things.add(cal5);
            holder.things.add(cal6);

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

    public boolean isUnmarshalTest() {
        return false;
    }

    @Override
    protected String getControlJSONDocumentContent() {
        return "{\"root\":{\n" +
                "   \"thing\":{\n" +
                "      \"type\":\"date\",\n" +
                "      \"value\":\"1977-02-13\"\n" +
                "   },\n" +
                "   \"things\":[{\n" +
                "      \"type\":\"date\",\n" +
                "      \"value\":\"1977-02-13\"\n" +
                "     },{\n" +
                "      \"type\":\"date\",\n" +
                "      \"value\":\"1982-05-30\"\n" +
                "     },{\n" +
                "      \"type\":\"time\",\n" +
                "      \"value\":\"09:30:05\"\n" +
                "     },{\n" +
                "      \"type\":\"dateTime\",\n" +
                "      \"value\":\"1985-09-23T10:33:05.001\"\n" +
                "     },\n" +
                "     {\n" +
                "      \"type\":\"dateTime\",\n" +
                "      \"value\":\"1977-02-13T08:30:02.000\"\n" +
                "     },\n" +
                "     {\n" +
                "      \"type\":\"dateTime\",\n" +
                "      \"value\":\"1977-02-13T08:30:02\"\n" +
                "     },    \n" +
                "     {\n" +
                "      \"type\":\"dateTime\",\n" +
                "      \"value\":\"2013-02-20T10:29:58"+TIMEZONE_OFFSET+"\"\n" +
                "     }\n" +
                "   ],\n" +
                "   \"gregCal\":\"1985-09-23T10:33:05.001\",\n" +
                "   \"gregCalTime\":\"09:30:05\"\n" +
                "}}";
    }

    @Override
    protected Document getControlDocument() {
        StringReader reader = new StringReader(CONTROL_XML_DOCUMENT);
        InputSource is = new InputSource(reader);
        Document doc = null;
        try {
            doc = parser.parse(is);
        } catch (Exception e) {
            fail("An error occurred setting up the control document");
        }
        return doc;
    }

    @Override
    public Object getReadControlObject() {
        XMLGregorianCalendarHolder holder = (XMLGregorianCalendarHolder)getControlObject();
        Date removed = (Date)holder.things.remove(6);
        DatatypeFactory factory;
        try {
            factory = DatatypeFactory.newInstance();
            XMLGregorianCalendar xmlGrelCal = factory.newXMLGregorianCalendar("2013-02-20T10:29:58-05:00");
            holder.things.add(xmlGrelCal);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            fail();
        }
        return holder;
    }
}


