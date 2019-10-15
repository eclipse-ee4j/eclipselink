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
//     tware - add aliasing to AggregateNodes
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
