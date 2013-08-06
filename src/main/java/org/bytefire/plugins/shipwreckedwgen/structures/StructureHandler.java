package org.bytefire.plugins.shipwreckedwgen.structures;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Chunk;
import org.bytefire.plugins.shipwreckedwgen.ChunkHandler;
import org.bytefire.plugins.shipwreckedwgen.ShipwreckedWGen;

public class StructureHandler {

    private ShipwreckedWGen plugin;
    private HashMap<String, Structure> structEditors;
    private HashMap<String, ArrayList<Long>> queuedUpdates;

    public StructureHandler(ShipwreckedWGen plugin){
        this.plugin = plugin;
        structEditors = new HashMap<String, Structure>();
        queuedUpdates = new HashMap<String, ArrayList<Long>>();
    }

    public void addEditor(String world, Structure struct){
        if (!structEditors.containsKey(world)) structEditors.put(world, struct);
    }

    public boolean isEditor(String world){
        return structEditors.containsKey(world);
    }

    public Structure getEditor(String world){
        return structEditors.get(world);
    }

    public void queueUpdate(Chunk chunk){
        String name = chunk.getWorld().getName();
        if (!queuedUpdates.containsKey(name)) queuedUpdates.put(name, new ArrayList<Long>());
        ArrayList<Long> queue = queuedUpdates.get(name);
        long hash = ChunkHandler.intToLong(chunk.getX(), chunk.getZ());
        if (!queue.contains(hash)) queue.add(hash);
    }

    public ArrayList<Long> getQueuedUpates(String world){
        return queuedUpdates.get(world);
    }

    public void clearQueuedUpdates(String world){
        queuedUpdates.clear();
    }

}
