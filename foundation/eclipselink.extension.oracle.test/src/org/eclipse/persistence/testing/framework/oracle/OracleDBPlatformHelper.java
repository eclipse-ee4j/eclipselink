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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.framework.oracle;

import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.platform.database.oracle.Oracle8Platform;
import org.eclipse.persistence.platform.database.oracle.Oracle9Platform;

public class OracleDBPlatformHelper extends org.eclipse.persistence.testing.framework.OracleDBPlatformHelper{

    public OraclePlatform getOracle8Platform(){
        return new Oracle8Platform();
    }

    public OraclePlatform getOracle9Platform(){
        return new Oracle9Platform();
    }
}
