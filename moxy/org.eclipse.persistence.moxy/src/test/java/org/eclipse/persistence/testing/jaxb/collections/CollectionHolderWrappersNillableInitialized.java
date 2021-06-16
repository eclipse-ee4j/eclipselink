/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 27 January 2012 - 2.3.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.collections;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CollectionHolderWrappersNillableInitialized extends CollectionHolderWrappersNillable {

    public static final List TEST_LIST = new ArrayList();

    public CollectionHolderWrappersNillableInitialized() {
        collection1 = TEST_LIST;
        collection2 = TEST_LIST;
        collection3 = TEST_LIST;
        collection4 = TEST_LIST;
        collection5 = TEST_LIST;
        collection6 = TEST_LIST;
        collection7 = TEST_LIST;
    }

}
