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
package org.eclipse.persistence.testing.models.inheritance;

public class LadyBug extends Insect {
    protected Integer lb_ID;
    protected Integer lb_numberOfSpots;

    public Integer getLb_ID() {
        return lb_ID;
    }

    public Integer getLb_numberOfSpots() {
        return lb_numberOfSpots;
    }

    public void setLb_ID(Integer param1) {
        lb_ID = param1;
    }

    public void setLb_numberOfSpots(Integer param1) {
        lb_numberOfSpots = param1;
    }
}
