/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

/* $Header: MyAttachmentUnmarshaller.java 19-oct-2006.17:18:25 mfobrien Exp $ */
/* Copyright (c) 2006, Oracle. All rights reserved. */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    mfobrien    10/19/06 - Creation
 */

/**
 *  @version $Header: MyAttachmentUnmarshaller.java 19-oct-2006.17:18:25 mfobrien Exp $
 *  @author  mfobrien
 *  @since   11.1
 */
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection;

import java.io.IOException;
import java.util.HashMap;

import javax.activation.DataHandler;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller;

public class MyAttachmentUnmarshaller implements XMLAttachmentUnmarshaller {
    public static final String PHOTO_PATH1 = "http://www.example.com/admin/images/ocom/oralogo_small.gif";
    public static final String PHOTO_BASE64 = "/9j/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAAUCAATAJoEASIAAhEBAxEBBCIA/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADgQBAAIRAxEEAAA/AOp8T63rPivxg/hXQrk21tCdtxKrYJK/fJI5wDxgdSPyUfBaJgDJrkhkPLEW45Pf+Kqmnagng34sasdWBitr95CsxHyqHcOrfTsfQ/SvVF1fTHUMuo2jKeQROpB/WuCnThVcnV1d/uPrMZi8VgI0qeCXLBxTuknzN7tuzPf6KKK83/4UtCOU12VXHIP2YcH/AL6o8Ka7rfhrxavhPX5muYpcLbys24qT90hjyVPTB6HHTmvR31jTI0Z31G0VFGSzTqAB+deV3N4njX4uadLpimS008xlpgOCsbly30ycD/69FSnCk4ulo7/eGExeKzCFWGO96Ci3dpLla2s7L7gooorH0fwjH4u8X69byXjW3kTyOGWPdnMhHqK6X/hStv8A9BuX/wABx/8AFVD4Ag+0+MfFcImlhLs48yJtrL+9PINZXiu78b+FL8R3Gt3UlpIxENyv3WHoeOD7flmsIxpRp88431f5nqVa+Oq4z6th66haMbJrf3Ve2jCiiirdpDqPw78e6bpUWovd2N+Y1aMggEM2z7vOCDyCP/rV63qF/b6Zp899dyCOCBC7sfQenqewHc15z4K8HPqOpQeKtW1mPVH4eHynZhuHTcWAIK/3cDB/KqvxB1i58T+ILbwfo7FwJR9oK9C47H/ZUZJ9/pW9ObpU3K270R5eLw8MwxkKXNdwj+8lay03+a2CiiiuWvZPEPiWfUPGVuJI4rKVdhVuYwOy+u0YJ+ua9l8I+I4vE/h+G+TAnH7u4jH8Eg6/geCPY1w6eNYfCVs3hpvC87R2gML7pQRLnqx+XndnP41yfhLxOvhbxU8ywTwaTdvskhkOSi5+U57lc/kT61lTqxozXvXvv6nfjMDVzDDSSpcvJrTs1rG22jevVBRRRXT69q+ueNfGE/hzQ7lrawtmKzSqxXO04ZmI5IzwAOvX6Tf8KWgIy2uylu5+zDk/99VneGdTt/B3xE1W01JxFa3jN5dy33dpbcjZ/ukHr0z9K9ZXV9NZQy6haFSMgiZcH9a1pU6dW8qmrv8AccOPxeLy/kpYL3afKmmknzXWrbs9QoooridB+FFho+sQahPfPeCA7kieEKN3Ynk9OtZvgL/kqXir/rpP/wCj69JGqaexAF9bEngATLz+teZeCbiC2+J/ilp5o4lMs4BdgoJ873qpwhTnBQ7/AKGNDFYrF0MTLENt8iS0/veQUUUV6xXPeNPEg8L+HZb1ArXLsIrdG6Fz3PsACfwrW/tXTv8An/tf+/y/41wvxVSLVfCqPZXMM72k4meONwzbMEE4B7ZBPtk9q3rTcabcdzyssw0amMpwrL3W1f8ArzCiiiue0vwTqPi+zXV9f8QGI3A3xRMfMO09DgsAox0A7HtV3/hT+nf9DH/5CX/4qsTwz4N8M+IdMSdvED210oxNbybFKt3IyeV9D/Wtr/hVnh7/AKGb/wAej/xrghTUop8l/PmPrcRjJUasqaxLgl9lUtF6BRRRXQv8P7M+Do9EsLtCVm82WZ1yJX9wDxjgDrwMd812Vlbm0sLa2MhkMMSxlz/FgAZ6n+dVNCttP0/SbfT9NlikgtkCfI4bnqScdycn8a0q9GnTjHVI+NxeLrVW4Tk2rt6qz16/8DoFFFFc1420XTtU0GaW9tUllt1zE5yGTJGcEc/hXzjRRXm5gkppn2vB85Sw9SLeienkFFFFFfSPg3RdN0rQYJLG0jhknQNK4yWc+5POPbpRRRl6Tm2LjCco0KcU9G3fzCiiiuN+G3/I9+J/+uj/APo016Xf6faapZSWd9bpPbyfeRxwaKK7cKr0rPz/ADPms+k447mi7NKP/pKCiiivBPA+q32m63eW9ncvFC9tcMyDkFkjZlOD3BA5rp/gzElxfa1fTKJLpREBK3JG8uW/MqPyoorz8K7zgn3f5H1mexUcLiJRVm4wu+/vMKKKK9drh/ivbQzeCJppI1aSCWNo2PVSWAP6GiivTxH8KXofEZQ2sfRt/MvzCiiiuc0/TrTW/hGL3UoRcXVksi28zEhkAbgZHUc9DmvJ6KK8nEpcsH5H6Hk0pOriY30U3ZdvQKKKK6v4bW0N14706OeNZEG9wrdMhCQfzFUfGkaxeNdYVBgG6dvxJyf1NFFS1/s69f0NYyf9ryV9PZr/ANKYUUUVhV6n8F7O3muNZuJYVeWOOKNWbnCtv3DHvtH5UUU8Gr1o/wBdCOI21llVry/9KQUUUVz3xK0iw0fxL5On2y28ToHKITjJx0B6fQVxtFFRiElVkkdOTzlPAUpTd211CiiivoT4ZWkFv4FsZIYlR5y8krDq7byMn8ABXX0UV7dD+FH0R+XZm28bWb/ml+bCiiiv/9k=";
    public static final String XOP_NAMESPACE_PREFIX = "xop";
    public static final String XOP_NAMESPACE_URL = "http://www.w3.org/2004/08/xop/include";
    public static final String ATTACHMENT_PREFIX = "c_id";
    public static final String ATTACHMENT_TEST_ID = "c_id0";
    public boolean getAttachmentAsDataHandlerWasCalled;

	public HashMap attachments;

    
    public MyAttachmentUnmarshaller() {
        getAttachmentAsDataHandlerWasCalled = false;
        attachments = new HashMap();
    }
    
    public byte[] getAttachmentAsByteArray(String cid) {
    	Object obj = attachments.get(cid);
    	if(obj instanceof byte[]){
    		return (byte[])obj;
    	}
    	
        try {
			return ((String)((DataHandler)obj).getContent()).getBytes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

    }

    public DataHandler getAttachmentAsDataHandler(String cid) {
        this.getAttachmentAsDataHandlerWasCalled = true;
        Object obj = attachments.get(cid);        
        if (obj instanceof DataHandler) {
            return (DataHandler)obj;
        }
        return null;
    }

    public boolean isXOPPackage() {
        // force attachment usage
        return true;
    }
    
    public boolean getAttachmentAsDataHandlerWasCalled() {
        return this.getAttachmentAsDataHandlerWasCalled;
    }
}
