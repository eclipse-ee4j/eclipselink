/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

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
