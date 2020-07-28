/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     06/22/2010-2.2 Guy Pelletier
//       - 308729: Persistent Unit deployment exception when mappedsuperclass has no annotations but has lifecycle callbacks
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

@MappedSuperclass
public class PowerTool {

    /**
     * Note: this class is currently used to test a processing error (when a
     * lifecycle callback method annotation is incorrectly used to determine
     * a PROPERTY access type)
     *
     * Please do not change this class, it should only contain the one mapped
     * lifecycle method below.
     */

    public static int POWER_TOOL_PRE_PERSIST_COUNT = 0;

    @PrePersist
    public void forSale() {
        POWER_TOOL_PRE_PERSIST_COUNT++;
    }
}
