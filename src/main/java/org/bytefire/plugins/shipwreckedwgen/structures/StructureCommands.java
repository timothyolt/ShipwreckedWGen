package org.bytefire.plugins.shipwreckedwgen.structures;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bytefire.plugins.shipwreckedwgen.ShipwreckedWGen;

public class StructureCommands implements CommandExecutor{

    private ShipwreckedWGen plugin;

    public StructureCommands(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
        if (args[0] == null) return true;
        if ("load".equals(args[0])){
            if (args[1] == null) return true;
            sender.sendMessage("Loading editor world " + args[1] + " ...");
            WorldCreator creator = new WorldCreator(args[1] + ".structure");
            creator.environment(World.Environment.NORMAL);
            creator.generator(new StructureEditorChunkGenerator(plugin, args[1] + ".structure"));
            creator.createWorld();
            sender.sendMessage("Done!");
            return true;
        }
        else if ("tp".equals(args[0])){
            if (args[1] == null) return true;
            World destWorld = Bukkit.getServer().getWorld(args[1] + ".structure");
            if (!plugin.getStructureHandler().isEditor(destWorld.getName())) return true;
            Location dest = plugin.getStructureHandler().getEditor(args[1] + ".structure").getOrigin();
            dest.setWorld(destWorld);
            sender.sendMessage("Teleporting to editor world: " + args[1]);
            ((Player)sender).teleport(dest);
            ((Player)sender).setGameMode(GameMode.CREATIVE);
            return true;
        }
        else if ("save".equals(args[0])){
            if (args[1] == null) return true;
            sender.sendMessage("Saving editor world " + args[1] + " ...");
            Structure struct = plugin.getStructureHandler().getEditor(args[1] + ".structure");
            struct.update();
            sender.sendMessage("Done!");
            return true;
        }
        return true;
    }

}
