/*
 * Copyright (c) 2014, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 IBM Corporation and/or its affiliates. All rights reserved.
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
            { "7501", "Bean validation mode on {0}marshaller is set to an unsupported value, \"{1}\"." },

            { "7510", "Constraints violated on {0}marshalled bean:\n{1}{2}" },

            { "7525", "Cannot parse the \"{0}\" property, because it is both annotated with @NotNull and has the " +
                    "\"xs:nillable=true\" attribute." }

    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }

}
