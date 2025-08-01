/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     12/14/2017-3.0 Tomas Kraus
//       - 291546: Performance degradation due to usage of Vector in DescriptorEventManager
package org.eclipse.persistence.testing.models.events;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

import java.util.List;

public class Phone implements DescriptorEventListener {
    public String phoneNo;
    public Number id;
    public boolean preInsertExecuted;
    public boolean postInsertExecuted;
    public boolean preCreateExecuted;
    public boolean preRemoveExecuted;
    public boolean preUpdateExecuted;
    public boolean postUpdateExecuted;
    public boolean preDeleteExecuted;
    public boolean postDeleteExecuted;
    public boolean preWriteExecuted;
    public boolean postWriteExecuted;
    public boolean postBuildExecuted;
    public boolean postRefreshExecuted;
    public boolean postMergeExecuted;
    public boolean postCloneExecuted;
    public boolean aboutToInsertExecuted;
    public boolean aboutToUpdateExecuted;
    public boolean aboutToDeleteExecuted;

    public Phone() {
        resetFlags();
    }

    @Override
    public void aboutToInsert(DescriptorEvent event) {
        aboutToInsertExecuted = true;
    }

    @Override
    public void aboutToDelete(DescriptorEvent event) {
        aboutToDeleteExecuted = true;
    }

    @Override
    public void aboutToUpdate(DescriptorEvent event) {
        aboutToUpdateExecuted = true;
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.events.Phone.class);
        descriptor.setTableName("EPHONE");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("phoneNo", "DESCR");

        return descriptor;
    }

    public static Phone example1() {
        Phone phone = new Phone();

        phone.phoneNo = "123-4567";
        return phone;
    }

    public static Phone example2() {
        Phone phone = new Phone();

        phone.phoneNo = "234-5678";
        return phone;
    }

    @Override
    public boolean isOverriddenEvent(DescriptorEvent event, List<DescriptorEventManager> eventManagers) {
        return false;
    }

    @Override
    public void postBuild(DescriptorEvent event) {
        postBuildExecuted = true;
    }

    @Override
    public void postClone(DescriptorEvent event) {
        postCloneExecuted = true;
    }

    @Override
    public void postDelete(DescriptorEvent event) {
        postDeleteExecuted = true;
    }

    @Override
    public void postInsert(DescriptorEvent event) {
        postInsertExecuted = true;
    }

    @Override
    public void postMerge(DescriptorEvent event) {
        postMergeExecuted = true;
    }

    @Override
    public void postRefresh(DescriptorEvent event) {
        postRefreshExecuted = true;
    }

    @Override
    public void postUpdate(DescriptorEvent event) {
        postUpdateExecuted = true;
    }

    @Override
    public void postWrite(DescriptorEvent event) {
        postWriteExecuted = true;
    }

    public void preCreate(DescriptorEvent event) {
        preCreateExecuted = true;
    }

    @Override
    public void preDelete(DescriptorEvent event) {
        preDeleteExecuted = true;
    }

    @Override
    public void preInsert(DescriptorEvent event) {
        preInsertExecuted = true;
    }

    @Override
    public void preRemove(DescriptorEvent event) {
        preRemoveExecuted = true;
    }

    @Override
    public void preUpdate(DescriptorEvent event) {
        preUpdateExecuted = true;
    }

    @Override
    public void preWrite(DescriptorEvent event) {
        preWriteExecuted = true;
    }

    @Override
    public void prePersist(DescriptorEvent event) {
    }

    @Override
    public void preUpdateWithChanges(DescriptorEvent event) {
    }

    public void resetFlags() {
        preInsertExecuted = false;
        postInsertExecuted = false;
        preCreateExecuted = false;
        preUpdateExecuted = false;
        postUpdateExecuted = false;
        preDeleteExecuted = false;
        postDeleteExecuted = false;
        preRemoveExecuted = false;
        preWriteExecuted = false;
        postWriteExecuted = false;
        postBuildExecuted = false;
        aboutToInsertExecuted = false;
        aboutToUpdateExecuted = false;
        aboutToDeleteExecuted = false;
        postCloneExecuted = false;
        postMergeExecuted = false;
        postRefreshExecuted = false;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("EPHONE");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("DESCR", String.class, 40);

        return definition;
    }

    public String toString() {
        return "Phone(" + this.phoneNo + ")";
    }
}
