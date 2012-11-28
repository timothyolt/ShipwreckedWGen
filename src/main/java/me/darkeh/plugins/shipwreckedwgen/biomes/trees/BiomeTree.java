package me.darkeh.plugins.shipwreckedwgen.biomes.trees;

public interface BiomeTree {
    /* Feilds
     * Trees may vary but all contain these basic feilds defining the tree:
     * private Random rand;
     * private Location center;
     * private int height;
     * private int branches;
     */

    /* Consructor
     * Trees may vary but all trees must have a constructor of the sorts:
     * public BiomeTree(Random rand, Location center)
     */

    /*
     * Generates a branch on the tree
     * Starts at the specified Y Section of the tree
     * Upward angle of the branch is decided by its height
     */
    public void branch(int ySection, int height);

    /*
     * Generates a full tree including branches
     * Returns true if the tree was interrupted during generation
     * Returns false if the tree was generated sucessfully
     */
    public boolean generate();
}
