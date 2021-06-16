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
package org.eclipse.persistence.testing.models.directmap;

import java.util.*;

/**
 * Object that will hold our mappings.
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date March 04, 2003
 */
public class DirectMapMappings {
    public int id;
    public Hashtable directMap;
    public Hashtable directMapForBatchRead;
    public Hashtable indirectionDirectMap;
    public Hashtable blobDirectMap;
    public HashMap directHashMap;

    public DirectMapMappings() {
        directMap = new Hashtable();
        directMapForBatchRead = new Hashtable();
        indirectionDirectMap = new Hashtable();
        blobDirectMap = new Hashtable();
        directHashMap = new HashMap();
    }
}
