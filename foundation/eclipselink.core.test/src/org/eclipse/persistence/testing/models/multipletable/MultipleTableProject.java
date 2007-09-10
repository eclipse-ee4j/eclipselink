/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.multipletable;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

/**
 * Project definition for the MultipleTableSystem
 *
 * @auther Guy Pelletier
 * @version 1.0
 * @date June 17, 2005
 */
public class MultipleTableProject extends Project {
    public MultipleTableProject() {
        setName("MultipleTableProject");
        addDescriptor(buildCowDescriptor());
        addDescriptor(buildHorseDescriptor());
        addDescriptor(buildHumanDescriptor());
        addDescriptor(buildSwanDescriptor());
    }

    public ClassDescriptor buildCowDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Cow.class);
        descriptor.addTableName("MULTI_COW");
        descriptor.addTableName("MULTI_CALFS");
        descriptor.addPrimaryKeyFieldName("MULTI_COW.ID");
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_COW.CALFS_ID", "MULTI_CALFS.ID");

        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("MULTI_CALFS.ID");
        descriptor.setSequenceNumberName("CALF_COUNT_SEQ");
        descriptor.setAlias("Cow");

        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Mappings.
        DirectToFieldMapping cowIdMapping = new DirectToFieldMapping();
        cowIdMapping.setAttributeName("cowId");
        cowIdMapping.setFieldName("MULTI_COW.ID");
        descriptor.addMapping(cowIdMapping);

        DirectToFieldMapping calfCountIdMapping = new DirectToFieldMapping();
        calfCountIdMapping.setAttributeName("calfCountId");
        calfCountIdMapping.setFieldName("MULTI_CALFS.ID");
        descriptor.addMapping(calfCountIdMapping);

        DirectToFieldMapping nameMapping = new DirectToFieldMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setFieldName("MULTI_COW.NAME");
        nameMapping.setNullValue("");
        descriptor.addMapping(nameMapping);

        DirectToFieldMapping calfCountMapping = new DirectToFieldMapping();
        calfCountMapping.setAttributeName("calfCount");
        calfCountMapping.setFieldName("MULTI_CALFS.CALFS");
        descriptor.addMapping(calfCountMapping);

        return descriptor;
    }
    
    public ClassDescriptor buildHorseDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Horse.class);
        descriptor.addTableName("MULTI_HORSE");
        descriptor.addTableName("MULTI_FOALS");
        descriptor.addPrimaryKeyFieldName("MULTI_HORSE.ID");
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_FOALS.HORSE_ID", "MULTI_HORSE.ID");
        
        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("MULTI_HORSE.ID");
        descriptor.setSequenceNumberName("MULTI_HORSE_SEQ");
        descriptor.setAlias("Horse");

        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Mappings.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("MULTI_HORSE.ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping nameMapping = new DirectToFieldMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setFieldName("MULTI_HORSE.NAME");
        descriptor.addMapping(nameMapping);

        DirectToFieldMapping foalCountMapping = new DirectToFieldMapping();
        foalCountMapping.setAttributeName("foalCount");
        foalCountMapping.setFieldName("MULTI_FOALS.FOALS");
        descriptor.addMapping(foalCountMapping);

        return descriptor;
    }

    public ClassDescriptor buildHumanDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Human.class);
        descriptor.addTableName("MULTI_HUMAN");
        descriptor.addTableName("MULTI_KIDS");
        descriptor.addPrimaryKeyFieldName("MULTI_HUMAN.ID");
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_KIDS.ID", "MULTI_HUMAN.ID");
        
        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("MULTI_HUMAN.ID");
        descriptor.setSequenceNumberName("MULTI_HUMAN_SEQ");
        descriptor.setAlias("Human");

        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Mappings.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("MULTI_HUMAN.ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping nameMapping = new DirectToFieldMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setFieldName("MULTI_HUMAN.NAME");
        descriptor.addMapping(nameMapping);

        DirectToFieldMapping foalCountMapping = new DirectToFieldMapping();
        foalCountMapping.setAttributeName("kidCount");
        foalCountMapping.setFieldName("MULTI_KIDS.KIDS");
        descriptor.addMapping(foalCountMapping);

        return descriptor;
    }
    
    public ClassDescriptor buildSwanDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Swan.class);
        descriptor.addTableName("MULTI_SWAN");
        descriptor.addTableName("MULTI_CYGNETS");
        descriptor.addPrimaryKeyFieldName("MULTI_SWAN.ID");
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_SWAN.ID", "MULTI_CYGNETS.SWAN_ID");
        
        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("MULTI_SWAN.ID");
        descriptor.setSequenceNumberName("MULTI_SWAN_SEQ");
        descriptor.setAlias("Swan");

        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Mappings.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("MULTI_SWAN.ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping nameMapping = new DirectToFieldMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setFieldName("MULTI_SWAN.NAME");
        descriptor.addMapping(nameMapping);

        DirectToFieldMapping foalCountMapping = new DirectToFieldMapping();
        foalCountMapping.setAttributeName("cygnetCount");
        foalCountMapping.setFieldName("MULTI_CYGNETS.CYGNETS");
        descriptor.addMapping(foalCountMapping);

        return descriptor;
    }
}
