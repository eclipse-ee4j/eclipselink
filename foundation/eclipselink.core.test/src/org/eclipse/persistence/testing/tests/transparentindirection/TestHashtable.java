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
package org.eclipse.persistence.testing.tests.transparentindirection;

import java.util.*;

/**
 * test a container that is a subclass of Hashtable
 */
public class TestHashtable extends Hashtable {

    /**
     * TestHashtable constructor comment.
     */
    public TestHashtable() {
        super();
    }

    /**
     * TestHashtable constructor comment.
     * @param initialCapacity int
     */
    public TestHashtable(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * TestHashtable constructor comment.
     * @param initialCapacity int
     * @param loadFactor float
     */
    public TestHashtable(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
}
