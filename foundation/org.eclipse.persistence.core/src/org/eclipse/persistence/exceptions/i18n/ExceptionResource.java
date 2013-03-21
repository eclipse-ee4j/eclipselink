/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.exceptions.i18n;


/**
 * INTERNAL:
 * English ResourceBundle for EclipseLinkException messages.
 *
 * Creation date: (12/6/00 9:47:38 AM)
 * @author: Rick Barkhouse
 */
public class ExceptionResource extends java.util.ListResourceBundle {
    static final Object[][] contents = {
                                           { "NoExceptionTranslationForThisLocale", "(There is no English translation for this exception.) {0}" },
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
    protected Object[][] getContents() {
        return contents;
    }
}
