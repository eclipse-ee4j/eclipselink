/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.weaving;

// J2SE imports

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Transient;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;

import java.beans.PropertyChangeListener;

import static jakarta.persistence.GenerationType.TABLE;
@Entity
@Table(name="SIMPLE_UICT")
public class SimpleObjectWithUserImpldChangeTracking implements ChangeTracker{

    protected PropertyChangeListener pcl = null;
    protected Integer id; // PK

    public SimpleObjectWithUserImpldChangeTracking() {
    }

    @Override
    @Transient
    public PropertyChangeListener _persistence_getPropertyChangeListener() {
        return pcl;
    }
    @Override
    public void _persistence_setPropertyChangeListener(PropertyChangeListener pcl) {
        this.pcl = pcl;
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="SIMPLE_TABLE_GENERATOR")
    @TableGenerator(
        name="SIMPLE_TABLE_GENERATOR",
        table="SIMPLE_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="SIMPLE_SEQ")
    @Column(name="ID")
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
}
