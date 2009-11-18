package org.hisp.dhis.dashboard.nr.action;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
/*
    private String contentType;

    public String getContentType()
    {
        return contentType;
    }
*/
    private String fileName;

    public String getFileName()
    {
        return fileName;
    }
/*
    private int bufferSize;

    public int getBufferSize()
    {
        return bufferSize;
    }
*/
    private String htmlCode;
    
    public void setHtmlCode( String htmlCode )
    {
        this.htmlCode = htmlCode;
    }
        
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {                        

        fileName = "NullReporterResult.xls";

        //String zipFilePath = "c:/MonthlySC.xls";
        //inputStream = new BufferedInputStream( new FileInputStream(zipFilePath) , 1024 );
        inputStream = new BufferedInputStream( new ByteArrayInputStream( htmlCode.getBytes("UTF-8") ) );
        
        return SUCCESS;
    }


}
