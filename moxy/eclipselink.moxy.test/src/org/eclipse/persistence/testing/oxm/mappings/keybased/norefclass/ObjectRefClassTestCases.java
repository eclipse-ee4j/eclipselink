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
//     bdoughan - April 9/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.keybased.norefclass;

public class ObjectRefClassTestCases extends AbstractTestCases {

    private static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/keybased/norefclass/norefclass.xml";

    public ObjectRefClassTestCases(String name) throws Exception {
        super(name);
        setProject(new ObjectRefClassProject());
        setControlDocument(XML_RESOURCE);
    }

}
