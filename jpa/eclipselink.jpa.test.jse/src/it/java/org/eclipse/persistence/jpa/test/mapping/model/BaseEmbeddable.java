/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     04/17/2020 - Will Dazey
//       - 561664: JoinColumn with same name as referencedColumnName
package org.eclipse.persistence.jpa.test.mapping.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class BaseEmbeddable {

    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(
            name = "BASE_PARENT_ID", 
            referencedColumnName = "BASE_PARENT_ID")
    private BaseParent parent;
}
