/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     jlamande    - Initial API and implementation
//     Tomas Kraus - EclipseLink jUnit tests integration
package org.eclipse.persistence.testing.models.jpa.advanced.customer;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Abstract customer's address entity.
 */
@Entity
@Table(name="CMP3_AD_CU_ADDRESS")
@Inheritance
@DiscriminatorColumn(name="TYPE",length=1)
@SequenceGenerator(name="CMP3_AD_CU_ADDRESS_ID_SEQ")
public abstract class CustomerAddress {

    /** Customer's address primary key. */
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CMP3_AD_CU_ADDRESS_ID_SEQ")
    @Column(name="ID")
    private long id;

    /**
     * Get street name.
     * @return Street name attribute value.
     */
    public abstract String getStreet();

    /**
     * Set street name.
     * @param street Street name to be set.
     */
    public abstract void setStreet(final String street);

}
