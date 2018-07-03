/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm.schema.model;

import java.util.HashMap;
import java.util.Map;

public class SimpleType implements Restrictable {
    private String name;
    private List list;
    private Union union;
    private Restriction restriction;
    private Map attributesMap;
    private Annotation annotation;

    public SimpleType() {
        attributesMap = new HashMap();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setRestriction(Restriction restriction) {
        if (restriction == null)
            return;

        restriction.setOwner(this);
        if (this.restriction == null) {
            this.restriction = restriction;
        } else {
            this.restriction.mergeWith(restriction);
        }

    }

    public Restriction getRestriction() {
        return restriction;
    }

    public List getList() {
        return list;
    }

    public void setList(List l) {
        this.list = l;
    }

    public String getOwnerName() {
        if (getName() != null) {
            return getName();
        }

        //need to get owner name...owner could be element, schema, attribute or restriction
        return null;
    }

    public void setUnion(Union union) {
        this.union = union;
    }

    public Union getUnion() {
        return union;
    }

    public void setAttributesMap(Map attributesMap) {
        this.attributesMap = attributesMap;
    }

    public Map getAttributesMap() {
        return attributesMap;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public Annotation getAnnotation() {
        return annotation;
    }
}
