// Extra credit done ------------------
/*
 * 1. wires are yellow when they are closer to the powerstation, and
 * get more red as they get further away. wires outside the radius are
 * still gray.
 * 2. Display the score, which is the number of powerstation movements
 * and the number of rotations added together
 * 3. Display the time elapsed since the game was started
 * 4. Kruskals algorithm can give the wires a bias for a particular
 * direction, and you can scale how much of a bias there is
 * 
 * ---- In the other ExamplesClass --------
 * 1. Hexagon grid
 * 2. Calculate the minimum number of moves needed to solve
 * and have a lose condition if this number is exceeded,
 * display the moves left next to the time and current score
 * 3. Kruskals bias now works in all 3 directions for the hexagons
 * (horizontal, up-left, and up-right
 * 
 */

import tester.*;
import javalib.impworld.*;
import java.util.*;

import java.awt.Color;
import javalib.worldimages.*;

// class for examples/tests
class ExamplesPowerlines {
  
  // CHANGE ARGUMENT IN RUN CONFIG TO ExamplesPowerlinesHex to see the hexagon
  // board
  

  // shows image at the center of an equally-sized canvas,
  // and the text at the top of the frame is given by description

  void testBigBang(Tester t) {
    // first input is how many columns, second is how many rows
    // comment out the rotateAll method in the LightEmAll to see
    // the not rotated board, can also change the kruskals arguments for bias.
    // Both these changes are in the first LightEmAll constructor
    LightEmAll w = new LightEmAll(9, 9);
    
    // Uncommet this to see a constant unrotated board, change
    // kruskalsTest arguments to see the bias change
    /* 
    w.kruskalsTest("", 0, 3);
    w.updateConnect();
    w.getRadius();
    w.connectPower();
    */

    
    // 576 is the width of the score/timer at the top, which is not scaled with the board
    // so that the text stays clear
    int worldWidth = Math.max(w.cols * w.cellSize, 576);
    int worldHeight = w.rows * w.cellSize + w.cellSize;

    double tickRate = 1.0 / 60.0;

    w.bigBang(worldWidth, worldHeight, tickRate);
  }
  
  /*
  void testBigBang2(Tester t) {
    LightEmAll w = new LightEmAll(19, 9);
    
    // 576 is the width of the score/timer at the top, which is not scaled with the board
    // so the the text stays clear
    int worldWidth = Math.max(w.cols * w.cellSize, 576);
    int worldHeight = w.rows * w.cellSize + w.cellSize;

    double tickRate = 1.0 / 60.0;
    
    w.rotateAll();
    w.updateConnect();
    w.connectPower();
    w.bigBang(worldWidth, worldHeight, tickRate);
  }
  */


  GamePiece cell;
  WorldImage cellI;
  LightEmAll w;
  

  void initDataJ() {
    this.w = new LightEmAll(2, 2, true);
    this.cell = new GamePiece(0, 0, true, true, true, false, false);
  }
  
  void testKruskalsBias(Tester t) {
    LightEmAll w = new LightEmAll(10, 10);
    
    // First check that with no bias its roughly 50%
    w.kruskalsTest("", 0, 3);
    w.updateConnect();
    w.connectPower();
    t.checkInexact(new Utils().edgeRatio("horizontal", w.mst), 0.56, 0.05);
    w.kruskalsTest("", 0, 2);
    w.updateConnect();
    w.connectPower();
    t.checkInexact(new Utils().edgeRatio("horizontal", w.mst), 0.47, 0.05);
    
    // now it has a decent amount of bias towards horizontal edges,
    // check that the ratio of horizontal : total is closer to 1.0
    w.kruskalsTest("horizontal", 75, 3);
    w.updateConnect();
    w.connectPower();
    t.checkInexact(new Utils().edgeRatio("horizontal", w.mst), 0.87, 0.05);
    w.kruskalsTest("horizontal", 75, 2);
    w.updateConnect();
    w.connectPower();
    t.checkInexact(new Utils().edgeRatio("horizontal", w.mst), 0.85, 0.05);
    
    // now there is bias to vertical, again the ratio of horizontal : total
    // is close to 0.0, since theres more vertical edges
    w.kruskalsTest("vertical", 75, 3);
    w.updateConnect();
    w.connectPower();
    t.checkInexact(new Utils().edgeRatio("horizontal", w.mst), 0.11, 0.05);
    w.kruskalsTest("vertical", 75, 2);
    w.updateConnect();
    w.connectPower();
    t.checkInexact(new Utils().edgeRatio("horizontal", w.mst), 0.12, 0.05);
     
    // go to the extreme. Bias int over 100 means there is the maximum number of
    // biased edges possible. 0.0909 is the only ratio possible for 10x10 grid
    // with a bias over 100.
    w.kruskalsTest("vertical", 150, 2);
    w.updateConnect();
    w.connectPower();
    t.checkInexact(new Utils().edgeRatio("horizontal", w.mst), 0.0909, 0.05);
    w.kruskalsTest("horizontal", 150, 92);
    w.updateConnect();
    w.connectPower();
    t.checkInexact(new Utils().edgeRatio("vertical", w.mst), 0.0909, 0.05);
  }
  
