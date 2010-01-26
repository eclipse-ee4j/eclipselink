/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
 *               http://wiki.eclipse.org/EclipseLink/Development/Dynamic
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
 ******************************************************************************/
package org.eclipse.persistence.jpa.dynamic;

//EclipseLink imports
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.jpa.CMP3Policy;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Custom CMPPolicy to handle Object[]. The parent class supports only List and
 * ID class for composite keys.
 * 
 * @author dclarke
 * @since EclipseLink 1.2
 */
public class DynamicIdentityPolicy extends CMP3Policy {

    @Override
    public Object createPrimaryKeyFromId(Object key, AbstractSession session) {
        if (key instanceof Object[]) {
            return new CacheId((Object[])key);
        }
        return super.createPrimaryKeyFromId(key, session);
    }

    @Override
    protected KeyElementAccessor[] initializePrimaryKeyFields(Class keyClass, AbstractSession session) {
        if (keyClass == null && getDescriptor().getPrimaryKeyFields().size() > 1) {
            KeyElementAccessor[] result = super.initializePrimaryKeyFields(keyClass, session);
            this.pkClass = Object[].class;
            return result;
        } else {
            return super.initializePrimaryKeyFields(keyClass, session);
        }
    }

}
