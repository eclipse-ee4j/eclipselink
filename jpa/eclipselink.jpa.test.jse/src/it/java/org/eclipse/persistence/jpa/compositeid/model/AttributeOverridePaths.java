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

public final class AttributeOverridePaths {
    public final static String CLIENT_ID = "clientId";
    public final static String COMPB_ID = "compBId";
    public final static String ENVIRONMENT = "environment";
    public final static String IDENTIFIER = "identifier";
    public final static String RN = "rn";
    public final static String COMPA_ID = "compAId";
    public final static String USER_ID = "userId";
    public final static String VALUE = "value";
    
    public final static String RN_VALUE = RN + "." + VALUE;
    public final static String CLIENT_ID_VALUE = CLIENT_ID + "." + VALUE;
    public final static String COMPA_ID_ENVIRONMENT_VALUE = COMPA_ID + "." + ENVIRONMENT + "." + VALUE;
    public final static String COMPA_ID_IDENTIFIER_VALUE = COMPA_ID + "." + IDENTIFIER + "." + VALUE;
    public final static String USER_ID_VALUE = USER_ID + "." + VALUE;
    public final static String COMPB_ID_CLIENT_ID_VALUE = COMPB_ID + "." + CLIENT_ID + "." + VALUE;
    public final static String COMPB_ID_ROLE_ID_ENVIRONMENT_VALUE = COMPB_ID + "." + COMPA_ID + "." + ENVIRONMENT + "." + VALUE;
    public final static String COMPB_ID_ROLE_ID_IDENTIFIER_VALUE = COMPB_ID + "." + COMPA_ID + "." + IDENTIFIER + "." + VALUE;
    public final static String COMPB_ID_RN_VALUE = COMPB_ID + "." + RN + "." + VALUE;

}
