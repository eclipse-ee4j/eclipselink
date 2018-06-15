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
//  - Denise Smith - September 2013
package org.eclipse.persistence.testing.jaxb.xmladapter.noarg;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class Root {

    public String name;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(MyAdapter.class)
    public Something theThing;

    public boolean equals(Object obj){
        if(obj instanceof Root){
            Root compareObj = (Root)obj;

            return name.equals(compareObj.name) &&
            ((theThing == null && compareObj.theThing == null) ||(theThing != null && theThing.equals(compareObj.theThing)));
        }
        return false;
    }
}
