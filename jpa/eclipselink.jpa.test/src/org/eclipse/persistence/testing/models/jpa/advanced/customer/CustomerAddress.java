/*******************************************************************************
 * Copyright (c) 2014, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     jlamande    - Initial API and implementation
 *     Tomas Kraus - EclipseLink jUnit tests integration
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.customer;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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
