/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// mmacivor - January 09, 2009 - 1.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.attributekey.threadsafety;

import java.io.StringReader;

import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.testing.oxm.mappings.keybased.Root;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.Employee;



public class ReferenceUnmarshaller implements Runnable {
    private XMLUnmarshaller unmarshaller;
    private String resource;
    private String controlStreet;
    public ReferenceUnmarshaller(XMLUnmarshaller unmarshaller, String resource, String controlStreet) {
        this.unmarshaller = unmarshaller;
        this.resource = resource;
        this.controlStreet = controlStreet;
    }

    public void run() {
        for(int i = 0; i < 2000; i++) {
            StringReader reader = new StringReader(resource);
            Root root = (Root)unmarshaller.unmarshal(reader);
            if(!(controlStreet.equals(((Employee)root.employee).address.street))) {
                throw new RuntimeException("Incorrect Address Resolved");
            }
        }
    }


}
