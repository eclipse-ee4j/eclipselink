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

public interface ViewRow  {

    public boolean equals(String key, Object value);

    public boolean isAllCollTypes();

    public boolean isAllMethodParams();

    public boolean isAllMethodResults();

    public boolean isAllObjects();

    public boolean isAllQueueTables();

    public boolean isAllSynonyms();

    public boolean isAllTypeAttrs();

    public boolean isAllTypeMethods();

    public boolean isAllTypes();

    public boolean isUserArguments();

    public boolean isAllArguments();

    public boolean isSingleColumnViewRow();
}
