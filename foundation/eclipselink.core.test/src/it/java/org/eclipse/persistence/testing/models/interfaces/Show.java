/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.interfaces;

/**
 * A show is a program.
 */
public class Show implements Program, java.io.Serializable {
    public String name;
    public String description;
    public Number duration = Float.valueOf(0);

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Number getDuration() {
        return duration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public void setDuration(double duration) {
        this.duration = Double.valueOf(duration);
    }

    public void setDuration(float duration) {
        this.duration = Float.valueOf(duration);
    }

    @Override
    public void setDuration(Number duration) {
        this.duration = duration;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