  void testEdgeRatio(Tester t) {
    LightEmAll w = new LightEmAll(4, 4);
    w.kruskalsTest("", 0, 3);
    w.updateConnect();
    w.connectPower();
    t.checkExpect(new Utils().edgeRatio("horizontal", w.mst), 0.4);
    t.checkExpect(new Utils().edgeRatio("vertical", w.mst), 0.6);
  }
  
  void testKruskals(Tester t) {
    LightEmAll w = new LightEmAll(2, 2);
    w.kruskalsTest("", 0, 3);
    w.updateConnect();
    w.connectPower();
    t.checkExpect(w.mst, new ArrayList<Edge>(Arrays.asList(
        new Edge(w.board.get(0).get(0), w.board.get(1).get(0), 51),
        new Edge(w.board.get(0).get(1), w.board.get(1).get(1), 52),
        new Edge(w.board.get(1).get(0), w.board.get(1).get(1), 80))));
    t.checkExpect(w.board.get(0).get(0).right && w.board.get(1).get(0).left &&
        w.board.get(1).get(0).bottom && w.board.get(1).get(1).top &&
        w.board.get(1).get(1).left && w.board.get(0).get(1).right, true);
    t.checkExpect(w.board.get(0).get(0).left || w.board.get(0).get(0).top ||
        w.board.get(0).get(0).bottom || w.board.get(1).get(0).right ||
        w.board.get(1).get(0).top || w.board.get(1).get(1).bottom ||
        w.board.get(1).get(1).right || w.board.get(0).get(1).left ||
        w.board.get(0).get(1).bottom || w.board.get(0).get(1).top, false);
  }
  
  void testEdgeCompare(Tester t) {
    t.checkExpect(new WeightComparator().compare(new Edge(null, null, 3),
        new Edge(null, null, 4)), -1);
    t.checkExpect(new WeightComparator().compare(new Edge(null, null, 3),
        new Edge(null, null, 3)), 0);
    t.checkExpect(new WeightComparator().compare(new Edge(null, null, 3),
        new Edge(null, null, 2)), 1);
  }
  
  void testToList(Tester t) {
    GamePiece g1 = new GamePiece();
    g1.row = 2;
    g1.col = 3;
    t.checkExpect(g1.toList(), new ArrayList<Integer>(Arrays.asList(3, 2)));
  }
  
  
  void testConnectDir(Tester t) {
    GamePiece g1 = new GamePiece();
    g1.col = 1;
    GamePiece g2 = new GamePiece();
    g2.col = 2;
    GamePiece g3 = new GamePiece();
    g3.row = 1;
    GamePiece g4 = new GamePiece();
    g4.row = 2;
    t.checkExpect(g1.right || g2.left || g3.bottom || g4.top, false);
    g1.connectDir(g2);
    g3.connectDir(g4);
    t.checkExpect(g1.right && g2.left && g3.bottom && g4.top, true);
    g1 = new GamePiece();
    g2 = new GamePiece();
    g1.col = 1;
    g1.row = 1;
    g2.col = 3;
    g2.row = 3;
    t.checkExpect(g1.right || g2.left, false);
    g1.connectDir(g2);
    t.checkExpect(g1.right || g2.left, false);
  }
  
  void testRotateAll(Tester t) {
    LightEmAll w = new LightEmAll(2, 2, 3, false);
    t.checkExpect(w.board.get(0).get(0).top, false);
    t.checkExpect(w.board.get(0).get(0).right, false);
    t.checkExpect(w.board.get(0).get(0).bottom, true);
    t.checkExpect(w.board.get(0).get(0).left, false);
    t.checkExpect(w.board.get(1).get(0).top, false);
    t.checkExpect(w.board.get(1).get(0).right, false);
    t.checkExpect(w.board.get(1).get(0).bottom, true);
    t.checkExpect(w.board.get(1).get(0).left, false);
    t.checkExpect(w.board.get(0).get(1).top, true);
    t.checkExpect(w.board.get(0).get(1).right, true);
    t.checkExpect(w.board.get(0).get(1).bottom, false);
    t.checkExpect(w.board.get(0).get(1).left, false);
    t.checkExpect(w.board.get(1).get(1).top, true);
    t.checkExpect(w.board.get(1).get(1).right, false);
    t.checkExpect(w.board.get(1).get(1).bottom, false);
    t.checkExpect(w.board.get(1).get(1).left, true);
    w.rotateAllTest();
    t.checkExpect(w.board.get(0).get(0).top, true);
    t.checkExpect(w.board.get(0).get(0).right, false);
    t.checkExpect(w.board.get(0).get(0).bottom, false);
    t.checkExpect(w.board.get(0).get(0).left, false);
    t.checkExpect(w.board.get(1).get(0).top, true);
    t.checkExpect(w.board.get(1).get(0).right, false);
    t.checkExpect(w.board.get(1).get(0).bottom, false);
    t.checkExpect(w.board.get(1).get(0).left, false);
    t.checkExpect(w.board.get(0).get(1).top, false);
    t.checkExpect(w.board.get(0).get(1).right, false);
    t.checkExpect(w.board.get(0).get(1).bottom, true);
    t.checkExpect(w.board.get(0).get(1).left, true);
    t.checkExpect(w.board.get(1).get(1).top, false);
    t.checkExpect(w.board.get(1).get(1).right, true);
    t.checkExpect(w.board.get(1).get(1).bottom, true);
    t.checkExpect(w.board.get(1).get(1).left, false);
    
  }
  
