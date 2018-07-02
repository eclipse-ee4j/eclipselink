/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Mike Norman - May 06 2008, created DBWS tools package

package org.eclipse.persistence.tools.dbws;

import static org.eclipse.persistence.tools.dbws.NamingConventionTransformer.ElementStyle.ELEMENT;
import static org.eclipse.persistence.tools.dbws.NamingConventionTransformer.ElementStyle.NONE;

public class DefaultNamingConventionTransformer implements NamingConventionTransformer {

    protected NamingConventionTransformer nextTransformer = null;

    public NamingConventionTransformer getNextTransformer() {
        return nextTransformer;
    }
    public void setNextTransformer(NamingConventionTransformer nextTransformer) {
        this.nextTransformer = nextTransformer;
    }

    protected boolean isDefaultTransformer() {
        return false;
    }

    public String generateSchemaAlias(String tableName) {
        NamingConventionTransformer nct = getNextTransformer();
        if (nct == null) {
            return tableName;
        }
        else {
            return nct.generateSchemaAlias(tableName);
        }
    }

    public String generateElementAlias(String originalElementName) {
        NamingConventionTransformer nct = getNextTransformer();
        if (nct == null) {
            return originalElementName;
        }
        else {
            return nct.generateElementAlias(originalElementName);
        }
    }

    public ElementStyle styleForElement(String elementName) {
        return ELEMENT;
    }

    public String getOptimisticLockingField() {
        NamingConventionTransformer nct = getNextTransformer();
        if (nct == null) {
            return DEFAULT_OPTIMISTIC_LOCKING_FIELD;
        }
        else {
            if (nct.styleForElement(DEFAULT_OPTIMISTIC_LOCKING_FIELD) == NONE) {
                return null;
            }
            return nct.getOptimisticLockingField();
        }
    }
}
