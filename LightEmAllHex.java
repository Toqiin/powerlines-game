import java.awt.Color;

import java.util.*;
import javalib.impworld.*;
import javalib.worldimages.*;


// class to represent the world and game board
class LightEmAllHex extends World {
  // a list of columns of GamePieces,
  // i.e., represents the board in column-major order
  ArrayList<ArrayList<GamePieceHex>> board;
  // a list of all nodes
  ArrayList<GamePieceHex> nodes;
  // a list of edges of the minimum spanning tree
  //ArrayList<Edge> mst;
  // the width and height of the board
  int cols;
  int rows;
  int cellSize = 32;
  ArrayList<EdgeHex> mst;
  // the current location of the power station,
  // as well as its effective radius
  int powerRowInd;
  int powerColInd;
  int radius;
  int time = 0;
  int tick = 0;
  int moves = 0;
  ArrayList<Posn> centers = new ArrayList<Posn>();
  int mostPSMoves = 0;

  LightEmAllHex(int cols, int rows) {
    this.board = new ArrayList<ArrayList<GamePieceHex>>();
    this.cols = cols;
    this.rows = rows;
    this.powerColInd = (this.cols) / 2;
    this.powerRowInd = 0;
    this.getCenters();
    // uses kruskals algorithm to generate the board. Put "vertical" or
    // "horizontal" in the first argument to specify which direction gets 
    // a bias, and input an integer into the second argument
    // to determine the strength of the bias.
    // Putting any other string in the first argument will give no bias,
    // putting 0 as the int will give no bias, and putting a negative int
    // will reverse the bias direction specified.
    this.kruskals("up left", 0);
    this.updateConnect();
    this.getRadius();

    // uncomment this method to see the grid without random rotation
    // this.rotateAll();
    this.updateConnect();
    this.connectPower();
  }

  /*
  LightEmAllHex(int cols, int rows, String test) {
    this.board = new ArrayList<ArrayList<GamePieceHex>>();
    this.cols = cols;
    this.rows = rows;
    this.powerColInd = (this.cols) / 2;
    this.powerRowInd = 0;
    //this.genBoard3();
    this.kruskals("", 0);
    this.updateConnect();
    this.getRadius();
    //this.rotateAll();
    this.updateConnect();
    this.connectPower();
  }
   */

  /*
  LightEmAllHex(int cols, int rows, int size) {
    this.board = new ArrayList<ArrayList<GamePieceHex>>();
    this.cols = cols;
    this.rows = rows;
    this.powerColInd = (this.cols) / 2;
    this.powerRowInd = 0;
    this.cellSize = size;
    //this.genBoard3();
    this.kruskals("", 0);
    this.updateConnect();
    this.getRadius();
    this.rotateAll();
    this.updateConnect();
    this.connectPower();
  }
   */

  /*
  LightEmAllHex(int cols, int rows, boolean test) {
    this.board = new ArrayList<ArrayList<GamePieceHex>>();
    this.cols = cols;
    this.rows = rows;
    this.powerColInd = (this.cols - 1) / 2;
    this.powerRowInd = (this.rows - 1) / 2;
    //this.genBoard();
  }
   */

  // sets this.centers equal to an arrayList of posns that are at the centers of
  // the board's hexagons
  public void getCenters() {
    for (int i = 0; i < this.cols; i++) {
      for (int j = 0; j < this.rows; j++) {
        if (j % 2 == 0) {
          this.centers.add(new Posn(
              i * (int)(1.732 * this.cellSize) + this.cellSize + (int)(this.cellSize * 1.732 / 2),
              (j * (this.cellSize + this.cellSize / 2)) + 50 + (this.cellSize)));
        } else {
          this.centers.add(new Posn(
              i * (int)(1.732 * this.cellSize) + this.cellSize,
              (j * (this.cellSize + this.cellSize / 2)) + 50 + (this.cellSize)));
        }

      }
    }

  }

