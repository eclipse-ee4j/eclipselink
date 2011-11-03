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

import java.io.IOException;
import java.net.URL;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.xml.sax.Locator;

public class XmlLocationTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmllocation/data.xml";

    public XmlLocationTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{ Data.class, SubData.class, DetailData.class, LeafData.class });
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        boolean includeSysId = false;
        if (this.getName().endsWith("URL")) {
            includeSysId = true;
        }

        Data d = new Data();
        d.key = "dat325";
        d.data1 = "sdjfhdsaoiufhosaidufh";
        d.data2 = "kjdfgkjdsfg8374934874";
        d.data3 = "84376328476324XXXXXXX";
        d.locator = new TestLocator(15, 89, 29, includeSysId);

        SubData sd1 = new DetailData(); sd1.info = "name|rbarkhouse"; sd1.setLoc(new TestLocator(20, 35, 4, includeSysId)); d.subData.add(sd1);
        SubData sd2 = new SubData(); sd2.info = "phone|6132832684";  sd2.setLoc(new TestLocator(30, 26, 17, includeSysId)); d.subData.add(sd2);
        SubData sd3 = new LeafData(); sd3.info = "id|8827"; sd3.setLoc(new TestLocator(32, 33, 4, includeSysId)); d.subData.add(sd3);

        if (this.getName().endsWith("Node") || this.getName().endsWith("UnmarshallerHandler")) {
            TestLocator noLoc = new TestLocator(0, 0, 0, false);

            d.locator = noLoc;
            sd1.setLoc(noLoc);
            sd2.setLoc(noLoc);
            sd3.setLoc(noLoc);
        }

        return d;
    }

    private class TestLocator implements Locator {
        private boolean includeSysId = false;
        private String controlSysId = null;

        /**
         * Different parsers take different approaches when returning the 
         * XML Location of a given element.  The default XML parser
         * considers the end of the opening tag as the beginning of the element
         * (eg L15 C89), whereas Woodstox and XDK use the beginning of the opening
         * tag (eg. L15 C29).
         */
        int line, column, alternateColumn;

        public TestLocator(int l, int c, int alt, boolean sysId) {
            this.includeSysId = sysId;

            URL url = ClassLoader.getSystemClassLoader().getResource(XML_RESOURCE);
            this.controlSysId = url.toExternalForm();

            this.line = l;
            this.column = c;
            this.alternateColumn = alt;
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

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj instanceof Locator) {
                Locator aLocator = (Locator) obj;
                if (includeSysId) {
                    if (!(this.getSystemId().equals(aLocator.getSystemId()))) return false;
                }
                if (this.line != aLocator.getLineNumber()) return false;
                if ((this.column != aLocator.getColumnNumber()) && (this.alternateColumn != aLocator.getColumnNumber())) return false;

                return true;
            }
            return false;
        }
    }

}
