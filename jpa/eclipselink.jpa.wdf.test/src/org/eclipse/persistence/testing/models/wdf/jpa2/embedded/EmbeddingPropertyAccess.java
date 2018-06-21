/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa2.embedded;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TMP2_EMBEDD_PA")
public class EmbeddingPropertyAccess {

    public EmbeddingPropertyAccess() {
    }

    public EmbeddingPropertyAccess(int anId) {
        id = anId;
    }

    @Id
    private int id;

    @Embedded
    @AttributeOverride(name = "data", column = @Column(name = "EMB_FA_DATA"))
    private EmbeddedFieldAccess fieldAccess;

    @Embedded
    @AttributeOverride(name = "data", column = @Column(name = "EMB_PA_DATA"))
    private EmbeddedPropertyAccess propertyAccess;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EmbeddedFieldAccess getFieldAccess() {
        return fieldAccess;
    }

    public void setFieldAccess(EmbeddedFieldAccess fieldAccess) {
        this.fieldAccess = fieldAccess;
    }

    public EmbeddedPropertyAccess getPropertyAccess() {
        return propertyAccess;
    }

    public void setPropertyAccess(EmbeddedPropertyAccess propertyAccess) {
        this.propertyAccess = propertyAccess;
    }
}
