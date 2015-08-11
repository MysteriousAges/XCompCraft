package nil.simpledim;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class NetworkHandler {
	
	public enum PacketType {
		UNKNOWN,
		TELEPORT_ITEM_INFO;
	}
	
	private static String CHANNEL_NAME = SimpleDim.NAME;
	
	private FMLEventChannel channel;
	
	public static NetworkHandler createNetworkHandler() {
		NetworkHandler handler = new NetworkHandler();
		handler.channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNEL_NAME);
		handler.channel.register(handler);
		return handler;
	}
	
	@SubscribeEvent
	public void onPacket(ServerCustomPacketEvent event) {
		this.parseAndDispatchPacket(event.packet, ((NetHandlerPlayServer)event.handler).playerEntity);
	}
	
	@SubscribeEvent
	public void onPacket(ClientCustomPacketEvent event) {
		this.parseAndDispatchPacket(event.packet, null);
	}
	
	private void parseAndDispatchPacket(FMLProxyPacket packetPayload, EntityPlayerMP player) {
		
	}

	public void sendPlayerTransportItemInfo(EntityPlayerMP player) {
	}
}
