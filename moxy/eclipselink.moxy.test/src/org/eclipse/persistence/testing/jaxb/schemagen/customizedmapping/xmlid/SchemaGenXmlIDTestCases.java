/*******************************************************************************
* Copyright (c) 1998, 2009 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* dmccann - June 2/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlid;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.XMLConstants;

import junit.framework.TestCase;

public class SchemaGenXmlIDTestCases  extends TestCase {
    static String tmpdir;

    public SchemaGenXmlIDTestCases(String name) throws Exception {
        super(name);
        tmpdir = (System.getenv("T_WORK") == null ? "" : (System.getenv("T_WORK") + "/"));
    }
    
    /**
     * Exception case - @XmlID must be on String property
     */
    public void testInvalidXmlID() {
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        boolean exception = false;
        try {
            Class[] classes = new Class[]{ MyInvalidClass.class }; 
            JAXBContext context = (org.eclipse.persistence.jaxb.JAXBContext) org.eclipse.persistence.jaxb.JAXBContextFactory.createContext(classes, null);
            context.generateSchema(outputResolver);
        } catch (Exception ex) {
            exception = true;
        }
        assertTrue("An error did not occur as expected", exception);
    }

    class MySchemaOutputResolver extends SchemaOutputResolver {
        public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            File schemaFile = new File(tmpdir + suggestedFileName);
            return new StreamResult(schemaFile);
        }
    }
}
