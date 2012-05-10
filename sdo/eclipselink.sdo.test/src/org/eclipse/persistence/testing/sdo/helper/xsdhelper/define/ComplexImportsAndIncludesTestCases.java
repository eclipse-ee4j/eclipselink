/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - Feb 20 2008 Initial test case creation 
 ******************************************************************************/ 

package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.helper.SchemaResolver;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.XSDHelperTestCases;
import org.xml.sax.InputSource;

public class ComplexImportsAndIncludesTestCases extends XSDHelperTestCases {
    public ComplexImportsAndIncludesTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(ComplexImportsAndIncludesTestCases.class);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complexincludesandimports/xsdfile6.txt";
    }

    public void testDefineTest() throws Exception {
        InputStream is = new FileInputStream(getSchemaToDefine());
        StreamSource ss = new StreamSource(is);
        ss.setSystemId("includeDeptAndEmp.xsd");
        List types = ((SDOXSDHelper) xsdHelper).define(ss, new TestSchemaResolver());

        log("\nActual:\n");
        log(types);
        assertEquals(15, types.size());
    }

    public static class TestSchemaResolver implements SchemaResolver {
        private java.util.Map fileIndex = new HashMap();

        public TestSchemaResolver() throws IOException {
            FileInputStream indexFIS = new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complexincludesandimports/index.txt");

            java.util.Properties fileProps = new java.util.Properties();
            fileProps.load(indexFIS);

            java.util.Enumeration keyEnum = fileProps.keys();
            while (keyEnum.hasMoreElements()) {
                String s = (String) keyEnum.nextElement();
                fileIndex.put(fileProps.get(s), s);
            }
        }

        public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation) {
            if (schemaLocation == null) {
                return null;
            }

            try {
                URI destURI = new URI(schemaLocation);

                if (!destURI.isAbsolute()) {
                    if ((sourceXSD.getSystemId() == null) || "".equals(sourceXSD.getSystemId())) {
                        throw new IllegalArgumentException("Cannot find source URI to resolve a relative schemaLocation: " + schemaLocation);
                    } else {
                        URI sourceURI = new URI((sourceXSD.getSystemId()));
                        destURI = sourceURI.resolve(destURI);
                    }
                }

                String destSysId = destURI.toString();

                String s = (String) this.fileIndex.get(destSysId);
                if (s == null) {
                    throw new IllegalArgumentException("cannot find " + destSysId);
                }
                InputStream iStream = new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complexincludesandimports/" + s + ".txt");

                StreamSource sSrc = new StreamSource(iStream);
                sSrc.setSystemId(destSysId);
                return sSrc;

            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Satisfy EntityResolver interface implementation.
         * Allow resolution of external entities.
         * 
         * @param publicId
         * @param systemId
         * @return null
         */
        public InputSource resolveEntity(String publicId, String systemId) {
            return null;
        }
    }
}