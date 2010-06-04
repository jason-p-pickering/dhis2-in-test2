package org.hisp.dhis.dashboard.ta.action;

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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.SixMonthlyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;

import com.opensymphony.xwork2.Action;

import edu.emory.mathcs.backport.java.util.Collections;

public class GenerateTabularAnalysisResultAction
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

    @SuppressWarnings("unused")
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

    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryOptionComboService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }
    
    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }
    
    private DataValueService dataValueService;
    
    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input & Output
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

    private List<OrganisationUnit> selOUList;
    private List<Date> selStartPeriodList;
    private List<Date> selEndPeriodList;

    private OrganisationUnit selOrgUnit;

    private List<String> selectedServices;

    public void setSelectedServices( List<String> selectedServices )
    {
        this.selectedServices = selectedServices;
    }

    private String deSelection;

    public void setDeSelection( String deSelection )
    {
        this.deSelection = deSelection;
    }

    private String ouSelCB;

    public void setOuSelCB( String ouSelCB )
    {
        this.ouSelCB = ouSelCB;
    }

    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }

    private Integer orgUnitLevelCB;

    public void setOrgUnitLevelCB( Integer orgUnitLevelCB )
    {
        this.orgUnitLevelCB = orgUnitLevelCB;
    }

    private String periodTypeLB;

    public void setPeriodTypeLB( String periodTypeLB )
    {
        this.periodTypeLB = periodTypeLB;
    }

    private String aggPeriodCB;

    public void setAggPeriodCB( String aggPeriodCB )
    {
        this.aggPeriodCB = aggPeriodCB;
    }

    private List<String> yearLB;

    public void setYearLB( List<String> yearLB )
    {
        this.yearLB = yearLB;
    }

    private List<String> periodLB;

    public void setPeriodLB( List<String> periodLB )
    {
        this.periodLB = periodLB;
    }
    
    private String ouRadio;
    
    public void setOuRadio( String ouRadio )
    {
        this.ouRadio = ouRadio;
    }

    private Map<OrganisationUnit, Integer> ouChildCountMap;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        /* Initialization */
        statementManager.initialise();
        
        selOUList = new ArrayList<OrganisationUnit>();
        selStartPeriodList = new ArrayList<Date>();
        selEndPeriodList = new ArrayList<Date>();
        
        ouChildCountMap = new HashMap<OrganisationUnit, Integer>();

        String monthOrder[] = { "04", "05", "06", "07", "08", "09","10", "11", "12", "01", "02", "03" };
        int monthDays[] = { 30, 31, 30, 31, 31, 30, 31, 30, 31, 31, 28, 31 };
        
        int startRow = 1;
        @SuppressWarnings("unused")
        int startCol = 0;
        int headerRow = 1;
        int headerCol = 0;
        
        String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";        
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );
        
        sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow+1 );
        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );
        
        /* Orgunit Info */

        if ( ouSelCB != null )
        {
            for ( String ouStr : orgUnitListCB )
            {
                OrganisationUnit ou = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouStr ) );
                selOUList.add( ou );
            }
        }
        else
        {
            selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            
            selOUList = getChildOrgUnitTree( selOrgUnit );
                        
            Iterator<OrganisationUnit> ouIterator = selOUList.iterator();
            while( ouIterator.hasNext() )
            {
                OrganisationUnit orgU = ouIterator.next();
                if( organisationUnitService.getLevelOfOrganisationUnit( orgU ) > orgUnitLevelCB )
                {
                    ouIterator.remove();
                }
            }
        }
        
        int minOULevel = 1;
        minOULevel = organisationUnitService.getLevelOfOrganisationUnit( selOUList.get( 0 ) );
        
        int maxOuLevel = 1;
        if( orgUnitLevelCB != null ) maxOuLevel = orgUnitLevelCB;
        else maxOuLevel = minOULevel;
        
        int c1 = headerCol+1;
        
        if( ouSelCB != null )
        {
            sheet0.mergeCells( c1, headerRow, c1, headerRow+1 );
            sheet0.addCell( new Label( c1, headerRow, "Facility", getCellFormat1() ) );
            c1++;
        }
        else
        {
            for(int i = minOULevel; i <= maxOuLevel; i++ )
            {           
                sheet0.mergeCells( c1, headerRow, c1, headerRow+1 );
                sheet0.addCell( new Label( c1, headerRow, "Level"+i, getCellFormat1() ) );
                c1++;
            }
        }
        
        /* Period Info */

        String startD = "";
        String endD = "";
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        List<String> periodNames = new ArrayList<String>();

        for( String year : yearLB )
        {  
            int selYear = Integer.parseInt( year.split( "-" )[0] );
            
            if(periodTypeLB.equalsIgnoreCase( YearlyPeriodType.NAME ))
            {                
                
                startD = "" + selYear + "-04-01";
                endD = "" + (selYear+1) + "-03-31";
                
                selStartPeriodList.add( format.parseDate( startD ) );
                selEndPeriodList.add( format.parseDate( endD ) );

                periodNames.add( ""+selYear+"-"+ (selYear+1) );
                
                continue;
            }
            
            for( String periodStr : periodLB )
            {   
                int period = Integer.parseInt( periodStr );
                
                if(periodTypeLB.equalsIgnoreCase( MonthlyPeriodType.NAME ))
                {
                    simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
                    
                    if( period >= 9 )
                    {
                        startD = "" + (selYear+1) + "-" + monthOrder[ period ] + "-01";
                        endD = "" + (selYear+1) + "-" + monthOrder[ period ] + "-" + monthDays[ period ];

                        if ( (selYear+1) % 4 == 0 && period == 10 )
                        {
                            endD = "" + (selYear+1) + "-" + monthOrder[ period ] + "-" + (monthDays[ period ]+1);
                        }                        
                    }
                    else
                    {
                        startD = "" + selYear + "-" + monthOrder[ period ] + "-01";
                        endD = "" + selYear + "-" + monthOrder[ period ] + "-" + monthDays[ period ];
                    }
                    
                    selStartPeriodList.add( format.parseDate( startD ) );
                    selEndPeriodList.add( format.parseDate( endD ) );
                    periodNames.add( simpleDateFormat.format( format.parseDate( startD ) ) );
                }
                else if(periodTypeLB.equalsIgnoreCase( QuarterlyPeriodType.NAME ))
                {
                    if( period == 0)
                    {
                        startD = "" + selYear + "-04-01";
                        endD = "" + selYear + "-06-30";
                        periodNames.add( selYear+"-Q1" );
                    }
                    else if ( period == 1 )
                    {
                        startD = "" + selYear + "-07-01";
                        endD = "" + selYear + "-09-30";
                        periodNames.add( selYear+"-Q2" );
                    }
                    else if ( period == 2 )
                    {
                        startD = "" + selYear + "-10-01";
                        endD = "" + selYear + "-12-31";
                        periodNames.add( selYear+"-Q3" );
                    }
                    else 
                    {
                        startD = "" + (selYear+1) + "-01-01";
                        endD = "" + (selYear+1) + "-03-31";
                        periodNames.add( (selYear)+"-Q4" );
                    }
                    selStartPeriodList.add( format.parseDate( startD ) );
                    selEndPeriodList.add( format.parseDate( endD ) );
                }
                else if(periodTypeLB.equalsIgnoreCase( SixMonthlyPeriodType.NAME ))
                {
                    if( period == 0 )
                    {
                        startD = "" + selYear + "-04-01";
                        endD = "" + selYear + "-09-30";
                        periodNames.add( selYear+"-HY1" );
                    }
                    else
                    {
                        startD = "" + selYear + "-10-01";
                        endD = "" + (selYear+1) + "-03-31";
                        periodNames.add( selYear+"-HY2" );
                    }
                    selStartPeriodList.add( format.parseDate( startD ) );
                    selEndPeriodList.add( format.parseDate( endD ) );
                }
                
                //selStartPeriodList.add( format.parseDate( startD ) );
                //selEndPeriodList.add( format.parseDate( endD ) );
                //System.out.println( startD + " : " + endD );
            }
        }
        
        
        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;
        
        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for(OrganisationUnit ou : selOUList )
        {            
            sheet0.addCell( new Number( headerCol, headerRow+1+rowCount, rowCount, getCellFormat2() ) );
            colCount = 1 + organisationUnitService.getLevelOfOrganisationUnit( ou ) - minOULevel;
            //System.out.println(colCount + " : " + minOULevel + " : " + organisationUnitService.getLevelOfOrganisationUnit( ou ));
            
            //sheet0.mergeCells( colCount, headerRow+1+rowCount, colCount, headerRow+1+rowCount+ouChildCountMap.get( ou ));
            
            if( ouSelCB != null )
            {
                sheet0.addCell( new Label( headerCol+1, headerRow+1+rowCount, ou.getName(), getCellFormat2() ) );
            }
            else
            {
                for(int i = 1; i <= maxOuLevel-minOULevel+1; i++ )
                {          
                    if( i == colCount )
                    {
                        sheet0.addCell( new Label( colCount, headerRow+1+rowCount, ou.getName(), getCellFormat2() ) );
                    }
                    else
                    {
                        sheet0.addCell( new Label( i, headerRow+1+rowCount, " ", getCellFormat2() ) );
                    }                
                }
            }
            
            colCount = c1;
            for( String service : selectedServices )
            {
                String partsOfService[] = service.split( ":" ); 
                if( partsOfService[0].equalsIgnoreCase( "I" ) )
                {
                    flag = 1;
                    selIndicator = indicatorService.getIndicator( Integer.parseInt( partsOfService[1] ) );
                    if( rowCount ==1 )
                    {
                        if( aggPeriodCB == null )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount+selStartPeriodList.size()-1, startRow);
                        }
                        else
                        {
                            sheet0.mergeCells( colCount, startRow, colCount, startRow+1 );
                        }
                        
                        sheet0.addCell( new Label( colCount, startRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementService.getDataElement( Integer.parseInt( partsOfService[1] ) );
                    
                    
                    if( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( Integer.parseInt( partsOfService[2] ) );
                        if( rowCount == 1 )
                        {
                            if( aggPeriodCB == null )
                            {
                                sheet0.mergeCells( colCount, startRow, colCount+selStartPeriodList.size()-1, startRow );
                            }
                            else
                            {
                                sheet0.mergeCells( colCount, startRow, colCount, startRow+1 );
                            }

                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName()+"-"+selDecoc.getName(), getCellFormat1() ) );
                        }
                    }
                    else
                    {
                        if( rowCount == 1 )
                        {
                            if( aggPeriodCB == null )
                            {
                                sheet0.mergeCells( colCount, startRow, colCount+selStartPeriodList.size()-1, startRow );
                            }
                            else
                            {
                                sheet0.mergeCells( colCount, startRow, colCount, startRow+1 );
                            }
                            
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                    }
                }
                                                
                int periodCount = 0;
                double numAggValue = 0.0;
                double denAggValue = 0.0;
                double dvAggValue = 0.0;
                String aggStrValue = "";
                
                for( Date sDate : selStartPeriodList )
                {
                    Date eDate = selEndPeriodList.get( periodCount );
                    double pwnumAggValue=0.0;
                    double pwdenAggValue=0.0;
                    double pwdvAggValue=0.0;
                    double pwdAggIndValue = 0.0;
                    
                    String tempStr = "";
                    
                    double tempAggVal;
                    if( flag == 1 )
                    {
                        tempAggVal= aggregationService.getAggregatedNumeratorValue( selIndicator, sDate, eDate, ou );
                        if(tempAggVal == -1 ) tempAggVal = 0.0;
                        pwnumAggValue = tempAggVal;
                        tempAggVal = aggregationService.getAggregatedDenominatorValue( selIndicator, sDate, eDate, ou );
                        if(tempAggVal == -1 ) tempAggVal = 0.0;
                        pwdenAggValue = tempAggVal;
                        
                        tempAggVal = aggregationService.getAggregatedIndicatorValue( selIndicator, sDate, eDate, ou );
                        if(tempAggVal == -1 ) tempAggVal = 0.0;
                        pwdAggIndValue = tempAggVal;
                        
                        pwdAggIndValue = Math.round( pwdAggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        tempStr = ""+pwdAggIndValue;

                    }
                    else if( flag == 2 )
                    {
                        if( deSelection.equalsIgnoreCase( "optioncombo" ) )
                        {
                            if( selDataElement.getType().equalsIgnoreCase( DataElement.TYPE_INT ) )
                            {
                                tempAggVal = aggregationService.getAggregatedDataValue( selDataElement, selDecoc, sDate, eDate, ou );
                                if(tempAggVal == -1 ) tempAggVal = 0.0;
                                pwdvAggValue = tempAggVal;
                                
                                tempStr = ""+(int) pwdvAggValue;
                            }
                            else
                            {
                                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                if( tempPeriod != null )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, selDecoc );
                                
                                    if( dataValue != null )
                                    {
                                        tempStr = dataValue.getValue();
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else
                        {
                            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                                selDataElement.getCategoryCombo().getOptionCombos() );

                            if( selDataElement.getType().equalsIgnoreCase( DataElement.TYPE_INT ) )
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator.next();
    
                                    tempAggVal = aggregationService.getAggregatedDataValue( selDataElement, decoc1, sDate, eDate, ou );
                                    if(tempAggVal == -1 ) tempAggVal = 0.0;
                                    pwdvAggValue += tempAggVal;                                
                                }
                                
                                tempStr = ""+(int)pwdvAggValue;
                            }
                            else
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator.next();
    
                                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                    Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                    if( tempPeriod != null )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, decoc1 );
                                    
                                        if( dataValue != null )
                                        {
                                            tempStr += dataValue.getValue() +" : ";
                                        }
                                        else
                                        {
                                            tempStr = "  ";
                                        }
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                            }
                        }
                    }
                    
                    if( aggPeriodCB == null )
                    {
                        if( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, startRow+1, periodNames.get( periodCount ), getCellFormat1() ) );
                        }
                        
                        if( flag == 1 )
                        {
                            sheet0.addCell( new Number( colCount, headerRow+1+rowCount, pwdAggIndValue, getCellFormat2() ) );
                        }
                        else
                        {
                            if( selDataElement.getType().equalsIgnoreCase( DataElement.TYPE_INT ) )
                            {
                                sheet0.addCell( new Number( colCount, headerRow+1+rowCount, (int) pwdvAggValue, getCellFormat2() ) );
                            }
                            else
                            {
                                sheet0.addCell( new Label( colCount, headerRow+1+rowCount, tempStr, getCellFormat2() ) );
                            }
                        }
                        
                        colCount++;
                    }
                    else
                    {
                        numAggValue += pwnumAggValue;
                        denAggValue += pwdenAggValue;
                        dvAggValue += pwdvAggValue;
                        aggStrValue += tempStr + " - ";
                    }
                    
                    periodCount++;
                }// Period Loop
                
                if( aggPeriodCB != null )
                {
                    if( flag == 1 )
                    {
                        try
                        {
                            double tempDouble = Double.parseDouble( selIndicator.getDenominator().trim() );
                            
                            //if(tempInt == 1) denAggValue = 1;
                            denAggValue = tempDouble;
                        }
                        catch( Exception e )
                        {
                            System.out.println("Denominator is not expression");
                        }
                        
                        double aggIndValue = ( numAggValue / denAggValue ) * selIndicator.getIndicatorType().getFactor();
                        aggIndValue = Math.round( aggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        sheet0.addCell( new Number( colCount, headerRow+1+rowCount, aggIndValue, getCellFormat2() ) );
                    }
                    else if( flag == 2 )
                    {
                        if( selDataElement.getType().equalsIgnoreCase( DataElement.TYPE_INT ) )
                        {
                            sheet0.addCell( new Number( colCount, headerRow+1+rowCount, (int)dvAggValue, getCellFormat2() ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( colCount, headerRow+1+rowCount, aggStrValue, getCellFormat2() ) );
                        }
                    }
                    
                    colCount++;
                }
                //colCount++;
            }// Service loop end
            
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();
        
        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
        
        statementManager.destroy();

        return SUCCESS;
    }

    
    // Returns the OrgUnitTree for which Root is the orgUnit
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>(orgUnit.getChildren());
        
        if( children != null )
        {
            ouChildCountMap.put( orgUnit, children.size() );
        }
        else
        {
            ouChildCountMap.put( orgUnit, 0 );
        }
        
        Collections.sort( children, new OrganisationUnitNameComparator() );

        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = (OrganisationUnit) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }// getChildOrgUnitTree end
    
    
    public WritableCellFormat getCellFormat1() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();                        
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_25 );   
        wCellformat.setWrap( true );

        return wCellformat;
    }
    
    public WritableCellFormat getCellFormat2() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();                        
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

}// class end
