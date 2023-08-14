/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     13/01/2022-4.0.0 Tomas Kraus
//       - 1391: JSON support in JPA
package org.eclipse.persistence.mappings.converters.spi;

import org.eclipse.persistence.mappings.converters.Converter;

import java.util.Map;
import java.util.function.Supplier;

// This converter is being used in JPA MappingAccessor.
/**
 * Java Service loader interface for data converters.
 */
public interface ConverterProvider {

    /**
     * Returns converter suppliers mapping of specific provider.
     *
     * @return converter suppliers mapping registered in this provider
     */
    Map<String, Supplier<Converter>> converters();

}
