/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlrootelement;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class DotTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlrootelement/DotRoot.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlrootelement/DotRoot.json";

    public DotTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {DotRoot.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        return new DotRoot();
    }

}
