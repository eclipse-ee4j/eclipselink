/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import static javax.persistence.InheritanceType.JOINED;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

//OVERRIDE @Inheritance from SINGLE_TABLE to JOINED
//http://wiki.eclipse.org/Introduction_to_EclipseLink_JPA_%28ELUG%29#.40Inheritance

@Inheritance(strategy=JOINED)
@Entity(name="GalacticMetamodel")
@Table(name="CMP3_MM_GALACTIC")
public class GalacticPosition extends Position implements java.io.Serializable {
/*    @Id
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
*/    
    // Any reference to this embedded key requires a bidirectional relationship (not unidirectional)
    @EmbeddedId
    @Column(name="GALACTIC_ID")    
    protected EmbeddedPK primaryKey;
    
    @Version
    @Column(name="GALACTIC_VERSION")
    private int version;
    
    // The M:1 side is the owning side for "positionUC12"
    @ManyToOne(fetch=EAGER)
/*    @JoinTable(name="CMP3_MM_COMPUTER_MM_GALACTIC", 
            joinColumns = @JoinColumn(name="GALACTIC_ID"), 
            inverseJoinColumns = @JoinColumn(name="COMPUTER_ID"))*/   
    private Computer computerUC12;

    // Unidirectional ManyToOne becomes a ManyToMany (no mappedBy on the inverse side)
    @ManyToOne(fetch=EAGER)
    private Computer computerUniUC13;
    
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
    
    
    //@OneToMany(cascade=ALL)//, mappedBy="position")
    // A Collection where the Collection type (Map, Set, List) is not defined at design time
    // see design issue #58
    // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_58:_20090807:_ManagedType_Attribute_Initialization_must_differentiate_between_Collection_and_List
    //private Collection<Observation> observations;
    
    public GalacticPosition() {}

    public EmbeddedPK getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(EmbeddedPK primaryKey) {
        this.primaryKey = primaryKey;
    }
    
    public int getVersion() { 
        return version; 
    }
    
    protected void setVersion(int version) {
        this.version = version;
    }

/*    public Integer getId() {
        return primaryKey;
    }

    public void setId(Integer id) {
        this.primaryKey = id;
    }*/
    
}
