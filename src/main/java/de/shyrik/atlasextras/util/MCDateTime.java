package de.shyrik.atlasextras.util;

import net.minecraft.client.resources.I18n;

public class MCDateTime {

    public static final long DAYLENGTH = 24000L;
    public static final String[] DAYS = { "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday" };

    public long totalDays;
    public int dayOfWeek;
    public int hour;
    public int min;

    public MCDateTime(long ticks) {
        totalDays = (ticks + DAYLENGTH ) / DAYLENGTH;
        dayOfWeek = (int)(totalDays % 7);

        long daytime = ticks % DAYLENGTH;
        hour = (int)(daytime > 18000 ? daytime / 1000 - 18 : daytime / 1000 + 6);
        min = (int)Math.floor(daytime % 1000F / ( 50F / 3F));
    }

    public String getDayName() {
        return I18n.format( "atlasextras.datetime.day." + DAYS[dayOfWeek]);
    }
}
