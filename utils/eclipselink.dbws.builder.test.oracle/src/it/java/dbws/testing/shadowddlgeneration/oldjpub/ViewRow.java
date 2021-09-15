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

public interface ViewRow  {

    boolean equals(String key, Object value);

    boolean isAllCollTypes();

    boolean isAllMethodParams();

    boolean isAllMethodResults();

    boolean isAllObjects();

    boolean isAllQueueTables();

    boolean isAllSynonyms();

    boolean isAllTypeAttrs();

    boolean isAllTypeMethods();

    boolean isAllTypes();

    boolean isUserArguments();

    boolean isAllArguments();

    boolean isSingleColumnViewRow();
}
