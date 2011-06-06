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

import javax.xml.namespace.QName;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class QNameTestCases extends OXTestCase {
    private static final QName CONTROL_QNAME = new QName("local");
    private static final QName CONTROL_QNAME_NS = new QName("urn:test", "local");
    private static final String CONTROL_STRING = "local";
    private static final String CONTROL_STRING_NS = "{urn:test}local";

    // XML Conversion Manager
    private XMLConversionManager xcm;

    public QNameTestCases(String name) {
        super(name);
    }

    public void setUp() {
        xcm = XMLConversionManager.getDefaultXMLManager();
    }

    public void testQNameToQName() {
        QName control = CONTROL_QNAME;
        QName test = (QName)xcm.convertObject(control, QName.class);
        assertEquals(control, test);
    }

    public void testQNameToString_default_null() {
        QName qName = null;
        QName control = null;
        String test = (String)xcm.convertObject(control, String.class);
        assertEquals(control, test);
    }

    public void testQNameToString_default() {
        QName qName = CONTROL_QNAME;
        String control = CONTROL_STRING;
        String test = (String)xcm.convertObject(qName, String.class);
        assertEquals(control, test);
    }

    public void testQNameToString_default_ns() {
        QName qName = CONTROL_QNAME_NS;
        String control = CONTROL_STRING_NS;
        String test = (String)xcm.convertObject(qName, String.class);
        assertEquals(control, test);
    }

    public void testQNameToString_qname() {
        QName qName = CONTROL_QNAME;
        String control = CONTROL_STRING;
        String test = (String)xcm.convertObject(qName, String.class, XMLConstants.QNAME_QNAME);
        assertEquals(control, test);
    }

    public void testQNameToString_qname_ns() {
        QName qName = CONTROL_QNAME_NS;
        String control = CONTROL_STRING_NS;
        String test = (String)xcm.convertObject(qName, String.class, XMLConstants.QNAME_QNAME);
        assertEquals(control, test);
    }

    public void testStringToQName_default_null() {
        String string = null;
        QName control = null;
        QName test = (QName)xcm.convertObject(string, QName.class);
        assertEquals(control, test);
    }

    public void testStringToQName_default() {
        String string = CONTROL_STRING;
        QName control = CONTROL_QNAME;
        QName test = (QName)xcm.convertObject(string, QName.class);
        assertEquals(control, test);
    }

    public void testStringToQName_default_ns() {
        String string = CONTROL_STRING_NS;
        QName control = CONTROL_QNAME_NS;
        QName test = (QName)xcm.convertObject(string, QName.class);
        assertEquals(control, test);
    }

    public void testStringToQName_qname() {
        String string = CONTROL_STRING;
        QName control = CONTROL_QNAME;
        QName test = (QName)xcm.convertObject(string, QName.class, XMLConstants.QNAME_QNAME);
        assertEquals(control, test);
    }

    public void testStringToQName_qname_ns() {
        String string = CONTROL_STRING_NS;
        QName control = CONTROL_QNAME_NS;
        QName test = (QName)xcm.convertObject(string, QName.class, XMLConstants.QNAME_QNAME);
        assertEquals(control, test);
    }

    public void testIntegerToQName_default() {
        try {
            Integer integer = new Integer(1);
            xcm.convertObject(integer, QName.class);
        } catch (ConversionException e) {
            assertTrue("The incorrect exception was thrown", e.getErrorCode() == ConversionException.COULD_NOT_BE_CONVERTED);
        }
    }

    public void testIntegerToQName_qname() {
        try {
            Integer integer = new Integer(1);
            xcm.convertObject(integer, QName.class, XMLConstants.QNAME_QNAME);
        } catch (ConversionException e) {
            assertTrue("The incorrect exception was thrown", e.getErrorCode() == ConversionException.COULD_NOT_BE_CONVERTED);
        }
    }
}
