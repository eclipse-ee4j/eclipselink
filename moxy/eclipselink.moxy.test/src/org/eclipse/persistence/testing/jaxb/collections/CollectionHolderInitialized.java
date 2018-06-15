/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.collections;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CollectionHolderInitialized extends CollectionHolder{

    public CollectionHolderInitialized(){
        collection1 = new ArrayList();
        collection2 = new ArrayList();
        collection3 = new ArrayList();
        collection4 = new ArrayList();
        collection5 = new ArrayList();
        collection6 = new ArrayList();
        collection7 = new ArrayList();
        collection8 = new ArrayList();
        collection9 = new ArrayList();
        collection10 = new HashMap();
        collection11 = new ArrayList();
        collection12 = new ArrayList();
        collection13 = new ArrayList();
        collection14 = new ArrayList();
    }

}
