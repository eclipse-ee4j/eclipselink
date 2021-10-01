/*******************************************************************************
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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

// Contributors:
//     Oracle - initial implementation
package org.eclipse.persistence.testing.models.jpa.plsql;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import org.eclipse.persistence.annotations.Struct;

import java.util.Arrays;

@Embeddable
@Struct(name="PLSQL_P_PLSQL_INNER_BLOB_REC", fields={"BLOB_ID", "BLOB_CONTENT", "CLOB_CONTENT"})
public class InnerObjBlob {

    public InnerObjBlob() {
    }

    public InnerObjBlob(int blobId, byte[] blobContent, String clobContent) {
        this.blobId = blobId;
        this.blobContent = blobContent;
        this.clobContent = clobContent;
    }

    @Column(name="BLOB_ID")
    private int blobId;

    @Column(name="BLOB_CONTENT")
    @Lob
    private byte[] blobContent;

    @Column(name="CLOB_CONTENT")
    @Lob
    private String clobContent;

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

    public String getClobContent() {
        return clobContent;
    }

    public void setClobContent(String clobContent) {
        this.clobContent = clobContent;
    }

    @Override
    public String toString() {
        return "InnerObjBlob{" +
                "blobId=" + blobId +
                ", blobContent=" + Arrays.toString(blobContent) +
                ", clobContent='" + clobContent + '\'' +
                '}';
    }
}
