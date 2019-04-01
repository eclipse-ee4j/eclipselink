/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.eis.mappings;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.XMLField;

/**
 * <p>EIS Composite Direct Collection Mappings map a collection of simple Java attributes
 * to and from an EIS Record according to its descriptor's record type.
 *
 * <table border="1">
 * <caption>Record formats</caption>
 * <tr>
 * <th id="c1">Record Type</th>
 * <th id="c2">Description</th>
 * </tr>
 * <tr>
 * <td headers="c1">Indexed</td>
 * <td headers="c2">Ordered collection of record elements.  The indexed record EIS format
 * enables Java class attribute values to be retrieved by position or index.</td>
 * </tr>
 * <tr>
 * <td headers="c1">Mapped</td>
 * <td headers="c2">Key-value map based representation of record elements.  The mapped record
 * EIS format enables Java class attribute values to be retrieved by an object key.</td>
 * </tr>
 * <tr>
 * <td headers="c1">XML</td>
 * <td headers="c2">Record/Map representation of an XML DOM element.</td>
 * </tr>
 * </table>
 *
 * @see org.eclipse.persistence.eis.EISDescriptor#useIndexedRecordFormat
 * @see org.eclipse.persistence.eis.EISDescriptor#useMappedRecordFormat
 * @see org.eclipse.persistence.eis.EISDescriptor#useXMLRecordFormat
 *
 * @since Oracle TopLink 10<i>g</i> Release 2 (10.1.3)
 */
public class EISCompositeDirectCollectionMapping extends AbstractCompositeDirectCollectionMapping implements EISMapping {
    public EISCompositeDirectCollectionMapping() {
        super();
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isEISMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * Initialize the mapping.
     */
    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if (this.getField() instanceof XMLField && getValueConverter() instanceof TypeConversionConverter) {
            TypeConversionConverter converter = (TypeConversionConverter)getValueConverter();
            this.getField().setType(converter.getObjectClass());
        }
    }

    /**
     * Set the Mapping field name attribute to the given XPath String
     *
     * @param xpathString String
     *
     */
    public void setXPath(String xpathString) {
        setField(new XMLField(xpathString));
    }

    /**
     * Get the XPath String
     *
     * @return String the XPath String associated with this Mapping
     *
     */
    public String getXPath() {
        return getFieldName();
    }

    /**
     * PUBLIC:
     * Return the name of the field that holds the nested collection.
     */
    @Override
    public String getFieldName() {
        return this.getField().getName();
    }

    /**
     * PUBLIC:
     * Set the name of the field that holds the nested collection.
     */
    public void setFieldName(String fieldName) {
        this.setField(new DatabaseField(fieldName));
    }
}
