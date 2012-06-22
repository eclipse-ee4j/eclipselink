/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.EventListener;
import javax.persistence.PrePersist;

/**
 * A superclass listener for SportsCar and Bus entities.
 * 
 * It implements the following annotations:
 * - PrePersist
 * 
 * It overrides the following annotations:
 * - None
 * 
 * It inherits the following annotations:
 * - None
 */
public class ListenerSuperclass implements EventListener {
    public static int COMMON_PRE_PERSIST_COUNT = 0;
    
    @PrePersist
	public void prePersist(Object obj) {
        COMMON_PRE_PERSIST_COUNT++;
	}
}