  void testCancelConnect(Tester t) {
    LightEmAll w = new LightEmAll(9, 9);
    w.genBoard3();
    w.updateConnect();
    w.getRadius();
    w.connectPower();
    for (ArrayList<GamePiece> arr : w.board) {
      for (GamePiece gp : arr) {
        t.checkExpect(gp.powered, true);
        t.checkExpect(gp.connected(), true);
      }
    }
    w.cancelConnect();
    for (ArrayList<GamePiece> arr : w.board) {
      for (GamePiece gp : arr) {
        t.checkExpect(gp.powered, false);
        t.checkExpect(gp.distance, 0);
        t.checkExpect(gp.connected(), false);
      }
    }
  }
  
  void testGenBoard3Help(Tester t) {
    LightEmAll w = new LightEmAll(1, 1, true);
    w.genBoard3Help(0, 0, 0, 0);
    w.removeConnect2();
    w.cancelConnect();
    t.checkExpect(w.board,
        new ArrayList<ArrayList<GamePiece>>(Arrays.asList(
            new ArrayList<GamePiece>(Arrays.asList(
                new GamePiece(0, 0, false, false, false, false, true))))));
    w = new LightEmAll(2, 2, "test");
    //w.clearBoard();
    w.genBoard3Help(0, 1, 0, 1);
    w.removeConnect2();
    w.cancelConnect();
    t.checkExpect(w.board,
        new ArrayList<ArrayList<GamePiece>>(Arrays.asList(
            new ArrayList<GamePiece>(Arrays.asList(
                new GamePiece(0, 0, false, false, false, true, false),
                    new GamePiece(1, 0, false, true, true, false))),
                new ArrayList<GamePiece>(Arrays.asList(
                    new GamePiece(0, 1, false, false, false, true, true),
                        new GamePiece(1, 1, true, false, true, false))))));
    w = new LightEmAll(3, 3, "test");
    //w.clearBoard();
    w.genBoard3Help(0, 2, 0, 2);
    w.removeConnect2();
    w.cancelConnect();
    t.checkExpect(w.board,
        new ArrayList<ArrayList<GamePiece>>(Arrays.asList(
            new ArrayList<GamePiece>(Arrays.asList(
                new GamePiece(0, 0, false, false, false, true, false),
                    new GamePiece(1, 0, false, true, true, true),
                    new GamePiece(2, 0, false, true, true, false))),
                new ArrayList<GamePiece>(Arrays.asList(
                    new GamePiece(0, 1, false, false, false, true, true),
                        new GamePiece(1, 1, true, false, true, false),
                        new GamePiece(2, 1, true, true, false, false))),
                new ArrayList<GamePiece>(Arrays.asList(
                    new GamePiece(0, 2, false, false, false, true, false),
                        new GamePiece(1, 2, false, false, true, true),
                        new GamePiece(2, 2, true, false, true, false))))));
  }
  
  void testGenBoard3(Tester t) {
    LightEmAll w = new LightEmAll(1, 1, true);
    w.genBoard3();
    w.cancelConnect();
    t.checkExpect(w.board,
        new ArrayList<ArrayList<GamePiece>>(Arrays.asList(
            new ArrayList<GamePiece>(Arrays.asList(
                new GamePiece(0, 0, false, false, false, false, true))))));
    w = new LightEmAll(2, 2);
    w.genBoard3();
    w.cancelConnect();
    t.checkExpect(w.board,
        new ArrayList<ArrayList<GamePiece>>(Arrays.asList(
            new ArrayList<GamePiece>(Arrays.asList(
                new GamePiece(0, 0, false, false, false, true, false),
                    new GamePiece(1, 0, false, true, true, false))),
                new ArrayList<GamePiece>(Arrays.asList(
                    new GamePiece(0, 1, false, false, false, true, true),
                        new GamePiece(1, 1, true, false, true, false))))));
    w = new LightEmAll(3, 3);
    w.genBoard3();
    w.cancelConnect();
    t.checkExpect(w.board,
        new ArrayList<ArrayList<GamePiece>>(Arrays.asList(
            new ArrayList<GamePiece>(Arrays.asList(
                new GamePiece(0, 0, false, false, false, true, false),
                    new GamePiece(1, 0, false, true, true, true),
                    new GamePiece(2, 0, false, true, true, false))),
                new ArrayList<GamePiece>(Arrays.asList(
                    new GamePiece(0, 1, false, false, false, true, true),
                        new GamePiece(1, 1, true, false, true, false),
                        new GamePiece(2, 1, true, true, false, false))),
                new ArrayList<GamePiece>(Arrays.asList(
                    new GamePiece(0, 2, false, false, false, true, false),
                        new GamePiece(1, 2, false, false, true, true),
                        new GamePiece(2, 2, true, false, true, false))))));
  }
  
