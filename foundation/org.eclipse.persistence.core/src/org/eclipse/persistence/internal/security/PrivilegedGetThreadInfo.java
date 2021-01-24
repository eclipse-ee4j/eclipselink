/*
 * Copyright (c) 2020 IBM and/or its affiliates. All rights reserved.
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
//     IBM - ConcurrencyUtil call of ThreadMXBean.getThreadInfo() needs doPriv
//     Oracle - backport to 2.7 branch
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
