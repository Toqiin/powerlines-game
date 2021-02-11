import java.awt.Color;
import java.util.*;
import javalib.impworld.*;
import javalib.worldimages.*;


// class to represent the world and game board
class LightEmAll extends World {
  // a list of columns of GamePieces,
  // i.e., represents the board in column-major order
  ArrayList<ArrayList<GamePiece>> board;
  // a list of all nodes
  ArrayList<GamePiece> nodes;
  // a list of edges of the minimum spanning tree
  //ArrayList<Edge> mst;
  // the width and height of the board
  int cols;
  int rows;
  // DONT CHANGE THE CELL SIZE, 64 IS GOOD
  int cellSize = 64;
  // the current location of the power station,
  // as well as its effective radius
  int powerRowInd;
  int powerColInd;
  int radius;
  int time = 0;
  int tick = 0;
  int moves = 0;
  int seed = 0;
  ArrayList<Edge> mst;

  // constructor to run the game
  LightEmAll(int cols, int rows) {
    this.board = new ArrayList<ArrayList<GamePiece>>();
    this.cols = cols;
    this.rows = rows;
    this.powerColInd = (this.cols) / 2;
    this.powerRowInd = 0;
    //this.genBoard3();

    // uses kruskals algorithm to generate the board. Put "vertical" or
    // "horizontal" in the first argument to specify which direction gets 
    // a bias, and input an integer into the second argument
    // to determine the strength of the bias.
    // Putting any other string in the first argument will give no bias,
    // putting 0 as the int will give no bias, and putting a negative int
    // will reverse the bias direction specified.
    this.kruskals("vertical", 0);
    this.updateConnect();
    this.getRadius();

    // uncomment this method to see the grid with random rotation

    this.rotateAll();
    this.updateConnect();
    this.connectPower();
  }

  LightEmAll(int cols, int rows, String test) {
    this.board = new ArrayList<ArrayList<GamePiece>>();
    this.cols = cols;
    this.rows = rows;
    this.powerColInd = (this.cols) / 2;
    this.powerRowInd = 0;
    this.genBoard3();
    //this.kruskals();
    this.updateConnect();
    this.getRadius();
    //this.rotateAll();
    this.updateConnect();
    this.connectPower();
  }

  LightEmAll(int cols, int rows, int size) {
    this.board = new ArrayList<ArrayList<GamePiece>>();
    this.cols = cols;
    this.rows = rows;
    this.powerColInd = (this.cols) / 2;
    this.powerRowInd = 0;
    this.cellSize = size;
    this.genBoard3();
    //this.kruskals();
    this.updateConnect();
    this.getRadius();
    this.rotateAll();
    this.updateConnect();
    this.connectPower();
  }

  LightEmAll(int cols, int rows, int seed, boolean seedTest) {
    this.board = new ArrayList<ArrayList<GamePiece>>();
    this.cols = cols;
    this.rows = rows;
    this.powerColInd = (this.cols) / 2;
    this.powerRowInd = 0;
    this.genBoard3();
    //this.rotateAllTest();
    this.updateConnect();
    this.getRadius();
    this.seed = seed;
  }

  LightEmAll(int cols, int rows, boolean test) {
    this.board = new ArrayList<ArrayList<GamePiece>>();
    this.cols = cols;
    this.rows = rows;
    this.powerColInd = (this.cols - 1) / 2;
    this.powerRowInd = (this.rows - 1) / 2;
    this.genBoard();
  }

  /*
  public void clearBoard() {
    for (ArrayList<GamePiece> arr : this.board) {
      for (GamePiece gp : arr) {
        gp.top = false;
        gp.right = false;
        gp.bottom = false;
        gp.left = false;
      }
    }
  }
   */