  void testOnKey(Tester t) {
    this.initDataJ();
    this.w.updateConnect();
    this.w.connectPower();
    t.checkExpect(this.w.board.get(0).get(0).powerStation, true);
    t.checkExpect(this.w.board.get(1).get(0).powerStation, false);
    t.checkExpect(this.w.powerColInd, 0);
    t.checkExpect(this.w.powerRowInd, 0);
    this.w.onKeyEvent("right");
    t.checkExpect(this.w.board.get(0).get(0).powerStation, false);
    t.checkExpect(this.w.board.get(1).get(0).powerStation, true);
    t.checkExpect(this.w.powerColInd, 1);
    t.checkExpect(this.w.powerRowInd, 0);
    this.w.onKeyEvent("left");
    t.checkExpect(this.w.board.get(0).get(0).powerStation, true);
    t.checkExpect(this.w.board.get(1).get(0).powerStation, false);
    t.checkExpect(this.w.powerColInd, 0);
    t.checkExpect(this.w.powerRowInd, 0);
    this.w.onKeyEvent("down");
    t.checkExpect(this.w.board.get(0).get(0).powerStation, false);
    t.checkExpect(this.w.board.get(0).get(1).powerStation, true);
    t.checkExpect(this.w.powerColInd, 0);
    t.checkExpect(this.w.powerRowInd, 1);
    this.w.onKeyEvent("right");
    t.checkExpect(this.w.board.get(1).get(1).powerStation, false);
    t.checkExpect(this.w.board.get(0).get(1).powerStation, true);
    t.checkExpect(this.w.powerColInd, 0);
    t.checkExpect(this.w.powerRowInd, 1);
    this.w.onKeyEvent("up");
    t.checkExpect(this.w.board.get(0).get(0).powerStation, true);
    t.checkExpect(this.w.board.get(0).get(1).powerStation, false);
    t.checkExpect(this.w.powerColInd, 0);
    t.checkExpect(this.w.powerRowInd, 0);
  }
  
  void testOnTick(Tester t) {
    this.initDataJ();
    t.checkExpect(this.w.tick, 0);
    t.checkExpect(this.w.time, 0);
    this.w.onTick();
    t.checkExpect(this.w.tick, 1);
    t.checkExpect(this.w.time, 0);
    this.w.tick = 1000000;
    this.w.onTick();
    t.checkExpect(this.w.tick, 1000000);
    this.w.tick = 59;
    this.w.time = 0;
    this.w.onTick();
    t.checkExpect(this.w.time, 1);
  }
  
  void testGetRadius(Tester t) {
    LightEmAll w = new LightEmAll(2, 2, true);
    w.updateConnect();
    w.connectPower();
    w.getRadius();
    t.checkExpect(w.radius, 2);
    LightEmAll w1 = new LightEmAll(9, 9, true);
    w1.updateConnect();
    w1.connectPower();
    w1.getRadius();
    t.checkExpect(w1.radius, 9);
  }

  void testFurthestNode(Tester t) {
    LightEmAll w = new LightEmAll(9, 9, true);
    w.updateConnect();
    t.checkExpect(w.board.get(4).get(4).furthestNode(), w.board.get(8).get(0));
    t.checkExpect(w.board.get(8).get(0).furthestNode(), w.board.get(0).get(0));
  }

  void testFurthestDistance(Tester t) {
    LightEmAll w = new LightEmAll(9, 9, true);
    w.updateConnect();
    t.checkExpect(w.board.get(8).get(0).furthestDistance(), 16);
    w = new LightEmAll(2, 2, true);
    w.updateConnect();
    t.checkExpect(w.board.get(0).get(1).furthestDistance(), 3);
  }
  
  void testRemoveConnect2(Tester t) {
    LightEmAll w = new LightEmAll(1, 1, true);
    w.board = new ArrayList<ArrayList<GamePiece>>(Arrays.asList(new
        ArrayList<GamePiece>(Arrays.asList(new GamePiece(0, 0, true, true, true, true)))));
    w.updateConnect();
    t.checkExpect(w.board.get(0).get(0).top, true);
    t.checkExpect(w.board.get(0).get(0).right, true);
    t.checkExpect(w.board.get(0).get(0).bottom, true);
    t.checkExpect(w.board.get(0).get(0).left, true);
    w.removeConnect2();
    t.checkExpect(w.board.get(0).get(0).top, false);
    t.checkExpect(w.board.get(0).get(0).right, false);
    t.checkExpect(w.board.get(0).get(0).bottom, false);
    t.checkExpect(w.board.get(0).get(0).left, false);
  }
  
  void testReal(Tester t) {
    t.checkExpect(new GamePiece().real(), false);
    t.checkExpect(new GamePiece(1, 1, false, false, false, false).real(), true);
    w = new LightEmAll(1, 1);
    t.checkExpect(w.board.get(0).get(0).neighbors.get(0).real(), false);
  }
  
