/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
