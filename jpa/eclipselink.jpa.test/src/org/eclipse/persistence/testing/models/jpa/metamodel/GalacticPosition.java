/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

//OVERRIDE @Inheritance from SINGLE_TABLE to JOINED
//http://wiki.eclipse.org/Introduction_to_EclipseLink_JPA_%28ELUG%29#.40Inheritance

@Inheritance(strategy=JOINED)
@Entity(name="GalacticMetamodel")
@Table(name="CMP3_MM_GALACTIC")
public class GalacticPosition implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy=TABLE, generator="GALACTIC_MM_TABLE_GENERATOR")
    @TableGenerator(
        name="GALACTIC_MM_TABLE_GENERATOR", 
        table="CMP3_MM_GALACTIC_SEQ", 
        pkColumnName="SEQ_MM_NAME", 
        valueColumnName="SEQ_MM_COUNT",
        pkColumnValue="CUST_MM_SEQ"
    )
    @Column(name="GALACTIC_ID")    
    private Integer primaryKey;
    
    // Any reference to this embedded key requires a bidirectional relationship (not unidirectional)
/*    @EmbeddedId
    @Column(name="GALACTIC_ID")    
    protected EmbeddedPK primaryKey;*/
    
    @Version
    @Column(name="GALACTIC_VERSION")
    private int version;
    
    // Unidirectional OneToOne
    // There is no get/set method on purpose - for testing
//    @OneToOne(fetch=EAGER)
//    @JoinColumn(name="GALACTIC_ID", referencedColumnName="GALACTIC_ID")    
    
//    @OneToOne
//    //@JoinColumn(name="FUTURE_POS_GALACTIC_ID")
//    @JoinTable(name="CLIENT_ACCT",
//        joinColumns=@JoinColumn(name="C_ID", referencedColumnName="CLIENTID"),
//        inverseJoinColumns=@JoinColumn(name="A_ID", referencedColumnName="ACCTNUM")    
//    public GalacticPosition futurePosition;
    
    public GalacticPosition() {}

/*    public EmbeddedPK getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(EmbeddedPK primaryKey) {
        this.primaryKey = primaryKey;
    }*/
    
    public int getVersion() { 
        return version; 
    }
    
    protected void setVersion(int version) {
        this.version = version;
    }

    public Integer getId() {
        return primaryKey;
    }

    public void setId(Integer id) {
        this.primaryKey = id;
    }
    
}
