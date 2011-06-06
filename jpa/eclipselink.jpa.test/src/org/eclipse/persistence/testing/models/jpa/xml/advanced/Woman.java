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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

public class Woman {
    private Integer id;
    private String firstName;
    private String lastName;
    private PartnerLink partnerLink;

	public Woman() {}
    
    //@Id
	public Integer getId() { 
        return id; 
    }
    
    public String getFirstName() { 
        return firstName; 
    }
    
    public void setFirstName(String name) { 
        this.firstName = name; 
    }

    public String getLastName() { 
        return lastName; 
    }
    
    public void setLastName(String name) { 
        this.lastName = name; 
    }

    //@OneToOne(mappedBy="woman")
	public PartnerLink getPartnerLink() { 
        return partnerLink; 
    }
    
	public void setId(Integer id) { 
        this.id = id; 
    }
    
    public void setPartnerLink(PartnerLink partnerLink) { 
        this.partnerLink = partnerLink; 
    }
}
