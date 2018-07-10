/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation, Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     07/09/2018-2.6 Jody Grassel
 *       - 536853: MapsID processing sets up to fail validation
 ******************************************************************************/

package org.eclipse.persistence.jpa.compositeid.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

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
