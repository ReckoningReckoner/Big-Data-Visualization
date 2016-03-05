/**
 * Draws a line on screen connecting from the TextObject to the top of the screen
 * The top of the screen is dividied into 25 fragments
 * The location of the crime corresponds with the location of top of the screen
 *
 * @author Viraj Bangari
 * @version 1.0
 * @since Jan 11 2015
 */
 
class Line {
   PVector location;
   color c;
   float[] random;
   float timeInt;
   String time;
   
   /**
    * Gets the time as a string, finds the hours and minutes, and converts it into a decimal float
    *
    * @param time_ The string to find the hour in
    * @param location_ the location of the first point of the line
    * @param c_ the color of the line
    */
   Line(String time, PVector location_, color c_) {
      this.time = time;
      String temp[] = split(time, ":");
      if (temp[3].equals("AM")) {
         if (Float.parseFloat(temp[0]) != 12) {
            timeInt = Float.parseFloat(temp[0]) + Float.parseFloat(temp[1])/60;
         } else {
            timeInt = Float.parseFloat(temp[1])/60;
         }
      } else if (temp[3].equals("PM")) {
         if (Float.parseFloat(temp[0]) != 12) {
            timeInt = 12 +Float.parseFloat(temp[0]) + Float.parseFloat(temp[1])/60;
         } else {
            timeInt = 12 + Float.parseFloat(temp[1])/60;
         }
      }
      random = new float[2];
      random[0] = random(1);
      random[1] = random(1);
      c = c_;
      location = location_;
   }
   
   /*
    * Displays line on screen
    * First point of the line is the originating text object
    * Second point corresponds with the hour of the crime (12:00AM on left side of screen, 11:58PM on the right)
    * Uses perlin noise to curve the lines (aesthetics)
    */
   void display(PVector newLocation) {
      location.set(newLocation);
      strokeWeight(2);
      stroke(c, 130);
      noFill();
      beginShape();
      curveVertex(width*noise(random[0]), height/2);
      curveVertex(location.x, location.y);
      curveVertex(timeInt*width/24, 0);
      curveVertex(height/2, width*noise(random[1]));
      endShape();
      if (int(mouseX) >=int(timeInt*width/24)-width/1000 && int(mouseX) <=int(timeInt*width/24)+width/1000 && mouseY <= height/2) {
         text(time, mouseX, mouseY);
      }
   }
}