/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface StructConverter {
    /**
     * (Required) Name this converter. The name should be unique across the 
     * whole persistence unit.
     */
    String name();

    /**
     * (Required) The converter class to be used. This class must implement the
     * EclipseLink org.eclipse.persistence.platform.database.converters.StructConverter 
     * interface.
     * 
     * You may also alternatively specify a pre-defined EclipseLink 
     * org.eclipse.persistence.config.StructConverterType 
     */
    String converter(); 
}
