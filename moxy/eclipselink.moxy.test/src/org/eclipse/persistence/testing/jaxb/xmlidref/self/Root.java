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
// Denise Smith - October 2013
package org.eclipse.persistence.testing.jaxb.xmlidref.self;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Root {

    public List<Item> items;

    public boolean equals(Object obj){
        if(obj instanceof Root){
            Root r = (Root)obj;
            if(items.size() != r.items.size()){
                return false;
            }
            return items.equals(r.items);
        }
        return false;
    }
}


