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

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class EmailAccount {
    public String owner;
    public String emailAddress;
    public String hostName;
    public Number id;
    public boolean preInsertExecuted;
    public boolean postInsertExecuted;
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

    public EmailAccount() {
        resetFlags();
    }

    public EmailAccount(String newEmailAddress, String newOwner, String newHostName) {
        setEmailAddress(newEmailAddress);
        setOwner(newOwner);
        setHostName(newHostName);
        resetFlags();
    }

    public void aboutToInsertMethod(DescriptorEvent event) {
        aboutToInsertExecuted = true;
    }

    public void aboutToUpdateMethod(DescriptorEvent event) {
        aboutToUpdateExecuted = true;
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(EmailAccount.class);
        descriptor.setTableName("EMAILACC");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("emailAddress", "EMAILADD");
        descriptor.addDirectMapping("owner", "OWNER");
        descriptor.addDirectMapping("hostName", "HOSTNAME");

        return descriptor;
    }

    public static EmailAccount example1() {
        return new EmailAccount("tedt@geocities.com", "Ted Turner", "mail.geocities.com");
    }

    public static EmailAccount example2() {
        return new EmailAccount("billd@freemail.com", "Bill Dullmar", "mail.freemail.com");
    }

    public static EmailAccount example3() {
        return new EmailAccount("taniad@freemail.com", "Tania Davidson", "mail.freemail.com");
    }

    private String getEmailAddress() {
        return emailAddress;
    }

    private String getHostName() {
        return hostName;
    }

    private String getOwner() {
        return owner;
    }

    public void postBuildMethod(DescriptorEvent event) {
        postBuildExecuted = true;
    }

    public void postCloneMethod(DescriptorEvent event) {
        postCloneExecuted = true;
    }

    public void postDeleteMethod(DescriptorEvent event) {
        postDeleteExecuted = true;
    }

    public void postInsertMethod(DescriptorEvent event) {
        postInsertExecuted = true;
    }

    public void postMergeMethod(DescriptorEvent event) {
        postMergeExecuted = true;
    }

    public void postRefreshMethod(DescriptorEvent event) {
        postRefreshExecuted = true;
    }

    public void postUpdateMethod(DescriptorEvent event) {
        postUpdateExecuted = true;
    }

    public void postWriteMethod(DescriptorEvent event) {
        postWriteExecuted = true;
    }

    public void preDeleteMethod(DescriptorEvent event) {
        preDeleteExecuted = true;
    }

    public void preInsertMethod(DescriptorEvent event) {
        preInsertExecuted = true;
    }

    public void preUpdateMethod(DescriptorEvent event) {
        preUpdateExecuted = true;
    }

    public void preWriteMethod(DescriptorEvent event) {
        preWriteExecuted = true;
    }

    public void resetFlags() {
        preInsertExecuted = false;
        postInsertExecuted = false;
        preUpdateExecuted = false;
        postUpdateExecuted = false;
        preDeleteExecuted = false;
        postDeleteExecuted = false;
        preWriteExecuted = false;
        postWriteExecuted = false;
        postBuildExecuted = false;
        aboutToInsertExecuted = false;
        aboutToUpdateExecuted = false;
        postCloneExecuted = false;
        postMergeExecuted = false;
        postRefreshExecuted = false;
    }

    private void setEmailAddress(String newEmailAddress) {
        emailAddress = newEmailAddress;
    }

    public void setHostName(String newHostName) {
        hostName = newHostName;
    }

    private void setOwner(String newOwner) {
        owner = newOwner;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("EMAILACC");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("EMAILADD", String.class, 40);
        definition.addField("OWNER", String.class, 20);
        definition.addField("HOSTNAME", String.class, 20);

        return definition;
    }

    public String toString() {
        return "EmailAccount(" + getEmailAddress() + ")";
    }
}
