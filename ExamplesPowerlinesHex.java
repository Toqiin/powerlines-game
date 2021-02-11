

import tester.*;
import javalib.impworld.*;
import java.util.*;

import java.awt.Color;
import javalib.worldimages.*;

// class for examples/tests
class ExamplesPowerlinesHex {
  
  // change the run config argument to ExamplesPowerlines to test the normal
  // grid with squares instead of hexagons

  void testBigBang(Tester t) {
    
    // --- UNCOMMENT FOR STANDARD GAME --------------
    
    LightEmAllHex w = new LightEmAllHex(9, 9);

    // ----------------------------------------------
    
    // --- UNCOMMENT FOR CONSTANT GAME, no rotate ---
    // change the arguments of kruskalsTest to see different bias
    /*
    LightEmAllHex w = new LightEmAllHex(9, 9);
    w.kruskalsTest("horizontal", 0, 3);
    w.updateConnect();
    w.getRadius();
    w.connectPower();
    */
    // ----------------------------------------------


    // 700 is the width of the score/timer at the top, which is not scaled with the board
    // so the the text stays clear
    int worldWidth = (int) Math.max((w.cols + 1) * w.cellSize * 1.732, 700);
    int worldHeight = (int)(w.rows * w.cellSize * 1.5) + 50 + w.cellSize;

    double tickRate = 1.0 / 60.0;

    //w.genBoard();
    //w.connectPower();
    w.bigBang(worldWidth, worldHeight, tickRate);
  }


  GamePieceHex cell;
  WorldImage cellI;
  LightEmAllHex w1;

  void initDataJ() {
    this.w1 = new LightEmAllHex(2, 2);
    this.cell = new GamePieceHex(0, 0, false, true, false, false, true, true);
  }
  
  void testToListHex(Tester t) {
    GamePieceHex gp = new GamePieceHex();
    gp.row = 3;
    gp.col = 5;
    t.checkExpect(gp.toList(), new ArrayList<Integer>(Arrays.asList(5, 3)));
  }

  void testCheckLostHex(Tester t) {
    LightEmAllHex w = new LightEmAllHex(2, 2);
    w.mostPSMoves = 2;
    w.moves = 0;
    t.checkExpect(w.checkLost(), false);
    w.mostPSMoves = 2;
    w.moves = 7;
    t.checkExpect(w.checkLost(), true);
    w.mostPSMoves = 2;
    w.moves = 2;
    t.checkExpect(w.checkLost(), false);
  }

  void testEdgeRatioHex(Tester t) {
    LightEmAllHex w = new LightEmAllHex(9, 9);
    w.kruskalsTest("", 0, 3);
    w.updateConnect();
    w.getRadius();
    w.connectPower();
    t.checkExpect(new UtilsHex().edgeRatio("horizontal", w.mst), 0.3375);
    t.checkExpect(new UtilsHex().edgeRatio("up right", w.mst), 0.35);
    t.checkExpect(new UtilsHex().edgeRatio("up left", w.mst), 0.3125);
  }

  void testKruskalsBias(Tester t) {
    LightEmAllHex w = new LightEmAllHex(9, 9);
    w.kruskalsTest("horizontal", 100, 3);
    w.updateConnect();
    w.getRadius();
    w.connectPower();
    t.checkExpect(new UtilsHex().edgeRatio("horizontal", w.mst), 0.9);
    w = new LightEmAllHex(9, 9);
    w.kruskalsTest("up right", 100, 3);
    w.updateConnect();
    w.getRadius();
    w.connectPower();
    t.checkExpect(new UtilsHex().edgeRatio("up right", w.mst), 0.85);
    w = new LightEmAllHex(9, 9);
    w.kruskalsTest("up left", 100, 3);
    w.updateConnect();
    w.getRadius();
    w.connectPower();
    t.checkExpect(new UtilsHex().edgeRatio("up left", w.mst), 0.85);
  }

