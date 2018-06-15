/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2016 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     10/19/2016 - 2.6 Will Dazey
//       - 506168 : Nested Embeddables AttributeOverride Test
package org.eclipse.persistence.jpa.embeddable.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

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
