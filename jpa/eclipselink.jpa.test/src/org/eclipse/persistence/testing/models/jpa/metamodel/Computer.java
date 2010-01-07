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

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.TABLE;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

// Name attribute is requires so we do not have a collision with jpa.inheritance.Computer
@Entity(name="ComputerMetamodel")
@Table(name="CMP3_MM_COMPUTER")
public class Computer implements java.io.Serializable {
    
    private static final long serialVersionUID = -8396759932330865145L;

    @Id
    @GeneratedValue(strategy=TABLE, generator="COMPUTER_MM_TABLE_GENERATOR")
    @TableGenerator(
        name="COMPUTER_MM_TABLE_GENERATOR", 
        table="CMP3_MM_COMPUTER_SEQ", 
        pkColumnName="SEQ_MM_NAME", 
        valueColumnName="SEQ_MM_COUNT",
        pkColumnValue="CUST_MM_SEQ"
    )
    @Column(name="COMPUTER_ID")    
    private Integer id;
    
    @Version
    @Column(name="COMPUTER_VERSION")
    private int version;

    @OneToOne(fetch=EAGER)
    @JoinColumn(name="GALACTIC_GALACTIC_ID", referencedColumnName="GALACTIC_ID")    
    //@Column(name="LOCATION_ID", unique=false, nullable=false, updatable=false)
    private GalacticPosition location;
    
    private String name;

    // The M:1 side is the owning side for "computers"
    @ManyToOne(fetch=EAGER)//LAZY)
/*    @JoinTable(name="CMP3_MM_MANUF_MM_COMPUTER", 
            joinColumns = @JoinColumn(name="COMPUTER_ID"), 
            inverseJoinColumns = @JoinColumn(name="PERSON_ID"))*/   
    private Manufacturer manufacturer;

/*    // The M:1 side is the owning side for "corporateComputers"
    @ManyToOne(fetch=EAGER)//LAZY)
    @JoinTable(name="CMP3_MM_MANUF_MM_CORPORATION", 
            joinColumns = @JoinColumn(name="COMPUTER_ID"), 
            inverseJoinColumns =@JoinColumn(name="PERSON_ID"))   
    private Corporation corporation;*/
    
    // Inverse side 
    @OneToMany(cascade=ALL, mappedBy="computer")
    // A Collection where the Collection type (Map, Set, List) is not defined at design time
    // see design issue #58
    // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_58:_20090807:_ManagedType_Attribute_Initialization_must_differentiate_between_Collection_and_List
    private Collection<Board> circuitBoards;

    // UC10:  mapKey defined via generics and is a java class defined as an IdClass on the element(value) class
    @OneToMany(mappedBy="computerUC10", cascade=ALL, fetch=EAGER)
    @MapKey // key defaults to an instance of the composite pk class
    //@MapKey(name="name")
    private Map<EnclosureIdClassPK, Enclosure> enclosuresUC10;

    // UC12:  mapKey defined via generics and is an Embeddable (EmbeddedId) java class defined as an IdClass on the element(value) class
    @OneToMany(mappedBy="computerUC12", cascade=ALL, fetch=EAGER)
    @MapKey // key defaults to an instance of the composite pk class
    //@MapKey(name="primaryKey")
    private Map<EmbeddedPK, GalacticPosition> positionUC12;

    // UC13:  mapKey defined via generics and is an Embeddable (EmbeddedId) java class defined as an IdClass on the element(value) class
    // However, here we make the owning OneToMany - unidirectional and an effective ManyToMany
    //@OneToMany(mappedBy="computerUniUC13", cascade=ALL, fetch=EAGER)
    @MapKey // key defaults to an instance of the composite pk class
    private Map<EmbeddedPK, GalacticPosition> positionUniUC13;
    
    public void addEnclosure(Enclosure enclosure) {
        enclosure.setComputer(this);
        enclosuresUC10.put(enclosure.buildPK(), enclosure);
    }
    
    public Computer() {
        super();
        enclosuresUC10 = new HashMap<EnclosureIdClassPK, Enclosure>();
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public GalacticPosition getLocation() {
        return location;
    }

    public void setLocation(GalacticPosition location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Collection<Board> getCircuitBoards() {
        return circuitBoards;
    }

    public void setCircuitBoards(Collection<Board> circuitBoards) {
        this.circuitBoards = circuitBoards;
    }
    
    public Map<EnclosureIdClassPK, Enclosure> getEnclosures() {
        return enclosuresUC10;
    }

    public void setEnclosuresUC10(Map<EnclosureIdClassPK, Enclosure> enclosures) {
        this.enclosuresUC10 = enclosures;
    }
}
