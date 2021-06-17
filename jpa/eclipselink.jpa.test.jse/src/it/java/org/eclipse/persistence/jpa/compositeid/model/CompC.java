/********************************************************************************
 * Copyright (c) 2018, 2021 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 ******************************************************************************/

// Contributors:
//     07/09/2018-2.6 Jody Grassel
//        - 536853: MapsID processing sets up to fail validation

package org.eclipse.persistence.jpa.compositeid.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class CompC {
    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name=AttributeOverridePaths.COMPB_ID_RN_VALUE, column=@Column(name=ColumnNames.FK_RELATIVE)),
        @AttributeOverride(name=AttributeOverridePaths.COMPB_ID_ROLE_ID_IDENTIFIER_VALUE, column=@Column(name=ColumnNames.FK_IDENTIFIE)),
        @AttributeOverride(name=AttributeOverridePaths.COMPB_ID_ROLE_ID_ENVIRONMENT_VALUE, column=@Column(name=ColumnNames.FK_ENVIRONME)),
        @AttributeOverride(name=AttributeOverridePaths.COMPB_ID_CLIENT_ID_VALUE, column=@Column(name=ColumnNames.FK_CLIENT)),
        @AttributeOverride(name=AttributeOverridePaths.USER_ID_VALUE, column=@Column(name=ColumnNames.FK_USER_ID))
    })
    private CompCId id;

    @ManyToOne(fetch=FetchType.LAZY)
    @MapsId("compBId")
    @JoinColumns({
        @JoinColumn(name=ColumnNames.FK_ENVIRONME, referencedColumnName=ColumnNames.FK_ENVIRONME),
        @JoinColumn(name=ColumnNames.FK_IDENTIFIE, referencedColumnName=ColumnNames.FK_IDENTIFIE),
        @JoinColumn(name=ColumnNames.FK_CLIENT, referencedColumnName=ColumnNames.CLIENT),
        @JoinColumn(name=ColumnNames.FK_RELATIVE, referencedColumnName=ColumnNames.RN)
    })	
    private CompB compB;

    public CompCId getId() {
        return id;
    }

    public void setId(CompCId id) {
        this.id = id;
    }

    public CompB getCompB() {
        return compB;
    }

    public void setCompB(CompB compB) {
        this.compB = compB;
    }


}
