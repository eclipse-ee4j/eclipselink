/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.optimisticlocking;

public class Knitting extends AbstractVideogameObject implements Skill {

    public Knitting() {
        this(null, null);
    }

    public Knitting(String name, String description) {
        super(name, description);
    }

    /**
     * The behaviour method
     */
    public boolean isCool() {
        return false;
    }

}
