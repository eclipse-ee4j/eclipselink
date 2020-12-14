/*******************************************************************************
 * Copyright (c) 2020 IBM and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     IBM - ConcurrencyUtil call of ThreadMXBean.getThreadInfo() needs doPriv
 ******************************************************************************/
package org.eclipse.persistence.internal.security;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.security.PrivilegedExceptionAction;

public class PrivilegedGetThreadInfo implements PrivilegedExceptionAction<ThreadInfo[]> {
    private final long[] ids;
    private final int maxDepth;
    
    public PrivilegedGetThreadInfo(long[] ids, int maxDepth) {
        this.ids = ids;
        this.maxDepth = maxDepth;
    }
    
    public PrivilegedGetThreadInfo(int maxDepth) {
        this.ids = null;
        this.maxDepth = maxDepth;
    }
    
    @Override
    public ThreadInfo[] run() throws Exception {
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        
        if (ids != null) {
            return threadMXBean.getThreadInfo(ids, maxDepth);
        } else {
            return threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), maxDepth);
        }
    }

}
