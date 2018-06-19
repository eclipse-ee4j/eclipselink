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

public class ElemInfo {

    public String elemTypeName;
    public String elemTypeOwner;
    public String elemTypeMod;
    public int elemTypeLength;
    public int elemTypePrecision;
    public int elemTypeScale;

    protected ElemInfo() {
    }

    public ElemInfo(AllCollTypes r) {
        elemTypeName = r.elemTypeName;
        elemTypeOwner = r.elemTypeOwner;
        elemTypeMod = r.elemTypeMod;
        elemTypeLength = r.elemLength;
        elemTypePrecision = r.elemPrecision;
        elemTypeScale = r.elemScale;
    }

    public String toString() {
        return elemTypeOwner + "," + elemTypeName + "," + elemTypeMod + "," + elemTypeLength + ","
            + elemTypePrecision + "," + elemTypeScale;
    }

}
