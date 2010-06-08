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
 * The transaction manager supports a synchronization mechanism
 * that allows the interested party to be notified before and
 * after the transaction completes. Using the registerSynchronization
 * method, the application server registers a Synchronization object
 * for the transaction currently associated with the target Transaction
 * object.
 */
public interface Synchronization {

    /**
     * The beforeCompletion method is called by the transaction manager prior
     * to the start of the two-phase transaction commit process. This call is
     * executed with the transaction context of the transaction that is being
     * committed.
     */
    public void beforeCompletion();

    /**
     * This method is called by the transaction
     * manager after the transaction is committed or rolled back.
     *
     * @param status The status of the transaction completion.
     */
	public void afterCompletion(int status);
}
