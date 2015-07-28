package nil.simpledim.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import nil.simpledim.SimpleDim;
import nil.simpledim.util.EntityDimensionalTransportHandler;

public class EnhancedTeleportCommand extends CommandBase {

	private static final String COMMAND_NAME = "etp";
	private static final String COMMANDS_ETP_USAGE = "commands." + COMMAND_NAME + ".usage";

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return COMMANDS_ETP_USAGE;
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args) {
		if (args.length == 1) {
			if (isPlayerOnline(args[0])) {
				EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
				EntityPlayerMP target = getPlayerByUsername(args[0]);
				if (player != null && target != null) {
					teleportPlayerToTarget(player, target);
				}
				else {
					throw new PlayerNotFoundException();
				}
			}
		}
		else if (args.length == 2) {
			if (isPlayerOnline(args[0]) && isPlayerOnline(args[1])) {
				EntityPlayerMP player = getPlayerByUsername(args[0]);
				EntityPlayerMP target = getPlayerByUsername(args[1]);
				if (player != null && target != null) {
					teleportPlayerToTarget(player, target);
				}
				else {
					throw new PlayerNotFoundException();
				}
			}
		}
		else if (args.length == 3) {
			EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
			try {
				double x = Double.parseDouble(args[0]);
				double y = Double.parseDouble(args[1]);
				double z = Double.parseDouble(args[2]);
				if (player != null && 0 <= y) {
					teleportPlayerToTarget(player, x, y, z);
				}
				else {
					throw new PlayerNotFoundException();
				}
			}
			catch (NumberFormatException e) {
				throw new WrongUsageException(COMMANDS_ETP_USAGE);
			}
		}
		else if (args.length == 4) {
			if (args[0].matches("-?[0-9]*\\.?[0-9]+")) {
				EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
				try {
					double x = Double.parseDouble(args[0]);
					double y = Double.parseDouble(args[1]);
					double z = Double.parseDouble(args[2]);
					WorldServer targetWorld = tryGetWorldServerForDimension(args[3]);
					
					if (targetWorld == null) {
						throw new WrongUsageException(COMMANDS_ETP_USAGE);
					}
					
					if (player != null && 0 <= y) {
						teleportPlayerToTarget(player, targetWorld, x, y, z);
					}
					else {
						throw new PlayerNotFoundException();
					}
				}
				catch (NumberFormatException e) {
					throw new WrongUsageException(COMMANDS_ETP_USAGE);
				}
			}
			else {
				EntityPlayerMP player = getPlayerByUsername(args[0]);
				try {
					double x = Double.parseDouble(args[1]);
					double y = Double.parseDouble(args[2]);
					double z = Double.parseDouble(args[3]);
					if (player != null && 0 <= y) {
						teleportPlayerToTarget(player, x, y, z);
					}
					else {
						throw new PlayerNotFoundException();
					}
				}
				catch (NumberFormatException e) {
					throw new WrongUsageException(COMMANDS_ETP_USAGE);
				}
			}
		}
		else if (args.length == 5) {
			EntityPlayerMP player = getPlayerByUsername(args[0]);
			try {
				double x = Double.parseDouble(args[1]);
				double y = Double.parseDouble(args[2]);
				double z = Double.parseDouble(args[3]);
				WorldServer targetWorld = tryGetWorldServerForDimension(args[4]);
				
				if (targetWorld == null) {
					throw new WrongUsageException(COMMANDS_ETP_USAGE);
				}
				
				if (player != null && 0 <= y) {
					teleportPlayerToTarget(player, targetWorld, x, y, z);
				}
				else {
					throw new PlayerNotFoundException();
				}
			}
			catch (NumberFormatException e) {
				throw new WrongUsageException(COMMANDS_ETP_USAGE);
			}
		}
	}

	private WorldServer tryGetWorldServerForDimension(String dimensionId) {
		WorldServer targetWorld;
		if (dimensionId.matches("[A-z]+")) {
			int dimId = SimpleDim.getConfig().getDimensionInfoForWorld(dimensionId).dimensionId;
			targetWorld = MinecraftServer.getServer().worldServerForDimension(dimId);
		}
		else {
			int dimId = Integer.parseInt(dimensionId);
			targetWorld = MinecraftServer.getServer().worldServerForDimension(dimId);
		}
		return targetWorld;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List addTabCompletionOptions(ICommandSender commandSender, String[] args) {
		// Match a player name in the 1st or 2nd slot
		if (1 <= args.length && args.length <= 2) {
			return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		}
		else if (4 <= args.length && args.length <= 5) {
			List<String> dimNameList = SimpleDim.getConfig().getAllDimensionNames();
			return getListOfStringsMatchingLastWord(args, dimNameList.toArray(new String[dimNameList.size()]));
		}
		else {
			return null;
		}
	}
	
	private static void teleportPlayerToTarget(EntityPlayerMP player, EntityPlayerMP target) {
		if (player.worldObj.provider.dimensionId == target.worldObj.provider.dimensionId) {
			teleportPlayerToTarget(player, target.posX, target.posY, target.posZ);
		}
		else {
			teleportPlayerToTarget(player, DimensionManager.getWorld(target.worldObj.provider.dimensionId),
					target.posX, target.posY, target.posX);
		}
	}
	
	private static void teleportPlayerToTarget(EntityPlayerMP player, double x, double y, double z) {
		player.setPosition(x, y, z);
	}
	
	private static void teleportPlayerToTarget(EntityPlayerMP player, WorldServer world, double x, double y, double z) {
		EntityDimensionalTransportHandler.teleportPlayerToCoords(player, world, x, y, z);
	}
	
	private static boolean isPlayerOnline(String playerName) {
		for (String pName : MinecraftServer.getServer().getAllUsernames()) {
			if (playerName.equalsIgnoreCase(pName)) {
				return true;
			}
		}
		return false;
	}
	
	private static EntityPlayerMP getPlayerByUsername(String playerName) {
		return MinecraftServer.getServer().getConfigurationManager().func_152612_a(playerName);
	}

}
