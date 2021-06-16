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

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class LawnMower extends PowerTool {
    /**
     * Note: this class is currently used to test a processing error only and
     * does not define a table in the InheritedTableManager for further testing
     * in the InheritedModelTestSuite. Expanding this class is ok, but do not
     * add an explicit access type (leave it to discover the access type as
     * FIELD)
     */

    @Id
    @GeneratedValue
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
