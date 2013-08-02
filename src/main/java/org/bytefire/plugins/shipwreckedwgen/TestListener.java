package org.bytefire.plugins.shipwreckedwgen;

import java.util.HashMap;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bytefire.plugins.shipwreckedwgen.structures.StructureEditorChunkGenerator;

public class TestListener implements Listener {
    private ShipwreckedWGen plugin;
    private World testWorld;
    TestListener(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

//    @EventHandler
//    void testStuff(PlayerLoginEvent event){
//        Player tester = event.getPlayer();
//        if (tester.getName().equalsIgnoreCase("DarkSpear57")){
//        }
//    }
//
//    @EventHandler
//    void testStuff2(PlayerToggleSneakEvent event){
//        Player tester = event.getPlayer();
//        if (tester.getName().equalsIgnoreCase("DarkSpear57") && event.isSneaking()){
//            tester.teleport(testWorld.getSpawnLocation());
//        }
//    }
//
//    @EventHandler(priority = EventPriority.HIGHEST)
//    void testStuff3(PlayerTeleportEvent event){
//        System.out.println(event.getPlayer().getName() + " Teleported");
//        if (event.getTo().getWorld() == testWorld && event.isCancelled()) event.setCancelled(false);
//        //testWorld.
//    }

    HashMap<String, Integer> particleLimit = new HashMap<String, Integer>();

//    @EventHandler
//    void fireStep(PlayerMoveEvent event){
//        Player tester = event.getPlayer();
//        if (particleLimit.containsKey(tester.getName())){
//            int wait = particleLimit.get(tester.getName());
//            if (wait > 0){
//                particleLimit.put(tester.getName(), wait - 1);
//                return;
//            }
//            else particleLimit.put(tester.getName(), 5);
//        }
//        else particleLimit.put(tester.getName(), 5);
//        if (event.getFrom().distance(event.getTo()) > 0.2 && !tester.isFlying()){
//            Location loc = tester.getLocation();
//            loc.setY(loc.getY() - 1);
//            tester.playEffect(loc, Effect.MOBSPAWNER_FLAMES, 0);
//        }
//    }
}
