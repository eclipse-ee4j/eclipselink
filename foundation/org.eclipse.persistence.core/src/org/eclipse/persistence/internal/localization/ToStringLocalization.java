/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.localization;


/**
 *  <p>
 * <b>Purpose</b>: This is for any toString messages
 *
 * @author: Shannon Chen
 * @since TOPLink/Java 5.0
 */
public class ToStringLocalization extends EclipseLinkLocalization {
    public static String buildMessage(String key, Object[] arguments) {
        return buildMessage("ToStringLocalization", key, arguments);
    }

    public static String buildMessage(String key) {
        return buildMessage(key, (Object[])null);
    }
}
