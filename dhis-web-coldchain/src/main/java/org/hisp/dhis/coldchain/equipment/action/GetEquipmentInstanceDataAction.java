package org.hisp.dhis.coldchain.equipment.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.catalog.Catalog;
import org.hisp.dhis.coldchain.catalog.CatalogService;
import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.inventory.Equipment;
import org.hisp.dhis.coldchain.inventory.EquipmentService;
import org.hisp.dhis.coldchain.inventory.EquipmentInstance;
import org.hisp.dhis.coldchain.inventory.EquipmentInstanceService;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;

import com.opensymphony.xwork2.Action;

public class GetEquipmentInstanceDataAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentInstanceService equipmentInstanceService;

    public void setEquipmentInstanceService( EquipmentInstanceService equipmentInstanceService )
    {
        this.equipmentInstanceService = equipmentInstanceService;
    }
    
    private EquipmentService equipmentService;
    
    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }

    
    private CatalogService catalogService;
    
    public void setCatalogService( CatalogService catalogService )
    {
        this.catalogService = catalogService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer equipmentInstanceId;
    
    public void setEquipmentInstanceId( Integer equipmentInstanceId )
    {
        this.equipmentInstanceId = equipmentInstanceId;
    }

    private EquipmentInstance equipmentInstance;

    public EquipmentInstance getEquipmentInstance()
    {
        return equipmentInstance;
    }

    private List<InventoryTypeAttribute> inventoryTypeAttributes;
    
    public List<InventoryTypeAttribute> getInventoryTypeAttributes()
    {
        return inventoryTypeAttributes;
    }

    private Map<Integer, String> equipmentValueMap;
    
    public Map<Integer, String> getEquipmentValueMap()
    {
        return equipmentValueMap;
    }

    private List<Catalog> catalogs;
    
    public List<Catalog> getCatalogs()
    {
        return catalogs;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        equipmentInstance = equipmentInstanceService.getEquipmentInstance( equipmentInstanceId );
        
        inventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>( equipmentInstance.getInventoryType().getInventoryTypeAttributes() );
        
        equipmentValueMap = new HashMap<Integer, String>();
        
        List<Equipment> equipmentDetailsList = new ArrayList<Equipment>( equipmentService.getEquipments( equipmentInstance ) );
        
        for( Equipment equipmentDetails : equipmentDetailsList )
        {
            if ( InventoryTypeAttribute.TYPE_COMBO.equalsIgnoreCase( equipmentDetails.getInventoryTypeAttribute().getValueType() ) )
            {
                equipmentValueMap.put( equipmentDetails.getInventoryTypeAttribute().getId(), equipmentDetails.getInventoryTypeAttributeOption().getName() );
            }
            else
            {
                equipmentValueMap.put( equipmentDetails.getInventoryTypeAttribute().getId(), equipmentDetails.getValue() );
            }
        }
        
        CatalogType catalogType = equipmentInstance.getInventoryType().getCatalogType();
        
        if( catalogType != null )
        {
            catalogs = new ArrayList<Catalog>( catalogService.getCatalogs( catalogType ) );
        }

        return SUCCESS;
    }
}
