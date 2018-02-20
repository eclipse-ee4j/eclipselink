/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/20/2018-2.7 Will Dazey
 *       - 531062: Incorrect expression type created for CollectionExpression
 ******************************************************************************/
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
