/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.descriptors;

import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose</b>: Used by CMP policy to raise life-cycle events.
 * This allows CMP to avoid using DescriptorEvents which are expensive and not required.
 */
public interface CMPLifeCycleListener {
    void invokeEJBLoad(Object bean, AbstractSession session);
    void invokeEJBStore(Object bean, AbstractSession session);
    void postInsert(Object bean, AbstractSession session);
}