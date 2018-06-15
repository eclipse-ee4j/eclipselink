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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;

import org.eclipse.persistence.oxm.XMLDescriptor;

public class MWProperty extends MWModel {

    private volatile String key;
        public static final String KEY_PROPERTY = "key";

    private volatile String value;
        public static final String VALUE_PROPERTY = "value";


    /**
     * Default constructor - for TopLink use only.
     */
    private MWProperty() {
        super();
    }

    public MWProperty(MWModel parent) {
        super(parent);
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        Object old = this.key;
        this.key = key;
        this.firePropertyChanged(KEY_PROPERTY, old, key);
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        Object old = this.value;
        this.value = value;
        this.firePropertyChanged(VALUE_PROPERTY, old, value);
    }

    public void toString(StringBuffer sb) {
        sb.append(this.key);
        sb.append("=");
        sb.append(this.value);
    }

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MWProperty.class);

        descriptor.addDirectMapping("key", "key/text()");
        descriptor.addDirectMapping("value", "value/text()");

        return descriptor;
    }

}