  // returns a world scene of the board
  public WorldScene drawBoard(WorldScene scene) {

    for (int i = 0; i < this.cols; i++) {
      for (int j = 0; j < this.rows; j++) {
        if (j % 2 == 0) {
          scene.placeImageXY(this.board.get(i).get(j).drawCellGrad(this.radius),
              i * (int)(1.732 * this.cellSize) + this.cellSize + (int)(this.cellSize * 1.732 / 2),
              (j * (this.cellSize + this.cellSize / 2)) + 50 + (this.cellSize));
        } else {
          scene.placeImageXY(this.board.get(i).get(j).drawCellGrad(this.radius),
              i * (int)(1.732 * this.cellSize) + this.cellSize,
              (j * (this.cellSize + this.cellSize / 2)) + 50 + (this.cellSize));
        }

      }
    }

    return scene;
  }

  // tester method for kruskals with a seeded random
  public void kruskalsTest(String bias, int biasInt, int seed) {
    //System.out.println("running kruskals");
    // numbers to add to the randomly generated edge weights
    // in order to have a bias for a particular direction
    int biasUpRight = 0;
    int biasUpLeft = 0;
    int biasHori = 0;
    if (bias.equals("horizontal")) {
      biasUpRight += biasInt;
      biasUpLeft += biasInt;
    } else if (bias.equals("up right")) {
      biasHori += biasInt;
      biasUpLeft += biasInt;
    } else if (bias.equals("up left")) {
      biasHori += biasInt;
      biasUpRight += biasInt;
    }

    // sets the board to have all wires set to false
    this.board = new ArrayList<ArrayList<GamePieceHex>>();
    for (int i = 0; i < this.cols; i++) {
      this.board.add(new ArrayList<GamePieceHex>());
      for (int j = 0; j < this.rows; j++) {
        if (i == this.powerColInd && j == this.powerRowInd) {
          this.board.get(i).add(new GamePieceHex(j, i, false, false,
              false, false, false, false, true));
        } else {
          this.board.get(i).add(new GamePieceHex(j, i, false, false,
              false, false, false, false));
        }
      }
    }

    // Initializes the list of all possible edges.
    // Initializes the random object
    ArrayList<EdgeHex> allEdges = new ArrayList<EdgeHex>();
    Random rand = new Random(seed);
    for (int i = 0; i < this.cols; i++) {
      for (int j = 0; j < this.rows; j++) {
        // only makes edges from this node to the one on the right
        // and to the one on the bottom
        // this prevents duplicate edges that connect the same nodes 
        // but in opposite directions
        if (j % 2 == 0) {
          if (i + 1 < this.cols) {
            allEdges.add(new EdgeHex(this.board.get(i).get(j),
                this.board.get(i + 1).get(j), rand.nextInt(99) + 1 + biasHori));
          }
          if (i + 1 < this.cols && j + 1 < this.rows) {
            allEdges.add(new EdgeHex(this.board.get(i).get(j), this.board.get(i + 1).get(j + 1),
                rand.nextInt(99) + 1 + biasUpLeft));
          }
          if (j + 1 < this.rows) {
            allEdges.add(new EdgeHex(this.board.get(i).get(j), this.board.get(i).get(j + 1),
                rand.nextInt(99) + 1 + biasUpRight));
          }

        }
        if (j % 2 == 1) {
          if (i + 1 < this.cols) {
            allEdges.add(new EdgeHex(this.board.get(i).get(j),
                this.board.get(i + 1).get(j), rand.nextInt(99) + 1 + biasHori));
          }
          if (j + 1 < this.rows) {
            allEdges.add(new EdgeHex(this.board.get(i).get(j), this.board.get(i).get(j + 1),
                rand.nextInt(99) + 1 + biasUpLeft));
          }
          if (i != 0 && j + 1 < this.rows) {
            allEdges.add(new EdgeHex(this.board.get(i).get(j), this.board.get(i - 1).get(j + 1),
                rand.nextInt(99) + 1 + biasUpRight));
          }

        }

      }
    }
    allEdges.sort(new WeightComparatorHex());
    HashMap<ArrayList<Integer>, ArrayList<Integer>> reps =
        new HashMap<ArrayList<Integer>, ArrayList<Integer>>();
    ArrayList<EdgeHex> treeEdges = new ArrayList<EdgeHex>();
    for (ArrayList<GamePieceHex> arr : this.board) {
      for (GamePieceHex gp : arr) {
        reps.put(gp.toList(), gp.toList());
      }
    }
    int nodes = this.rows * this.cols;
    while (treeEdges.size() < nodes - 1) {
      EdgeHex current = allEdges.remove(0);
      if (new UtilsHex().find(reps, current.fromNode.toList()).equals(new
          UtilsHex().find(reps, current.toNode.toList()))) {
        // do nothing because the edge would make a cycle, and this case is more specific
        // than the else
      } else {
        treeEdges.add(current);
        new UtilsHex().union(reps, new UtilsHex().find(reps, current.fromNode.toList()),
            new UtilsHex().find(reps, current.toNode.toList()));
      }
    }

    for (EdgeHex e : treeEdges) {
      e.fromNode.connectDir(e.toNode);
    }
    //System.out.println("done kruskals");
    this.mst = treeEdges;
  }

