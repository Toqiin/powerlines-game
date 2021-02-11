import java.util.*;
import java.awt.Color;
import javalib.worldimages.*;

// class to represent a single cell on the board
class GamePiece {
  int row;
  int col;
  // DONT CHANGE THIS
  int size = 64;
  boolean left;
  boolean right;
  boolean top;
  boolean bottom;
  boolean powerStation = false;
  boolean powered = false;
  // order of indices is top, right, bottom, left
  ArrayList<GamePiece> neighbors;
  int distance = 0;
  
  // constructs a cell w/ a power station
  GamePiece(int row, int col, boolean left, boolean right, boolean top,
      boolean bottom, boolean pS) {
    this.row = row;
    this.col = col;
    this.powerStation = pS;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.neighbors = new ArrayList<GamePiece>(Arrays.asList(new GamePiece(),
        new GamePiece(), new GamePiece(), new GamePiece()));
  }
  
  // constructs a cell that is not a power station
  GamePiece(int row, int col, boolean left, boolean right, boolean top,
      boolean bottom) {
    this.row = row;
    this.col = col;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.neighbors = new ArrayList<GamePiece>(Arrays.asList(new GamePiece(),
        new GamePiece(), new GamePiece(), new GamePiece()));
  }
  
  // constructs an empty/fake cell
  GamePiece() {
    this.row = 0;
    this.col = 0;
    this.left = false;
    this.right = false;
    this.top = false;
    this.bottom = false;
    this.neighbors = new ArrayList<GamePiece>();
  }
  
  // rotates the cell clockwise by updating the directional booleans
  public void handleCellClick() {
    boolean tL = this.left;
    boolean tR = this.right;
    boolean tT = this.top;
    boolean tB = this.bottom;
    this.left = tB;
    this.bottom = tR;
    this.right = tT;
    this.top = tL;
  }
  
  // generates a string off of this GamePieces col and row
  public String locToString() {
    return Integer.toString(this.col) + " " + Integer.toString(this.row);
  }
  