  void testUpdateConnectHex(Tester t) {
    LightEmAllHex w = new LightEmAllHex(2, 2);
    w.kruskalsTest("", 0, 3);
    w.cancelConnect();
    w.updateConnect();
    t.checkExpect(w.board.get(0).get(0).neighbors.get(0), new GamePieceHex());
    t.checkExpect(w.board.get(0).get(0).neighbors.get(1), new GamePieceHex());
    t.checkExpect(w.board.get(0).get(0).neighbors.get(3), new GamePieceHex());
    t.checkExpect(w.board.get(0).get(0).neighbors.get(5), new GamePieceHex());
    t.checkExpect(w.board.get(1).get(0).neighbors.get(0), new GamePieceHex());
    t.checkExpect(w.board.get(1).get(0).neighbors.get(1), new GamePieceHex());
    t.checkExpect(w.board.get(1).get(0).neighbors.get(2), new GamePieceHex());
    t.checkExpect(w.board.get(1).get(0).neighbors.get(3), new GamePieceHex());
    t.checkExpect(w.board.get(1).get(1).neighbors.get(0), new GamePieceHex());
    t.checkExpect(w.board.get(1).get(1).neighbors.get(4), new GamePieceHex());
    t.checkExpect(w.board.get(1).get(1).neighbors.get(5), new GamePieceHex());
    t.checkExpect(w.board.get(1).get(1).neighbors.get(2), new GamePieceHex());
    t.checkExpect(w.board.get(1).get(1).neighbors.get(3), new GamePieceHex());
    t.checkExpect(w.board.get(0).get(1).neighbors.get(0), new GamePieceHex());
    t.checkExpect(w.board.get(0).get(1).neighbors.get(4), new GamePieceHex());
    t.checkExpect(w.board.get(0).get(1).neighbors.get(5), new GamePieceHex());
    t.checkExpect(w.board.get(0).get(1).neighbors.get(2), new GamePieceHex());
    t.checkExpect(w.board.get(0).get(1).neighbors.get(3), new GamePieceHex());
    t.checkExpect(w.board.get(0).get(0).neighbors.get(2), w.board.get(1).get(0));
    t.checkExpect(w.board.get(0).get(0).neighbors.get(4), w.board.get(0).get(1));
    t.checkExpect(w.board.get(1).get(0).neighbors.get(5), w.board.get(0).get(0));
    t.checkExpect(w.board.get(1).get(0).neighbors.get(4), w.board.get(1).get(1));
    t.checkExpect(w.board.get(1).get(1).neighbors.get(1), w.board.get(1).get(0));
    t.checkExpect(w.board.get(0).get(1).neighbors.get(1), w.board.get(0).get(0));
  }

  void testOnClickHex(Tester t) {
    LightEmAllHex w = new LightEmAllHex(2, 2);
    w.kruskalsTest("", 0, 3);
    w.updateConnect();
    w.connectPower();
    t.checkExpect(w.board.get(0).get(0).powered, true);
    t.checkExpect(w.board.get(0).get(0).right, true);
    t.checkExpect(w.board.get(0).get(0).bottomLeft, true);
    t.checkExpect(w.board.get(0).get(0).left, false);
    t.checkExpect(w.board.get(0).get(0).bottomRight, false);
    w.onMouseClicked(new Posn(58, 80));
    t.checkExpect(w.board.get(0).get(0).powered, false);
    t.checkExpect(w.board.get(0).get(0).right, false);
    t.checkExpect(w.board.get(0).get(0).bottomLeft, false);
    t.checkExpect(w.board.get(0).get(0).left, true);
    t.checkExpect(w.board.get(0).get(0).bottomRight, true);
    t.checkExpect(w.board.get(0).get(1).powered, false);

  }

  void testCheckWonHex(Tester t) {
    LightEmAllHex w = new LightEmAllHex(5, 5);
    for (ArrayList<GamePieceHex> arr : w.board) {
      for (GamePieceHex gp : arr) {
        gp.powered = false;
        gp.distance = 0;
      }
    }
    t.checkExpect(w.checkWon(), false);
    for (ArrayList<GamePieceHex> arr : w.board) {
      for (GamePieceHex gp : arr) {
        gp.powered = true;
        gp.distance = 0;
      }
    }
    t.checkExpect(w.checkWon(), true);
  }

