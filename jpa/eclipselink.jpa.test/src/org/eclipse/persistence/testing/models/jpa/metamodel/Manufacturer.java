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

import static javax.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity(name="ManufacturerMetamodel")
@Table(name="CMP3_MM_MANUF")
public class Manufacturer extends Corporation implements java.io.Serializable{
    @Version
    @Column(name="MANUF_VERSION")
    private int version;
    
    // If a JoinTable with a JoinColumn is used - then we need a mappedBy on the inverse side here
    @OneToMany(cascade=ALL, mappedBy="manufacturer")
    private Set<Computer> computers = new HashSet<Computer>();

    // If a JoinTable with a JoinColumn is used - then we need a mappedBy on the inverse side here
    @OneToMany(cascade=ALL, mappedBy="employer")
    private List<HardwareDesigner> hardwareDesigners = new ArrayList<HardwareDesigner>();
    
    // If a JoinTable with a JoinColumn is used - then we need a mappedBy on the inverse side here
    //@OneToMany(cascade=ALL, mappedBy="manufacturer")
    //private Collection<SoftwareDesigner> softwareDesigners = new HashSet<SoftwareDesigner>();

    // If a JoinTable with a JoinColumn is used - then we need a mappedBy on the inverse side here
    @OneToMany(cascade=ALL, mappedBy="mappedEmployer")
    private Map<String, HardwareDesigner> hardwareDesignersMap = new HashMap<String, HardwareDesigner>();

    public Manufacturer() {}

    public int getVersion() { 
        return version; 
    }
    
    protected void setVersion(int version) {
        this.version = version;
    }


    public Collection<Computer> getComputers() { 
        return computers; 
    }
    
    public void setComputers(Set<Computer> newValue) { 
        this.computers = newValue; 
    }

    public void addComputer(Computer aComputer) {
        getComputers().add(aComputer);
        aComputer.setManufacturer(this);
    }

    public void removeComputer(Computer aComputer) {
        getComputers().remove(aComputer);
        aComputer.setManufacturer(null);
    }

    public List<HardwareDesigner> getHardwareDesigners() {
        return hardwareDesigners;
    }

    public void setHardwareDesigners(List<HardwareDesigner> designers) {
        this.hardwareDesigners = designers;
    }

    public Map<String, HardwareDesigner> getHardwareDesignersMap() {
        return hardwareDesignersMap;
    }

    public void setHardwareDesignersMap(Map<String, HardwareDesigner> hardwareDesignersMap) {
        this.hardwareDesignersMap = hardwareDesignersMap;
    }

    public void addHardwareDesignerToMap(HardwareDesigner aDesigner) {
        this.getHardwareDesignersMap().put(aDesigner.getName(), aDesigner);
        aDesigner.setMappedEmployer(this);
    }

    public void removeHardwareDesignerFromMapr(HardwareDesigner aDesigner) {
        this.getHardwareDesignersMap().remove(aDesigner.getName());
        aDesigner.setMappedEmployer(null);
    }
    
}
