package org.bytefire.plugins.shipwreckedwgen;

import java.util.HashMap;
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
//        if (tester.getName().equalsIgnoreCase("DarkSpear57") && event.isSneaking()){
//            //JungleBush tree = new JungleBush(new Random(), tester.getLocation());
//            //tree.generate();
//            //tester.sendMessage(Byte.tester.getLocation().getBlock().getRelative(0, -1, 0).getData());
//        }
    }

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