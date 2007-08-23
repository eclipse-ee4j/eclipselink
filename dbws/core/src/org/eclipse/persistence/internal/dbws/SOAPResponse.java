// Copyright (c) 2007  Oracle. All rights reserved.  

package org.eclipse.persistence.internal.dbws;

// Javase imports

// Java extension imports

// TopLink imports

/**
 * <p><b>INTERNAL</b>: Helper class holds the value returned for a DBWS Operation
 * Parent class for all DBWS Operations' code-generated Response sub-classes; the contained
 * result field is mapped as appropriate for each code-generated Response sub-class
 * in {@link SOAPResponseWriter}
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */
public class SOAPResponse {
  
    public SOAPResponse() {
      super();
    }
    
    protected Object result = null;
    
    public void setResult(Object result) {
      this.result = result;
    }

    public Object getResult() {
      return result;
    }
}
