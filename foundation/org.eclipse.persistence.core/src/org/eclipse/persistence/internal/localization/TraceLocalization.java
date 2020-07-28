/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2018 IBM Corporation. All rights reserved.
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
//     11/07/2017 - Dalia Abo Sheasha
//       - 526957 : Split the logging and trace messages
package org.eclipse.persistence.internal.localization;


/**
 *  <p>
 * <b>Purpose</b>: This is for any trace related messages
 * Messages are not currently translated by default here.
 *
 * @author: Shannon Chen
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
        return buildMessage(key, (Object[])null, translate);
    }

    public static String buildMessage(String key) {
        return buildMessage(key, (Object[])null);
    }
}
