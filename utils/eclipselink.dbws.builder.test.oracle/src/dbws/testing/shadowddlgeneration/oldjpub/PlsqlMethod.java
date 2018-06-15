/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

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
