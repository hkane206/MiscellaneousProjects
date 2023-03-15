import java.util.*;
import java.io.*;
/**
 * Demonstrates the calculation of shortest paths in the US Highway
 * network, showing the functionality of GraphProcessor and using
 * Visualize
 * To do: Add your name(s) as authors
 */
public class GraphDemo {
    
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to our project!");
        Scanner input = new Scanner(System.in);

        //enter names of US cities, match format of uscities.csv
        System.out.print("Start city (ex. Durham NC): ");
        String[] startCity = input.nextLine().split(" ");
        
        //Account for names with more than one word (ex Salt Lake City UT)
        String startCityFormat = startCity[0];
        for (int i = 1; i < startCity.length - 1; i++) {
            startCityFormat += " " + startCity[i];
        }
        startCityFormat += "," + startCity[startCity.length -1];

        System.out.print("End city (ex. Durham NC): ");
        String[] endCity = input.nextLine().split(" ");

        String endCityFormat = endCity[0];
        for (int i = 1; i < endCity.length - 1; i++) {
            endCityFormat += " " + endCity[i];
        }
        endCityFormat += "," + endCity[endCity.length -1];

        input.close();

        Point startPoint = new Point(0.0, 0.0);
        Point endPoint = new Point(0.0, 0.0);
        FileInputStream csv = new FileInputStream(new File("data/uscities.csv"));
        Scanner csvRead = new Scanner(csv);

        while (csvRead.hasNext()) {
            String[] cityInfo = csvRead.nextLine().split(",");
            String cityFormat = cityInfo[0] + "," + cityInfo[1];
            if (startCityFormat.equals(cityFormat)) {
                Double lat1 = Double.parseDouble(cityInfo[2]);
                Double long1 = Double.parseDouble(cityInfo[3]);
                startPoint = new Point(lat1, long1);
            }
            else if (endCityFormat.equals(cityFormat)) {
                Double lat2 = Double.parseDouble(cityInfo[2]);
                Double long2 = Double.parseDouble(cityInfo[3]);
                endPoint = new Point(lat2, long2);
            }
        }
        csvRead.close();

        if (startPoint.getLat() == 0.0 && startPoint.getLon() == 0.0) {
            throw new Exception("Starting city not found");
        }
        if (endPoint.getLat() == 0.0 && endPoint.getLon() == 0.0) {
            throw new Exception("Ending city not found");
        }

        //Find nearest point to each in uscities.csv
        //Find shortest path and its distance
        //time how long it took for program to calculate

        GraphProcessor g = new GraphProcessor();
        g.initialize(new FileInputStream(new File("data/usa.graph")));
        long startTime = System.nanoTime();

        Point closestToStart = g.nearestPoint(startPoint);
        Point closestToEnd = g.nearestPoint(endPoint);

        List<Point> route = g.route(closestToStart, closestToEnd);

        double routeDistance = g.routeDistance(route);
        Long endTime = System.nanoTime();
        Long totalTime = (endTime - startTime)/1000000;

        //Visualize
        Visualize vis = new Visualize("data/usa.vis", "images/usa.png");
        vis.drawPoint(closestToStart);
        vis.drawPoint(closestToEnd);
        vis.drawRoute(route);

        //Print required variables
        System.out.println("\nRoute Distance: " + routeDistance + " mi");
        System.out.println("Total time to get nearest points, route, and get distance: " + totalTime + " ms");
    }
}