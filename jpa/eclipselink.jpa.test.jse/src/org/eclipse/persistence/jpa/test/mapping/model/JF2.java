/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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
//     12/02/2019-3.0 - Alexandre Jacob
//       - 541046: @JoinFetch doesn't work with default value
package org.eclipse.persistence.jpa.test.mapping.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;

@Entity
public class JF2 {

    @Id
    private int id;

    @ManyToOne
    @JoinFetch(JoinFetchType.OUTER)
    private JF3 jf3;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public JF3 getJf3() {
        return jf3;
    }

    public void setJf3(JF3 jf3) {
        this.jf3 = jf3;
    }
}
