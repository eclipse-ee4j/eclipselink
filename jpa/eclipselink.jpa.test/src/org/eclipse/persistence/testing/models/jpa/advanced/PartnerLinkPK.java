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
package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.*;

public class PartnerLinkPK {
    private int manId;
    private Integer womanId;

	public PartnerLinkPK() {}

	   public PartnerLinkPK(int manId, Integer womanId) {
	       this.manId = manId;
	       this.womanId = womanId;
	   }

	
    @Id
	public int getMan() { 
        return manId; 
    }
    
    @Id
	public Integer getWoman() { 
        return womanId; 
    }
    
	public void setMan(int manId) { 
        this.manId = manId; 
    }
    
    public void setWoman(Integer womanId) { 
        this.womanId = womanId; 
    }

    public boolean equals(Object anotherPartnerLinkPK) {
        if (anotherPartnerLinkPK.getClass() != PartnerLinkPK.class) {
            return false;
        }
        
        return getMan() ==(((PartnerLinkPK) anotherPartnerLinkPK).getMan()) && 
               getWoman().equals(((PartnerLinkPK) anotherPartnerLinkPK).getWoman());
    }
}
