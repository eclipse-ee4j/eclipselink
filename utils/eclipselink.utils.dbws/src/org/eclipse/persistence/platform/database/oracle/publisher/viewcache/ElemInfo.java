package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

public class ElemInfo {
    protected ElemInfo() {
    }

    public ElemInfo(AllCollTypes r) {
        elemTypeName = r.elemTypeName;
        elemTypeOwner = r.elemTypeOwner;
        elemTypeMod = r.elemTypeMod;
        elemTypeLength = r.elemLength;
        elemTypePrecision = r.elemPrecision;
        elemTypeScale = r.elemScale;
    }

    public String toString() {
        return elemTypeOwner + "," + elemTypeName + "," + elemTypeMod + "," + elemTypeLength + ","
            + elemTypePrecision + "," + elemTypeScale;

    }

    public String elemTypeName;
    public String elemTypeOwner;
    public String elemTypeMod;
    public int elemTypeLength;
    public int elemTypePrecision;
    public int elemTypeScale;
}
