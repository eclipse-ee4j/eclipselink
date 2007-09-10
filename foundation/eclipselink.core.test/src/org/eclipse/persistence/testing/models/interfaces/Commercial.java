/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.interfaces;

/**
 * A commercial is a program.
 */
public class Commercial implements Program, java.io.Serializable {
    public String name;
    public String description;
    public Number duration;

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
