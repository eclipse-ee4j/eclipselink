/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.entityagent.model;

import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityResult;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NamedStatement;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.StoredProcedureParameter;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Transient;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity(name = "AgentQueryRecord")
@Table(name = "EA_QUERY_RECORD")
@NamedQuery(name = "EAQuery.byId", query = "select r from AgentQueryRecord r where r.id = :id",
        resultClass = AgentQueryRecord.class)
@NamedQuery(name = "EAQuery.byTitle", query = "select r from AgentQueryRecord r where r.title = :title",
        resultClass = AgentQueryRecord.class)
@NamedNativeQuery(name = "EAQuery.nativeById", query = "select * from EA_QUERY_RECORD where ID = ?1",
        resultClass = AgentQueryRecord.class)
@NamedStatement(name = "EAQuery.rename", statement = "update AgentQueryRecord r set r.title = :title where r.id = :id")
@NamedStoredProcedureQuery(name = "EAQuery.rows", procedureName = "EA_QUERY_PROC_ROWS",
        resultClasses = AgentQueryRecord.class,
        parameters = @StoredProcedureParameter(mode = ParameterMode.IN, type = Integer.class))
@NamedEntityGraph(name = "EAQuery.group", attributeNodes = @NamedAttributeNode("group"))
@SqlResultSetMapping(name = "EAQuery.recordMapping", entities = @EntityResult(entityClass = AgentQueryRecord.class))
@SqlResultSetMapping(name = "EAQuery.summaryMapping", classes = @ConstructorResult(
        targetClass = AgentQueryRecord.Summary.class,
        columns = {@ColumnResult(name = "ID", type = Integer.class), @ColumnResult(name = "TITLE", type = String.class)}))
@SqlResultSetMapping(name = "EAQuery.titleMapping", columns = @ColumnResult(name = "TITLE", type = String.class))
@SqlResultSetMapping(name = "EAQuery.lockedMapping", entities = @EntityResult(
        entityClass = AgentQueryRecord.class, lockMode = LockModeType.PESSIMISTIC_WRITE))
public class AgentQueryRecord {
    public record Summary(Integer id, String title) {
    }

    @Transient
    public int loadCallbacks;

    @PostLoad
    void loaded() {
        loadCallbacks++;
    }

    @Id
    public Integer id;
    @Column(name = "TITLE")
    public String title;
    @Convert(converter = AgentQueryCodeConverter.class)
    public AgentQueryCode code;
    @Version
    public Integer version;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    public AgentQueryGroup group;

    public AgentQueryGroup getGroup() {
        return group;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AgentQueryRecord() {
    }

    public AgentQueryRecord(int id, String title, AgentQueryGroup group) {
        this.id = id;
        this.title = title;
        this.code = new AgentQueryCode(title);
        this.group = group;
    }
}
