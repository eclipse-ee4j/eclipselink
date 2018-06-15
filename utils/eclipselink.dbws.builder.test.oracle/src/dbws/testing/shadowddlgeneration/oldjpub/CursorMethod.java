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
package  dbws.testing.shadowddlgeneration.oldjpub;

/**
 * A Method returns REF CURSOR
 */
public interface CursorMethod {

    public TypeClass getReturnEleType();

    public boolean isSingleCol();

    public String singleColName();

    public boolean returnBeans();

    public boolean returnResultSet();
}
