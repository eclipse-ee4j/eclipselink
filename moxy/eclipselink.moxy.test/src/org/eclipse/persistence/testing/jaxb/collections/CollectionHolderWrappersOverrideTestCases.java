/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class CollectionHolderWrappersOverrideTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholderwrappers.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholderwrappers.json";
    private final static String BINDINGS = "org/eclipse/persistence/testing/jaxb/collections/override-bindings.xml";

    public CollectionHolderWrappersOverrideTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        Class[] classes = new Class[1];
        classes[0] = CollectionHolderWrappersInitialized.class;
        setClasses(classes);
    }

    @Override
    protected Object getControlObject() {
        CollectionHolderWrappers control = new CollectionHolderWrappers();
        List TEST_LIST = new ArrayList();
        control.collection1 = TEST_LIST;
        control.collection2 = TEST_LIST;
        control.collection3 = TEST_LIST;
        control.collection4 = TEST_LIST;
        control.collection5 = TEST_LIST;
        control.collection6 = TEST_LIST;
        control.collection7 = TEST_LIST;
        return control;
    }

    @Override
    protected Map getProperties() {
    	InputStream inputStream = ClassLoader.getSystemResourceAsStream(BINDINGS);
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.collections", new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        return properties;
    }

    @Override
    public Object getReadControlObject() {
        return new CollectionHolderWrappers();
    }

}