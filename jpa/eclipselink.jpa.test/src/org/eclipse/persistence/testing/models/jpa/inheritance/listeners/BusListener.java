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
 ******************************************************************************/  


package org.eclipse.persistence.testing.models.jpa.inheritance.listeners;

import javax.persistence.PrePersist;
import javax.persistence.PostPersist;
import javax.persistence.PreRemove;
import javax.persistence.PostRemove;
import javax.persistence.PreUpdate;
import javax.persistence.PostUpdate;

/**
 * A listener for the Bus entity.
 * 
 * It implements the following annotations:
 * - PreRemove
 * - PostRemove
 * - PreUpdate
 * - PostUpdate
 * 
 * It overrides the following annotations:
 * - PrePersist from ListenerSuperclass
 * - PostPersist from FueledVehicleListener
 * 
 * It inherits the following annotations:
 * - PostLoad from Vehicle.
 */
public class BusListener extends ListenerSuperclass {
    public static int PRE_PERSIST_COUNT = 0;
    public static int POST_PERSIST_COUNT = 0;
    public static int PRE_REMOVE_COUNT = 0;
    public static int POST_REMOVE_COUNT = 0;
    public static int PRE_UPDATE_COUNT = 0;
    public static int POST_UPDATE_COUNT = 0;

    @Override
	@PrePersist
    public void prePersist(Object bus) {
        PRE_PERSIST_COUNT++;
	}
    
	@PostPersist
    // Protected access
    protected void postPersist(Object bus) {
        POST_PERSIST_COUNT++;
	}

	@PreRemove
	public void preRemove(Object bus) {
        PRE_REMOVE_COUNT++;
	}

	@PostRemove
	public void postRemove(Object bus) {
        POST_REMOVE_COUNT++;
	}

	@PreUpdate
    // Package access
	void preUpdate(Object bus) {
        PRE_UPDATE_COUNT++;
	}

	@PostUpdate
    // Private access
	private void postUpdate(Object bus) {
        POST_UPDATE_COUNT++;
	}
}
