/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inheritance.listeners;

import javax.persistence.PrePersist;
import javax.persistence.PostPersist;

/**
 * A listener for all TireInfo entities.
 */
public class TireInfoListener extends ListenerSuperclass {
    public static int PRE_PERSIST_COUNT = 0;
    public static int POST_PERSIST_COUNT = 0;

	@PrePersist
    public void prePersist(Object tireInfo) {
        PRE_PERSIST_COUNT++;
	}
    
	@PostPersist
    public void postPersist(Object tireInfo) {
        POST_PERSIST_COUNT++;
	}
}
