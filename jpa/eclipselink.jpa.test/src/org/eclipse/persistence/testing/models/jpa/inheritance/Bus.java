/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Entity
@EntityListeners(org.eclipse.persistence.testing.models.jpa.inheritance.listeners.BusListener.class)
@Table(name="CMP3_BUS")
@DiscriminatorValue("BU")
@PrimaryKeyJoinColumn(name="BUS_ID", referencedColumnName="ID")
public class Bus extends AbstractBus {
    private Person busDriver;
    
	@OneToOne(cascade=PERSIST, fetch=LAZY)
	@JoinColumn(name="DRIVER_ID", referencedColumnName="ID")
    public Person getBusDriver() {
        return busDriver;
    }

    public void setBusDriver(Person busDriver) {
        this.busDriver = busDriver;
    }
}