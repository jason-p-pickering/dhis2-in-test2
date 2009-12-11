package org.hisp.dhis.dashboard.nr.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;

import com.opensymphony.xwork2.Action;

import edu.emory.mathcs.backport.java.util.Collections;
import org.amplecode.quick.StatementManager;

public class GenerateNullReporterResultAction
    implements Action
{
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataSetService dataSetService;

    public DataSetService getDataSetService()
    {
        return dataSetService;
    }

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }


    // ---------------------------------------------------------------
    // Output Parameters
    // ---------------------------------------------------------------
    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    private List<DataSet> dataSetList1;

    public List<DataSet> getDataSetList1()
    {
        return dataSetList1;
    }

    private List<Integer> results;

    public List<Integer> getResults()
    {
        return results;
    }

    private Map<DataSet, Collection<Period>> dataSetPeriods;

    public Map<DataSet, Collection<Period>> getDataSetPeriods()
    {
        return dataSetPeriods;
    }

    List<Period> selectedPeriodList;

    public List<Period> getSelectedPeriodList()
    {
        return selectedPeriodList;
    }

    List<String> levelNames;

    public List<String> getLevelNames()
    {
        return levelNames;
    }

    private int maxOULevel;

    public int getMaxOULevel()
    {
        return maxOULevel;
    }

    private int minOULevel;

    // ---------------------------------------------------------------
    // Input Parameters
    // ---------------------------------------------------------------
    private String ouIDTB;

    public void setOuIDTB( String ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private String includeZeros;

    public void setIncludeZeros( String includeZeros )
    {
        this.includeZeros = includeZeros;
    }

    private int sDateLB;

    public void setSDateLB( int dateLB )
    {
        sDateLB = dateLB;
    }

    public int getSDateLB()
    {
        return sDateLB;
    }

    private int eDateLB;

    public void setEDateLB( int dateLB )
    {
        eDateLB = dateLB;
    }

    public int getEDateLB()
    {
        return eDateLB;
    }

    private List<OrganisationUnit> selOUList;

    private OrganisationUnit selOrgUnit;

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

    private List<String> selectedDataElements;

    public void setSelectedDataElements( List<String> selectedDataElements )
    {
        this.selectedDataElements = selectedDataElements;
    }

    public List<String> getSelectedDataElements()
    {
        return selectedDataElements;
    }

    private Map<DataElement, PeriodType> dePeriodTypeMap;

    public Map<DataElement, PeriodType> getDePeriodTypeMap()
    {
        return dePeriodTypeMap;
    }

    private Map<OrganisationUnit, Map<Period, List<DataElement>>> nullReportResult;

    public Map<OrganisationUnit, Map<Period, List<DataElement>>> getNullReportResult()
    {
        return nullReportResult;
    }

    private Map<Period, List<DataElement>> periodDeListMap;

    public Map<Period, List<DataElement>> getPeriodDeListMap()
    {
        return periodDeListMap;
    }

    private int size;

    public int getSize()
    {
        return size;
    }

    List<DataSet> dataSetList;

    public List<DataSet> getDataSetList()
    {
        return dataSetList;
    }

    private List<Period> periods;

    public List<Period> getPeriods()
    {
        return periods;
    }

    // private List<Period>
    private List<Period> periodsColl;

    private List<OrganisationUnit> ouHavingNullValues;

    public List<OrganisationUnit> getOuHavingNullValues()
    {
        return ouHavingNullValues;
    }

    private List<OrganisationUnit> ouHavingNullValuesWithLowerLevel;

    public List<OrganisationUnit> getOuHavingNullValuesWithLowerLevel()
    {
        return ouHavingNullValuesWithLowerLevel;
    }

    private OrganisationUnit parentOu;

    public OrganisationUnit getParentOu()
    {
        return parentOu;
    }

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    private Map<OrganisationUnit, Integer> ouChildCountMap;

    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------
    public String execute()
        throws Exception
    {
	statementManager.initialise();
        // VelocityContext context = new VelocityContext();
        simpleDateFormat = new SimpleDateFormat( "MMM y" );

        nullReportResult = new HashMap<OrganisationUnit, Map<Period, List<DataElement>>>();
        selOUList = new ArrayList<OrganisationUnit>();
        ouChildCountMap = new HashMap<OrganisationUnit, Integer>();
        // OrgUnit Related Info

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
            while ( ouIterator.hasNext() )
            {
                OrganisationUnit orgU = ouIterator.next();
                if ( organisationUnitService.getLevelOfOrganisationUnit( orgU ) > orgUnitLevelCB )
                {
                    ouIterator.remove();
                }
            }
        }

        minOULevel = 1;
        minOULevel = organisationUnitService.getLevelOfOrganisationUnit( selOUList.get( 0 ) );

        int maxOuLevel = 1;
        if ( orgUnitLevelCB != null )
        {
            maxOuLevel = orgUnitLevelCB;
        }
        else
        {
            maxOuLevel = minOULevel;
        }

        Period startDate = periodService.getPeriod( sDateLB );
        Period endDate = periodService.getPeriod( eDateLB );
        List<OrganisationUnit> ouHavingNullValuesWithHigherLevel = new ArrayList<OrganisationUnit>();
        ouHavingNullValuesWithLowerLevel = new ArrayList<OrganisationUnit>();
        ouHavingNullValues = new ArrayList<OrganisationUnit>();
        List<DataElement> deList = new ArrayList<DataElement>();

        dePeriodTypeMap = new HashMap<DataElement, PeriodType>();
        // periodDeListMap = new HashMap<Period, List<DataElement>>();

        for ( String deid : selectedDataElements )
        {
            DataElement de1 = dataElementService.getDataElement( Integer.parseInt( deid ) );

            dataSetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );

            int flag = 0;
            for ( DataSet ds : dataSetList )
            {
                if ( ds.getDataElements().contains( de1 ) )
                {
                    dePeriodTypeMap.put( de1, ds.getPeriodType() );
                    flag = 1;
                    break;
                }
            }
            if ( flag == 0 )
            {
                dePeriodTypeMap.put( de1, new MonthlyPeriodType() );
            }

            deList.add( de1 );
        }

        periodsColl = new ArrayList<Period>( periodService.getIntersectingPeriods( startDate.getStartDate(), endDate
            .getEndDate() ) );
        size = periodsColl.size();
        // System.out.println("periods size is "+size);
        Collections.sort( periodsColl, new PeriodTypeComparator() );
        periods = new ArrayList<Period>();

        for ( OrganisationUnit curOu : selOUList )
        {
            // System.out.println("OrganisationUnit "+curOu);

            periodDeListMap = new HashMap<Period, List<DataElement>>();
            for ( Period p : periodsColl )
            {
                // System.out.println(p.getStartDate() + " - "+p.getEndDate());
                List<DataElement> resultDeList = new ArrayList<DataElement>();

                for ( DataElement de : deList )
                {

                    if ( (dePeriodTypeMap.get( de ).equals( p.getPeriodType() )) )
                    {
                        double aggValue = 0;
                        if ( ouSelCB != null )
                        {

                            List<DataElementCategoryOptionCombo> decocList = new ArrayList<DataElementCategoryOptionCombo>(
                                de.getCategoryCombo().getOptionCombos() );
                            for ( DataElementCategoryOptionCombo decoc : decocList )
                            {
                                double tempVal = aggregationService.getAggregatedDataValue( de, decoc,
                                    p.getStartDate(), p.getEndDate(), curOu );
                                // System.out.println("tempVal = " + tempVal);
                                if ( includeZeros != null )
                                {
                                    if ( tempVal > 0.0 )
                                    {
                                        aggValue += tempVal;
                                    }
                                    else
                                    {
                                        aggValue = -1.0;
                                    }
                                }
                                else
                                {
                                    if ( tempVal >= 0.0 )
                                    {
                                        aggValue += tempVal;
                                    }
                                    else
                                    {
                                        aggValue = -1.0;
                                    }
                                }
                            }

                            // System.out.println("aggValue = "+aggValue);
                            if ( aggValue < 0.0 )
                            {
                                resultDeList.add( de );
                            }
                        }
                        else
                        {
							DataElementCategoryOptionCombo decoc = de.getCategoryCombo().getOptionCombos().iterator().next();
                            DataValue dataValue = dataValueService.getDataValue( curOu, de, p, decoc );

                            if ( dataValue == null )
                            {
                                resultDeList.add( de );
                                // System.out.println("dataValue is null ");
                            }
                            else
                            {
                                if ( includeZeros != null )
                                {
                                    // System.out.println("dataValue is not null, dataValue = "+dataValue.getValue());
                                    if ( Integer.parseInt( dataValue.getValue() ) == 0 )
                                    {
                                        resultDeList.add( de );

                                    }
                                }

                            }

                        }
                    }
                }
                if ( resultDeList.size() != 0 )
                {
                    periodDeListMap.put( p, resultDeList );
                    // nullReportResult.put(curOu, periodDeListMap);
                }
            }
            // System.out.println("----------------------- " +
            // periodDeListMap.size() + " " + curOu );

            if ( periodDeListMap.size() != 0 )
            {
                nullReportResult.put( curOu, periodDeListMap );

                ouHavingNullValues.add( curOu );
                ouHavingNullValuesWithHigherLevel.add( curOu );
                for ( Period p : periodDeListMap.keySet() )
                {
                    if ( !periods.contains( p ) )
                    {
                        periods.add( p );
                    }

                }
            }

        }
        // System.out.println("ouHavingNullValues size = "+ouHavingNullValues.size()
        // + "minOULevel = "+minOULevel + " maxOuLevel = "+maxOuLevel);
        for ( int level = minOULevel; level < maxOuLevel; level++ )
        {
            for ( OrganisationUnit ou : ouHavingNullValuesWithHigherLevel )
            {
                int levelOu = organisationUnitService.getLevelOfOrganisationUnit( ou );
                if ( levelOu == level )
                {
                    ouHavingNullValues.remove( ou );
                    ouHavingNullValuesWithLowerLevel.add( ou );
                    // System.out.println("--------------------- removing ou "+ou.getName());
                }
            }
        }

        if ( periods != null )
        {
            size = periods.size();
        }
        else
        {
            size = 0;
        }
        Collections.sort( ouHavingNullValues, new OrganisationUnitNameComparator() );
        Collections.sort( periods, new PeriodComparator() );
        Collections.sort( periods, new PeriodTypeComparator() );
	statementManager.destroy();
        return SUCCESS;
    }

    // Returns the OrgUnitTree for which Root is the orgUnit
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );

        if ( children != null )
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
}
