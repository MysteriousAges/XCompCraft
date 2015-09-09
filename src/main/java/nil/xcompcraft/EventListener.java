package nil.xcompcraft;

import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class EventListener {

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			if (event.player instanceof EntityPlayerMP) {
				SimpleDim.modRef.networkHandler.sendPlayerTransportItemInfo((EntityPlayerMP)event.player);
			}
		}
	}
}
