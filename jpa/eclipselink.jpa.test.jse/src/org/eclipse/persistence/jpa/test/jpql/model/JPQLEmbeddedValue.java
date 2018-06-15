/*
 * Copyright (c) 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/20/2018-2.7 Will Dazey
//       - 531062: Incorrect expression type created for CollectionExpression
package org.eclipse.persistence.jpa.test.jpql.model;

import javax.persistence.Embeddable;

@Embeddable
public class JPQLEmbeddedValue {
    private String value;

    public JPQLEmbeddedValue() { }

    public JPQLEmbeddedValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
