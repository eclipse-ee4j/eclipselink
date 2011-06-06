/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
 *       of the Metamodel implementation for EclipseLink 2.0 release involving
 *       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.TABLE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

@Entity(name="MemoryMetamodel")
@Table(name="CMP3_MM_MEMORY")
public class Memory implements java.io.Serializable{
    @Id
    @GeneratedValue(strategy=TABLE, generator="MEMORY_MM_TABLE_GENERATOR")
    @TableGenerator(
        name="MEMORY_MM_TABLE_GENERATOR", 
        table="CMP3_MM_MEMORY_SEQ", 
        pkColumnName="SEQ_MM_NAME", 
        valueColumnName="SEQ_MM_COUNT",
        pkColumnValue="CUST_MM_SEQ"
    )
    @Column(name="MEMORY_ID")    
    private Integer id;
    
    @Version
    @Column(name="MEMORY_VERSION")
    private int version;
    
    // The M:1 side is the owning side
    @ManyToOne(fetch=EAGER)//LAZY)
/*    @JoinTable(name="CMP3_MM_BOARD_MM_MEMORY", 
            joinColumns = @JoinColumn(name="MEMORY_ID"), 
            inverseJoinColumns =@JoinColumn(name="BOARD_ID"))*/   
    private Board board;
    
    public Memory() {}

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
    
    public int getVersion() { 
        return version; 
    }
    
    protected void setVersion(int version) {
        this.version = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    
}
