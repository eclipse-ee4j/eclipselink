/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.weaving;

// J2SE imports
import java.beans.PropertyChangeListener;

// J2EE persistence imports
import static jakarta.persistence.GenerationType.TABLE;
import jakarta.persistence.*;

import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
@Entity
@Table(name="SIMPLE_UICT")
public class SimpleObjectWithUserImpldChangeTracking implements ChangeTracker{

    protected PropertyChangeListener pcl = null;
    protected Integer id; // PK

    public SimpleObjectWithUserImpldChangeTracking() {
    }

    @Transient
    public PropertyChangeListener _persistence_getPropertyChangeListener() {
        return pcl;
    }
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
