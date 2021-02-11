import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import javalib.worldimages.Posn;

// class for utility functions
class UtilsHex {
  
  // finds the representative of the given node in the given hashmap
  public ArrayList<Integer> find(HashMap<ArrayList<Integer>,
      ArrayList<Integer>> map, ArrayList<Integer> node) {
    if (map.get(node).equals(node)) {
      return node;
    } else {
      return new UtilsHex().find(map, map.get(node));
    }
  }
  
  // joins the two nodes in the map so they have the same representative
  public void union(HashMap<ArrayList<Integer>,
      ArrayList<Integer>> map, ArrayList<Integer> node1, ArrayList<Integer> node2) {
    map.put(node1, node2);
  }
  
  // returns the posn in the list thats closest to the given posn
  public Posn closest(ArrayList<Posn> arr, Posn click) {
    int index = 0;
    double distance = Math.hypot(click.x - arr.get(0).x, click.y - arr.get(0).y);
    for (int i = 0; i < arr.size(); i++) {
      if (Math.hypot(click.x - arr.get(i).x, click.y - arr.get(i).y) < distance) {
        distance = Math.hypot(click.x - arr.get(i).x, click.y - arr.get(i).y);
        index = i;
      }
    }
    return arr.get(index);
  }
  
  // returns a double representing the ratio of edges in the given direction 
  // to the total edges
  public double edgeRatio(String dir, ArrayList<EdgeHex> arr) {
    double total = arr.size();
    double num = 0.0;
    if (dir.equals("horizontal")) {
      for (EdgeHex edge : arr) {
        //System.out.println("edge");
        if (edge.toNode.row == edge.fromNode.row) {
          num++;
        }
      }
      double result = num / total;
      return result;
    } else if (dir.equals("up right")) {
      for (EdgeHex edge : arr) {
        if (edge.fromNode.row % 2 == 0) {
          if (edge.fromNode.row == edge.toNode.row - 1 && edge.fromNode.col == edge.toNode.col) {
            num ++;
          }
        } else {
          if (edge.fromNode.row == edge.toNode.row - 1
              && edge.fromNode.col - 1 == edge.toNode.col) {
            num ++;
          }
        }
      }
      double result = num / total;
      return result;
    } else if (dir.equals("up left")) {
      for (EdgeHex edge : arr) {
        if (edge.fromNode.row % 2 == 0) {
          if (edge.fromNode.row == edge.toNode.row - 1
              && edge.fromNode.col == edge.toNode.col - 1) {
            num ++;
          }
        } else {
          if (edge.fromNode.row == edge.toNode.row - 1 && edge.fromNode.col == edge.toNode.col) {
            num ++;
          }
        }
      }
      double result = num / total;
      return result;
    } else {
      return -1.0;
    }
  }
  
}

// Class to represent an edge in a hex lightemall
class EdgeHex {
  GamePieceHex fromNode;
  GamePieceHex toNode;
  int weight;
  
  EdgeHex(GamePieceHex f, GamePieceHex t, int w) {
    this.fromNode = f;
    this.toNode = t;
    this.weight = w;
  }  
}

// Class to compare the weights of two edges
class WeightComparatorHex implements Comparator<EdgeHex> {
  // compares the weights of two edges to be used for sorting
  public int compare(EdgeHex a, EdgeHex b) {
    if (a.weight < b.weight) {
      return -1;
    } else if (a.weight > b.weight) {
      return 1;
    } else {
      return 0;
    }
  }
}