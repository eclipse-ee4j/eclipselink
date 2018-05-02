/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Radek Felcman - May 2018
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import org.eclipse.persistence.oxm.annotations.XmlVariableNode;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "root")
public class RootNull {

    @XmlVariableNode("key")
    public List<ChildNull> childNulls = new ArrayList<>();

    @Override
    public boolean equals(Object obj){
        if(obj instanceof RootNull){
            return childNulls.equals(((RootNull)obj).childNulls);
        }
        return false;
    }
}
