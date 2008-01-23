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

import static javax.persistence.CascadeType.*;

@Entity
@Table(name="MW")
@IdClass(org.eclipse.persistence.testing.models.jpa.advanced.PartnerLinkPK.class)
public class PartnerLink {
    private Man man;
    private Woman woman;

	public PartnerLink() {}
    
    @OneToOne(cascade=PERSIST)
	@JoinColumn(name="M")
	public Man getMan() { 
        return man; 
    }
    
    @Id
    @Column(name="M", insertable=false, updatable=false)
	public Integer getManId() {
        return (getMan() == null) ? null : getMan().getId();
    }
    
    @OneToOne(cascade=PERSIST)
	@JoinColumn(name="W")
	public Woman getWoman() { 
        return woman; 
    }
    
    @Id
    @Column(name="W", insertable=false, updatable=false)
	public Integer getWomanId() {
        return (getWoman() == null) ? null : getWoman().getId();
    }
    
	public void setMan(Man man) { 
        this.man = man; 
    }
    
    public void setManId(Integer manId) {  
    }
    
    public void setWoman(Woman woman) { 
        this.woman = woman; 
    }
    
    public void setWomanId(Integer womanId) { 
    }
}
