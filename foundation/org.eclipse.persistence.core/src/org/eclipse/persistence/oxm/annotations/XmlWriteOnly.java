/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle = 2.2 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p><b>Purpose</b>: This annotation indicates that a specific property should be flagged as write-only
 * by EclipseLink. The value for this property will be written out to XML during
 * a marshal, but ignored during unmarshal if flagged as write-only.
 *
 * <p><b>Example:</b><br>
 * <code>
 * &nbsp;@XmlRootElement(name="customer")<br>
 * &nbsp;public class Customer {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;@XmlElement<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;@XmlWriteOnly<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;public String firstName<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;}</code>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlWriteOnly {}
