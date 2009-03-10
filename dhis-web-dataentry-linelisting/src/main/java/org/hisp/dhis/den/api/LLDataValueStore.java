package org.hisp.dhis.den.api;

import java.util.Collection;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.source.Source;

public interface LLDataValueStore
{

    String ID = LLDataValueStore.class.getName();
    
    /**
     * Adds a DataValue.
     * 
     * @param dataValue the DataValue to add.
     */
    void addDataValue( LLDataValue dataValue );

    /**
     * Updates a DataValue.
     * 
     * @param dataValue the DataValue to update.
     */
    void updateDataValue( LLDataValue dataValue );

    /**
     * Deletes a DataValue.
     * 
     * @param dataValue the DataValue to delete.
     */
    void deleteDataValue( LLDataValue dataValue );

    /**
     * Deletes all DataValues registered for the given Source.
     * 
     * @param source the Source for which the DataValues should be deleted.
     * @return the number of deleted DataValues.
     */
    int deleteDataValuesBySource( Source source );
    
    /**
     * Deletes all DataValues registered for the given DataElemt.
     * 
     * @param dataElement the DataElement for which the DataValues should be deleted.
     * @return the number of deleted DataValues.
     */
    int deleteDataValuesByDataElement( DataElement dataElement );

    LLDataValue getDataValue(  Source source, DataElement dataElement, Period period, DataElementCategoryOptionCombo optionCombo, int recordNo);
    
    /**
     * Returns a DataValue.
     * 
     * @param source the Source of the DataValue.
     * @param dataElement the DataElement of the DataValue.
     * @param period the Period of the DataValue.
     * @return the DataValue which corresponds to the given parameters, or null
     *         if no match.
     */
    Collection<LLDataValue> getDataValues( Source source, DataElement dataElement, Period period );
    
    /**
     * Returns a DataValue.
     * 
     * @param source the Source of the DataValue.
     * @param dataElement the DataElement of the DataValue.
     * @param period the Period of the DataValue.
     * @return the DataValue which corresponds to the given parameters, or null
     *         if no match.
     */
    Collection<LLDataValue> getDataValues( Source source, DataElement dataElement, Period period, DataElementCategoryOptionCombo optionCombo );

    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

    /**
     * Returns all DataValues.
     * 
     * @return a collection of all DataValues.
     */
    Collection<LLDataValue> getAllDataValues();
    
    /**
     * Returns all DataValues for a given Source and Period.
     * 
     * @param source the Source of the DataValues.
     * @param period the Period of the DataValues.
     * @return a collection of all DataValues which match the given Source and
     *         Period, or an empty collection if no values match.
     */
    Collection<LLDataValue> getDataValues( Source source, Period period );
    
    /**
     * Returns all DataValues for a given Source and DataElement.
     * 
     * @param source the Source of the DataValues.
     * @param dataElement the DataElement of the DataValues.
     * @return a collection of all DataValues which match the given Source and
     *         DataElement, or an empty collection if no values match.
     */
    Collection<LLDataValue> getDataValues( Source source, DataElement dataElement );

    /**
     * Returns all DataValues for a given collection of Sources and a
     * DataElement.
     * 
     * @param sources the Sources of the DataValues.
     * @param dataElement the DataElement of the DataValues.
     * @return a collection of all DataValues which match any of the given
     *         Sources and the DataElement, or an empty collection if no values
     *         match.
     */
    Collection<LLDataValue> getDataValues( Collection<? extends Source> sources, DataElement dataElement );

    /**
     * Returns all DataValues for a given Source, Period, and collection of
     * DataElements.
     * 
     * @param source the Source of the DataValues.
     * @param period the Period of the DataValues.
     * @param dataElements the DataElements of the DataValues.
     * @return a collection of all DataValues which match the given Source,
     *         Period, and any of the DataElements, or an empty collection if no
     *         values match.
     */
    Collection<LLDataValue> getDataValues( Source source, Period period, Collection<DataElement> dataElements );
    
    /**
     * Returns all DataValues for a given Source, Period, collection of
     * DataElements and collection of optioncombos.
     * 
     * @param source the Source of the DataValues.
     * @param period the Period of the DataValues.
     * @param dataElements the DataElements of the DataValues.
     * @return a collection of all DataValues which match the given Source,
     *         Period, and any of the DataElements, or an empty collection if no
     *         values match.
     */
    Collection<LLDataValue> getDataValues( Source source, Period period, Collection<DataElement> dataElements, Collection<DataElementCategoryOptionCombo> optionCombos );
    
    /**
     * Returns all DataValues for a given DataElement, collection of Periods, and 
     * collection of Sources.
     * 
     * @param dataElement the DataElements of the DataValues.
     * @param periods the Periods of the DataValues.
     * @param sources the Sources of the DataValues.
     * @return a collection of all DataValues which match the given DataElement,
     *         Periods, and Sources.
     */
    Collection<LLDataValue> getDataValues( DataElement dataElement, Collection<Period> periods, 
        Collection<? extends Source> sources );
    
    /**
     * Returns all DataValues for a given DataElement, DataElementCategoryOptionCombo,
     * collection of Periods, and collection of Sources.
     * 
     * @param dataElement the DataElements of the DataValues.
     * @param optionCombo the DataElementCategoryOptionCombo of the DataValues.
     * @param periods the Periods of the DataValues.
     * @param sources the Sources of the DataValues.
     * @return a collection of all DataValues which match the given DataElement,
     *         Periods, and Sources.
     */
    Collection<LLDataValue> getDataValues( DataElement dataElement, DataElementCategoryOptionCombo optionCombo, 
        Collection<Period> periods, Collection<? extends Source> sources );
    
    /**
     * Returns all DataValues for a given collection of DataElements, collection of Periods, and
     * collection of Sources, limited by a given start indexs and number of elements to return.
     * 
     * @param dataElements the DataElements of the DataValue.
     * @param periods the Periods of the DataValue.
     * @param sources the Sources of the DataValues.
     * @param firstResult the zero-based index of the first DataValue in the collection to return.
     * @param maxResults the maximum number of DataValues to return. 0 means no restrictions.
     * @return a collection of all DataValues which match the given collection of DataElements,
     *         Periods, and Sources, limited by the firstResult and maxResults property.
     */
    Collection<LLDataValue> getDataValues( Collection<DataElement> dataElements, Collection<Period> periods, 
        Collection<? extends Source> sources, int firstResult, int maxResults );
    
    /**
     * Returns all DataValues for a given collection of DataElementCategoryOptionCombos.
     * 
     * @param optionCombos the DataElementCategoryOptionCombos of the DataValue.
     * @return a collection of all DataValues which match the given collection of
     *         DataElementCategoryOptionCombos.
     */
    Collection<LLDataValue> getDataValues( Collection<DataElementCategoryOptionCombo> optionCombos );
    
    /**
     * Returns all DataValues for a given collection of DataElements.
     * 
     * @param dataElement the DataElements of the DataValue.
     * @return a collection of all DataValues which mach the given collection of DataElements.
     */
    Collection<LLDataValue> getDataValues( DataElement dataElement );
    
    int getMaxRecordNo();

    Map<String,String> processLineListBirths(OrganisationUnit organisationUnit, Period period);
    
    Map<String,String> processLineListDeaths(OrganisationUnit organisationUnit, Period periodL);
    
    Map<String,String> processLineListMaternalDeaths(OrganisationUnit organisationUnit, Period periodL);
}
