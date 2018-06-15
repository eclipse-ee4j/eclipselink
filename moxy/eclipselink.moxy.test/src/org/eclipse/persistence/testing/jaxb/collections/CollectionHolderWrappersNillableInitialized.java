/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 27 January 2012 - 2.3.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.collections;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

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
