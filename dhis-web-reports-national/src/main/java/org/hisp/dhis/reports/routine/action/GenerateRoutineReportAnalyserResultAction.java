package org.hisp.dhis.reports.routine.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import jxl.CellType;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.VerticalAlignment;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;

import com.opensymphony.xwork2.Action;

public class GenerateRoutineReportAnalyserResultAction
    implements Action
{
 
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }

    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private int availablePeriods;

    public void setAvailablePeriods( int availablePeriods )
    {
        this.availablePeriods = availablePeriods;
    }

    private String aggCB;

    public void setAggCB( String aggCB )
    {
        this.aggCB = aggCB;
    }

    private String organisationUnitGroupId;

    public void setOrganisationUnitGroupId( String organisationUnitGroupId )
    {
        this.organisationUnitGroupId = organisationUnitGroupId;
    }

    private List<OrganisationUnit> orgUnitList;

    private Period selectedPeriod;

    private SimpleDateFormat simpleDateFormat;

    private SimpleDateFormat monthFormat;

    private SimpleDateFormat dailyFormat;

    private SimpleDateFormat simpleMonthFormat;

    private SimpleDateFormat yearFormat;

    private SimpleDateFormat simpleYearFormat;

    private String reportFileNameTB;

    private String reportModelTB;

    private Date sDate;

    private Date eDate;

    private Date sDateTemp;

    private Date eDateTemp;

    private PeriodType periodType;

    private String raFolderName;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        statementManager.initialise();

        // Initialization
        raFolderName = reportService.getRAFolderName();
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthFormat = new SimpleDateFormat( "MMMM" );
        simpleMonthFormat = new SimpleDateFormat( "MMM" );
        yearFormat = new SimpleDateFormat( "yyyy" );
        simpleYearFormat = new SimpleDateFormat( "yy" );
        dailyFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        String deCodesXMLFileName = "";
        String parentUnit = "";

        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );
        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        
        System.out.println( "Report name is : " + selReportObj.getName()+ " , Report Model is  " + selReportObj.getModel() + " , Excel template name is : " + selReportObj.getExcelTemplateName() );
        System.out.println( " , Report XML file  is : " + selReportObj.getXmlTemplateName()+ " , Report Type is :  " + selReportObj.getReportType() );
        
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + "template" + File.separator + reportFileNameTB;
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + "output" + File.separator + UUID.randomUUID().toString() + ".xls";

        if( reportModelTB.equalsIgnoreCase( "DYNAMIC-ORGUNIT" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
            Collections.sort( orgUnitList, new OrganisationUnitNameComparator() );
        }
        else if( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
        //else if( reportModelTB.equalsIgnoreCase( "dynamicwithroot" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
            Collections.sort( orgUnitList, new OrganisationUnitNameComparator() );
            orgUnitList.add( orgUnit );

            parentUnit = orgUnit.getName();
            
            System.out.println( " orgUnitList size is  : " + orgUnitList.size()+ " , Parent orgUnit is  : " + orgUnit.getName() );
        }
        else if( reportModelTB.equalsIgnoreCase( "STATIC" ) || reportModelTB.equalsIgnoreCase( "STATIC-DATAELEMENTS" ) || reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList.add( orgUnit );
        }

        System.out.println( orgUnitList.get( 0 ).getName()+ " : " + selReportObj.getName()+" : Report Generation Start Time is : " + new Date() );
        
        OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( organisationUnitGroupId ) );
        
        List<OrganisationUnit> orgGroupMembers = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );

        
        selectedPeriod = periodService.getPeriod( availablePeriods );

        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );

        eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );

        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );

        // Getting DataValues
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );
        int orgUnitCount = 0;

        Iterator<OrganisationUnit> it = orgUnitList.iterator();
        while ( it.hasNext() )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) it.next();

            Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
            int count1 = 0;
            while ( reportDesignIterator.hasNext() )
            {
                Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();

                String deType = report_inDesign.getPtype();
                String sType = report_inDesign.getStype();
                String deCodeString = report_inDesign.getExpression();
                String tempStr = "";
                double tempNum = 0;

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                List<Calendar> calendarList = new ArrayList<Calendar>( reportService.getStartingEndingPeriods( deType, selectedPeriod ) );
                if ( calendarList == null || calendarList.isEmpty() )
                {
                    tempStartDate.setTime( selectedPeriod.getStartDate() );
                    tempEndDate.setTime( selectedPeriod.getEndDate() );
                    return SUCCESS;
                }
                else
                {
                    tempStartDate = calendarList.get( 0 );
                    tempEndDate = calendarList.get( 1 );
                }

                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = currentOrgUnit.getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                {
                    tempStr = parentUnit;
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getParent().getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getParent().getParent().getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITYCOMMENT" ) )
                {
                    tempStr = currentOrgUnit.getComment();
                }
                else if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) )
                {
                    tempStr = simpleDateFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) )
                {
                    tempStr = monthFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "DAILY-PERIOD" ) )
                {
                    tempStr = dailyFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) )
                {
                    tempStr = simpleMonthFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) )
                {
                    tempStr = simpleMonthFormat.format( eDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-START" ) )
                {
                    tempStr = monthFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-END" ) )
                {
                    tempStr = monthFormat.format( eDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) )
                {
                    tempStr = String.valueOf( tempStartDate.get( Calendar.WEEK_OF_MONTH ) );
                }
                else if ( deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) )
                {
                    String startMonth = "";

                    startMonth = monthFormat.format( sDate );

                    if ( startMonth.equalsIgnoreCase( "April" ) )
                    {
                        tempStr = "Quarter I";
                    }
                    else if ( startMonth.equalsIgnoreCase( "July" ) )
                    {
                        tempStr = "Quarter II";
                    }
                    else if ( startMonth.equalsIgnoreCase( "October" ) )
                    {
                        tempStr = "Quarter III";
                    }
                    else
                    {
                        tempStr = "Quarter IV";
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" ) )
                {
                    String startMonth = "";

                    startMonth = monthFormat.format( sDate );

                    if ( startMonth.equalsIgnoreCase( "April" ) )
                    {
                        tempStr = "Q1";
                    }
                    else if ( startMonth.equalsIgnoreCase( "July" ) )
                    {
                        tempStr = "Q2";
                    }
                    else if ( startMonth.equalsIgnoreCase( "October" ) )
                    {
                        tempStr = "Q3";
                    }
                    else
                    {
                        tempStr = "Q4";
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" ) )
                {
                    String startMonth = "";

                    startMonth = monthFormat.format( sDate );

                    if ( startMonth.equalsIgnoreCase( "April" ) )
                    {
                        tempStr = "Apr - Jun";
                    }
                    else if ( startMonth.equalsIgnoreCase( "July" ) )
                    {
                        tempStr = "Jul - Sep";
                    }
                    else if ( startMonth.equalsIgnoreCase( "October" ) )
                    {
                        tempStr = "Oct - Dec";
                    }
                    else
                    {
                        tempStr = "Jan - Mar";
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" ) )
                {
                    String startMonth = "";

                    startMonth = monthFormat.format( sDate );

                    if ( startMonth.equalsIgnoreCase( "April" ) )
                    {
                        tempStr = "April - June";
                    }
                    else if ( startMonth.equalsIgnoreCase( "July" ) )
                    {
                        tempStr = "July - September";
                    }
                    else if ( startMonth.equalsIgnoreCase( "October" ) )
                    {
                        tempStr = "October - December";
                    }
                    else
                    {
                        tempStr = "January - March";
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" ) )
                {
                    String startMonth = "";

                    startMonth = monthFormat.format( sDate );

                    if ( startMonth.equalsIgnoreCase( "April" ) )
                    {
                        tempStr = "Apr";
                    }
                    else if ( startMonth.equalsIgnoreCase( "July" ) )
                    {
                        tempStr = "Jul";
                    }
                    else if ( startMonth.equalsIgnoreCase( "October" ) )
                    {
                        tempStr = "Oct";
                    }
                    else
                    {
                        tempStr = "Jan";
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "QUARTER-START" ) )
                {
                    String startMonth = "";

                    startMonth = monthFormat.format( sDate );

                    if ( startMonth.equalsIgnoreCase( "April" ) )
                    {
                        tempStr = "April";
                    }
                    else if ( startMonth.equalsIgnoreCase( "July" ) )
                    {
                        tempStr = "July";
                    }
                    else if ( startMonth.equalsIgnoreCase( "October" ) )
                    {
                        tempStr = "October";
                    }
                    else
                    {
                        tempStr = "January";
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" ) )
                {
                    String endMonth = "";

                    endMonth = monthFormat.format( eDate );

                    if ( endMonth.equalsIgnoreCase( "June" ) )
                    {
                        tempStr = "Jun";
                    }
                    else if ( endMonth.equalsIgnoreCase( "September" ) )
                    {
                        tempStr = "Sep";
                    }
                    else if ( endMonth.equalsIgnoreCase( "December" ) )
                    {
                        tempStr = "Dec";
                    }
                    else
                    {
                        tempStr = "Mar";
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "QUARTER-END" ) )
                {
                    String endMonth = "";

                    endMonth = monthFormat.format( eDate );

                    if ( endMonth.equalsIgnoreCase( "June" ) )
                    {
                        tempStr = "June";
                    }
                    else if ( endMonth.equalsIgnoreCase( "September" ) )
                    {
                        tempStr = "September";
                    }
                    else if ( endMonth.equalsIgnoreCase( "December" ) )
                    {
                        tempStr = "December";
                    }
                    else
                    {
                        tempStr = "March";
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) )
                {
                    sDateTemp = sDate;

                    Calendar tempQuarterYear = Calendar.getInstance();

                    tempQuarterYear.setTime( sDateTemp );

                    String startMonth = "";

                    startMonth = monthFormat.format( sDateTemp );

                    periodType = selectedPeriod.getPeriodType();

                    if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                    {
                        sDateTemp = sDate;
                    }
                    else
                    {
                        if ( (startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                            .equalsIgnoreCase( "March" ))
                            && periodType.getName().equalsIgnoreCase( "Quarterly" ) )
                        {
                            tempQuarterYear.roll( Calendar.YEAR, -1 );

                            sDateTemp = tempQuarterYear.getTime();
                        }
                    }
                    tempStr = yearFormat.format( sDateTemp );
                }
                else if ( deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" ) )
                {
                    sDateTemp = sDate;

                    Calendar tempQuarterYear = Calendar.getInstance();

                    tempQuarterYear.setTime( sDateTemp );

                    String startMonth = "";

                    startMonth = monthFormat.format( sDateTemp );

                    periodType = selectedPeriod.getPeriodType();

                    if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                    {
                        sDateTemp = sDate;
                    }
                    else
                    {
                        if ( (startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                            .equalsIgnoreCase( "March" ))
                            && periodType.getName().equalsIgnoreCase( "Quarterly" ) )
                        {
                            tempQuarterYear.roll( Calendar.YEAR, -1 );

                            sDateTemp = tempQuarterYear.getTime();

                        }
                    }

                    tempStr = simpleYearFormat.format( sDateTemp );
                }
                else if ( deCodeString.equalsIgnoreCase( "YEAR-END" ) )
                {
                    sDateTemp = sDate;

                    Calendar tempQuarterYear = Calendar.getInstance();

                    tempQuarterYear.setTime( sDate );

                    sDate = tempQuarterYear.getTime();

                    String startMonth = "";

                    startMonth = monthFormat.format( sDateTemp );

                    periodType = selectedPeriod.getPeriodType();

                    if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                    {
                        tempQuarterYear.roll( Calendar.YEAR, 1 );

                        sDateTemp = tempQuarterYear.getTime();

                    }

                    if ( !(startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                        .equalsIgnoreCase( "March" )) )
                    {
                        tempQuarterYear.roll( Calendar.YEAR, 1 );

                        sDateTemp = tempQuarterYear.getTime();

                    }

                    tempStr = yearFormat.format( sDateTemp );
                }

                else if ( deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                {

                    sDateTemp = sDate;

                    eDateTemp = eDate;

                    Calendar tempQuarterYear = Calendar.getInstance();

                    String startYear = "";

                    String endYear = "";

                    String startMonth = "";

                    startMonth = monthFormat.format( sDateTemp );

                    periodType = selectedPeriod.getPeriodType();

                    tempQuarterYear.setTime( sDateTemp );

                    if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                    {
                        sDateTemp = sDate;
                    }

                    else
                    {
                        if ( (startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                            .equalsIgnoreCase( "March" )) )
                        {
                            tempQuarterYear.roll( Calendar.YEAR, -1 );

                            sDateTemp = tempQuarterYear.getTime();

                        }
                    }

                    startYear = yearFormat.format( sDateTemp );

                    tempQuarterYear.setTime( eDateTemp );

                    if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                    {
                        tempQuarterYear.roll( Calendar.YEAR, 1 );

                        eDateTemp = tempQuarterYear.getTime();
                    }

                    if ( !(startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                        .equalsIgnoreCase( "March" )) )
                    {
                        tempQuarterYear.roll( Calendar.YEAR, 1 );

                        eDateTemp = tempQuarterYear.getTime();

                    }
                    endYear = yearFormat.format( eDateTemp );

                    tempStr = startYear + " - " + endYear;

                }
                else if ( deCodeString.equalsIgnoreCase( "SLNO" ) )
                {
                    tempStr = "" + (orgUnitCount + 1);
                }
                else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                }
                else
                {
                    if ( sType.equalsIgnoreCase( "dataelement" ) )
                    {
                        if ( organisationUnitGroupId.equalsIgnoreCase( "ALL" ) )
                        {
                            tempStr = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                        }
                        else if ( organisationUnitGroupId.equalsIgnoreCase( "Selected_Only" ) )
                        {
                            tempStr = reportService.getIndividualResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                        }
                        else
                        {
                            
                            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( currentOrgUnit.getId() ) );
                            
                            orgUnitList.retainAll( orgGroupMembers );
                            
                            double temp = 0.0;
                            double value = 0.0;
                            for ( OrganisationUnit unit : orgUnitList )
                            {
                                tempStr = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), unit, reportModelTB );

                                try
                                {
                                    value = Double.valueOf( tempStr );
                                }
                                catch ( Exception e )
                                {
                                    value = 0.0;
                                    System.out.println( e );
                                }
                                temp += value;
                            }

                            tempNum = temp;
                            tempStr = String.valueOf( (int) temp );
                            //System.out.println( tempStr );
                        }
                    }
                    else if ( sType.equalsIgnoreCase( "dataelement-boolean" ) )
                    {
                        if ( aggCB == null )
                        {
                            tempStr = reportService.getBooleanDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                        }
                        else
                        {
                            tempStr = reportService.getBooleanDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                        }
                    }
                    else
                    {
                        if ( aggCB == null )
                        {
                            tempStr = reportService.getIndividualResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                        }
                        else
                        {
                            tempStr = reportService.getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                        }
                    }
                }
                
                int tempRowNo = report_inDesign.getRowno();
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
               
                if ( tempStr == null || tempStr.equals( " " ) )
                {
                    //tempColNo += orgUnitCount;
                    tempRowNo += orgUnitCount;
                    
                    WritableCellFormat wCellformat = new WritableCellFormat();
                    wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    wCellformat.setWrap( true );
                    wCellformat.setAlignment( Alignment.CENTRE );

                    sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
                }
               else 
               // if ( tempStr != null || tempStr != " " )
                {
                    if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-ORGUNIT" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPPP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                        {

                        }
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" )
                            || deCodeString.equalsIgnoreCase( "MONTH-START" )
                            || deCodeString.equalsIgnoreCase( "MONTH-END" )
                            || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" )
                            || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" )
                            || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-START" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-END" )
                            || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" )
                            || deCodeString.equalsIgnoreCase( "YEAR-END" )
                            || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                        {

                        }
                        else
                        {
                            tempColNo += orgUnitCount;
                        }
                    }
                    else if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
                    //else if ( reportModelTB.equalsIgnoreCase( "dynamicwithroot" ) )    
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" )
                            || deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPP" ) 
                            || deCodeString.equalsIgnoreCase( "FACILITYPPP" ) 
                            || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                        {

                        }
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" )
                            || deCodeString.equalsIgnoreCase( "MONTH-START" )
                            || deCodeString.equalsIgnoreCase( "MONTH-END" )
                            || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" )
                            || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" )
                            || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-START" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-END" )
                            || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" )
                            || deCodeString.equalsIgnoreCase( "YEAR-END" )
                            || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                        {

                        }
                        else
                        {
                            tempRowNo += orgUnitCount;
                        }
                    }

                    WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );

                    CellFormat cellFormat = cell.getCellFormat();
                    WritableCellFormat wCellformat = new WritableCellFormat();
                    wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    wCellformat.setWrap( true );
                    wCellformat.setAlignment( Alignment.CENTRE );
                    wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );

                    if ( cell.getType() == CellType.LABEL )
                    {
                        Label l = (Label) cell;
                        l.setString( tempStr );
                        l.setCellFormat( cellFormat );
                    }
                    else
                    {
                        try
                        {
                            tempNum = Double.valueOf( tempStr );
                            sheet0.addCell( new Number( tempColNo, tempRowNo, tempNum, wCellformat ) );
                        }
                        catch ( Exception e )
                        {
                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                        }
                    }

                }
                count1++;
            }// inner while loop end
            orgUnitCount++;
        }// outer while loop end

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + orgUnitList.get( 0 ).getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( selectedPeriod.getStartDate() ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( orgUnitList.get( 0 ).getName()+ " : " + selReportObj.getName()+" : Report Generation End Time is : " + new Date() );

        outputReportFile.deleteOnExit();

        statementManager.destroy();

        return SUCCESS;
    }

}
