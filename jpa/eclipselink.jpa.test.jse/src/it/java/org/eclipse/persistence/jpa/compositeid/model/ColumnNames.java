/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     07/09/2018-2.6 Jody Grassel
//        - 536853: MapsID processing sets up to fail validation

package org.eclipse.persistence.jpa.compositeid.model;

public final class ColumnNames {
    public final static String BTI = "BTI";
    public final static String CLIENT = "CLIENT";
    public final static String ENVIRONMENT = "ENVIRONMENT";
    public final static String IDENTIFIER = "IDENTIFIER";
    public final static String RN = "RN";
    public final static String USER_ID = "USER_ID";

    public final static String FK_ENVIRONME = "FK_ENVIRONME";
    public final static String FK_IDENTIFIE = "FK_IDENTIFIE";
    public final static String FK_USER_ID = "FK_USER_ID";
    public final static String FK_CLIENT = "FK_CLIENT";
    public final static String FK_RELATIVE = "FK_RELATIVE";
}
