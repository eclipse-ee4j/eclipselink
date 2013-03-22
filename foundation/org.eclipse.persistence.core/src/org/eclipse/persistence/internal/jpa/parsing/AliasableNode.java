/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - add aliasing to AggregateNodes
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.parsing;

/**
 * This interface should be implemented by any node that can be aliased in the select clause.
 * 
 * It will be used to help the select node build a list of aliased expressions
 * @author tware
 *
 */
public interface AliasableNode {

    public String getAlias();
    
    public void setAlias(String alias);
}
