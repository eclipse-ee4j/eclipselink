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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import org.eclipse.persistence.annotations.Struct;

import java.util.Arrays;

@Embeddable
@Struct(name="PLSQL_P_PLSQL_INNER_BLOB_REC", fields={"BLOB_ID", "BLOB_CONTENT"})
public class InnerObjBlob {

    public InnerObjBlob() {
    }

    public InnerObjBlob(int blobId, byte[] blobContent) {
        this.blobId = blobId;
        this.blobContent = blobContent;
    }

    @Column(name="BLOB_ID")
    private int blobId;

    @Column(name="BLOB_CONTENT")
    @Lob
    private byte[] blobContent;

    public int getBlobId() {
        return blobId;
    }

    public void setBlobId(int blobId) {
        this.blobId = blobId;
    }

    public byte[] getBlobContent() {
        return blobContent;
    }

    public void setBlobContent(byte[] blobContent) {
        this.blobContent = blobContent;
    }

    @Override
    public String toString() {
        return "InnerObjBBlob{" +
                "blobId=" + blobId +
                ", blobContent=" + Arrays.toString(blobContent) +
                '}';
    }
}
