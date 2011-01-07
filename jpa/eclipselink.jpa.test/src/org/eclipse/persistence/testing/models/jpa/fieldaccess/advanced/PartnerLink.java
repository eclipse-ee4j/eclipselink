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
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import javax.persistence.*;

import static javax.persistence.CascadeType.*;

@Entity(name = "PartnerLink")
@Table(name = "CMP3_FA_MW")
@IdClass(org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.PartnerLinkPK.class)
public class PartnerLink {
    @Id
    @Column(name = "M", insertable = false, updatable = false)
    private Integer manId;

    @Id
    @Column(name = "W", insertable = false, updatable = false)
    private Integer womanId;

    @OneToOne(cascade = PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "M")
    private Man man;

    @OneToOne(cascade = PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "W")
    private Woman woman;

    public PartnerLink() {
    }

    public Man getMan() {
        return man;
    }

    public Integer getManId() {
        manId = (getMan() == null) ? null : getMan().getId();
        return manId;
    }

    public Woman getWoman() {
        return woman;
    }

    public Integer getWomanId() {
        womanId = (getWoman() == null) ? null : getWoman().getId();
        return womanId;
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
