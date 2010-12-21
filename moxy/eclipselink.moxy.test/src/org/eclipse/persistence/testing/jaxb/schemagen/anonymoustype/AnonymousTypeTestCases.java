/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - Sept.22/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.schemagen.anonymoustype;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

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
        ExternalizedMetadataTestCases.compareSchemas(mysr.schemaFiles.get(TNS), new File(TNS_XSD));
        ExternalizedMetadataTestCases.compareSchemas(mysr.schemaFiles.get(TYPES_TNS), new File(TYPES_XSD));
    }
}