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
package javax.interceptor;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Context information passed to AroundInvoke and 
 * Interceptor-class lifecycle callback methods.
 */
public interface InvocationContext {

    /**
     * Returns the target instance. 
     */
    public Object getTarget();

    /**
     * Returns the method of the bean class for which the interceptor
     * was invoked.  For AroundInvoke methods, this is the business
     * method on the bean class. For lifecycle callback methods, 
     * returns null.
     */
    public Method getMethod();

    /**
     * Returns the parameters that will be used to invoke
     * the business method.  If setParameters has been called, 
     * getParameters() returns the values to which the parameters 
     * have been set.  
     * 
     * @exception java.lang.IllegalStateException if invoked within
     * a lifecycle callback method.
     */
    public Object[] getParameters();
    
    /**
     * Sets the parameters that will be used to invoke the 
     * business method.  
     *
     * @exception java.lang.IllegalStateException if invoked within
     * a lifecycle callback method.
     */
    public void setParameters(Object[] params);

    /**
     * Returns the context data associated with this invocation or
     * lifecycle callback.  If there is no context data, an
     * empty Map<String,Object> object will be returned.  
     */
    public Map<String, Object> getContextData();

    /**
     * Proceed to the next entry in the interceptor chain.
     * The proceed method returns the result of the next
     * method invoked.  If the method returns void, proceed
     * returns null.
     */
    public Object proceed() throws Exception;

} 
