/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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
//          Marcel Valovy - 2.6 - initial API and implementation
package org.eclipse.persistence.testing.jaxb.beanvalidation.special;

/**
 * A simple class that is not constrained with any bean validation annotations.
 * Easier would be to create instance of Object or String, however MOXy throws exception if we try to unmarshal
 * instance of such SDK classes.
 */
public class NonConstrainedClass {
}
