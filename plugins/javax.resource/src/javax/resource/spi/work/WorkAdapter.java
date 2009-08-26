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

package javax.resource.spi.work;

import java.lang.Object;
import java.lang.Runnable;
import java.lang.Exception;
import java.lang.Throwable;

/**
 * This class is provided as a convenience for easily creating 
 * <code>WorkListener</code> instances by extending this class
 * and overriding only those methods of interest.
 *
 * @version 1.0
 * @author  Ram Jeyaraman
 */
public class WorkAdapter implements WorkListener {

    /** 
     * Invoked when a <code>Work</code> instance has been accepted.
     */
    public void workAccepted(WorkEvent e) {}

    /** 
     * Invoked when a <code>Work</code> instance has been rejected.
     */
    public void workRejected(WorkEvent e) {}

    /** 
     * Invoked when a <code>Work</code> instance has started execution.
     * This only means that a thread has been allocated.
     */
    public void workStarted(WorkEvent e) {}

    /** 
     * Invoked when a <code>Work</code> instance has completed execution.
     */
    public void workCompleted(WorkEvent e) {}
}
