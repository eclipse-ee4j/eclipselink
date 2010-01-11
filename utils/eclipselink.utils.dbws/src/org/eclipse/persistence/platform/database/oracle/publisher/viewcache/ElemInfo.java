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
package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

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