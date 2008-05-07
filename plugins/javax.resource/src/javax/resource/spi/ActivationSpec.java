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

import javax.resource.ResourceException;

/**
 * This interface serves as a marker. An instance of an ActivationSpec must be
 * a JavaBean and must be serializable. This holds the activation configuration
 * information for a message endpoint.
 *
 * @version 1.0
 * @author  Ram Jeyaraman
 */
public interface ActivationSpec extends ResourceAdapterAssociation {

    /**
     * This method may be called by a deployment tool to validate the overall
     * activation configuration information provided by the endpoint deployer.
     * This helps to catch activation configuration errors earlier on without 
     * having to wait until endpoint activation time for configuration 
     * validation. The implementation of this self-validation check behavior 
     * is optional.
     *
     * @throws <code>InvalidPropertyException</code> 
     *                   indicates invalid configuration property settings.
     */
    void validate() throws InvalidPropertyException;
}
