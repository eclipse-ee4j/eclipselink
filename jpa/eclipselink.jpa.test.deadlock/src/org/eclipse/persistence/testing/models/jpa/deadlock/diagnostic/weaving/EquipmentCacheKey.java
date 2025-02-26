/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.weaving;

import java.util.ArrayList;
import java.util.List;


public interface EquipmentCacheKey {

    long getEntityId();

    String getId();
    void setId(String id);

    List<PortCacheKey> getPorts();
    void setPortCacheKeys(ArrayList<PortCacheKey> portCacheKeys);

    PortCacheKey removePort(int i);
    void addPortCacheKey(PortCacheKey p3);
}
