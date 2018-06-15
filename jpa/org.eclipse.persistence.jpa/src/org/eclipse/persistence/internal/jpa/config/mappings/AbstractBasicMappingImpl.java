/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.mappings;

import org.eclipse.persistence.internal.jpa.config.cache.CacheIndexImpl;
import org.eclipse.persistence.internal.jpa.config.columns.ColumnImpl;
import org.eclipse.persistence.internal.jpa.config.columns.FieldImpl;
import org.eclipse.persistence.internal.jpa.config.sequencing.GeneratedValueImpl;
import org.eclipse.persistence.internal.jpa.config.sequencing.SequenceGeneratorImpl;
import org.eclipse.persistence.internal.jpa.config.sequencing.TableGeneratorImpl;
import org.eclipse.persistence.internal.jpa.config.sequencing.UuidGeneratorImpl;
import org.eclipse.persistence.internal.jpa.config.tables.IndexImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.jpa.config.CacheIndex;
import org.eclipse.persistence.jpa.config.Column;
import org.eclipse.persistence.jpa.config.Field;
import org.eclipse.persistence.jpa.config.GeneratedValue;
import org.eclipse.persistence.jpa.config.Index;
import org.eclipse.persistence.jpa.config.ReturnInsert;
import org.eclipse.persistence.jpa.config.SequenceGenerator;
import org.eclipse.persistence.jpa.config.TableGenerator;
import org.eclipse.persistence.jpa.config.UuidGenerator;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
@SuppressWarnings("unchecked")
public class AbstractBasicMappingImpl<T extends BasicAccessor, R> extends AbstractDirectMappingImpl<T, R> {

    public AbstractBasicMappingImpl(T t) {
        super(t);
    }

    public CacheIndex setCacheIndex() {
        CacheIndexImpl cacheIndex = new CacheIndexImpl();
        getMetadata().setCacheIndex(cacheIndex.getMetadata());
        return cacheIndex;
    }

    public Column setColumn() {
        ColumnImpl column = new ColumnImpl();
        getMetadata().setColumn(column.getMetadata());
        return column;
    }

    public Field setField() {
        FieldImpl field = new FieldImpl();
        getMetadata().setField(field.getMetadata());
        return field;
    }

    public GeneratedValue setGeneratedValue() {
        GeneratedValueImpl generatedValue = new GeneratedValueImpl();
        getMetadata().setGeneratedValue(generatedValue.getMetadata());
        return generatedValue;
    }

    public Index setIndex() {
        IndexImpl index = new IndexImpl();
        getMetadata().setIndex(index.getMetadata());
        return index;
    }

    public R setMutable(Boolean mutable) {
        getMetadata().setMutable(mutable);
        return (R) this;
    }

    public ReturnInsert setReturnInsert() {
        ReturnInsertImpl returnInsert = new ReturnInsertImpl();
        getMetadata().setReturnInsert(returnInsert.getMetadata());
        return returnInsert;
    }

    public R setReturnUpdate() {
        getMetadata().setReturnUpdate(true);
        return (R) this;
    }

    public SequenceGenerator setSequenceGenerator() {
        SequenceGeneratorImpl sequenceGenerator = new SequenceGeneratorImpl();
        getMetadata().setSequenceGenerator(sequenceGenerator.getMetadata());
        return sequenceGenerator;
    }

    public TableGenerator setTableGenerator() {
        TableGeneratorImpl tableGenerator = new TableGeneratorImpl();
        getMetadata().setTableGenerator(tableGenerator.getMetadata());
        return tableGenerator;
    }

    public UuidGenerator setUuidGenerator() {
        UuidGeneratorImpl uuidGenerator = new UuidGeneratorImpl();
        getMetadata().setUuidGenerator(uuidGenerator.getMetadata());
        return uuidGenerator;
    }
}
