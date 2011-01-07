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
package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

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