  void testMakeSceneHex(Tester t) {
    LightEmAllHex w = new LightEmAllHex(2, 2);
    w.kruskalsTest("", 0, 3);
    w.updateConnect();
    w.connectPower();
    w.mostPSMoves = 0;
    w.moves = 0;
    w.time = 0;
    WorldScene scene = w.getEmptyScene();
    scene.placeImageXY(w.board.get(0).get(0).drawCellGrad(w.radius),
        0 * (int)(1.732 * 32) + 32 + (int)(32 * 1.732 / 2), (0 * (32 + 32 / 2)) + 50 + (32));
    scene.placeImageXY(w.board.get(1).get(0).drawCellGrad(w.radius),
        1 * (int)(1.732 * 32) + 32 + (int)(32 * 1.732 / 2), (0 * (32 + 32 / 2)) + 50 + (32));
    scene.placeImageXY(w.board.get(0).get(1).drawCellGrad(w.radius),
        0 * (int)(1.732 * 32) + 32, (1 * (32 + 32 / 2)) + 50 + (32));
    scene.placeImageXY(w.board.get(1).get(1).drawCellGrad(w.radius),
        1 * (int)(1.732 * 32) + 32, (1 * (32 + 32 / 2)) + 50 + (32));
    scene.placeImageXY(new TextImage("Score: " + Integer.toString(0), 36, Color.BLACK), 100, 32);
    scene.placeImageXY(new TextImage("Time: " + Integer.toString(0), 36, Color.BLACK), 275, 32);
    scene.placeImageXY(new TextImage("Moves Left: " + Integer.toString((int)(0) -
        0 + 1), 36, Color.BLACK), 500, 32);
    t.checkExpect(w.makeScene(), scene);
  }

  void testDrawBoardHex(Tester t) {
    LightEmAllHex w = new LightEmAllHex(2, 2);
    w.kruskalsTest("", 0, 3);
    w.updateConnect();
    w.connectPower();
    WorldScene scene = w.getEmptyScene();
    scene.placeImageXY(w.board.get(0).get(0).drawCellGrad(w.radius),
        0 * (int)(1.732 * 32) + 32 + (int)(32 * 1.732 / 2), (0 * (32 + 32 / 2)) + 50 + (32));
    scene.placeImageXY(w.board.get(1).get(0).drawCellGrad(w.radius),
        1 * (int)(1.732 * 32) + 32 + (int)(32 * 1.732 / 2), (0 * (32 + 32 / 2)) + 50 + (32));
    scene.placeImageXY(w.board.get(0).get(1).drawCellGrad(w.radius),
        0 * (int)(1.732 * 32) + 32, (1 * (32 + 32 / 2)) + 50 + (32));
    scene.placeImageXY(w.board.get(1).get(1).drawCellGrad(w.radius),
        1 * (int)(1.732 * 32) + 32, (1 * (32 + 32 / 2)) + 50 + (32));
    t.checkExpect(w.drawBoard(w.getEmptyScene()), scene);
  }

  void testWireColor(Tester t) {
    GamePieceHex gp = new GamePieceHex();
    gp.distance = 40;
    gp.powered = true;
    t.checkExpect(gp.wireColor(4), Color.LIGHT_GRAY);
    gp.distance = 0;
    gp.powered = false;
    t.checkExpect(gp.wireColor(4), Color.LIGHT_GRAY);
    gp.distance = 0;
    gp.powered = true;
    t.checkExpect(gp.wireColor(4), Color.yellow);
    gp.distance = 4;
    gp.powered = true;
    t.checkExpect(gp.wireColor(4), new Color(255, 0, 0));
    gp.distance = 2;
    gp.powered = true;
    t.checkExpect(gp.wireColor(4), new Color(255, 126, 0));
  }

