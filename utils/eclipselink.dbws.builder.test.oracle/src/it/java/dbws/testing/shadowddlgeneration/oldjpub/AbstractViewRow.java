/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

public abstract class AbstractViewRow implements ViewRow {

    @Override
    public boolean equals(String key, Object value) {
        return false;
    }

    @Override
    public boolean isAllArguments() {
        return false;
    }

    @Override
    public boolean isAllCollTypes() {
        return false;
    }

    @Override
    public boolean isAllMethodParams() {
        return false;
    }

    @Override
    public boolean isAllMethodResults() {
        return false;
    }

    @Override
    public boolean isAllObjects() {
        return false;
    }

    @Override
    public boolean isAllQueueTables() {
        return false;
    }

    @Override
    public boolean isAllSynonyms() {
        return false;
    }

    @Override
    public boolean isAllTypeAttrs() {
        return false;
    }

    @Override
    public boolean isAllTypeMethods() {
        return false;
    }

    @Override
    public boolean isAllTypes() {
        return false;
    }

    @Override
    public boolean isSingleColumnViewRow() {
        return false;
    }

    @Override
    public boolean isUserArguments() {
        return false;
    }

}
