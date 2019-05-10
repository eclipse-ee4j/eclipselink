/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     05/30/2008-1.0M8 Guy Pelletier
//       - 230213: ValidationException when mapping to attribute in MappedSuperClass
//     06/20/2008-1.0 Guy Pelletier
//       - 232975: Failure when attribute type is generic
//     08/11/2010-2.2 Guy Pelletier
//       - 312123: JPA: Validation error during Id processing on parameterized generic OneToOne Entity relationship from MappedSuperclass
package org.eclipse.persistence.testing.models.jpa21.advanced.inherited;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Table(name="JPA21_BLUE")
public class Blue extends Beer<BigDecimal, Float, Blue> implements Bluish<BigInteger, BigInteger>, Cloneable  {
    private BigInteger uniqueKey;
    public Blue() {}

    // This class is intentionally left with no annotations to test that
    // it picks us the access type from the mapped superclass.

    public boolean equals(Object anotherBlue) {
        if (anotherBlue.getClass() != Blue.class) {
            return false;
        }

        return (getId().equals(((Blue)anotherBlue).getId()));
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public BigInteger getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(BigInteger uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
