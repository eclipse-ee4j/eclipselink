/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm.schema.model;

import java.util.ArrayList;

public class AttributeGroup {
    private java.util.List attributes;
    private AnyAttribute anyAttribute;
    private String name;
    private String ref;

    public AttributeGroup() {
        attributes = new ArrayList();
    }

    public void setAttributes(java.util.List attributes) {
        this.attributes = attributes;
    }

    public java.util.List getAttributes() {
        return attributes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAnyAttribute(AnyAttribute anyAttribute) {
        this.anyAttribute = anyAttribute;
    }

    public AnyAttribute getAnyAttribute() {
        return anyAttribute;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getRef() {
        return ref;
    }
}
