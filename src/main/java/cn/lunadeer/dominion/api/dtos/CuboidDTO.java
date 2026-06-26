package cn.lunadeer.dominion.api.dtos;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Vector;

/**
 * Represents a cuboid (rectangular prism) in a 3D space.
 * This class provides methods to manipulate and query the cuboid's properties,
 * such as its dimensions, volume, and intersection with other cuboids.
 */
public class CuboidDTO {

    /**
     * A constant representing a zero-sized cuboid.
     */
    public static CuboidDTO ZERO = new CuboidDTO(0, 0, 0, 0, 0, 0);

    private int[] pos1 = new int[3];
    private int[] pos2 = new int[3];

    /**
     * Constructs a CuboidDTO with the specified positions.
     *
     * @param pos1 the first position of the cuboid
     * @param pos2 the second position of the cuboid
     */
    public CuboidDTO(int[] pos1, int[] pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        sortPos();
    }

    /**
     * Constructs a CuboidDTO with the same positions as the specified cuboid.
     *
     * @param cuboid the cuboid to copy positions from
     */
    public CuboidDTO(CuboidDTO cuboid) {
        this.pos1 = cuboid.getPos1().clone();
        this.pos2 = cuboid.getPos2().clone();
    }

    /**
     * Constructs a CuboidDTO with the specified coordinates.
     *
     * @param x1 the x-coordinate of the first position
     * @param y1 the y-coordinate of the first position
     * @param z1 the z-coordinate of the first position
     * @param x2 the x-coordinate of the second position
     * @param y2 the y-coordinate of the second position
     * @param z2 the z-coordinate of the second position
     */
    public CuboidDTO(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.pos1[0] = x1;
        this.pos1[1] = y1;
        this.pos1[2] = z1;
        this.pos2[0] = x2;
        this.pos2[1] = y2;
        this.pos2[2] = z2;
        sortPos();
    }

    /**
     * Constructs a CuboidDTO with the specified world and positions.
     *
     * @param pos1 the first position of the cuboid
     * @param pos2 the second position of the cuboid
     */
    public CuboidDTO(Vector<Integer> pos1, Vector<Integer> pos2) {
        this.pos1[0] = pos1.get(0);
        this.pos1[1] = pos1.get(1);
        this.pos1[2] = pos1.get(2);
        this.pos2[0] = pos2.get(0);
        this.pos2[1] = pos2.get(1);
        this.pos2[2] = pos2.get(2);
        sortPos();
    }

    /**
     * Sorts the positions to ensure pos1 is always less than or equal to pos2.
     */
    private void sortPos() {
        int[] temp = new int[3];
        for (int i = 0; i < 3; i++) {
            if (pos1[i] > pos2[i]) {
                temp[i] = pos1[i];
                pos1[i] = pos2[i];
                pos2[i] = temp[i];
            }
        }
    }

    /**
     * Gets the first position of the cuboid.
     *
     * @return the first position of the cuboid
     */
    public int[] getPos1() {
        return pos1.clone();
    }

    /**
     * Sets the first position of the cuboid.
     *
     * @param pos1 the new first position of the cuboid
     */
    public void setPos1(int[] pos1) {
        this.pos1 = pos1.clone();
    }

    /**
     * Gets the second position of the cuboid.
     *
     * @return the second position of the cuboid
     */
    public int[] getPos2() {
        return pos2.clone();
    }

    /**
     * Sets the second position of the cuboid.
     *
     * @param pos2 the new second position of the cuboid
     */
    public void setPos2(int[] pos2) {
        this.pos2 = pos2.clone();
    }

    public int x1() {
        return pos1[0];
    }

    public int y1() {
        return pos1[1];
    }

    public int z1() {
        return pos1[2];
    }

    public int x2() {
        return pos2[0];
    }

    public int y2() {
        return pos2[1];
    }

    public int z2() {
        return pos2[2];
    }

    public long xLength() {
        return pos2[0] - pos1[0];
    }

    public long yLength() {
        return pos2[1] - pos1[1];
    }

    public long zLength() {
        return pos2[2] - pos1[2];
    }

    public long getSquare() {
        return xLength() * zLength();
    }

    public long getVolume() {
        return xLength() * yLength() * zLength();
    }

    public boolean intersectWith(CuboidDTO cuboid) {
        return x1() < cuboid.x2() && x2() > cuboid.x1() &&
                y1() < cuboid.y2() && y2() > cuboid.y1() &&
                z1() < cuboid.z2() && z2() > cuboid.z1();
    }

    public boolean contain(CuboidDTO cuboid) {
        return contain(cuboid, false);
    }

    public boolean contain(CuboidDTO cuboid, boolean ignoreY) {
        if (ignoreY) {
            return x1() <= cuboid.x1() && x2() >= cuboid.x2() && z1() <= cuboid.z1() && z2() >= cuboid.z2();
        } else {
            return x1() <= cuboid.x1() && x2() >= cuboid.x2() && y1() <= cuboid.y1() && y2() >= cuboid.y2() && z1() <= cuboid.z1() && z2() >= cuboid.z2();
        }
    }

    public boolean contain(int x, int y, int z) {
        return x1() <= x && x2() > x && y1() <= y && y2() > y && z1() <= z && z2() > z;
    }

    public boolean containedBy(CuboidDTO cuboid) {
        return cuboid.contain(this);
    }

    public long minusSquareWith(CuboidDTO cuboid) {
        return getSquare() - cuboid.getSquare();
    }

    public long minusVolumeWith(CuboidDTO cuboid) {
        return getVolume() - cuboid.getVolume();
    }

    public void addUp(int size) {
        if (pos2[1] + size < pos1[1]) {
            pos2[1] = pos1[1] + 1;
        } else {
            pos2[1] += size;
        }
    }

    public void addDown(int size) {
        if (pos1[1] - size > pos2[1]) {
            pos1[1] = pos2[1] - 1;
        } else {
            pos1[1] -= size;
        }
    }

    public void addNorth(int size) {
        if (pos1[2] - size > pos2[2]) {
            pos1[2] = pos2[2] - 1;
        } else {
            pos1[2] -= size;
        }
    }

    public void addSouth(int size) {
        if (pos2[2] + size < pos1[2]) {
            pos2[2] = pos1[2] + 1;
        } else {
            pos2[2] += size;
        }
    }

    public void addEast(int size) {
        if (pos2[0] + size < pos1[0]) {
            pos2[0] = pos1[0] + 1;
        } else {
            pos2[0] += size;
        }
    }

    public void addWest(int size) {
        if (pos1[0] - size > pos2[0]) {
            pos1[0] = pos2[0] - 1;
        } else {
            pos1[0] -= size;
        }
    }
}
