/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.mappings;

import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;

/**
 * <p>Subclass of Field for fields that are mapped to unions. Maintains a list 
 * of schema types instead of just one single schema type. Schema types can be 
 * added using the addSchemaType API.
 *
 * Constants has a list of useful constants including a list of QNames for
 * built-in schema types that can be used when adding schema types.
 *
 * <p>When reading and writing an element that is mapped with an UnionField, a 
 * conversion to each of the schema types on the field (in the order they are 
 * specified ) is tried until a conversion is successful. The Java type to 
 * convert to is based on the list of schema type to Java conversion pairs 
 * specified on the field.

 * @see Field
 * @see Constants
 */
public interface UnionField<
    NAMESPACE_RESOLVER extends NamespaceResolver> extends Field<NAMESPACE_RESOLVER> {

    /**
     * Adds the new type value to the list of types
     * @param value QName to be added to the list of schema types
     */
     public void addSchemaType(QName value);

    /**
      * Return the list of schema types
      * @return the list of types
      */
    public List<QName> getSchemaTypes();

}
