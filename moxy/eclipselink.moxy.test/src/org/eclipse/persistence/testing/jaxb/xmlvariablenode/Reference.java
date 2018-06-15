/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Reference {

    public String name;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Reference)) {
            return false;
        }
        Reference r = (Reference)obj;
        if (name != null)
            return name.equals(r.name);
        else
            return r.name == null;
    }



}
