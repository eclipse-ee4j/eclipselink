/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     06/22/2010-2.2 Guy Pelletier
//       - 308729: Persistent Unit deployment exception when mappedsuperclass has no annotations but has lifecycle callbacks
package org.eclipse.persistence.testing.models.jpa.inherited;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

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
