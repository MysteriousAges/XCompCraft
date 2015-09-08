package nil.simpledim.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import nil.simpledim.SimpleDim;
import nil.simpledim.config.TransportItemInfo;
import nil.simpledim.config.TransportItemType;
import nil.simpledim.util.InvalidTeleporterItemStackException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TeleporterItem extends Item {
	
	public TeleporterItem() {
		super();
		setUnlocalizedName("simpledim.teleporterItem");
		setCreativeTab(CreativeTabs.tabMaterials);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		// TODO: Teleporting.
		return itemStack;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemStack) {
		if (!itemStack.hasTagCompound()) {
			throw new InvalidTeleporterItemStackException("Teleporter item stack does not have NBT information attached.");
		}
		TransportItemInfo itemInfo = TransportItemInfo.getFromItemStack(itemStack);
		return itemInfo.displayName;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		TransportItemType.registerIcons(register);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addInformation(ItemStack itemStack, EntityPlayer player, List tooltips, boolean showAdvancedTooltips) {
		TransportItemInfo itemInfo = TransportItemInfo.getFromItemStack(itemStack);
		tooltips.add("Bound to the dimension " + itemInfo.forDimension);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List itemList) {
		for (ItemStack itemStack : SimpleDim.getConfig().getAllTransportItemStacks()) {
			itemList.add(itemStack);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return TransportItemType.maxLayers;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack itemStack, int pass) {
		TransportItemInfo itemInfo = TransportItemInfo.getFromItemStack(itemStack);
		return itemInfo.getIconForPass(pass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack itemStack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		return getIcon(itemStack, renderPass);
	}

	@Override
	public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
		TransportItemInfo itemInfo = TransportItemInfo.getFromItemStack(itemStack);
		return itemInfo.getColourForLayer(renderPass);
	}
}
