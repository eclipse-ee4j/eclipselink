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

public class WrapperMethodMetadata {

    protected String name;
    protected String[] paramTypes;
    protected String[] paramNames;
    protected String returnType;

    public WrapperMethodMetadata(String name, String[] paramTypes, String[] paramNames,
        String returnType) {
        this.name = name;
        this.paramTypes = paramTypes;
        this.paramNames = paramNames;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public String[] getParamTypes() {
        return paramTypes;
    }

    public String[] getParamNames() {
        return paramNames;
    }

    public String getReturnType() {
        return returnType;
    }
}
