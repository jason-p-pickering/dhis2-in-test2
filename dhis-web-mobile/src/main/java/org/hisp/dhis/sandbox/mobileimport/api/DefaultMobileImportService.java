package org.hisp.dhis.sandbox.mobileimport.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DefaultMobileImportService implements MobileImportService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }

    // -------------------------------------------------------------------------
    // Services
    // -------------------------------------------------------------------------

    public User getUserInfo( String mobileNumber )
    {

        List<User> allUsers = new ArrayList<User>( userStore.getAllUsers() );

        User selectedUser = new User();

        for ( User user : allUsers )
        {            
            if ( user.getPhoneNumber() != null && user.getPhoneNumber().equalsIgnoreCase( mobileNumber ) )
                selectedUser = user;
        }

        return selectedUser;
    }

    public Period getPeriodInfo( String startDate )
        throws Exception
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        //String convertedDate = dateFormat.format( startDate );

        List<Period> monthlyPeriods = new ArrayList<Period>( periodStore
            .getPeriodsByPeriodType( new MonthlyPeriodType() ) );

        for ( Period period : monthlyPeriods )
        {
            String tempDate = dateFormat.format( period.getStartDate() );

            System.out.println(tempDate + " **** " + startDate );            

            if ( tempDate.equalsIgnoreCase( startDate ) )
            {
                return period;
            }
        }

        return null;

    }

    public MobileImportParameters getParametersFromXML( String fileName )
        throws Exception
    {
        File importFile = locationManager.getFileForReading( fileName, "mi", "pending" );        

        String mobileNumber;
        String smsTime;
        String startDate;

        String tempDeid;
        String tempDataValue;

        Map<String, Integer> dataValues = new HashMap<String, Integer>();

        MobileImportParameters mobileImportParameters = new MobileImportParameters();

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( importFile );
            if ( doc == null )
            {
                System.out.println( "XML File Not Found at user home" );
                return null;
            }

            // To get Mobile Number
            NodeList sourceInfo = doc.getElementsByTagName( "source" );
            Element sourceInfoElement = (Element) sourceInfo.item( 0 );
            NodeList textsourceInfoNameList = sourceInfoElement.getChildNodes();
            mobileNumber = ((Node) textsourceInfoNameList.item( 0 )).getNodeValue().trim();

            mobileImportParameters.setMobileNumber( mobileNumber );

            // To get Period
            NodeList periodInfo = doc.getElementsByTagName( "period" );
            Element periodInfoElement = (Element) periodInfo.item( 0 );
            NodeList textperiodInfoNameList = periodInfoElement.getChildNodes();
            startDate = ((Node) textperiodInfoNameList.item( 0 )).getNodeValue().trim();

            mobileImportParameters.setStartDate( startDate );

            // To get TimeStamp
            NodeList timeStampInfo = doc.getElementsByTagName( "timeStamp" );
            Element timeStampInfoElement = (Element) timeStampInfo.item( 0 );
            NodeList texttimeStampInfoNameList = timeStampInfoElement.getChildNodes();
            smsTime = ((Node) texttimeStampInfoNameList.item( 0 )).getNodeValue().trim();

            System.out.println("In getParametersFromXML : "+ mobileNumber+" : "+startDate+" : "+smsTime);

            mobileImportParameters.setSmsTime( smsTime );

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
                    tempDeid = ((Node) textdataElementIdList.item( 0 )).getNodeValue().trim();

                    NodeList valueList = dataValueElement.getElementsByTagName( "value" );
                    Element valueElement = (Element) valueList.item( 0 );
                    NodeList textValueElementList = valueElement.getChildNodes();
                    tempDataValue = ((Node) textValueElementList.item( 0 )).getNodeValue();

                    String tempDeID = tempDeid ;
                    Integer tempDV = Integer.parseInt( tempDataValue );

                    System.out.println("In getParametersFromXML : "+ tempDeID + " : "+ tempDV );

                    dataValues.put( tempDeID, tempDV );
                }
            }// end of for loop with s var

            mobileImportParameters.setDataValues( dataValues );           

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
                importFolderPath = newpath + File.separator  + "mi" + File.separator
                    + "pending";
            }

            File dir = new File( importFolderPath );
            
            System.out.println(dir.getAbsolutePath());
            
            System.out.println(dir.listFiles());
            
            String[] files = dir.list();

            System.out.println("In getImportFiles Method: "+ files.length);
            
            fileNames = Arrays.asList( files );
            System.out.println("In getImportFiles Method: "+ fileNames.size());
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
        
        catch( Exception e )
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
                
                System.out.println(sourceFilePath);

                destFilePath = newpath + File.separator + "mi" + File.separator + "completed" + File.separator
                    + fileName;
                
                System.out.println(destFilePath);

            }

            File sourceFile = new File( sourceFilePath );

            File destFile = new File( destFilePath );
            
            int status = moveFile( sourceFile, destFile );
            
            if( status == 1 )
                sourceFile.delete();

        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }

    }

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

                destFilePath = newpath + File.separator + "mi" + File.separator + "bounced" + File.separator
                    + fileName;
            }

            File sourceFile = new File( sourceFilePath );

            File destFile = new File( destFilePath );
            
            int status = moveFile( sourceFile, destFile );
            
            if( status == 1 )
                sourceFile.delete();

        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }
    }
}