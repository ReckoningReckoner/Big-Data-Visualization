/** 
 * Contains methods for reading dates from file and converting them into a numerical value
 * Can also find a time associated with a date as well as 
 *
 * @author Viraj Bangari
 * @version 1.0
 * @since Dec 27 2014
 */
class Date {
   Date() {
   }

   /**
    * Returns value of month
    * Date needs to be in a MM/DD/YYYY format
    *
    * @param args Date to be compared
    *
    * @return month  
    */
   int monthValue(String args) {
      String[] date = split(args, "/");
      int Month = Integer.parseInt(date[0]);
      return Month;
   }

   /**
    * Returns value of day
    * Date needs to be in a MM/DD/YYYY format
    *
    * @param args Date to be compared
    *
    * @return The date as numerical value 
    */
   int dayValue(String args) {
      String[] date = split(args, "/");
      int Day = Integer.parseInt(date[1]);
      return Day;
   }

   /**
    * Returns value of year
    * Date needs to be in a MM/DD/YYYY format
    *
    * @param args Date to be compared
    *
    * @return Year  
    */
   int yearValue(String args) {
      String[] date = split(args, "/");
      int Year = Integer.parseInt(date[2]);
      return Year;
   }

   /**
    * Parses the CSV file, and returns either the date or time which are both 7th column
    * 
    * @param line The line currently being parsed
    * @param pick Used to determine whether to return date ("date") or time ("time")
    *
    * @return The date or time, as determined by the user. Returns null if pick constructor is invalid.
    */
   String dateOrTime(String line, String pick) {
      String[] column = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
      String[] dateAndTime = column[7].split(" ");
      String date = dateAndTime[0];
      String time = dateAndTime[1];
      if (pick.equals("date")) {
         return date;
      } else if (pick.equals("time")) {
         return time+":"+dateAndTime[2];
      } else {
         return null;
      }
   }

   /**
    * Returns the crimes found on any specific day
    *
    * @param line The lines to be parsed 
    *
    * @return The name of crime that occured
    */
   String crime(String line) {
      String[] column = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
      String crime = column[6];
      return crime;
   }
}

