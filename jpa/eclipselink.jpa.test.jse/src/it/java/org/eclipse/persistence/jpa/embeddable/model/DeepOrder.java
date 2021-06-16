/*
 * Copyright (c) 2016, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     10/19/2016 - 2.6 Will Dazey
//       - 506168 : Nested Embeddables AttributeOverride Test
package org.eclipse.persistence.jpa.embeddable.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class DeepOrder {

    @EmbeddedId
    @AttributeOverride(name = "orderpk.billingAddress.zipcode.zip", column = @Column(name="deepOverride"))
    private DeepOrderPK id;

    public DeepOrder() { }

    public DeepOrderPK getId() {
        return id;
    }

    public void setId(DeepOrderPK id) {
        this.id = id;
    }
}
