/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlvalue;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XsiTypeTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlvalue/XsiType.xml";

    public XsiTypeTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {XsiTypeRoot.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected XsiTypeRoot getControlObject() {
        XsiTypeRoot root = new XsiTypeRoot();
        root.setValue(123);
        return root;
    }

}