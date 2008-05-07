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

import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Indicates a dependency on the local or remote view of an Enterprise
 * Java Bean.  
 *
 */

@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface EJB {

    /**
     * The logical name of the ejb reference within the declaring component's
     * (java:comp/env) environment.
     */
    String name() default "";

    String description() default "";

    /**
     * The ejb-name of the Enterprise Java Bean to which this reference 
     * is mapped.  Only applicable if the target EJB is defined within the 
     * same application or stand-alone module as the declaring component.
     */
    String beanName() default "";

    /**
     * Holds one of the following interface types of the target EJB :
     *  [ Local business interface, Remote business interface, 
     *    Local Home interface, Remote Home interface ]
     *  
     */
    Class beanInterface() default Object.class;

    /**
      * The product specific name of the EJB component to which this
      * ejb reference should be mapped.  This mapped name is often a
      * global JNDI name, but may be a name of any form. 
      * 
      * Application servers are not required to support any particular 
      * form or type of mapped name, nor the ability to use mapped names. 
      * The mapped name is product-dependent and often installation-dependent. 
      * No use of a mapped name is portable. 
      */ 
    String mappedName() default "";
}
