/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.testing.jaxb.beanvalidation.dom;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.6
 */
@XmlRootElement
public enum Department {
    RDBMS, JavaSE, JavaEE, Sales, Support
}
