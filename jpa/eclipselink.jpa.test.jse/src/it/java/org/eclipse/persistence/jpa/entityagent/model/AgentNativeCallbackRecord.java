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

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostInsert;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PostUpsert;
import jakarta.persistence.PreInsert;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PreUpsert;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.descriptors.DescriptorEventManager;

@Entity
@Table(name = "EA_NATIVE_CALLBACK")
@AgentNativeCallbackRecord.ValidationProbe(event = "insert", groups = AgentNativeCallbackRecord.InsertChecks.class)
@AgentNativeCallbackRecord.ValidationProbe(event = "update", groups = AgentNativeCallbackRecord.UpdateChecks.class)
@AgentNativeCallbackRecord.ValidationProbe(event = "upsert", groups = AgentNativeCallbackRecord.UpsertChecks.class)
public class AgentNativeCallbackRecord implements DescriptorEventListener {
    public interface InsertChecks { }
    public interface UpdateChecks { }
    public interface UpsertChecks { }

    @Id
    public Integer id;
    public String name;
    @Version
    public Integer version;
    @Transient
    public List<String> callbacks = new ArrayList<>();
    @Transient
    public List<String> nativeEvents = new ArrayList<>();
    @Transient
    public boolean valid = true;
    @Transient
    public boolean failUpsert;
    @Transient
    public Runnable nestedOperation;

    public AgentNativeCallbackRecord() {
    }

    public AgentNativeCallbackRecord(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @PrePersist
    void beforePersist() { callbacks.add("pre-persist"); }
    @PostPersist
    void afterPersist() { callbacks.add("post-persist"); }
    @PreInsert
    void beforeInsert() { callbacks.add("pre-insert"); }
    @PostInsert
    void afterInsert() { callbacks.add("post-insert"); }
    @PreUpdate
    void beforeUpdate() { callbacks.add("pre-update"); }
    @PostUpdate
    void afterUpdate() { callbacks.add("post-update"); }
    @PreUpsert
    void beforeUpsert() {
        callbacks.add("pre-upsert");
        if (failUpsert) {
            throw new IllegalStateException("upsert callback failure");
        }
        if (nestedOperation != null) {
            nestedOperation.run();
        }
    }
    @PostUpsert
    void afterUpsert() { callbacks.add("post-upsert"); }

    @Override
    public void preInsert(DescriptorEvent event) { nativeEvents.add("entity-pre-insert"); }
    @Override
    public void postInsert(DescriptorEvent event) { nativeEvents.add("entity-post-insert"); }
    @Override
    public void preUpdate(DescriptorEvent event) { nativeEvents.add("entity-pre-update"); }
    @Override
    public void preUpdateWithChanges(DescriptorEvent event) { nativeEvents.add("entity-pre-update-changes"); }
    @Override
    public void postUpdate(DescriptorEvent event) { nativeEvents.add("entity-post-update"); }

    @Override
    public void aboutToInsert(DescriptorEvent event) { }
    @Override
    public void aboutToUpdate(DescriptorEvent event) { }
    @Override
    public void aboutToDelete(DescriptorEvent event) { }
    @Override
    public void postBuild(DescriptorEvent event) { }
    @Override
    public void postClone(DescriptorEvent event) { }
    @Override
    public void postDelete(DescriptorEvent event) { }
    @Override
    public void postMerge(DescriptorEvent event) { }
    @Override
    public void postRefresh(DescriptorEvent event) { }
    @Override
    public void postWrite(DescriptorEvent event) { }
    @Override
    public void prePersist(DescriptorEvent event) { }
    @Override
    public void preDelete(DescriptorEvent event) { }
    @Override
    public void preRemove(DescriptorEvent event) { }
    @Override
    public void preWrite(DescriptorEvent event) { }
    @Override
    public boolean isOverriddenEvent(DescriptorEvent event, List<DescriptorEventManager> eventManagers) {
        return false;
    }

    @Constraint(validatedBy = ProbeValidator.class)
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(ValidationProbes.class)
    public @interface ValidationProbe {
        String message() default "invalid callback record";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
        String event();
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValidationProbes {
        ValidationProbe[] value();
    }

    public static class ProbeValidator implements ConstraintValidator<ValidationProbe, AgentNativeCallbackRecord> {
        private String event;

        @Override
        public void initialize(ValidationProbe annotation) {
            event = annotation.event();
        }

        @Override
        public boolean isValid(AgentNativeCallbackRecord record, ConstraintValidatorContext context) {
            record.callbacks.add("validate-" + event);
            return record.valid;
        }
    }
}
