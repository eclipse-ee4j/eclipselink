/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.package2;

public class Unmappable {
    private String prop;

    private Unmappable(String prop) {
        this.prop = prop;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public static Unmappable getInstance(String value) {
        return new Unmappable(value);
    }

    public boolean equals(Object obj) {
        return (obj instanceof Unmappable) && ((Unmappable)obj).getProp().equals(this.getProp());
    }
}
