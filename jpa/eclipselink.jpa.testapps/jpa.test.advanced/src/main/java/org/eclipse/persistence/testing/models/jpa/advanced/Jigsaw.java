/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Table(name="CMP3_JIGSAW")
public class Jigsaw implements Serializable {

    private int id;
    private List<JigsawPiece> pieces;

    public Jigsaw() {
        super();
        this.pieces = new ArrayList<>();
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

    @OneToMany(cascade={ALL}, mappedBy="jigsaw")
    @JoinColumn(name="FK_JIGSAW_ID")
    @OrderColumn(name="PIECE_NUMBER")
    public List<JigsawPiece> getPieces() {
        return pieces;
    }

    public void setPieces(List<JigsawPiece> pieces) {
        this.pieces = pieces;
    }

    public void addPiece(JigsawPiece piece) {
        getPieces().add(piece);
        piece.setJigsaw(this);
    }

    public void removePiece(JigsawPiece piece) {
        getPieces().remove(piece);
        piece.setJigsaw(null);
    }

}
