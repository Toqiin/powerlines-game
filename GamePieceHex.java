import java.util.*;
import java.awt.Color;
import javalib.worldimages.*;

// class to represent a single cell on the board
class GamePieceHex {
  int row;
  int col;
  int size = 32;
  boolean left;
  boolean right;
  boolean topLeft;
  boolean topRight;
  boolean bottomLeft;
  boolean bottomRight;
  boolean powerStation = false;
  boolean powered = false;
  // order of indices is top, right, bottom, left
  ArrayList<GamePieceHex> neighbors;
  int distance = 0;

  // constructs a cell w/ a power station
  GamePieceHex(int row, int col, boolean left, boolean right, boolean topLeft, boolean topRight,
      boolean bottomLeft, boolean bottomRight, boolean pS) {
    this.row = row;
    this.col = col;
    this.powerStation = pS;
    this.left = left;
    this.right = right;
    this.topRight = topRight;
    this.topLeft = topLeft;
    this.bottomLeft = bottomLeft;
    this.bottomRight = bottomRight;
    this.neighbors = new ArrayList<GamePieceHex>(Arrays.asList(new GamePieceHex(),
        new GamePieceHex(), new GamePieceHex(), new GamePieceHex(),
        new GamePieceHex(), new GamePieceHex()));
  }

  // constructs a cell that is not a power station
  GamePieceHex(int row, int col, boolean left, boolean right, boolean topLeft, boolean topRight,
      boolean bottomLeft, boolean bottomRight) {
    this.row = row;
    this.col = col;
    this.left = left;
    this.right = right;
    this.topRight = topRight;
    this.topLeft = topLeft;
    this.bottomLeft = bottomLeft;
    this.bottomRight = bottomRight;
    this.neighbors = new ArrayList<GamePieceHex>(Arrays.asList(new GamePieceHex(),
        new GamePieceHex(), new GamePieceHex(), new GamePieceHex(), new GamePieceHex(),
        new GamePieceHex()));
  }

  // constructs an empty/fake cell
  GamePieceHex() {
    this.row = 0;
    this.col = 0;
    this.left = false;
    this.right = false;

    this.topRight = false;
    this.topLeft = false;
    this.bottomLeft = false;
    this.bottomRight = false;
    this.neighbors = new ArrayList<GamePieceHex>();
  }

  // rotates the cell clockwise by updating the directional booleans
  public void handleCellClick() {

    boolean tL = this.left;
    boolean tR = this.right;
    boolean tTL = this.topLeft;
    boolean tTR = this.topRight;
    boolean tBL = this.bottomLeft;
    boolean tBR = this.bottomRight;
    this.left = tBL;
    this.bottomLeft = tBR;
    this.bottomRight = tR;
    this.right = tTR;
    this.topRight = tTL;
    this.topLeft = tL;

  }

  // generates a string off of this GamePieces col and row
  public String locToString() {
    return Integer.toString(this.col) + " " + Integer.toString(this.row);
  }

  // BFS to power all nodes connected to the powerstation and set their distances
  public void bfsPower() {
    Queue<GamePieceHex> worklist = new LinkedList<GamePieceHex>();
    ArrayList<GamePieceHex> found = new ArrayList<GamePieceHex>();
    ArrayList<GamePieceHex> distanced = new ArrayList<GamePieceHex>();
    this.powered = true;
    this.distance = 0;

    worklist.add(this);
    while (!worklist.isEmpty()) {
      GamePieceHex next = worklist.remove();
      if (found.contains(next)) {
        // already found go next
      } else {
        found.add(next);
        for (GamePieceHex gp : next.neighbors) {
          if (gp.real()) {
            if (!found.contains(gp)) {
              worklist.add(gp);
              if (distanced.contains(gp)) {
                // already found go next
              } else {
                gp.powered = true;
                gp.distance = next.distance + 1;
                distanced.add(gp);
              }

            }
          } 
        }
      }
    }
  }

  // determines if this cell is connected to any other "real" cells (not the empty ones).
  // primarily used to test updateConnect()
  public boolean connected() {
    int i = 0;
    for (GamePieceHex gp : this.neighbors) {
      i = i + gp.neighbors.size();
    }
    return i != 0;
  }

  // returns the color of the wires of this cell based on distance from
  // the powerstation
  public Color wireColor(int radius) {
    double rate = 0;
    if (radius == 0) {
      // cant divide by 0
    } else {
      rate = 255 / radius;
    }

    Color c;
    if (this.powered && this.distance <= radius) {
      if (this.distance == 0) {
        c = Color.yellow;
      } else {
        c = new Color(255, (int)((radius - this.distance) * rate), 0);
      }
      //c = Color.yellow;
    } else {
      c = Color.LIGHT_GRAY;
    }
    return c;
  }

