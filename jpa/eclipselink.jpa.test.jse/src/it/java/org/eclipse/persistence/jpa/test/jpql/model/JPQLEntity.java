/*
 * Copyright (c) 2018, 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
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
//     02/20/2018-2.7 Will Dazey
//       - 531062: Incorrect expression type created for CollectionExpression
package org.eclipse.persistence.jpa.test.jpql.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "parentTable")
public class JPQLEntity {

    @EmbeddedId
    @AttributeOverrides({ @AttributeOverride(name = "value1.value", column = @Column(name = "value1")),
            @AttributeOverride(name = "value2.value", column = @Column(name = "value2")) })
    private JPQLEntityId id;

    @Column(name = "string1")
    private String string1;
    
    @Column(name = "string2")
    private String string2;

}
