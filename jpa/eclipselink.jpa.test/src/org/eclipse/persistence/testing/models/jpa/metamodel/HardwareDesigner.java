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

import static javax.persistence.FetchType.EAGER;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

// No name attribute for testing
@Entity
@Table(name="CMP3_MM_HWDESIGNER")
public class HardwareDesigner extends Designer {
    
    private static final long serialVersionUID = -6424921774260902781L;

    // The M:1 side is the owning side
    @ManyToOne(fetch=EAGER)//LAZY)
/*    @JoinTable(name="CMP3_MM_MANUF_MM_HWDESIGNER", 
            joinColumns = @JoinColumn(name="DESIGNER_ID"), 
            inverseJoinColumns =@JoinColumn(name="MANUF_ID"))*/   
    private Manufacturer employer;

    // The M:1 side is the owning side
    @ManyToOne(fetch=EAGER)//LAZY)
/*    @JoinTable(name="CMP3_MM_MANUF_MM_HWDES_MAP", 
            joinColumns = @JoinColumn(name="DESIGNER_MAP_ID"), 
            inverseJoinColumns =@JoinColumn(name="MANUF_ID"))*/   
    private Manufacturer mappedEmployer;

    // The M:1 side is the owning side
    @ManyToOne(fetch=EAGER)//LAZY)
/*    @JoinTable(name="CMP3_MM_MANUF_MM_HWDES_MAPUC1A", 
            joinColumns = @JoinColumn(name="DESIGNER_MAP_ID"), 
            inverseJoinColumns =@JoinColumn(name="MANUF_ID"))*/   
    private Manufacturer mappedEmployerUC1a;

    // The M:1 side is the owning side
    @ManyToOne(fetch=EAGER)//LAZY)
/*    @JoinTable(name="CMP3_MM_MANUF_MM_HWDES_MAPUC2", 
            joinColumns = @JoinColumn(name="DESIGNER_MAP_ID"), 
            inverseJoinColumns =@JoinColumn(name="MANUF_ID"))*/   
    private Manufacturer mappedEmployerUC2;

    // The M:1 side is the owning side
    @ManyToOne(fetch=EAGER)//LAZY)
/*    @JoinTable(name="CMP3_MM_MANUF_MM_HWDES_MAPUC4", 
            joinColumns = @JoinColumn(name="DESIGNER_MAP_ID"), 
            inverseJoinColumns =@JoinColumn(name="MANUF_ID"))*/   
    private Manufacturer mappedEmployerUC4;

    // UC6 is invalid
    // The M:1 side is the owning side
/*    @ManyToOne(fetch=EAGER)//LAZY)
    @JoinTable(name="CMP3_MM_MANUF_MM_HWDES_MAPUC6", 
            joinColumns = @JoinColumn(name="DESIGNER_MAP_ID"), 
            inverseJoinColumns =@JoinColumn(name="MANUF_ID"))   
    private Manufacturer mappedEmployerUC6;*/

    // The M:1 side is the owning side
    @ManyToOne(fetch=EAGER)//LAZY)
/*    @JoinTable(name="CMP3_MM_MANUF_MM_HWDES_MAPUC7", 
            joinColumns = @JoinColumn(name="DESIGNER_MAP_ID"), 
            inverseJoinColumns =@JoinColumn(name="MANUF_ID"))*/   
    private Manufacturer mappedEmployerUC7;

    // The M:1 side is the owning side
    @ManyToOne(fetch=EAGER)
/*    @JoinTable(name="CMP3_MM_MANUF_MM_HWDES_MAPUC8", 
            joinColumns = @JoinColumn(name="DESIGNER_MAP_ID"), 
            inverseJoinColumns =@JoinColumn(name="MANUF_ID"))*/   
    private Manufacturer mappedEmployerUC8;
    
    @Version
    @Column(name="HWDESIGNER_VERSION")
    private int version;
    
    public HardwareDesigner() {}

    public int getVersion() { 
        return version; 
    }
    
    protected void setVersion(int version) {
        this.version = version;
    }

    public Manufacturer getEmployer() {
        return employer;
    }

    public void setEmployer(Manufacturer employer) {
        this.employer = employer;
    }

    public Manufacturer getMappedEmployer() {
        return mappedEmployer;
    }

    public void setMappedEmployer(Manufacturer mappedEmployer) {
        this.mappedEmployer = mappedEmployer;
    }

    public Manufacturer getMappedEmployerUC1a() {
        return mappedEmployerUC1a;
    }

    public void setMappedEmployerUC1a(Manufacturer mappedEmployerUC1a) {
        this.mappedEmployerUC1a = mappedEmployerUC1a;
    }

    public Manufacturer getMappedEmployerUC2() {
        return mappedEmployerUC2;
    }

    public void setMappedEmployerUC2(Manufacturer mappedEmployerUC2) {
        this.mappedEmployerUC2 = mappedEmployerUC2;
    }

    public Manufacturer getMappedEmployerUC4() {
        return mappedEmployerUC4;
    }

    public void setMappedEmployerUC4(Manufacturer mappedEmployerUC4) {
        this.mappedEmployerUC4 = mappedEmployerUC4;
    }

    public Manufacturer getMappedEmployerUC7() {
        return mappedEmployerUC7;
    }

    public void setMappedEmployerUC7(Manufacturer mappedEmployerUC7) {
        this.mappedEmployerUC7 = mappedEmployerUC7;
    }

    public Manufacturer getMappedEmployerUC8() {
        return mappedEmployerUC8;
    }

    public void setMappedEmployerUC8(Manufacturer mappedEmployerUC8) {
        this.mappedEmployerUC8 = mappedEmployerUC8;
    }
    
}
