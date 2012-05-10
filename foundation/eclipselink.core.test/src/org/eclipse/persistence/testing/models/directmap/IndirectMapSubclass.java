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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.directmap;

import org.eclipse.persistence.indirection.IndirectMap;
import java.util.Map;

/**
 * Bug 3945357
 * Subclass of IndirectMap designed to test that transparent indirection can work
 * with custom map classes
 */
public class IndirectMapSubclass extends IndirectMap {

    /**
     * PUBLIC:
     * Construct a new, empty IndirectMap with a default
     * capacity and load factor.
     */
    public IndirectMapSubclass() {
        super();
    }

    /**
     * PUBLIC:
     * Construct a new, empty IndirectMap with the specified initial capacity
     * and default load factor.
     *
     * @param   initialCapacity   the initial capacity of the hashtable
     */
    public IndirectMapSubclass(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * PUBLIC:
     * Construct a new, empty IndirectMap with the specified initial
     * capacity and load factor.
     *
     * @param      initialCapacity   the initial capacity of the hashtable
     * @param      loadFactor        a number between 0.0 and 1.0
     * @exception  IllegalArgumentException  if the initial capacity is less
     *               than or equal to zero, or if the load factor is less than
     *               or equal to zero
     */
    public IndirectMapSubclass(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * PUBLIC:
     * Construct a new IndirectMap with the same mappings as the given Map.
     * The IndirectMap is created with a capacity of twice the number of entries
     * in the given Map or 11 (whichever is greater), and a default load factor, which is 0.75.
     * @param m a map containing the mappings to use
     */
    public IndirectMapSubclass(Map m) {
        super(m);
    }
}
