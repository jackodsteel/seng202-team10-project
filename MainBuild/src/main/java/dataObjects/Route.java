package dataObjects;


import dataManipulation.UpdateData;
import main.HelperFunctions;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Has methods for all main functionality of the routing.
 */
public class Route {
    private int duration, age, rank;
    private String name, startTime, stopTime, startDate, stopDate, startDay, startMonth, startYear, stopDay, stopMonth,
            stopYear, bikeid, gender, userType, listName;
    private Location startLocation, endLocation;
    private double distance;


    /**
     * Constructor for Route class, used by Datafilterer class when filtering records from the database.
     */
    public Route(int tripDuration, String stTime, String spTime, String stDay, String stMonth, String stYear,
                 String spDay, String spMonth, String spYear, double stStationLat, double stStationLong,
                 double endStationLat, double endStationLong, int stStationID, int endStationID, String stStationAdr,
                 String endStationAdr, String bId, int riderGender, String riderType, int riderAge, String list) {
        duration = tripDuration;
        startTime = stTime;
        startDay = stDay;
        startMonth = stMonth;
        startYear = stYear;
        stopTime = spTime;
        stopDay = spDay;
        stopMonth = spMonth;
        stopYear = spYear;
        bikeid = bId;
        userType = riderType;
        age = riderAge;
        listName = list;
        setGender(riderGender);
        startLocation = new StationLocation(stStationID, stStationLat, stStationLong, stStationAdr);
        endLocation = new StationLocation(endStationID, endStationLat, endStationLong, endStationAdr);
        startDate = getDateString(startDay, startMonth, startYear);
        stopDate = getDateString(stopDay, stopMonth, stopYear);
        setDistance();
    }


    /**
     * Constructor used when a user adds a route as a favourite and a rank needs to be added.
     */
    public Route(int tripDuration, String stTime, String spTime, String stDay, String stMonth, String stYear,
                 String spDay, String spMonth, String spYear, double stStationLat, double stStationLong,
                 double endStationLat, double endStationLong, int stStationID, int endStationID, String stStationAdr,
                 String endStationAdr, String bId, int riderGender, String riderType, int riderAge, String list,
                 int routeRank) {
        duration = tripDuration;
        startTime = stTime;
        startDay = stDay;
        startMonth = stMonth;
        startYear = stYear;
        stopTime = spTime;
        stopDay = spDay;
        stopMonth = spMonth;
        stopYear = spYear;
        bikeid = bId;
        userType = riderType;
        age = riderAge;
        listName = list;
        setGender(riderGender);
        startLocation = new StationLocation(stStationID, stStationLat, stStationLong, stStationAdr);
        endLocation = new StationLocation(endStationID, endStationLat, endStationLong, endStationAdr);
        startDate = getDateString(startDay, startMonth, startYear);
        stopDate = getDateString(stopDay, stopMonth, stopYear);
        rank = routeRank;
    }

    /**
     * Constructor used when a user adds a route as a taken route, inputting the routes distance.
     */
    public Route(int tripDuration, String stTime, String spTime, String stDay, String stMonth, String stYear,
                 String spDay, String spMonth, String spYear, double stStationLat, double stStationLong,
                 double endStationLat, double endStationLong, int stStationID, int endStationID, String stStationAdr,
                 String endStationAdr, String bId, int riderGender, String riderType, int riderAge, String list,
                 double rdistance) {
        duration = tripDuration;
        startTime = stTime;
        startDay = stDay;
        startMonth = stMonth;
        startYear = stYear;
        stopTime = spTime;
        stopDay = spDay;
        stopMonth = spMonth;
        stopYear = spYear;
        bikeid = bId;
        userType = riderType;
        age = riderAge;
        listName = list;
        setGender(riderGender);
        startLocation = new StationLocation(stStationID, stStationLat, stStationLong, stStationAdr);
        endLocation = new StationLocation(endStationID, endStationLat, endStationLong, endStationAdr);
        startDate = getDateString(startDay, startMonth, startYear);
        stopDate = getDateString(stopDay, stopMonth, stopYear);
        distance = rdistance;
    }


