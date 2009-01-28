/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     01/28/2009-1.1 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)   
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Lob;

public class ExpertBeerConsumer extends RatedBeerConsumer<String, String, String> {
    private Map<Date, String> quotes;
    private Collection<byte[]> audio;
    
    public ExpertBeerConsumer() {
        super();
        quotes = new HashMap<Date, String>();
        audio = new ArrayList<byte[]>();
    }   
    
    @ElementCollection
    @Lob
    public Collection<byte[]> getAudio() {
        return audio;
    }
    
    public Map<Date, String> getQuotes() {
        return quotes;
    }
    
    public void setQuotes(Map<Date, String> quotes) {
        this.quotes = quotes;
    }

    public void setAudio(Collection<byte[]> audio) {
        this.audio = audio;
    }
}
