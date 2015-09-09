package nil.xcompcraft.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import nil.xcompcraft.SimpleDim;
import nil.xcompcraft.config.DimensionInfo;
import nil.xcompcraft.config.TransportItemInfo;
import nil.xcompcraft.config.TransportItemType;
import nil.xcompcraft.util.EntityDimensionalTransportHandler;
import nil.xcompcraft.util.InvalidTeleporterItemStackException;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TeleporterItem extends Item {
	
	public TeleporterItem() {
		super();
		setUnlocalizedName("simpledim.teleporterItem");
		setCreativeTab(CreativeTabs.tabMaterials);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		TransportItemInfo itemInfo = TransportItemInfo.getFromItemStack(itemStack);
		if (itemInfo != null) {
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && entityPlayer instanceof EntityPlayerMP) {
				EntityPlayerMP playerMP = (EntityPlayerMP)entityPlayer;
				DimensionInfo dimInfo = SimpleDim.getConfig().getDimensionInfoForWorld(itemInfo.forDimension);
				WorldServer targetWorld;
				if (world.provider.dimensionId != dimInfo.dimensionId) {
					targetWorld = dimInfo.getWorldServer();
				}
				else {
					targetWorld = DimensionManager.getWorld(0);
				}
				ChunkCoordinates spawnCoords = playerMP.getBedLocation(targetWorld.provider.dimensionId);
				if (spawnCoords == null) {
					spawnCoords = targetWorld.getSpawnPoint();
				}
				spawnCoords.posX += 0.5;
				spawnCoords.posZ += 0.5;
				EntityDimensionalTransportHandler.teleportPlayerToCoordsSafe(playerMP, targetWorld, spawnCoords);
			}
		}
		return itemStack;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		entityPlayer.setItemInUse(itemStack, getMaxItemUseDuration(itemStack));
		return itemStack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack) {
		TransportItemInfo info = TransportItemInfo.getFromItemStack(itemStack);
		return info.useTime;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.bow;
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