  /*
   * uses kruskals algorithm to set this.mst equal to a random MST
     and uses this list of edges to set each gampieces appropriate boolean
     fields to true.
     String bias is one of
     - "horizontal"
     - "up left"
     - "up right"
     and determines the direction of the bias. any other string gives no bias
     Int biasInt is the amount of bias wanted. 0 is no bias, 100+ gives maximum bias
     where every cell has a connection in that direction
   */
  public void kruskals(String bias, int biasInt) {
    //System.out.println("running kruskals");
    // numbers to add to the randomly generated edge weights
    // in order to have a bias for a particular direction
    int biasUpRight = 0;
    int biasUpLeft = 0;
    int biasHori = 0;
    if (bias.equals("horizontal")) {
      biasUpRight += biasInt;
      biasUpLeft += biasInt;
    } else if (bias.equals("up right")) {
      biasHori += biasInt;
      biasUpLeft += biasInt;
    } else if (bias.equals("up left")) {
      biasHori += biasInt;
      biasUpRight += biasInt;
    }

    // sets the board to have all wires set to false
    this.board = new ArrayList<ArrayList<GamePieceHex>>();
    for (int i = 0; i < this.cols; i++) {
      this.board.add(new ArrayList<GamePieceHex>());
      for (int j = 0; j < this.rows; j++) {
        if (i == this.powerColInd && j == this.powerRowInd) {
          this.board.get(i).add(new GamePieceHex(j, i, false, false,
              false, false, false, false, true));
        } else {
          this.board.get(i).add(new GamePieceHex(j, i, false, false,
              false, false, false, false));
        }
      }
    }

    // Initializes the list of all possible edges.
    // Initializes the random object
    ArrayList<EdgeHex> allEdges = new ArrayList<EdgeHex>();
    Random rand = new Random();
    for (int i = 0; i < this.cols; i++) {
      for (int j = 0; j < this.rows; j++) {
        // only makes edges from this node to the one on the right
        // and to the one on the bottom
        // this prevents duplicate edges that connect the same nodes 
        // but in opposite directions
        if (j % 2 == 0) {
          if (i + 1 < this.cols) {
            allEdges.add(new EdgeHex(this.board.get(i).get(j),
                this.board.get(i + 1).get(j), rand.nextInt(99) + 1 + biasHori));
          }
          if (i + 1 < this.cols && j + 1 < this.rows) {
            allEdges.add(new EdgeHex(this.board.get(i).get(j), this.board.get(i + 1).get(j + 1),
                rand.nextInt(99) + 1 + biasUpLeft));
          }
          if (j + 1 < this.rows) {
            allEdges.add(new EdgeHex(this.board.get(i).get(j), this.board.get(i).get(j + 1),
                rand.nextInt(99) + 1 + biasUpRight));
          }

        }
        if (j % 2 == 1) {
          if (i + 1 < this.cols) {
            allEdges.add(new EdgeHex(this.board.get(i).get(j),
                this.board.get(i + 1).get(j), rand.nextInt(99) + 1 + biasHori));
          }
          if (j + 1 < this.rows) {
            allEdges.add(new EdgeHex(this.board.get(i).get(j), this.board.get(i).get(j + 1),
                rand.nextInt(99) + 1 + biasUpLeft));
          }
          if (i != 0 && j + 1 < this.rows) {
            allEdges.add(new EdgeHex(this.board.get(i).get(j), this.board.get(i - 1).get(j + 1),
                rand.nextInt(99) + 1 + biasUpRight));
          }

        }

      }
    }
    allEdges.sort(new WeightComparatorHex());
    HashMap<ArrayList<Integer>, ArrayList<Integer>> reps =
        new HashMap<ArrayList<Integer>, ArrayList<Integer>>();
    ArrayList<EdgeHex> treeEdges = new ArrayList<EdgeHex>();
    for (ArrayList<GamePieceHex> arr : this.board) {
      for (GamePieceHex gp : arr) {
        reps.put(gp.toList(), gp.toList());
      }
    }
    int nodes = this.rows * this.cols;
    while (treeEdges.size() < nodes - 1) {
      EdgeHex current = allEdges.remove(0);
      if (new UtilsHex().find(reps, current.fromNode.toList()).equals(new
          UtilsHex().find(reps, current.toNode.toList()))) {
        // do nothing because the edge would make a cycle, and this case is more specific
        // than the else
      } else {
        treeEdges.add(current);
        new UtilsHex().union(reps, new UtilsHex().find(reps, current.fromNode.toList()),
            new UtilsHex().find(reps, current.toNode.toList()));
      }
    }

    for (EdgeHex e : treeEdges) {
      e.fromNode.connectDir(e.toNode);
    }
    //System.out.println("done kruskals");
    this.mst = treeEdges;
  }

