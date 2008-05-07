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

package javax.resource.spi;

import java.util.Timer;
import javax.resource.spi.work.WorkManager;

/**
 * This provides a mechanism to pass a bootstrap context to a resource adapter
 * instance when it is bootstrapped. That is, when 
 * (<code>start(BootstrapContext)</code>) method on the 
 * <code>ResourceAdapter</code> class is invoked. The bootstrap
 * context contains references to useful facilities that could be used by the
 * resource adapter instance.
 *
 * @version 1.0
 * @author  Ram Jeyaraman
 */
public interface BootstrapContext {
    /**
     * Provides a handle to a <code>WorkManager</code> instance. The
     * <code>WorkManager</code> instance could be used by a resource adapter to
     * do its work by submitting <code>Work</code> instances for execution. 
     *
     * @return a <code>WorkManager</code> instance.
     */
    WorkManager getWorkManager();

    /**
     * Provides a handle to a <code>XATerminator</code> instance. The
     * <code>XATerminator</code> instance could be used by a resource adapter 
     * to flow-in transaction completion and crash recovery calls from an EIS.
     *
     * @return a <code>XATerminator</code> instance.
     */
    XATerminator getXATerminator();

    /**
     * Creates a new <code>java.util.Timer</code> instance. The
     * <code>Timer</code> instance could be used to perform periodic 
     * <code>Work</code> executions or other tasks.
     *
     * @throws UnavailableException indicates that a 
     * <code>Timer</code> instance is not available. The 
     * request may be retried later.
     *
     * @return a new <code>Timer</code> instance.
     */
    Timer createTimer() throws UnavailableException;
}
