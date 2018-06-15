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
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlmarshaller.locator;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class AnyCollectionTestCases extends LocatorTestCase {

    public AnyCollectionTestCases(String name) throws Exception {
        super(name);
    }

    @Override
    public Class[] getClasses() {
        Class[] classes = {AnyCollectionRoot.class, Child.class};
        return classes;
    }

    public AnyCollectionRoot setupRootObject() {
        AnyCollectionRoot control = new AnyCollectionRoot();
        control.setName("123456789");
        control.getChild().add(child);
        return control;
    }

}
