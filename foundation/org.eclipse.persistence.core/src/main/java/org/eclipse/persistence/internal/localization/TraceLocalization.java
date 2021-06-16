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
//     11/07/2017 - Dalia Abo Sheasha
//       - 526957 : Split the logging and trace messages
package org.eclipse.persistence.internal.localization;


/**
 *  <p>
 * <b>Purpose</b>: This is for any trace related messages
 * Messages are not currently translated by default here.
 *
 * @author Shannon Chen
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public class TraceLocalization extends EclipseLinkLocalization {
    public static String buildMessage(String key, Object[] arguments) {
        return buildMessage("TraceLocalization", key, arguments);
    }

    public static String buildMessage(String key, Object[] arguments, boolean translate) {
        return buildMessage("TraceLocalization", key, arguments, translate);
    }

    public static String buildMessage(String key, boolean translate) {
        return buildMessage(key, null, translate);
    }

    public static String buildMessage(String key) {
        return buildMessage(key, null);
    }
}
