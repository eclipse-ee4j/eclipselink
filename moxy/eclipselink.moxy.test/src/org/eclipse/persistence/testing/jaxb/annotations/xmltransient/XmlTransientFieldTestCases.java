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
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmltransient;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlTransientFieldTestCases extends JAXBWithJSONTestCases{

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmltransient/xmltransient.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmltransient/xmltransient.json";

    public XmlTransientFieldTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {FieldRoot.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected FieldRoot getControlObject() {
        FieldRoot root = new FieldRoot();
        root.setA("A");
        root.setB("B");
        return root;
    }

}