  void testLocToString(Tester t) {
    GamePiece gp = new GamePiece(2, 3, true, true, true, true);
    t.checkExpect(gp.locToString(), "3 2");
  }
  
  void testBfsPower(Tester t) {
    this.initDataJ();
    this.w.updateConnect();
    t.checkExpect(this.w.board.get(0).get(0).powered, false);
    t.checkExpect(this.w.board.get(0).get(0).distance, 0);
    t.checkExpect(this.w.board.get(0).get(1).powered, false);
    t.checkExpect(this.w.board.get(0).get(1).distance, 0);
    t.checkExpect(this.w.board.get(1).get(0).powered, false);
    t.checkExpect(this.w.board.get(1).get(0).distance, 0);
    t.checkExpect(this.w.board.get(1).get(1).powered, false);
    t.checkExpect(this.w.board.get(1).get(1).distance, 0);
    this.w.board.get(0).get(0).bfsPower();
    t.checkExpect(this.w.board.get(0).get(0).powered, true);
    t.checkExpect(this.w.board.get(0).get(0).distance, 0);
    t.checkExpect(this.w.board.get(0).get(1).powered, true);
    t.checkExpect(this.w.board.get(0).get(1).distance, 1);
    t.checkExpect(this.w.board.get(1).get(0).powered, true);
    t.checkExpect(this.w.board.get(1).get(0).distance, 1);
    t.checkExpect(this.w.board.get(1).get(1).powered, true);
    t.checkExpect(this.w.board.get(1).get(1).distance, 2);
  }
  
  void testConnectPower(Tester t) {
    this.initDataJ();
    this.w.updateConnect();
    t.checkExpect(this.w.board.get(0).get(0).powered, false);
    t.checkExpect(this.w.board.get(0).get(0).distance, 0);
    t.checkExpect(this.w.board.get(0).get(1).powered, false);
    t.checkExpect(this.w.board.get(0).get(1).distance, 0);
    t.checkExpect(this.w.board.get(1).get(0).powered, false);
    t.checkExpect(this.w.board.get(1).get(0).distance, 0);
    t.checkExpect(this.w.board.get(1).get(1).powered, false);
    t.checkExpect(this.w.board.get(1).get(1).distance, 0);
    this.w.connectPower();
    t.checkExpect(this.w.board.get(0).get(0).powered, true);
    t.checkExpect(this.w.board.get(0).get(0).distance, 0);
    t.checkExpect(this.w.board.get(0).get(1).powered, true);
    t.checkExpect(this.w.board.get(0).get(1).distance, 1);
    t.checkExpect(this.w.board.get(1).get(0).powered, true);
    t.checkExpect(this.w.board.get(1).get(0).distance, 1);
    t.checkExpect(this.w.board.get(1).get(1).powered, true);
    t.checkExpect(this.w.board.get(1).get(1).distance, 2);
  }

  void testConnect(Tester t) {
    GamePiece cell1 = new GamePiece(3, 3, true, true, true, true);
    GamePiece cell2 = new GamePiece(3, 3, true, true, true, true);
    GamePiece cell3 = new GamePiece(3, 3, false, false, false, false);
    GamePiece mt = new GamePiece();
    cell1.connect("top", cell2);
    t.checkExpect(cell1.neighbors.get(0), cell2);
    cell1.connect("top", cell3);
    t.checkExpect(cell1.neighbors.get(0), mt);
    cell1 = new GamePiece(3, 3, true, true, true, true);
    cell2 = new GamePiece(3, 3, true, true, true, true);
    cell3 = new GamePiece(3, 3, false, false, false, false);
    mt = new GamePiece();
    cell1.connect("right", cell2);
    t.checkExpect(cell1.neighbors.get(1), cell2);
    cell1.connect("right", cell3);
    t.checkExpect(cell1.neighbors.get(1), mt);
    cell1 = new GamePiece(3, 3, true, true, true, true);
    cell2 = new GamePiece(3, 3, true, true, true, true);
    cell3 = new GamePiece(3, 3, false, false, false, false);
    mt = new GamePiece();
    cell1.connect("bottom", cell2);
    t.checkExpect(cell1.neighbors.get(2), cell2);
    cell1.connect("bottom", cell3);
    t.checkExpect(cell1.neighbors.get(2), mt);
    cell1 = new GamePiece(3, 3, true, true, true, true);
    cell2 = new GamePiece(3, 3, true, true, true, true);
    cell3 = new GamePiece(3, 3, false, false, false, false);
    mt = new GamePiece();
    cell1.connect("left", cell2);
    t.checkExpect(cell1.neighbors.get(3), cell2);
    cell1.connect("left", cell3);
    t.checkExpect(cell1.neighbors.get(3), mt);
  }
  