  // draws the cell with its wiring having a color that is yellow at distance from 
  // powerstation at 0, and gets more red until it passes the radius, when it goes gray.
  public WorldImage drawCellGrad(int radius) {

    Color c = this.wireColor(radius);

    WorldImage ps;
    if (this.powerStation) {
      ps = new StarImage(this.size / 2.5, 5, OutlineMode.SOLID, Color.CYAN);
    } else {
      ps = new EmptyImage();
    }

    WorldImage topLeft;
    WorldImage topRight;
    WorldImage right;
    WorldImage bottomRight;
    WorldImage bottomLeft;
    WorldImage left;
    if (this.topLeft) {
      topLeft = new RotateImage(new RectangleImage(this.size / 8, (int)(this.size / 2 * 1.732),
          OutlineMode.SOLID, c).movePinhole(0, (int)(this.size / 4 * 1.732)), -30);
    } else {
      topLeft = new EmptyImage();
    }
    if (this.topRight) {
      topRight = new RotateImage(new RectangleImage(this.size / 8, (int)(this.size / 2 * 1.732),
          OutlineMode.SOLID, c).movePinhole(0, (int)(this.size / 4 * 1.732)), 30);
    } else {
      topRight = new EmptyImage();
    }
    if (this.right) {
      right = new RectangleImage((int)(this.size / 2 * 1.732), this.size / 8,
          OutlineMode.SOLID, c).movePinhole(-(int)(this.size / 4 * 1.732), 0);
    } else {
      right = new EmptyImage();
    }
    if (this.bottomLeft) {
      bottomLeft = new RotateImage(new RectangleImage(this.size / 8,
          (int)(this.size / 2 * 1.732) + 1,
          OutlineMode.SOLID, c).movePinhole(0, -(int)(this.size / 4 * 1.732)), 30);
    } else {
      bottomLeft = new EmptyImage();
    }
    if (this.bottomRight) {
      bottomRight = new RotateImage(new RectangleImage(this.size / 8,
          (int)(this.size / 2 * 1.732) + 1,
          OutlineMode.SOLID, c).movePinhole(0, -(int)(this.size / 4 * 1.732)), -30);
    } else {
      bottomRight = new EmptyImage();
    }
    if (this.left) {
      left = new RectangleImage((int)(this.size / 2 * 1.732) - 1, this.size / 8,
          OutlineMode.SOLID, c).movePinhole((int)(this.size / 4 * 1.732), 0);
    } else {
      left = new EmptyImage();
    }

    WorldImage wires = new OverlayImage(topLeft, new OverlayImage(right, 
        new OverlayImage(bottomLeft, new OverlayImage(left,
            new OverlayImage(topRight, bottomRight)))));

    WorldImage cell = new OverlayImage(new RotateImage(new HexagonImage(this.size,
        OutlineMode.OUTLINE, Color.BLACK), 30), new RotateImage(new HexagonImage(this.size,
            OutlineMode.SOLID, Color.DARK_GRAY), 30));

    return new OverlayImage(ps, new OverlayImage(wires, cell));
  }

  // if this cell is connected in the provided direction to the given cell,
  // update this cells neighbor list to include that cell at the right spot,
  // otherwise put in a fake cell
  public void connect(String side, GamePieceHex gamePiece) {
    if (side.equals("tl")) {
      if (this.topLeft && gamePiece.bottomRight) {
        this.neighbors.set(0, gamePiece);
      } else {
        this.neighbors.set(0, new GamePieceHex());
      }
    } else if (side.equals("tr")) {
      if (this.topRight && gamePiece.bottomLeft) {
        this.neighbors.set(1, gamePiece);
      } else {
        this.neighbors.set(1, new GamePieceHex());
      }
    } else if (side.equals("right")) {
      if (this.right && gamePiece.left) {
        this.neighbors.set(2, gamePiece);
      } else {
        this.neighbors.set(2, new GamePieceHex());
      }
    } else if (side.equals("br")) {
      if (this.bottomRight && gamePiece.topLeft) {
        this.neighbors.set(3, gamePiece);
      } else {
        this.neighbors.set(3, new GamePieceHex());
      }
    } else if (side.equals("bl")) {
      if (this.bottomLeft && gamePiece.topRight) {
        this.neighbors.set(4, gamePiece);
      } else {
        this.neighbors.set(4, new GamePieceHex());
      }
    } else if (side.equals("left")) {
      if (this.left && gamePiece.right) {
        this.neighbors.set(5, gamePiece);
      } else {
        this.neighbors.set(5, new GamePieceHex());
      }
    }
  }

  // returns whether this cell is mutually connected to the given cell in the
  // given direction
  public boolean connected2(String side, GamePieceHex gamePiece) {
    if (side.equals("topL")) {
      return this.topLeft && gamePiece.bottomRight;
    } else if (side.equals("topR")) {
      return this.topRight && gamePiece.bottomLeft;
    } else if (side.equals("right")) {
      return this.right && gamePiece.left;
    } else if (side.equals("bottomR")) {
      return this.bottomRight && gamePiece.topLeft;
    } else if (side.equals("bottomL")) {
      return this.bottomLeft && gamePiece.topRight;
    } else if (side.equals("left")) {
      return this.left && gamePiece.right;
    } else {
      return false;
    }
  }

