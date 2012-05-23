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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import javax.persistence.*;

public class PartnerLinkPK {
	@Id
    private Integer manId;
	@Id
    private Integer womanId;
	
	// This should not get processed as part of the primary key.
	public static final long serialVersionUID = 7696472275622076147L;
	// Nor this ...
	public transient Integer transientField = 0;

	public PartnerLinkPK() {}    
    
	public Integer getManId() { 
        return manId; 
    }    
    
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
