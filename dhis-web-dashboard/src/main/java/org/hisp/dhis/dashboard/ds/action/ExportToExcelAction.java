package org.hisp.dhis.dashboard.ds.action;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import com.opensymphony.xwork2.ActionSupport;

public class ExportToExcelAction extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------
    
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String contentType;

    public String getContentType()
    {
        return contentType;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    private int bufferSize;

    public int getBufferSize()
    {
        return bufferSize;
    }

    
    private String htmlCode;
    
    public void setHtmlCode( String htmlCode )
    {
        this.htmlCode = htmlCode;
    }

    /*
    private StringBuffer htmlCode;
    
    public void setHtmlCode( StringBuffer htmlCode )
    {
        this.htmlCode = htmlCode;
    }
*/
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {                        

        fileName = "dataStatusResult.xls";

        inputStream = new BufferedInputStream( new ByteArrayInputStream( htmlCode.getBytes("UTF-8") ) );
        
        return SUCCESS;
    }


}
