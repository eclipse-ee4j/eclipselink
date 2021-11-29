/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial implementation
package org.eclipse.persistence.testing.models.jpa.plsql;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.eclipse.persistence.annotations.Struct;

@Embeddable
@Struct(name="PLSQL_P_PLSQL_OUTER_STRUCT_REC", fields={"STRUCT_ID", "STRUCT_CONTENT"})
public class OuterObjBlob {

    public OuterObjBlob() {
    }

    public OuterObjBlob(int structId, InnerObjBlob structContent) {
        this.structId = structId;
        this.structContent = structContent;
    }

    @Column(name="STRUCT_ID")
    private int structId;

    @Column(name="STRUCT_CONTENT")
    private InnerObjBlob structContent;

    public int getStructId() {
        return structId;
    }

    public void setStructId(int structId) {
        this.structId = structId;
    }

    public InnerObjBlob getStructContent() {
        return structContent;
    }

    public void setStructContent(InnerObjBlob structContent) {
        this.structContent = structContent;
    }

    @Override
    public String toString() {
        return "OuterObjBBlob{" +
                "structId=" + structId +
                ", structContent=" + structContent +
                '}';
    }
}