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
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

/**
 * INTERNAL:
 * English ResourceBundle for BeanValidationException messages.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.6
 */
public final class BeanValidationExceptionResource extends ListResourceBundle {

    static final Object[][] contents = {

            { "7500", "Bean validation mode on {0}marshaller is set to ON, but no Bean validation provider was found." },
            { "7501", "Bean validation mode on {0}marshaller is set to illegal value, \"{1}\"." },

            { "7510", "Constraints violated on {0}marshalled bean:\n{1}{2}" },

            { "7525", "Cannot parse property {0}, because it is both annotated with @NotNull and has attribute " +
                    "\"xs:nillable=true\"." }

    };

    /**
     * Return the lookup table.
     */
    @Override
    protected Object[][] getContents() {
        return contents;
    }

}
