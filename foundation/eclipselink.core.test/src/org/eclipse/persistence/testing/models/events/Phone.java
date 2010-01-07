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
package org.eclipse.persistence.testing.models.events;

import java.util.Vector;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

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

    public void aboutToInsert(DescriptorEvent event) {
        aboutToInsertExecuted = true;
    }

    public void aboutToDelete(DescriptorEvent event) {
        aboutToDeleteExecuted = true;
    }

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

        phone.phoneNo = new String("123-4567");
        return phone;
    }

    public static Phone example2() {
        Phone phone = new Phone();

        phone.phoneNo = new String("234-5678");
        return phone;
    }

    public boolean isOverriddenEvent(DescriptorEvent event, Vector eventManagers) {
        return false;
    }

    public void postBuild(DescriptorEvent event) {
        postBuildExecuted = true;
    }

    public void postClone(DescriptorEvent event) {
        postCloneExecuted = true;
    }

    public void postDelete(DescriptorEvent event) {
        postDeleteExecuted = true;
    }

    public void postInsert(DescriptorEvent event) {
        postInsertExecuted = true;
    }

    public void postMerge(DescriptorEvent event) {
        postMergeExecuted = true;
    }

    public void postRefresh(DescriptorEvent event) {
        postRefreshExecuted = true;
    }

    public void postUpdate(DescriptorEvent event) {
        postUpdateExecuted = true;
    }

    public void postWrite(DescriptorEvent event) {
        postWriteExecuted = true;
    }

    public void preCreate(DescriptorEvent event) {
        preCreateExecuted = true;
    }

    public void preDelete(DescriptorEvent event) {
        preDeleteExecuted = true;
    }

    public void preInsert(DescriptorEvent event) {
        preInsertExecuted = true;
    }

    public void preRemove(DescriptorEvent event) {
        preRemoveExecuted = true;
    }

    public void preUpdate(DescriptorEvent event) {
        preUpdateExecuted = true;
    }

    public void preWrite(DescriptorEvent event) {
        preWriteExecuted = true;
    }

    public void prePersist(DescriptorEvent event) {
    }

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
