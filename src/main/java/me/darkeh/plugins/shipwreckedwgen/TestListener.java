package me.darkeh.plugins.shipwreckedwgen;

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
        tester.sendMessage(Long.toString(System.nanoTime()));
        tester.sendMessage(Long.toString(System.nanoTime()));
        for(int i = 0;i < 100; i++) tester.getWorld().loadChunk(100, 100, false);
        tester.sendMessage(Long.toString(System.nanoTime()));
        tester.sendMessage(Long.toString(System.nanoTime()));
    }
}
