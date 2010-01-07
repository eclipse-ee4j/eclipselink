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
 *     10/22/2009 - Guy Pelletier/Prakash Selvaraj - added tests for DDL generation of table per class
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass;

import java.io.Serializable;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries({
  @NamedQuery(name = "PlatinumCustomer.findAll", query = "select o from PlatinumCustomer o")
})
public class PlatinumCustomer extends GoldCustomer implements Serializable {
    @OneToMany
    //@JoinTable
    private List<PlatinumBenefit> platinumBenefitList;

    public PlatinumCustomer() {
    }

    public List<PlatinumBenefit> getPlatinumBenefitList() {
        return platinumBenefitList;
    }

    public void setPlatinumBenefitList(List<PlatinumBenefit> platinumBenefitList) {
        this.platinumBenefitList = platinumBenefitList;
    }
}
