/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.services.mbean;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.services.DevelopmentServices;

/**
 * <p>
 * <b>Purpose</b>: Provide a dynamic interface into the TopLink Identity Map Manager.
 * <p>
 * <b>Description</b>: This class is ment to provide a framework for gaining access to configuration and
 * statistics of the TopLink Cache during runtime.  It will provide the basis for developement
 * of a JMX service and possibly other frameworks.
 * <ul>
 * <li>
 * </ul>
 *
 * @deprecated Will be replaced by a server-specific equivalent for org.eclipse.persistence.services.oc4j.Oc4jRuntimeServices
 * @see org.eclipse.persistence.services.oc4j.Oc4jRuntimeServices
 */
public class MBeanDevelopmentServices extends DevelopmentServices implements MBeanDevelopmentServicesMBean {
    public MBeanDevelopmentServices(AbstractSession session) {
        super(session);
    }
}