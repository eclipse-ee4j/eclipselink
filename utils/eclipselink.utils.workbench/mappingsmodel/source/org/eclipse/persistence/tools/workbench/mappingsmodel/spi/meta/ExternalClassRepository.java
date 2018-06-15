/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta;

/**
 * Interface for a repository of external classes.
 * @see ExternalClassRepositoryFactory
 */
public interface ExternalClassRepository {

    /**
     * This list may be helpful for a repository implementation
     * that cannot load void and the primitive classes in the
     * same fashion it loads "normal" reference classes.
     */
    ExternalClassDescription[] PRIMITIVE_EXTERNAL_CLASS_DESCRIPTIONS = {
        new PrimitiveExternalClassDescription(void.class),
        new PrimitiveExternalClassDescription(boolean.class),
        new PrimitiveExternalClassDescription(char.class),
        new PrimitiveExternalClassDescription(byte.class),
        new PrimitiveExternalClassDescription(short.class),
        new PrimitiveExternalClassDescription(int.class),
        new PrimitiveExternalClassDescription(long.class),
        new PrimitiveExternalClassDescription(float.class),
        new PrimitiveExternalClassDescription(double.class)
    };

    /**
     * Returns the "default" ExternalClassDescription with the specified name.
     * If the repository contains multiple types with the specified
     * name, this method returns the "default" one, as defined by
     * the repository implementation (e.g. the "default" type could
     * be the first type encountered on the project classpath).
     */
    ExternalClassDescription getClassDescription(String className);

    /**
     * Returns all the ExternalClassDescriptions in the repository.
     * This list should include the "primitive" classes listed above
     * (e.g. int, void); but should not include any "array"
     * classes (e.g. java.lang.Object[], int[][]).
     */
    ExternalClassDescription[] getClassDescriptions();

}
