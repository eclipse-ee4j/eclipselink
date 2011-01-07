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

import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.persistence.testing.framework.*;

/**
 * Measure the concurrency of ConcurrentHashMap.
 */
public class ConcurrentHashMapPutConcurrentTest extends ConcurrentPerformanceComparisonTest {
    protected ConcurrentHashMap map;
    
    public ConcurrentHashMapPutConcurrentTest() {
        setDescription("Measure the concurrency of ConcurrentHashMap.");
    }
    
    public void setup() {
        super.setup();
        map = new ConcurrentHashMap(10);
        for (int index = 0; index < 10; index++) {
            map.put(new Integer(index), new Integer(index));
        }
    }
    
    public void runTask() throws Exception {
        Integer value = (Integer)this.map.put(new Integer(5), new Integer(5));
    }
}
