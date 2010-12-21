/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://glassfish.dev.java.net/public/CDDLv1.0.html or
 * glassfish/bootstrap/legal/CDDLv1.0.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at glassfish/bootstrap/legal/CDDLv1.0.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */
package javax.ejb;

import java.io.Serializable;

/**
 *
 * The TimerHandle interface is implemented by all EJB timer handles.
 *
 */
public interface TimerHandle extends Serializable {

    /**
     * Obtain a reference to the timer represented by this handle.
     *
     * @return a reference to the timer represented by this handle.
     *
     * @exception java.lang.IllegalStateException If this method is
     * invoked while the instance is in a state that does not allow access 
     * to this method.
     * 
     * @exception javax.ejb.NoSuchObjectLocalException If invoked on a
     * handle whose associated timer has expired or has been cancelled.
     * 
     * @exception javax.ejb.EJBException If this method could not complete due
     * to a system-level failure.
     */
    public Timer getTimer() throws java.lang.IllegalStateException, 
        javax.ejb.NoSuchObjectLocalException, javax.ejb.EJBException;

} 
