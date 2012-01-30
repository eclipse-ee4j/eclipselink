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
 *  - rbarkhouse - 27 January 2012 - 2.3.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.collections;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class CollectionHolderWrappersNillableTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholderwrappersnillable.xml";
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/wrappersnillable.xsd";
    
    public CollectionHolderWrappersNillableTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = CollectionHolderWrappersNillable.class;
        setClasses(classes);
    }

    @Override
    protected Object getControlObject() {
        return new CollectionHolderWrappersNillable();
    }
    
    public void testSchemaGen() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(XSD_RESOURCE);     
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        controlSchemas.add(instream);

        testSchemaGen(controlSchemas);
    }

}