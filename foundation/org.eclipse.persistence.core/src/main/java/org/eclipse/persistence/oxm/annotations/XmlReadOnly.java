/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle = 2.2 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p><b>Purpose: </b>This annotation indicates that a specific property should be flagged as read-only
 * by EclipseLink. The value for this property will never be written out to XML during
 * a marshal if flagged as read-only.
 *
 * <p><b>Example:</b><br>
 * <code>
 * &nbsp;@XmlRootElement(name="customer")<br>
 * &nbsp;public class Customer {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;@XmlElement<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;@XmlReadOnly<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;public String firstName<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;}</code>
 */

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlReadOnly {
}
