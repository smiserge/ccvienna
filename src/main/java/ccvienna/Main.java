/*
 * Extract subtitles from MET / Brightcove online stream.
 */
package ccvienna;


import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class Main {

    public static void main(String[] args) {
        String curr_url_sample = "https://live.performa.intio.tv/media/986c89b3-a626-43ba-a031-f0249c7f6021/subs-eng-116.vtt";
         
        // parse base URL and token
        String subs_base_name = "subs-eng-";
        int subs_index = curr_url_sample.indexOf(subs_base_name);
        String url_base = curr_url_sample.substring(0, subs_index);
        //int qm_index = curr_url_sample.indexOf("?");
        //String bcv_url_token = curr_url_sample.substring(qm_index+1);
        
        HttpsClient client = new HttpsClient();
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter("/Users/serge/subtitles.srt");
        } catch (IOException ex) {
            //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        //URL for initial segment
        int segment_id = 1;
        int srt_id = 1; //per SRT spec its index starts at 1 
        String subs_name = subs_base_name + segment_id + ".vtt";
        String subs_url = url_base + subs_name;

        ArrayList srtRecords = new ArrayList<SrtRecord>();
        int rc = client.getContent(subs_url, srtRecords);
        while (rc == 200) {
            //System.out.println(" *******************  Segment: " + segment_id + " RC = " + rc);
            //System.out.println(segment_id);
            for (int i = 0; i < srtRecords.size(); i++) {
                SrtRecord srt = (SrtRecord) srtRecords.get(i);
                try {
                    myWriter.write("" + srt_id + "\n");
                    myWriter.write(srt.toString() + "\n");
                } catch (IOException e) {
//                 System.out.println("An error occurred.");
//                 e.printStackTrace();
                }
                System.out.println(srt_id);
                System.out.println(srt.toString());
                srt_id++;
            }
            //System.out.println(" xxx ");

            // prepare & read next segment
            srtRecords = new ArrayList<SrtRecord>();
            segment_id++;
            subs_name = subs_base_name + segment_id + ".vtt";
            subs_url = url_base + subs_name;

            rc = client.getContent(subs_url, srtRecords);
        }
        System.out.println(" *******************  finished reading. Last Segment: " + segment_id + " RC = " + rc);
        try {
            myWriter.close();
        } catch (IOException ex) {
            //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

