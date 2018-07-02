/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_JIGSAW_PIECE")
public class JigsawPiece implements Serializable {

    private int id;
    private int pieceNumber;
    private Jigsaw jigsaw;

    public JigsawPiece() {
        super();
    }

    public JigsawPiece(int pieceNumber) {
        super();
        setPieceNumber(pieceNumber);
    }

    @Id
    @GeneratedValue
    @Column(name="ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name="PIECE_NUMBER")
    public int getPieceNumber() {
        return pieceNumber;
    }

    public void setPieceNumber(int pieceNumber) {
        this.pieceNumber = pieceNumber;
    }

    @ManyToOne
    @JoinColumn(name="FK_JIGSAW_ID")
    public Jigsaw getJigsaw() {
        return jigsaw;
    }

    public void setJigsaw(Jigsaw jigsaw) {
        this.jigsaw = jigsaw;
    }

}
