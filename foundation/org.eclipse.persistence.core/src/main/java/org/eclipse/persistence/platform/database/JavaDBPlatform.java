/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
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
