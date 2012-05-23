/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;

@Cacheable(true)
@Entity
@DiscriminatorValue("4")
public class Bicycle extends Vehicle {

    private static final long serialVersionUID = -5261998177428381310L;

    private transient CallbackEventListener callbackListener;

    @Basic
    @Column(name = "NUM_GEARS")
    protected short numberOfGears;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(name = "TMP_EMP_BICYCLE", joinColumns = { @JoinColumn(name = "BICYCLE_ID") }, inverseJoinColumns = { @JoinColumn(name = "EMPLOYEE_ID") })
    protected Collection<Employee> riders;

    public void setNumberOfGears(short numberOfGears) {
        this.numberOfGears = numberOfGears;
    }

    public short getNumberOfGears() {
        return numberOfGears;
    }

    public void setRiders(Collection<Employee> riders) {
        this.riders = riders;
    }

    public Collection<Employee> getRiders() {
        return riders;
    }

    public void registerListener(final CallbackEventListener listener) {
        callbackListener = listener;
    }

    @PrePersist
    public void callbackPrePersist() {
        if (callbackListener != null) {
            callbackListener.callbackCalled(this, PrePersist.class);
        }
    }

    @PreRemove
    public void callbackPreRemove() {
        if (callbackListener != null) {
            callbackListener.callbackCalled(this, PreRemove.class);
        }
    }
}
