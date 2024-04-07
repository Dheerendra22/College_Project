package com.College.Vindhya_Group_Of_Institutions;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class LectureTimeValidator {
    private static Set<String> lecturesAttendedToday = new HashSet<>();
    private static int lastDayOfMonth = -1;

    public static boolean isLectureTimeValid(String selectedLecture) {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Check if it's a new day
        if (currentDayOfMonth != lastDayOfMonth) {
            // Reset the set of attended lectures for the new day
            lecturesAttendedToday.clear();
            lastDayOfMonth = currentDayOfMonth;
        }

        // Get the current time
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Check if the lecture has already been attended today
        if (lecturesAttendedToday.contains(selectedLecture)) {
            // Lecture already attended today
            return false;
        }

        // Define the time limits for each lecture based on the selected lecture number
        TimeRange lecture1Time = new TimeRange(9, 30, 10, 20);
        TimeRange lecture2Time = new TimeRange(10, 21, 11, 10);
        TimeRange lecture3Time = new TimeRange(11, 11, 12, 0);
        TimeRange lecture4Time = new TimeRange(12, 1, 12, 50);
        TimeRange lecture5Time = new TimeRange(13, 31, 14, 20);
        TimeRange lecture6Time = new TimeRange(14, 21, 15, 10);

        // Check the selected lecture number and validate against its corresponding time range
        boolean isTimeValid ;
        switch (selectedLecture) {
            case "Lecture 1":
                isTimeValid = lecture1Time.isWithinRange(hourOfDay, minute);
                break;
            case "Lecture 2":
                isTimeValid = lecture2Time.isWithinRange(hourOfDay, minute);
                break;
            case "Lecture 3":
                isTimeValid = lecture3Time.isWithinRange(hourOfDay, minute);
                break;
            case "Lecture 4":
                isTimeValid = lecture4Time.isWithinRange(hourOfDay, minute);
                break;
            case "Lecture 5":
                isTimeValid = lecture5Time.isWithinRange(hourOfDay, minute);
                break;
            case "Lecture 6":
                isTimeValid = lecture6Time.isWithinRange(hourOfDay, minute);
                break;
            // Add more cases for other lectures as needed
            default:
                // Default to false if the selected lecture is not recognized
                isTimeValid = false;
                break;
        }

        // If lecture time is valid, add it to the set of attended lectures for today
        if (isTimeValid) {
            lecturesAttendedToday.add(selectedLecture);
        }

        return isTimeValid;
    }

    // Helper class to represent a time range
    private static class TimeRange {
        private final int startHour;
        private final int startMinute;
        private final int endHour;
        private final int endMinute;

        public TimeRange(int startHour, int startMinute, int endHour, int endMinute) {
            this.startHour = startHour;
            this.startMinute = startMinute;
            this.endHour = endHour;
            this.endMinute = endMinute;
        }

        public boolean isWithinRange(int hour, int minute) {
            if (hour > startHour && hour < endHour) {
                return true;
            } else if (hour == startHour && minute >= startMinute) {
                return true;
            } else return hour == endHour && minute <= endMinute;
        }
    }
}
