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

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;

@Entity(name="SoftwareDesignerMetamodel")
@Table(name="CMP3_MM_SWDESIGNER")
public class SoftwareDesigner extends Designer implements java.io.Serializable{
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
