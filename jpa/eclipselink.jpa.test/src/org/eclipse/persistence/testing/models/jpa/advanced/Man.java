/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.*;

import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Entity
public class Man {
    private int id;
    private String firstName;
    private String lastName;
    private PartnerLink partnerLink;

	public Man() {}
    
    @Id
    @GeneratedValue(strategy=IDENTITY)
	public int getId() { 
        return id; 
    }

    @Column(name="F_NAME")
    public String getFirstName() { 
        return firstName; 
    }
    
    public void setFirstName(String name) { 
        this.firstName = name; 
    }

    @Column(name="L_NAME")
    public String getLastName() { 
        return lastName; 
    }
    
    public void setLastName(String name) { 
        this.lastName = name; 
    }
    
    @OneToOne(mappedBy="man")
	public PartnerLink getPartnerLink() { 
        return partnerLink; 
    }
    
	public void setId(int id) { 
        this.id = id; 
    }
    
    public void setPartnerLink(PartnerLink partnerLink) { 
        this.partnerLink = partnerLink; 
    }
}
