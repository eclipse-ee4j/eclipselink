/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Gordon Yorke - Initial Contribution
package org.eclipse.persistence.testing.models.jpa.xml.cacheable;

import java.util.ArrayList;
import java.util.List;

public class ProtectedRelationshipsEntity {

    protected int id;
    protected String name;

    protected CacheableTrueEntity cacheableFalse;

    protected CacheableTrueEntity cacheableProtected;

    protected List<CacheableTrueEntity> cacheableProtecteds;

    protected List<CacheableTrueEntity> cacheableProtecteds2;

    protected List<String> elementCollection;

    protected List<String> basicCollection;

    public ProtectedRelationshipsEntity() {
        cacheableProtecteds = new ArrayList<>();
        cacheableProtecteds2 = new ArrayList<>();
        elementCollection = new ArrayList<>();
        basicCollection = new ArrayList<>();
    }

}
