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
import static javax.persistence.InheritanceType.JOINED;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
@Access(AccessType.FIELD) // for 316991
/**
 * Note that this Entity inherits from a Transient superclass (a non-entity, non-mappedSuperclass)
 * The Position class will be a BasicType (with no attributes outside of its Java class).
 * The Metamodel API will therefore not allow inheritance of any attributes into subclasses.
 * Any attempt to access attributes of Position via GalacticPosition will get an expected IAE.
 */
public class GalacticPosition extends Position implements java.io.Serializable {
    private static final long serialVersionUID = 1395818966377137158L;

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
    
    @Embedded
    private Observation observation;
    
    public Observation getObservation() {
        return observation;
    }

    public void setObservation(Observation observation) {
        this.observation = observation;
    }

    public GalacticPosition() {}

    public EmbeddedPK getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(EmbeddedPK primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Computer getComputerUC12() {
        return computerUC12;
    }

    public void setComputerUC12(Computer computerUC12) {
        this.computerUC12 = computerUC12;
    }

    public Computer getComputerUniUC13() {
        return computerUniUC13;
    }

    public void setComputerUniUC13(Computer computerUniUC13) {
        this.computerUniUC13 = computerUniUC13;
    }
        
    public int getVersion() { 
        return version; 
    }
    
    protected void setVersion(int version) {
        this.version = version;
    }
}
