/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     IBM - ConcurrencyUtil call of ThreadMXBean.getThreadInfo() needs doPriv
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
