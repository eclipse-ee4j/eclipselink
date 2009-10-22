/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import javax.persistence.Basic;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.ReadOnly;

@ReadOnly
@Entity
@Table(name = "TMP_READONLY")
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.INTEGER)
@DiscriminatorValue("-1")
public class ReadOnlyEntity {

    @Id
    protected int id;

    @Basic
    protected String name;

    @Version
    protected int version;

    public ReadOnlyEntity() {

    }

    public ReadOnlyEntity(int aId, String aName) {
        id = aId;
        name = aName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
