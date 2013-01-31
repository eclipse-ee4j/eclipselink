/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09 Jan 2013-2.5 Gordon Yorke
 *       - 397772: JPA 2.1 Entity Graph Support
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa;

import java.util.Map;

import javax.persistence.AttributeNode;
import javax.persistence.Subgraph;


/**
 * Concrete JPA AttributeNode class. For this implementation the AttributeNode includes information from live
 * AttributeItems.
 */
public class AttributeNodeImpl<X> implements AttributeNode<X>{
    
    
    protected String currentAttribute;

    protected AttributeNodeImpl(){
    }

    protected AttributeNodeImpl(String attribute){
        this.currentAttribute = attribute;
    }

    public String getAttributeName() {
        return currentAttribute;
    }

    public Map<Class, Subgraph> getSubgraphs() {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<Class, Subgraph> getKeySubgraphs() {
        // TODO Auto-generated method stub
        return null;
    }

}