  // returns a boolean of whether the game is won or not
  // EFFECT: ends the world saying you won if the boolean is true
  public boolean checkWon() {
    boolean won = true;
    for (ArrayList<GamePieceHex> arr : this.board) {
      for (GamePieceHex gp : arr) {
        if (!gp.powered || gp.distance > this.radius) {
          won = false;
        }
      }
    }
    if (won) {
      this.endOfWorld("YOU WON!");
    }
    return won;
  }

  // draws the last scene by displaying a message over the makescene scene
  public WorldScene lastScene(String msg) {
    WorldScene scene = this.makeScene();
    scene.placeImageXY(new TextImage(msg, this.cellSize * 2, FontStyle.BOLD, Color.GREEN),
        (int) Math.max((this.cols + 1) * this.cellSize * 1.732, 700) / 2,
        ((int)(this.rows * this.cellSize * 1.5) + 50 + this.cellSize) / 2);
    return scene;
  }

  // undoes all connections between cells to make tests easier
  public void cancelConnect() {
    for (ArrayList<GamePieceHex> arr : this.board) {
      for (GamePieceHex gp : arr) {
        gp.neighbors = new ArrayList<GamePieceHex>(Arrays.asList(new GamePieceHex(),
            new GamePieceHex(), new GamePieceHex(), new GamePieceHex(),
            new GamePieceHex(), new GamePieceHex()));
        gp.powered = false;
        gp.distance = 0;
      }
    }
  }


