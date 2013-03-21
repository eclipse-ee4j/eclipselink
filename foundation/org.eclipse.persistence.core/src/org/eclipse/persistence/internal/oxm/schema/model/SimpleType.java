/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
        this.restriction = restriction;
        if (restriction != null) {
            restriction.setOwner(this);
        }

        //set Owner
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
