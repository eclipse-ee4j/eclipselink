/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.models.jpa22.jta;

/**
 * {@link Animal} entity modification event.
 */
public class AnimalEvent {

    /** Event type. */
    public static enum Type {
        CREATE,
        UPDATE,
        DELETE
    }

    /**
     * Build {@link Animal} entity create event.
     * @param animal new {@link Animal} entity that was created
     * @return {@link Animal} entity create event
     */
    public static AnimalEvent create(final Animal animal) {
        return new AnimalEvent(null, animal, Type.CREATE);
    }

    /**
     * Build {@link Animal} entity update event.
     * @param oldValue {@link Animal} entity before update
     * @param newValue {@link Animal} entity after update
     * @return {@link Animal} entity update event
     */
   public static AnimalEvent update(final Animal oldValue, final Animal newValue) {
        return new AnimalEvent(oldValue, newValue, Type.UPDATE);
    }

    /**
     * Build {@link Animal} entity delete event.
     * @param animal {@link Animal} entity that was deleted
     * @return {@link Animal} entity delete event
     */
    public static AnimalEvent delete(final Animal animal) {
        return new AnimalEvent(animal, null, Type.DELETE);
    }

    /** {@link Animal} entity old value ({@code UPDATE} and {@code DELETE}) events. */
    private final Animal oldValue;

    /** {@link Animal} entity new value ({@code CREATE} and {@code UPDATE}) events. */
    private final Animal newValue;

    /** {@link Animal} entity modification type. */
    private final Type type;

    /**
     * Creates an instance of {@link Animal} entity modification event.
     *
     * @param oldValue {@link Animal} entity old value ({@code UPDATE} and {@code DELETE}) events
     * @param newValue {@link Animal} entity new value ({@code CREATE} and {@code UPDATE}) events
     * @param type {@link Animal} entity modification type
     */
    private AnimalEvent(final Animal oldValue, final Animal newValue, final Type type) {
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.type = type;
    }

    /**
     * Get {@link Animal} entity old value for {@code UPDATE} and {@code DELETE} events.
     *
     * @return {@link Animal} entity old value
     */
    public Animal getOld() {
        return oldValue;
    }

    /**
     * Get {@link Animal} entity new value for {@code CREATE} and {@code UPDATE} events.
     *
     * @return {@link Animal} entity new value
     */
    public Animal getNew() {
        return newValue;
    }

    /**
     * Get {@link Animal} entity modification type.
     *
     * @return {@link Animal} entity modification type
     */
    public Type getType() {
        return type;
    }

}
