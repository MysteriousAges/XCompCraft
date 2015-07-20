package nil.simpledim.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TeleporterItem extends Item {

	public TeleporterItem() {
		super();
		setUnlocalizedName("simpledim.teleporterItem");
		setCreativeTab(CreativeTabs.tabMaterials);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		/*if (entityPlayer != null && !entityPlayer.isRiding() && entityPlayer instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP)entityPlayer;
			
			int currentDim = world.provider.dimensionId;
			if (currentDim == 0 || currentDim == SimpleDim.modRef.config.dimensionId) {
				int targetDim = 0;
				
				if (currentDim == 0) {
					targetDim = SimpleDim.modRef.config.dimensionId;
				}
				
				SimpleDimTeleporter teleporter = new SimpleDimTeleporter(MinecraftServer.getServer().worldServerForDimension(targetDim));
				player.mcServer.getConfigurationManager().transferPlayerToDimension(player, targetDim, teleporter);
			}
		}*/
		return itemStack;
	}
}
