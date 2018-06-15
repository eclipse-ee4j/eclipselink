/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     10/25/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
package org.eclipse.persistence.testing.models.jpa22.advanced;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import org.eclipse.persistence.testing.models.jpa22.advanced.enums.Health;
import org.eclipse.persistence.testing.models.jpa22.advanced.enums.Level;

@Embeddable
public class RunnerInfo {
    @Column(name="R_LEVEL")
    protected Level level;

    @Column(name="R_HEALTH")
    protected Health health;

    @Embedded
    protected RunnerStatus status;

    public RunnerInfo() {}

    public Health getHealth() {
        return health;
    }

    public Level getLevel() {
        return level;
    }

    public RunnerStatus getStatus() {
        return status;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setHealth(Health health) {
        this.health = health;
    }

    public void setStatus(RunnerStatus status) {
        this.status = status;
    }
}
