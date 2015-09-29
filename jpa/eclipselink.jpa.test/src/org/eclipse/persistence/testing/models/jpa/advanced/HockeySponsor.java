/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 *     
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.beans.PropertyChangeListener;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.internal.descriptors.changetracking.AttributeChangeListener;
import org.eclipse.persistence.sessions.UnitOfWork;

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
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getSimpleName() + " hashcode:[" + System.identityHashCode(this) + "]");
        if (this instanceof ChangeTracker) {
            PropertyChangeListener listener = ((ChangeTracker)this)._persistence_getPropertyChangeListener();
            buffer.append(" listener:[" + listener + "]");
            if (listener != null && listener instanceof AttributeChangeListener) {
                UnitOfWork uow = ((AttributeChangeListener)listener).getUnitOfWork();
                buffer.append(" uow:[" + uow + "] uow hashcode: " + System.identityHashCode(uow));
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
