/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Sun Microsystems

package org.eclipse.persistence.platform.database;

/**
 * <p><b>Purpose</b>: Allows to use JavaDBPlatform as a synonym for DerbyPlatform
 */
public class JavaDBPlatform extends DerbyPlatform {
    // Do not add any code to this class.
    // JavaDB is the official name of databse bundled with glassfish
    // The only purpose of this class is to allow use of JavaDBPlatform as a
    // synonym for DerbyPlatform
    // All the Derby specific code should be added to DerbyPlatform
}
