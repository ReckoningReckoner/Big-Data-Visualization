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

void setup() {
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

void draw() {
   background(#263350);

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
      text("Press if file is modified", 3*width/50, 8.5*height/10);
   }

   if (srchBut.buttonPressed()) {
      fill(255);
      text ("Enter Search Date in MM/DD/YYYY", 47*width/50, 8.5*height/10);
   }   

   idBut.run(); 
   srchBut.run(); 
   displayInput();
}


void keyPressed() {
   inputTyper();
}

void mousePressed() {
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
void index() {
   loading = true;
   index = id.reindexFile(index);
   loading = false;
}

/**
 * Displays an animation during loading
 */
void loadingBar() {
   pushMatrix();
   translate(mouseX, mouseY);
   load+= 0.2;
   textAlign(CENTER, CENTER);
   for (int j = 0; j< 2; j++) {
      rotate(PI);
      for (int i = 0; i < 100; i++) {
         rotate(PI/50);
         if (i > 0) fill(255, 255/(100-i));
         noStroke();
         ellipse(20*cos(load), 20*sin(load), 10, 10);
      }
   }
   popMatrix();
}

/**
 * Displays text input on screen
 */
void displayInput() {
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
void inputTyper() {
   if (keyCode == BACKSPACE) {
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
void search() {
   loading = true;
   done = false;
   error = false;
   ArrayList<String[]> crimes = new ArrayList<String[]>();
   int start = millis();
   long pointer = id.getBestPointer(index, input);
   if (pointer != -1) {
      lc = new LightCycle(input);
      crimes = id.selectionSort(id.linearDescending(pointer, input));
      if (crimes.size() > 0) {
         tx.clear();
         objectMaker(crimes);
         for (int i = 0; i < crimes.size (); i++) {
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
void objectMaker(ArrayList<String[]> c) {
   println(c.size());
   if (c.size() > 1) {
      int counter = 0;
      String[] temp = {"", ""};
      for (int i = 0; i < c.size () -1; i++) {
         temp[0] = temp[1];
         temp[1] = c.get(i)[0];
         println(temp[0], temp[1]);
         if (temp[0].equals(temp[1]) == false) {
            counter = 0;
            color fill = color(random(255), random(255), random(255), 100); //adds random color
            float h = map(noise(random(0, 100)), 0, 1, height/2, height); 
            float w = map(noise(random(0, 100)), 0, 1, -width/8, 9*width/8); 
            tx.add(new TextObject(new PVector(w, h), c.get(i)[0], new PVector(0, 0), fill));
         } 
         if (tx.size() > 0) {
            counter++;
            tx.get(tx.size()-1).makeLine(c.get(i)[1]);
            tx.get(tx.size()-1).updateRadius(new PVector(20+counter, 20+counter));
         }
      }
   }
}