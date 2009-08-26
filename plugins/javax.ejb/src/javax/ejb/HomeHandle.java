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

import java.rmi.RemoteException;

/**
 * The HomeHandle interface is implemented by all home object handles. A handle
 * is an abstraction of a network reference to a home object. A handle is
 * intended to be used as a "robust" persistent reference to a home object.
 */
public interface HomeHandle extends java.io.Serializable {
    /**
     * Obtain the home object represented by this handle.
     *
     * @return the home object represented by this handle.
     *
     * @exception RemoteException The home object could not be obtained
     *    because of a system-level failure.
     */
    public EJBHome getEJBHome() throws RemoteException;
}
