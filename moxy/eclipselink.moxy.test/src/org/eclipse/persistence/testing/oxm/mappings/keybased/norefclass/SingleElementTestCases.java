/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     bdoughan - April 14/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.keybased.norefclass;

import org.eclipse.persistence.testing.oxm.xmlmarshaller.setschemas.SetXmlSchemaTestCases;

public class SingleElementTestCases extends AbstractTestCases {

    private static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/keybased/norefclass/singleelement.xml";

    public SingleElementTestCases(String name) throws Exception {
        super(name);
        setProject(new SingleElementProject());
        setControlDocument(XML_RESOURCE);
    }

}
