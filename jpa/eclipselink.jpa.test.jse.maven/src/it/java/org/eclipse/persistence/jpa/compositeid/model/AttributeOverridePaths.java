/********************************************************************************
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 ******************************************************************************/

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
