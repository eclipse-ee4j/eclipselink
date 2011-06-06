/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.framework.wdf.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Serializable version of org.junit.runner.Result.
 */
public class SerializableResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private final int count;
    @SuppressWarnings("unused")
    private final int ignoreCount;
    @SuppressWarnings("unused")
    private final List<SerializableFailure> failures;
    @SuppressWarnings("unused")
    private final long runTime;
    
    private SerializableResult(int cnt, int ign, List<SerializableFailure> fls, long rt) {
        count = cnt;
        ignoreCount = ign;
        failures = fls;
        runTime = rt;
    }
    
    /**
     * Create a SerializableResult object from an org.junit.runner.Result object.
     * @param other the org.junit.runner.Result object to be converted
     * @return the SerializableResult object created from an org.junit.runner.Result object
     */
    @SuppressWarnings("unchecked")
    public static SerializableResult create(Result other) {
        final List<SerializableFailure> failures;
        
        if (other.getFailures() != null) {
            failures = new ArrayList<SerializableFailure>();
            for(Failure failure : other.getFailures()) {
                failures.add(SerializableFailure.create(failure));
            }
        } else {
            failures = Collections.EMPTY_LIST;
        }
        
        return new SerializableResult(other.getFailureCount(), other.getIgnoreCount(), failures, other.getRunTime());
        
    }
    
    public Result restore() {
        return new Result(); // FIXME don't know how to restore the result 
        
    }
    

}
