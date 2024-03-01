package com.College.Vindhya_Group_Of_Institutions;


import java.util.Calendar;

public class LectureTimeValidator {
    public static boolean isLectureTimeValid(String selectedLecture) {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Define the time limits for each lecture based on the selected lecture number
        TimeRange lecture1Time = new TimeRange(9, 30, 10, 20);
        TimeRange lecture2Time = new TimeRange(10, 21, 11, 10);
        TimeRange lecture3Time = new TimeRange(11, 11, 12, 0);
        TimeRange lecture4Time = new TimeRange(12, 1, 12, 50);
        TimeRange lecture5Time = new TimeRange(13, 31, 14, 20);
        TimeRange lecture6Time = new TimeRange(14, 21, 15, 10);


        // Check the selected lecture number and validate against its corresponding time range
        switch (selectedLecture) {
            case "Lecture 1":
                return lecture1Time.isWithinRange(hourOfDay, minute);
            case "Lecture 2":
                return lecture2Time.isWithinRange(hourOfDay, minute);
            case "Lecture 3":
                return lecture3Time.isWithinRange(hourOfDay, minute);
            case "Lecture 4":
                return lecture4Time.isWithinRange(hourOfDay, minute);
            case "Lecture 5":
                return lecture5Time.isWithinRange(hourOfDay, minute);
            case "Lecture 6":
                return lecture6Time.isWithinRange(hourOfDay, minute);
            // Add more cases for other lectures as needed
            default:
                // Default to true if the selected lecture is not recognized
                return true;
        }
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

