/**
 * 
 */
package org.eclipse.persistence.internal.xr;

public class IndexInfo {
    public int index = -1;
    public boolean derefVH = false;
    public IndexInfo(int index, boolean derefVH) {
        this.index = index;
        this.derefVH = derefVH;
    }
}