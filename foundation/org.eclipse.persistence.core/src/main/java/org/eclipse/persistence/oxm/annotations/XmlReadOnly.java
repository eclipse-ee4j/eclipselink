/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle = 2.2 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that a specific property should be flagged as read-only
 * by EclipseLink. The value for this property will never be written out to XML during
 * a marshal if flagged as read-only.
 *
 * <p><b>Example:</b>
 * {@snippet :
 *  @XmlRootElement(name="customer")
 *  public class Customer {
 *
 *      @XmlElement
 *      @XmlReadOnly
 *      public String firstName;
 *
 *  }
 * }
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlReadOnly {
}
