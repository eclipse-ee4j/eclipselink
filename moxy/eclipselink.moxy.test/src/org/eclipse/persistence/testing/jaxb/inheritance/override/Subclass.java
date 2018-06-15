/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.inheritance.override;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Subclass extends Superclass {

    @Override
    @XmlElement(name="foo")
    public List<Foo> getCollection() {
        return super.getCollection();
    }
}
