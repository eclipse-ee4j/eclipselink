/********************************************************************************
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
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
public class CompB {
    @EmbeddedId
    @AttributeOverrides({ 
        @AttributeOverride(name=AttributeOverridePaths.COMPA_ID_IDENTIFIER_VALUE, column=@Column(name=ColumnNames.FK_IDENTIFIE)),
        @AttributeOverride(name=AttributeOverridePaths.COMPA_ID_ENVIRONMENT_VALUE, column=@Column(name=ColumnNames.FK_ENVIRONME)),
        @AttributeOverride(name=AttributeOverridePaths.CLIENT_ID_VALUE, column=@Column(name=ColumnNames.CLIENT)),
        @AttributeOverride(name=AttributeOverridePaths.RN_VALUE, column=@Column(name=ColumnNames.RN))
    })
    CompBId id;

    @MapsId("compAId")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumns({ 
        @JoinColumn(name=ColumnNames.FK_IDENTIFIE, referencedColumnName=ColumnNames.IDENTIFIER),
        @JoinColumn(name=ColumnNames.FK_ENVIRONME, referencedColumnName=ColumnNames.ENVIRONMENT)
    })
    private CompA compA;

    public CompBId getId() {
        return id;
    }

    public void setId(CompBId id) {
        this.id = id;
    }

    public CompA getCompA() {
        return compA;
    }

    public void setCompA(CompA role) {
        this.compA = role;
    }


}
