package org.bytefire.plugins.shipwreckedwgen.structures;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StructureUtil {

    public static long mergeCoords(int x, int z){
        return ((long) x << 32) | ((long) z & 0xFFFFFFFFL);
    }

    public static int splitCoords(long xz, boolean left){
        if (left) return (int) (xz >> 32);
        else return (int) (xz | 0xFFFFFFFF00000000L);
    }

    public static void clearEditorCache(){
        File worldFolder = Bukkit.getWorldContainer();
        File[] files = worldFolder.listFiles();
        for (File file : files){
            if (file.getName().endsWith(".structure"))
                deleteEditorFolder(file);
        }
    }

    public static void deleteEditorFolder(File folder){
        File[] contents = folder.listFiles();
        for (File file : contents){
            if (file.isDirectory())
                deleteEditorFolder(file);
            else
                file.delete();
        }
        folder.delete();
    }
}
