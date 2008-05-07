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

package javax.transaction;

/**
 * RollbackException exception is thrown when the transaction has been 
 * marked for rollback only or the transaction has been rolled back
 * instead of committed. This is a local exception thrown by methods 
 * in the <code>UserTransaction</code>, <code>Transaction</code>, and 
 * <code>TransactionManager</code> interfaces.
 */
public class RollbackException extends java.lang.Exception 
{
	public RollbackException()
	{
		super();
	}

	public RollbackException(String msg)
	{
		super(msg);
	}
}

