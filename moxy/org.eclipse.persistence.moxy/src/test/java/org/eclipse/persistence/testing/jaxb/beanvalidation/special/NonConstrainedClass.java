/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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
//          Marcel Valovy - 2.6 - initial API and implementation
package org.eclipse.persistence.testing.jaxb.beanvalidation.special;

/**
 * A simple class that is not constrained with any bean validation annotations.
 * Easier would be to create instance of Object or String, however MOXy throws exception if we try to unmarshal
 * instance of such SDK classes.
 */
public class NonConstrainedClass {
}
