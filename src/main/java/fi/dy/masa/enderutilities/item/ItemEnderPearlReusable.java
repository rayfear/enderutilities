package fi.dy.masa.enderutilities.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.enderutilities.entity.EntityEnderPearlReusable;
import fi.dy.masa.enderutilities.item.base.ItemEnderUtilities;
import fi.dy.masa.enderutilities.reference.ReferenceNames;
import fi.dy.masa.enderutilities.setup.Configs;
import fi.dy.masa.enderutilities.util.EntityUtils;

public class ItemEnderPearlReusable extends ItemEnderUtilities
{
    @SideOnly(Side.CLIENT)
    private IIcon eliteIcon;

    public ItemEnderPearlReusable()
    {
        this.setMaxStackSize(4);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setUnlocalizedName(ReferenceNames.NAME_ITEM_ENDER_PEARL_REUSABLE);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        // damage 1: Elite Ender Pearl
        if (stack.getItemDamage() == 1)
        {
            return super.getUnlocalizedName() + ".elite";
        }

        return super.getUnlocalizedName();
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote == true)
        {
            return stack;
        }

        // Damage 1: "Elite version" of the pearl, makes the thrower fly with it. Idea by xisumavoid in episode Hermitcraft III 303 :)

        EntityEnderPearlReusable pearl = new EntityEnderPearlReusable(world, player, stack.getItemDamage() == 1);

        if (stack.getItemDamage() == 1)
        {
            Entity bottomEntity = EntityUtils.getBottomEntity(player);

            // Dismount the previous pearl if we are already riding one
            if (bottomEntity instanceof EntityEnderPearlReusable && bottomEntity.riddenByEntity != null)
            {
                bottomEntity.riddenByEntity.mountEntity(null);
            }

            EntityUtils.getBottomEntity(player).mountEntity(pearl);
        }

        --stack.stackSize;
        world.spawnEntityInWorld(pearl);
        world.playSoundAtEntity(player, "random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));

        return stack;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs creativeTab, List list)
    {
        if (Configs.disableItemEnderPearl.getBoolean(false) == false)
        {
            list.add(new ItemStack(this, 1, 0)); // Regular
            list.add(new ItemStack(this, 1, 1)); // Elite
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon(this.getIconString());
        this.eliteIcon = iconRegister.registerIcon(this.getIconString() + ".elite");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage)
    {
        if (damage == 1)
        {
            return this.eliteIcon;
        }

        return this.itemIcon;
    }
}
