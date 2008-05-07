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

package javax.resource.cci;

import javax.resource.ResourceException;

/** 
 * This serves as a request-response message listener type that message
 * endpoints (message-driven beans) may implement. This allows an EIS to
 * communicate with an endpoint using a request-response style.
 *
 *  @author  Ram Jeyaraman
 *  @version 1.0
 */    
public interface MessageListener {

    /**
     * This method allows an EIS to call a message endpoint using a 
     * request-response style communication.
     *
     * @param inputData a <code>Record</code> instance.
     *
     * @return a <code>Record</code> instance or null.
     *
     * @throws ResourceException indicates an exceptional condition.
     */
    Record onMessage(Record inputData) throws ResourceException;
}
