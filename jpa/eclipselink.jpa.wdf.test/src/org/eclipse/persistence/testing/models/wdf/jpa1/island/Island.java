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

package org.eclipse.persistence.testing.models.wdf.jpa1.island;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "TMP_ISLAND")
/* Test PseudoInheritance without subclasses */
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Cacheable(true)
// FIXME invalid after 2 seconds
public class Island {
    @Id
    @GeneratedValue
    private int id;

    private String name;

    public Island(String aName) {
        name = aName;
    }

    public Island() {

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
