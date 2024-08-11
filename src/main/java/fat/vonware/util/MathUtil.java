package fat.vonware.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class MathUtil
        implements Util {

    public static double distanceTo(final BlockPos blockPos) {
        return distanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
    public static double distanceTo(final double x, final double y, final double z) {
        if (mc.player == null) {
            return 0.0;
        }
        final float f = (float)(mc.player.posX - x);
        final float g = (float)(mc.player.posY - y);
        final float h = (float)(mc.player.posZ - z);
        return MathHelper.sqrt(f * f + g * g + h * h);
    }
    public static float fixedNametagScaling(final float scale) {
        return scale * 0.01f;
    }
    public static Vec3d roundVec(final Vec3d vec3d, final int places) {
        return new Vec3d(round(vec3d.x, places), round(vec3d.y, places), round(vec3d.z, places));
    }
    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.FLOOR);
        return bd.doubleValue();
    }
    public static double getDistance(final double posX, final double posY, final double posZ) {
        final double x = MathUtil.mc.player.posX - posX;
        final double y = MathUtil.mc.player.posY - posY;
        final double z = MathUtil.mc.player.posY - posZ;
        return MathHelper.sqrt(x * x + y * y + z * z);
    }

    public static double getDistance(final Entity entity) {
        final double x = MathUtil.mc.player.posX - entity.posX;
        final double y = MathUtil.mc.player.posY - entity.posY;
        final double z = MathUtil.mc.player.posY - entity.posZ;
        return MathHelper.sqrt(x * x + y * y + z * z);
    }
    public static int clamp(int num, int min, int max) {
        return num < min ? min : Math.min(num, max);
    }


    public static double square(double input) {
        return input * input;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean descending) {
        LinkedList<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        if (descending) {
            list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        } else {
            list.sort(Map.Entry.comparingByValue());
        }
        LinkedHashMap result = new LinkedHashMap();
        for (Map.Entry entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static String getTimeOfDay() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(11);
        if (timeOfDay < 12) {
            return "Good Morning ";
        }
        if (timeOfDay < 16) {
            return "Good Afternoon ";
        }
        if (timeOfDay < 21) {
            return "Good Evening ";
        }
        return "Good Night ";
    }
    public static float[] calcAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }
}

