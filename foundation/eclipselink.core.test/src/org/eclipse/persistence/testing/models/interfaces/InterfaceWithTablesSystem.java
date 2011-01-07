/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.interfaces;

import java.util.*;

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;

public class InterfaceWithTablesSystem extends TestSystem {
    public void addDescriptors(DatabaseSession session) {
        Vector descriptors = new Vector();

        descriptors.addElement(showDescriptor());
        descriptors.addElement(programDescriptor());
        descriptors.addElement(networkDescriptor());
        descriptors.addElement(scheduleDescriptor());
        descriptors.addElement(commercialDescriptor());
        session.addDescriptors(descriptors);
    }

    public RelationalDescriptor commercialDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Commercial.class);
        descriptor.getInheritancePolicy().setParentClass(Program.class);

        return descriptor;
    }

    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);

        schemaManager.replaceObject(programTable());
        schemaManager.replaceObject(networkTable());
        schemaManager.replaceObject(scheduleTable());
        schemaManager.replaceObject(scheduleProgramTable());

        schemaManager.createSequences();
    }

    public RelationalDescriptor networkDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Network.class);
        descriptor.setTableName("TV_NET");
        descriptor.setPrimaryKeyFieldName("NAME");

        descriptor.addDirectMapping("name", "NAME");

        return descriptor;
    }

    public TableDefinition networkTable() {
        TableDefinition table = new TableDefinition();

        table.setName("TV_NET");

        table.addPrimaryKeyField("NAME", String.class, 20);

        return table;
    }

    public void populate(DatabaseSession session) {
        PopulationManager manager = PopulationManager.getDefaultManager();

        TVSchedule schedule = new TVSchedule();
        schedule.broadcastor = new Network();
        ((Network)schedule.broadcastor).name = "NBC";

        Commercial eatYourWeaties = new Commercial();
        eatYourWeaties.setName("Eat Your Weaties");
        eatYourWeaties.setDescription("Mikey says that he likes it.");
        eatYourWeaties.setDuration(new Float(0.5));

        Show zena = new Show();
        zena.setName("Zena");
        zena.setDescription("Zena the worrier princess");
        zena.setDuration(new Float(20));

        schedule.segments.addElement(eatYourWeaties);
        schedule.segments.addElement(zena);

        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerObject(schedule);
        uow.commit();

        manager.registerObject(schedule, "example1");
        manager.registerObject(schedule.broadcastor, "example1");
        manager.registerObject(eatYourWeaties, "example1");
        manager.registerObject(zena, "example2");
    }

    public RelationalDescriptor programDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Program.class);
        descriptor.setTableName("TV_PROG");
        descriptor.setPrimaryKeyFieldName("NAME");

        descriptor.getInheritancePolicy().setClassIndicatorFieldName("PG_TYPE");
        descriptor.getInheritancePolicy().addClassIndicator(Show.class, "S");
        descriptor.getInheritancePolicy().addClassIndicator(Commercial.class, "C");

        descriptor.addDirectMapping("name", "getName", "setName", "NAME");
        descriptor.addDirectMapping("description", "getDescription", "setDescription", "DESCRIP");

        DirectToFieldMapping durationMapping = new DirectToFieldMapping();
        durationMapping.setAttributeName("duration");
        durationMapping.setGetMethodName("getDuration");
        durationMapping.setSetMethodName("setDuration");
        durationMapping.setFieldName("DUR");
        durationMapping.setAttributeClassification(Float.class);
        descriptor.addMapping(durationMapping);

        return descriptor;
    }

    public TableDefinition programTable() {
        TableDefinition table = new TableDefinition();

        table.setName("TV_PROG");

        table.addPrimaryKeyField("NAME", String.class, 20);
        table.addField("PG_TYPE", Character.class);
        table.addField("DESCRIP", String.class, 200);
        table.addField("DUR", Double.class);

        return table;
    }

    public RelationalDescriptor scheduleDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(TVSchedule.class);
        descriptor.setTableName("TV_SCED");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SCED_SEQ");
        descriptor.setSequenceNumberFieldName("ID");

        descriptor.addDirectMapping("timeSpot", "TM_SPOT");
        descriptor.addDirectMapping("id", "ID");

        OneToOneMapping broadcastorMapping = new OneToOneMapping();
        broadcastorMapping.setAttributeName("broadcastor");
        broadcastorMapping.setReferenceClass(Network.class);
        broadcastorMapping.setForeignKeyFieldName("NET_NAME");
        broadcastorMapping.dontUseIndirection();
        descriptor.addMapping(broadcastorMapping);

        ManyToManyMapping segmentsMapping = new ManyToManyMapping();
        segmentsMapping.setAttributeName("segments");
        segmentsMapping.setReferenceClass(Program.class);
        segmentsMapping.setRelationTableName("TV_SCPG");
        segmentsMapping.setSourceRelationKeyFieldName("SC_ID");
        segmentsMapping.setTargetRelationKeyFieldName("PG_NAME");
        segmentsMapping.dontUseIndirection();
        descriptor.addMapping(segmentsMapping);

        return descriptor;
    }

    public TableDefinition scheduleProgramTable() {
        TableDefinition table = new TableDefinition();

        table.setName("TV_SCPG");

        table.addField("SC_ID", Double.class);
        table.addField("PG_NAME", String.class, 20);

        return table;
    }

    public TableDefinition scheduleTable() {
        TableDefinition table = new TableDefinition();

        table.setName("TV_SCED");

        table.addField("ID", Double.class);
        table.addField("TM_SPOT", java.sql.Timestamp.class);
        table.addField("NET_NAME", String.class, 20);

        table.addForeignKeyConstraint("TV_SCED_TV_NET", "NET_NAME", "NAME", "TV_NET");

        return table;
    }

    public RelationalDescriptor showDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Show.class);
        descriptor.getInheritancePolicy().setParentClass(Program.class);

        return descriptor;
    }
}
