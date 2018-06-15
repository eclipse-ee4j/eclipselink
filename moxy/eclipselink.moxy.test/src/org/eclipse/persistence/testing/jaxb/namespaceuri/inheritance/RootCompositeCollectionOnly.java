/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - December 2011
package org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root", namespace="rootNamespace")
public class RootCompositeCollectionOnly {

    public List<BaseType > baseTypeList;

    public RootCompositeCollectionOnly(){
        baseTypeList = new ArrayList<BaseType>();
    }

    public boolean equals(Object obj){
        if(!(obj instanceof RootCompositeCollectionOnly)){
            return false;
        }

        if(!compareLists(baseTypeList, ((RootCompositeCollectionOnly)obj).baseTypeList)){
            return false;
        }
        return true;
    }

    private boolean compareLists(List list1, List list2){
        if(list1.size() != list2.size()){
            return false;
        }
        if(!list1.containsAll(list2)){
            return false;
        }
        if(!list2.containsAll(list1)){
            return false;
        }
        return true;
    }
}
