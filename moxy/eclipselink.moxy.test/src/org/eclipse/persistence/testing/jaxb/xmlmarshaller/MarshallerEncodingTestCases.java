/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;

public class MarshallerEncodingTestCases extends TestCase {
    private final static String XML_USASCII_HEADER = "<?xml version=\"1.0\" encoding=\"US-ASCII\"?>";
    private final static String XML_UTF8_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private final static String XML_UTF16_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>";
    private final static String XML_UTF16BE_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-16BE\"?>";
    private final static String XML_UTF16LE_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-16LE\"?>";
    private final static String XML_ISOLATIN_HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
    private final static String XML_INVALID_HEADER = "<?xml version=\"1.0\" encoding = \"INVALID\"?>";
    private final static String XML_BODY = "<employee><id>123</id><name>Bob</name><phone>123456789</phone></employee>";

    public MarshallerEncodingTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.jaxb.MarshallerEncodingTestCases" };
        TestRunner.main(arguments);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("org.eclipse.persistence.testing.oxm.jaxb.MarshallerEncodingTestCases");

        suite.addTest(buildEncodingSuite("US-ASCII", XML_USASCII_HEADER));
        suite.addTest(buildEncodingSuite("UTF-8", XML_UTF8_HEADER));
        suite.addTest(buildEncodingSuite("UTF-16", XML_UTF16_HEADER));
        suite.addTest(buildEncodingSuite("UTF-16BE", XML_UTF16BE_HEADER));
        suite.addTest(buildEncodingSuite("UTF-16LE", XML_UTF16LE_HEADER));
        suite.addTest(buildEncodingSuite("ISO-8859-1", XML_ISOLATIN_HEADER));

        suite.addTest(new MarshallerEncodingTest("testInvalidEncoding", "INVALID_ENCODING", XML_INVALID_HEADER + XML_BODY));
        return suite;
    }

    private static TestSuite buildEncodingSuite(String encoding, String controlHeader) {
        TestSuite suite = new TestSuite(encoding);

        String controlString = controlHeader + Helper.cr() + XML_BODY;

        // suite.addTest(new MarshallerEncodingTest("testXMLHeader", encoding, controlString));
        suite.addTest(new MarshallerEncodingTest("testXMLEncoding", encoding, controlString));
        suite.addTest(new MarshallerEncodingTest("testXMLEncodingFileOutputStream", encoding, controlString));
        suite.addTest(new MarshallerEncodingTest("testXMLEncodingFileOutputStreamEnc", encoding, controlString));
        suite.addTest(new MarshallerEncodingTest("testXMLEncodingFileWriter", encoding, controlString));
        //suite.addTest(new MarshallerEncodingTest("testXMLEncodingResult", encoding, controlString));		
        return suite;
    }
}
