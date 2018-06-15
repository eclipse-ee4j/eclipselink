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
package org.eclipse.persistence.testing.models.jpa.lob;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is used to test serialization to persistent fields in other entities,
 * such as mapping attributes of this type to String or blob field types.
 */
@XmlRootElement
public class SerializableNonEntity implements Serializable {
    Long someValue;

    public SerializableNonEntity() {
    }

    public SerializableNonEntity(Long value) {
        this.someValue = value;
    }

    public void setSomeValue(Long someValue) {
        this.someValue = someValue;
    }

    public Long getSomeValue() {
        return someValue;
    }
}
