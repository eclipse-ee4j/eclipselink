/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa2.embedded;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TMP2_EMBEDD_FA")
public class EmbeddingFieldAccess {

    public EmbeddingFieldAccess() {
    }

    public EmbeddingFieldAccess(int anId) {
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
