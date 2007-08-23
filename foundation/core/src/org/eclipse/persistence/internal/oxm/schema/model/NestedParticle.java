/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.schema.model;

/**
 * <p><b>Purpose</b>: Interface to represent a Choice or Sequence
 * @see org.eclipse.persistence.internal.oxm.schema.model.Sequence
 * @see org.eclipse.persistence.internal.oxm.schema.model.Choice
 */
public interface NestedParticle extends TypeDefParticleOwner {
    public void addSequence(Sequence sequence);

    public void addChoice(Choice choice);

    public void addElement(Element elem);

    public void addAny(Any any);

    public boolean hasAny();

    public void setSequences(java.util.List sequences);

    public void setChoices(java.util.List choices);

    public void setAnys(java.util.List anys);

    public void setElements(java.util.List elements);

    public void setMinOccurs(String minOccurs);

    public String getMinOccurs();

    public String getMaxOccurs();

    public void setMaxOccurs(String maxOccurs);

    public void setOwner(TypeDefParticleOwner owner);

    public boolean isEmpty();
}