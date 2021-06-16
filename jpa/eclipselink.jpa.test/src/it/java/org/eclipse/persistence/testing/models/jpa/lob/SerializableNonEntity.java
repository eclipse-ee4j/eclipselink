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
package org.eclipse.persistence.testing.models.jpa.lob;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlRootElement;

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
