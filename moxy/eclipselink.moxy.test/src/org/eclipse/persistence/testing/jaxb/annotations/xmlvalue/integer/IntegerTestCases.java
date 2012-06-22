/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.annotations.xmlvalue.integer;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class IntegerTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlvalue/integer.xml";

    public IntegerTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Top.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        Top root = new Top();

        Bottom attributeBottom = new Bottom();
        attributeBottom.value = 1;
        Middle attributeMiddle = new Middle();
        attributeMiddle.value = attributeBottom;
        root.attribute = attributeMiddle;

        Bottom textBottom = new Bottom();
        textBottom.value = 2;
        Middle textMiddle = new Middle();
        textMiddle.value = textBottom;
        root.text = textMiddle;

        return root;
    }

}
