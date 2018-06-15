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
package org.eclipse.persistence.jaxb;

/**
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.6
 */
public enum BeanValidationMode {

    /**
     * If a Bean Validation provider is present in the environment,
     * the JAXB implementation provider must perform the automatic validation
     * of entities. If no Bean Validation provider is present in the
     * environment, no validation takes place.
     */
    AUTO,

    /**
     * The JAXB implementation provider must perform the validation of entities.
     * It is an error if there is no Bean Validation provider present in the
     * environment.
     */
    CALLBACK,

    /**
     * The JAXB implementation provider must not perform lifecycle event validation.
     */
    NONE
}
