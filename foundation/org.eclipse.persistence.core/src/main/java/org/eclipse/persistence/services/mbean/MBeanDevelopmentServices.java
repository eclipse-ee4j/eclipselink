/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     @author  mobrien
//     @since   EclipseLink 1.0 enh# 235168
package org.eclipse.persistence.services.mbean;

import org.eclipse.persistence.services.DevelopmentServices;
import org.eclipse.persistence.sessions.Session;

/**
 * <p>
 * <b>Purpose</b>: Provide a dynamic interface into the EclipseLink Identity Map Manager.
 * <p>
 * <b>Description</b>: This class is meant to provide a framework for gaining access to configuration and
 * statistics of the EclipseLink Cache during runtime.  It will provide the basis for development
 * of a JMX service and possibly other frameworks.
 * <ul>
 * <li>
 * </ul>
 */
public class MBeanDevelopmentServices extends DevelopmentServices implements MBeanDevelopmentServicesMBean {
    public MBeanDevelopmentServices(Session session) {
        super(session);
    }
}