  void testDrawCellHex(Tester t) {
    GamePieceHex gp = new GamePieceHex();
    gp.topLeft = true;
    gp.topRight = true;
    gp.right = true;
    gp.bottomRight = true;
    gp.bottomLeft = true;
    gp.left = true;
    gp.powerStation = true;
    gp.distance = 0;
    WorldImage ps = new StarImage(gp.size / 2.5, 5, OutlineMode.SOLID, Color.CYAN);
    Color c = Color.LIGHT_GRAY;
    WorldImage topLeft;
    WorldImage topRight;
    WorldImage right;
    WorldImage bottomRight;
    WorldImage bottomLeft;
    WorldImage left;
    topLeft = new RotateImage(new RectangleImage(gp.size / 8, (int)(gp.size / 2 * 1.732),
        OutlineMode.SOLID, c).movePinhole(0, (int)(gp.size / 4 * 1.732)), -30);
    topRight = new RotateImage(new RectangleImage(gp.size / 8, (int)(gp.size / 2 * 1.732),
        OutlineMode.SOLID, c).movePinhole(0, (int)(gp.size / 4 * 1.732)), 30);
    right = new RectangleImage((int)(gp.size / 2 * 1.732), gp.size / 8,
        OutlineMode.SOLID, c).movePinhole(-(int)(gp.size / 4 * 1.732), 0);
    bottomRight = new RotateImage(new RectangleImage(gp.size / 8, (int)(gp.size / 2 * 1.732) + 1,
        OutlineMode.SOLID, c).movePinhole(0, -(int)(gp.size / 4 * 1.732)), -30);
    bottomLeft = new RotateImage(new RectangleImage(gp.size / 8, (int)(gp.size / 2 * 1.732) + 1,
        OutlineMode.SOLID, c).movePinhole(0, -(int)(gp.size / 4 * 1.732)), 30);
    left = new RectangleImage((int)(gp.size / 2 * 1.732) - 1, gp.size / 8,
        OutlineMode.SOLID, c).movePinhole((int)(gp.size / 4 * 1.732), 0);
    WorldImage wires = new OverlayImage(topLeft, new OverlayImage(right, 
        new OverlayImage(bottomLeft, new OverlayImage(left,
            new OverlayImage(topRight, bottomRight)))));
    WorldImage cell = new OverlayImage(new RotateImage(new HexagonImage(gp.size,
        OutlineMode.OUTLINE, Color.BLACK), 30), new RotateImage(new HexagonImage(gp.size,
            OutlineMode.SOLID, Color.DARK_GRAY), 30));

    t.checkExpect(gp.drawCellGrad(4), new OverlayImage(ps, new OverlayImage(wires, cell)));
  }

  void testGetCenters(Tester t) {
    LightEmAllHex w = new LightEmAllHex(2, 2);
    t.checkExpect(w.centers, new ArrayList<Posn>(Arrays.asList(new Posn(59, 82), new Posn(32, 130),
        new Posn(114, 82), new Posn(87, 130))));
  }

  void testRotateAllHex(Tester t) {
    LightEmAllHex w = new LightEmAllHex(2, 2);
    w.kruskalsTest("", 0, 3);
    w.updateConnect();
    w.getRadius();
    w.connectPower();
    t.checkExpect(w.board.get(0).get(0).right, true);
    t.checkExpect(w.board.get(0).get(0).bottomLeft, true);
    t.checkExpect(w.board.get(1).get(0).bottomLeft, true);
    t.checkExpect(w.board.get(1).get(0).left, true);
    t.checkExpect(w.board.get(1).get(1).topRight, true);
    t.checkExpect(w.board.get(0).get(1).topRight, true);
    t.checkExpect(w.mostPSMoves, 0);
    w.rotateAllTest(3);
    w.updateConnect();
    w.connectPower();
    t.checkExpect(w.board.get(0).get(0).topRight, true);
    t.checkExpect(w.board.get(0).get(0).bottomRight, true);
    t.checkExpect(w.board.get(1).get(0).bottomLeft, true);
    t.checkExpect(w.board.get(1).get(0).bottomRight, true);
    t.checkExpect(w.board.get(1).get(1).topLeft, true);
    t.checkExpect(w.board.get(0).get(1).topLeft, true);
    t.checkExpect(w.mostPSMoves, 4);
    w = new LightEmAllHex(4, 4);
    w.kruskalsTest("", 0, 3);
    w.updateConnect();
    w.getRadius();
    w.connectPower();
    t.checkExpect(w.mostPSMoves, 2);
    w.rotateAllTest(3);
    w.updateConnect();
    w.connectPower();
    t.checkExpect(w.mostPSMoves, 16);
  }

  void testOnTickHex(Tester t) {
    LightEmAllHex w = new LightEmAllHex(2, 2);
    w.tick = 0;
    w.time = 0;
    t.checkExpect(w.time, 0);
    t.checkExpect(w.tick, 0);
    w.onTick();
    t.checkExpect(w.time, 0);
    t.checkExpect(w.tick, 1);
    w.tick = 59;
    w.onTick();
    t.checkExpect(w.time, 1);
    t.checkExpect(w.tick, 60);
    w.time = 999;
    w.tick = 121999;
    w.onTick();
    t.checkExpect(w.time, 999);
    t.checkExpect(w.tick, 121999);
  }

