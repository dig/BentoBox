package us.tastybento.bskyblock.commands.admin;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

import us.tastybento.bskyblock.Constants;
import us.tastybento.bskyblock.api.commands.CompositeCommand;
import us.tastybento.bskyblock.api.commands.User;
import us.tastybento.bskyblock.util.SafeSpotTeleport;

public class AdminTeleportCommand extends CompositeCommand {

    public AdminTeleportCommand(CompositeCommand parent) {
        super(parent, "tp", "tpnether", "tpend");
    }

    @Override
    public void setup() {
        this.setPermission(Constants.PERMPREFIX + "admin.tp");
        this.setOnlyPlayer(true);
        this.setDescription("commands.admin.tp.description");
    }

    @Override
    public boolean execute(User user, List<String> args) {
        if (args.isEmpty()) {
            user.sendMessage("commands.admin.tp.help");
            return true;
        }
        
        // Convert name to a UUID
        final UUID targetUUID = getPlayers().getUUID(args.get(0));
        if (targetUUID == null) {
            user.sendMessage("errors.unknown-player");
            return true;
        } else {
            if (getPlayers().hasIsland(targetUUID) || getPlayers().inTeam(targetUUID)) {
                Location warpSpot = getIslands().getIslandLocation(targetUUID).toVector().toLocation(getPlugin().getIslandWorldManager().getIslandWorld());
                if (this.getLabel().equals("tpnether")) {
                    warpSpot = getIslands().getIslandLocation(targetUUID).toVector().toLocation(getPlugin().getIslandWorldManager().getNetherWorld()); 
                } else if (this.getLabel().equals("tpend")) {
                    warpSpot = getIslands().getIslandLocation(targetUUID).toVector().toLocation(getPlugin().getIslandWorldManager().getEndWorld()); 
                }
                // Other wise, go to a safe spot
                String failureMessage = user.getTranslation("commands.admin.tp.manual", "[location]", warpSpot.getBlockX() + " " + warpSpot.getBlockY() + " "
                        + warpSpot.getBlockZ());
                new SafeSpotTeleport(getPlugin(), user.getPlayer(), warpSpot, failureMessage);
                return true;
            }
            user.sendMessage("command.admin.tp.no-island");
            return true;
        }
    }

}
