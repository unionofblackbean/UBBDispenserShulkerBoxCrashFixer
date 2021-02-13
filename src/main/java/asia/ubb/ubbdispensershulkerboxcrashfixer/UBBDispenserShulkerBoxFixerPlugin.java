package asia.ubb.ubbdispensershulkerboxcrashfixer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class UBBDispenserShulkerBoxFixerPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent e) {
        if (e.getBlock().getY() == 0 || e.getBlock().getY() == e.getBlock().getWorld().getMaxHeight()) {
            if (e.getItem().getType().name().endsWith("SHULKER_BOX")) {
                e.setCancelled(true);
            }
        }
    }

}
