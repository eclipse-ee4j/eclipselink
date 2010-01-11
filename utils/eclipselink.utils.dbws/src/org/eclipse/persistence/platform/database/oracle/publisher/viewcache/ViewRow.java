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