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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import java.util.HashSet;
import java.util.Collection;
import javax.persistence.*;
import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;

@Entity(name="ManufacturerMetamodel")
@Table(name="CMP3_MM_MANUF")
public class Manufacturer extends Corporation implements java.io.Serializable{
    @Version
    @Column(name="MANUF_VERSION")
    private int version;
    
    // If a JoinTable with a JoinColumn is used - then we need a mappedBy on the inverse side here
    @OneToMany(cascade=ALL, mappedBy="manufacturer")
    private Collection<Computer> computers = new HashSet<Computer>();

    // If a JoinTable with a JoinColumn is used - then we need a mappedBy on the inverse side here
    @OneToMany(cascade=ALL, mappedBy="employer")
    private Collection<HardwareDesigner> hardwareDesigners = new HashSet<HardwareDesigner>();
    
/*    // If a JoinTable with a JoinColumn is used - then we need a mappedBy on the inverse side here
    @OneToMany(cascade=ALL, mappedBy="manufacturer")
    private Collection<SoftwareDesigner> softwareDesigners = new HashSet<SoftwareDesigner>();
*/
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
    
    public void setComputers(Collection<Computer> newValue) { 
        this.computers = newValue; 
    }

    public void addComputer(Computer aComputer) {
        getComputers().add(aComputer);
        aComputer.setManufacturer(this);
    }

    public void removeComputer(Computer aComputer) {
        getComputers().remove(aComputer);
    }

    public Collection<HardwareDesigner> getHardwareDesigners() {
        return hardwareDesigners;
    }

    public void setHardwareDesigners(Collection<HardwareDesigner> hardwareDesigners) {
        this.hardwareDesigners = hardwareDesigners;
    }

    
}
