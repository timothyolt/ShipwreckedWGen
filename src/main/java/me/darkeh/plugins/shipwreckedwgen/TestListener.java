package me.darkeh.plugins.shipwreckedwgen;

import java.util.Random;
import me.darkeh.plugins.shipwreckedwgen.biomes.trees.DesertTree;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;


public class TestListener implements Listener {
    private ShipwreckedWGen plugin;
    TestListener(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    @EventHandler
    void testStuff(PlayerToggleSneakEvent event){
        Player tester = event.getPlayer();
        DesertTree tree = new DesertTree(new Random(), tester.getLocation());
        tree.generate();
    }
}
