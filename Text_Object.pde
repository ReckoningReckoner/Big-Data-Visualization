/**
 * Draws a circle with text on screen
 * Each circle represents a different call for the seattle 911 dept.
 * The radius of each circle corresponds with the number of calls
 * Lines are drawn from each circle, corresponding with the crimes that occured on a certain date
 *
 * @author Viraj Bangari
 * @version 2.0
 * @since Jan 09 2015
 */
class TextObject {
   ArrayList<Line> l;
   PVector location;
   String text;
   PVector radius;
   color c;


   /**
    * Contructs button object
    *
    * @param location_ The location of the circle
    * @param text_ The text to be displayed
    * @param radius_ The radius of the circle 
    * @param c_ The color of the circle
    */
   TextObject(PVector location_, String text_, PVector radius_, color c_) {
      l = new ArrayList<Line>();
      location = location_;
      text = text_;
      c = c_;
      radius = radius_;
   }

   /**
    * Runs other methods found in class
    */
   void run() {
      display();
      for (Line i : l) {
         i.display(location);
      }
   }

   /**
    * Changes the radius of circle
    *
    * @param newRadius the new radius of the circle
    */
   void updateRadius(PVector newRadius) {
      radius.set(newRadius);
   }

   /**
    * Displays circle on screen
    */
   void display() {
      noStroke();
      fill(c);
      rectMode(RADIUS);
      ellipse(location.x, location.y, radius.x, radius.y);
   }

   /**
    * Displays text in the center of circle
    */
   void label() {
      fill(#F2F2F2);
      textAlign(CENTER, CENTER);
      text(text, location.x, location.y);
   }

   /** 
    * Makes a line object
    * Each line object is connected to the originating text object
    * The seecond point corresponds with the hour of the crime 
    *
    * @param time The time string to send to the line
    
    */
   void makeLine(String time) {
      l.add(new Line(time, location, c));
   }

   /**
    * Checks is mouse is over the button
    *
    * @return true if mouse is over the button
    */
   boolean buttonPressed() {
      if (mouseX >= location.x-radius.x && mouseX <= location.x+radius.x && mouseY >= location.y-radius.y && mouseY<=location.y+radius.y) {
         return true;
      } else {
         return false;
      }
   }
}