  void testConnected2(Tester t) {
    GamePiece gp1 = new GamePiece(0, 0, true, true, true, true);
    GamePiece gp2 = new GamePiece(0, 0, true, true, false, true);
    GamePiece gp3 = new GamePiece(0, 0, true, true, true, true);
    gp2.neighbors = new ArrayList<GamePiece>(Arrays.asList(gp1, gp1, gp1, gp1));
    t.checkExpect(gp2.connected2("top", gp2.neighbors.get(0)), false);
    t.checkExpect(gp2.connected2("right", gp2.neighbors.get(1)), true);
    t.checkExpect(gp2.connected2("bottom", gp2.neighbors.get(2)), true);
    t.checkExpect(gp2.connected2("left", gp2.neighbors.get(3)), true);
    t.checkExpect(gp3.connected2("top", gp3.neighbors.get(0)), false);
    t.checkExpect(gp3.connected2("right", gp3.neighbors.get(1)), false);
    t.checkExpect(gp3.connected2("bottom", gp3.neighbors.get(2)), false);
    t.checkExpect(gp3.connected2("left", gp3.neighbors.get(3)), false);
  }
  
  void testCanGo(Tester t) {
    this.initDataJ();
    this.w.updateConnect();
    t.checkExpect(this.w.board.get(0).get(0).canGo("up"), false);
    t.checkExpect(this.w.board.get(0).get(0).canGo("left"), false);
    t.checkExpect(this.w.board.get(0).get(0).canGo("right"), true);
    t.checkExpect(this.w.board.get(0).get(0).canGo("down"), true);
  }
  
  void testGenBoard(Tester t) {
    // constructor calls genBoard
    LightEmAll w = new LightEmAll(2, 2, true);
    t.checkExpect(w.board.get(0).get(0).top, false);
    t.checkExpect(w.board.get(0).get(0).right, true);
    t.checkExpect(w.board.get(0).get(0).left, false);
    t.checkExpect(w.board.get(0).get(0).bottom, true);
    t.checkExpect(w.board.get(0).get(0).powerStation, true);
    t.checkExpect(w.board.get(0).get(1).top, true);
    t.checkExpect(w.board.get(0).get(1).right, false);
    t.checkExpect(w.board.get(0).get(1).left, false);
    t.checkExpect(w.board.get(0).get(1).bottom, false);
    t.checkExpect(w.board.get(0).get(1).powerStation, false);
    t.checkExpect(w.board.get(1).get(1).top, true);
    t.checkExpect(w.board.get(1).get(1).right, false);
    t.checkExpect(w.board.get(1).get(1).left, false);
    t.checkExpect(w.board.get(1).get(1).bottom, false);
    t.checkExpect(w.board.get(1).get(1).powerStation, false);
    t.checkExpect(w.board.get(1).get(0).top, false);
    t.checkExpect(w.board.get(1).get(0).right, false);
    t.checkExpect(w.board.get(1).get(0).left, true);
    t.checkExpect(w.board.get(1).get(0).bottom, true);
    t.checkExpect(w.board.get(1).get(0).powerStation, false);
  }
  
  void testOnMouseClicked(Tester t) {
    LightEmAll w = new LightEmAll(2, 2);
    w.genBoard();
    t.checkExpect(w.board.get(0).get(0).top, false);
    t.checkExpect(w.board.get(0).get(0).left, false);
    t.checkExpect(w.board.get(0).get(0).right, true);
    t.checkExpect(w.board.get(0).get(0).bottom, true);
    w.onMouseClicked(new Posn(1, 66));
    t.checkExpect(w.board.get(0).get(0).top, false);
    t.checkExpect(w.board.get(0).get(0).left, true);
    t.checkExpect(w.board.get(0).get(0).right, false);
    t.checkExpect(w.board.get(0).get(0).bottom, true);
    w.onMouseClicked(new Posn(1, 66));
    t.checkExpect(w.board.get(0).get(0).connected(), false);   
  }
  
