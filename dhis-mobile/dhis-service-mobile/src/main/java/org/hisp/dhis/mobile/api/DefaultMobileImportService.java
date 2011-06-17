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
package org.hisp.dhis.mobile.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.amplecode.quick.BatchHandlerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.mobile.SmsService;
import org.hisp.dhis.mobile.api.ReceiveSMSService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DefaultMobileImportService
    implements MobileImportService
{

    private static final Log LOG = LogFactory.getLog( DefaultMobileImportService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private SendSMSService sendSMSService;
    
    public void setSendSMSService( SendSMSService sendSMSService )
    {
        this.sendSMSService = sendSMSService;
    }

    private ReceiveSMSService receiveSMSService;
    
    public void setReceiveSMSService( ReceiveSMSService receiveSMSService )
    {
        this.receiveSMSService = receiveSMSService;
    }

    SmsService smsService;

    public void setSmsService( SmsService smsService )
    {
        this.smsService = smsService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private LocationManager locationManager;

    public void setLocationManager( LocationManager locationManager )
    {
        this.locationManager = locationManager;
    }

    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------

    private String storedBy;

    // -------------------------------------------------------------------------
    // Services
    // -------------------------------------------------------------------------
    
    @Override
    public void readAllMessages()
    {
        smsService.readAllMessages();
        System.out.println("Message reading done");
    }
    
    @Override
    public User getUserInfo( String mobileNumber )
    {
        Collection<User> userList = userStore.getUsersByPhoneNumber( mobileNumber );

        User selectedUser = null;

        if ( userList != null && userList.size() > 0 )
        {
            selectedUser = userList.iterator().next();
        }

        return selectedUser;
    }

    @Override
    public Period getPeriodInfo( String startDate, String periodType )
        throws Exception
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        List<Period> periods = null;
        PeriodType pt = null;
        if ( periodType.equals( "3" ) )
        {
            pt = new MonthlyPeriodType();
            periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( pt ) );
        }
        else if ( periodType.equals( "1" ) )
        {
            pt = new DailyPeriodType();
            periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( pt ) );
        }
        else if( periodType.equals( "6" ) )
        {
            pt = new YearlyPeriodType();
            periods =  new ArrayList<Period>( periodService.getPeriodsByPeriodType( pt ) );
        }
        else if( periodType.equals( "2" ) )
        {
            pt = new WeeklyPeriodType();
            periods =  new ArrayList<Period>( periodService.getPeriodsByPeriodType( pt ) );
        }
        
        for ( Period period : periods )
        {
            String tempDate = dateFormat.format( period.getStartDate() );
            if ( tempDate.equalsIgnoreCase( startDate ) )
            {
                return period;
            }
        }

        Period period = pt.createPeriod( dateFormat.parse( startDate ) );
        period = reloadPeriodForceAdd( period );
        periodService.addPeriod( period );

        return period;
    }

    private final Period reloadPeriod( Period period )
    {
        return periodService.getPeriod( period.getStartDate(), period.getEndDate(), period.getPeriodType() );
    }

    private final Period reloadPeriodForceAdd( Period period )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            periodService.addPeriod( period );

            return period;
        }

        return storedPeriod;
    }

    @Override
    public MobileImportParameters getParametersFromXML( String fileName )
        throws Exception
    {
        File importFile = locationManager.getFileForReading( fileName, "mi", "pending" );

        String mobileNumber;
        String smsTime;
        String startDate;
        String periodType;
        String formType;
        String anmName;
        String anmQuery;

        String tempDeid;
        String tempDataValue;

        Map<String, String> dataValues = new HashMap<String, String>();

        MobileImportParameters mobileImportParameters = new MobileImportParameters();

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( importFile );
            if ( doc == null )
            {
                return null;
            }

            // To get Mobile Number
            NodeList sourceInfo = doc.getElementsByTagName( "source" );
            Element sourceInfoElement = (Element) sourceInfo.item( 0 );
            NodeList textsourceInfoNameList = sourceInfoElement.getChildNodes();
            mobileNumber = textsourceInfoNameList.item( 0 ).getNodeValue().trim();

            mobileImportParameters.setMobileNumber( mobileNumber );

            // To get Period
            NodeList periodInfo = doc.getElementsByTagName( "period" );
            Element periodInfoElement = (Element) periodInfo.item( 0 );
            NodeList textperiodInfoNameList = periodInfoElement.getChildNodes();
            startDate = textperiodInfoNameList.item( 0 ).getNodeValue().trim();

            mobileImportParameters.setStartDate( startDate );

            // To get TimeStamp
            NodeList timeStampInfo = doc.getElementsByTagName( "timeStamp" );
            Element timeStampInfoElement = (Element) timeStampInfo.item( 0 );
            NodeList texttimeStampInfoNameList = timeStampInfoElement.getChildNodes();
            smsTime = texttimeStampInfoNameList.item( 0 ).getNodeValue().trim();

            mobileImportParameters.setSmsTime( smsTime );

            // To get PeriodType
            NodeList periodTypeInfo = doc.getElementsByTagName( "periodType" );
            Element periodTypeInfoElement = (Element) periodTypeInfo.item( 0 );
            NodeList textPeriodTypeInfoNameList = periodTypeInfoElement.getChildNodes();
            periodType = textPeriodTypeInfoNameList.item( 0 ).getNodeValue().trim();

            mobileImportParameters.setPeriodType( periodType );

            // To get FormType
            NodeList formTypeInfo = doc.getElementsByTagName( "formtype" );
            Element formTypeInfoElement = (Element) formTypeInfo.item( 0 );
            NodeList formTypeInfoNameList = formTypeInfoElement.getChildNodes();
            formType = formTypeInfoNameList.item( 0 ).getNodeValue().trim();

            mobileImportParameters.setFormType( formType );
            
            if( formType.equalsIgnoreCase( MobileImportParameters.FORM_TYPE_DATAFORM ) )
            {
                NodeList listOfDataValues = doc.getElementsByTagName( "dataValue" );
                int totalDataValues = listOfDataValues.getLength();
                for ( int s = 0; s < totalDataValues; s++ )
                {
                    Node dataValueNode = listOfDataValues.item( s );
                    if ( dataValueNode.getNodeType() == Node.ELEMENT_NODE )
                    {
                        Element dataValueElement = (Element) dataValueNode;
    
                        NodeList dataElementIdList = dataValueElement.getElementsByTagName( "dataElement" );
                        Element dataElementElement = (Element) dataElementIdList.item( 0 );
                        NodeList textdataElementIdList = dataElementElement.getChildNodes();
                        tempDeid = textdataElementIdList.item( 0 ).getNodeValue().trim();
    
                        NodeList valueList = dataValueElement.getElementsByTagName( "value" );
                        Element valueElement = (Element) valueList.item( 0 );
                        NodeList textValueElementList = valueElement.getChildNodes();
                        tempDataValue = textValueElementList.item( 0 ).getNodeValue();
    
                        String tempDeID = tempDeid;
                        //Integer tempDV = Integer.parseInt( tempDataValue );
    
                        dataValues.put( tempDeID, tempDataValue );
                    }
                }// end of for loop with s var
    
                mobileImportParameters.setDataValues( dataValues );
            }
            else if( formType.equalsIgnoreCase( MobileImportParameters.FORM_TYPE_ANMREGFORM ) )
            {
                // To get ANM Name
                NodeList anmNameInfo = doc.getElementsByTagName( "anmname" );
                Element anmNameInfoElement = (Element) anmNameInfo.item( 0 );
                NodeList anmNameInfoNameList = anmNameInfoElement.getChildNodes();
                anmName = anmNameInfoNameList.item( 0 ).getNodeValue().trim();

                mobileImportParameters.setAnmName( anmName );
            }
            else if( formType.equalsIgnoreCase( MobileImportParameters.FORM_TYPE_ANMQUERYFORM ) )
            {
                // To get ANM Query
                NodeList anmQueryInfo = doc.getElementsByTagName( "anmquery" );
                Element anmQueryInfoElement = (Element) anmQueryInfo.item( 0 );
                NodeList anmQueryInfoNameList = anmQueryInfoElement.getChildNodes();
                anmQuery = anmQueryInfoNameList.item( 0 ).getNodeValue().trim();

                mobileImportParameters.setAnmQuery( anmQuery );
            }
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }

        return mobileImportParameters;

    }// getParametersFromXML end

    @Override
    public List<String> getImportFiles()
    {
        List<String> fileNames = new ArrayList<String>();

        try
        {
            String importFolderPath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator
                + "mi" + File.separator + "pending";

            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                importFolderPath = newpath + File.separator + "mi" + File.separator + "pending";
            }

            File dir = new File( importFolderPath );

            String[] files = dir.list();

            fileNames = Arrays.asList( files );
        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }

        return fileNames;
    }

    public int moveFile( File source, File dest )
        throws IOException
    {

        if ( !dest.exists() )
        {
            dest.createNewFile();
        }

        InputStream in = null;

        OutputStream out = null;

        try
        {

            in = new FileInputStream( source );

            out = new FileOutputStream( dest );

            byte[] buf = new byte[1024];

            int len;

            while ( (len = in.read( buf )) > 0 )
            {
                out.write( buf, 0, len );
            }
        }
        catch ( Exception e )
        {
            return -1;
        }
        finally
        {
            in.close();

            out.close();
        }

        return 1;

    }

    @Override
    public void moveImportedFile( String fileName )
    {
        try
        {
            String sourceFilePath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "mi"
                + File.separator + "pending" + File.separator + fileName;

            String destFilePath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "mi"
                + File.separator + "completed" + File.separator + fileName;

            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                sourceFilePath = newpath + File.separator + "mi" + File.separator + "pending" + File.separator
                    + fileName;

                destFilePath = newpath + File.separator + "mi" + File.separator + "completed" + File.separator
                    + fileName;
            }

            File sourceFile = new File( sourceFilePath );

            File destFile = new File( destFilePath );

            int status = moveFile( sourceFile, destFile );

            if ( status == 1 )
            {
                sourceFile.delete();
            }

        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }

    }

    @Override
    public void moveFailedFile( String fileName )
    {
        try
        {
            String sourceFilePath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "mi"
                + File.separator + "pending" + File.separator + fileName;

            String destFilePath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "mi"
                + File.separator + "bounced" + File.separator + fileName;

            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                sourceFilePath = newpath + File.separator + "mi" + File.separator + "pending" + File.separator
                    + fileName;

                destFilePath = newpath + File.separator + "mi" + File.separator + "bounced" + File.separator + fileName;
            }

            File sourceFile = new File( sourceFilePath );

            File destFile = new File( destFilePath );

            int status = moveFile( sourceFile, destFile );

            if ( status == 1 )
            {
                sourceFile.delete();
            }

        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }
    }

    /*
    @Transactional
    public void importAllFiles()
    {
        BatchHandler<DataValue> batchHandler = batchHandlerFactory.createBatchHandler( DataValueBatchHandler.class );
        batchHandler.init();

        String importStatus;
        importStatus = "";

        List<String> fileNames = new ArrayList<String>( getImportFiles() );

        for ( String importFile : fileNames )
        {
            try
            {
                MobileImportParameters mobImportParameters = getParametersFromXML( importFile );

                if ( mobImportParameters == null )
                {
                    LOG.error( importFile + " Import File is not Properly Formated First" );
                    importStatus += "<br>" + new Date() + ": " + importFile + " Import File is not Properly Formated.";
                    moveFailedFile( importFile );
                    continue;
                }

                User curUser = getUserInfo( mobImportParameters.getMobileNumber() );

                if ( curUser != null )
                {

                    UserCredentials userCredentials = userStore.getUserCredentials( curUser );

                    if ( (userCredentials != null)
                        && (mobImportParameters.getMobileNumber().equals( curUser.getPhoneNumber() )) )
                    {
                        storedBy = userCredentials.getUsername();
                    }
                    else
                    {
                        LOG.error( " Import File Contains Unrecognised Phone Numbers : "
                            + mobImportParameters.getMobileNumber() );
                        importStatus += "<br><font color=red><b>Import File Contains Unrecognised Phone Numbers :"
                            + mobImportParameters.getMobileNumber() + ".</b></font>";
                        moveFailedFile( importFile );
                        continue;
                    }

                    List<Source> sources = new ArrayList<Source>( curUser.getOrganisationUnits() );

                    if ( sources == null || sources.size() <= 0 )
                    {
                        importStatus += "<br><font color=red><b>No User Exist Who Registered Phone No. Is :"
                            + mobImportParameters.getMobileNumber() + ".</b></font>";
                        moveFailedFile( importFile );
                        continue;
                    }
                    Source source = sources.get( 0 );

                    System.out.println( "getStartDate = " + mobImportParameters.getStartDate() + " getPeriodType = "
                        + mobImportParameters.getPeriodType() );

                    Period period = getPeriodInfo( mobImportParameters.getStartDate(), mobImportParameters
                        .getPeriodType() );

                    SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
                    SimpleDateFormat monthFormat = new SimpleDateFormat("MMM-yy");

                    Date timeStamp = dateFormat.parse( mobImportParameters.getSmsTime() );

                    Map<String, String> dataValueMap = new HashMap<String, String>( mobImportParameters
                        .getDataValues() );

                    if ( dataValueMap == null || dataValueMap.size() <= 0 )
                    {
                        LOG.error( "dataValue map is null" );
                    }
                    else if ( source == null )
                    {
                        LOG.error( "source is null" );
                    }
                    else if ( period == null )
                    {
                        LOG.error( "period is null" );
                    }
                    else if ( timeStamp == null )
                    {
                        LOG.error( "timeStamp is null" );
                    }

                    if ( source == null || period == null || timeStamp == null || dataValueMap == null
                        || dataValueMap.size() <= 0 )
                    {

                        LOG.error( importFile + " Import File is not Properly Formated" );
                        importStatus += "<br>" + new Date() + ": " + importFile
                            + " Import File is not Properly Formated.<br>";
                        moveFailedFile( importFile );
                        continue;
                    }

                    Set<String> keys = dataValueMap.keySet();

                    for ( String key : keys )
                    {
                        String parts[] = key.split( "\\." );

                        String deStr = parts[0];

                        String optStr = parts[1];

                        String value = String.valueOf( dataValueMap.get( key ) );

                        DataElement dataElement = dataElementService.getDataElement( Integer.valueOf( deStr ) );

                        DataElementCategoryOptionCombo optionCombo = new DataElementCategoryOptionCombo();

                        optionCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer
                            .valueOf( optStr ) );

                        DataValue dataValue = new DataValue( dataElement, period, source, value, storedBy, timeStamp,
                            null, optionCombo );
                        boolean exists = batchHandler.objectExists( dataValue );

                        if ( !exists )
                        {
                            if ( value != null )
                            {
                                batchHandler.addObject( dataValue );
                            }
                        }
                        else
                        {
                            dataValue.setValue( value );

                            dataValue.setTimestamp( timeStamp );

                            dataValue.setStoredBy( storedBy );

                            batchHandler.updateObject( dataValue );
                        }
                    }

                    importStatus += "<br>" + new Date() + ": " + importFile + " is Imported Successfully.";

                    moveImportedFile( importFile );
                }
                else
                {
                    LOG.error( importFile + " Phone number not found... Sending to Bounced" );
                    moveFailedFile( importFile );
                }
            }
            catch ( Exception e )
            {
                e.printStackTrace();
                LOG.error( e.getMessage() );
                LOG.error( "Exception caused in importing... Moving to Bounced" );
                moveFailedFile( importFile );
            }
            finally
            {
                batchHandler.flush();
            }
        }
    }
*/
    @Override
    @Transactional
    public void importPendingFiles()
    {
        List<String> fileNames = new ArrayList<String>( getImportFiles() );

        for ( String importFile : fileNames )
        {
            String statusMsg = importXMLFile( importFile );
            
            SendSMS sendSMS = new SendSMS( importFile.replace( ".xml", "" ), statusMsg );
            
            sendSMSService.addSendSMS( sendSMS );
        }
    }
    
    /*
    @Transactional
    public List<String> importPendingFile( String importFile )
    {
        List<String> statusMsgs = new ArrayList<String>();
        
        int insertFlag = 1;
        String insertQuery = "INSERT INTO datavalue (dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated ) VALUES ";
        String importStatus="";

        try
        {
            MobileImportParameters mobImportParameters = getParametersFromXML( importFile );

            if ( mobImportParameters == null )
            {
                LOG.error( importFile + " Import File is not Properly Formated First" );
                moveFailedFile( importFile );
                
                statusMsgs.add( 0, "1" );
                statusMsgs.add( 1, "Data not Received Properly, Please send again" );
                
                return statusMsgs;
            }

            User curUser = getUserInfo( mobImportParameters.getMobileNumber() );

            if ( curUser != null )
            {
                UserCredentials userCredentials = userStore.getUserCredentials( curUser );

                if ( (userCredentials != null) && (mobImportParameters.getMobileNumber().equals( curUser.getPhoneNumber() )) )
                {
                    storedBy = userCredentials.getUsername();
                }
                else
                {
                    LOG.error( " Import File Contains Unrecognised Phone Numbers : "
                        + mobImportParameters.getMobileNumber() );
                    moveFailedFile( importFile );
                    
                    statusMsgs.add( 0, "2" );
                    statusMsgs.add( 1, "Phone number is not registered to any facility. Please contact admin" );
                    
                    return statusMsgs;
                }

                List<Source> sources = new ArrayList<Source>( curUser.getOrganisationUnits() );

                if ( sources == null || sources.size() <= 0 )
                {
                    LOG.error( " No User Exists with corresponding Phone Numbers : "
                        + mobImportParameters.getMobileNumber() );
                    moveFailedFile( importFile );

                    statusMsgs.add( 0, "2" );
                    statusMsgs.add( 1, "Phone number is not registered to any facility. Please contact admin" );
                    
                    return statusMsgs;
                }
                
                Source source = sources.get( 0 );

                Period period = getPeriodInfo( mobImportParameters.getStartDate(), mobImportParameters.getPeriodType() );

                SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMM-yy");

                Date timeStamp = dateFormat.parse( mobImportParameters.getSmsTime() );            
                
                long t;
                if ( timeStamp == null )
                {
                    Date d = new Date();
                    t = d.getTime();
                } 
                else
                {
                    t = timeStamp.getTime();
                }

                java.sql.Date lastUpdatedDate = new java.sql.Date( t );

                Map<String, String> dataValueMap = new HashMap<String, String>( mobImportParameters.getDataValues() );

                if( dataValueMap == null || dataValueMap.size() <= 0 )
                {
                    LOG.error( "dataValue map is null" );
                }
                else if( source == null )
                {
                    LOG.error( "source is null" );
                }
                else if( period == null )
                {
                    LOG.error( "period is null" );
                }
                else if( timeStamp == null )
                {
                    LOG.error( "timeStamp is null" );
                }

                if( source == null || period == null || timeStamp == null || dataValueMap == null || dataValueMap.size() <= 0 )
                {
                    LOG.error( importFile + " Import File is not Properly Formated" );
                    moveFailedFile( importFile );

                    statusMsgs.add( 0, "1" );
                    statusMsgs.add( 1, "Data not Received Properly, Please send again" );
                    
                    return statusMsgs;
                }

                Set<String> keys = dataValueMap.keySet();

                for ( String key : keys )
                {
                    String parts[] = key.split( "\\." );

                    String deStr = parts[0];

                    String optStr = parts[1];

                    String value = String.valueOf( dataValueMap.get( key ) );

                    DataElement dataElement = dataElementService.getDataElement( Integer.valueOf( deStr ) );

                    DataElementCategoryOptionCombo optionCombo = new DataElementCategoryOptionCombo();

                    optionCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.valueOf( optStr ) );

                    DataValue dataValue = dataValueService.getDataValue( source, dataElement, period, optionCombo );

                    if ( dataValue == null )
                    {
                        if ( value != null )
                        {
                            insertQuery += "( "+ dataElement.getId() + ", " + period.getId() + ", "+ source.getId() +", " + optionCombo.getId() + ", '" + value + "', '" + storedBy + "', '" + lastUpdatedDate + "' ), ";
                            
                            insertFlag = 2;
                        }
                    }
                    else
                    {
                        dataValue.setValue( value );

                        dataValue.setTimestamp( timeStamp );

                        dataValue.setStoredBy( storedBy );

                        dataValueService.updateDataValue( dataValue );
                    }
                }
                
                if( insertFlag != 1 )
                {
                    insertQuery = insertQuery.substring( 0, insertQuery.length()-2 );
                    
                    jdbcTemplate.update( insertQuery );
                }

                moveImportedFile( importFile );
                
                if( period.getPeriodType().getName().equalsIgnoreCase( "monthly" ))
                {
                    importStatus = "THANK YOU FOR SENDING MONTHLY REPORT FOR " + monthFormat.format( period.getStartDate() );
                    
                    statusMsgs.add( 0, "3" );
                    statusMsgs.add( 1, importStatus );
                }
                else if( period.getPeriodType().getName().equalsIgnoreCase( "daily" ))
                {
                    importStatus = "THANK YOU FOR SENDING DAILY REPORT FOR " + dateFormat.format( period.getStartDate() );

                    statusMsgs.add( 0, "4" );
                    statusMsgs.add( 1, importStatus );
                }
                else
                {
                    importStatus = "THANK YOU FOR SENDING REPORT FOR " + dateFormat.format( period.getStartDate() ) + " : " + dateFormat.format( period.getEndDate() );
                
                    statusMsgs.add( 0, "5" );
                    statusMsgs.add( 1, importStatus );
                }
            }
            else
            {
                LOG.error( importFile + " Phone number not found... Sending to Bounced" );
                importStatus = "Phone number is not registered to any facility. Please contact admin";
                
                statusMsgs.add( 0, "2" );
                statusMsgs.add( 1, importStatus );

                moveFailedFile( importFile );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
            LOG.error( e.getMessage() );
            LOG.error( "Exception caused in importing... Moving to Bounced" );
            
            importStatus = "Data not Received Properly, Please send again";
            
            statusMsgs.add( 0, "1" );
            statusMsgs.add( 1, importStatus );

            moveFailedFile( importFile );
        }
        finally
        {
        }

        return statusMsgs;
    }
    */

    @Transactional
    public String importANMRegData( String importFile, MobileImportParameters mobImportParameters )
    {
        String importStatus="";

        try
        {
            User curUser = getUserInfo( mobImportParameters.getMobileNumber() );

            if ( curUser != null )
            {
                UserCredentials userCredentials = userStore.getUserCredentials( curUser );

                if ( (userCredentials != null)
                    && (mobImportParameters.getMobileNumber().equals( curUser.getPhoneNumber() )) )
                {
                }
                else
                {
                    LOG.error( " Import File Contains Unrecognised Phone Numbers : "
                        + mobImportParameters.getMobileNumber() );
                    moveFailedFile( importFile );
                    return "Phone number is not registered to any facility. Please contact admin";
                }

                List<OrganisationUnit> sources = new ArrayList<OrganisationUnit>( curUser.getOrganisationUnits() );

                if ( sources == null || sources.size() <= 0 )
                {
                    LOG.error( " No User Exists with corresponding Phone Numbers : "
                        + mobImportParameters.getMobileNumber() );
                    moveFailedFile( importFile );
                    
                    return "Phone number is not registered to any facility. Please contact admin";
                }
                
                OrganisationUnit source = sources.get( 0 );
                String anmName = mobImportParameters.getAnmName();

                if ( source == null || anmName == null || anmName.trim().equalsIgnoreCase( "" ) )
                {
                    LOG.error( importFile + " Import File is not Properly Formated" );
                    moveFailedFile( importFile );
                    
                    return "Data not Received Properly, Please send again";
                }

                source.setComment( anmName );
                
                organisationUnitService.updateOrganisationUnit( source );

                moveImportedFile( importFile );
                
               importStatus = "YOUR NAME IS REGISTERD SUCCESSFULLY";
            }
            else
            {
                LOG.error( importFile + " Phone number not found... Sending to Bounced" );
                importStatus = "Phone number is not registered to any facility. Please contact admin";
                moveFailedFile( importFile );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LOG.error( e.getMessage() );
            LOG.error( "Exception caused in importing... Moving to Bounced" );
            
            importStatus = "Data not Received Properly, Please send again";
            moveFailedFile( importFile );
        }
        finally
        {
        }

        return importStatus;
    }
    
    @Transactional
    public String importANMQueryData( String importFile, MobileImportParameters mobImportParameters )
    {
        String importStatus="";

        try
        {
            User curUser = getUserInfo( mobImportParameters.getMobileNumber() );

            if ( curUser != null )
            {
                UserCredentials userCredentials = userStore.getUserCredentials( curUser );

                if ( (userCredentials != null)
                    && (mobImportParameters.getMobileNumber().equals( curUser.getPhoneNumber() )) )
                {
                }
                else
                {
                    LOG.error( " Import File Contains Unrecognised Phone Numbers : "
                        + mobImportParameters.getMobileNumber() );
                    moveFailedFile( importFile );
                    return "Phone number is not registered to any facility. Please contact admin";
                }

                List<OrganisationUnit> sources = new ArrayList<OrganisationUnit>( curUser.getOrganisationUnits() );

                if ( sources == null || sources.size() <= 0 )
                {
                    LOG.error( " No User Exists with corresponding Phone Numbers : "
                        + mobImportParameters.getMobileNumber() );
                    moveFailedFile( importFile );
                    
                    return "Phone number is not registered to any facility. Please contact admin";
                }
                
                String anmQuery = mobImportParameters.getAnmQuery();

                if ( anmQuery == null || anmQuery.trim().equalsIgnoreCase( "" ) )
                {
                    LOG.error( importFile + " Import File is not Properly Formated" );
                    moveFailedFile( importFile );
                    
                    return "Data not Received Properly, Please send again";
                }

                ReceiveSMS receiveSMS = new ReceiveSMS( importFile, anmQuery );
                receiveSMSService.addReceiveSMS( receiveSMS );
                
                moveImportedFile( importFile );
                
               importStatus = "YOUR Query IS REGISTERD SUCCESSFULLY";
            }
            else
            {
                LOG.error( importFile + " Phone number not found... Sending to Bounced" );
                importStatus = "Phone number is not registered to any facility. Please contact admin";
                moveFailedFile( importFile );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LOG.error( e.getMessage() );
            LOG.error( "Exception caused in importing... Moving to Bounced" );
            
            importStatus = "Data not Received Properly, Please send again";
            moveFailedFile( importFile );
        }
        finally
        {
        }

        return importStatus;
    }
    @Override
    @Transactional
    public String importXMLFile( String importFile )
    {
        int insertFlag = 1;
        String insertQuery = "INSERT INTO datavalue (dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated ) VALUES ";
        String importStatus="";

        try
        {
            MobileImportParameters mobImportParameters = getParametersFromXML( importFile );

            if ( mobImportParameters == null )
            {
                LOG.error( importFile + " Import File is not Properly Formated First" );
                moveFailedFile( importFile );
                
                return "Data not Received Properly, Please send again";
            }

            // Checking for FormType, if formtype is ANMREG
            if( mobImportParameters.getFormType().equalsIgnoreCase( MobileImportParameters.FORM_TYPE_ANMREGFORM ) )
            {
                importStatus = importANMRegData( importFile, mobImportParameters );
                
                return importStatus;
            }
            else if( mobImportParameters.getFormType().equalsIgnoreCase( MobileImportParameters.FORM_TYPE_ANMQUERYFORM ) )
            {
                importStatus = importANMQueryData( importFile, mobImportParameters );
                
                return importStatus;
            }
            
            User curUser = getUserInfo( mobImportParameters.getMobileNumber() );

            if ( curUser != null )
            {

                UserCredentials userCredentials = userStore.getUserCredentials( curUser );

                if ( (userCredentials != null)
                    && (mobImportParameters.getMobileNumber().equals( curUser.getPhoneNumber() )) )
                {
                    storedBy = userCredentials.getUsername();
                }
                else
                {
                    LOG.error( " Import File Contains Unrecognised Phone Numbers : "
                        + mobImportParameters.getMobileNumber() );
                    moveFailedFile( importFile );
                    return "Phone number is not registered to any facility. Please contact admin";
                }

                List<OrganisationUnit> sources = new ArrayList<OrganisationUnit>( curUser.getOrganisationUnits() );

                if ( sources == null || sources.size() <= 0 )
                {
                    LOG.error( " No User Exists with corresponding Phone Numbers : "
                        + mobImportParameters.getMobileNumber() );
                    moveFailedFile( importFile );
                    
                    return "Phone number is not registered to any facility. Please contact admin";
                }
                
                OrganisationUnit source = sources.get( 0 );

                Period period = getPeriodInfo( mobImportParameters.getStartDate(), mobImportParameters.getPeriodType() );

                SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMM-yy");

                Date timeStamp = dateFormat.parse( mobImportParameters.getSmsTime() );            
                
                long t;
                if ( timeStamp == null )
                {
                    Date d = new Date();
                    t = d.getTime();
                } 
                else
                {
                    t = timeStamp.getTime();
                }

                java.sql.Date lastUpdatedDate = new java.sql.Date( t );

                Map<String, String> dataValueMap = new HashMap<String, String>( mobImportParameters.getDataValues() );

                if ( dataValueMap == null || dataValueMap.size() <= 0 )
                {
                    LOG.error( "dataValue map is null" );
                }
                else if ( source == null )
                {
                    LOG.error( "source is null" );
                }
                else if ( period == null )
                {
                    LOG.error( "period is null" );
                }
                else if ( timeStamp == null )
                {
                    LOG.error( "timeStamp is null" );
                }

                if ( source == null || period == null || timeStamp == null || dataValueMap == null
                    || dataValueMap.size() <= 0 )
                {

                    LOG.error( importFile + " Import File is not Properly Formated" );
                    moveFailedFile( importFile );
                    
                    return "Data not Received Properly, Please send again";
                }

                Set<String> keys = dataValueMap.keySet();

                for ( String key : keys )
                {
                    String parts[] = key.split( "\\." );

                    String deStr = parts[0];

                    String optStr = parts[1];

                    String value = String.valueOf( dataValueMap.get( key ) );

                    DataElement dataElement = dataElementService.getDataElement( Integer.valueOf( deStr ) );

                    DataElementCategoryOptionCombo optionCombo = new DataElementCategoryOptionCombo();

                    optionCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer
                        .valueOf( optStr ) );

                    DataValue dataValue = dataValueService.getDataValue( source, dataElement, period, optionCombo );

                    if ( dataValue == null )
                    {
                        if ( value != null )
                        {
                            insertQuery += "( "+ dataElement.getId() + ", " + period.getId() + ", "+ source.getId() +", " + optionCombo.getId() + ", '" + value + "', '" + storedBy + "', '" + lastUpdatedDate + "' ), ";

                            insertFlag = 2;
                        }
                    }
                    else
                    {
                        dataValue.setValue( value );

                        dataValue.setTimestamp( timeStamp );

                        dataValue.setStoredBy( storedBy );

                        dataValueService.updateDataValue( dataValue );
                    }
                }
                
                if( insertFlag != 1 )
                {
                    insertQuery = insertQuery.substring( 0, insertQuery.length()-2 );
                    
                    jdbcTemplate.update( insertQuery );
                }

                moveImportedFile( importFile );
                
                if( period.getPeriodType().getName().equalsIgnoreCase( "monthly" ))
                {
                    importStatus = "THANK YOU FOR SENDING MONTHLY REPORT FOR " + monthFormat.format( period.getStartDate() );
                }
                else if( period.getPeriodType().getName().equalsIgnoreCase( "daily" ))
                {
                    importStatus = "THANK YOU FOR SENDING DAILY REPORT FOR " + dateFormat.format( period.getStartDate() );
                }
                else
                {
                    importStatus = "THANK YOU FOR SENDING REPORT FOR " + dateFormat.format( period.getStartDate() ) + " : " + dateFormat.format( period.getEndDate() );
                }
            }
            else
            {
                LOG.error( importFile + " Phone number not found... Sending to Bounced" );
                importStatus = "Phone number is not registered to any facility. Please contact admin";
                moveFailedFile( importFile );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LOG.error( e.getMessage() );
            LOG.error( "Exception caused in importing... Moving to Bounced" );
            
            importStatus = "Data not Received Properly, Please send again";
            moveFailedFile( importFile );
        }
        finally
        {
        }

        return importStatus;
    }

}
