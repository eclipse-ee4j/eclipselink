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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     01/05/2010-2.1 Guy Pelletier 
 *       - 211324: Add additional event(s) support to the EclipseLink-ORM.XML Schema
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Entity
@EntityListeners({
    org.eclipse.persistence.testing.models.jpa.inheritance.listeners.BusListener.class,
    org.eclipse.persistence.testing.models.jpa.inheritance.listeners.BusNativeListener.class
})
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
