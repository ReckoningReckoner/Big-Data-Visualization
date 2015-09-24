import java.io.*;
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
   ArrayList < ArrayList < Long >> assignDefaultValues(ArrayList < ArrayList < Long >> index) {
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
   ArrayList < ArrayList < Long >> reindexFile(ArrayList < ArrayList < Long >> index) {
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
            println(years[0], months[0], years[1], months[1], raf.getFilePointer(), millis());
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
   long getBestPointer(ArrayList<ArrayList<Long>> index, String date) {
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
   int binarySearch2D(ArrayList<ArrayList<Long>> search, long item, int min, int max) {
      int mid = min+((max-min)/2);
      if (min > max) { //item not in array
         return -1;
      } else {
         if (item < search.get(mid).get(0)) { //item in upper part of array
            return binarySearch2D(search, item, mid+1, max);
         } else if (item > search.get(mid).get(0)) { //item in lower part of array
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
   int binarySearch1D(ArrayList<Long> search, long item, int min, int max) {
      int mid = min+((max-min)/2);
      if (mid%2 == 0) {
         mid++;
      }
      if (min > max) { //item not in array
         return -1;
      } else {
         if (item < search.get(mid)) { //item in upper part of array
            return binarySearch1D(search, item, mid+2, max);
         } else if (item > search.get(mid)) { //item in lower part of array
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
   ArrayList<String[]> linearDescending(long seeker, String search) {
      ArrayList<String[]> last = new ArrayList<String[]>();
      try {
         raf.seek(seeker);
         raf.readLine(); //skips header line
         boolean found = false;
         fill(255);
         for (int i = 0; i <= raf.length (); i++) {
            String date = d.dateOrTime(raf.readLine(), "date");
            if (d.dayValue(date) < d.dayValue(search) || d.monthValue(date) != d.monthValue(search)) {
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
   ArrayList<String[]> selectionSort(ArrayList<String[]> swap) {
      for (int i = 0; i <= swap.size ()-2; i++) { //skip last item in array
         int compare = i;
         String[] temp;
         for (int j = i + 1; j <= swap.size ()-1; j++) {
            if (swap.get(j)[0].compareTo(swap.get(compare)[0]) < 0) { //first time, it finds the smallest element in the array. All the times after that, it coninually finds the next highest element in array 
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

