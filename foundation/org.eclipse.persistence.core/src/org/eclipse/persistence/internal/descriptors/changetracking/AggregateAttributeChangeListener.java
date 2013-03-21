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
package org.eclipse.persistence.internal.descriptors.changetracking;

import java.beans.*;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * <p>
 * <b>Purpose</b>: Define a listener for attribute change tracking.
 * <p>
 * <b>Description</b>: Listener is notified on a PropertyChangeEvent from the object it belongs to.
 * <p>
 * <b>Responsibilities</b>: Set the flag to true and build ObjectChangeSet that includes the
 * ChangeRecords for the changed attributes.
 */
public class AggregateAttributeChangeListener extends AttributeChangeListener {

    //Used when the current ChangeListener is within an Aggregate.
    protected AttributeChangeListener parentListener;
    protected String parentAttributeName;

    /**
     * Create a AttributeChangeListener with a descriptor and unit of work
     */
    public AggregateAttributeChangeListener(ClassDescriptor descriptor, UnitOfWorkImpl uow, AttributeChangeListener parentListener, String parentAttribute, Object owner) {
        super(descriptor, uow, owner);
        this.parentListener = parentListener;
        this.parentAttributeName = parentAttribute;
    }

    /**
     * This method creates the object change set if necessary.  It also creates/updates
     * the change record based on the new value.  Object should check the if newValue and
     * oldValue are identical.  If they are identical, do not create PropertyChangeEvent
     * and call this method.
     */
    public void internalPropertyChange(PropertyChangeEvent evt) {
        super.internalPropertyChange(evt);
        // Also need to raise the event in the parent, to create a change set for it.
        // Only raise the event in parent if something actually changed.
        if (this.objectChangeSet != null && this.parentListener != null) {
            if (evt.getSource() == owner){
                this.parentListener.internalPropertyChange(new PropertyChangeEvent(evt.getSource(), parentAttributeName, evt.getOldValue(), evt.getNewValue()));
            } else {
                // the event's source is not the owner of this change tracking policy - this is a nested aggregate change.
                this.parentListener.internalPropertyChange(new PropertyChangeEvent(owner, parentAttributeName, evt.getOldValue(), evt.getNewValue()));
            }
        }
    }
    /**
     * Used to control the parent listener;
     */
    public void setParentListener(AttributeChangeListener listener){
        this.parentListener = listener;
    }
}