  // sets this.radius to the furthest distance from the furthest node
  // from the powerstation to the furthest node from that node
  public void getRadius() {
    GamePieceHex furthestNode =
        this.board.get(this.powerColInd).get(this.powerRowInd).furthestNode();
    int d = furthestNode.furthestDistance();
    this.radius = (d / 2) + 1;
    this.mostPSMoves =
        this.board.get(this.powerColInd).get(this.powerRowInd).furthestDistance() -
        this.radius;
  }

  // updates connections, setting all the nodes connected to powerstation
  // to have this.powered = true, and sets their distance properly
  public void connectPower() {
    for (ArrayList<GamePieceHex> arr : this.board) {
      for (GamePieceHex gp : arr) {
        gp.powered = false;
        gp.distance = 0;
      }
    }
    this.board.get(this.powerColInd).get(this.powerRowInd).bfsPower();
  }

  // randomly rotates all the cells on the board and increments this.mostPSmoves
  // accordingly
  public void rotateAll() {
    for (ArrayList<GamePieceHex> arr : this.board) {
      for (GamePieceHex gp : arr) {
        Random rand = new Random();

        int randInt = rand.nextInt(5) + 1;

        // three pronged symmetrical pieces are special
        if ((gp.left && gp.topRight && gp.bottomRight && !gp.right
            && !gp.bottomLeft && !gp.topLeft) ||
            (gp.right && gp.bottomLeft && gp.topLeft && !gp.left
                && !gp.topRight && !gp.bottomRight)) {
          if (randInt % 2 == 0) {
            this.mostPSMoves += 0;
          } else {
            this.mostPSMoves++;
          }
          // straight pieces are also special
        } else if ((gp.left && !gp.topRight && !gp.bottomRight && gp.right
            && !gp.bottomLeft && !gp.topLeft) ||
            (!gp.left && gp.topRight && !gp.bottomRight && !gp.right
                && gp.bottomLeft && !gp.topLeft) ||
            (!gp.left && !gp.topRight && gp.bottomRight && !gp.right
                && !gp.bottomLeft && gp.topLeft)) {
          if (randInt % 3 == 0) {
            this.mostPSMoves += 0;
          } else if (randInt - 1 % 3 == 0) {
            this.mostPSMoves += 2;
          } else if (randInt - 2 % 3 == 0) {
            this.mostPSMoves += 1;
          }
        } else {
          this.mostPSMoves += 6 - randInt;
        }

        this.mostPSMoves += 6 - randInt;
        while (randInt > 0) {
          gp.handleCellClick();
          randInt--;
        }
      }
    }
  }

  // test method for rotateAll using a seeded random
  public void rotateAllTest(int seed) {

    for (ArrayList<GamePieceHex> arr : this.board) {
      for (GamePieceHex gp : arr) {
        Random rand = new Random(seed);
        int randInt = rand.nextInt(5) + 1;
        if ((gp.left && gp.topRight && gp.bottomRight && !gp.right
            && !gp.bottomLeft && !gp.topLeft) ||
            (gp.right && gp.bottomLeft && gp.topLeft && !gp.left
                && !gp.topRight && !gp.bottomRight)) {
          if (randInt % 2 == 0) {
            this.mostPSMoves += 0;
          } else {
            this.mostPSMoves++;
          }
        } else if ((gp.left && !gp.topRight && !gp.bottomRight && gp.right
            && !gp.bottomLeft && !gp.topLeft) ||
            (!gp.left && gp.topRight && !gp.bottomRight && !gp.right
                && gp.bottomLeft && !gp.topLeft) ||
            (!gp.left && !gp.topRight && gp.bottomRight && !gp.right
                && !gp.bottomLeft && gp.topLeft)) {
          if (randInt % 3 == 0) {
            this.mostPSMoves += 0;
          } else if (randInt - 1 % 3 == 0) {
            this.mostPSMoves += 2;
          } else if (randInt - 2 % 3 == 0) {
            this.mostPSMoves += 1;
          }
        } else {
          this.mostPSMoves += 6 - randInt;
        }

        while (randInt > 0) {
          gp.handleCellClick();
          randInt--;
        }
      }
    }
  }

