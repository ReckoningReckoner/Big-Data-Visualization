/**
 * Distinguishes between the daylight and night hours
 * Uses forumlae to approximetely find the time of sunrise and sunset
 * Formulae are derived from the data at http://www.gaisma.com/en/location/seattle-washington.html
 *
 * @author Viraj Bangari
 * @version 1.0
 * @since Jan 12 2015
 */
class LightCycle {
   float sunrise;
   float sunset;

   /**
    * Finds the sunrise and sunset times, and then assigns it to a variable
    *
    * @param date The date to find tne sunrise/sunset times
    */
   LightCycle(String date) {
      sunrise = getSunriseTime(date);
      sunset = getSunsetTime(date);
   }

   /**
    * Returns the time of sunrise
    *
    * @param date The date that is used to find the sunrise time
    *
    * @return The time of sunrise as a decimal
    */
   float getSunriseTime(String date) {
      Date d = new Date(); // life sucks
      float x = d.monthValue(date) + d.dayValue(date)/30;
      return 1.5*cos((TWO_PI/13)*(x))+6;
   }

   /**
    * Returns the time of sunset
    *
    * @param date The date that is used to find the sunset time
    *
    * @return The time of sunset as a decimal
    */
   float getSunsetTime(String date) {
      Date d = new Date();
      float x = d.monthValue(date) + d.dayValue(date)/30;
      return -2.5*cos((TWO_PI/13)*(x))+19;
   }

   /** 
    * Displays a two trasparent rects on screen
    * The area that the rects cover represent night time hours
    * Other areas are sunlight hours
    */
   void display() {
      fill(0, 45);
      rectMode(CORNERS);
      rect(0, 0, sunrise*width/24, height);
      rect(sunset*width/24, 0, width, height);
   }
}

