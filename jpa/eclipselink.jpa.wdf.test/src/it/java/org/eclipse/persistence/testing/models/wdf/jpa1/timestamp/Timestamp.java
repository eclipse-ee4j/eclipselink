/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "TMP_TIMESTAMP")
public class Timestamp {

    @Id
    private Long id;

    @PrePersist
    public void prePersist() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        id = System.currentTimeMillis();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
