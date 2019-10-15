/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class ReferenceTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/predicate/reference.xml";

    public ReferenceTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {ReferenceRoot.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected ReferenceRoot getControlObject() {
        ReferenceRoot root = new ReferenceRoot();

        ReferenceChild rc1 = new ReferenceChild();
        rc1.setId("1");
        root.getChildren().add(rc1);

        ReferenceChild rc2 = new ReferenceChild();
        rc2.setId("2");
        rc2.setParent(rc1);
        rc1.getChildren().add(rc2);
        root.getChildren().add(rc2);

        ReferenceChild rc3 = new ReferenceChild();
        rc3.setId("3");
        rc3.setParent(rc1);
        rc1.getChildren().add(rc3);
        root.getChildren().add(rc3);

        return root;
    }

}
