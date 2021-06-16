/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.models.jpa.cascadedeletes;

import static jakarta.persistence.CascadeType.ALL;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import org.eclipse.persistence.annotations.PrivateOwned;

@Entity
public class MachineState {

    @Id
    private long id;
@PrivateOwned
    @OneToMany(cascade = ALL)
    private List<ThreadInfo> threads;

    public MachineState() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<ThreadInfo> getThreads() {
        return threads;
    }

    public void setThreads(List<ThreadInfo> threads) {
        this.threads = threads;
    }
}
