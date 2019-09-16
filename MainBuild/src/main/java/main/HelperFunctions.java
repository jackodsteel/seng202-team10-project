package main;

import dataObjects.Cyclist;
import dataObjects.Route;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.List;


/**
 * Includes some useful static helper functions that may be applicable to multiple classes and that don't fit anywhere
 */
public class HelperFunctions {

    /**
     * Takes four doubles as two points and returns the distance between them. Uses the haversine formula as lat long coords are circle based.
     *
     * @param lat1 Point 1 Lat
     * @param lng1 Point 1 Long
     * @param lat2 Point 2 Lat
     * @param lng2 Point 2 Long
     * @return Distance
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c;
    }

    /**
     * Converts a simple int of seconds to a formatted string that contains the number of days, hours, minutes and seconds.
     *
     * @param seconds
     * @return A String of the format "wd, xh, ym, zs", that only includes values that are significant (drops leading zeros)
     */
    public static String secondsToString(int seconds) {
        int days;
        int hours;
        int mins;
        int secs;
        String out = "";
        days = seconds / 86400;
        out += ((days != 0) ? days + "d, " : "");
        hours = (seconds % 86400) / 3600;
        out += ((hours != 0 || days != 0) ? hours + "h, " : "");
        mins = ((seconds % 86400) % 3600) / 60;
        out += ((mins != 0 || days != 0 || hours != 0) ? mins + "m, " : "");
        secs = (seconds % 86400 % 3600) % 60;
        out += secs + "s";
        //System.out.println(out);
        return out;
    }

    /**
     * Takes a series of strings and calculates the difference in times between the two. Ignore daylight savings time currently
     *
     * @param start_year
     * @param start_month
     * @param start_day
     * @param start_time
     * @param end_year
     * @param end_month
     * @param end_day
     * @param end_time
     * @return Difference in seconds
     */
    public static int getDuration(String start_year, String start_month, String start_day, String start_time,
                                  String end_year, String end_month, String end_day, String end_time) {
        String[] start_time_seperated = start_time.split(":");
        int start_hour = Integer.parseInt(start_time_seperated[0]);
        int start_min = Integer.parseInt(start_time_seperated[1]);
        int start_sec = Integer.parseInt(start_time_seperated[2]);

        String[] end_time_seperated = end_time.split(":");
        int end_hour = Integer.parseInt(end_time_seperated[0]);
        int end_min = Integer.parseInt(end_time_seperated[1]);
        int end_sec = Integer.parseInt(end_time_seperated[2]);

        int start_year_int = Integer.parseInt(start_year);
        int start_month_int = Integer.parseInt(start_month);
        int start_day_int = Integer.parseInt(start_day);

        int end_year_int = Integer.parseInt(end_year);
        int end_month_int = Integer.parseInt(end_month);
        int end_day_int = Integer.parseInt(end_day);

        DateTime start = new DateTime(start_year_int, start_month_int, start_day_int, start_hour, start_min, start_sec, DateTimeZone.UTC);
        DateTime end = new DateTime(end_year_int, end_month_int, end_day_int, end_hour, end_min, end_sec, DateTimeZone.UTC);
        Duration duration = new Duration(start, end);
        return duration.toStandardSeconds().getSeconds();
    }

    /**
     * Turns the date input into a three part String array where
     * the values are year, month, day respectively.
     *
     * @param date String of the form yyyy-MM-dd
     */
    public static String[] convertDates(String date) {
        String dateInts[] = new String[3];
        if (date == null || date.length() != 10 || date.charAt(4) != '-' || date.charAt(7) != '-') {
            return null;
        }
        dateInts[0] = date.substring(0, 4);
        dateInts[1] = date.substring(5, 7);
        dateInts[2] = date.substring(8);
        if (Integer.parseInt(dateInts[1]) > 12 || Integer.parseInt(dateInts[2]) > 31) {
            return null;
        }
        return dateInts;
    }

    /**
     * Checks the validity of user time input.
     *
     * @param time time to check
     * @return a boolean true if the time is valid, false otherwise
     */
    public static Boolean checkTime(String time) {
        if (!time.matches("([0-1][0-9]|2[0-4]):[0-5][0-9](:[0-5][0-9])?")) {
            return false;
        }
        return true;
    }

    /**
     * Checks that the date entered has a year between 1900-2017, month between 1-12 and day between 1-31.
     * If these bounds aren't met, a true boolean will be returned.
     *
     * @param day   the dates day
     * @param month the dates month
     * @param year  the dates year
     * @return true if an invalid date is given, false otherwise
     */
    public static boolean checkDateDetails(int day, int month, int year) {
        if (year < 1900 || year > 2017) { // People born out of these bounds are assumed not to use this program.
            return true;
        } else if (month < 1 || month > 12 || day < 1 || day > 31) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Calculates the total distance a user has cycled.
     * @param cyclist the cyclist whose total distance cycled is to be calculated
     * @return the total distance the cyclist has travelled
     */
    public static double calculateDistanceCycled(Cyclist cyclist) {
        List<Route> takenList = new ArrayList<>();
        takenList.addAll(cyclist.getTakenRoutes());
        double distanceCycled = 0;
        for (Route route : takenList) {
            distanceCycled += route.getDistance();
        }
        return HelperFunctions.format2dp(distanceCycled);
    }


    /**
     * Calculates the average distance a user has cycled.
     * @param cyclist the cyclist whose total distance cycled is to be calculated
     * @param hu the instance of the current users being handled in the program
     * @return average distance travelled
     */
    public static double cacluateAverageDistance(Cyclist cyclist, HandleUsers hu) {
        List<Route> takenList = new ArrayList<>();
        takenList.addAll(hu.currentCyclist.getTakenRoutes());
        double averageDistance = 0;
        if (takenList.size() == 0) { // Avoid divide by 0 error.
            averageDistance = 0;
        } else {
            averageDistance = calculateDistanceCycled(cyclist) / takenList.size();
        }
        return HelperFunctions.format2dp(averageDistance);
    }


    /**
     * Calculates the users shortest distance, returns an arbitrarily large number if there are no
     * routes in the takenRouteList.
     * @param cyclist the cyclist whose shortest route is to be found
     * @return shortest distance out of all taken routes
     */
    public static double calculateShortestRoute(Cyclist cyclist) {
        List<Route> takenList = new ArrayList<>();
        takenList.addAll(cyclist.getTakenRoutes());
        double shortestDistance = 9999999; // No routes will be of a size greater than this.
        for (int i = 0; i < cyclist.getTakenRoutes().size(); i++) {
            if (takenList.get(i).getDistance() < shortestDistance) {
                shortestDistance = takenList.get(i).getDistance();
            }
        }
        return shortestDistance;
    }


    /**
     * Calculates the users longest distance, returns -1 if there are no
     * routes in the takenRouteList.
     * @param cyclist the cyclist whose shortest route is to be found
     * @return shortest distance out of all taken routes
     */
    public static double calculateLongestRoute(Cyclist cyclist) {
        List<Route> takenList = new ArrayList<>();
        takenList.addAll(cyclist.getTakenRoutes());
        double longestDistance = -1;
        for (Route route : takenList) {
            if (route.getDistance() > longestDistance) {
                longestDistance = route.getDistance();
            }
        }
        return longestDistance;
    }


    /**
     * Formats a number to 2dp without rounding.
     * @param number the double to be formatted
     * @return a 2dp format of the given number
     *
     */
    public static double format2dp(double number) {
        number = Math.round(number * 100);
        number = number / 100;
        return number;
    }
}
