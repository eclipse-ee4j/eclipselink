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
//     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
//       of the Metamodel implementation for EclipseLink 2.0 release involving
//       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
//       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
package org.eclipse.persistence.testing.models.jpa.metamodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Version;

import java.io.Serializable;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.TABLE;
import static jakarta.persistence.InheritanceType.JOINED;

// OVERRIDE @Inheritance from SINGLE_TABLE to JOINED
// http://wiki.eclipse.org/Introduction_to_EclipseLink_JPA_%28ELUG%29#.40Inheritance

@Entity(name="ProcessorMetamodel")
@Table(name="CMP3_MM_PROC")
@Inheritance(strategy=JOINED)
public class Processor implements Serializable {
    @Id
    @GeneratedValue(strategy=TABLE, generator="PROC_MM_TABLE_GENERATOR")
    @TableGenerator(
        name="PROC_MM_TABLE_GENERATOR",
        table="CMP3_MM_PROC_SEQ",
        pkColumnName="SEQ_MM_NAME",
        valueColumnName="SEQ_MM_COUNT",
        pkColumnValue="CUST_MM_SEQ"
    )
    @Column(name="PROC_ID")
    private Integer id;

  @Version
  @Column(name="PROC_VERSION")
  private int version;

    // The M:1 side is the owning side
    @ManyToOne(fetch=EAGER)//LAZY)
/*    @JoinTable(name="CMP3_MM_BOARD_MM_PROC",
            joinColumns = @JoinColumn(name="PROC_ID"),
            inverseJoinColumns =@JoinColumn(name="BOARD_ID"))*/
    private Board board;

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }
}
