/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.eis.cobol;

import java.util.*;

/**
* <b>Purpose</b>:  This class contains meta information for a record.
*/
public class RecordMetaData implements CompositeObject {

    /** a collection of fields that the record contains */
    private Vector myFields;

    /** the name of the record */
    private String myName;

    /** constructor */
    public RecordMetaData() {
        initialize();
    }

    public RecordMetaData(String name) {
        initialize(name);
    }

    public RecordMetaData(String name, Vector fields) {
        initialize(name, fields);
    }

    private void initialize() {
        myFields = new Vector();
    }

    private void initialize(String name) {
        myName = name;
        myFields = new Vector();
    }

    private void initialize(String name, Vector fields) {
        myName = name;
        myFields = fields;
    }

    /** getter for record name */
    public String getName() {
        return myName;
    }

    /** setter for record name */
    public void setName(String newName) {
        myName = newName;
    }

    /** getter for myFields */
    public Vector getFields() {
        return myFields;
    }

    /** setter for myFields */
    public void setFields(Vector newFields) {
        myFields = newFields;
    }

    /** adds the field to the collection */
    public void addField(FieldMetaData newField) {
        myFields.addElement(newField);
    }

    /** since a record is by defintion is composite, this always returns true */
    public boolean isComposite() {
        return true;
    }

    /** retrieves the <code>FieldMetaData</code> with the corresponding name if it exists */
    public FieldMetaData getFieldNamed(String fieldName) {
        Enumeration fieldsEnum = getFields().elements();
        while (fieldsEnum.hasMoreElements()) {
            FieldMetaData field = (FieldMetaData)fieldsEnum.nextElement();
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }
}
