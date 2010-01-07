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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity(name="SoftwareDesignerMetamodel")
@Table(name="CMP3_MM_SWDESIGNER")
public class SoftwareDesigner extends Designer {
    
    private static final long serialVersionUID = -6424921774260902782L;
    
    @Version
    @Column(name="SWDESIGNER_VERSION")
    private int version;
    
    // The M:1 side is the owning side
/*    @ManyToOne(fetch=EAGER)//LAZY)
    @JoinTable(name="CMP3_MM_MANUF_MM_SWDESIGNER", 
            joinColumns = @JoinColumn(name="PERSON_ID"), 
            inverseJoinColumns =@JoinColumn(name="MANUF_ID"))   
    private Manufacturer manufacturer;
*/    
    public SoftwareDesigner() {}

    public int getVersion() { 
        return version; 
    }
    
    protected void setVersion(int version) {
        this.version = version;
    }

}
