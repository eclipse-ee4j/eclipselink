/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     @author  mobrien
//     @since   EclipseLink 1.0 enh# 235168
package org.eclipse.persistence.services.mbean;

import org.eclipse.persistence.services.RuntimeServices;
import org.eclipse.persistence.sessions.Session;

/**
 * <p>
 * <b>Purpose</b>: Provide a dynamic interface into the EclipseLink Session.
 * <p>
 * <b>Description</b>: This class is meant to provide a framework for gaining access to configuration
 * of the EclipseLink Session during runtime.  It will provide the basis for development
 * of a JMX service and possibly other frameworks.
 * <ul>
 * <li>
 * </ul>
 *
 */
public class MBeanRuntimeServices extends RuntimeServices implements MBeanRuntimeServicesMBean {
    public MBeanRuntimeServices(Session session) {
        super(session);
    }
}
