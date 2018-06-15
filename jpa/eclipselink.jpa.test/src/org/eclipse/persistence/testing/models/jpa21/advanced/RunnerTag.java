/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     07/16/2013-2.5.1 Guy Pelletier
//       - 412384: Applying Converter for parameterized basic-type for joda-time's DateTime does not work
package org.eclipse.persistence.testing.models.jpa21.advanced;

public class RunnerTag {
    protected String description;

    public RunnerTag(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setTag(String description) {
        this.description = description;
    }
}
