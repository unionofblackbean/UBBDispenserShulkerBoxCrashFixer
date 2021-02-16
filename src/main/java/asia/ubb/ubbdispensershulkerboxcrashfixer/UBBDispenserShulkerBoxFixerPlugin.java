package asia.ubb.ubbdispensershulkerboxcrashfixer;

import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.material.Directional;
import org.bukkit.plugin.java.JavaPlugin;

public class UBBDispenserShulkerBoxFixerPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
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

            if (y0FacingDown || yMaxFacingDown)
                if (e.getItem().getType().name().toLowerCase().endsWith("shulker_box"))
                    e.setCancelled(true);
        }
    }

}
