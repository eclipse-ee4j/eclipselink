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

public abstract class SimpleComponent {
    private SimpleType simpleType;//do we need a separate LocalSimpleType class?
    private String name;
    private String type;//should be a QName later
    private String defaultValue;
    private boolean isSetDefaultValue = false;
    private String fixed;
    private Map attributesMap;
    private Annotation annotation;
    private String form;
    private String ref;

    public SimpleComponent() {
        attributesMap = new HashMap();
    }

    public void setSimpleType(SimpleType simpleType) {
        this.simpleType = simpleType;
    }

    public SimpleType getSimpleType() {
        return simpleType;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        isSetDefaultValue = true;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setFixed(String fixed) {
        this.fixed = fixed;
    }

    public String getFixed() {
        return fixed;
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

    public boolean isSetDefaultValue() {
        return isSetDefaultValue;
    }


    public void setForm(String form) {
        this.form = form;
    }

    public String getForm() {
        return form;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getRef() {
        return ref;
    }
}
