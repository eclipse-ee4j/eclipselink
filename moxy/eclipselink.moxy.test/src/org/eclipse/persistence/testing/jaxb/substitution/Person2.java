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
//     Rick Barkhouse - 2.3.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.substitution;

import javax.xml.bind.JAXBElement;

public class Person2 {

    protected JAXBElement<String> name;

    public JAXBElement<String> getName() {
        return name;
    }

    public void setName(JAXBElement<String> value) {
        this.name = ((JAXBElement<String> ) value);
    }

    public boolean equals(Object arg0) {
        if (arg0 == null) {
            return false;
        }

        if (!(arg0 instanceof Person2)) {
            return false;
        }

        Person2 aPerson = (Person2) arg0;

        if (this.name == null && aPerson.name != null) {
            return false;
        }

        if (this.name != null && aPerson.name == null) {
            return false;
        }

        return this.name.getValue().equals(aPerson.name.getValue());
    }

}
