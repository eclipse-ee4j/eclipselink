/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.performance.java;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Measure the concurrency of Hashtable.
 */
public class HashtablePutConcurrentTest extends ConcurrentPerformanceComparisonTest {
    protected Hashtable map;
    protected Integer[] keys = new Integer[100];
    
    public HashtablePutConcurrentTest() {
        setDescription("Measure the concurrency of Hashtable.");
        for (int index = 0; index < 100; index ++) {
            this.keys[index] = new Integer(index);
        }
    }
    
    public void setup() {
        super.setup();
        map = new Hashtable(100);
        for (int index = 0; index < 100; index++) {
            map.put(new Integer(index), new Integer(index));
        }
    }
    
    public void runTask() throws Exception {
        for (int index = 0; index < 100; index ++) {
            Integer value = (Integer)this.map.put(this.keys[index],this.keys[index]);
        }
    }
}
