/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.multipletable;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

/**
 * Project definition for the MultipleTableSystem
 *
 * @author Guy Pelletier
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
        addDescriptor(buildSuperCowDescriptor());
        addDescriptor(buildSuperHorseDescriptor());
        addDescriptor(buildSuperSwanDescriptor());
    }

    public ClassDescriptor buildCowDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Cow.class);
        descriptor.addTableName("MULTI_COW");
        descriptor.addTableName("MULTI_CALFS");
        descriptor.addTableName("MULTI_COW_AGE");
        descriptor.addTableName("MULTI_COW_WEIGHT");
        descriptor.addPrimaryKeyFieldName("MULTI_COW.ID");

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("MULTI_COW.TYPE");
        descriptor.getInheritancePolicy().addClassIndicator(Cow.class, "C");
        descriptor.getInheritancePolicy().addClassIndicator(SuperCow.class, "SC");
        
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_COW.CALFS_ID", "MULTI_CALFS.C_ID");
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_COW.AGE_ID", "MULTI_COW_AGE.A_ID");
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_COW.WEIGHT_ID", "MULTI_COW_WEIGHT.W_ID");

        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("MULTI_COW.ID");
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
        calfCountIdMapping.setFieldName("MULTI_CALFS.C_ID");
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

        DirectToFieldMapping weightIdMapping = new DirectToFieldMapping();
        weightIdMapping.setAttributeName("weightId");
        weightIdMapping.setFieldName("MULTI_COW.WEIGHT_ID");
        descriptor.addMapping(weightIdMapping);

        DirectToFieldMapping weightMapping = new DirectToFieldMapping();
        weightMapping.setAttributeName("weight");
        weightMapping.setFieldName("MULTI_COW_WEIGHT.WEIGHT");
        descriptor.addMapping(weightMapping);

        DirectToFieldMapping ageIdMapping = new DirectToFieldMapping();
        ageIdMapping.setAttributeName("ageId");
        ageIdMapping.setFieldName("MULTI_COW.AGE_ID");
        descriptor.addMapping(ageIdMapping);

        DirectToFieldMapping ageMapping = new DirectToFieldMapping();
        ageMapping.setAttributeName("age");
        ageMapping.setFieldName("MULTI_COW_AGE.AGE");
        descriptor.addMapping(ageMapping);

        return descriptor;
    }
    
    public ClassDescriptor buildHorseDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Horse.class);
        descriptor.addTableName("MULTI_HORSE");
        descriptor.addTableName("MULTI_FOALS");
        descriptor.addTableName("MULTI_HORSE_AGE");
        descriptor.addTableName("MULTI_HORSE_WEIGHT");
        descriptor.addPrimaryKeyFieldName("MULTI_HORSE.ID");

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("MULTI_HORSE.TYPE");
        descriptor.getInheritancePolicy().addClassIndicator(Horse.class, "H");
        descriptor.getInheritancePolicy().addClassIndicator(SuperHorse.class, "SH");
        
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_FOALS.F_HORSE_ID", "MULTI_HORSE.ID");
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_HORSE_AGE.A_HORSE_ID", "MULTI_HORSE.ID");
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_HORSE_WEIGHT.W_HORSE_ID", "MULTI_HORSE.ID");
        
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

        DirectToFieldMapping weightMapping = new DirectToFieldMapping();
        weightMapping.setAttributeName("weight");
        weightMapping.setFieldName("MULTI_HORSE_WEIGHT.WEIGHT");
        descriptor.addMapping(weightMapping);

        DirectToFieldMapping ageMapping = new DirectToFieldMapping();
        ageMapping.setAttributeName("age");
        ageMapping.setFieldName("MULTI_HORSE_AGE.AGE");
        descriptor.addMapping(ageMapping);

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
        descriptor.addTableName("MULTI_SWAN_AGE");
        descriptor.addTableName("MULTI_SWAN_WEIGHT");
        descriptor.addPrimaryKeyFieldName("MULTI_SWAN.ID");

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("MULTI_SWAN.TYPE");
        descriptor.getInheritancePolicy().addClassIndicator(Swan.class, "S");
        descriptor.getInheritancePolicy().addClassIndicator(SuperSwan.class, "SS");
        
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_SWAN.ID", "MULTI_CYGNETS.C_SWAN_ID");
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_SWAN_AGE.A_SWAN_ID", "MULTI_SWAN.ID");
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_SWAN_WEIGHT.W_SWAN_ID", "MULTI_SWAN.ID");
        
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

        DirectToFieldMapping weightMapping = new DirectToFieldMapping();
        weightMapping.setAttributeName("weight");
        weightMapping.setFieldName("MULTI_SWAN_WEIGHT.WEIGHT");
        descriptor.addMapping(weightMapping);

        DirectToFieldMapping ageMapping = new DirectToFieldMapping();
        ageMapping.setAttributeName("age");
        ageMapping.setFieldName("MULTI_SWAN_AGE.AGE");
        descriptor.addMapping(ageMapping);

        return descriptor;
    }

    public ClassDescriptor buildSuperCowDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(SuperCow.class);
        descriptor.addTableName("MULTI_SUPER_COW");

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setParentClass(Cow.class);
        
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_SUPER_COW.SC_ID", "MULTI_COW.ID");

        // Descriptor Properties.
        descriptor.setAlias("SuperCow");

        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();
        
        // Mappings.
        DirectToFieldMapping speedMapping = new DirectToFieldMapping();
        speedMapping.setAttributeName("speed");
        speedMapping.setFieldName("MULTI_SUPER_COW.SPEED");
        descriptor.addMapping(speedMapping);

        return descriptor;
    }
    
    public ClassDescriptor buildSuperHorseDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(SuperHorse.class);
        descriptor.addTableName("MULTI_SUPER_HORSE");

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setParentClass(Horse.class);
        
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_SUPER_HORSE.SH_ID", "MULTI_HORSE.ID");

        // Descriptor Properties.
        descriptor.setAlias("SuperHorse");

        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();
        
        // Mappings.
        DirectToFieldMapping speedMapping = new DirectToFieldMapping();
        speedMapping.setAttributeName("speed");
        speedMapping.setFieldName("MULTI_SUPER_HORSE.SPEED");
        descriptor.addMapping(speedMapping);

        return descriptor;
    }

    public ClassDescriptor buildSuperSwanDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(SuperSwan.class);
        descriptor.addTableName("MULTI_SUPER_SWAN");
        descriptor.addTableName("MULTI_SWAN_WINGSPAN");

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setParentClass(Swan.class);
        
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_SUPER_SWAN.SS_ID", "MULTI_SWAN.ID");
        descriptor.addForeignKeyFieldNameForMultipleTable("MULTI_SWAN_WINGSPAN.A_SWAN_ID", "MULTI_SUPER_SWAN.SS_ID");
        
        // Descriptor Properties.
        descriptor.setAlias("SuperSwan");

        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();
        
        // Mappings.
        DirectToFieldMapping speedMapping = new DirectToFieldMapping();
        speedMapping.setAttributeName("speed");
        speedMapping.setFieldName("MULTI_SUPER_SWAN.SPEED");
        descriptor.addMapping(speedMapping);
        
        DirectToFieldMapping wingSpanMapping = new DirectToFieldMapping();
        wingSpanMapping.setAttributeName("wingSpan");
        wingSpanMapping.setFieldName("MULTI_SWAN_WINGSPAN.WING_SPAN");
        descriptor.addMapping(wingSpanMapping);

        return descriptor;
    }
}