  // creates the MST for the grid using a given bias and bias amount, 
  // then connects all nodes based on these edges
  public void kruskals(String bias, int biasInt) {
    // numbers to add to the randomly generated edge weights
    // in order to have a bias for a particular direction
    int biasVert = 0;
    int biasHori = 0;
    if (bias.equals("horizontal")) {
      biasVert += biasInt;
    } else if (bias.equals("vertical")) {
      biasHori += biasInt;
    }

    // sets the board to have all wires set to false
    this.board = new ArrayList<ArrayList<GamePiece>>();
    for (int i = 0; i < this.cols; i++) {
      this.board.add(new ArrayList<GamePiece>());
      for (int j = 0; j < this.rows; j++) {
        if (i == this.powerColInd && j == this.powerRowInd) {
          this.board.get(i).add(new GamePiece(j, i,
              false, false, false, false, true));
        } else {
          this.board.get(i).add(new GamePiece(j, i,
              false, false, false, false));
        }
      }
    }

    // Initializes the list of all possible edges.
    // Initializes the random object
    ArrayList<Edge> allEdges = new ArrayList<Edge>();
    Random rand = new Random();
    for (int i = 0; i < this.cols; i++) {
      for (int j = 0; j < this.rows; j++) {
        // only makes edges from this node to the one on the right
        // and to the one on the bottom
        // this prevents duplicate edges that connect the same nodes 
        // but in opposite directions
        if ((i + 1) < this.cols) {
          allEdges.add(new Edge(this.board.get(i).get(j),
              this.board.get(i + 1).get(j), rand.nextInt(99) + 1 + biasHori));
        }
        if ((j + 1) < this.rows) {
          allEdges.add(new Edge(this.board.get(i).get(j),
              this.board.get(i).get(j + 1), rand.nextInt(99) + 1 + biasVert));
        }
      }
    }
    allEdges.sort(new WeightComparator());
    HashMap<ArrayList<Integer>, ArrayList<Integer>> reps =
        new HashMap<ArrayList<Integer>, ArrayList<Integer>>();
    ArrayList<Edge> treeEdges = new ArrayList<Edge>();
    for (ArrayList<GamePiece> arr : this.board) {
      for (GamePiece gp : arr) {
        reps.put(gp.toList(), gp.toList());
      }
    }
    int nodes = this.rows * this.cols;
    while (treeEdges.size() < nodes - 1) {
      Edge current = allEdges.remove(0);
      if (new Utils().find(reps, current.fromNode.toList()).equals(new
          Utils().find(reps, current.toNode.toList()))) {
        // do nothing becuase it will create a cycle, but is to specific for else
      } else {
        treeEdges.add(current);
        new Utils().union(reps, new Utils().find(reps, current.fromNode.toList()),
            new Utils().find(reps, current.toNode.toList()));
      }
    }

    for (Edge e : treeEdges) {
      e.fromNode.connectDir(e.toNode);
    }

    this.mst = treeEdges;
  }

  // tester for kruskals with seeded random
  public void kruskalsTest(String bias, int biasInt, int seed) {
    // numbers to add to the randomly generated edge weights
    // in order to have a bias for a particular direction
    int biasVert = 0;
    int biasHori = 0;
    if (bias.equals("horizontal")) {
      biasVert += biasInt;
    } else if (bias.equals("vertical")) {
      biasHori += biasInt;
    }

    // sets the board to have all wires set to false
    this.board = new ArrayList<ArrayList<GamePiece>>();
    for (int i = 0; i < this.cols; i++) {
      this.board.add(new ArrayList<GamePiece>());
      for (int j = 0; j < this.rows; j++) {
        if (i == this.powerColInd && j == this.powerRowInd) {
          this.board.get(i).add(new GamePiece(j, i,
              false, false, false, false, true));
        } else {
          this.board.get(i).add(new GamePiece(j, i,
              false, false, false, false));
        }
      }
    }

    // Initializes the list of all possible edges.
    // Initializes the random object
    ArrayList<Edge> allEdges = new ArrayList<Edge>();
    Random rand = new Random(seed);
    for (int i = 0; i < this.cols; i++) {
      for (int j = 0; j < this.rows; j++) {
        // only makes edges from this node to the one on the right
        // and to the one on the bottom
        // this prevents duplicate edges that connect the same nodes 
        // but in opposite directions
        if ((i + 1) < this.cols) {
          allEdges.add(new Edge(this.board.get(i).get(j),
              this.board.get(i + 1).get(j), rand.nextInt(99) + 1 + biasHori));
        }
        if ((j + 1) < this.rows) {
          allEdges.add(new Edge(this.board.get(i).get(j),
              this.board.get(i).get(j + 1), rand.nextInt(99) + 1 + biasVert));
        }
      }
    }
    allEdges.sort(new WeightComparator());
    HashMap<ArrayList<Integer>, ArrayList<Integer>> reps =
        new HashMap<ArrayList<Integer>, ArrayList<Integer>>();
    ArrayList<Edge> treeEdges = new ArrayList<Edge>();
    for (ArrayList<GamePiece> arr : this.board) {
      for (GamePiece gp : arr) {
        reps.put(gp.toList(), gp.toList());
      }
    }
    int nodes = this.rows * this.cols;
    while (treeEdges.size() < nodes - 1) {
      Edge current = allEdges.remove(0);
      if (new Utils().find(reps, current.fromNode.toList()).equals(new
          Utils().find(reps, current.toNode.toList()))) {
        // do nothing becuase it will create a cycle, but is to specific for else
      } else {
        treeEdges.add(current);
        new Utils().union(reps, new Utils().find(reps, current.fromNode.toList()),
            new Utils().find(reps, current.toNode.toList()));
      }
    }

    for (Edge e : treeEdges) {
      e.fromNode.connectDir(e.toNode);
    }

    this.mst = treeEdges;
  }