  // BFS to power all nodes connected to the powerstation and set their distances
  public void bfsPower() {
    Queue<GamePiece> worklist = new LinkedList<GamePiece>();
    ArrayList<GamePiece> found = new ArrayList<GamePiece>();
    this.powered = true;
    this.distance = 0;
    
    worklist.add(this);
    while (!worklist.isEmpty()) {
      GamePiece next = worklist.remove();
      if (!found.contains(next)) {
        found.add(next);
        for (GamePiece gp : next.neighbors) {
          if (gp.real()) {
            if (!found.contains(gp)) {
              worklist.add(gp);
              gp.powered = true;
              gp.distance = next.distance + 1;
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
    for (GamePiece gp : this.neighbors) {
      i = i + gp.neighbors.size();
    }
    return i != 0;
  }
  
  // draws this cell without gradient wiring
  public WorldImage drawCell(int radius) {
    WorldImage ps;
    if (this.powerStation) {
      ps = new StarImage(this.size / 2.5, 5, OutlineMode.SOLID, Color.CYAN);
    } else {
      ps = new EmptyImage();
    }
    Color c;
    if (this.powered && this.distance <= radius) {
      c = Color.yellow;
    } else {
      c = Color.LIGHT_GRAY;
    }
    WorldImage top;
    WorldImage right;
    WorldImage bottom;
    WorldImage left;
    if (this.top) {
      top = new RectangleImage(this.size / 8, this.size / 2,
          OutlineMode.SOLID, c).movePinhole(0, (this.size / 4));
    } else {
      top = new EmptyImage();
    }
    if (this.right) {
      right = new RectangleImage(this.size / 2, this.size / 8,
          OutlineMode.SOLID, c).movePinhole(-(this.size / 4), 0);
    } else {
      right = new EmptyImage();
    }
    if (this.bottom) {
      bottom = new RectangleImage(this.size / 8, this.size / 2,
          OutlineMode.SOLID, c).movePinhole(0, -(this.size / 4));
    } else {
      bottom = new EmptyImage();
    }
    if (this.left) {
      left = new RectangleImage(this.size / 2, this.size / 8,
          OutlineMode.SOLID, c).movePinhole((this.size / 4), 0);
    } else {
      left = new EmptyImage();
    }

    WorldImage wires = new OverlayImage(top, new OverlayImage(right, 
        new OverlayImage(bottom, left)));

    WorldImage cell = new FrameImage(new RectangleImage(this.size, this.size,
        OutlineMode.SOLID, Color.DARK_GRAY));

    return new OverlayImage(ps, new OverlayImage(wires, cell));
  }
  
  // draws the cell with its wiring having a color that is yellow at distance from 
  // powerstation at 0, and gets more red until it passes the radius, when it goes gray.
  public WorldImage drawCellGrad(int radius) {
    double rate = 0;
    if (radius != 0) {
      rate = 255 / radius; 
    } 
    
    WorldImage ps;
    if (this.powerStation) {
      ps = new StarImage(this.size / 2.5, 5, OutlineMode.SOLID, Color.CYAN);
    } else {
      ps = new EmptyImage();
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
    WorldImage top;
    WorldImage right;
    WorldImage bottom;
    WorldImage left;
    if (this.top) {
      top = new RectangleImage(this.size / 8, this.size / 2,
          OutlineMode.SOLID, c).movePinhole(0, (this.size / 4));
    } else {
      top = new EmptyImage();
    }
    if (this.right) {
      right = new RectangleImage(this.size / 2, this.size / 8,
          OutlineMode.SOLID, c).movePinhole(-(this.size / 4), 0);
    } else {
      right = new EmptyImage();
    }
    if (this.bottom) {
      bottom = new RectangleImage(this.size / 8, this.size / 2,
          OutlineMode.SOLID, c).movePinhole(0, -(this.size / 4));
    } else {
      bottom = new EmptyImage();
    }
    if (this.left) {
      left = new RectangleImage(this.size / 2, this.size / 8,
          OutlineMode.SOLID, c).movePinhole((this.size / 4), 0);
    } else {
      left = new EmptyImage();
    }

    WorldImage wires = new OverlayImage(top, new OverlayImage(right, 
        new OverlayImage(bottom, left)));

    WorldImage cell = new FrameImage(new RectangleImage(this.size, this.size,
        OutlineMode.SOLID, Color.DARK_GRAY));

    return new OverlayImage(ps, new OverlayImage(wires, cell));
  }
  
  // if this cell is connected in the provided direction to the given cell,
  // update this cells neighbor list to include that cell at the right spot,
  // otherwise put in a fake cell
  public void connect(String side, GamePiece gamePiece) {
    if (side.equals("top")) {
      if (this.top && gamePiece.bottom) {
        this.neighbors.set(0, gamePiece);
      } else {
        this.neighbors.set(0, new GamePiece());
      }
    } else if (side.equals("right")) {
      if (this.right && gamePiece.left) {
        this.neighbors.set(1, gamePiece);
      } else {
        this.neighbors.set(1, new GamePiece());
      }
    } else if (side.equals("bottom")) {
      if (this.bottom && gamePiece.top) {
        this.neighbors.set(2, gamePiece);
      } else {
        this.neighbors.set(2, new GamePiece());
      }
    } else if (side.equals("left")) {
      if (this.left && gamePiece.right) {
        this.neighbors.set(3, gamePiece);
      } else {
        this.neighbors.set(3, new GamePiece());
      }
    }
  }
  
  // returns whether this cell is mutually connected to the given cell in the
  // given direction
  public boolean connected2(String side, GamePiece gamePiece) {
    if (side.equals("top")) {
      return this.top && gamePiece.bottom;
    } else if (side.equals("right")) {
      return this.right && gamePiece.left;
    } else if (side.equals("bottom")) {
      return this.bottom && gamePiece.top;
    } else if (side.equals("left")) {
      return this.left && gamePiece.right;
    } else {
      return false;
    }
  }
  
  // the same as connected2, but with top replaced by up to match key inputs
  // String dir is the direction the powerstation is trying to move
  public boolean canGo(String dir) {
    if (dir.equals("up")) {
      return this.neighbors.get(0).real();
    } else if (dir.equals("right")) {
      return this.neighbors.get(1).real();
    } else if (dir.equals("down")) {
      return this.neighbors.get(2).real();
    } else if (dir.equals("left")) {
      return this.neighbors.get(3).real();
    } else {
      return false;
    }
  }
  
  // determines if this cell is real or simply a placeholder cell
  public boolean real() {
    return this.neighbors.size() != 0;
  }
  
  // gets the node furthest away from this node
  public GamePiece furthestNode() {
    Queue<GamePiece> worklist = new LinkedList<GamePiece>();
    ArrayList<GamePiece> found = new ArrayList<GamePiece>();
    ArrayList<GamePiece> node = new ArrayList<GamePiece>();
    ArrayList<Integer> dList = new ArrayList<Integer>();
    
    node.add(this);
    dList.add(0);

    
    worklist.add(this);
    while (!worklist.isEmpty()) {
      GamePiece next = worklist.remove();
      if (!found.contains(next)) {
        found.add(next);
        for (GamePiece gp : next.neighbors) {
          
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
    Queue<GamePiece> worklist = new LinkedList<GamePiece>();
    ArrayList<GamePiece> found = new ArrayList<GamePiece>();
    ArrayList<GamePiece> node = new ArrayList<GamePiece>();
    ArrayList<Integer> dList = new ArrayList<Integer>();
    
    node.add(this);
    dList.add(0);
    
    worklist.add(this);
    while (!worklist.isEmpty()) {
      GamePiece next = worklist.remove();
      if (!found.contains(next)) {
        found.add(next);
        for (GamePiece gp : next.neighbors) {
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

  // gets a list representing the location, used as keys in hashmap
  public ArrayList<Integer> toList() {
    return new ArrayList<Integer>(Arrays.asList(this.col, this.row));
  }
  
  // connects the two nodes by updating boolean fields
  public void connectDir(GamePiece toNode) {
    if (toNode.col - this.col == 1) {
      this.right = true;
      toNode.left = true;
    } else if (toNode.col - this.col == -1) {
      this.left = true;
      toNode.right = true;
    } else if (toNode.row - this.row == -1) {
      this.top = true;
      toNode.bottom = true;
    } else if (toNode.row - this.row == 1) {
      this.bottom = true;
      toNode.top = true;
    }
  }

}