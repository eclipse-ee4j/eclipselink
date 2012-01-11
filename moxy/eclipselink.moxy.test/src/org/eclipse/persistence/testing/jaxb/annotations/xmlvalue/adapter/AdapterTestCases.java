/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlvalue.adapter;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class AdapterTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlvalue/adapter.xml";

    public AdapterTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Top.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        Top root = new Top();

        Bottom attributeBottom = new Bottom("attribute");
        Middle attributeMiddle = new Middle(attributeBottom);
        root.attribute = attributeMiddle;

        Bottom textBottom = new Bottom("text");
        Middle textMiddle = new Middle(textBottom);
        root.text = textMiddle;

        return root;
    }

}