  // the same as connected2, but with top replaced by up to match key inputs
  public boolean canGo(String dir) {
    if (dir.equals("tl")) {
      return this.neighbors.get(0).real();
    } else if (dir.equals("tr")) {
      return this.neighbors.get(1).real();
    } else if (dir.equals("right")) {
      return this.neighbors.get(2).real();
    } else if (dir.equals("br")) {
      return this.neighbors.get(3).real();
    } else if (dir.equals("bl")) {
      return this.neighbors.get(4).real();
    } else if (dir.equals("left")) {
      return this.neighbors.get(5).real();
    } else {
      return false;
    }
  }

  // determines if this cell is real or simply a placeholder cell
  public boolean real() {
    return this.neighbors.size() != 0;
  }

  // gets the node furthest away from this node
  public GamePieceHex furthestNode() {
    Queue<GamePieceHex> worklist = new LinkedList<GamePieceHex>();
    ArrayList<GamePieceHex> found = new ArrayList<GamePieceHex>();
    ArrayList<GamePieceHex> node = new ArrayList<GamePieceHex>();
    ArrayList<Integer> dList = new ArrayList<Integer>();

    node.add(this);
    dList.add(0);


    worklist.add(this);
    while (!worklist.isEmpty()) {
      GamePieceHex next = worklist.remove();
      if (found.contains(next)) {
        // already found go next
      } else {

        found.add(next);
        for (GamePieceHex gp : next.neighbors) {

          if (gp.real()) {
            if (!found.contains(gp)) {

              worklist.add(gp);
              int dist = dList.get(node.indexOf(next)) + 1;
              node.add(gp);
              dList.add(dist);
            }
          } 
        }
      }
    }

    int currMax = 0;
    for (int i : dList) {
      if (i > currMax) {
        currMax = i;
      }
    }


    return node.get(dList.indexOf(currMax));
    //return found.get(found.size() - 1);

  }

  // gets the distance away of the furthest node from this node
  public int furthestDistance() {
    Queue<GamePieceHex> worklist = new LinkedList<GamePieceHex>();
    ArrayList<GamePieceHex> found = new ArrayList<GamePieceHex>();
    ArrayList<GamePieceHex> node = new ArrayList<GamePieceHex>();
    ArrayList<Integer> dList = new ArrayList<Integer>();

    node.add(this);
    dList.add(0);

    worklist.add(this);
    while (!worklist.isEmpty()) {
      GamePieceHex next = worklist.remove();
      if (found.contains(next)) {
        // already found go next
      } else {
        found.add(next);
        for (GamePieceHex gp : next.neighbors) {
          if (gp.real()) {
            if (!found.contains(gp)) {
              worklist.add(gp);
              int dist = dList.get(node.indexOf(next)) + 1;
              node.add(gp);
              dList.add(dist);
            }
          } 
        }
      }
    }

    int currMax = 0;
    for (int i : dList) {
      if (i > currMax) {
        currMax = i;
      }
    }

    return currMax;

  }

  // makes an arraylist of the location of the cell, used as they key for hashmaps
  public ArrayList<Integer> toList() {
    return new ArrayList<Integer>(Arrays.asList(this.col, this.row));
  }

  // connects this cell to that cell by updating the appropriate boolean fields of
  // both cells
  public void connectDir(GamePieceHex toNode) {
    if (toNode.col - this.col == 1 && toNode.row == this.row) {
      this.right = true;
      toNode.left = true;
    } else if (toNode.col - this.col == -1 && toNode.row - this.row == 0) {
      this.left = true;
      toNode.right = true;
    } else if (toNode.row - this.row == -1 && toNode.col == this.col && this.row % 2 == 1) {
      this.topRight = true;
      toNode.bottomLeft = true;
    } else if (toNode.row - this.row == -1 && toNode.col == this.col + 1 && this.row % 2 == 0) {
      this.topRight = true;
      toNode.bottomLeft = true;
    } else if (toNode.row - this.row == -1 && toNode.col == this.col - 1 && this.row % 2 == 1) {
      this.topLeft = true;
      toNode.bottomRight = true;
    } else if (toNode.row - this.row == -1 && toNode.col == this.col && this.row % 2 == 0) {
      this.topLeft = true;
      toNode.bottomRight = true;
    } else if (toNode.row - this.row == 1 && toNode.col == this.col && this.row % 2 == 1) {
      this.bottomRight = true;
      toNode.topLeft = true;
    } else if (toNode.row - this.row == 1 && toNode.col == this.col + 1 && this.row % 2 == 0) {
      this.bottomRight = true;
      toNode.topLeft = true;
    } else if (toNode.row - this.row == 1 && toNode.col == this.col - 1 && this.row % 2 == 1) {
      this.bottomLeft = true;
      toNode.topRight = true;
    } else if (toNode.row - this.row == 1 && toNode.col == this.col && this.row % 2 == 0) {
      this.bottomLeft = true;
      toNode.topRight = true;
    }
  }

}

