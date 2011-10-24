/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 08 September 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmllocation;

import java.net.URL;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.xml.sax.Locator;

public class XmlLocationNonTransientTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmllocation/data-nontransient.xml";

    public XmlLocationNonTransientTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{ DataNT.class, SubDataNT.class, DetailDataNT.class, LeafDataNT.class });
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        boolean includeSysId = false;
        if (this.getName().endsWith("URL")) {
            includeSysId = true;
        }

        DataNT d = new DataNT();
        d.key = "dat325";
        d.data1 = "sdjfhdsaoiufhosaidufh";
        d.data2 = "kjdfgkjdsfg8374934874";
        d.data3 = "84376328476324XXXXXXX";
        d.locator = new TestLocator(2, 61, includeSysId);

        SubDataNT sd1 = new DetailDataNT(); sd1.info = "name|rbarkhouse"; sd1.locator = new TestLocator(7, 37, includeSysId); d.subData.add(sd1);
        SubDataNT sd2 = new SubDataNT(); sd2.info = "phone|6132832684";  sd2.locator = new TestLocator(14, 13, includeSysId); d.subData.add(sd2);
        SubDataNT sd3 = new LeafDataNT(); sd3.info = "id|8827"; sd3.locator = new TestLocator(21, 35, includeSysId); d.subData.add(sd3);

        if (this.getName().endsWith("Node") || this.getName().endsWith("UnmarshallerHandler")) {
            TestLocator noLoc = new TestLocator(0, 0, false);

            d.locator = noLoc;
            sd1.locator = noLoc;
            sd2.locator = noLoc;
            sd3.locator = noLoc;
        }

        return d;
    }

    @Override
    public void testXMLToObjectFromInputStream() throws Exception {
    	super.testXMLToObjectFromInputStream();
    }
    
    private class TestLocator implements Locator {
        private boolean includeSysId = false;
        private String controlSysId = null;

        int line, column;

        public TestLocator(int l, int c, boolean sysId) {
            this.includeSysId = sysId;

            URL url = ClassLoader.getSystemClassLoader().getResource(XML_RESOURCE);
            this.controlSysId = url.toExternalForm();

            this.line = l;
            this.column = c;
        }

        public String getPublicId() {
            return null;
        }

        public String getSystemId() {
            if (!includeSysId) {
                return null;
            } else {
                return this.controlSysId;
            }
        }

        public int getLineNumber() {
            return line;
        }

        public int getColumnNumber() {
            return column;
        }
    }

}
