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
//     Matt MacIvor - 2.5 - initial implementation
package org.eclipse.persistence.jaxb;


/**
 * Represents an AttributeNode of an entity graph.
 */
public interface AttributeNode {

    /*
     * returns the name of the referencing attribute.
     */
    public String getAttributeName();
}
