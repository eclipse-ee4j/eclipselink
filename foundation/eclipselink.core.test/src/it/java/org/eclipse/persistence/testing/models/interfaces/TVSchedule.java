/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.interfaces;

import java.io.*;
import java.util.*;

/**
 * Schedule has a collection of TVSegments which are Commercials or Shows.
 * and a broadcastor which is a Network, all relationships are done through interfaces to test them.
 */
public class TVSchedule implements Serializable {
    public Double id;
    public Vector segments = new Vector();
    public Broadcastor broadcastor;
    public Date timeSpot;

    public TVSchedule() {
        Calendar c = Calendar.getInstance();
        c.set(1998, 4, 15);
        c.set(Calendar.MILLISECOND, 0);
        this.timeSpot = c.getTime();
    }

    public String toString() {
        return "TVSchedule";
    }
}
