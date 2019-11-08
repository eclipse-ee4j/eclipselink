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
//     07/16/2009-2.0 Guy Pelletier
//       - 277039: JPA 2.0 Cache Usage Settings
package org.eclipse.persistence.testing.models.jpa.cacheable;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name="JPA_CHILD_CACHEABLE_FALSE")
@DiscriminatorValue("CCFE")
@Cacheable(false)
public class ChildCacheableFalseEntity extends CacheableTrueEntity {
    public ChildCacheableFalseEntity() {}
}
