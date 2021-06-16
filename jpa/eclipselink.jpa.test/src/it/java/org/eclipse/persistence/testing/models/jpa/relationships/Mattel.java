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
//     03/26/2008-1.0M6 Guy Pelletier
//       - 211302: Add variable 1-1 mapping support to the EclipseLink-ORM.XML Schema
//     12/02/2010-2.2 Guy Pelletier
//       - 251554: ExcludeDefaultMapping annotation needed
package org.eclipse.persistence.testing.models.jpa.relationships;

import static jakarta.persistence.GenerationType.TABLE;

import java.util.List;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.ExcludeDefaultMappings;

@Entity
@Table(name="CMP3_MATTEL")
@ExcludeDefaultMappings
public class Mattel implements Manufacturer {
    private Integer id;
    private String name;

    // These are bogus attributes that normally would cause default mapping
    // generation. However the ExcludeDefaultMappings is for this class.
    private String ignoredBasic;
    private Item ignoredOneToOne;
    private Mattel ignoredVariableOneToOne;
    private List<Customer> ignoredOneToMany;

    public Mattel() {}

    @Id
    @GeneratedValue(strategy=TABLE, generator="MANUFACTURER_TABLE_GENERATOR")
    public Integer getId() {
        return id;
    }

    @Basic
    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIgnoredBasic() {
        return ignoredBasic;
    }

    public List<Customer> getIgnoredOneToMany() {
        return ignoredOneToMany;
    }

    public Item getIgnoredOneToOne() {
        return ignoredOneToOne;
    }

    public Mattel getIgnoredVariableOneToOne() {
        return ignoredVariableOneToOne;
    }

    public void setIgnoredBasic(String ignoredBasic) {
        this.ignoredBasic = ignoredBasic;
    }

    public void setIgnoredOneToMany(List<Customer> ignoredOneToMany) {
        this.ignoredOneToMany = ignoredOneToMany;
    }

    public void setIgnoredOneToOne(Item ignoredOneToOne) {
        this.ignoredOneToOne = ignoredOneToOne;
    }

    public void setIgnoredVariableOneToOne(Mattel ignoredVariableOneToOne) {
        this.ignoredVariableOneToOne = ignoredVariableOneToOne;
    }
}
