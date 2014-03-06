package org.osiam.tests.stress;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class DataStorage {

    public static void storeData(Metric metricData) throws IOException{
        
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("metricData.csv", true)));
        
        out.println(getPrintLine(metricData));
        out.close();
    }
    
    private static String getPrintLine(Metric metricData){
        StringBuilder line = new StringBuilder();
        
        line.append(new Date()).append(", ")
        ;
        return line.toString();
    }
    
}
