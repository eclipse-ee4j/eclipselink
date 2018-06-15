/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.internal.jaxb.many;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public abstract class MultiDimensionalManyValue<T extends ManyValue<?, Object>> extends ManyValue<T, Object> {

    protected abstract Class<T> componentClass();

}
