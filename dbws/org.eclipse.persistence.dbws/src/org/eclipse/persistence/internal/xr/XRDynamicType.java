package org.eclipse.persistence.internal.xr;

//javase imports

//EclipseLink imports
import org.eclipse.persistence.internal.dynamic.DynamicTypeImpl;

/**
 * Dummy implementation of {@link DynamicType}
 * @author mnorman
 *
 */
public class XRDynamicType extends DynamicTypeImpl {

    public XRDynamicType() {
        super();
    }

    public String getName() {
        return "<null>";
    }
}
