/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Denise Smith - October 2013
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.load;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.eclipse.persistence.Version;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.helper.SchemaResolver;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;
import commonj.sdo.impl.HelperProvider;

public class LoadSchemasWithImportCloseStream extends TestCase {
    static String PATH = "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/load/";
    SDOXSDHelper xsdHelper = (SDOXSDHelper) XSDHelper.INSTANCE;

    public void testLoadSchemasWithImportAndInheritance() throws Exception{
        loadXSD(PATH + "SchemaB.xsd");
    }

    public void loadXSD(String xsdFileName) throws Exception{
        FileInputStream fInstream = null;
        try {
            fInstream = new FileInputStream(xsdFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }
        System.out.println(Version.getVersionString());
        MySchemaResolver sr = new MySchemaResolver();
        xsdHelper.define(new StreamSource(fInstream), sr);
        List<MyFileInputStream> theStreams = sr.getStreams();
        for(int i=0; i< theStreams.size(); i++){
            assertTrue(theStreams.get(i).isClosed());
        }
    }

    private class MySchemaResolver implements SchemaResolver {
        private  List<MyFileInputStream> streams = new ArrayList<MyFileInputStream>();

        public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation) {
            try {
                InputStream fInstream = this.getClass().getClassLoader().getResourceAsStream("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/load/" + schemaLocation);
                MyFileInputStream myStream = new MyFileInputStream(fInstream);
                streams.add(myStream);
                return new StreamSource(myStream, "systemID");
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }

            return null;
        }

        public List<MyFileInputStream> getStreams(){
            return streams;
        }

        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return null;
        }
    }
    public class MyFileInputStream extends BufferedInputStream{

        private boolean isClosed = false;

        public MyFileInputStream(InputStream is) throws FileNotFoundException {
            super(is);
        }

        public void close() throws IOException{
            super.close();
            isClosed = true;
        }

        public boolean isClosed(){
            return isClosed;
        }

    }
}
