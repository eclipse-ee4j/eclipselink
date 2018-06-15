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
// dmccann - June 4/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.binder.adapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Class that acts as a tree map.
 *
 */
public class MapEntry {
    @XmlAttribute
    public Integer key = -1;

    @XmlAttribute
    public String value = null;

    @XmlElement
    public MapEntry nextValue = null;

    /*
     * The default constructor does nothing.
     */
    public MapEntry() {}

    public MapEntry(Integer key, String value, MapEntry nextValue) {
        this.key = key;
        this.value = value;
        this.nextValue = nextValue;
    }
}
