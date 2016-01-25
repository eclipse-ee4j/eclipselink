/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p>
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal.naming;

/**
 * Interface for property naming conversion implementations.
 * TODO subject to change, default javax.json.bind.config.PropertyNamingStrategy does not have reverse conversion method.
 *
 * @author Roman Grigoriadi
 */
public interface PropertyNamingStrategy {

    /**
     * Convert from model property name to json name.
     *
     * @param modelPropertyName name of a class property, customized or default, not null
     * @return name of a property to marshall into json
     */
    String toJsonPropertyName(String modelPropertyName);

    /**
     * Converts from json property name to class property name
     *
     * @param jsonPropertyName property name as it appesars in json
     * @return name of a class property
     */
    String toModelPropertyName(String jsonPropertyName);
}
