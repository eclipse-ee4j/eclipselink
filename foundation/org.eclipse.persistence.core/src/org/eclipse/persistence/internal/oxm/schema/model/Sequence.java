/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.oxm.schema.model;

import java.util.ArrayList;

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
                getElements().add(next);
            }
        }
    }

    public void setSequences(java.util.List sequences) {
        if ((sequences != null) && (sequences.size() > 0)) {
            for (int i = 0; i < sequences.size(); i++) {
                ((Sequence)sequences.get(i)).setOwner(this);
            }

            orderedElements.addAll(sequences);
        }
    }

    public void setChoices(java.util.List choices) {
        if ((choices != null) && (choices.size() > 0)) {
            for (int i = 0; i < choices.size(); i++) {
                ((Choice)choices.get(i)).setOwner(this);
            }

            orderedElements.addAll(choices);
        }
    }

    public void setNestedParticles(java.util.List nestedParticles) {
        for (int i = 0; i < nestedParticles.size(); i++) {
            NestedParticle next = (NestedParticle)nestedParticles.get(i);
            if (next instanceof Choice) {
                addChoice((Choice)next);
            } else if (next instanceof Sequence) {
                addSequence((Sequence)next);
            }
        }
    }

    public void addSequence(Sequence sequence) {
        orderedElements.add(sequence);
        sequence.setOwner(this);
    }

    public void addChoice(Choice choice) {
        orderedElements.add(choice);
        choice.setOwner(this);
    }

    public void addElement(Element elem) {
        orderedElements.add(elem);
        getElements().add(elem);
    }

    public void addAny(Any any) {
        orderedElements.add(any);

    }

    public void setElements(java.util.List elements) {
        orderedElements.addAll(elements);
        getElements().addAll(elements);
    }

    public void setAnys(java.util.List anys) {
        orderedElements.addAll(anys);
    }

    public boolean hasAny() {
        for (int i = 0; i < orderedElements.size(); i++) {
            Object next = orderedElements.get(i);
            if (next instanceof Any) {
                return true;
            }
        }
        return false;

    }

    public boolean isEmpty() {
        return !(orderedElements.size() > 0);
    }

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
