/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//  06/12/2008-1.0M9 James Sutherland
package org.eclipse.persistence.sessions.factories;

/**
 * This class handles migration from TopLink when broad imports were used.
 * This class was moved to config.
 * @author James Sutherland
 * @deprecated replaced by org.eclipse.persistence.config.DescriptorCustomizer
 */
@Deprecated
public interface DescriptorCustomizer extends org.eclipse.persistence.config.DescriptorCustomizer {

}
