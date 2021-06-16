/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm.schema.model;

/**
 * <p><b>Purpose</b>: Interface to represent a Choice or Sequence
 * @see org.eclipse.persistence.internal.oxm.schema.model.Sequence
 * @see org.eclipse.persistence.internal.oxm.schema.model.Choice
 */
public interface NestedParticle extends TypeDefParticleOwner {
    void addSequence(Sequence sequence);

    void addChoice(Choice choice);

    void addElement(Element elem);

    void addAny(Any any);

    boolean hasAny();

    void setSequences(java.util.List sequences);

    void setChoices(java.util.List choices);

    void setAnys(java.util.List anys);

    void setElements(java.util.List elements);

    void setMinOccurs(String minOccurs);

    String getMinOccurs();

    String getMaxOccurs();

    void setMaxOccurs(String maxOccurs);

    void setOwner(TypeDefParticleOwner owner);

    boolean isEmpty();
}
