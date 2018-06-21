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
//     tware - contribution direct from Oracle TopLink
package org.eclipse.persistence.internal.localization;

/**
*  <p>
* <b>Purpose</b>: This is for DMS related messages
*
* @since TOPLink AS 10.0.3
*/
public class DMSLocalization extends EclipseLinkLocalization {

 public static String buildMessage(String key, Object[] arguments) {
     return buildMessage("DMSLocalization", key, arguments);
 }

 public static String buildMessage(String key) {
     return buildMessage(key, (Object[])null);
 }
}
