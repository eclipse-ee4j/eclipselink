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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity(name="HardwareDesignerMetamodel")
@Table(name="CMP3_MM_HWDESIGNER")
public class HardwareDesigner extends Designer implements java.io.Serializable{
    // The M:1 side is the owning side
    @ManyToOne(fetch=EAGER)//LAZY)
    @JoinTable(name="CMP3_MM_MANUF_MM_HWDESIGNER", 
            joinColumns = @JoinColumn(name="DESIGNER_ID"), 
            inverseJoinColumns =@JoinColumn(name="MANUF_ID"))   
    private Manufacturer employer;

    // The M:1 side is the owning side
    @ManyToOne(fetch=EAGER)//LAZY)
    @JoinTable(name="CMP3_MM_MANUF_MM_HWDES_MAP", 
            joinColumns = @JoinColumn(name="DESIGNER_MAP_ID"), 
            inverseJoinColumns =@JoinColumn(name="MANUF_ID"))   
    private Manufacturer mappedEmployer;
    
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
    
}
