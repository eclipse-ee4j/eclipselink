/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Denise Smith - May 2013
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import java.util.ArrayList;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlVariableNodeXmlValueCollectionAttrTestCases extends JAXBWithJSONTestCases{
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootXmlValueCollectionAttr.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootXmlValueCollectionAttr.json";

    public XmlVariableNodeXmlValueCollectionAttrTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class<?>[]{RootXmlValueCollectionAttr.class});
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
    }

    @Override
    protected Object getControlObject() {
        RootXmlValueCollectionAttr r = new RootXmlValueCollectionAttr();
        r.name = "theRootName";

        ThingXmlValue thing = new ThingXmlValue();
        thing.thingName = "thinga";
        thing.thingValue = "thingavalue";

        ThingXmlValue thingb = new ThingXmlValue();
        thingb.thingName = "thingb";
        thingb.thingValue = "thingbvalue";

        ThingXmlValue thingc = new ThingXmlValue();
        thingc.thingName = "thingc";
        thingc.thingValue = "thingcvalue";

        r.things= new ArrayList<ThingXmlValue>();
        r.things.add(thing);
        r.things.add(thingb);
        r.things.add(thingc);
        return r;
    }



}
