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
 * This exception indicates that the transaction associated with processing
 * of the request has been rolled back, or it has been marked to roll back. 
 * Thus the requested operation either could not be performed or was not 
 * performed because further computation on behalf of the transaction would be
 * fruitless.
 */
public class TransactionRolledbackException extends java.rmi.RemoteException 
{
	public TransactionRolledbackException()
	{
		super();
	}

	public TransactionRolledbackException(String msg)
	{
		super(msg);
	}
}

