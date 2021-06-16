/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.advanced;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

@Entity
@Table(name="CMP3_JIGSAW")
public class Jigsaw implements Serializable {

    private int id;
    private List<JigsawPiece> pieces;

    public Jigsaw() {
        super();
        this.pieces = new ArrayList<JigsawPiece>();
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
