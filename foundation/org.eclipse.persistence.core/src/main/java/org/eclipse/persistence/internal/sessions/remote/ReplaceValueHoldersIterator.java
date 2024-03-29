/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.sessions.remote;

import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.indirection.UnitOfWorkValueHolder;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;

import java.util.Map;

/**
 * Helper class for RemoteSessionController.
 * This descriptor iterator will replace all the normal
 * value holders in an object with remote value holders
 * that can be serialized to the client.
 */
public class ReplaceValueHoldersIterator extends DescriptorIterator {
    RemoteSessionController controller;

    /**
     * default constructor
     */
    private ReplaceValueHoldersIterator() {
        super();
    }

    /**
     * public constructor
     */
    public ReplaceValueHoldersIterator(RemoteSessionController controller) {
        this();
        this.initialize(controller);
    }

    /**
     * build and return an object descriptor for the specified object
     */
    protected ObjectDescriptor buildObjectDescriptor(Object object) {
        return controller.buildObjectDescriptor(object);
    }

    /**
     * build a properly initialized remote value holder
     */
    protected RemoteValueHolder buildRemoteValueHolderFor(ValueHolderInterface valueHolder) {
        RemoteValueHolder remoteValueHolder = null;

        if (valueHolder instanceof RemoteValueHolder) {
            remoteValueHolder = (RemoteValueHolder)valueHolder;
        } else if ((valueHolder instanceof UnitOfWorkValueHolder) && ((UnitOfWorkValueHolder)valueHolder).getWrappedValueHolder() instanceof RemoteValueHolder) {
            return (RemoteValueHolder)((UnitOfWorkValueHolder)valueHolder).getWrappedValueHolder();
        } else {
            remoteValueHolder = new RemoteValueHolder();
            remoteValueHolder.setWrappedServerValueHolder(valueHolder);
            remoteValueHolder.setMapping(this.getCurrentMapping());
        }
        saveRemoteValueHolder(remoteValueHolder);
        return remoteValueHolder;
    }

    /**
     * initialize instance
     */
    protected void initialize(RemoteSessionController controller) {
        this.controller = controller;
    }

    /**
     * Iterate an indirect container.
     */
    @Override
    protected void internalIterateIndirectContainer(IndirectContainer container) {
        ValueHolderInterface containedValueHolder = container.getValueHolder();

        if (containedValueHolder instanceof RemoteValueHolder) {
            containedValueHolder = ((RemoteValueHolder)containedValueHolder).getWrappedServerValueHolder();
        }

        synchronized (containedValueHolder) {
            // extract VH within sync block to ensure correct one is used.
            ValueHolderInterface valueHolder = container.getValueHolder();
            RemoteValueHolder remoteValueHolder = buildRemoteValueHolderFor(valueHolder);

            container.setValueHolder(remoteValueHolder);
            remoteValueHolder.setServerIndirectionObject(container);
        }
    }

    /**
     * Synchronously create a remote value holder. The value holder passed in is ignored
     * so that we can ensure that the value holder being held by the object is used
     * instead of the one that was passed in.
     */
    @Override
    protected void internalIterateValueHolder(ValueHolderInterface originalValueHolder) {
        ValueHolderInterface rootValueHolder = originalValueHolder;

        // Ensure we have the base server side value holder. There will only be one of these per server.
        if (rootValueHolder instanceof RemoteValueHolder) {
            rootValueHolder = ((RemoteValueHolder)rootValueHolder).getWrappedServerValueHolder();
        }
        synchronized (rootValueHolder) {
            ValueHolderInterface valueHolder = (ValueHolderInterface)getCurrentMapping().getAttributeValueFromObject(getVisitedParent());
            RemoteValueHolder remoteValueHolder = buildRemoteValueHolderFor(valueHolder);

            if (valueHolder != remoteValueHolder) {
                if (this.getCurrentMapping().isOneToOneMapping()) {
                    this.setOneToOneMappingSettingsIn(remoteValueHolder);
                }
                this.getCurrentMapping().setAttributeValueInObject(this.getVisitedParent(), remoteValueHolder);
            }
        }
    }

    /**
     * if we get here, it is a domain object
     */
    @Override
    protected void iterate(Object object) {
        ((Map)getResult()).put(object, this.buildObjectDescriptor(object));
    }

    /**
     * save the remote value holder for later use
     */
    protected void saveRemoteValueHolder(RemoteValueHolder remoteValueHolder) {
        controller.saveRemoteValueHolder(remoteValueHolder);
    }

    /**
     * Set one-to-one mapping information.
     * This allows us to look for a cache hit
     * before going to the database.
     */
    protected void setOneToOneMappingSettingsIn(RemoteValueHolder remoteValueHolder) {
        ObjectReferenceMapping oneToOneMapping = (ObjectReferenceMapping)this.getCurrentMapping();// cast it
        if (oneToOneMapping.getDescriptor().getObjectBuilder().isPrimaryKeyMapping(oneToOneMapping)) {
            remoteValueHolder.setRow(oneToOneMapping.extractPrimaryKeyRowForSourceObject(this.getVisitedParent(), this.getSession()));
        }
        remoteValueHolder.setTargetObjectPrimaryKeys(oneToOneMapping.extractPrimaryKeysForReferenceObject(this.getVisitedParent(), this.getSession()));
    }
}
