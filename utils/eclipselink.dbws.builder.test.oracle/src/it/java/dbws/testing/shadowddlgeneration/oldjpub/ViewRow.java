/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
