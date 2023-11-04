import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static void saveEvents(List<Event> events, String filename) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Event event : events) {
                bw.write(event.getDate() + "," + event.getTitle() + "," + event.getDescription());
                bw.newLine();
            }
        }
    }

    public static List<Event> loadEvents(String filename) throws IOException {
        List<Event> events = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length == 3) {
                    events.add(new Event(parts[0], parts[1], parts[2]));
                }
            }
        }
        
        return events;
    }
}
