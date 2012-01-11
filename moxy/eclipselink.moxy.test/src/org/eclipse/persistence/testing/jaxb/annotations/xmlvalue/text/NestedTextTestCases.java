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
package org.eclipse.persistence.testing.jaxb.annotations.xmlvalue.text;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class NestedTextTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlvalue/nestedText.xml";

    public NestedTextTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Top.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Top getControlObject() {
        Bottom bottom = new Bottom();
        bottom.bottomAttr = "C";
        bottom.value = "ABC";

        Middle2 middle2 = new Middle2();
        middle2.middle2Attr = "B";
        middle2.bottom = bottom;
        bottom.middle2 = middle2;

        Middle1 middle1 = new Middle1();
        middle1.middle1Attr = "A";
        middle1.middle2 = middle2;
        middle2.middle1 = middle1;

        Top top = new Top();
        top.middle1 = middle1;
        middle1.top = top;
        return top;
    }

}