  void testOnKey(Tester t) {
    LightEmAllHex w = new LightEmAllHex(2, 2);
    w.kruskalsTest("up right", 0, 3);
    w.mostPSMoves = 100;
    w.radius = 0;
    w.updateConnect();
    w.connectPower();
    t.checkExpect(w.powerColInd, 1);
    t.checkExpect(w.powerRowInd, 0);
    w.onKeyEvent("a");
    t.checkExpect(w.powerColInd, 0);
    t.checkExpect(w.powerRowInd, 0);
    w.onKeyEvent("z");
    t.checkExpect(w.powerColInd, 0);
    t.checkExpect(w.powerRowInd, 1);
    w.onKeyEvent("e");
    t.checkExpect(w.powerColInd, 0);
    t.checkExpect(w.powerRowInd, 0);
    w.onKeyEvent("d");
    t.checkExpect(w.powerColInd, 1);
    t.checkExpect(w.powerRowInd, 0);
    w.onKeyEvent("w");
    t.checkExpect(w.powerColInd, 1);
    t.checkExpect(w.powerRowInd, 0);
    w.onKeyEvent("x");
    t.checkExpect(w.powerColInd, 1);
    t.checkExpect(w.powerRowInd, 0);
  }

  void testKruskalsHex(Tester t) {
    LightEmAllHex w = new LightEmAllHex(2, 2);
    w.kruskalsTest("", 0, 3);
    w.updateConnect();
    w.connectPower();
    t.checkExpect(w.mst, new ArrayList<EdgeHex>(Arrays.asList(
        new EdgeHex(w.board.get(0).get(0), w.board.get(1).get(0), 51),
        new EdgeHex(w.board.get(0).get(0), w.board.get(0).get(1), 52),
        new EdgeHex(w.board.get(1).get(0), w.board.get(1).get(1), 79))));
    t.checkExpect(w.board.get(0).get(0).right && w.board.get(0).get(0).bottomLeft &&
        w.board.get(1).get(0).bottomLeft && w.board.get(1).get(0).left &&
        w.board.get(0).get(1).topRight && w.board.get(1).get(1).topRight, true);
  }

  void testLastSceneHex(Tester t) {
    LightEmAllHex w = new LightEmAllHex(2, 2);
    w.kruskalsTest("", 0, 3);
    w.updateConnect();
    w.connectPower();
    w.mostPSMoves = 0;
    w.moves = 0;
    w.time = 0;
    WorldScene scene = w.makeScene();
    scene.placeImageXY(new TextImage("YOU WON", w.cellSize * 2, FontStyle.BOLD, Color.GREEN),
        (int) Math.max((w.cols + 1) * w.cellSize * 1.732, 700) / 2,
        ((int)(w.rows * w.cellSize * 1.5) + 50 + w.cellSize) / 2);
    t.checkExpect(w.lastScene("YOU WON"), scene);
  }

  void testCancelConnectHex(Tester t) {
    LightEmAllHex w = new LightEmAllHex(2, 2);
    w.cancelConnect();
    for (ArrayList<GamePieceHex> arr : w.board) {
      for (GamePieceHex gp : arr) {
        t.checkExpect(gp.neighbors, new ArrayList<GamePieceHex>(Arrays.asList(new GamePieceHex(),
            new GamePieceHex(), new GamePieceHex(), new GamePieceHex(),
            new GamePieceHex(), new GamePieceHex())));
        t.checkExpect(gp.powered, false);
        t.checkExpect(gp.distance, 0);
      }
    }
  }

  void testGetRadiusHex(Tester t) {
    LightEmAllHex w = new LightEmAllHex(2, 2);
    w.kruskalsTest("", 0, 3);
    w.updateConnect();
    w.getRadius();
    w.connectPower();
    t.checkExpect(w.radius, 2);
    t.checkExpect(w.mostPSMoves, 0);
    w = new LightEmAllHex(5, 5);
    w.kruskalsTest("", 0, 3);
    w.updateConnect();
    w.getRadius();
    w.connectPower();
    t.checkExpect(w.radius, 6);
    t.checkExpect(w.mostPSMoves, 2);
  }

