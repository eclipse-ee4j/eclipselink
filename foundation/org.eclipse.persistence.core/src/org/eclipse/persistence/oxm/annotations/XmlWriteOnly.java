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
 *     Oracle = 2.2 - Initial contribution
 ******************************************************************************/
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
 * &nbsp;}<br><br> *
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlWriteOnly {}