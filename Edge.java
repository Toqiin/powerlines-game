
import java.util.*;

// class for Utility functions
class Utils {
  
  // finds the representative of the node in the given map
  public ArrayList<Integer> find(HashMap<ArrayList<Integer>,
      ArrayList<Integer>> map, ArrayList<Integer> node) {
    if (map.get(node).equals(node)) {
      return node;
    } else {
      return find(map, map.get(node));
    }
  }
  
  // joins the two nodes to have the same rep
  public void union(HashMap<ArrayList<Integer>,
      ArrayList<Integer>> map, ArrayList<Integer> node1, ArrayList<Integer> node2) {
    map.put(node1, node2);
  }
  
  // gets the ration of edges in the given direction in the given list
  public double edgeRatio(String dir, ArrayList<Edge> arr) {
    double total = arr.size();
    double num = 0.0;
    if (dir.equals("horizontal")) {
      for (Edge edge : arr) {
        //System.out.println("edge");
        if (edge.toNode.row == edge.fromNode.row) {
          num++;
        }
      }
      double result = num / total;
      return result;
    } else if (dir.equals("vertical")) {
      for (Edge edge : arr) {
        if (edge.toNode.col == edge.fromNode.col) {
          num++;
        }
      }
      double result = num / total;
      return result;
    } else {
      System.out.println("neither");
      return 0.0;
    }
  }
  
}

// represents edges in the square game
class Edge {
  GamePiece fromNode;
  GamePiece toNode;
  int weight;
  
  Edge(GamePiece f, GamePiece t, int w) {
    this.fromNode = f;
    this.toNode = t;
    this.weight = w;
  }

}

// compares weights of edges for the sqaure game
class WeightComparator implements Comparator<Edge> {
  public int compare(Edge a, Edge b) {
    if (a.weight < b.weight) {
      return -1;
    } else if (a.weight > b.weight) {
      return 1;
    } else {
      return 0;
    }
  }
}
