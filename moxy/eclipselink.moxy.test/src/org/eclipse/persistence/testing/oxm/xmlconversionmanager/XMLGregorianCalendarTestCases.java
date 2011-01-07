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
package org.eclipse.persistence.testing.oxm.xmlconversionmanager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Calendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.persistence.Version;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.JavaPlatform;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XMLGregorianCalendarTestCases extends XMLMappingTestCases {
    
    private DatatypeProject proj = new DatatypeProject();
    
    public XMLGregorianCalendarTestCases(String name) throws Exception {
        super(name);
        setProject(proj);
        setControlDocument("org/eclipse/persistence/testing/oxm/xmlconversionmanager/emp.xml");
    }

    public void setUp() throws Exception {
        super.setUp();
        getXmlConversionManager().setTimeZoneQualified(true);
        getXmlConversionManager().setTimeZone(TimeZone.getTimeZone("GMT+3"));
    }
    
    public Object getControlObject() {
        try {
            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set(1976, Calendar.FEBRUARY, 17, 06, 15, 30);
            cal.setTimeZone(TimeZone.getTimeZone("GMT+3"));
            
            XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            xcal.setYear(2009);
            xcal.setMonth(2);
            xcal.setDay(17);
            xcal.setHour(07);
            xcal.setMinute(30);
            xcal.setSecond(0);
            xcal.setMillisecond(0);
            xcal.setTimezone(+180);
            
            Duration dur = DatatypeFactory.newInstance().newDurationDayTime(true, 14, 3, 30, 0);
            
            DatatypeEmployee emp = new DatatypeEmployee();
            emp.name = "Alan Smithee";
            emp.deptNumber = 172;
            emp.birthDate = cal;
            emp.hireDate = xcal;
            emp.vacationTaken = dur;
            
            return emp;
        } catch (DatatypeConfigurationException dce) {
            dce.printStackTrace();
            return null;
        }
    }
    
    public Document getWriteControlDocument() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/xmlconversionmanager/emp.xml");
        Document doc = parser.parse(inputStream);
        removeEmptyTextNodes(doc);
        inputStream.close();
        return doc;
    }
    
    public void testRoundtrip() throws Exception {
        getXmlConversionManager().setTimeZoneQualified(true);
        
        Document controlDoc = getWriteControlDocument();
        
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/xmlconversionmanager/emp.xml");
        
        Object objFromXML = xmlUnmarshaller.unmarshal(inputStream);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        xmlMarshaller.marshal(objFromXML, outputStream);
        StringReader sreader = new StringReader(outputStream.toString());
        InputSource inputSource = new InputSource(sreader);
        Document testDoc = parser.parse(inputSource);
        removeEmptyTextNodes(testDoc);
        sreader.close();
        
        assertXMLIdentical(controlDoc, testDoc);
    }

    public void testRoundtripNoTimeZone() throws Exception {
        // Temporarily undo the TimeZone configuration so that our Object will be
        // in the VM's time zone:
        getXmlConversionManager().setTimeZone(null);
        
        Document controlDoc = getWriteControlDocument();  
        
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/xmlconversionmanager/emp.xml");

        Object objFromXML = xmlUnmarshaller.unmarshal(inputStream);

        // Now set the TimeZone again, to ensure XMLConversionManager is making the
        // proper adjustments:
        getXmlConversionManager().setTimeZone(TimeZone.getTimeZone("GMT+3"));
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        xmlMarshaller.marshal(objFromXML, outputStream);
        StringReader sreader = new StringReader(outputStream.toString());
        InputSource inputSource = new InputSource(sreader);
        Document testDoc = parser.parse(inputSource);
        removeEmptyTextNodes(testDoc);
        sreader.close();
        
        assertXMLIdentical(controlDoc, testDoc);
    }
    
    public void testConversionFromObjectWithSchemaType() throws Exception {
        XMLGregorianCalendar aCal = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        aCal.setYear(2009);
        aCal.setMonth(2);
        aCal.setDay(17);
        aCal.setHour(07);
        aCal.setMinute(30);
        aCal.setSecond(0);
        aCal.setMillisecond(0);
        aCal.setTimezone(+180);

        String testString;
        
        String gDayString       = "---17+03:00";
        String gMonthString     = "--02+03:00";
        String gMonthDayString  = "--02-17+03:00";
        String gYearString      = "2009+03:00";
        String gYearMonthString = "2009-02+03:00";
        String dateString       = "2009-02-17+03:00";
        String timeString       = "07:30:00.0+03:00";
        String dateTimeString   = "2009-02-17T07:30:00.0+03:00";
        
        testString = (String) getXmlConversionManager().convertObject(aCal, String.class, XMLConstants.G_DAY_QNAME);
        assertEquals("Object to String conversion failed.", gDayString, testString);
        
        testString = (String) getXmlConversionManager().convertObject(aCal, String.class, XMLConstants.G_MONTH_QNAME);
        assertEquals("Object to String conversion failed.", gMonthString, testString);
            
        testString = (String) getXmlConversionManager().convertObject(aCal, String.class, XMLConstants.G_MONTH_DAY_QNAME);
        assertEquals("Object to String conversion failed.", gMonthDayString, testString);
        
        testString = (String) getXmlConversionManager().convertObject(aCal, String.class, XMLConstants.G_YEAR_QNAME);
        assertEquals("Object to String conversion failed.", gYearString, testString);
        
        testString = (String) getXmlConversionManager().convertObject(aCal, String.class, XMLConstants.G_YEAR_MONTH_QNAME);
        assertEquals("Object to String conversion failed.", gYearMonthString, testString);
        
        testString = (String) getXmlConversionManager().convertObject(aCal, String.class, XMLConstants.DATE_QNAME);
        assertEquals("Object to String conversion failed.", dateString, testString);
        
        testString = (String) getXmlConversionManager().convertObject(aCal, String.class, XMLConstants.TIME_QNAME);
        assertEquals("Object to String conversion failed.", timeString, testString);
        
        testString = (String) getXmlConversionManager().convertObject(aCal, String.class, XMLConstants.DATE_TIME_QNAME);
        assertEquals("Object to String conversion failed.", dateTimeString, testString);        
    }
    
    public void testConversionFromStringWithSchemaType() throws Exception {
        String aString = "2009-02-17T07:30:00.000+03:00";
        
        XMLGregorianCalendar c = (XMLGregorianCalendar) getXmlConversionManager().convertStringToXMLGregorianCalendar(aString, XMLConstants.DATE_QNAME);
        
        assertEquals("Calendar's 'hour' field was not cleared.", c.getHour(), DatatypeConstants.FIELD_UNDEFINED);
        assertEquals("Calendar's 'minute' field was not cleared.", c.getMinute(), DatatypeConstants.FIELD_UNDEFINED);
        assertEquals("Calendar's 'second' field was not cleared.", c.getSecond(), DatatypeConstants.FIELD_UNDEFINED);
        assertEquals("Calendar's 'millisecond' field was not cleared.", c.getMillisecond(), DatatypeConstants.FIELD_UNDEFINED);
        
        assertNotSame("Calendar's 'year' field was not set.", c.getYear(), DatatypeConstants.FIELD_UNDEFINED);
        assertNotSame("Calendar's 'month' field was not set.", c.getMonth(), DatatypeConstants.FIELD_UNDEFINED);
        assertNotSame("Calendar's 'day' field was not set.", c.getDay(), DatatypeConstants.FIELD_UNDEFINED);
    }
    
    public XMLConversionManager getXmlConversionManager() {
        return (XMLConversionManager) xmlContext.getSession(0).getDatasourceLogin().getDatasourcePlatform().getConversionManager();
    }
    
}
