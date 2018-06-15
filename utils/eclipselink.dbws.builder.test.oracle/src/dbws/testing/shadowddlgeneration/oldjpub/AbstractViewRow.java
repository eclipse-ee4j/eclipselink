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

public abstract class AbstractViewRow implements ViewRow {

    public boolean equals(String key, Object value) {
        return false;
    }

    public boolean isAllArguments() {
        return false;
    }

    public boolean isAllCollTypes() {
        return false;
    }

    public boolean isAllMethodParams() {
        return false;
    }

    public boolean isAllMethodResults() {
        return false;
    }

    public boolean isAllObjects() {
        return false;
    }

    public boolean isAllQueueTables() {
        return false;
    }

    public boolean isAllSynonyms() {
        return false;
    }

    public boolean isAllTypeAttrs() {
        return false;
    }

    public boolean isAllTypeMethods() {
        return false;
    }

    public boolean isAllTypes() {
        return false;
    }

    public boolean isSingleColumnViewRow() {
        return false;
    }

    public boolean isUserArguments() {
        return false;
    }

}