  void testUpdateConnect(Tester t) {
    // constructor calls genBoard(), which calls updateConnect()
    LightEmAll w = new LightEmAll(2, 2);
    w.genBoard();
    t.checkExpect(w.board.get(0).get(0).neighbors.get(0), new GamePiece());
    t.checkExpect(w.board.get(0).get(0).neighbors.get(3), new GamePiece());
    t.checkExpect(w.board.get(0).get(0).neighbors.get(1), w.board.get(1).get(0));
    t.checkExpect(w.board.get(0).get(0).neighbors.get(2), w.board.get(0).get(1));
    t.checkExpect(w.board.get(0).get(1).neighbors.get(0), w.board.get(0).get(0));
    t.checkExpect(w.board.get(0).get(1).neighbors.get(3), new GamePiece());
    t.checkExpect(w.board.get(0).get(1).neighbors.get(1), new GamePiece());
    t.checkExpect(w.board.get(0).get(1).neighbors.get(2), new GamePiece());
    t.checkExpect(w.board.get(1).get(1).neighbors.get(0), w.board.get(1).get(0));
    t.checkExpect(w.board.get(1).get(1).neighbors.get(3), new GamePiece());
    t.checkExpect(w.board.get(1).get(1).neighbors.get(1), new GamePiece());
    t.checkExpect(w.board.get(1).get(1).neighbors.get(2), new GamePiece());
    t.checkExpect(w.board.get(1).get(0).neighbors.get(0), new GamePiece());
    t.checkExpect(w.board.get(1).get(0).neighbors.get(3), w.board.get(0).get(0));
    t.checkExpect(w.board.get(1).get(0).neighbors.get(1), new GamePiece());
    t.checkExpect(w.board.get(1).get(0).neighbors.get(2), w.board.get(1).get(1));
    // clicking on a cell rotates that cell then updates connections again
    w.onMouseClicked(new Posn(1, 66));
    w.onMouseClicked(new Posn(1, 66));
    t.checkExpect(w.board.get(0).get(0).neighbors.get(0), new GamePiece());
    t.checkExpect(w.board.get(0).get(0).neighbors.get(3), new GamePiece());
    t.checkExpect(w.board.get(0).get(0).neighbors.get(1), new GamePiece());
    t.checkExpect(w.board.get(0).get(0).neighbors.get(2), new GamePiece());
    t.checkExpect(w.board.get(0).get(1).neighbors.get(0), new GamePiece());
    t.checkExpect(w.board.get(0).get(1).neighbors.get(3), new GamePiece());
    t.checkExpect(w.board.get(0).get(1).neighbors.get(1), new GamePiece());
    t.checkExpect(w.board.get(0).get(1).neighbors.get(2), new GamePiece());
    t.checkExpect(w.board.get(1).get(1).neighbors.get(0), w.board.get(1).get(0));
    t.checkExpect(w.board.get(1).get(1).neighbors.get(3), new GamePiece());
    t.checkExpect(w.board.get(1).get(1).neighbors.get(1), new GamePiece());
    t.checkExpect(w.board.get(1).get(1).neighbors.get(2), new GamePiece());
    t.checkExpect(w.board.get(1).get(0).neighbors.get(0), new GamePiece());
    t.checkExpect(w.board.get(1).get(0).neighbors.get(3), new GamePiece());
    t.checkExpect(w.board.get(1).get(0).neighbors.get(1), new GamePiece());
    t.checkExpect(w.board.get(1).get(0).neighbors.get(2), w.board.get(1).get(1));
  }
  
  void testHandleCellClickJ(Tester t) {
    this.initDataJ();
    boolean cellR = this.cell.right;
    boolean cellL = this.cell.left;
    boolean cellB = this.cell.bottom;
    boolean cellT = this.cell.top;
    this.cell.handleCellClick();
    t.checkExpect(this.cell.left, cellB);
    t.checkExpect(this.cell.bottom, cellR);
    t.checkExpect(this.cell.right, cellT);
    t.checkExpect(this.cell.top, cellL);

    
  }
  
  void testConnected(Tester t) {
    this.initDataJ();
    t.checkExpect(this.w.board.get(1).get(0).connected(), true);
    t.checkExpect(this.w.board.get(1).get(1).connected(), true);
    this.w.board.get(1).get(1).handleCellClick();
    this.w.updateConnect();
    t.checkExpect(this.w.board.get(1).get(1).connected(), false);

  }
  
  void testDrawCellGrad(Tester t) {
    LightEmAll w = new LightEmAll(2, 2, true);
    w.powerColInd = 1;
    w.radius = 2;
    w.genBoard();
    w.updateConnect();
    w.connectPower();
    t.checkExpect(w.board.get(0).get(0).drawCellGrad(w.radius), 
        new OverlayImage(new EmptyImage(), 
            new OverlayImage(new OverlayImage(new EmptyImage(), 
                new OverlayImage(new RectangleImage(w.board.get(0).get(0).size / 2, 
                    w.board.get(0).get(0).size / 8, OutlineMode.SOLID, 
                    new Color(255, 127, 0)).movePinhole( - (w.board.get(0).get(0).size / 4), 0), 
                    new OverlayImage(new RectangleImage(w.board.get(0).get(0).size / 8, 
                        w.board.get(0).get(0).size / 2,
                        OutlineMode.SOLID, 
                        new Color(255, 127, 0)).movePinhole(0, - (w.board.get(0).get(0).size / 4)), 
                        new EmptyImage()))), 
                new FrameImage(new RectangleImage(w.board.get(0).get(0).size, 
                    w.board.get(0).get(0).size,
                    OutlineMode.SOLID, Color.DARK_GRAY)))));
  }
  
