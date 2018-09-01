package de.shyrik.atlasextras.features.travel;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.core.Configuration;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MarkerMap extends WorldSavedData {

    private static final String DATA_ID = AtlasExtras.MODID + ":atlasmarkermap";
    private static final String NBT_ID = "id";
    private static final String NBT_X = "x";
    private static final String NBT_Y = "y";
    private static final String NBT_Z = "z";
    private static final String NBT_TRAVELTO = "travelto";
    private static final String NBT_TRAVELFROM = "travelfrom";
    private static final String NBT_SOURCEMOD = "sourcemod";
    private Map<Integer, Mark> marks = new ConcurrentHashMap<>();

    public MarkerMap() {
        super(DATA_ID);
    }

    public MarkerMap(String s) {
        super(s);
    }

    public static MarkerMap instance(@Nonnull World world) {
        MarkerMap map = (MarkerMap) world.getPerWorldStorage().getOrLoadData(MarkerMap.class, DATA_ID);
        if (map == null) {
            map = new MarkerMap();
            world.getPerWorldStorage().setData(DATA_ID, map);
        }
        return map;
    }


    public void put(int id, BlockPos pos, boolean canJumpTo, boolean canJumpFrom, String modid) {
        marks.put(id, new Mark(id, pos, canJumpTo, canJumpFrom, modid));
        markDirty();
    }

    public void remove(int id) {
        marks.remove(id);
        markDirty();
    }

    public Mark get(int id) {
        if (marks.containsKey(id)) return marks.get(id);
        return null;
    }

    public Mark getFromPos(@Nonnull BlockPos pos) {
        for (Map.Entry<Integer, Mark> entry : marks.entrySet()) {
            if (entry.getValue().pos.toLong() == pos.toLong()) return entry.getValue();
        }
        return null;
    }

    public boolean hasOnPos(@Nonnull BlockPos pos) {
        return getFromPos(pos) != null;
    }

    public boolean hasJumpNearby(@Nonnull BlockPos pos) {
        for (Map.Entry<Integer, Mark> entry : marks.entrySet()) {
            if (entry.getValue().canTravelFrom && entry.getValue().pos.getDistance(pos.getX(), pos.getY(), pos.getZ()) <= Configuration.COMPAT.distanceToMarker)
                return true;
        }
        return false;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbt) {
        NBTTagList nbtList = nbt.getTagList("marks", 10);
        for (int i = 0; i < nbtList.tagCount(); i++) {
            NBTTagCompound tag = nbtList.getCompoundTagAt(i);
            int id = tag.getInteger(NBT_ID);

            marks.put(id, new Mark(id,
                    new BlockPos(tag.getInteger(NBT_X), tag.getInteger(NBT_Y), tag.getInteger(NBT_Z)),
                    tag.getBoolean(NBT_TRAVELTO), tag.getBoolean(NBT_TRAVELFROM),
                    tag.getString(NBT_SOURCEMOD)));
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagList nbtList = new NBTTagList();
        for (Map.Entry<Integer, Mark> entry : marks.entrySet()) {
            Mark mark = entry.getValue();

            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger(NBT_ID, mark.id);
            nbt.setInteger(NBT_X, mark.pos.getX());
            nbt.setInteger(NBT_Y, mark.pos.getY());
            nbt.setInteger(NBT_Z, mark.pos.getZ());
            nbt.setBoolean(NBT_TRAVELTO, mark.canTravelTo);
            nbt.setBoolean(NBT_TRAVELFROM, mark.canTravelFrom);
            nbt.setString(NBT_SOURCEMOD, mark.sourceMod);

            nbtList.appendTag(nbt);
        }
        compound.setTag("marks", nbtList);
        return compound;
    }

    public class Mark {
        public int id;
        public BlockPos pos;
        public boolean canTravelTo;
        public boolean canTravelFrom;
        public String sourceMod;

        public Mark(int id, BlockPos pos, boolean canTravelTo, boolean canTravelFrom, String sourceMod) {
            this.id = id;
            this.pos = pos;
            this.canTravelTo = canTravelTo;
            this.canTravelFrom = canTravelFrom;
            this.sourceMod = sourceMod;
        }
    }
}
