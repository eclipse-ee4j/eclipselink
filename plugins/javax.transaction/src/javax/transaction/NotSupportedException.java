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
 * NotSupportedException exception indicates that the request cannot be
 * executed because the operation is not a supported feature. For example, 
 * because nested transactions are not supported, the Transaction Manager 
 * throws this exception when a calling thread
 * that is already associated with a transaction attempts to start a new 
 * transaction. (A nested transaction occurs when a thread is already
 * associated with one transaction and attempts to start a second 
 * transaction.)
 */
public class NotSupportedException extends java.lang.Exception 
{
	public NotSupportedException()
	{
		super();
	}

	public NotSupportedException(String msg)
	{
		super(msg);
	}
}

