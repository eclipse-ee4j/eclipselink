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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.inheritance;

import org.eclipse.persistence.testing.models.inheritance.Insect;

public class GrassHopper extends Insect {
    protected Integer gh_ID;
    protected Integer gh_maximumJump;

    public Integer getGh_ID() {
        return gh_ID;
    }

    public Integer getGh_maximumJump() {
        return gh_maximumJump;
    }

    public void setGh_ID(Integer param1) {
        gh_ID = param1;
    }

    public void setGh_maximumJump(Integer param1) {
        gh_maximumJump = param1;
    }
}