  // determines if the game is won
  public void checkWon() {
    boolean won = true;
    for (ArrayList<GamePiece> arr : this.board) {
      for (GamePiece gp : arr) {
        if (!gp.powered || gp.distance > this.radius) {
          won = false;
        }
      }
    }
    if (won) {
      this.endOfWorld("YOU WON!");
    }
  }

  // renders the scene after the game is over, displaying msg
  public WorldScene lastScene(String msg) {
    WorldScene scene = this.makeScene();
    scene.placeImageXY(new TextImage(msg, this.cellSize, FontStyle.BOLD, Color.GREEN),
        Math.max(this.cols * this.cellSize, 576) / 2,
        (this.rows * this.cellSize) / 2 + this.cellSize);
    return scene;
  }

  // undoes all connections between cells to make tests easier
  public void cancelConnect() {
    for (ArrayList<GamePiece> arr : this.board) {
      for (GamePiece gp : arr) {
        gp.neighbors = new ArrayList<GamePiece>(Arrays.asList(new GamePiece(),
            new GamePiece(), new GamePiece(), new GamePiece()));
        gp.powered = false;
        gp.distance = 0;
      }
    }
  }

  // helper to recursively generate a part of the game board
  public void genBoard3Help(int startCol, int endCol,
      int startRow, int endRow) {
    int midCol = (startCol + endCol) / 2;
    int midRow = (startRow + endRow) / 2;
    int midCol1 = midCol + 1;
    int midRow1 = midRow + 1;
    if (startCol == endCol) {
      midCol1 = midCol;
    }
    if (startRow == endRow) {
      midRow1 = midRow;
    }

    this.board.get(midCol).get(endRow).right = true;
    this.board.get(midCol1).get(endRow).left = true;
    this.board.get(startCol).get(midRow).bottom = true;
    this.board.get(startCol).get(midRow1).top = true;
    this.board.get(endCol).get(midRow).bottom = true;
    this.board.get(endCol).get(midRow1).top = true;

    // handles sections of the board where the width and height are more than 2
    if (!((endCol - startCol <= 1 || endRow - startRow <= 1))) {
      this.genBoard3Help(startCol, midCol, startRow, midRow);
      this.genBoard3Help(startCol, midCol, midRow1, endRow);
      this.genBoard3Help(midCol1, endCol, startRow, midRow);
      this.genBoard3Help(midCol1, endCol, midRow1, endRow);
      // handles sections with a height of 2 by subdiving into left and right halves
    } else if (endCol - startCol > 1) {
      this.genBoard3Help(startCol, midCol, startRow, endRow);
      this.genBoard3Help(midCol1, endCol, startRow, endRow);
      // handles sections with a width of 2 by creating a tall "U" shape
    } else if (endRow - startRow > 1) {
      this.board.get(startCol).get(startRow).bottom = true;
      this.board.get(endCol).get(startRow).bottom = true;
      this.board.get(startCol).get(endRow).top = true;
      this.board.get(endCol).get(endRow).top = true;

      for (int k = startRow + 1; k < endRow; k++) {
        this.board.get(startCol).get(k).top = true;
        this.board.get(startCol).get(k).bottom = true;
        this.board.get(endCol).get(k).top = true;
        this.board.get(endCol).get(k).bottom = true;

      }
    }

  }

