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
 *     Denise Smith - October 20, 2009
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.textui.TestRunner;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.helpers.DefaultHandler;

public class XMLMarshalExceptionTestCases  extends OXTestCase {
 
    private XMLContext context;

    public XMLMarshalExceptionTestCases(String name) {
        super(name);
     }

    public static void main(String[] args) {
        
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLMarshalExceptionTestCase" };
        TestRunner.main(arguments);
    }

    public void setUp() throws Exception {
    }
    
    public void testMarshalWithNoPrefixInNamespaceResolver() {
   
        DefaultHandler output = new DefaultHandler();
        try {
            context = getXMLContext(new XMLMarshallerExceptionTestProject());        
        } catch (org.eclipse.persistence.exceptions.IntegrityException e){
        	assertTrue(e.getIntegrityChecker().getCaughtExceptions().size() == 1);
        	Exception nestedException = (Exception)e.getIntegrityChecker().getCaughtExceptions().get(0);
        	assertTrue(nestedException instanceof XMLMarshalException);
        	assertTrue("An unexpected XMLMarshalException was caught.", ((XMLMarshalException)nestedException).getErrorCode() == XMLMarshalException.NAMESPACE_NOT_FOUND);
        	return;
        }

        assertTrue("An XMLValidation should have been caught but wasn't.", false);
    }   
}