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
