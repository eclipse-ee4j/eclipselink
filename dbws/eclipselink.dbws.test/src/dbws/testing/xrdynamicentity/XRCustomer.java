package dbws.testing.xrdynamicentity;

import org.eclipse.persistence.internal.xr.XRFieldInfo;
import org.eclipse.persistence.internal.xr.XRDynamicEntity;

public class XRCustomer extends XRDynamicEntity {

    public static XRFieldInfo XRFI = new XRFieldInfo();
    
    public XRCustomer() {
        super();
    }

    @Override
    public XRFieldInfo getFieldInfo() {
        return XRFI;
    }
}