  void testConnectPowerHex(Tester t) {
    LightEmAllHex w = new LightEmAllHex(2, 2);
    w.kruskalsTest("", 0, 3);
    w.updateConnect();
    w.connectPower();
    t.checkExpect(w.board.get(0).get(0).distance, 1);
    t.checkExpect(w.board.get(1).get(0).distance, 0);
    t.checkExpect(w.board.get(0).get(1).distance, 2);
    t.checkExpect(w.board.get(1).get(1).distance, 1);
    t.checkExpect(w.board.get(0).get(0).powered, true);
    t.checkExpect(w.board.get(0).get(1).powered, true);
    t.checkExpect(w.board.get(1).get(1).powered, true);
    t.checkExpect(w.board.get(1).get(0).powered, true);
    w.board.get(0).get(0).handleCellClick();
    w.updateConnect();
    w.connectPower();
    t.checkExpect(w.board.get(0).get(0).distance, 0);
    t.checkExpect(w.board.get(1).get(0).distance, 0);
    t.checkExpect(w.board.get(0).get(1).distance, 0);
    t.checkExpect(w.board.get(1).get(1).distance, 1);
    t.checkExpect(w.board.get(0).get(0).powered, false);
    t.checkExpect(w.board.get(0).get(1).powered, false);
    t.checkExpect(w.board.get(1).get(1).powered, true);
    t.checkExpect(w.board.get(1).get(0).powered, true);
  }

  void testUtils(Tester t) {
    this.initDataJ();
    UtilsHex uh = new UtilsHex();
    HashMap<ArrayList<Integer>, ArrayList<Integer>> hm = 
        new HashMap<ArrayList<Integer>, ArrayList<Integer>>();
    hm.put(new ArrayList<Integer>(Arrays.asList(2, 3)), 
        new ArrayList<Integer>(Arrays.asList(2, 3)));
    t.checkExpect(new ArrayList<Integer>(Arrays.asList(2, 3)), 
        uh.find(hm, new ArrayList<Integer>(Arrays.asList(2, 3))));

    this.initDataJ();
    uh.union(hm, new ArrayList<Integer>(Arrays.asList(3, 4)), 
        new ArrayList<Integer>(Arrays.asList(2, 3)));
    t.checkExpect(hm.get(new ArrayList<Integer>(Arrays.asList(3, 4))), 
        new ArrayList<Integer>(Arrays.asList(2, 3)));
    t.checkExpect(uh.closest(this.w1.centers, new Posn(55, 90)), new Posn(59, 82));
    t.checkExpect(uh.closest(this.w1.centers, new Posn(200, 200)), new Posn(87, 130));

  }

  void testHexCompare(Tester t) {
    WeightComparatorHex wch = new WeightComparatorHex();
    t.checkExpect(wch.compare(new EdgeHex(this.cell, this.cell, 40), 
        new EdgeHex(this.cell, this.cell, 50)), -1);
    t.checkExpect(wch.compare(new EdgeHex(this.cell, this.cell, 40), 
        new EdgeHex(this.cell, this.cell, 30)), 1);
    t.checkExpect(wch.compare(new EdgeHex(this.cell, this.cell, 40), 
        new EdgeHex(this.cell, this.cell, 40)), 0);

  }

  void testCellClick(Tester t) {
    this.initDataJ();
    GamePieceHex temp = this.cell;
    this.cell.handleCellClick();
    t.checkExpect(this.cell.left, temp.bottomLeft);
    t.checkExpect(this.cell.bottomLeft, temp.bottomRight);
    t.checkExpect(this.cell.right, temp.topRight);
    t.checkExpect(this.cell.topRight, temp.topLeft);
  }

  void testConnected(Tester t) {
    this.initDataJ();
    t.checkExpect(this.w1.board.get(0).get(0).connected(), true);
    t.checkExpect(this.w1.board.get(0).get(1).connected(), true);
    t.checkExpect(this.w1.board.get(1).get(0).connected(), true);
    t.checkExpect(this.w1.board.get(1).get(1).connected(), true);
  }

  void testColor(Tester t) {
    this.initDataJ();
    w1.kruskalsTest("", 0, 3);
    w1.updateConnect();
    w1.getRadius();
    w1.connectPower();
    t.checkExpect(this.w1.board.get(0).get(0).wireColor(32), new Color(255, 217, 0));
    t.checkExpect(this.w1.board.get(0).get(1).wireColor(32), new Color(255, 210, 0));
    t.checkExpect(this.w1.board.get(1).get(0).wireColor(32), new Color(255, 255, 0));
    t.checkExpect(this.w1.board.get(1).get(1).wireColor(32), new Color(255, 217, 0));
  }

}