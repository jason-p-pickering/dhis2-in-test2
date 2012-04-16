package org.hisp.dhis.coldchain.inventory;

import java.io.Serializable;
import java.util.Date;

public class EquipmentWorkingStatus implements Serializable
{

    private EquipmentInstance equipmentInstance;
    
    private Date reportingDate;
    
    private Date updationDate;
    
    private String status;
    
    private String description;
    
    private String storedBy;
    
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public EquipmentWorkingStatus( )
    {
        
    }
    
    public EquipmentWorkingStatus( EquipmentInstance equipmentInstance, Date reportingDate, Date updationDate, String status )
    {
        this.equipmentInstance = equipmentInstance;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof EquipmentWorkingStatus) )
        {
            return false;
        }

        final EquipmentWorkingStatus other = (EquipmentWorkingStatus) o;

        return equipmentInstance.equals( other.getEquipmentInstance() );

    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + equipmentInstance.hashCode();

        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public EquipmentInstance getEquipmentInstance()
    {
        return equipmentInstance;
    }

    public void setEquipmentInstance( EquipmentInstance equipmentInstance )
    {
        this.equipmentInstance = equipmentInstance;
    }

    public Date getReportingDate()
    {
        return reportingDate;
    }

    public void setReportingDate( Date reportingDate )
    {
        this.reportingDate = reportingDate;
    }

    public Date getUpdationDate()
    {
        return updationDate;
    }

    public void setUpdationDate( Date updationDate )
    {
        this.updationDate = updationDate;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus( String status )
    {
        this.status = status;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

}
