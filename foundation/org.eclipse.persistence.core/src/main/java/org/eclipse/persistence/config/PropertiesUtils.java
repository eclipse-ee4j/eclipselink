/*
 * Copyright (c) 2015, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
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
//     02/19/2015 - Rick Curtis
//       - 458877 : Add national character support
//     03/06/2015-2.7.0 Dalia Abo Sheasha
//       - 461607: PropertiesUtils does not process methods with String parameters correctly.
//     05/06/2015-2.7.0 Rick Curtis
//       - 466626: Fix bug in getMethods() when Java 2 security is enabled.
package org.eclipse.persistence.config;

/**
 * A static utility class that handles parsing a String
 * "key=value,key1=value1...." and calls an appropriate set[key]([value]) method
 * on the provided instance.
 * @deprecated This class is for <strong>internal use</strong> only.
 */
    /**
     * @deprecated This constructor will be marked private and the class final. It is not designed for extensibility.
     */
    @Deprecated(since = "4.0.3", forRemoval = true)

public class PropertiesUtils extends org.eclipse.persistence.internal.helper.PropertiesUtils {
}
