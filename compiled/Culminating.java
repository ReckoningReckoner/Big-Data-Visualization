import processing.core.*; 
import processing.xml.*; 

import java.io.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class Culminating extends PApplet {

/**
 * Creates a vizualation of seattle 911 crime calls
 * @author Viraj Bangari
 * @version 4.0
 * @since Jan 13 2015
 */

ArrayList <ArrayList<Long>> index;
ArrayList<TextObject> tx;
Indexer id;
Button idBut;
Button srchBut;
LightCycle lc;
String input = "";
float load;
boolean error = false;
boolean done = false;
boolean loading = false;

public void setup() {
   try {
      size(1080, 620);
      id = new Indexer("/Users/Viraj/Desktop/Stuff 4.0/Google Drive/Grade 12/Compsci/Culminating/data/Seattle_Police_Department_911_Incident_Response.csv");
      idBut = new Button(new PVector(3*width/50, 9*height/10), "Index File", new PVector(40, 20), color(255));
      srchBut = new Button(new PVector(47*width/50, 9*height/10), "Search File", new PVector(40, 20), color(255));
      tx = new ArrayList<TextObject>();

      index = id.assignDefaultValues(index);
      println(index);
   } 
   catch (Exception e) {
      println(e);
   }
}

public void draw() {
   background(0xff263350);

   if (loading) loadingBar();

   if (error) text("Date not found", width/2, height/2);

   if (done) {
      lc.display();
      for (TextObject t : tx) {
         t.run();
         if (t.buttonPressed()) t.label();
      }
   }

   if (idBut.buttonPressed()) { 
      fill(255);
      text("Press if file is modified", 3*width/50, 8.5f*height/10);
   }

   if (srchBut.buttonPressed()) {
      fill(255);
      text ("Enter Search Date in MM/DD/YYYY", 47*width/50, 8.5f*height/10);
   }   

   idBut.run(); 
   srchBut.run(); 
   displayInput();
}


public void keyPressed() {
   inputTyper();
}

public void mousePressed() {
   if (idBut.buttonPressed()) {
      thread("index");
   } 
   if (srchBut.buttonPressed()) {
      thread("search");
   }
}

/**
 * Runs methods for indexing the file
 */
public void index() {
   loading = true;
   index = id.reindexFile(index);
   loading = false;
}

/**
 * Displays an animation during loading
 */
public void loadingBar() {
   pushMatrix();
   translate(mouseX, mouseY);
   load+= 0.2f;
   textAlign(CENTER, CENTER);
   for (int j = 0; j<  2; j++) {
      rotate(PI);
      for (int i = 0; i <  100; i++) {
         rotate(PI/50);
         if (i >  0) fill(255, 255/(100-i));
         noStroke();
         ellipse(20*cos(load), 20*sin(load), 10, 10);
      }
   }
   popMatrix();
}

/**
 * Displays text input on screen
 */
public void displayInput() {
   if (input != null) {
      textAlign(CENTER, CENTER);
      fill(255);
      text(input, 43*width/50, 9*height/10);
   }
}

/**
 * Recieves keyCode input from user, and saves it onto a string
 * The string is displayed in draw();
 */
public void inputTyper() {
   if (keyCode == BACKSPACE) {
      input = null;
      input = "";
   } else if (keyCode == ENTER) {
      println(input);
   } else {    
      if (keyCode >= 47 && keyCode <= 57) {
         input += key;
      }
   }
}

/**
 * Runs methods for searching csv file
 * Gets a date from user, and finds alll the crimes that happened on that day
 * Writes to an StringList with all the found crimes
 *
 * @param crimes The StringList being written to 
 * 
 */
public void search() {
   loading = true;
   done = false;
   error = false;
   ArrayList<String[]> crimes = new ArrayList<String[]>();
   int start = millis();
   long pointer = id.getBestPointer(index, input);
   if (pointer != -1) {
      lc = new LightCycle(input);
      crimes = id.selectionSort(id.linearDescending(pointer, input));
      if (crimes.size() >  0) {
         tx.clear();
         objectMaker(crimes);
         for (int i = 0; i <  crimes.size (); i++) {
            println(crimes.get(i));
         }
         done = true;
         println("time to complete:", millis()-start, "ms");
      } else {
         error = true;
      }
   } else {
      error = true;
   }
   loading = false;
}

/**
 * Creates circles for each item in crimes StringList,
 * The radius of each circle is the natural log of the number of crimes*25
 * Circles are positioned on screen based on perlin noise
 * Each circle draws a line
 *
 * @param c The StringList being read
 */
public void objectMaker(ArrayList<String[]> c) {
   println(c.size());
   if (c.size() >  1) {
      int counter = 0;
      String[] temp = {
         "", ""
      };
      for (int i = 0; i <  c.size () -1; i++) {
         temp[0] = temp[1];
         temp[1] = c.get(i)[0];
         println(temp[0], temp[1]);
         if (temp[0].equals(temp[1]) == false) {
            counter = 0;
            int fill = color(random(255), random(255), random(255), 100); //adds random color
            float h = map(noise(random(0, 100)), 0, 1, height/2, height); 
            float w = map(noise(random(0, 100)), 0, 1, -width/8, 9*width/8); 
            tx.add(new TextObject(new PVector(w, h), c.get(i)[0], new PVector(0, 0), fill));
         } 
         if (tx.size() >  0) {
            counter++;
            tx.get(tx.size()-1).makeLine(c.get(i)[1]);
            tx.get(tx.size()-1).updateRadius(new PVector(20+counter, 20+counter));
         }
      }
   }
}

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
   Button(PVector location_, String text_, PVector radius_, int c_) {
      super(location_, text_, radius_, c_);
   }

   /**
    * Runs other methods found in class
    */
   public void run() {
      display();
      label();
   }

   /**
    * Displays the button on screen
    */
   public void display() {
      if (buttonPressed()) {
         fill(0xff8C8D8E);
      } else {
         fill(0xffDCDEE3);
      }
      noStroke();
      rectMode(RADIUS);
      rect(location.x, location.y, radius.x, radius.y, 10.0f);
   }

   /**
    * Displays text on the button
    */
   public void label() {
      fill(0);
      textAlign(CENTER, CENTER);
      text(text, location.x, location.y);
   }
}

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
   public int monthValue(String args) {
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
   public int dayValue(String args) {
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
   public int yearValue(String args) {
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
   public String dateOrTime(String line, String pick) {
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
   public String crime(String line) {
      String[] column = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
      String crime = column[6];
      return crime;
   }
}


/** 
 * Contains methods for indexing the file, as well as for searching and sorting arrays
 *
 * @author Viraj Bangari
 * @version 3.0
 * @since Jan 01 2015
 */
class Indexer {
   RandomAccessFile raf;
   Date d;

   /** 
    * Indexer class uses RandomAccessFile and Date classes
    *
    * @throws Exception e
    */
   Indexer(String file) {
      try {
         raf = new RandomAccessFile(file, "r");
         d = new Date();
      } 
      catch (Exception e) {
         println(e);
      }
   }

   /** 
    * Reads index values of csv file found in text file, then returns it as an ArrayList
    * Index values are in the form of ;year, month, pointer for prev month, month, pointer for prev month...;year, month, pointer for prev month, month, pointer for prev month...
    *
    * @param index the ArrayList which the indexes need to be returned to
    *
    * @return The ArrayList with the default indexes
    *
    * @throws Exception e
    */
   public ArrayList < ArrayList < Long >> assignDefaultValues(ArrayList < ArrayList < Long >> index) {
      index = new ArrayList <ArrayList<Long>>();
      BufferedReader br = createReader("Indexes.txt");
      try {
         String temp = br.readLine();
         if (temp == null) {
            println("File not indexed properly, please press 'index file' button");
         } else {
            String[]tempSplit = split(temp, ",");
            int counter = -1;
            for (String i : tempSplit) {
               if (i.equals(";")) {
                  counter++;
                  ArrayList<Long> part = new ArrayList<Long>();
                  index.add(part);
               } else if (i.equals("")) {
                  break;
               } else  index.get(counter).add(Long.parseLong(i));
            }
         }
      }
      catch (Exception e) {
         println(e);
      }

      return index;
   }

   /**
    * Indexes every file by month
    * At every month month, return pointer position, so it can be used to speed up linear search
    * Ex. [[2014 4 1513 3 1823 2 1912 ...],[2013 12 25123...]...] 2014 is the year, 4 is fourth month, and 1513 is the pointer at which the first month can be found
    * Use in case of file changes
    * Prints the output to a textfile called Indexes.txt found in the data folder
    * Format of printed output ;year, month, pointer for month, month, pointer for month...;year, month, pointer for month, month, pointer for month...
    *
    * @param raf The file to be indexed
    *
    * @return ArrayList with year position as well as pointerPosition (in 2D array)
    *
    * @throws Exception e
    */
   public ArrayList < ArrayList < Long >> reindexFile(ArrayList < ArrayList < Long >> index) {
      PrintWriter output = createWriter("data/Indexes.txt");  
      try {
         long[] years = new long[2];
         long[] months = new long[2];
         int counter = -1;
         raf.readLine(); //skips header line
         for (int i = 0; i <= raf.length ()-1; i++) {
            long nextYear = d.yearValue(d.dateOrTime(raf.readLine(), "date")); //Extracts value of month
            long nextMonth = d.monthValue(d.dateOrTime(raf.readLine(), "date")); //Extracts value of day
            years[0] = years[1]; //makes sure years are repeated
            months[0] = months[1]; // makes sure months are repeeated
            years[1] = nextYear;
            months[1] = nextMonth;
            println(years[0], months[0], years[1], months[1], pointer, millis());
            if (years[0] != years[1]) {
               counter++; //for determining which array the month should be added to
               ArrayList <Long> part = new ArrayList <Long> ();
               part.add(years[1]);
               index.add(part);
               /* printing to text file*/
               output.print(";"); //semicolon separation between first dimensional array
               output.print(",");  //comma separation between second dimensional array
               output.print(years[1]);
               output.print(","); //comma separation between second dimensional array
            }
            if (months[0] != months[1]) {
               index.get(counter).add(months[1]);
               index.get(counter).add(raf.getFilePointer());
               /* printing to text file*/
               output.print(months[1]); 
               output.print(",");  //comma separation between second dimensional array
               output.print(raf.getFilePointer());
               output.print(",");  //comma separation between second dimensional array
            }
         }
      } 
      catch (Exception e) {
         println(e);
      }
      output.flush();
      output.close();
      return index;
   }

   /**
    * Gets an input date from the user, and then prints out a pointer that is closest to said date by month
    * Runs the binarySearch2D and binarySearch1D methods, and sends them the array to serach
    *
    * @param index The array going to be searched
    * @param date The date to be found
    *
    * @return The closesent pointer to the date by month (starting at the end of a month)
    */
   public long getBestPointer(ArrayList<ArrayList<Long>> index, String date) {
      try {
         int year = binarySearch2D(index, d.yearValue(date), 0, index.size()-1);
         if (year != -1) { //check if year is in index
            int month = binarySearch1D(index.get(year), d.monthValue(date), 0, index.get(year).size()-1);
            if (month!= -1) { //check if month is in index
               return index.get(year).get(month+1);
            } else {
               return -1; //invalid year
            }
         } else {
            return -1; //invalid month
         }
      } 
      catch (Exception e) {
         return -1;
      }
   }
   
   /** 
    * Does a binary search for the first dimension for a 2D ArrayList<ArrayList<Long>>
    * 
    * 
    * @param search The 2D ArrayList being searched
    * @param item The item being searched for
    * @param min The lower boundary to search through
    * @param max The upper boundary to serach through
    *
    * @return position of the first dimensional ArrayList in the ArrayList being searched
    */
   public int binarySearch2D(ArrayList<ArrayList<Long>> search, long item, int min, int max) {
      int mid = min+((max-min)/2);
      if (min >  max) { //item not in array
         return -1;
      } else {
         if (item <  search.get(mid).get(0)) { //item in upper part of array
            return binarySearch2D(search, item, mid+1, max);
         } else if (item >  search.get(mid).get(0)) { //item in lower part of array
            return binarySearch2D(search, item, min, mid-1);
         } else { //item has been found
            return mid;
         }
      }
   }

   /** 
    * Does a binary search through a 1D ArrayList<Long>, assuming every other element is sorted starting from the second value (index 1)
    * 
    * @param search The ArrayList being searched
    * @param item The item being searched for
    * @param min The lower boundary to search through
    * @param max The upper boundary to serach through
    *
    * @return position of item in ArrayList
    */
   public int binarySearch1D(ArrayList<Long> search, long item, int min, int max) {
      int mid = min+((max-min)/2);
      if (mid%2 == 0) {
         mid++;
      }
      if (min >  max) { //item not in array
         return -1;
      } else {
         if (item <  search.get(mid)) { //item in upper part of array
            return binarySearch1D(search, item, mid+2, max);
         } else if (item >  search.get(mid)) { //item in lower part of array
            return binarySearch1D(search, item, min, mid-2);
         } else { //item has been found
            return mid;
         }
      }
   }

   /**
    * Returns crimes that happened on specific date
    * Uses linear search
    * Reads from top to bottom of file
    * 
    * @param search The date to look for
    * @param seeker Starting pointer to search for
    * 
    * @retrun An array of strings with all crimes on that date and the time it occured
    */
   public ArrayList<String[]> linearDescending(long seeker, String search) {
      ArrayList<String[]> last = new ArrayList<String[]>();
      try {
         raf.seek(seeker);
         raf.readLine(); //skips header line
         boolean found = false;
         fill(255);
         for (int i = 0; i <= raf.length (); i++) {
            String date = d.dateOrTime(raf.readLine(), "date");
            if (d.dayValue(date) <  d.dayValue(search) || d.monthValue(date) != d.monthValue(search)) {
               break;
            }
            if (date.equals(search)) {
               found = true;
               println("Found!");
               String line = raf.readLine();
               String[] temp = new String[2];
               temp[0] = d.crime(line);
               temp[1] = d.dateOrTime(line, "time");
               last.add(temp);
               //last.append(d.crime(raf.readLine()));
            } else if (date.equals(search) != true && found) {
               println("Done");
               break;
            } else {
               println(date);
            }
         }
      } 
      catch (Exception e) {
         println(e);
      }
      return last;
   }

   /** 
    * Sorts the strings in alphabetical order;
    * Uses a selectionSort algorithim
    *
    * @param swap The array to be swapped simply renamed
    * @return swap The new sorted array, in ascending order
    */
   public ArrayList<String[]> selectionSort(ArrayList<String[]> swap) {
      for (int i = 0; i <= swap.size ()-2; i++) { //skip last item in array
         int compare = i;
         String[] temp;
         for (int j = i + 1; j <= swap.size ()-1; j++) {
            if (swap.get(j)[0].compareTo(swap.get(compare)[0]) <  0) { //first time, it finds the smallest element in the array. All the times after that, it coninually finds the next highest element in array 
               compare = j;
            }
         }
         temp = swap.get(compare); //swap items
         swap.set(compare, swap.get(i));
         swap.set(i, temp);
      }
      return swap;
   }
}

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
   public float getSunriseTime(String date) {
      Date d = new Date();
      float x = d.monthValue(date) + d.dayValue(date)/30;
      return 1.5f*cos((TWO_PI/13)*(x))+6;
   }

   /**
    * Returns the time of sunset
    *
    * @param date The date that is used to find the sunset time
    *
    * @return The time of sunset as a decimal
    */
   public float getSunsetTime(String date) {
      Date d = new Date();
      float x = d.monthValue(date) + d.dayValue(date)/30;
      return -2.5f*cos((TWO_PI/13)*(x))+19;
   }

   /** 
    * Displays a two trasparent rects on screen
    * The area that the rects cover represent night time hours
    * Other areas are sunlight hours
    */
   public void display() {
      fill(0, 45);
      rectMode(CORNERS);
      rect(0, 0, sunrise*width/24, height);
      rect(sunset*width/24, 0, width, height);
   }
}

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
   int c;
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
   Line(String time, PVector location_, int c_) {
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
   public void display(PVector newLocation) {
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
      if (PApplet.parseInt(mouseX) >=PApplet.parseInt(timeInt*width/24)-width/1000 && PApplet.parseInt(mouseX) <=PApplet.parseInt(timeInt*width/24)+width/1000 && mouseY <= height/2) {
         text(time, mouseX, mouseY);
      }
   }
}

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
   int c;


   /**
    * Contructs button object
    *
    * @param location_ The location of the circle
    * @param text_ The text to be displayed
    * @param radius_ The radius of the circle 
    * @param c_ The color of the circle
    */
   TextObject(PVector location_, String text_, PVector radius_, int c_) {
      l = new ArrayList<Line>();
      location = location_;
      text = text_;
      c = c_;
      radius = radius_;
   }

   /**
    * Runs other methods found in class
    */
   public void run() {
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
   public void updateRadius(PVector newRadius) {
      radius.set(newRadius);
   }

   /**
    * Displays circle on screen
    */
   public void display() {
      noStroke();
      fill(c);
      rectMode(RADIUS);
      ellipse(location.x, location.y, radius.x, radius.y);
   }

   /**
    * Displays text in the center of circle
    */
   public void label() {
      fill(0xffF2F2F2);
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
   public void makeLine(String time) {
      l.add(new Line(time, location, c));
   }

   /**
    * Checks is mouse is over the button
    *
    * @return true if mouse is over the button
    */
   public boolean buttonPressed() {
      if (mouseX >= location.x-radius.x && mouseX <= location.x+radius.x && mouseY >= location.y-radius.y && mouseY<=location.y+radius.y) {
         return true;
      } else {
         return false;
      }
   }
}

   static public void main(String args[]) {
      PApplet.main(new String[] { "--bgcolor=#FFFFFF", "Culminating" });
   }
}