    //Getters for primary key info
    public String getBikeID() {
        return bikeid;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getStartDay() {
        return startDay;
    }

    public String getStartMonth() {
        return startMonth;
    }

    public String getStartYear() {
        return startYear;
    }


    // Getters
    public int getDuration() {
        return duration;
    }

    public int getStartStationID() {
        return startLocation.getNumber();
    }

    public int getEndStationID() {
        return endLocation.getNumber();
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public void setName(String input) {
        name = input;
    }

    public String getStopTime() {
        return stopTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStopDate() {
        return stopDate;
    }

    public String getStopDay() {
        return stopDay;
    }

    public String getStopMonth() {
        return stopMonth;
    }

    public String getStopYear() {
        return stopYear;
    }

    public String getStartAddress() {
        return startLocation.getAddress();
    }

    public String getEndAddress() {
        return endLocation.getAddress();
    }

    public String getGender() {
        return gender;
    }

    public String getUserType() {
        return userType;
    }

    public String getListName() {return listName;}

    public double getStartLatitude() {
        return startLocation.getLatitude();
    }

    public double getStartLongitude() {
        return startLocation.getLongitude();
    }

    public double getEndLatitude() {
        return endLocation.getLatitude();
    }

    public double getEndLongitude() {
        return endLocation.getLongitude();
    }

    public double getDistance() {
        return HelperFunctions.format2dp(distance);
    }

    //-----THIS GETTER IS USED DO NOT DELETE------
    public int getRank() {return rank;}
    //--------------------------------------------

    // Setters
    private void setDistance() {
        distance = HelperFunctions.getDistance(getStartLatitude(), getStartLongitude(), getEndLatitude(), getEndLongitude());
    }

    private void setStopDate() {
        stopDate = getDateString(stopDay, stopMonth, stopYear);
    }

    public void setDuration(int newDuration) {
        if (newDuration != duration) {
            duration = newDuration;
            UpdateData.updateRouteField("tripduration", duration, bikeid, startYear, startMonth,
                    startDay, startTime);
        }
    }

    public void setStartAddress(String newAddress) {
        if (!newAddress.equals(startLocation.getAddress())) {
            startLocation.setAddress(newAddress);
            UpdateData.updateRouteField("start_station_name", startLocation.getAddress(), bikeid, startYear,
                    startMonth, startDay, startTime);
        }
    }

    public void setStartID(int newID) {
        if (newID != startLocation.getNumber()) {
            startLocation.setNumber(newID);
            UpdateData.updateRouteField("start_station_id", startLocation.getNumber(), bikeid,
                    startYear, startMonth, startDay, startTime);
        }
    }

    public void setStartLat(double newLat) {
        if (newLat != startLocation.getLatitude()) {
            startLocation.setLatitude(newLat);
            UpdateData.updateRouteField("start_latitude", startLocation.getLatitude(), bikeid,
                    startYear, startMonth, startDay, startTime);
        }
    }

    public void setStartLong(double newLong) {
        if (newLong != startLocation.getLongitude()) {
            startLocation.setLongitude(newLong);
            UpdateData.updateRouteField("start_longitude", startLocation.getLongitude(), bikeid,
                    startYear, startMonth, startDay, startTime);
        }
    }

    public void setEndAddress(String newAddress) {
        if (!newAddress.equals(endLocation.getAddress())) {
            endLocation.setAddress(newAddress);
            UpdateData.updateRouteField("end_station_name", endLocation.getAddress(), bikeid, startYear,
                    startMonth, startDay, startTime);
        }
    }

    public void setEndID(int newID) {
        if (newID != endLocation.getNumber()) {
            endLocation.setNumber(newID);
            UpdateData.updateRouteField("end_station_id", endLocation.getNumber(), bikeid,
                    startYear, startMonth, startDay, startTime);
        }
    }

    public void setEndLat(double newLat) {
        if (newLat != endLocation.getLatitude()) {
            endLocation.setLatitude(newLat);
            UpdateData.updateRouteField("end_latitude", endLocation.getLatitude(), bikeid,
                    startYear, startMonth, startDay, startTime);
        }
    }

    public void setEndLong(double newLong) {
        if (newLong != endLocation.getLongitude()) {
            endLocation.setLongitude(newLong);
            UpdateData.updateRouteField("end_longitude", endLocation.getLongitude(), bikeid,
                    startYear, startMonth, startDay, startTime);
        }
    }

    public void setStopTime(String time) {
        if (time.length() < 8) {
            time = time + ":00";
        }
        if (!time.equals(stopTime)) {
            stopTime = time;
            UpdateData.updateRouteField("end_time", stopTime, bikeid, startYear, startMonth, startDay, startTime);
        }
    }

    public void setStopDay(String day) {
        if (day.length() < 2) {
            day = "0" + day;
        }
        if (!day.equals(stopDay)) {
            stopDay = day;
            UpdateData.updateRouteField("end_day", stopDay, bikeid, startYear, startMonth, startDay, startTime);
            setStopDate();
        }
    }

    public void setStopMonth(String month) {
        if (month.length() < 2) {
            month = "0" + month;
        }
        if (!month.equals(stopMonth)) {
            stopMonth = month;
            UpdateData.updateRouteField("end_month", stopMonth, bikeid, startYear, startMonth, startDay, startTime);
            setStopDate();
        }
    }

    public void setStopYear(String year) {
        if (!year.equals(stopYear)) {
            stopYear = year;
            UpdateData.updateRouteField("end_year", stopYear, bikeid, startYear, startMonth, startDay, startTime);
            setStopDate();
        }
    }

    public void setUserType(String newType) {
        if (!newType.equals(userType)) {
            userType = newType;
            UpdateData.updateRouteField("usertype", userType, bikeid, startYear, startMonth, startDay, startTime);
        }
    }

    public void setAge(int newAge) {
        if (newAge != age) {
            age = newAge;
            UpdateData.updateRouteField("birth_year", age, bikeid, startYear, startMonth,
                    startDay, startTime);
        }
    }

    public void setListName(String newList) {
        if (!newList.equals(listName)) {
            if (newList.equals("")) {
                listName = null;
            } else {
                listName = newList;
            }
            UpdateData.updateRouteField("list_name", listName, bikeid, startYear, startMonth,
                    startDay, startTime);
        }
    }

    public void setGender(String newGender) {
        if (!newGender.equals(gender)) {
            switch (newGender) {
                case "Male":
                    gender = newGender;
                    UpdateData.updateRouteField("gender", 1, bikeid, startYear, startMonth, startDay, startTime);
                    break;
                case "Female":
                    gender = newGender;
                    UpdateData.updateRouteField("gender", 2, bikeid, startYear, startMonth, startDay, startTime);
                    break;
                case "Other":
                    gender = newGender;
                    UpdateData.updateRouteField("gender", 3, bikeid, startYear, startMonth, startDay, startTime);
                    break;
                default:
                    gender = newGender;
                    UpdateData.updateRouteField("gender", 0, bikeid, startYear, startMonth, startDay, startTime);
                    break;
            }
        }
    }

    //Used when creating Route objects from the database.
    public void setGender(int riderGender) {
        if (riderGender == 1) {
            gender = "Male";
        } else if (riderGender == 2) {
            gender = "Female";
        } else if (riderGender == 3) {
            gender = "Other";
        } else {
            gender = "Not Specified";
        }
    }

////////////////////////////END OF GETTERS AND SETTERS\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /**
     * getDateString takes the day, month and year and separate integers and returns a more recognisable date format as
     * a string.
     *
     * @param day   day is of type int. This is the day a route was started/finished on.
     * @param month month is of type int. This is the month a route was started/finished on.
     * @param year  year is of type int. This is the year a route was started/finished on.
     * @return dateString, of type String. This a recognisable date format as a string.
     */
    private String getDateString(String day, String month, String year) {
        return day + "/" + month + "/" + year;
    }

    /**
     * Defines the hashCode function.
     * Allows for hashsets that don't collide on the same points
     * Collides if the other route has same bikeid, and start date/time
     *
     * @return A hash value
     */
    public int hashCode() {
        return new HashCodeBuilder(137, 337)
                .append(bikeid)
                .append(startYear)
                .append(startMonth)
                .append(startDay)
                .append(startTime)
                .toHashCode();
    }

    /**
     * Defines the equals functions, allowing for easy equality testing
     * Fails if the other location has same bikeid, and start date/time
     *
     * @param obj The object to compare
     * @return Equality boolean
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof Route))
            return false;
        if (obj == this)
            return true;

        Route rhs = (Route) obj;
        return new EqualsBuilder()
                .append(bikeid, rhs.getBikeID())
                .append(startYear, rhs.getStartYear())
                .append(startMonth, rhs.getStartMonth())
                .append(startDay, rhs.getStartDay())
                .append(startTime, rhs.getStartTime())
                .isEquals();
    }

}