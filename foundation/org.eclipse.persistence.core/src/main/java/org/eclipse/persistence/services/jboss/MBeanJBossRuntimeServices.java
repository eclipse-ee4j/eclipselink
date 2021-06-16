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
//     @author  mobrien
//     @since   EclipseLink 2.1.1 enh# 316511
//     06/30/2010-2.1.1 Michael O'Brien
//       - 316513: Enable JMX MBean functionality for JBoss, Glassfish and WebSphere in addition to WebLogic
//       Move JMX MBean generic registration code up from specific platforms
//       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/316513</link>
package org.eclipse.persistence.services.jboss;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.Session;

/**
 * <p>
 * <b>Purpose</b>: Provide a dynamic interface into the EclipseLink Session.
 * <p>
 * <b>Description</b>: This class is meant to provide a framework for gaining access to configuration
 * of the EclipseLink Session during runtime.  It will provide the basis for development
 * of a JMX service and possibly other frameworks.
 *
 * @since EclipseLink 2.1.1
 */
public class MBeanJBossRuntimeServices extends JBossRuntimeServices implements MBeanJBossRuntimeServicesMBean {
    public MBeanJBossRuntimeServices(Session session) {
        super((AbstractSession)session);
    }
}
