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
//     ailitchev - 2010.01.08 Bug 299147 - em.find isolated read-only entity throws exception
package org.eclipse.persistence.testing.models.jpa.advanced;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.ReadOnly;
import org.eclipse.persistence.config.CacheIsolationType;

@Entity
@Table(name="CMP3_READONLY_ISOLATED")
@ReadOnly
@Cache(isolation=CacheIsolationType.ISOLATED)
public class ReadOnlyIsolated  {
    private Integer id;
    private String code;

    public ReadOnlyIsolated() {}

    public String getCode() {
        return code;
    }

    @Id
    public Integer getId() {
        return id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