  // generates the board using a subdivision algorithm
  public void genBoard3() {
    this.board = new ArrayList<ArrayList<GamePiece>>();
    for (int i = 0; i < this.cols; i++) {
      this.board.add(new ArrayList<GamePiece>());
      for (int j = 0; j < this.rows; j++) {
        if (i == this.powerColInd && j == this.powerRowInd) {
          this.board.get(i).add(new GamePiece(j, i,
              false, false, false, false, true));
        } else {
          this.board.get(i).add(new GamePiece(j, i,
              false, false, false, false));
        }
      }
    }
    this.genBoard3Help(0, this.cols - 1, 0, this.rows - 1);
    this.removeConnect2();
  }

  // sets every direction on every cell to false if that cell is not mutually
  // connected to another cell in the direction
  public void removeConnect2() {
    this.updateConnect();
    for (int i = 0; i < this.cols; i++) {
      for (int j = 0; j < this.rows; j++) {
        if (!(this.board.get(i).get(j).neighbors.get(0).real())) {
          this.board.get(i).get(j).top = false;
        }
        if (!(this.board.get(i).get(j).neighbors.get(1).real())) {
          this.board.get(i).get(j).right = false;
        }
        if (!(this.board.get(i).get(j).neighbors.get(2).real())) {
          this.board.get(i).get(j).bottom = false;
        }
        if (!(this.board.get(i).get(j).neighbors.get(3).real())) {
          this.board.get(i).get(j).left = false;
        }
      }

    }
  }

  // sets this.radius to the furthest distance from the furthest node
  // from the powerstation to the furthest node from that node
  public void getRadius() {
    GamePiece furthestNode =
        this.board.get(this.powerColInd).get(this.powerRowInd).furthestNode();
    int d = furthestNode.furthestDistance();
    this.radius = (d / 2) + 1;
  }

  // updates connections, setting all the nodes connected to powerstation
  // to have this.powered = true, and sets their distance properly
  public void connectPower() {
    for (ArrayList<GamePiece> arr : this.board) {
      for (GamePiece gp : arr) {
        gp.powered = false;
        gp.distance = 0;
      }
    }
    this.board.get(this.powerColInd).get(this.powerRowInd).bfsPower();
  }

  // randomly rotates all the cells on the board
  public void rotateAll() {
    for (ArrayList<GamePiece> arr : this.board) {
      for (GamePiece gp : arr) {
        Random rand = new Random();
        int randInt = rand.nextInt(4);
        while (randInt > 0) {
          gp.handleCellClick();
          randInt--;
        }
      }
    }
  }

