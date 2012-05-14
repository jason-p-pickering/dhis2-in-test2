package org.hisp.dhis.coldchain.inventory;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.dataentryform.DataEntryForm;

//public class InventoryType implements Serializable
public class InventoryType extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    private int id;
    
    private String name;
    
    private String description;
    
    private boolean tracking;
    
    private CatalogType catalogType;
    
   // private Set<InventoryTypeAttribute> inventoryTypeAttributes;
    
    private List<InventoryTypeAttribute> inventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>();
    
    private DataEntryForm dataEntryForm;
    

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public InventoryType()
    {
        
    }
    public InventoryType( String name, boolean tracking )
    {
        this.name = name;
        this.tracking = tracking;
    }
    
    public InventoryType( String name, String description, boolean tracking, CatalogType catalogType )
    {
        this.name = name;
        this.description = description;
        this.tracking = tracking;
        this.catalogType = catalogType;
    }
    
    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------
    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

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

        if ( !(o instanceof InventoryType) )
        {
            return false;
        }

        final InventoryType other = (InventoryType) o;

        return name.equals( other.getName() );
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public int getId()
    {
        return id;
    }
    public void setId( int id )
    {
        this.id = id;
    }
    public String getName()
    {
        return name;
    }
    public void setName( String name )
    {
        this.name = name;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription( String description )
    {
        this.description = description;
    }
    public boolean isTracking()
    {
        return tracking;
    }
    public void setTracking( boolean tracking )
    {
        this.tracking = tracking;
    }
    public CatalogType getCatalogType()
    {
        return catalogType;
    }
    public void setCatalogType( CatalogType catalogType )
    {
        this.catalogType = catalogType;
    }
    /*
    public Set<InventoryTypeAttribute> getInventoryTypeAttributes()
    {
        return inventoryTypeAttributes;
    }
    public void setInventoryTypeAttributes( Set<InventoryTypeAttribute> inventoryTypeAttributes )
    {
        this.inventoryTypeAttributes = inventoryTypeAttributes;
    }
    */
    public List<InventoryTypeAttribute> getInventoryTypeAttributes()
    {
        return inventoryTypeAttributes;
    }
    public void setInventoryTypeAttributes( List<InventoryTypeAttribute> inventoryTypeAttributes )
    {
        this.inventoryTypeAttributes = inventoryTypeAttributes;
    }
    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }
    public void setDataEntryForm( DataEntryForm dataEntryForm )
    {
        this.dataEntryForm = dataEntryForm;
    }
    
    
    
}