  // increments the tick count and the time count if the tick is a multiple of 60
  public void onTick() {
    if (this.tick < 75000) {
      this.tick++;
    }
    if (this.time < 999 && this.tick % 60 == 0) {
      this.time++;
    }
  }

  // handles keyboard input to move the powerstation along the connected wires
  public void onKeyEvent(String key) {
    if (key.equals("w") && this.board.get(this.powerColInd).get(this.powerRowInd).canGo("tl")) {
      if (this.moves < 999) {
        this.moves++;
      }
      int oldCol = this.powerColInd;
      int oldRow = this.powerRowInd;
      this.powerRowInd = this.powerRowInd - 1;
      if (oldRow % 2 == 1) {
        this.powerColInd = this.powerColInd - 1;
      }
      this.board.get(oldCol).get(oldRow).powerStation = false;
      this.board.get(this.powerColInd).get(this.powerRowInd).powerStation = true;
      this.connectPower();

    } else if (key.equals("e") && this.board.get(this.powerColInd).get(
        this.powerRowInd).canGo("tr")) {
      if (this.moves < 999) {
        this.moves++;
      }
      int oldCol = this.powerColInd;
      int oldRow = this.powerRowInd;
      this.powerRowInd = this.powerRowInd - 1;
      if (oldRow % 2 == 0) {
        this.powerColInd = this.powerColInd + 1;
      }
      this.board.get(oldCol).get(oldRow).powerStation = false;
      this.board.get(this.powerColInd).get(this.powerRowInd).powerStation = true;
      this.connectPower();

    } else if (key.equals("d") && this.board.get(this.powerColInd).get(
        this.powerRowInd).canGo("right")) {
      if (this.moves < 999) {
        this.moves++;
      }
      int oldCol = this.powerColInd;
      int oldRow = this.powerRowInd;
      this.powerColInd = this.powerColInd + 1;
      this.board.get(oldCol).get(oldRow).powerStation = false;
      this.board.get(this.powerColInd).get(this.powerRowInd).powerStation = true;
      this.connectPower();

    } else if (key.equals("x") && this.board.get(this.powerColInd).get(
        this.powerRowInd).canGo("br")) {
      if (this.moves < 999) {
        this.moves++;
      }
      int oldCol = this.powerColInd;
      int oldRow = this.powerRowInd;
      this.powerRowInd = this.powerRowInd + 1;
      if (oldRow % 2 == 0) {
        this.powerColInd = this.powerColInd + 1;
      }
      this.board.get(oldCol).get(oldRow).powerStation = false;
      this.board.get(this.powerColInd).get(this.powerRowInd).powerStation = true;
      this.connectPower();

    } else if (key.equals("z") && this.board.get(this.powerColInd).get(
        this.powerRowInd).canGo("bl")) {
      if (this.moves < 999) {
        this.moves++;
      }
      int oldCol = this.powerColInd;
      int oldRow = this.powerRowInd;
      this.powerRowInd = this.powerRowInd + 1;
      if (oldRow % 2 == 1) {
        this.powerColInd = this.powerColInd - 1;
      }
      this.board.get(oldCol).get(oldRow).powerStation = false;
      this.board.get(this.powerColInd).get(this.powerRowInd).powerStation = true;
      this.connectPower();

    } else if (key.equals("a") && this.board.get(this.powerColInd).get(
        this.powerRowInd).canGo("left")) {
      if (this.moves < 999) {
        this.moves++;
      }
      int oldCol = this.powerColInd;
      int oldRow = this.powerRowInd;
      this.powerColInd += -1;
      this.board.get(oldCol).get(oldRow).powerStation = false;
      this.board.get(this.powerColInd).get(this.powerRowInd).powerStation = true;
      this.connectPower();

    }

    this.checkWon();
    this.checkLost();
  }