  // tester for rotate all with seeded random
  public void rotateAllTest() {
    for (ArrayList<GamePiece> arr : this.board) {
      for (GamePiece gp : arr) {
        Random rand = new Random(this.seed);
        int randInt = rand.nextInt(4);
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
    if (key.equals("up") && this.board.get(this.powerColInd).get(this.powerRowInd).canGo("up")) {
      if (this.moves < 999) {
        this.moves++;
      }
      int oldCol = this.powerColInd;
      int oldRow = this.powerRowInd;
      this.powerRowInd = this.powerRowInd += -1;
      this.board.get(oldCol).get(oldRow).powerStation = false;
      this.board.get(this.powerColInd).get(this.powerRowInd).powerStation = true;
      this.connectPower();

    } else if (key.equals("right") && this.board.get(
        this.powerColInd).get(this.powerRowInd).canGo("right")) {
      if (this.moves < 999) {
        this.moves++;
      }
      int oldCol = this.powerColInd;
      int oldRow = this.powerRowInd;
      this.powerColInd = this.powerColInd += 1;
      this.board.get(oldCol).get(oldRow).powerStation = false;
      this.board.get(this.powerColInd).get(this.powerRowInd).powerStation = true;
      this.connectPower();

    } else if (key.equals("down") && this.board.get(
        this.powerColInd).get(this.powerRowInd).canGo("down")) {
      if (this.moves < 999) {
        this.moves++;
      }
      int oldCol = this.powerColInd;
      int oldRow = this.powerRowInd;
      this.powerRowInd = this.powerRowInd += 1;
      this.board.get(oldCol).get(oldRow).powerStation = false;
      this.board.get(this.powerColInd).get(this.powerRowInd).powerStation = true;
      this.connectPower();

    } else if (key.equals("left") && this.board.get(
        this.powerColInd).get(this.powerRowInd).canGo("left")) {
      if (this.moves < 999) {
        this.moves++;
      }
      int oldCol = this.powerColInd;
      int oldRow = this.powerRowInd;
      this.powerColInd = this.powerColInd += -1;
      this.board.get(oldCol).get(oldRow).powerStation = false;
      this.board.get(this.powerColInd).get(this.powerRowInd).powerStation = true;
      this.connectPower();
    }
    this.checkWon();
  }

  // updates every cells connections / neighbor lists
  public void updateConnect() {
    for (int i = 0; i < this.cols; i++) {
      for (int j = 0; j < this.rows; j++) {
        int top = j - 1;
        int right = i + 1;
        int bot = j + 1;
        int left = i - 1;
        if (j == 0) {
          top = -1;
        }
        if (j == (this.rows - 1)) {
          bot = -1;
        }
        if (i == 0) {
          left = -1;
        }
        if (i == (this.cols - 1)) {
          right = -1;
        }
        if (top >= 0) {
          this.board.get(i).get(j).connect("top", this.board.get(i).get(top));
        }
        if (right >= 0) {
          this.board.get(i).get(j).connect("right", this.board.get(right).get(j));
        }
        if (bot >= 0) {
          this.board.get(i).get(j).connect("bottom", this.board.get(i).get(bot));
        }
        if (left >= 0) {
          this.board.get(i).get(j).connect("left", this.board.get(left).get(j));
        }
      }
    }
  }

  // rotates the cell that was clicked then updates connections
  public void onMouseClicked(Posn pos) {

    if (pos.y >= this.cellSize) {
      if (this.moves < 999) {
        this.moves++;
      }
      int i = pos.x / this.cellSize;
      int j = (pos.y - this.cellSize) / this.cellSize;
      if (j >= 0) {
        this.board.get(i).get(j).handleCellClick();
        this.updateConnect();
        this.connectPower();
      }
    }
    this.checkWon();
  }

  // returns a world scene of the board
  public WorldScene drawBoard(WorldScene scene) {
    for (int i = 0; i < this.cols; i++) {
      for (int j = 0; j < this.rows; j++) {
        scene.placeImageXY(this.board.get(i).get(j).drawCellGrad(this.radius),
            (i * this.cellSize) + (this.cellSize / 2),
            (j * this.cellSize) + 3 * (this.cellSize / 2));
      }
    }
    return scene;
  }

  // generates the 2d-arraylist to represent the game board
  public void genBoard() {
    this.board = new ArrayList<ArrayList<GamePiece>>();
    int middle = (this.rows - 1) / 2;
    for (int i = 0; i < this.cols; i++) {
      this.board.add(new ArrayList<GamePiece>());
      for (int j = 0; j < this.rows; j++) {
        boolean top = true;
        boolean bottom = true;
        boolean left = true;
        boolean right = true;
        if (j == 0) {
          top = false;
          //System.out.println("test");
        }
        if (j == this.rows - 1) {
          bottom = false;
        }
        if (j != middle) {
          left = false;
          right = false;
        }
        if (i == 0) {
          left = false;
        }
        if (i == this.cols - 1) {
          right = false;
        }
        if (i == this.powerColInd && j == this.powerRowInd) {
          this.board.get(i).add(new GamePiece(j, i, left, right, top, bottom, true));
        } else {
          this.board.get(i).add(new GamePiece(j, i, left, right, top, bottom));
        }

      }

    }
    this.updateConnect();
  }



  // draws the world that gets displayed through big bang
  public WorldScene makeScene() {
    WorldScene scene = this.drawBoard(this.getEmptyScene());
    scene.placeImageXY(new TextImage("Score: " + Integer.toString(this.moves),
        48, Color.BLACK), 150, 32);
    scene.placeImageXY(new TextImage("Time: " + Integer.toString(this.time),
        48, Color.BLACK), 400, 32);
    return scene;
  }

}
