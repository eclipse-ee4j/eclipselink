/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     tware - March 25/2008 - 1.0M6 - Initial implementation

package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An InstantiationCopyPolicy is used to set an
 * org.eclipse.persistence.descriptors.copying.InstantiationCopyPolicy on an
 * Entity.
 * <p>
 * InstantiationCopyPolicy is the default CopyPolicy if weaving is not used,
 * or if property access is used.
 * <p>
 * A special CloneCopyPolicy is used if weaving and field access is used.
 * <p>
 * An InstantiationCopyPolicy should be specified on an Entity,
 * MappedSuperclass or Embeddable.
 * <p>
 * Example:
 * <pre><code>
 * {@literal @}Entity
 * {@literal @}InstantiationCopyPolicy
 * public class Employee {
 * </code></pre>
 *
 * @see org.eclipse.persistence.descriptors.copying.InstantiationCopyPolicy
 * @see org.eclipse.persistence.annotations.CloneCopyPolicy
 * @see org.eclipse.persistence.annotations.CopyPolicy
 *
 * @author tware
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface InstantiationCopyPolicy {
}
