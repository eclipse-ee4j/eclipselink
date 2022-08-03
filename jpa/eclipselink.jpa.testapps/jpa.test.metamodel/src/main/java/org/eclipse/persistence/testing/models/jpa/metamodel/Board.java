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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.TABLE;

@Entity(name="BoardMetamodel")
@Table(name="CMP3_MM_BOARD")
public class Board implements java.io.Serializable{

    private static final long serialVersionUID = 791539954634456200L;

    @Id
    @GeneratedValue(strategy=TABLE, generator="BOARD_MM_TABLE_GENERATOR")
    @TableGenerator(
        name="BOARD_MM_TABLE_GENERATOR",
        table="CMP3_MM_BOARD_SEQ",
        pkColumnName="SEQ_MM_NAME",
        valueColumnName="SEQ_MM_COUNT",
        pkColumnValue="CUST_MM_SEQ"
    )
    @Column(name="BOARD_ID")
    private Integer id;

    @Version
    @Column(name="BOARD_VERSION")
    private int version;

    // If a JoinTable with a JoinColumn is used - then we need a mappedBy on the inverse side here
    @OneToMany(cascade=ALL, mappedBy="board", fetch=EAGER)
    private ArrayList<Processor> processors = new ArrayList<>();

    // If a JoinTable with a JoinColumn is used - then we need a mappedBy on the inverse side here
    @OneToMany(cascade=ALL, mappedBy="board")
    private Collection<Memory> memories = new LinkedHashSet<>();

    // The M:1 side is the owning side for "circuitBoards"
    @ManyToOne(fetch=EAGER)
    @JoinTable(name="CMP3_MM_COMPUTER_MM_BOARD",
            joinColumns = @JoinColumn(name="BOARD_ID"),
            inverseJoinColumns = @JoinColumn(name="COMPUTER_ID"))
    private Computer computer;

    public Board() {}

    public int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }

    public Collection<Processor> getProcessors() {
        return processors;
    }

    public void setProcessors(ArrayList<Processor> processors) {
        this.processors = processors;
    }

    public Collection<Memory> getMemories() {
        return memories;
    }

    public void setMemories(Collection<Memory> memories) {
        this.memories = memories;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Computer getComputer() {
        return computer;
    }

    public void setComputer(Computer computer) {
        this.computer = computer;
    }
}
