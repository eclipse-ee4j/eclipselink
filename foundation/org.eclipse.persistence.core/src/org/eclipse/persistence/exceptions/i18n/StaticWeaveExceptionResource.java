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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

public class StaticWeaveExceptionResource extends ListResourceBundle {
        static final Object[][] contents = {
                                           { "40001", "An exception was thrown while trying to open an archive from URL: {0}"},
                                           { "40002", "No source was specified for weaving"},
                                           { "40003", "No target was specified for weaving"},
                                           { "40004", "Performing weaving in place for JAR file is not allowed"},
                                           { "40005", "An exception was thrown while trying to open a logging file: {0}"},
                                           { "40006", "Logging level was specified in wrong value, must be one of (OFF,SEVERE,WARNING,INFO,CONFIG,FINE,FINER,FINEST)"},
                                           { "40007", "An exception was thrown while weaving: {0}"}
    };

    /**
      * Return the lookup table.
      */
    protected Object[][] getContents() {
        return contents;
    }
}
