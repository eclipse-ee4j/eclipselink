/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     07/09/2018-2.6 Jody Grassel
 *       - 536853: MapsID processing sets up to fail validation
 ******************************************************************************/

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
