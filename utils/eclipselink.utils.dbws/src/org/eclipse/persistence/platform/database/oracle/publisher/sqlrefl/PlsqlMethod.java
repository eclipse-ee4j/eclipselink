/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

/**
 * A Method provides information about a single method of a type.
 */
public class PlsqlMethod extends ProcedureMethod {
    /**
     * Construct a Method
     */
    public PlsqlMethod(String name, String overloadNumber, int modifiers, TypeClass returnType,
        TypeClass[] parameterTypes, String[] parameterNames, int[] parameterModes,
        boolean[] parameterDefaults, int paramLen) {
        super(name, overloadNumber, modifiers, returnType, parameterTypes, parameterNames,
            parameterModes, parameterDefaults, paramLen);
    }
}
