package andrews.pandoras_creatures.container;

import andrews.pandoras_creatures.container.slot.BufflonBackAttachmentSlot;
import andrews.pandoras_creatures.container.slot.BufflonSaddleSlot;
import andrews.pandoras_creatures.container.slot.BufflonStorageSlot;
import andrews.pandoras_creatures.entities.BufflonEntity;
import andrews.pandoras_creatures.registry.PCContainers;
import andrews.pandoras_creatures.registry.PCItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BufflonContainer extends AbstractContainerMenu
{	
    // Inventory of Bufflon Entity
	private final Inventory bufflonStorge;
    //Instance of Bufflon Entity
    private final BufflonEntity bufflonEntity;
	
	public BufflonContainer(final int windowId, final Inventory playerInventory, FriendlyByteBuf data)
	{
		this(windowId, playerInventory, data.readInt());
	}
	
	public BufflonContainer(int windowId, Inventory playerInventory, int entityId)
	{
		super(PCContainers.BUFFLON.get(), windowId);
		
        this.bufflonEntity = (BufflonEntity) playerInventory.player.level.getEntity(entityId);
        this.bufflonStorge = bufflonEntity.bufflonStorage;

        bufflonStorge.startOpen(playerInventory.player);

        //The Bufflon Saddle Slot
        this.addSlot(new BufflonSaddleSlot(bufflonEntity, bufflonStorge, 0, -17, 72));
        //The Bufflon Back Attachment Slot
        this.addSlot(new BufflonBackAttachmentSlot(bufflonEntity, bufflonStorge, 1, 11, 72));
        
        //The Bufflon Inventory
	    for(int y = 0; y < 6; ++y)
	    {
	    	for(int x = 0; x < 9; ++x)
	    	{
	    		this.addSlot(new BufflonStorageSlot(bufflonEntity, bufflonStorge, x + y * 9 + 2, 45 + x * 18, 8 + y * 18 + -18));
	    	}
	    }
        
        //The player inventory offset on the Y axis
        int yOffsetInventory = 130;
        //The Inventory
        for(int y = 0; y < 3; ++y)
        {
        	for(int x = 0; x < 9; ++x)
        	{
        		this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, yOffsetInventory + y * 18 + -18));
        	}
        }
        //The Hotbar
        for(int x = 0; x < 9; ++x)
        {
        	this.addSlot(new Slot(playerInventory, x, 8 + x * 18, yOffsetInventory + 40));
        }
	}
	
	/**
	 * Determines whether supplied player can use this container
	 */
	@Override
	public boolean stillValid(Player playerIn)
	{
		return this.bufflonStorge.stillValid(playerIn) && this.bufflonEntity.isAlive() && this.bufflonEntity.getDistance(playerIn) < 8.0F;
	}
	
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
	 	if(slot != null && slot.hasItem())
	 	{
	 		ItemStack itemstackInSlot = slot.getItem();
	 		itemstack = itemstackInSlot.copy();
	 		
	    	if(index < (6 * 9) + 2)
	    	{
	    		if(!this.moveItemStackTo(itemstackInSlot, (6 * 9) + 2, this.slots.size(), true))
	    		{
	    			return ItemStack.EMPTY;
	    		}
	    	}
	    	else if(!this.moveItemStackTo(itemstackInSlot, 0, getInventorySizeForAttachments(), false))
	    	{
	    		return ItemStack.EMPTY;
	    	}

	    	//Some Slot update code
	    	if(itemstackInSlot.isEmpty())
	    	{
	    		slot.set(ItemStack.EMPTY);
	    	}
	    	else
	    	{
	    		slot.setChanged();
	    	}
	 	}
	 	return itemstack;
	}
	
	/**
	 * @return - The inventory size depending on the type of attachment the Bufflon Entity has
	 */
	private int getInventorySizeForAttachments()
	{
		if(this.slots.get(1).hasItem() == false)
		{
			return 2;
		}
		else
		{
			if(this.slots.get(1).getItem().getItem() == PCItems.BUFFLON_PLAYER_SEATS.get())
			{
				return 2;
			}
			else if(this.slots.get(1).getItem().getItem() == PCItems.BUFFLON_SMALL_STORAGE.get())
			{
				return (3 * 9) + 2;
			}
			else if(this.slots.get(1).getItem().getItem() == PCItems.BUFFLON_LARGE_STORAGE.get())
			{
				return (6 * 9) + 2;
			}
			else
			{
				return 2;
			}
		}
	}
	
	/**
	 * Called when the container is closed.
	 */
	@Override
	public void removed(Player playerIn)
	{
		super.removed(playerIn);
		this.bufflonStorge.stopOpen(playerIn);
	}
	
	/**
	 * @return - The Bufflon Entity this container belongs to
	 */
	public BufflonEntity getBufflonEntity()
    {
        return bufflonEntity;
    }
}
