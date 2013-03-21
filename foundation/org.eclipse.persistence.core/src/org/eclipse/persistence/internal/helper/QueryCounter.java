/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
