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
// dmccann - Novemner 11/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FooWithRootElement {
    public String id;

    public FooWithRootElement() {}
    public FooWithRootElement(String id) {
        this.id = id;
    }

    public boolean equals(Object o) {
        FooWithRootElement f;
        try {
            f = (FooWithRootElement) o;
        } catch (ClassCastException e) {
            return false;
        }
        return this.id.equals(f.id);
    }
}
