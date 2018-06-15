/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
    public Number duration = new Float(0);

    public String getDescription() {
        return description;
    }

    public Number getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDuration(double duration) {
        this.duration = new Double(duration);
    }

    public void setDuration(float duration) {
        this.duration = new Float(duration);
    }

    public void setDuration(Number duration) {
        this.duration = duration;
    }

    public void setName(String name) {
        this.name = name;
    }
}
