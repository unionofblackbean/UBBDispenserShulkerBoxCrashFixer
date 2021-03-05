package asia.ubb.ubbdispensershulkerboxcrashfixer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.material.Directional;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class UBBDispenserShulkerBoxFixerPlugin extends JavaPlugin {

    private static String playerMessage;
    private static String consoleMessage;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        final String localeKey = getConfig().getString("Language");
        File langFile = new File(this.getDataFolder(), localeKey + ".yml");
        if (!langFile.exists())
            this.saveResource(localeKey + ".yml", false);

        try {
            FileConfiguration lang = YamlConfiguration.loadConfiguration(langFile);
            playerMessage = ChatColor.translateAlternateColorCodes('&', lang.getString("PlayerMessage"));
            consoleMessage = ChatColor.translateAlternateColorCodes('&', lang.getString("ConsoleMessage"));
        } catch (Throwable e) {
            getLogger().severe(
                    "Failed to load locale " + localeKey + " because file doesn't exist. We will use English as default message language.");
            playerMessage =
                    "A dispenser at the top or bottom of the world and near you wants to dispense a shulker box with items. We have blocked it. Please don't do this again, or you will be banned by the server.";
            consoleMessage =
                    "A dispenser({0}, {1}, {2}, {3}) at the top or bottom of the world wants to dispense a shulker box with items. The player near it is {4}. We have blocked it.";
        }

        this.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onBlockDispense(BlockDispenseEvent e) {
                final Block b = e.getBlock();
                if (b.getType() == Material.DISPENSER) {
                    final BlockFace f = ((Directional) b.getState().getData()).getFacing();

                    final boolean y0FacingDown = b.getY() == 0 &&
                            f == BlockFace.DOWN;
                    final boolean yMaxFacingDown = b.getY() == b.getWorld().getMaxHeight() - 1 &&
                            f == BlockFace.UP;

                    if (y0FacingDown || yMaxFacingDown) {
                        if (e.getItem().getType().name().toLowerCase().endsWith("shulker_box")) {
                            e.setCancelled(true);

                            final Location loc = b.getLocation();
                            final List<String> players = new ArrayList<>();
                            Bukkit.getOnlinePlayers().forEach(player -> {
                                final Location playerLoc = player.getLocation();
                                if (playerLoc.distance(loc) <= 10.0) {
                                    player.sendMessage(playerMessage);
                                    players.add(player.getName());
                                }
                            });

                            getLogger().info(MessageFormat.format(consoleMessage,
                                    loc.getWorld().getName(),
                                    loc.getBlockX(),
                                    loc.getBlockY(),
                                    loc.getBlockZ(),
                                    players));
                        }
                    }
                }
            }
        }, this);
    }
}
