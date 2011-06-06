package org.eclipse.persistence.internal.xr;

import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.internal.dynamic.DynamicPropertiesInitializatonPolicy;
import org.eclipse.persistence.internal.dynamic.DynamicTypeImpl;

public class XRDynamicPropertiesInitializatonPolicy extends DynamicPropertiesInitializatonPolicy {

    public XRDynamicPropertiesInitializatonPolicy() {
        super();
    }

    @Override
    public void initializeProperties(DynamicTypeImpl type, DynamicEntityImpl entity) {
        // no-op
    }

}
