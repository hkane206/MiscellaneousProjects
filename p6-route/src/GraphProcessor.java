import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.util.*;

/**
 * Models a weighted graph of latitude-longitude points
 * and supports various distance and routing operations.
 * To do: Add your name(s) as additional authors
 * @author Brandon Fain
 *
 */
public class GraphProcessor {
    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * @param file a FileInputStream of the .graph file
     * @throws Exception if file not found or error reading
     */

    private int numVertices;
    private int numEdges;
    private Point[] pointArray;
    private String[] namesArray;
    private Map<Point, Set<Point>> adjList = new HashMap<>();
    private String[] edgesNames;

    public void initialize(FileInputStream file) throws Exception {
        Scanner reader = new Scanner(file);
        String[] graphInfo = reader.nextLine().split(" ");
        numVertices = Integer.parseInt(graphInfo[0]);
        numEdges = Integer.parseInt(graphInfo[1]);

        List<Point> tempPointArray = new ArrayList<>();
        List<String> tempNameArray = new ArrayList<>();

        for (int i = 0; i < numVertices; i++) {
            String[] pointInfo = reader.nextLine().split(" ");
            String name = pointInfo[0];
            double latitude = Double.parseDouble(pointInfo[1]);
            double longitude = Double.parseDouble(pointInfo[2]);
            
            Point current = new Point(latitude, longitude);
            tempPointArray.add(current);
            tempNameArray.add(name);
            adjList.put(current, new HashSet<Point>());
        }
        int x= 0;
        pointArray = new Point[tempPointArray.size()];
        for(Point p : tempPointArray){
            pointArray[x] = p;
            x++;
        }
        x = 0;
        namesArray = new String[tempNameArray.size()];
        for(String s : tempNameArray){
            namesArray[x] = s;
            x++;
        }
        List<String> tempEdgeNameArray = new ArrayList<>();
        
        for (int i = 0; i < numEdges; i++) {
            String[] edgeInfo = reader.nextLine().split(" ");
            int uIndex = Integer.parseInt(edgeInfo[0]);
            int vIndex = Integer.parseInt(edgeInfo[1]);

            adjList.get(pointArray[uIndex]).add(pointArray[vIndex]);
            adjList.get(pointArray[vIndex]).add(pointArray[uIndex]);

            if (edgeInfo.length > 2) {
                tempEdgeNameArray.add(edgeInfo[2]);
            }
            else {
                tempEdgeNameArray.add(null);
            }
        }
        x = 0;
        edgesNames = new String[tempEdgeNameArray.size()];
        for(String s : tempNameArray){
            edgesNames[x] = s;
            x++;
        }
        reader.close();
    }

    private void getAdjList() {
        for (Point key : adjList.keySet()) {
            System.out.print(key + ": ");
            for (Point p : adjList.get(key)) {
                System.out.print(p + " ");
            }
            System.out.print("\n");
        }
    }


    /**
     * Searches for the point in the graph that is closest in
     * straight-line distance to the parameter point p
     * @param p A point, not necessarily in the graph
     * @return The closest point in the graph to p
     */
    public Point nearestPoint(Point p) {
        Point tempPoint = new Point(1000000000000000.0, 100000000000000.0);
        for (Point point : adjList.keySet()) {
            double temp = point.distance(p);
            if (temp < tempPoint.distance(p)) {
                tempPoint = point;
            }
        }
        return tempPoint;
    }


    /**
     * Calculates the total distance along the route, summing
     * the distance between the first and the second Points, 
     * the second and the third, ..., the second to last and
     * the last. Distance returned in miles.
     * @param start Beginning point. May or may not be in the graph.
     * @param end Destination point May or may not be in the graph.
     * @return The distance to get from start to end
     */
    public double routeDistance(List<Point> route) {
        double distance = 0.0;
        for (int i = 0; i < route.size() - 1; i++) {
            distance += route.get(i).distance(route.get(i+1));
        }
        return distance;
    }
    

    /**
     * Checks if input points are part of a connected component
     * in the graph, that is, can one get from one to the other
     * only traversing edges in the graph
     * @param p1 one point
     * @param p2 another point
     * @return true if p2 is reachable from p1 (and vice versa)
     */
    public boolean connected(Point p1, Point p2) {

        boolean found = false;
        for(Point p : adjList.keySet()){
            if(p.equals(p1) ){
                found = true;
            }
            if(p.equals(p2)){
                found = true;
            }
        }
        if(!found){
            return false;
        }

        List<Point> visited = new ArrayList<Point>();
        Point current;

        Stack<Point> stack = new Stack<Point>();
        visited.add(p1);
        stack.push(p1);

        while(stack.size() > 0)
        {
            current = stack.pop();
            for(Point neigh : adjList.get(current)){
                if(!visited.contains(neigh)){
                    if(neigh.equals(p2)){
                        return true;
                    }
                    visited.add(neigh);
                    stack.push(neigh);

                }

            }

        }
        return false;
    }


    /**
     * Returns the shortest path, traversing the graph, that begins at start
     * and terminates at end, including start and end as the first and last
     * points in the returned list. If there is no such route, either because
     * start is not connected to end or because start equals end, throws an
     * exception.
     * @param start Beginning point.
     * @param end Destination point.
     * @return The shortest path [start, ..., end].
     * @throws InvalidAlgorithmParameterException if there is no such route, 
     * either because start is not connected to end or because start equals end.
     */
    public List<Point> route(Point start, Point end) throws InvalidAlgorithmParameterException {

        if( !connected(start, end) || start.equals(end)){
            throw new InvalidAlgorithmParameterException("No path between start and end"); 
        }

        Map<Point, Double> route = new HashMap<>();
        Map<Point, Point> previous = new HashMap<Point, Point>();
        Comparator<Point> comp = (a, b) -> (int)(route.get(a) - route.get(b));
        PriorityQueue<Point> toExplore = new PriorityQueue<>(comp);

        Point current = start;
        route.put(current, 0.0);
        toExplore.add(current);
        //previous.add(start);

        while (!toExplore.isEmpty()) {
            current = toExplore.remove();
            if(current.equals(end)){
                break;
            }
            
            for (Point neighbor : adjList.get(current)) {
                double weight = current.distance(neighbor);

                if (!route.containsKey(neighbor) || route.get(neighbor) > route.get(current) + weight) {
                    route.put(neighbor, route.get(current) + weight);
                    previous.put(neighbor, current);
                    toExplore.add(neighbor);
                }
            }
        }

        Point done = end;
        List<Point> returnList = new ArrayList<>();
        returnList.add(done);

        while(!done.equals(start)){
            Point temp = previous.get(done);
            returnList.add(temp);
            done = temp;
        }
        returnList.add(start);
        Collections.reverse(returnList);

        return returnList;
          
    }
}
