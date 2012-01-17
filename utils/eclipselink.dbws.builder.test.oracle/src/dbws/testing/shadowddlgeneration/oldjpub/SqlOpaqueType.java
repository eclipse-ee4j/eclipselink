/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package dbws.testing.shadowddlgeneration.oldjpub;

public class SqlOpaqueType extends SqlType {

    public SqlOpaqueType(SqlName sqlName, boolean generateMe, SqlType parentType,
        SqlReflector reflector) {
        super(sqlName, OracleTypes.OPAQUE, generateMe, parentType, reflector);
    }

    /**
     * Determines if this Type represents an Opaque type.
     * <p/>
     */
    public boolean isOpaque() {
        // INFEASIBLE
        return true;
    }

}
