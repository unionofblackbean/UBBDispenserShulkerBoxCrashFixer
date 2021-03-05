package asia.ubb.ubbdispensershulkerboxcrashfixer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.material.Directional;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.MessageFormat;

public class UBBDispenserShulkerBoxFixerPlugin extends JavaPlugin implements Listener {

    private final Configuration config = this.getConfig();
    private final boolean consoleLogMessages = this.config.getBoolean("log.console");
    private final String defaultConsoleMessage = "A dispenser at ({0}, {1}, {2}) in world {3} tries to dispense a shulker box with items out of the would. Dispensation was cancelled.";
    private String consoleMessage = null;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        String langCode = this.config.getString("lang").trim();
        String langFileName = "msg_" + langCode + ".yml";

        File langFile = new File(this.getDataFolder(), langFileName);
        if (!langFile.exists())
            this.saveResource(langFileName, false);

        FileConfiguration langMessagesConfig = YamlConfiguration.loadConfiguration(langFile);
        this.consoleMessage = ChatColor.translateAlternateColorCodes('&',
                langMessagesConfig.getString(
                        "message.console",
                        this.defaultConsoleMessage));

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent e) {
        Block b = e.getBlock();
        if (b.getType() == Material.DISPENSER) {
            BlockFace f = ((Directional) b.getState().getData()).getFacing();

            boolean y0FacingDown = b.getY() == 0 &&
                    f == BlockFace.DOWN;
            boolean yMaxFacingDown = b.getY() == b.getWorld().getMaxHeight() - 1 &&
                    f == BlockFace.UP;

            if (y0FacingDown || yMaxFacingDown) {
                if (e.getItem().getType().name().toLowerCase().endsWith("shulker_box")) {
                    e.setCancelled(true);

                    if (consoleLogMessages)
                        this.getLogger().info(
                                MessageFormat.format(
                                        consoleMessage == null ? this.defaultConsoleMessage : this.consoleMessage,
                                        b.getX(),
                                        b.getY(),
                                        b.getZ(),
                                        b.getWorld().getName()));
                }
            }
        }
    }
}
