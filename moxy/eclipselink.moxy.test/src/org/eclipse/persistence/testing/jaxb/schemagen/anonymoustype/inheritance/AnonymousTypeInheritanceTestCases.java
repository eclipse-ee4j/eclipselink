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
//     Matt MacIvor - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.anonymoustype.inheritance;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases.MySchemaOutputResolver;

public class AnonymousTypeInheritanceTestCases  extends TestCase {

    public void testAnonymousComplexType() throws Exception {
        String CONTROL_XSD = "org/eclipse/persistence/testing/jaxb/schemagen/anonymoustype/inheritance/foo.xsd";

        JAXBContext cxt = JAXBContextFactory.createContext(new Class[]{ObjectFactory.class}, null);

        MySchemaOutputResolver mysr = new MySchemaOutputResolver();
        cxt.generateSchema(mysr);

        assertTrue("Expected 1 schema to be generated, but there were [" + mysr.schemaFiles.size() + "]", mysr.schemaFiles.size() == 1);
        ExternalizedMetadataTestCases.compareSchemas(mysr.schemaFiles.get(""), new File(Thread.currentThread().getContextClassLoader().getResource(CONTROL_XSD).getPath()));
    }
}
