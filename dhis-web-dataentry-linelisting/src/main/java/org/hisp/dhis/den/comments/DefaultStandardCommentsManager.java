package org.hisp.dhis.den.comments;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.thoughtworks.xstream.XStream;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: DefaultStandardCommentsManager.java 2869 2007-02-20 14:26:09Z andegje $
 */

public class DefaultStandardCommentsManager
    implements StandardCommentsManager
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private String standardCommentsFile;

    public void setStandardCommentsFile( String standardCommentsFile )
    {
        this.standardCommentsFile = standardCommentsFile;
    }

    // -------------------------------------------------------------------------
    // StandardCommentsManager implementation
    // -------------------------------------------------------------------------

    private List<String> standardComments;

    @SuppressWarnings("unchecked")
    public List<String> getStandardComments() throws StandardCommentsManagerException
    {
        if ( standardComments == null )
        {
            Reader reader = null;
            try 
            {
                reader = new BufferedReader( new InputStreamReader( getClass().getClassLoader().getResourceAsStream( standardCommentsFile ), "UTF-8" ) );
            }
            catch ( UnsupportedEncodingException e )
            {
                throw new StandardCommentsManagerException( "Unsupported encoding", e );
            }
            
            XStream xStream = new XStream();
            standardComments = (List<String>) xStream.fromXML( reader );
        }

        return standardComments;
    }
}
