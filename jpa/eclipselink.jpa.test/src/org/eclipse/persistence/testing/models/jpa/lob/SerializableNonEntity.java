/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.lob;

import java.io.Serializable;

/**
 * This class is used to test serialization to persistent fields in other entities,
 * such as mapping attributes of this type to String or blob field types.
 */
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
