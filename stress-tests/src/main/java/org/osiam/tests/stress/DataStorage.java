package org.osiam.tests.stress;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class DataStorage {

    public static void storeData(String... values) throws IOException{
        
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("metricData.csv", true)));
        
        out.println(getPrintLine(values));
        out.close();
    }
    
    private static String getPrintLine(String... values){
        StringBuilder line = new StringBuilder();
        
        line.append(new Date()).append(", ");
        
        for (String string : values) {
            line.append(string).append(", ");
        }
        return line.toString();
    }
    
}
