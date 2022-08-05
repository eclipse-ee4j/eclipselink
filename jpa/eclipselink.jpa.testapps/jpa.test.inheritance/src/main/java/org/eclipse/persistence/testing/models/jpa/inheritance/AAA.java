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
//     01/26/2011-2.3 Guy Pelletier
//       - 307664:  Lifecycle callbacks not called for object from IndirectSet
package org.eclipse.persistence.testing.models.jpa.inheritance;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.TypeConverter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="CMP3_AAA")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="DTYPES", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("1")
public  class AAA {
    String id;
    String foo;

    Set<DDD> ddds;
    private Set<String> stringSet;

    public AAA() {
        ddds = new HashSet<>();
    }

    AAA(String id) {
        this();
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CMP3_AAA_GENERATOR")
    @TableGenerator(name = "CMP3_AAA_GENERATOR", table = "CMP3_AAA_SEQ")
    @Convert("stringToInt")
    @TypeConverter(name="stringToInt", dataType=Integer.class, objectType=String.class)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    @OneToMany(mappedBy="aaa", cascade=CascadeType.ALL)
    public Set<DDD> getDdds() {
        return ddds;
    }

    public void setDdds(Set<DDD> ddds) {
        this.ddds = ddds;
    }

    @ElementCollection(targetClass=String.class)
    @CollectionTable(name="AAA_STRINGSET")
    public Set<String> getStringSet() {
        return stringSet;
    }

    public void setStringSet(Set<String> stringSet) {
        this.stringSet = stringSet;
    }
}
