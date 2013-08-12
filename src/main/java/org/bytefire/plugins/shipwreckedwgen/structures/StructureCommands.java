package org.bytefire.plugins.shipwreckedwgen.structures;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        if (args.length < 1) error(sender, "No operation specified");
        if      (args[0].equals("load")) return cmdLoad(sender, args);
        else if (args[0].equals("tp"))   return cmdTp(sender, args);
        else if (args[0].equals("save")) return cmdSave(sender, args);
        return true;
    }

    public boolean cmdLoad(CommandSender sender, String[] args){
        if (args.length < 2) {
            error(sender, "No structure specified");
            return true;
        }
        if (Bukkit.getServer().getWorld(args[1] + ".structure") != null){
            error(sender, "Structure already loaded");
            return true;
        }
        try {
            message(sender, "Loading editor world " + args[1] + " ...");
            WorldCreator creator = new WorldCreator(args[1] + ".structure");
            creator.environment(World.Environment.NORMAL);
            creator.generator(new StructureEditorChunkGenerator(plugin, args[1] + ".structure"));
            creator.createWorld();

            message(sender, "Done!");
            return true;
        } catch (Exception e){
            error(sender, "An internal error occurred");
            return true;
        }
    }

    public boolean cmdTp(CommandSender sender, String[] args){
        if (!(sender instanceof Player)){
            error(sender, "You are not a player");
            return true;
        }
        if (args.length < 2) {
            error(sender, "No structure specified");
            return true;
        }
        World destWorld = Bukkit.getServer().getWorld(args[1] + ".structure");
        if (destWorld == null){
            cmdLoad(sender, args);
            destWorld = Bukkit.getServer().getWorld(args[1] + ".structure");
            if (destWorld == null){
                error(sender, "Structure " + args[1] + " could not be loaded");
                return true;
            }
        }
        if (!plugin.getStructureHandler().isEditor(destWorld.getName())) {
            error(sender, args[1] + " does not refer to a structure");
            return true;
        }
        Structure location = plugin.getStructureHandler().getEditor(args[1] + ".structure");
        Location dest = location.getOrigin();
        dest.setWorld(destWorld);
        message(sender, "Teleporting to editor world " + args[1]);
        ((Player)sender).teleport(dest);
        ((Player)sender).setGameMode(GameMode.CREATIVE);

        return true;
    }

    public boolean cmdSave(CommandSender sender, String[] args){
        Structure struct;
        if (args.length < 2){
            if (sender instanceof Player) {
                String world = ((Player) sender).getWorld().getName();
                if (plugin.getStructureHandler().isEditor(world))
                    struct = plugin.getStructureHandler().getEditor(world);
                else {
                    error(sender, "No structure specified");
                    return true;
                }
            }
            else {
                error(sender, "No structure specified");
                return true;
            }
        }
        else struct = plugin.getStructureHandler().getEditor(args[1] + ".structure");
        if (struct != null){
            message(sender, "Saving structure " + struct.getName() + " ...");
            struct.update();
            message(sender, "Done!");
            return true;
        }
        else {
            error(sender, "Not a valid structure");
            return true;
        }
    }

    public void message(CommandSender sender, String message){
        sender.sendMessage(ChatColor.AQUA + "[Struct] " + ChatColor.WHITE + message);
    }

    public void error(CommandSender sender, String message){
        sender.sendMessage(ChatColor.AQUA + "[Struct] " + ChatColor.RED + message);
    }

}
