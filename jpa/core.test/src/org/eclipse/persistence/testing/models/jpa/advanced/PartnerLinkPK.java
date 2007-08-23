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

public class PartnerLinkPK {
    private Integer manId;
    private Integer womanId;

	public PartnerLinkPK() {}
    
    @Id
	public Integer getManId() { 
        return manId; 
    }
    
    @Id
	public Integer getWomanId() { 
        return womanId; 
    }
    
	public void setManId(Integer manId) { 
        this.manId = manId; 
    }
    
    public void setWomanId(Integer womanId) { 
        this.womanId = womanId; 
    }

    public boolean equals(Object anotherPartnerLinkPK) {
        if (anotherPartnerLinkPK.getClass() != PartnerLinkPK.class) {
            return false;
        }
        
        return getManId().equals(((PartnerLinkPK) anotherPartnerLinkPK).getManId()) && 
               getWomanId().equals(((PartnerLinkPK) anotherPartnerLinkPK).getWomanId());
    }
}