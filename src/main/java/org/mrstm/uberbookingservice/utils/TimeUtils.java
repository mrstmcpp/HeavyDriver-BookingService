package org.mrstm.uberbookingservice.utils;

import java.util.Date;

public class TimeUtils {

    private TimeUtils(){

    }
    public static String calculateTotalTimeTaken(Date startTime, Date endTime) {
        if (startTime == null) return "Not started";
        if (endTime == null) return "Ongoing";

        long durationMillis = endTime.getTime() - startTime.getTime();
        if (durationMillis < 0) return "Invalid Time Range";

        long hours = durationMillis / (1000 * 60 * 60);
        long minutes = (durationMillis / (1000 * 60)) % 60;
        long seconds = (durationMillis / 1000) % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0 || hours > 0) sb.append(minutes).append("m ");
        sb.append(seconds).append("s");

        return sb.toString().trim();
    }


}
