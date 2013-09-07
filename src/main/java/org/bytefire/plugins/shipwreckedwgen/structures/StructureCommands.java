package org.bytefire.plugins.shipwreckedwgen.structures;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bytefire.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bytefire.plugins.shipwreckedwgen.structures.Structure.StructureType;

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
        else if (args[0].equals("set")) return cmdSet(sender, args);
        else if (args[0].equals("passive")) return cmdPassive(sender, args);
        else error(sender, "Not a valid operation");
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
        Structure struct = getStructureFromSender(sender, args, 1);
        if (struct != null){
            message(sender, "Saving structure " + struct.getName() + " ...");
            struct.save();
            message(sender, "Done!");
            return true;
        }
        else return true;
    }

    public boolean cmdSet(CommandSender sender, String[] args){
        String[] newargs = Arrays.copyOfRange(args, 1, args.length);
        if (args.length < 2) error(sender, "No operation specified");
        if      (args[1].equals("origin")) return cmdSetOrigin(sender, newargs);
        else if (args[1].equals("distance")) return cmdSetDistance(sender, newargs);
        else if (args[1].equals("chance")) return cmdSetChance(sender, newargs);
        else if (args[1].equals("biome"))  return cmdSetBiome(sender, newargs);
        else if (args[1].equals("type"))   return cmdSetType(sender, newargs);
        else if (args[1].equals("max"))    return cmdSetYMax(sender, newargs);
        else if (args[1].equals("min"))    return cmdSetYMin(sender, newargs);
        else if (args[1].equals("grow"))   return cmdSetGrow(sender, newargs);
        else error(sender, "Not a valid operation");
        return true;
    }

    public boolean cmdSetOrigin(CommandSender sender, String[] args){
        Structure struct = getStructureFromSender(sender, args, 1);
        Player player = getPlayerFromSender(sender, args, 2);
        if (struct == null || player == null) return true;
        Location origin = player.getLocation().subtract(0, 1, 0);
        struct.setOrigin(origin);
        message(sender, "Set origin of " + struct.getName() +
            " to {x=" + Integer.toString(origin.getBlockX()) +
            ", y=" + Integer.toString(origin.getBlockY()) +
            ", z=" + Integer.toString(origin.getBlockZ()) + "}"
        );
        return true;
    }

    public boolean cmdSetDistance(CommandSender sender, String[] args){
        if (args.length < 2){
            error(sender, "No minimum distance specified");
            return true;
        }
        int dist = Integer.valueOf(args[1]);
        Structure struct = getStructureFromSender(sender, args, 2);
        if (struct == null) return true;
        struct.setMaxHeight(dist);
        message(sender, "Minimum distance set to " + Integer.toString(dist));
        return true;
    }

    public boolean cmdSetChance(CommandSender sender, String[] args){
        if (args.length < 2){
            error(sender, "No spawn chance specified");
            return true;
        }
        int chance = Integer.valueOf(args[1]);
        Structure struct = getStructureFromSender(sender, args, 2);
        if (struct == null) return true;
        struct.setMaxHeight(chance);
        message(sender, "Spawn chance set to " + Integer.toString(chance));
        return true;
    }

    public boolean cmdSetBiome(CommandSender sender, String[] args){
        if (args.length < 2){
            error(sender, "No biome specified");
            return true;
        }
        Biome biome;
        if      (args[1].toUpperCase().equals("SNOW"))
            biome = Biome.TAIGA;
        else if (args[1].toUpperCase().equals("END"))
            biome = Biome.SKY;
        else biome = Biome.valueOf(args[1].toUpperCase());
        if (biome == null){
            error(sender, "Not a valid biome");
            return true;
        }
        if (args[1].toUpperCase().equals("NULL"))
            biome = null;
        else if (args[1].toUpperCase().equals("NONE"))
            biome = null;
        Structure struct = getStructureFromSender(sender, args, 2);
        if (struct == null) return true;
        struct.setRequiredBiome(biome);
        if (biome == null) message(sender, "Required biome set to none");
        else message(sender, "Required biome set to " + biome.toString().toLowerCase());
        return true;
    }

    public boolean cmdSetType(CommandSender sender, String[] args){
        if (args.length < 2){
            error(sender, "No type specified");
            return true;
        }
        StructureType type;
        if      (args[1].toUpperCase().equals("SKY"))
            type = StructureType.AIR;
        else type = StructureType.valueOf(args[1].toUpperCase());
        if (type == null){
            error(sender, "Not a valid type");
            return true;
        }
        Structure struct = getStructureFromSender(sender, args, 2);
        if (struct == null) return true;
        struct.setType(type);
        message(sender, "Structure type set to " + type.toString().toLowerCase());
        return true;
    }

    public boolean cmdSetYMax(CommandSender sender, String[] args){
        if (args.length < 2){
            error(sender, "No maximum height specified");
            return true;
        }
        int yMax = Integer.valueOf(args[1]);
        Structure struct = getStructureFromSender(sender, args, 2);
        if (struct == null) return true;
        struct.setMaxHeight(yMax);
        message(sender, "Maximum height set to " + Integer.toString(yMax));
        return true;
    }

    public boolean cmdSetYMin(CommandSender sender, String[] args){
        if (args.length < 2){
            error(sender, "No minimum height specified");
            return true;
        }
        int yMin = Integer.valueOf(args[1]);
        Structure struct = getStructureFromSender(sender, args, 2);
        if (struct == null) return true;
        struct.setMaxHeight(yMin);
        message(sender, "Maximum height set to " + Integer.toString(yMin));
        return true;
    }

    public boolean cmdSetGrow(CommandSender sender, String[] args){
        if (args.length < 2){
            error(sender, "No grow specified");
            return true;
        }
        boolean grow = Boolean.valueOf(args[1]);
        Structure struct = getStructureFromSender(sender, args, 2);
        if (struct == null) return true;
        struct.setGrowFromBounds(grow);
        message(sender, "Bound growth set to " + Boolean.toString(grow));
        return true;
    }
    
    public boolean cmdPassive(CommandSender sender, String[] args){
        String[] newargs = Arrays.copyOfRange(args, 1, args.length);
        if (args.length < 2) error(sender, "No operation specified");
        if      (args[1].equals("material")) return cmdPassiveMaterial(sender, newargs);
        else if (args[1].equals("distance")) return cmdSetDistance(sender, newargs);
        else error(sender, "Not a valid operation");
        return true;
    }
    
    public boolean cmdPassiveMaterial(final CommandSender sender, String[] args){
        if (args.length < 2){
            error(sender, "No material specified");
            return true;
        }
        if (args.length < 3){
            error(sender, "No passive flag specified");
            return true;
        }
        final Material passive = Material.getMaterial(args[1].toUpperCase());
        final boolean flag = Boolean.valueOf(args[2]);
        if (passive == null){
            error(sender, "Invalid material name");
            return true;
        }
        final Structure struct = getStructureFromSender(sender, args, 3);
        if (struct == null) return true;
        struct.update();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
            public void run() {
                int id = passive.getId();
                message(sender, "Making all instances of " + passive.toString().toLowerCase() + (flag ? " passive" : " assertive"));
                Collection<StructureChunk> chunks = struct.getAllChunks().values();
                for (StructureChunk chunk : chunks){
                    Collection<StructureSection> sects = chunk.getAllSections().values();
                    for (StructureSection sect : sects){
                        byte[] materials = sect.getBlocks();
                        for (int i = 0; i < materials.length; i++)
                            if (materials[i] == id) sect.setBlockPassive(i, flag);
                    }
                }
                message(sender, "All instances of " + passive.toString().toLowerCase() + " are now"  + (flag ? " passive" : " assertive"));
            }
        });
        return true;
    }

    public Structure getStructureFromSender(CommandSender sender, String[] args, int index){
        Structure struct;
        if (args.length < index + 1){
            if (sender instanceof Player) {
                String world = ((Player) sender).getWorld().getName();
                if (plugin.getStructureHandler().isEditor(world))
                    struct = plugin.getStructureHandler().getEditor(world);
                else {
                    error(sender, "No structure specified");
                    return null;
                }
            }
            else {
                error(sender, "No structure specified");
                return null;
            }
        }
        else struct = plugin.getStructureHandler().getEditor(args[index] + ".structure");
        if (struct == null) error(sender, "Not a valid structure");
        return struct;
    }

    public Player getPlayerFromSender(CommandSender sender, String[] args, int index){
        if (args.length < index + 1){
            if (sender instanceof Player) return ((Player) sender);
            else {
                error(sender, "No player specified");
                return null;
            }
        }
        else {
            List<Player> players = plugin.getServer().matchPlayer(args[index]);
            if (players.isEmpty()){
                error(sender, "Not a valid player");
                return null;
            }
            return players.get(0);
        }
    }

    public void message(CommandSender sender, String message){
        sender.sendMessage(ChatColor.AQUA + "[Struct] " + ChatColor.WHITE + message);
    }

    public void error(CommandSender sender, String message){
        sender.sendMessage(ChatColor.AQUA + "[Struct] " + ChatColor.RED + message);
    }

}
