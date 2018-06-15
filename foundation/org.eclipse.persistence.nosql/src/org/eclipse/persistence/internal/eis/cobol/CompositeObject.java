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
package org.eclipse.persistence.internal.eis.cobol;

import java.util.*;

/**
* <b>Purpose</b>: This interface defines api for composite fields/records.  It exists so that
* a composite object can be referred to generically within the parser.
*/
public interface CompositeObject {

    /** this method returns the name of the object */
    public String getName();

    /** this method returns a Vector of fields that the object contains */
    public Vector getFields();

    /** this method sets the fields to the new values */
    public void setFields(Vector fields);

    /** this method adds a field to the fields Vector */
    public void addField(FieldMetaData field);

    /** this method returns a field, if such a field exists, by the given name */
    public FieldMetaData getFieldNamed(String fieldName);
}
