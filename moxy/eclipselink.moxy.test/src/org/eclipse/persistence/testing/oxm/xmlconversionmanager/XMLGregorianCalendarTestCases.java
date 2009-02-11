/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XMLGregorianCalendarTestCases extends XMLMappingTestCases {
    
    private XMLContext ctx;
    private DatatypeProject proj = new DatatypeProject();
    
    public XMLGregorianCalendarTestCases(String name) throws Exception {
        super(name);
        setProject(proj);
        ctx = getXMLContext(proj);
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
        
        Object objFromXML = ctx.createUnmarshaller().unmarshal(inputStream);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ctx.createMarshaller().marshal(objFromXML, outputStream);
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

        Object objFromXML = ctx.createUnmarshaller().unmarshal(inputStream);

        // Now set the TimeZone again, to ensure XMLConversionManager is making the
        // proper adjustments:
        getXmlConversionManager().setTimeZone(TimeZone.getTimeZone("GMT+3"));
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ctx.createMarshaller().marshal(objFromXML, outputStream);
        StringReader sreader = new StringReader(outputStream.toString());
        InputSource inputSource = new InputSource(sreader);
        Document testDoc = parser.parse(inputSource);
        removeEmptyTextNodes(testDoc);
        sreader.close();
        
        assertXMLIdentical(controlDoc, testDoc);
    }
    
    public XMLConversionManager getXmlConversionManager() {
        return (XMLConversionManager) ctx.getSession(0).getDatasourceLogin().getDatasourcePlatform().getConversionManager();
    }
    
}