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
//     @since   EclipseLink 1.1 enh# 248748
//     10/20/2008-1.1M4 Michael O'Brien
//       - 248748: Add WebLogic 10.3 specific JMX MBean attributes and functions
//       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/248748</link>
//     27/01/2009-1.1.1 Michael O'Brien
//       - 262583: removal of category arg get//EclipseLinkLogLevel functions - rev 3308
//     06/30/2010-2.1.1 Michael O'Brien
//       - 316513: Enable JMX MBean functionality for JBoss, Glassfish and WebSphere in addition to WebLogic
//       Move JMX MBean generic registration code up from specific platforms
//       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/316513</link>
package org.eclipse.persistence.services.weblogic;

import org.eclipse.persistence.services.mbean.MBeanRuntimeServicesMBean;

/**
 * <p>
 * <b>Purpose</b>: Provide a dynamic interface into the EclipseLink Session.
 * <p>
 * <b>Description</b>: This class is meant to provide facilities for managing an EclipseLink session external
 * to EclipseLink over JMX.
 */
public interface MBeanWebLogicRuntimeServicesMBean extends MBeanRuntimeServicesMBean {


}
