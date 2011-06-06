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
import static javax.persistence.InheritanceType.JOINED;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

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
