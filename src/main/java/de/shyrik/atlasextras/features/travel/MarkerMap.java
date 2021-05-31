package de.shyrik.atlasextras.features.travel;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.core.Configuration;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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

    @Override
    public void load(CompoundNBT cmp) {
        ListNBT nbtList = cmp.getList("marks", 10);
        for (int i = 0; i < nbtList.size(); i++) {
            CompoundNBT tag = nbtList.getCompound(i);
            int id = tag.getInt(NBT_ID);

            marks.put(id, new Mark(id,
                    new BlockPos(tag.getInt(NBT_X), tag.getInt(NBT_Y), tag.getInt(NBT_Z)),
                    tag.getBoolean(NBT_TRAVELTO),
                    tag.getBoolean(NBT_TRAVELFROM),
                    tag.getString(NBT_SOURCEMOD)
            ));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT cmp) {
        ListNBT nbtList = new ListNBT();
        for (Mark mark : marks.values()) {

            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt(NBT_ID, mark.id);
            nbt.putInt(NBT_X, mark.pos.getX());
            nbt.putInt(NBT_Y, mark.pos.getY());
            nbt.putInt(NBT_Z, mark.pos.getZ());
            nbt.putBoolean(NBT_TRAVELTO, mark.canTravelTo);
            nbt.putBoolean(NBT_TRAVELFROM, mark.canTravelFrom);
            nbt.putString(NBT_SOURCEMOD, mark.sourceMod);

            nbtList.add(nbt);
        }

        cmp.put("marks", nbtList);
        return cmp;
    }

    private static MarkerMap ins;
    public static MarkerMap instance(@Nonnull World world) {
        if (ins == null) {
            ins = new MarkerMap();
        }

        return ((ServerWorld)world).getDataStorage().computeIfAbsent(() -> ins, DATA_ID);
    }


    public void put(int id, BlockPos pos, boolean canJumpTo, boolean canJumpFrom, String modid) {
        marks.put(id, new Mark(id, pos, canJumpTo, canJumpFrom, modid));
        setDirty();
    }

    public void remove(int id) {
        marks.remove(id);
        setDirty();
    }

    public Mark get(int id) {
        return marks.getOrDefault(id, null);
    }

    public Mark getFromPos(@Nonnull BlockPos pos) {
        for (Mark entry : marks.values()) {
            if (entry.pos.asLong() == pos.asLong()) return entry;
        }
        return null;
    }

    public boolean hasOnPos(@Nonnull BlockPos pos) {
        return getFromPos(pos) != null;
    }

    public boolean hasJumpNearby(@Nonnull BlockPos pos) {
        for (Mark entry : marks.values()) {
            if (entry.canTravelFrom && entry.pos.closerThan(pos, Configuration.COMPAT.distanceToMarker))
                return true;
        }

        return false;
    }

    public static class Mark {
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
