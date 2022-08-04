/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.nosql.adapters.sdk;

import jakarta.resource.cci.InteractionSpec;

import oracle.nosql.driver.Consistency;
import oracle.nosql.driver.Durability;
import oracle.nosql.driver.Version;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.eis.interactions.EISInteraction;
import org.eclipse.persistence.eis.interactions.MappedInteraction;

/**
 * Interaction spec for Oracle NoSQL JCA adapter.
 *
 * @author Radek Felcman
 * @since EclipseLink 4.0
 */
public class OracleNoSQLInteractionSpec implements InteractionSpec {

    public enum InteractionType {
        XML, MAPPED
    }

    private OracleNoSQLOperation operation;
    private Consistency consistency;
    private Durability durability;
    private int timeout;
    private String key;
    private Version version;
    private String tableName;
    private ClassDescriptor descriptor;
    private InteractionType interactionType;

    /**
     * Default constructor.
     */
    public OracleNoSQLInteractionSpec(EISInteraction interaction) {
        if (interaction instanceof MappedInteraction) {
            this.interactionType = InteractionType.MAPPED;
        } else {
            this.interactionType = InteractionType.XML;
        }
    }

    public OracleNoSQLOperation getOperation() {
        return operation;
    }

    public void setOperation(OracleNoSQLOperation operation) {
        this.operation = operation;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Consistency getConsistency() {
        return consistency;
    }

    public void setConsistency(Consistency consistency) {
        this.consistency = consistency;
    }

    public Durability getDurability() {
        return durability;
    }

    public void setDurability(Durability durability) {
        this.durability = durability;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public ClassDescriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public InteractionType getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(InteractionType interactionType) {
        this.interactionType = interactionType;
    }

    @Override
    public String toString() {
        return getClass().getName() + "(" + getOperation() + ")";
    }
}
