/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
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
//  - Denise Smith - September 2013
package org.eclipse.persistence.testing.jaxb.xmladapter.noarg;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class Root {

    public String name;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(MyAdapter.class)
    public Something theThing;

    public boolean equals(Object obj){
        if(obj instanceof Root compareObj){

            return name.equals(compareObj.name) &&
            ((theThing == null && compareObj.theThing == null) ||(theThing != null && theThing.equals(compareObj.theThing)));
        }
        return false;
    }
}
