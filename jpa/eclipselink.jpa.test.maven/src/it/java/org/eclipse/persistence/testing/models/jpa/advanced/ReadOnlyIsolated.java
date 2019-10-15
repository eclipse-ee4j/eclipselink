/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     ailitchev - 2010.01.08 Bug 299147 - em.find isolated read-only entity throws exception
package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
