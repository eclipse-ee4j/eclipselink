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
 *     12/02/2010-2.2 Guy Pelletier 
 *       - 251554: ExcludeDefaultMapping annotation needed
 ******************************************************************************/
package org.eclipse.persistence.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specifies default mappings (those that are not explicitly decorated in XML or
 * using annotations)should be omitted.
 * 
 * An ExcludeDefaultMappings annotation can be specified on an entity,
 * embeddable or mapped superclass and applies solely to the mappings
 * of the class it is defined on.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.2
 */
@Target({TYPE}) 
@Retention(RUNTIME)
public @interface ExcludeDefaultMappings {
}
