/*******************************************************************************
 * Copyright (c) 2014, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Marcel Valovy - 2.6 - initial implementation
 ******************************************************************************/
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
    protected Object[][] getContents() {
        return contents;
    }

}
