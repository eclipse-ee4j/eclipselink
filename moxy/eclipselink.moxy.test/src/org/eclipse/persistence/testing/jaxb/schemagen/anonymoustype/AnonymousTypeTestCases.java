/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - Sept.22/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.anonymoustype;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases.MySchemaOutputResolver;

public class AnonymousTypeTestCases extends TestCase {

    public void testAnonymousComplexType() throws Exception {
        String TNS = "http://xmlns.oracle.com/Test";
        String TYPES_TNS = "http://xmlns.oracle.com/Test/types";
        String TNS_XSD = "org/eclipse/persistence/testing/jaxb/schemagen/anonymoustype/test_ns.xsd";
        String TYPES_XSD = "org/eclipse/persistence/testing/jaxb/schemagen/anonymoustype/target_ns.xsd";

        TypeMappingInfo t1 = new TypeMappingInfo();
        t1.setAnnotations(new Annotation[0]);
        t1.setType(Process.class);
        t1.setElementScope(TypeMappingInfo.ElementScope.Global);
        t1.setXmlTagName(new QName(TNS, "process"));

        TypeMappingInfo[] types = { t1 };
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.DEFAULT_TARGET_NAMESPACE_KEY, TYPES_TNS);
        JAXBContext cxt = JAXBContextFactory.createContext(types, properties, Thread.currentThread().getContextClassLoader());

        MySchemaOutputResolver mysr = new MySchemaOutputResolver();
        cxt.generateSchema(mysr);

        assertTrue("Expected two schemas to be generated, but there were [" + mysr.schemaFiles.size() + "]", mysr.schemaFiles.size() == 2);
        ExternalizedMetadataTestCases.compareSchemas(mysr.schemaFiles.get(TNS), getFile(TNS_XSD));
        ExternalizedMetadataTestCases.compareSchemas(mysr.schemaFiles.get(TYPES_TNS), getFile(TYPES_XSD));
    }

    private File getFile(String resourceName) {
        return new File(Thread.currentThread().getContextClassLoader().getResource(resourceName).getPath());
    }
}
