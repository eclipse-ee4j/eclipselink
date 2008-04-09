/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import java.util.*;

/**
 *  INTERNAL:
 *  Interface which abstracts the version of the JDK we are on.
 *  This should only implement methods that are dependent on JDK version
 *  The implementers should implement the minimum amount of functionality required to
 *  allow support of multiple versions of the JDK.
 *  @see JDK15Platform
 *  @see JavaPlatform
 *  @author Tom Ware
 */
public interface JDKPlatform {

    /**
     * Conforming queries with LIKE will act differently in different JDKs.
     */
    Boolean conformLike(Object left, Object right);

    /**
     * Get the milliseconds from a Calendar. JDK 1.4 offers a more efficient way of doing this.
     */
    long getTimeInMillis(Calendar calendar);

    /**
     * Set the milliseconds for a Calendar. JDK 1.4 offers a more efficient way of doing this.
     */
    void setTimeInMillis(Calendar calendar, long millis);

    /**
     * Get a concurrent Map that allow concurrent gets but block on put.
     */
    Map getConcurrentMap();

    /**
     * JDK 1.4 and its above offers the option of setting the cause of an exception
     */
    void setExceptionCause(Throwable exception, Throwable cause);

    /**
     * Return a boolean which determines where EclipseLink should include the EclipseLink-stored
     * Internal exception in it's stack trace.  For JDK 1.4 VMs with exception chaining
     * the Internal exception can be redundant and confusing.
     */
    boolean shouldPrintInternalException();
}