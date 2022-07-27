/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     dminsky - initial API and implementation
//
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.internal.descriptors.changetracking.AttributeChangeListener;
import org.eclipse.persistence.sessions.UnitOfWork;

import java.beans.PropertyChangeListener;

@Embeddable
public class HockeySponsor {
    
    @Column(name="SPONSOR_NAME")
    protected String name;
    @Column(name="SPONSOR_VALUE")
    protected int sponsorshipValue;
    
    public HockeySponsor() {
        super();
    }
    
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getClass().getSimpleName()).append(" hashcode:[").append(System.identityHashCode(this)).append("]");
        if (this instanceof ChangeTracker) {
            PropertyChangeListener listener = ((ChangeTracker)this)._persistence_getPropertyChangeListener();
            buffer.append(" listener:[").append(listener).append("]");
            if (listener instanceof AttributeChangeListener) {
                UnitOfWork uow = ((AttributeChangeListener)listener).getUnitOfWork();
                buffer.append(" uow:[").append(uow).append("] uow hashcode: ").append(System.identityHashCode(uow));
                buffer.append("]");
            }
        }
        return buffer.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSponsorshipValue() {
        return sponsorshipValue;
    }

    public void setSponsorshipValue(int sponsorshipValue) {
        this.sponsorshipValue = sponsorshipValue;
    }

}
