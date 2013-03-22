/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dmccann - August 6/2009 - 2.0 - Initial implementation     
 ******************************************************************************/
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.eclipse.persistence.config.DescriptorCustomizer;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** 
 * The XmlCustomizer annotation is used to specify a class that implements the 
 * org.eclipse.persistence.config.DescriptorCustomizer 
 * interface and is to run against a class descriptor after all metadata 
 * processing has been completed.
 */ 
@Target({TYPE})
@Retention(RUNTIME)
public @interface XmlCustomizer {

    /**
     * (Required) Defines the name of the descriptor customizer that should be
     * applied to this classes descriptor.
     */
     Class<? extends DescriptorCustomizer> value();

}