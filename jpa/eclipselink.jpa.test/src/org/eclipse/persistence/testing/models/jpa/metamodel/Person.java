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
 *     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
 *       of the Metamodel implementation for EclipseLink 2.0 release involving
 *       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.TABLE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;

//This class uses IntanceVariableAttributeAccessors during reflective calls
@MappedSuperclass
public class Person implements Serializable { //  changed from abstract to concrete for 248780   
    @Id
    @GeneratedValue(strategy=TABLE, generator="PERSON_MM_TABLE_GENERATOR")
    @TableGenerator(
        name="PERSON_MM_TABLE_GENERATOR", 
        table="CMP3_MM_PERSON_SEQ", 
        pkColumnName="SEQ_MM_NAME", 
        valueColumnName="SEQ_MM_COUNT",
        pkColumnValue="CUST_MM_SEQ"
    )    
    // InstanceVariableAttributeAccessor testing
    @Column(name="PERSON_ID")    
    private Integer id;
    
    
    // Verify special handling for PK for OneToMany (custom descriptor with fake PK name)
    // If a JoinTable with a JoinColumn is used - then we need a mappedBy on the inverse side here
    // However, bidirectional relationships are not allowed to MappedSuperclasses - as they have no identity
    // This @OneToMany implements internally as a @ManyToMany
    @OneToMany(fetch=EAGER, cascade=ALL)
    // Note: DI We do not check the values of the join column names - they can be anything
/*    @JoinTable(name="CMP3_MM_HIST_EMPLOY", 
                joinColumns = @JoinColumn(name="PERSON_ID", referencedColumnName="PERSON_ID"), 
                inverseJoinColumns = @JoinColumn(name="PERSON_ID", referencedColumnName="PERSON_ID"))*/   
    //private Collection<Manufacturer> historicalEmployers = new HashSet<Manufacturer>();
    private Collection<Manufacturer> historicalEmps = new ArrayList<Manufacturer>();
    
    private String name;

    public String getName() {
        return name;
    }

    //@Column(name="PERSON_NAME") // avoid Exception Description: Mapping metadata cannot be applied to properties/methods that take arguments. The attribute [method setName] from class [class org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer] is in violation of this restriction. Ensure the method has no arguments if it is mapped with annotations or in an XML mapping file.
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getId() {    
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Collection<Manufacturer> getHistoricalEmployers() {
        return historicalEmps;
    }

    public void setHistoricalEmployers(Collection<Manufacturer> historicalEmployers) {
        this.historicalEmps = historicalEmployers;
    }
    
}
