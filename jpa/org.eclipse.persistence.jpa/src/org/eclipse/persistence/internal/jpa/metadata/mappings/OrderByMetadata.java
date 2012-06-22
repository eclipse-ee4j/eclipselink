/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/25/2011-2.3 Guy Pelletier 
 *       - 333913: @OrderBy and <order-by/> without arguments should order by primary
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.mappings;

import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.mappings.CollectionMapping;

/**
 * Object to hold onto order by metadata.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.3
 */
public class OrderByMetadata extends ORMetadata {
    // Order by constants
    private static final String ASCENDING = "ASC";
    private static final String DESCENDING = "DESC";
    
    private String m_value;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public OrderByMetadata() {
        super("<order-by>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public OrderByMetadata(MetadataAnnotation orderBy, MetadataAccessor accessor) {
        super(orderBy, accessor);

        m_value = (String) orderBy.getAttributeString("value");
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof OrderByMetadata) {
            OrderByMetadata orderBy = (OrderByMetadata) objectToCompare;
            return valuesMatch(m_value, orderBy.getValue());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getValue() {
        return m_value;
    }
    
    /**
     * INTERNAL:
     * Process an order by value (if specified) for the given collection 
     * mapping. Order by specifies the ordering of the elements of a collection 
     * valued association at the point when the association is retrieved.
     * 
     * The syntax of the value ordering element is an orderby_list, as follows:
     * 
     * orderby_list ::= orderby_item [, orderby_item]*
     * orderby_item ::= property_or_field_name [ASC | DESC]
     * 
     * When ASC or DESC is not specified, ASC is assumed.
     * 
     * If the ordering element is not specified, ordering by the primary key
     * of the associated entity is assumed.
     * 
     * The property or field name must correspond to that of a persistent
     * property or field of the associated class. The properties or fields 
     * used in the ordering must correspond to columns for which comparison
     * operators are supported.
     */
    public void process(CollectionMapping mapping, MetadataDescriptor referenceDescriptor, MetadataClass javaClass) {
        if (m_value != null && ! m_value.equals("")) {
            StringTokenizer commaTokenizer = new StringTokenizer(m_value, ",");
        
            while (commaTokenizer.hasMoreTokens()) {
                StringTokenizer spaceTokenizer = new StringTokenizer(commaTokenizer.nextToken());
                String propertyOrFieldName = spaceTokenizer.nextToken();
                MappingAccessor referenceAccessor = referenceDescriptor.getMappingAccessor(propertyOrFieldName);
            
                if (referenceAccessor == null) {
                    throw ValidationException.invalidOrderByValue(propertyOrFieldName, referenceDescriptor.getJavaClass(), getAccessibleObjectName(), javaClass);
                }

                String attributeName = referenceAccessor.getAttributeName();
                String ordering = (spaceTokenizer.hasMoreTokens()) ? spaceTokenizer.nextToken() : ASCENDING;

                if (referenceAccessor.isEmbedded()) {
                    for (String orderByAttributeName : referenceDescriptor.getOrderByAttributeNames()) {
                        mapping.addAggregateOrderBy(m_value, orderByAttributeName, ordering.equals(DESCENDING));
                    }
                } else if (referenceAccessor.getClassAccessor().isEmbeddableAccessor()) {
                    // We have a specific order by from an embeddable, we need to rip off 
                    // the last bit of a dot notation if specified and pass in the chained 
                    // string names of the nested embeddables only.
                    String embeddableChain = m_value;
                    if (embeddableChain.contains(".")) {
                        embeddableChain = embeddableChain.substring(0, embeddableChain.lastIndexOf("."));
                    }
                    
                    mapping.addAggregateOrderBy(embeddableChain, attributeName, ordering.equals(DESCENDING)); 
                } else {
                    mapping.addOrderBy(attributeName, ordering.equals(DESCENDING));
                }
            }
        } else {
            // Default to the primary key field name(s).
            List<String> orderByAttributes = referenceDescriptor.getIdOrderByAttributeNames();
        
            if (referenceDescriptor.hasEmbeddedId()) {
                String embeddedIdAttributeName = referenceDescriptor.getEmbeddedIdAttributeName();
        
                for (String orderByAttribute : orderByAttributes) {
                    mapping.addAggregateOrderBy(embeddedIdAttributeName, orderByAttribute, false);
                }
            } else {
                for (String orderByAttribute : orderByAttributes) {
                    mapping.addOrderBy(orderByAttribute, false);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setValue(String value) {
        m_value = value;
    }
}

