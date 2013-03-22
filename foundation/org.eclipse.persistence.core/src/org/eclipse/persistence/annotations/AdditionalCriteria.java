/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/15/2010-2.2 Guy Pelletier 
 *       - 322008: Improve usability of additional criteria applied to queries at the session/EM
 ******************************************************************************/
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>An additional criteria can be specified at the Entity or MappedSuperclass 
 * level. When specified at the mapped superclass level, it applies to all 
 * inheriting entities unless those entities define their own additional 
 * criteria, at which point the additional criteria from the mapped superclass 
 * is ignored.</p>
 * 
 * <p>The additional criteria supports any valid JPQL string and must use 'this' 
 * as an alias to form your additional criteria. E.G.,
 * <pre>
 * @Entity
 * @AdditionalCriteria("this.nut.size = :NUT_SIZE and this.nut.color = :NUT_COLOR")
 * public class Bolt {...}
 * </pre>
 * </p>
 *   
 * <p>Additional criteria parameters are also accepted and are set through 
 * properties on the entity manager factory, or on an entity manager. When set 
 * on the entity manager, the properties must be set before any query execution
 * and should not be changed for the life span of that entity manager.</p>
 * 
 * <p>Properties set on the entity manager will override those similarly named 
 * properties set on the entity manager factory.</p>
 * 
 * <p>Additional criteria is not supported with any native queries.</p>
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.2
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface AdditionalCriteria {
    /**
     * (Required) The JPQL fragment to use as the additional criteria.
     */
    String value();
}
