package org.hisp.dhis.den.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.den.api.LLDataSets;
import org.hisp.dhis.den.api.LLDataValue;
import org.hisp.dhis.den.api.LLDataValueStore;
import org.hisp.dhis.den.util.DBConnection;
import org.hisp.dhis.hibernate.HibernateSessionManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.source.Source;

public class HibernateLLDataValueStore
    implements LLDataValueStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private HibernateSessionManager sessionManager;

    public void setSessionManager( HibernateSessionManager sessionManager )
    {
        this.sessionManager = sessionManager;
    }

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;
    
    public void setDataElementCategoryOptionComboService( DataElementCategoryOptionComboService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    private DBConnection dbConnection;
    
    public void setDbConnection( DBConnection dbConnection )
    {
        this.dbConnection = dbConnection;
    }
        
    
    // -------------------------------------------------------------------------
    // Support methods for reloading periods
    // -------------------------------------------------------------------------

    private final Period reloadPeriod( Period period )
    {
        Session session = sessionManager.getCurrentSession();

        if ( session.contains( period ) )
        {
            return period; // Already in session, no reload needed
        }

        return periodStore.getPeriod( period.getStartDate(), period.getEndDate(), period.getPeriodType() );
    }

    private final Period reloadPeriodForceAdd( Period period )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            periodStore.addPeriod( period );

            return period;
        }

        return storedPeriod;
    }

    // -------------------------------------------------------------------------
    // Basic DataValue
    // -------------------------------------------------------------------------

    public void addDataValue( LLDataValue dataValue )
    {
        dataValue.setPeriod( reloadPeriodForceAdd( dataValue.getPeriod() ) );

        // Session session = sessionManager.getCurrentSession();

        // session.save( dataValue );

        //Connection con = (new DBConnection()).openConnection();
        Connection con = dbConnection.openConnection();
        
        PreparedStatement pst = null;

        java.sql.Date date1 = new java.sql.Date( dataValue.getTimestamp().getTime() );

        String query = "INSERT INTO lldatavalue VALUES (?,?,?,?,?,?,?,?,?)";

        try
        {
            pst = con.prepareStatement( query );

            pst.setInt( 1, dataValue.getDataElement().getId() );
            pst.setInt( 2, dataValue.getPeriod().getId() );
            pst.setInt( 3, dataValue.getSource().getId() );
            pst.setInt( 4, dataValue.getOptionCombo().getId() );
            pst.setInt( 5, dataValue.getRecordNo() );
            pst.setString( 6, dataValue.getValue() );
            pst.setString( 7, dataValue.getStoredBy() );
            pst.setDate( 8, date1 );
            pst.setString( 9, dataValue.getComment() );

            pst.executeUpdate();
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception while inserting : " + e.getMessage() );
        }
        finally
        {
            try
            {
                if ( pst != null )
                    pst.close();

                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while closing DB Connections : " + e.getMessage() );
            }
        }
    }

    public void updateDataValue( LLDataValue dataValue )
    {
        dataValue.setPeriod( reloadPeriodForceAdd( dataValue.getPeriod() ) );

        // Session session = sessionManager.getCurrentSession();

        // session.update( dataValue );

        //Connection con = (new DBConnection()).openConnection();
        Connection con = dbConnection.openConnection();
        
        PreparedStatement pst = null;

        java.sql.Date date1 = new java.sql.Date( dataValue.getTimestamp().getTime() );

        String query = "UPDATE lldatavalue SET value = ?, lastupdated = ? WHERE dataelementid = ? and periodid = ? and sourceid = ? and categoryoptioncomboid = ? and recordno = ?";

        try
        {
            pst = con.prepareStatement( query );

            pst.setString( 1, dataValue.getValue() );
            pst.setDate( 2, date1 );
            pst.setInt( 3, dataValue.getDataElement().getId() );
            pst.setInt( 4, dataValue.getPeriod().getId() );
            pst.setInt( 5, dataValue.getSource().getId() );
            pst.setInt( 6, dataValue.getOptionCombo().getId() );
            pst.setInt( 7, dataValue.getRecordNo() );

            pst.executeUpdate();
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception while updating: " + e.getMessage() );
        }
        finally
        {
            try
            {
                if ( pst != null )
                    pst.close();

                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while closing DB Connections : " + e.getMessage() );
            }
        }
    }

    public void deleteDataValue( LLDataValue dataValue )
    {
        Session session = sessionManager.getCurrentSession();

        session.delete( dataValue );
    }

    public int deleteDataValuesBySource( Source source )
    {
        Session session = sessionManager.getCurrentSession();

        Query query = session.createQuery( "delete LLDataValue where source = :source" );
        query.setEntity( "source", source );

        return query.executeUpdate();
    }

    public int deleteDataValuesByDataElement( DataElement dataElement )
    {
        Session session = sessionManager.getCurrentSession();

        Query query = session.createQuery( "delete LLDataValue where dataElement = :dataElement" );
        query.setEntity( "dataElement", dataElement );

        return query.executeUpdate();
    }

    public LLDataValue getDataValue( Source source, DataElement dataElement, Period period,
        DataElementCategoryOptionCombo optionCombo, int recordNo )
    {
        Session session = sessionManager.getCurrentSession();

        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return null;
        }

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "optionCombo", optionCombo ) );
        criteria.add( Restrictions.eq( "recordNo", recordNo ) );

        return (LLDataValue) criteria.uniqueResult();

    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( Source source, DataElement dataElement, Period period )
    {
        Session session = sessionManager.getCurrentSession();

        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return null;
        }

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( Source source, DataElement dataElement, Period period,
        DataElementCategoryOptionCombo optionCombo )
    {
        Session session = sessionManager.getCurrentSession();

        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return null;
        }

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "optionCombo", optionCombo ) );

        return criteria.list();
    }

    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getAllDataValues()
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( Source source, Period period )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return Collections.emptySet();
        }

        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( Source source, DataElement dataElement )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( Collection<? extends Source> sources, DataElement dataElement )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.in( "source", sources ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( Source source, Period period, Collection<DataElement> dataElements )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return Collections.emptySet();
        }

        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.in( "dataElement", dataElements ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( Source source, Period period, Collection<DataElement> dataElements,
        Collection<DataElementCategoryOptionCombo> optionCombos )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return Collections.emptySet();
        }

        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.in( "dataElement", dataElements ) );
        criteria.add( Restrictions.in( "optionCombo", optionCombos ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( DataElement dataElement, Collection<Period> periods,
        Collection<? extends Source> sources )
    {
        Collection<Period> storedPeriods = new ArrayList<Period>();

        for ( Period period : periods )
        {
            Period storedPeriod = reloadPeriod( period );

            if ( storedPeriod != null )
            {
                storedPeriods.add( storedPeriod );
            }
        }

        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.in( "period", storedPeriods ) );
        criteria.add( Restrictions.in( "source", sources ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( DataElement dataElement, DataElementCategoryOptionCombo optionCombo,
        Collection<Period> periods, Collection<? extends Source> sources )
    {
        Collection<Period> storedPeriods = new ArrayList<Period>();

        for ( Period period : periods )
        {
            Period storedPeriod = reloadPeriod( period );

            if ( storedPeriod != null )
            {
                storedPeriods.add( storedPeriod );
            }
        }

        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "optionCombo", optionCombo ) );
        criteria.add( Restrictions.in( "period", storedPeriods ) );
        criteria.add( Restrictions.in( "source", sources ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( Collection<DataElement> dataElements, Collection<Period> periods,
        Collection<? extends Source> sources, int firstResult, int maxResults )
    {
        Collection<Period> storedPeriods = new ArrayList<Period>();

        for ( Period period : periods )
        {
            Period storedPeriod = reloadPeriod( period );

            if ( storedPeriod != null )
            {
                storedPeriods.add( storedPeriod );
            }
        }

        if ( storedPeriods.size() == 0 )
        {
            return Collections.emptySet();
        }

        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );

        criteria.add( Restrictions.in( "dataElement", dataElements ) );
        criteria.add( Restrictions.in( "period", storedPeriods ) );
        criteria.add( Restrictions.in( "source", sources ) );

        if ( maxResults != 0 )
        {
            criteria.addOrder( Order.asc( "dataElement" ) );
            criteria.addOrder( Order.asc( "period" ) );
            criteria.addOrder( Order.asc( "source" ) );

            criteria.setFirstResult( firstResult );
            criteria.setMaxResults( maxResults );
        }

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( Collection<DataElementCategoryOptionCombo> optionCombos )
    {

        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.in( "optionCombo", optionCombos ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( DataElement dataElement )
    {

        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
    }

    public int getMaxRecordNo()
    {
        Session session = sessionManager.getCurrentSession();

        // Criteria criteria = session.createCriteria( LLDataValue.class );

        String sql_query = "from LLDataValue order by recordNo";

        Query query = session.createQuery( sql_query );

        List<LLDataValue> list = new ArrayList<LLDataValue>( query.list() );

        if ( list == null || query.list().isEmpty() )
            return 0;

        Integer maxCount = (Integer) list.get( list.size() - 1 ).getRecordNo();

        return maxCount.intValue();
    }

    public Map<String, String> processLineListBirths( OrganisationUnit organisationUnit, Period periodL )
    {
        Map<String, String> deValueMap = new HashMap<String, String>();
        int ouId = organisationUnit.getId();

        Period storedPeriod = reloadPeriod( periodL );
        int pId = storedPeriod.getId();

        //Connection con = (new DBConnection()).openConnection();
        Connection con = dbConnection.openConnection();

        int[] aggDeIds = { LLDataSets.LLB_BIRTHS, LLDataSets.LLB_BIRTHS_MALE, LLDataSets.LLB_BIRTHS_FEMALE,
            LLDataSets.LLB_WEIGHED_MALE, LLDataSets.LLB_WEIGHED_FEMALE, LLDataSets.LLB_WEIGHED_LESS1800_MALE,
            LLDataSets.LLB_WEIGHED_LESS1800_FEMALE, LLDataSets.LLB_WEIGHED_LESS2500_MALE,
            LLDataSets.LLB_WEIGHED_LESS2500_FEMALE, LLDataSets.LLB_BREASTFED_MALE, LLDataSets.LLB_BREASTFED_FEMALE };
        String[] queries = new String[11];

        // Total Live Birth
        queries[0] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLB_CHILD_NAME;
        // Total Live Birth Male
        queries[1] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLB_SEX + "  AND value = 'M'";
        // Total Live Birth Female
        queries[2] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLB_SEX + "  AND value = 'F'";

        // Live Birth Weighed Male
        queries[3] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = "
            + ouId
            + " AND periodid = "
            + pId
            + " AND dataelementid = "
            + LLDataSets.LLB_WIEGH
            + " AND (value NOT LIKE 'NK' OR value IS not null) AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLB_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Live Birth Weighed Female
        queries[4] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = "
            + ouId
            + " AND periodid = "
            + pId
            + " AND dataelementid = "
            + LLDataSets.LLB_WIEGH
            + " AND (value NOT LIKE 'nk' OR value IS not null) AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLB_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Live Birth Weighed Lessthan 1800 Male
        queries[5] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLB_WIEGH
            + " AND value < '1800' AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLB_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Live Birth Weighed Lessthan 1800 Female
        queries[6] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLB_WIEGH
            + " AND value < '1800' AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLB_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Live Birth Weighed Lessthan 2500 Male
        queries[7] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLB_WIEGH
            + " AND value < '2500' AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLB_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Live Birth Weighed Lessthan 2500 Female
        queries[8] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLB_WIEGH
            + " AND value < '2500' AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLB_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Live Birth Breastfeeding in FirstHour Male
        queries[9] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLB_BREASTFED
            + " AND value LIKE 'Y' AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLB_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Live Birth Breastfeeding in FirstHour Female
        queries[10] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLB_BREASTFED
            + " AND value LIKE 'Y' AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLB_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        try
        {
            for ( int i = 0; i < aggDeIds.length; i++ )
            {
                DataElement de = dataElementService.getDataElement( aggDeIds[i] );
                DataElementCategoryOptionCombo oc = de.getCategoryCombo().getOptionCombos().iterator().next();
                if ( de != null && oc != null )
                {
                    PreparedStatement pst = con.prepareStatement( queries[i] );
                    System.out.println( queries[i] );
                    ResultSet rs = pst.executeQuery();
                    try
                    {
                        if ( rs.next() )
                        {
                            deValueMap.put( de.getId() + ":" + oc.getId(), "" + rs.getInt( 1 ) );
                            System.out.println( "Value for " + de.getId() + " is " + rs.getInt( 1 ) );
                        }
                        else
                        {
                            deValueMap.put( de.getId() + ":" + oc.getId(), "0" );
                            System.out.println( "No Value for " + de.getId() );
                        }
                    }
                    catch ( Exception e )
                    {
                        System.out.println( "Exception : " + e.getMessage() );
                    }
                    finally
                    {
                        if ( pst != null )
                            pst.close();
                        if ( rs != null )
                            rs.close();
                    }
                }
            }
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
        }
        finally
        {
            try
            {
                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while closing DB Connections : " + e.getMessage() );
            }
        }

        return deValueMap;
    }

    public Map<String, String> processLineListDeaths( OrganisationUnit organisationUnit, Period periodL )
    {
        Map<String, String> deValueMap = new HashMap<String, String>();
        int ouId = organisationUnit.getId();

        Period storedPeriod = reloadPeriod( periodL );
        int pId = storedPeriod.getId();

        //Connection con = (new DBConnection()).openConnection();
        Connection con = dbConnection.openConnection();

        int[] aggDeIds = {
                            LLDataSets.LLD_DEATH_OVER5Y, LLDataSets.LLD_DEATH_OVER5Y_MALE, LLDataSets.LLD_DEATH_OVER5Y_FEMALE,
                            LLDataSets.LLD_DEATH_BELOW5Y, LLDataSets.LLD_DEATH_BELOW5Y_MALE, LLDataSets.LLD_DEATH_BELOW5Y_FEMALE,
                            LLDataSets.LLD_DEATH_BELOW1Y_MALE, LLDataSets.LLD_DEATH_BELOW1Y_FEMALE, LLDataSets.LLD_DEATH_BELOW1M_MALE,
                            LLDataSets.LLD_DEATH_BELOW1M_FEMALE, LLDataSets.LLD_DEATH_BELOW1W_MALE,
                            LLDataSets.LLD_DEATH_BELOW1W_FEMALE, LLDataSets.LLD_DEATH_BELOW1D_MALE,
                            LLDataSets.LLD_DEATH_BELOW1D_FEMALE,
                            
                            LLDataSets.LLD_CAUSE_DE1, LLDataSets.LLD_CAUSE_DE1, LLDataSets.LLD_CAUSE_DE2, LLDataSets.LLD_CAUSE_DE2,
                            LLDataSets.LLD_CAUSE_DE3, LLDataSets.LLD_CAUSE_DE3, LLDataSets.LLD_CAUSE_DE4, LLDataSets.LLD_CAUSE_DE4,
                            LLDataSets.LLD_CAUSE_DE5, LLDataSets.LLD_CAUSE_DE5, LLDataSets.LLD_CAUSE_DE6, LLDataSets.LLD_CAUSE_DE6,
                            LLDataSets.LLD_CAUSE_DE7, LLDataSets.LLD_CAUSE_DE7, LLDataSets.LLD_CAUSE_DE8, LLDataSets.LLD_CAUSE_DE8,
                            LLDataSets.LLD_CAUSE_DE9, LLDataSets.LLD_CAUSE_DE9, LLDataSets.LLD_CAUSE_DE10, LLDataSets.LLD_CAUSE_DE10,
                            LLDataSets.LLD_CAUSE_DE11, LLDataSets.LLD_CAUSE_DE11, LLDataSets.LLD_CAUSE_DE12, LLDataSets.LLD_CAUSE_DE12,
                            LLDataSets.LLD_CAUSE_DE13, LLDataSets.LLD_CAUSE_DE13, LLDataSets.LLD_CAUSE_DE14, LLDataSets.LLD_CAUSE_DE14,
                            LLDataSets.LLD_CAUSE_DE15, LLDataSets.LLD_CAUSE_DE15, LLDataSets.LLD_CAUSE_DE16, LLDataSets.LLD_CAUSE_DE16,
                            LLDataSets.LLD_CAUSE_DE17, LLDataSets.LLD_CAUSE_DE17, LLDataSets.LLD_CAUSE_DE18, LLDataSets.LLD_CAUSE_DE18,
                            LLDataSets.LLD_CAUSE_DE19, LLDataSets.LLD_CAUSE_DE19, LLDataSets.LLD_CAUSE_DE20, LLDataSets.LLD_CAUSE_DE20,
                            LLDataSets.LLD_CAUSE_DE21, LLDataSets.LLD_CAUSE_DE21, LLDataSets.LLD_CAUSE_DE22, LLDataSets.LLD_CAUSE_DE22,
                            LLDataSets.LLD_CAUSE_DE23, LLDataSets.LLD_CAUSE_DE23, LLDataSets.LLD_CAUSE_DE24, LLDataSets.LLD_CAUSE_DE24,
                            LLDataSets.LLD_CAUSE_DE25, LLDataSets.LLD_CAUSE_DE25, LLDataSets.LLD_CAUSE_DE26, LLDataSets.LLD_CAUSE_DE26,
                            LLDataSets.LLD_CAUSE_DE27, LLDataSets.LLD_CAUSE_DE27, LLDataSets.LLD_CAUSE_DE28, LLDataSets.LLD_CAUSE_DE28,
                        };
        
        int[] aggDeOptComIds = {
            LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT,
            LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT,
            LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT,
            LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT,
            LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT,
            LLDataSets.LLD_OPTIONCOMBO_DEFAULT,
            
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
        };

        String[] queries = new String[aggDeIds.length];

        // Death Over 5 Year
        queries[0] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value LIKE 'O5YEAR'";
        // Death Over 5 Year Male
        queries[1] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value LIKE 'O5YEAR' AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Death Over 5 Year Female
        queries[2] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value LIKE 'O5YEAR' AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        
        // Death Below 5 Year
        queries[3] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B5YEAR', 'B1YEAR', 'B1MONTH', 'B1WEEK', 'B1DAY')";
        // Death Below 5 Year Male
        queries[4] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B5YEAR', 'B1YEAR', 'B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Death Below 5 Year Female
        queries[5] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B5YEAR', 'B1YEAR', 'B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Death Below 1 Year Male
        queries[6] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1YEAR', 'B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Death Below 1 Year Female
        queries[7] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1YEAR', 'B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Death Below 1 Month Male
        queries[8] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Death Below 1 Month Female
        queries[9] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Death Below 1 Week Male
        queries[10] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Death Below 1 Week Female
        queries[11] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Death Below 1 Day Male
        queries[12] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Death Below 1 Day Female
        queries[13] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Birth Asphyxia under one  month Male
        queries[14] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_ASPHYXIA + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // Birth Asphyxia under one  month Female
        queries[15] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_ASPHYXIA + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1122 : Sepsis under one  month Male
        queries[16] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SEPSIS + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1122 : Sepsis under one  month Female
        queries[17] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SEPSIS + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1123 : Low Birth Weight under one  month Male
        queries[18] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_LOW_BIRTH_WEIGH + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1123 : Low Birth Weight under one  month Female
        queries[19] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_LOW_BIRTH_WEIGH + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1124 : Immunization reactions under one  month Male        
        queries[20] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_IMMREAC + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1124 : Immunization reactions under one  month Female
        queries[21] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_IMMREAC + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1125 : Others under one  month Male
        queries[22] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OTHERS + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1125 : Others under one  month Female
        queries[23] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OTHERS + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1126 : Not known under one  month Male
        queries[24] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_NOT_KNOWN + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1126 : Not known under one  month Female
        queries[25] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_NOT_KNOWN + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B1MONTH', 'B1WEEK', 'B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1127 : Pneumonia 1 month to 5 year Male
        queries[26] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PNEUMONIA + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B5YEAR', 'B1YEAR', 'B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1127 : Pneumonia 1 month to 5 year Female
        queries[27] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PNEUMONIA + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B5YEAR', 'B1YEAR', 'B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1128 : Diarrhoeal disease 1 month to 5 year Male
        queries[28] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIADIS + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B5YEAR', 'B1YEAR', 'B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1128 : Diarrhoeal disease 1 month to 5 year Female
        queries[29] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIADIS + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B5YEAR', 'B1YEAR', 'B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1129 : Measles 1 month to 5 year Male
        queries[30] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MEASLES + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B5YEAR', 'B1YEAR', 'B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1129 : Measles 1 month to 5 year Female
        queries[31] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MEASLES + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B5YEAR', 'B1YEAR', 'B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1130 : Other Fever related 1 month to 5 year Male
        queries[32] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B5YEAR', 'B1YEAR', 'B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1130 : Other Fever related 1 month to 5 year Female
        queries[33] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B5YEAR', 'B1YEAR', 'B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1131 : Others 1 month to 5 year Male
        queries[34] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OTHERS + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B5YEAR', 'B1YEAR', 'B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1131 : Others 1 month to 5 year Female
        queries[35] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OTHERS + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B5YEAR', 'B1YEAR', 'B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1132 : Not known 1 month to 5 year Male
        queries[36] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_NOT_KNOWN + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B5YEAR', 'B1YEAR', 'B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1132 : Not known 1 month to 5 year Female
        queries[37] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_NOT_KNOWN + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B5YEAR', 'B1YEAR', 'B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1133 : Diarrhoeal disease Above 5 years Male
        queries[38] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIADIS + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1133 : Diarrhoeal disease Above 5 years Female
        queries[39] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIADIS + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1134 : Tuberculosis Above 5 years Male
        queries[40] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TUBER + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1134 : Tuberculosis Above 5 years Female
        queries[41] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TUBER + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1135 : Malaria Above 5 years Male
        queries[42] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MALARIA + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1135 : Malaria Above 5 years Female
        queries[43] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MALARIA + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1136 : HIV/AIDS Above 5 years Male
        queries[44] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HIVAIDS + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1136 : HIV/AIDS Above 5 years Female
        queries[45] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HIVAIDS + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1137 : Other Fever related Above 5 years Male
        queries[46] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1137 : Other Fever related Above 5 years Female
        queries[47] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1138 : Pregnancy related death( maternal mortality) Above 5 years Male
        queries[48] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PRD + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1138 : Pregnancy related death( maternal mortality) Above 5 years Female
        queries[49] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PRD + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1139 : Sterilisation related deaths Above 5 years Male
        queries[50] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SRD + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1139 : Sterilisation related deaths Above 5 years Female
        queries[51] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SRD + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1140 : Accidents or injuries Above 5 years Male
        queries[52] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AI + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1140 : Accidents or injuries Above 5 years Female
        queries[53] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AI + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1141 : Suicides Above 5 years Male
        queries[54] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SUICIDES + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1141 : Suicides Above 5 years Female
        queries[55] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SUICIDES + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1142 : Animal Bites or stings Above 5 years Male
        queries[56] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_ABS + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1142 : Animal Bites or stings Above 5 years Female
        queries[57] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_ABS + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1143 : Other known Acute disease Male
        queries[58] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OKAD + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1143 : Other known Acute disease Female
        queries[59] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OKAD + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1144 : Other known Chronic disease Male
        queries[60] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OKCD + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1144 : Other known Chronic disease Female
        queries[61] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OKCD + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1145 : Cause Not Known, Above 5 years Male
        queries[62] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_NOT_KNOWN + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1145 : Cause Not Known, Above 5 years Female
        queries[63] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_NOT_KNOWN + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1146 : Respiratory Infections and Disease Male
        queries[64] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RID + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1146 : Respiratory Infections and Disease Female
        queries[65] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RID + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1147 : Heart disease and hypertension Above 5 years Male
        queries[66] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HDH + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1147 : Heart disease and hypertension Above 5 years Female
        queries[67] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HDH + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1148 : Stroke and Neurological disease Above 5 years Male
        queries[68] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SND + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1148 : Stroke and Neurological disease Above 5 years Female
        queries[69] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SND + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = " + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        try
        {
            for ( int i = 0; i < aggDeIds.length; i++ )
            {
                DataElement de = dataElementService.getDataElement( aggDeIds[i] );
                DataElementCategoryOptionCombo oc =  dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( aggDeOptComIds[i] );
                if ( de != null && oc != null )
                {
                    PreparedStatement pst = con.prepareStatement( queries[i] );                    
                    ResultSet rs = pst.executeQuery();
                    try
                    {
                        if ( rs.next() )
                        {
                            deValueMap.put( de.getId() + ":" + oc.getId(), "" + rs.getInt( 1 ) );
                            System.out.println( "Value for " + de.getId() + " is " + rs.getInt( 1 ) );
                        }
                        else
                        {
                            deValueMap.put( de.getId() + ":" + oc.getId(), "0" );
                            System.out.println( "No Value for " + de.getId() );
                        }
                    }
                    catch ( Exception e )
                    {
                        System.out.println( "Exception : " + e.getMessage() );
                    }
                    finally
                    {
                        if ( pst != null )  pst.close();
                        if ( rs != null ) rs.close();
                    }
                }
            }
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
        }
        finally
        {
            try
            {
                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while closing DB Connections : " + e.getMessage() );
            }
        }

        return deValueMap;
    }

    public Map<String, String> processLineListMaternalDeaths( OrganisationUnit organisationUnit, Period periodL )
    {
        Map<String, String> deValueMap = new HashMap<String, String>();
        int ouId = organisationUnit.getId();

        Period storedPeriod = reloadPeriod( periodL );
        int pId = storedPeriod.getId();

        //Connection con = (new DBConnection()).openConnection();
        Connection con = dbConnection.openConnection();

        int[] aggDeIds = { LLDataSets.LLMD_DURING_PREGNANCY, LLDataSets.LLMD_DURING_FIRST_TRIM,
            LLDataSets.LLMD_DURING_SECOND_TRIM, LLDataSets.LLMD_DURING_THIRD_TRIM, LLDataSets.LLMD_DURING_DELIVERY,
            LLDataSets.LLMD_AFTER_DEL_WITHIN_42DAYS, LLDataSets.LLMD_AGE_BELOW16, LLDataSets.LLMD_AGE_16TO19,
            LLDataSets.LLMD_AGE_19TO35, LLDataSets.LLMD_AGE_ABOVE35, LLDataSets.LLMD_AT_HOME, LLDataSets.LLMD_AT_SC,
            LLDataSets.LLMD_AT_PHC, LLDataSets.LLMD_AT_CHC, LLDataSets.LLMD_AT_MC, LLDataSets.LLMD_BY_UNTRAINED,
            LLDataSets.LLMD_BY_TRAINED, LLDataSets.LLMD_BY_ANM, LLDataSets.LLMD_BY_NURSE, LLDataSets.LLMD_BY_DOCTOR,
            LLDataSets.LLMD_CAUSE_ABORTION, LLDataSets.LLMD_CAUSE_OPL, LLDataSets.LLMD_CAUSE_FITS,
            LLDataSets.LLMD_CAUSE_SH, LLDataSets.LLMD_CAUSE_BBCD, LLDataSets.LLMD_CAUSE_BACD,
            LLDataSets.LLMD_CAUSE_HFBD, LLDataSets.LLMD_CAUSE_HFAD, LLDataSets.LLMD_CAUSE_NK, LLDataSets.LLMD_AGG_AUDITED

        };
        String[] queries = new String[aggDeIds.length];

        // Metarnal Death During Pregnancy
        queries[0] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DURATION_OF_PREGNANCY + "  AND value IN ('FTP', 'STP', 'TTP')";
        // Metarnal Death During First Trimester
        queries[1] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DURATION_OF_PREGNANCY + "  AND value = 'FTP'";
        // Metarnal Death During Second Trimester
        queries[2] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DURATION_OF_PREGNANCY + "  AND value = 'STP'";
        // Metarnal Death During Third Trimester
        queries[3] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DURATION_OF_PREGNANCY + "  AND value = 'TTP'";
        // Metarnal Death During Delivery
        queries[4] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DURATION_OF_PREGNANCY + "  AND value = 'DELIVERY'";
        // Metarnal Death after delivery within 42days
        queries[5] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DURATION_OF_PREGNANCY + "  AND value = 'ADW42D'";

        // Metarnal Death At age below 16
        queries[6] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_AGE_AT_DEATH + "  AND value < '16'";
        // Metarnal Death At age 16 to 19
        queries[7] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_AGE_AT_DEATH + "  AND value >= '16' and value < '19'";
        // Metarnal Death At age 19 to 35
        queries[8] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_AGE_AT_DEATH + "  AND value >= '19' and value <'35' ";
        // Metarnal Death At age above 35
        queries[9] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_AGE_AT_DEATH + "  AND value >= '35'";

        // Metarnal Death At Home
        queries[10] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DELIVERY_AT + "  AND value = 'HOME'";
        // Metarnal Death At SC
        queries[11] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DELIVERY_AT + "  AND value = 'SC'";
        // Metarnal Death At PHC
        queries[12] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DELIVERY_AT + "  AND value = 'PHC'";
        // Metarnal Death At CHC
        queries[13] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DELIVERY_AT + "  AND value = 'CHC'";
        // Metarnal Death At Medical College
        queries[14] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DELIVERY_AT + "  AND value = 'MC'";

        // Metarnal Death Assisted by Untrained
        queries[15] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_NATURE_OF_ASSISTANCE + "  AND value = 'UNTRAINED'";
        // Metarnal Death Assisted by Trained
        queries[16] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_NATURE_OF_ASSISTANCE + "  AND value = 'TRAINED'";
        // Metarnal Death Assisted by ANM
        queries[17] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_NATURE_OF_ASSISTANCE + "  AND value = 'ANM'";
        // Metarnal Death Assisted by Nurse
        queries[18] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_NATURE_OF_ASSISTANCE + "  AND value = 'NURSE'";
        // Metarnal Death Assisted by Doctor
        queries[19] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_NATURE_OF_ASSISTANCE + "  AND value = 'DOCTOR'";

        // Metarnal Death Cause Abortion
        queries[20] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'ABORTION'";
        // Metarnal Death Cause Obsturcted
        queries[21] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'OPL'";
        // Metarnal Death Cause Fits
        queries[22] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'FITS'";
        // Metarnal Death Cause Severe Hypertension
        queries[23] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'SH'";
        // Metarnal Death Cause Bleeding before Child Delivery
        queries[24] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'BBCD'";
        // Metarnal Death Cause Bleeding after Child Delivery
        queries[25] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'BACD'";
        // Metarnal Death Cause High fever before Delivery
        queries[26] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'HFBD'";
        // Metarnal Death Cause High fever after Delivery
        queries[27] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'HFAD'";
        // Metarnal Death Cause not known
        queries[28] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'NK'";
        // Metarnal Death Audited
        queries[28] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_AUDITED + "  AND value = 'Y'";

        
        try
        {
            for ( int i = 0; i < aggDeIds.length; i++ )
            {
                DataElement de = dataElementService.getDataElement( aggDeIds[i] );
                DataElementCategoryOptionCombo oc = de.getCategoryCombo().getOptionCombos().iterator().next();
                if ( de != null && oc != null )
                {
                    PreparedStatement pst = con.prepareStatement( queries[i] );
                    System.out.println( queries[i] );
                    ResultSet rs = pst.executeQuery();
                    try
                    {
                        if ( rs.next() )
                        {
                            deValueMap.put( de.getId() + ":" + oc.getId(), "" + rs.getInt( 1 ) );
                            System.out.println( "Value for " + de.getId() + " is " + rs.getInt( 1 ) );
                        }
                        else
                        {
                            deValueMap.put( de.getId() + ":" + oc.getId(), "0" );
                            System.out.println( "No Value for " + de.getId() );
                        }
                    }
                    catch ( Exception e )
                    {
                        System.out.println( "Exception : " + e.getMessage() );
                    }
                    finally
                    {
                        if ( pst != null )
                            pst.close();
                        if ( rs != null )
                            rs.close();
                    }
                }
            }
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
        }
        finally
        {
            try
            {
                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while closing DB Connections : " + e.getMessage() );
            }
        }

        return deValueMap;
    }

}
