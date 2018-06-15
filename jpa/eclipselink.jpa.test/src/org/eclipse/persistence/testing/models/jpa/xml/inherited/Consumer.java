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
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

public class Consumer  {
    public int post_load_count = 0;
    public int post_persist_count = 0;
    public int post_remove_count = 0;
    public int post_update_count = 0;
    public int pre_persist_count = 0;
    public int pre_remove_count = 0;
    public int pre_update_count = 0;

    public Consumer() {}

    public void postLoad() {
        ++post_load_count;
    }

    public void postPersist() {
        ++post_persist_count;
    }

    public void postRemove() {
        ++post_remove_count;
    }

    public void postUpdate() {
        ++post_update_count;
    }

    public void prePersist() {
        ++pre_persist_count;
    }

    public void preRemove() {
        ++pre_remove_count;
    }

    public void preUpdate() {
        ++pre_update_count;
    }
}
