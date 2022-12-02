import core.Line;
import core.Station;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    private static StationIndex stationIndex;
    private static final String DATA_FILE = "json/map.json";

    public static void main(String[] args) {

        String jsonMetroMap = getJsonFile();
        stationIndex = new StationIndex();

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(jsonMetroMap);

            JSONArray linesArray = (JSONArray) jsonData.get("lines");
            System.out.println(linesArray);
            parseLines(linesArray);

            JSONObject stationsObject = (JSONObject) jsonData.get("stations");
            System.out.println(stationsObject);
            parseStations(stationsObject);

            JSONArray connectionsArray = (JSONArray) jsonData.get("connections");
            System.out.println(connectionsArray);


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void parseStations(JSONObject stationsObject) {

        stationsObject.keySet().forEach(lineNumberObject ->
        {
            int lineNumber = Integer.parseInt((String) lineNumberObject);
            Line line = stationIndex.getLine(lineNumber);
            JSONArray stationsArray = (JSONArray) stationsObject.get(lineNumberObject);
            stationsArray.forEach(stationObject ->
            {
                Station station = new Station((String) stationObject, line);
                stationIndex.addStation(station);
                line.addStation(station);
            });
        });
    }

    private static void parseLines(JSONArray linesArray) {
        for (Object o : linesArray) {
            JSONObject jo = (JSONObject) o;
            int lineNumber = ((Long) jo.get("number")).intValue();
            String lineName = jo.get("name").toString();
            stationIndex.addLine(new Line(lineNumber, lineName));
        }
    }

    private static String getJsonFile() {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(DATA_FILE));
            lines.forEach(builder::append);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }
}
