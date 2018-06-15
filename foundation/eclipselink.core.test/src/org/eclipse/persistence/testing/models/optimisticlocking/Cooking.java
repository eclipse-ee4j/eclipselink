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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.optimisticlocking;

public class Cooking extends AbstractVideogameObject implements Skill {

    public Cooking() {
        this(null, null);
    }

    public Cooking(String name, String description) {
        super(name, description);
    }

    /**
     * The behaviour method
     */
    public boolean isCool() {
        return true;
    }

}
