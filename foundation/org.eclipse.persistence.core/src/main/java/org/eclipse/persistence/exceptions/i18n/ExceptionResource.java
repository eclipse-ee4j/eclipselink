/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     12/18/2014-2.6 Rick Curtis
//       - 454189 : Misc message cleanup.#2
package org.eclipse.persistence.exceptions.i18n;


/**
 * INTERNAL:
 * English ResourceBundle for EclipseLinkException messages.
 *
 * Creation date: (12/6/00 9:47:38 AM)
 * @author Rick Barkhouse
 */
public class ExceptionResource extends java.util.ListResourceBundle {
    static final Object[][] contents = {
                                           { "NoExceptionTranslationForThisLocale", "(There is no translation for this exception.) {0}" },
                                           { "ExceptionHeader", "Exception [EclipseLink-" },
                                           { "DescriptionHeader", "Exception Description: " },
                                           { "InternalExceptionHeader", "Internal Exception: " },
                                           { "TargetInvocationExceptionHeader", "Target Invocation Exception: " },
                                           { "ErrorCodeHeader", "Error Code: " },
                                           { "LocalExceptionStackHeader", "Local Exception Stack: " },
                                           { "InternalExceptionStackHeader", "Internal Exception Stack: " },
                                           { "TargetInvocationExceptionStackHeader", "Target Invocation Exception Stack: " },
                                           { "MappingHeader", "Mapping: " },
                                           { "DescriptorHeader", "Descriptor: " },
                                           { "QueryHeader", "Query: " },
                                           { "CallHeader", "Call: " },
                                           { "DescriptorExceptionsHeader", "Descriptor Exceptions: " },
                                           { "RuntimeExceptionsHeader", "Runtime Exceptions: " },
                                           { "ErrorFormattingMessage", "Error trying to format exception message: {0}  The arguments are: {1}" }
    };

    /**
     * Return the lookup table.
     */
    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
