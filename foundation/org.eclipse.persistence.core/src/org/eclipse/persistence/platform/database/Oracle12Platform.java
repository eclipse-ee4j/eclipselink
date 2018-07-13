/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
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
//     06/26/2018 - Will Dazey
//       - 532160 : Add support for non-extension OracleXPlatform classes
package org.eclipse.persistence.platform.database;

/**
 * <p><b>Purpose:</b>
 * Provides Oracle version specific behavior when 
 * org.eclipse.persistence.oracle bundle is not available.
 */
public class Oracle12Platform extends Oracle11Platform {
    public Oracle12Platform() {
        super();
    }
}
