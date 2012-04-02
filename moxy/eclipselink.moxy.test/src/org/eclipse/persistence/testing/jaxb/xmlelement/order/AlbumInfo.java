/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.4 - April 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelement.order;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;

@XmlRootElement
@XmlType(propOrder={"albums", "resultCount"})
public class AlbumInfo {
    private int _resultCount;
    private ArrayList<Album> _albums;

    public AlbumInfo() {
        _albums = new ArrayList<Album>();
    }

    public AlbumInfo(int resultCount, ArrayList<Album> albums) {
        _resultCount = resultCount;
        _albums = albums;
    }

    public int getResultCount() {
        return _resultCount;
    }

    public void setResultCount(int resultCount) {
        _resultCount = resultCount;
    }

    @XmlElement(name="results")
    public ArrayList<Album> getAlbums() {
        return _albums;
    }

    public void setAlbums(ArrayList<Album> _albums) {
        this._albums = _albums;
    }
    
    public boolean equals(Object obj){
    	if(obj instanceof AlbumInfo){
    		AlbumInfo aInfo = (AlbumInfo)obj;
    		return _resultCount == aInfo._resultCount && _albums.equals(aInfo._albums);    		
    	}
    	return false;
    	
    }

}
