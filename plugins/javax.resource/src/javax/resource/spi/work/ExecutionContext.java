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

import javax.transaction.xa.Xid;
import javax.resource.NotSupportedException;
//import javax.resource.spi.security.SecurityContext;

/**
 * This class models an execution context (transaction, security, etc) 
 * with which the <code>Work</code> instance must be executed.  
 * This class is provided as a convenience for easily creating 
 * <code>ExecutionContext</code> instances by extending this class
 * and overriding only those methods of interest.
 *
 * <p>Some reasons why it is better for <code>ExecutionContext</code> 
 * to be a class rather than an interface: 
 * <ul><li>There is no need for a resource adapter to implement this class. 
 * It only needs to implement the context information like 
 * transaction, etc.
 * <li>The resource adapter code does not have to change when the 
 * <code>ExecutionContext</code> class evolves. For example, more context 
 * types could be added to the <code>ExecutionContext</code> class 
 * (in the future) without forcing resource adapter implementations 
 * to change.</ul>
 *
 * @version 1.0
 * @author  Ram Jeyaraman
 */
public class ExecutionContext {

    /**
     * transaction context.
     */
    private Xid xid;

    /**
     * transaction timeout value.
     */
    private long transactionTimeout = WorkManager.UNKNOWN;

    /**
     * security context.
     */
    //private SecurityContext securityCtx;

    /**
     * set a transaction context.
     *
     * @param xid transaction context.
     */
    public void setXid(Xid xid) { this.xid = xid; }

    /*
     * @return an Xid object carrying a transaction context, 
     * if any.
     */
    public Xid getXid() { return this.xid; }

    /**
     * Set the transaction timeout value for a imported transaction.
     *
     * @param timeout transaction timeout value in seconds. Only positive
     * non-zero values are accepted. Other values are illegal and are 
     * rejected with a <code>NotSupportedException</code>.
     *
     * @throws NotSupportedException thrown to indicate an illegal timeout 
     * value.
     */
    public void setTransactionTimeout(long timeout) 
	throws NotSupportedException {
	if (timeout > 0) {
	    this.transactionTimeout = timeout;
	} else {
	    throw new NotSupportedException("Illegal timeout value");
	}
    }

    /** 
     * Get the transaction timeout value for a imported transaction.
     *
     * @return the specified transaction timeout value in seconds. When no
     * timeout value or an illegal timeout value had been specified, 
     * a value of -1 (<code>WorkManager.UNKNOWN</code>) 
     * is returned; such a transaction is excluded from regular 
     * timeout processing.
     */
    public long getTransactionTimeout() {
	return this.transactionTimeout;
    }

    /**
     * set a security context.
     *
     * @param securityCtx security context.
     */
    /*
    public void setSecurityContext(SecurityContext securityCtx) {
	this.securityCtx = securityCtx;
    }
    */
    /*
     * @return a <code>SecurityContext</code> object, if any.
     */
    /*
    public SecurityContext getSecurityContext() { return this.securityCtx; }
    */
}
