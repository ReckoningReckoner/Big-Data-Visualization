/**
 * Draws a rectangular button with text on screen
 * @author Viraj Bangari
 * @version 2.0
 * @since Jan 01 2015
 */
class Button extends TextObject {
   /**
    * Contructs button object
    *
    * @param location_ The location of the button
    * @param text_ The text to be displayed
    * @param radius_ The radius of the button 
    * @param c_ The color of the button
    */
   Button(PVector location_, String text_, PVector radius_, color c_) {
      super(location_, text_, radius_, c_);
   }

   /**
    * Runs other methods found in class
    */
   void run() {
      display();
      label();
   }

   /**
    * Displays the button on screen
    */
   void display() {
      if (buttonPressed()) {
         fill(#8C8D8E);
      } else {
         fill(#DCDEE3);
      }
      noStroke();
      rectMode(RADIUS);
      rect(location.x, location.y, radius.x, radius.y, 10.0);
   }

   /**
    * Displays text on the button
    */
   void label() {
      fill(0);
      textAlign(CENTER, CENTER);
      text(text, location.x, location.y);
   }
}

