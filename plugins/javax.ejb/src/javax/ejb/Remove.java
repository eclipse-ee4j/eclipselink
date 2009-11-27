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

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

/**
 * Applied to a business method of a stateful session bean class.  Indicates
 * that the stateful session bean is to be removed by the container after
 * completion of the method.
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Remove {

    /**
     * If true, the stateful session bean will not be removed if an 
     * exception is thrown from the designated method.
     */
    boolean retainIfException() default false;

}
