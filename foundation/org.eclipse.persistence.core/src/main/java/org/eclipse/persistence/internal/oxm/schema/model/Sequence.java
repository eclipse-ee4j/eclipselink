/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.oxm.schema.model;

import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>Purpose</b>: Class to represent a Sequence in a Schema
 */
public class Sequence extends TypeDefParticle implements NestedParticle{
    protected java.util.List orderedElements;

    public Sequence() {
        orderedElements = new ArrayList();
    }

    public java.util.List getOrderedElements() {
        return orderedElements;
    }

    public void setOrderedElements(java.util.List newElements) {
        orderedElements = newElements;
        for (int i = 0; i < newElements.size(); i++) {
            Object next = newElements.get(i);
            if (next instanceof Element) {
                getElements().add((Element) next);
            }
        }
    }

    @Override
    public void setSequences(List<Sequence> sequences) {
        if ((sequences != null) && (!sequences.isEmpty())) {
            for (int i = 0; i < sequences.size(); i++) {
                sequences.get(i).setOwner(this);
            }

            orderedElements.addAll(sequences);
        }
    }

    @Override
    public void setChoices(List<Choice> choices) {
        if ((choices != null) && (!choices.isEmpty())) {
            for (int i = 0; i < choices.size(); i++) {
                choices.get(i).setOwner(this);
            }

            orderedElements.addAll(choices);
        }
    }

    public void setNestedParticles(List<? extends NestedParticle> nestedParticles) {
        for (int i = 0; i < nestedParticles.size(); i++) {
            NestedParticle next = nestedParticles.get(i);
            if (next instanceof Choice) {
                addChoice((Choice)next);
            } else if (next instanceof Sequence) {
                addSequence((Sequence)next);
            }
        }
    }

    @Override
    public void addSequence(Sequence sequence) {
        orderedElements.add(sequence);
        sequence.setOwner(this);
    }

    @Override
    public void addChoice(Choice choice) {
        orderedElements.add(choice);
        choice.setOwner(this);
    }

    @Override
    public void addElement(Element elem) {
        orderedElements.add(elem);
        getElements().add(elem);
    }

    @Override
    public void addAny(Any any) {
        orderedElements.add(any);

    }

    @Override
    public void setElements(List<Element> elements) {
        orderedElements.addAll(elements);
        getElements().addAll(elements);
    }

    @Override
    public void setAnys(List<Any> anys) {
        orderedElements.addAll(anys);
    }

    @Override
    public boolean hasAny() {
        for (int i = 0; i < orderedElements.size(); i++) {
            Object next = orderedElements.get(i);
            if (next instanceof Any) {
                return true;
            }
        }
        return false;

    }

    @Override
    public boolean isEmpty() {
        return !(!orderedElements.isEmpty());
    }

    @Override
    public void setOwner(TypeDefParticleOwner owner) {
        super.setOwner(owner);
        for (int i = 0; i < orderedElements.size(); i++) {
            Object next = orderedElements.get(i);
            if (next instanceof TypeDefParticle) {
                ((TypeDefParticle)next).setOwner(this);
            }
        }
    }
}