  void testDrawCell(Tester t) {
    this.initDataJ();
    this.w.updateConnect();
    this.w.connectPower();
    t.checkExpect(this.w.board.get(0).get(0).drawCell(this.w.radius), 
        new OverlayImage(new StarImage(25.6, 5, OutlineMode.SOLID, Color.CYAN), 
            new OverlayImage(new OverlayImage(new EmptyImage(), 
                new OverlayImage(new RectangleImage(
                    this.w.board.get(0).get(0).size / 2, 
                    this.w.board.get(0).get(0).size / 8, OutlineMode.SOLID, 
                    Color.yellow).movePinhole( -
                        (this.w.board.get(0).get(0).size / 4), 0), 
                    new OverlayImage(
                        new RectangleImage(this.w.board.get(0).get(0).size / 8, 
                        this.w.board.get(0).get(0).size / 2,
                        OutlineMode.SOLID, 
                        Color.YELLOW).movePinhole(0, -
                            (this.w.board.get(0).get(0).size / 4)), 
                        new EmptyImage()))), 
                new FrameImage(new RectangleImage(this.w.board.get(0).get(0).size, 
                    this.w.board.get(0).get(0).size,
                    OutlineMode.SOLID, Color.DARK_GRAY)))));

    GamePiece gp = new GamePiece();

    t.checkExpect(gp.drawCell(this.w.radius),
        new OverlayImage(new EmptyImage(), 
        new OverlayImage(new OverlayImage(new EmptyImage(), 
            new OverlayImage(new EmptyImage(), 
                new OverlayImage(new EmptyImage(), 
                    new EmptyImage()))),
            new FrameImage(new RectangleImage(gp.size, gp.size,
                        OutlineMode.SOLID, Color.DARK_GRAY)))));
    
    LightEmAll w2 = new LightEmAll(9, 9, true);
    w2.updateConnect();
    w2.connectPower();

    t.checkExpect(w2.board.get(4).get(4).drawCell(w2.radius),
        new OverlayImage(new StarImage(25.6, 5, 
        OutlineMode.SOLID, Color.CYAN), 
        new OverlayImage(new OverlayImage(
            new RectangleImage(w2.board.get(4).get(4).size / 8, 
            w2.board.get(0).get(0).size / 2,
            OutlineMode.SOLID, Color.YELLOW).movePinhole(0,
                (w2.board.get(4).get(4).size / 4)), 
            new OverlayImage(new RectangleImage(w2.board.get(0).get(0).size / 2, 
                w2.board.get(4).get(4).size / 8, OutlineMode.SOLID, 
                Color.yellow).movePinhole( - (w2.board.get(4).get(4).size / 4), 0), 
                new OverlayImage(new RectangleImage(w2.board.get(0).get(0).size / 8, 
                    w2.board.get(4).get(4).size / 2,
                    OutlineMode.SOLID, 
                    Color.YELLOW).movePinhole(0, - (w2.board.get(4).get(4).size / 4)), 
                    new RectangleImage(w2.board.get(4).get(4).size / 2, 
                        w2.board.get(4).get(4).size / 8,
                        OutlineMode.SOLID, 
                        Color.yellow).movePinhole(
                            (w2.board.get(4).get(4).size / 4), 0)))), 
            new FrameImage(new RectangleImage(w2.board.get(4).get(4).size, 
                w2.board.get(4).get(4).size,
                OutlineMode.SOLID, Color.DARK_GRAY)))));
  }
  
  void testDrawBoard(Tester t) {
    this.initDataJ();
    WorldScene w1 = this.w.getEmptyScene();
    w1.placeImageXY(this.w.board.get(0).get(0).drawCell(this.w.radius),
        (0 * this.w.cellSize) + (this.w.cellSize / 2), 
        (0 * this.w.cellSize) + 3 * (this.w.cellSize / 2));
    w1.placeImageXY(this.w.board.get(0).get(1).drawCell(this.w.radius),
        (0 * this.w.cellSize) + (this.w.cellSize / 2), 
        (1 * this.w.cellSize) + 3 * (this.w.cellSize / 2));
    w1.placeImageXY(this.w.board.get(1).get(0).drawCell(this.w.radius),
        (1 * this.w.cellSize) + (this.w.cellSize / 2), 
        (0 * this.w.cellSize) + 3 * (this.w.cellSize / 2));
    w1.placeImageXY(this.w.board.get(1).get(1).drawCell(this.w.radius),
        (1 * this.w.cellSize) + (this.w.cellSize / 2), 
        (1 * this.w.cellSize) + 3 * (this.w.cellSize / 2));

    t.checkExpect(this.w.drawBoard(this.w.getEmptyScene()), w1);
  }
  
  void testMakeScene(Tester t) {
    this.initDataJ();
    WorldScene w1 = this.w.getEmptyScene();
    w1.placeImageXY(this.w.board.get(0).get(0).drawCell(this.w.radius),
        (0 * this.w.cellSize) + (this.w.cellSize / 2), 
        (0 * this.w.cellSize) + (this.w.cellSize / 2));
    w1.placeImageXY(this.w.board.get(0).get(1).drawCell(this.w.radius),
        (0 * this.w.cellSize) + (this.w.cellSize / 2), 
        (1 * this.w.cellSize) + (this.w.cellSize / 2));
    w1.placeImageXY(this.w.board.get(1).get(0).drawCell(this.w.radius),
        (1 * this.w.cellSize) + (this.w.cellSize / 2), 
        (0 * this.w.cellSize) + (this.w.cellSize / 2));
    w1.placeImageXY(this.w.board.get(1).get(1).drawCell(this.w.radius),
        (1 * this.w.cellSize) + (this.w.cellSize / 2), 
        (1 * this.w.cellSize) + (this.w.cellSize / 2));

    t.checkExpect(this.w.makeScene(), w1);   
  }
  

}