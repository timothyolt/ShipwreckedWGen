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

    public static final String structSyntax = "/struct <command>";
        public static final String structLoadSyntax = "/struct load <structure>";
        public static final String structTpSyntax = "/struct tp <structure>";
        public static final String structSaveSyntax = "/struct save [structure]";
        public static final String structSetSyntax = "/struct set <attribute>";
            public static final String structSetOriginSyntax = "/struct set origin [structure]";
            public static final String structSetDistanceSyntax = "/struct set distance <distance> [structure]";
            public static final String structSetChanceSyntax = "/struct set chance <chance> [structure]";
            public static final String structSetBiomeSyntax = "/struct set biome <biome> [structure]";
            public static final String structSetTypeSyntax = "/struct set type <type> [structure]";
            public static final String structSetMaxSyntax = "/struct set max <max> [structure]";
            public static final String structSetMinSyntax = "/struct set min <min> [structure]";
            public static final String structSetGrowSyntax = "/struct set grow <grow> [structure]";
        public static final String structPassiveSyntax = "/struct passive <operation>";
             public static final String structPassiveMaterialSyntax = "/struct passive material <material> <passive> [structure]";
        public static final String structHelpSyntax = "/struct help [command]";

    private ShipwreckedWGen plugin;

    public StructureCommands(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
        return cmdStruct(sender, args, false);
    }

    public boolean cmdStruct(CommandSender sender, String[] args, boolean help){
        if (sender.isOp() != true) {
            error(sender, "You do not have permission for that!");
            return true;
        }

        if (help && args.length < 1){
            message(sender, "Syntax: " + structSyntax);
            message(sender, "Sub-Commands:");
            message(sender, structLoadSyntax);
            message(sender, structTpSyntax);
            message(sender, structSaveSyntax);
            message(sender, structSetSyntax);
            message(sender, structPassiveSyntax);
            message(sender, structHelpSyntax);
            message(sender, "The base of every command with anything to do with structures.");
            return true;
        }

        if (args.length < 1) error(sender, "No operation specified");
        if      (args[0].equals("load")) return cmdLoad(sender, args, help);
        else if (args[0].equals("tp"))   return cmdTp(sender, args, help);
        else if (args[0].equals("save")) return cmdSave(sender, args, help);
        else if (args[0].equals("set")) return cmdSet(sender, args, help);
        else if (args[0].equals("passive")) return cmdPassive(sender, args, help);
        else if (args[0].equals("help")) return cmdHelp(sender, args, help);
        else error(sender, "Not a valid operation");
        return true;
    }

    public boolean cmdLoad(CommandSender sender, String[] args, boolean help){
        if (help){
            message(sender, "Syntax: " + structLoadSyntax);
            message(sender, "Loads a structure file from the plugin directory with the given name");
            message(sender, "Note: The '.structure' at the end of the file should NOT be included");
            message(sender, "Ex: /struct load desert1");
            return true;
        }

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

    public boolean cmdTp(CommandSender sender, String[] args, boolean help){
        if (help){
            message(sender, "Syntax: " + structTpSyntax);
            message(sender, "Teleports the player to the specified structure,");
            message(sender, "loading it if necessary");
            message(sender, "Ex: /struct tp desert1");
            return true;
        }

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
            cmdLoad(sender, args, help);
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

    public boolean cmdSave(CommandSender sender, String[] args, boolean help){
        if (help){
            message(sender, "Syntax: " + structSaveSyntax);
            message(sender, "Saves the structure specified, or");
            message(sender, "if none specified, the one the player is in");
            message(sender, "Ex: /struct save desert1");
            return true;
        }

        Structure struct = getStructureFromSender(sender, args, 1);
        if (struct != null){
            message(sender, "Saving structure " + struct.getName() + " ...");
            struct.save();
            message(sender, "Done!");
            return true;
        }
        else return true;
    }

    public boolean cmdSet(CommandSender sender, String[] args, boolean help){
        if (help && args.length < 2){
            message(sender, "Syntax: " + structSetSyntax);
            message(sender, "Sub-Commands:");
            message(sender, structSetOriginSyntax);
            message(sender, structSetDistanceSyntax);
            message(sender, structSetChanceSyntax);
            message(sender, structSetBiomeSyntax);
            message(sender, structSetTypeSyntax);
            message(sender, structSetMaxSyntax);
            message(sender, structSetMinSyntax);
            message(sender, structSetGrowSyntax);
            message(sender, "Sets attributes of the structure specified, or");
            message(sender, "if none specified, the one the player is in");
            return true;
        }

        String[] newargs = Arrays.copyOfRange(args, 1, args.length);
        if (args.length < 2) error(sender, "No operation specified");
        if      (args[1].equals("origin")) return cmdSetOrigin(sender, newargs, help);
        else if (args[1].equals("distance")) return cmdSetDistance(sender, newargs, help);
        else if (args[1].equals("chance")) return cmdSetChance(sender, newargs, help);
        else if (args[1].equals("biome"))  return cmdSetBiome(sender, newargs, help);
        else if (args[1].equals("type"))   return cmdSetType(sender, newargs, help);
        else if (args[1].equals("max"))    return cmdSetYMax(sender, newargs, help);
        else if (args[1].equals("min"))    return cmdSetYMin(sender, newargs, help);
        else if (args[1].equals("grow"))   return cmdSetGrow(sender, newargs, help);
        else error(sender, "Not a valid operation");
        return true;
    }

    public boolean cmdSetOrigin(CommandSender sender, String[] args, boolean help){
        if (help){
            message(sender, "Syntax: " + structSetOriginSyntax);
            message(sender, "Sets the origin of the structure specified to the player's");
            message(sender, "coordinates, or if none specified, the one the player is in");
            message(sender, "Ex: /struct set origin");
            return true;
        }

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

    public boolean cmdSetDistance(CommandSender sender, String[] args, boolean help){
        if (help){
            message(sender, "Syntax: " + structSetDistanceSyntax);
            message(sender, "Sets the minimum chunk spawn distance of the structure");
            message(sender, "specified, or if none specified, the one the player is in");
            message(sender, "Ex: /struct set distance 16");
            return true;
        }

        if (args.length < 2){
            error(sender, "No minimum distance specified");
            return true;
        }
        int dist = Integer.valueOf(args[1]);
        Structure struct = getStructureFromSender(sender, args, 2);
        if (struct == null) return true;
        struct.setDistance(dist);
        message(sender, "Minimum distance set to " + Integer.toString(dist));
        return true;
    }

    public boolean cmdSetChance(CommandSender sender, String[] args, boolean help){
        if (help){
            message(sender, "Syntax: " + structSetChanceSyntax);
            message(sender, "Sets the spawn chance of the structure specified,");
            message(sender, "or if none specified, the one the player is in");
            message(sender, "Note: a grid is constructed by the distance and each section");
            message(sender, "tests a random 1/chance to see if it will spawn");
            message(sender, "Ex: /struct set chance 16");
            return true;
        }

        if (args.length < 2){
            error(sender, "No spawn chance specified");
            return true;
        }
        int chance = Integer.valueOf(args[1]);
        Structure struct = getStructureFromSender(sender, args, 2);
        if (struct == null) return true;
        struct.setChance(chance);
        message(sender, "Spawn chance set to " + Integer.toString(chance));
        return true;
    }

    public boolean cmdSetBiome(CommandSender sender, String[] args, boolean help){
        if (help){
            message(sender, "Syntax: " + structSetBiomeSyntax);
            message(sender, "Sets the spawn biome of the structure specified,");
            message(sender, "or if none specified, the one the player is in");
            message(sender, "Note: use \"null\" to spawn in any biome");
            message(sender, "Ex: /struct set biome ocean");
            return true;
        }

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

    public boolean cmdSetType(CommandSender sender, String[] args, boolean help){
        if (help){
            message(sender, "Syntax: " + structSetTypeSyntax);
            message(sender, "Sets the spawn type of the structure specified,");
            message(sender, "or if none specified, the one the player is in");
            message(sender, "Note: \"surface\", \"sky\", \"underground\",");
            message(sender, "and \"all\" may be used");
            message(sender, "Ex: /struct set ocean");
            return true;
        }

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

    public boolean cmdSetYMax(CommandSender sender, String[] args, boolean help){
        if (help){
            message(sender, "Syntax: " + structSetMaxSyntax);
            message(sender, "Sets the vertical spawn max of the structure specified,");
            message(sender, "or if none specified, the one the player is in");
            message(sender, "Note: calculated from the origin of the structure");
            message(sender, "Ex: /struct set max 128");
            return true;
        }

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

    public boolean cmdSetYMin(CommandSender sender, String[] args, boolean help){
        if (help){
            message(sender, "Syntax: " + structSetMinSyntax);
            message(sender, "Sets the vertical spawn minimum of the structure specified,");
            message(sender, "or if none specified, the one the player is in");
            message(sender, "Note: calculated from the origin of the structure");
            message(sender, "Ex: /struct set min 128");
            return true;
        }

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

    public boolean cmdSetGrow(CommandSender sender, String[] args, boolean help){
        if (help){
            message(sender, "Syntax: " + structSetMaxSyntax);
            message(sender, "Toggles if structures can grow outside of their set,");
            message(sender, "boundaries. For example, the entire structure has to be");
            message(sender, "in a kind of biome, or under a certain height.");
            message(sender, "Note: " + ChatColor.RED + "currently unimplemented");
            message(sender, "Ex: /struct set grow true");
            return true;
        }

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

    public boolean cmdPassive(CommandSender sender, String[] args, boolean help){
        if (help && args.length < 2){
            message(sender, "Syntax: " + structPassiveSyntax);
            message(sender, "Sub-Commands:");
            message(sender, structPassiveMaterialSyntax);
            message(sender, "Commands that modify the passive/assertive property of blocks");
            return true;
        }

        String[] newargs = Arrays.copyOfRange(args, 1, args.length);
        if (args.length < 2) error(sender, "No operation specified");
        if      (args[1].equals("material")) return cmdPassiveMaterial(sender, newargs, help);
        else if (args[1].equals("distance")) return cmdSetDistance(sender, newargs, help);
        else error(sender, "Not a valid operation");
        return true;
    }

    public boolean cmdPassiveMaterial(final CommandSender sender, String[] args, boolean help){
        if (help){
            message(sender, "Syntax: " + structPassiveMaterialSyntax);
            message(sender, "Sets passive/assertive flag of all locations of a certain block");
            message(sender, "Note: Changing the block will not modify the flag,");
            message(sender, "so this is useful for making a section of assertive air");
            message(sender, "Ex: /struct passive material stone true");
            return true;
        }

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
        final boolean solid;
        final boolean nonSolid;
        if (passive == null){
            if (args[1].toUpperCase().equals("SOLID")) {
                solid = true;
                nonSolid = false;
            }
            else if (args[1].toUpperCase().equals("NONSOLID")) {
                solid = false;
                nonSolid = true;
            }
            else if (args[1].toUpperCase().equals("ALL")) {
                solid = true;
                nonSolid = true;
            }
            else {
                error(sender, "Invalid material name");
                return true;
            }
        }
        else {
            solid = false;
            nonSolid = false;
        }
        final Structure struct = getStructureFromSender(sender, args, 3);
        if (struct == null) return true;
        struct.update();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
            public void run() {
                int id = -1;
                if (passive != null) {
                    passive.getId();
                    message(sender, "Making all instances of " + passive.toString().toLowerCase() + (flag ? " passive" : " assertive"));
                }
                else message(sender, "Making all " + ((nonSolid && solid) ? "" : (solid ? "solid " : "non-solid ")) + "instances " + (flag ? " passive" : " assertive"));
                Collection<StructureChunk> chunks = struct.getAllChunks().values();
                for (StructureChunk chunk : chunks){
                    Collection<StructureSection> sects = chunk.getAllSections().values();
                    for (StructureSection sect : sects){
                        byte[] materials = sect.getBlocks();
                        for (int i = 0; i < materials.length; i++)
                            if (materials[i] == id) sect.setBlockPassive(i, flag);
                    }
                }
                if (passive != null) message(sender, "All instances of " + passive.toString().toLowerCase() + " are now"  + (flag ? " passive" : " assertive"));
                else message(sender, "All " + ((nonSolid && solid) ? "" : (solid ? "solid " : "non-solid ")) + "instances " + " are now"  + (flag ? " passive" : " assertive"));
            }
        });
        return true;
    }

    public boolean cmdHelp(CommandSender sender, String[] args, boolean help){
        if (help) {
            message(sender, "Syntax: /struct help [command]");
            message(sender, "The help command provides basic help on other commands.");
            message(sender, "Ex: /struct help set biome");
            return true;
        }

        String[] newargs = Arrays.copyOfRange(args, 1, args.length);
        return cmdStruct(sender, newargs, true);
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
