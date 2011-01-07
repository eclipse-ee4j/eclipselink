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
*     dmccann - Feb 09/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.xmlhelper;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.SDOXMLHelperTestCases;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class SDOXMLHelperExceptionTestCases extends SDOXMLHelperTestCases {
    public SDOXMLHelperExceptionTestCases(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.SDOXMLHelperExceptionTestCases" };
        TestRunner.main(arguments);
    }

    public void testSaveToStreamException() throws Exception {
        XMLDocument xmlDocument = null;
        OutputStream outputStream = new ByteArrayOutputStream();
        Object options = null; 
        try {
            xmlHelper.save(xmlDocument, outputStream, options);
            fail("An IllegalArugmentException should have occurred");
        } catch (Exception e) {}
    }

    public void testSaveToResultException() throws Exception {
        XMLDocument xmlDocument = null;
        Result outputResult = new StreamResult(); 
        Object options = null; 
        try {
            xmlHelper.save(xmlDocument, outputResult, options);
            fail("An IllegalArugmentException should have occurred");
        } catch (Exception e) {}
    }

    public void testSaveToWriterException() throws Exception {
        XMLDocument xmlDocument = null;
        Writer outputWriter = new PrintWriter(new ByteArrayOutputStream());
        Object options = null; 
        try {
            xmlHelper.save(xmlDocument, outputWriter, options);
            fail("An IllegalArugmentException should have occurred");
        } catch (Exception e) {}
    }
}
