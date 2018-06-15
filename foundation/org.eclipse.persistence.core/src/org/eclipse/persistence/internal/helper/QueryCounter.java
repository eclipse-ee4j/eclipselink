/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.helper;


/**
 * INTERNAL:
 * This counter is used by the sessions to assign individual id's to all queries.
 * It is not synchronized because ++ with volatile is atomic, and queries executing at the same time do not require to refresh twice.
 * It is part of the fix for Bug#2698903 which arose from the fix for BUG#2612628
 */
public class QueryCounter {
    private static volatile long count = 0;

    public static long getCount() {
        return ++count;
    }
}