  // determines if you lost and ends the world if you did
  public boolean checkLost() {
    if (this.moves > this.mostPSMoves * 1.4) {
      this.endOfWorld("YOU LOST");
    }
    return this.moves > this.mostPSMoves * 1.4;
  }

  // updates every cells connections / neighbor lists
  public void updateConnect() {

    for (int i = 0; i < this.cols; i++) {
      for (int j = 0; j < this.rows; j++) {
        if (j - 1 >= 0) {
          if (j % 2 == 0 && i == this.cols - 1) {
            // do nothing because this will never have a connection, but it cant go
            // in else
          } else if (j % 2 == 0) {
            this.board.get(i).get(j).connect("tr", this.board.get(i + 1).get(j - 1));
          } else {
            this.board.get(i).get(j).connect("tr", this.board.get(i).get(j - 1));
          }
        }
        if (j - 1 >= 0) {
          if (j % 2 == 1 && i == 0) {
            // do nothing because this will never have a connection, but it cant go
            // in else
          } else if (j % 2 == 1) {
            this.board.get(i).get(j).connect("tl", this.board.get(i - 1).get(j - 1));
          } else {
            this.board.get(i).get(j).connect("tl", this.board.get(i).get(j - 1));
          }
        }
        if (j + 1 <= this.rows - 1) {
          if (j % 2 == 0 && i == this.cols - 1) {
            // do nothing because this will never have a connection, but it cant go
            // in else
          } else if (j % 2 == 0) {
            this.board.get(i).get(j).connect("br", this.board.get(i + 1).get(j + 1));
          } else {
            this.board.get(i).get(j).connect("br", this.board.get(i).get(j + 1));
          }
        }
        if (j + 1 <= this.rows - 1) {
          if (j % 2 == 1 && i == 0) {
            // no connection will ever be made
          } else if (j % 2 == 1) {
            this.board.get(i).get(j).connect("bl", this.board.get(i - 1).get(j + 1));
          } else {
            this.board.get(i).get(j).connect("bl", this.board.get(i).get(j + 1));
          }
        }
        if (i + 1 <= this.cols - 1) {
          this.board.get(i).get(j).connect("right", this.board.get(i + 1).get(j));
        }
        if (i - 1 >= 0) {
          this.board.get(i).get(j).connect("left", this.board.get(i - 1).get(j));
        }
      }
    }
  }

  // rotates the cell that was clicked then updates connections
  public void onMouseClicked(Posn pos) {
    Posn click = new UtilsHex().closest(this.centers, pos);
    boolean onCell = Math.hypot(pos.x - click.x, pos.y - click.y) <= this.cellSize;

    int i;
    int j;

    if (onCell) {
      if ((click.y - 50 ) % this.cellSize == 0) {
        i = ((click.x - 59) / 55);
        j = 2 * (click.y - 82 ) / 96;
        this.board.get(i).get(j).handleCellClick();
        this.updateConnect();
        this.connectPower();
        this.moves++;
      } else {
        i = (click.x - 32) / 55;
        j = (2 * (click.y - 34) / 96) - 1;
        this.board.get(i).get(j).handleCellClick();
        this.updateConnect();
        this.connectPower();
        this.moves++;
      }

    }


    this.checkWon();
    this.checkLost();


  }




  // draws the world that gets displayed through big bang
  public WorldScene makeScene() {
    WorldScene scene = this.drawBoard(this.getEmptyScene());
    scene.placeImageXY(new TextImage("Score: " + Integer.toString(this.moves),
        36, Color.BLACK), 100, 32);
    scene.placeImageXY(new TextImage("Time: " + Integer.toString(this.time),
        36, Color.BLACK), 275, 32);
    scene.placeImageXY(new TextImage("Moves Left: " + Integer.toString((int)(1.4
        * this.mostPSMoves) -
        this.moves + 1), 36, Color.BLACK), 500, 32);
    return scene;
  }

}

