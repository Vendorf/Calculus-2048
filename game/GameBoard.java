package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

//import javax.sound.sampled.Clip;

public class GameBoard {

	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int UP = 2;
	public static final int DOWN = 3;

	public static final int ROWS = 5;
	public static final int COLS = 5;

	private final int startingTiles = 2;
	private Tile[][] board;
	private boolean dead;
	private boolean won;
	private BufferedImage gameBoard;
	private int x;
	private int y;

	private static int SPACING = 10;
	public static int BOARD_WIDTH = (COLS + 1) * SPACING + COLS * Tile.WIDTH;
	public static int BOARD_HEIGHT = (ROWS + 1) * SPACING + ROWS * Tile.HEIGHT;

	private long elapsedMS;
	private long startTime;
	private boolean hasStarted;

//	private ScoreManager scores;
	private Leaderboards lBoard;
	//private AudioHandler audio;
	private int saveCount = 0;

	public GameBoard(int x, int y) {
		this.x = x;
		this.y = y;
		board = new Tile[ROWS][COLS];
		gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		createBoardImage();

//		audio = AudioHandler.getInstance();
//		audio.load("click.wav", "click");
//		audio.load("MainSong.mp3", "BG");
//		audio.adjustVolume("BG", -10);
//		audio.play("BG", Clip.LOOP_CONTINUOUSLY);

		lBoard = Leaderboards.getInstance();
		lBoard.loadScores();
//		scores = new ScoreManager(this);
//		scores.loadGame();
//		scores.setBestTime(lBoard.getFastestTime());
//		scores.setCurrentTopScore(lBoard.getHighScore());
		if(true){
			start();
		}
//		if(scores.newGame()){
//			start();
//			scores.saveGame();
//		}
//		else{
//			for(int i = 0; i < scores.getBoard().length; i++){
//				if(scores.getBoard()[i] == 0) continue;
//Fix this?
//It wants to put a 0 at the location; we need it to put an element
				//spawn(i / ROWS, i % COLS, scores.getBoard()[i]);
//				spawn(i / ROWS, i % COLS, new Element(0, scores.getBoard()[i], 0));
//			}
			// not calling setDead because we don't want to save anything
			dead = checkDead();
			// not coalling setWon because we don't want to save the time
			won = checkWon();
		}
//	}

	public void reset(){
		board = new Tile[ROWS][COLS];
		start();
//		scores.saveGame();
		dead = false;
		won = false;
		hasStarted = false;
		startTime = System.nanoTime();
		elapsedMS = 0;
		saveCount = 0;
	}

	private void start() {
		for (int i = 0; i < startingTiles; i++) {
			spawnRandom();
		}
	}

	/** Debug method */
	private void spawn(int row, int col, Element element) {
		board[row][col] = new Tile(element, getTileX(col), getTileY(row));
	}

	private void createBoardImage() {
		Graphics2D g = (Graphics2D) gameBoard.getGraphics();
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
		g.setColor(Color.lightGray);

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				int x = SPACING + SPACING * col + Tile.WIDTH * col;
				int y = SPACING + SPACING * row + Tile.HEIGHT * row;
				g.fillRoundRect(x, y, Tile.WIDTH, Tile.HEIGHT, Tile.ARC_WIDTH, Tile.ARC_HEIGHT);
			}
		}
	}

	public void update() {
		saveCount++;
		if (saveCount >= 120) {
			saveCount = 0;
//			scores.saveGame();
		}
		
		if (!won && !dead) {
			if (hasStarted) {
				elapsedMS = (System.nanoTime() - startTime) / 1000000;
//				scores.setTime(elapsedMS);
			}
			else {
				startTime = System.nanoTime();
			}
		}

		checkKeys();

//		if (scores.getCurrentScore() > scores.getCurrentTopScore()) {
//			scores.setCurrentTopScore(scores.getCurrentScore());
//		}

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if (current == null) continue;
				current.update();
				resetPosition(current, row, col);
				if (current.getValue() == 2048) {
					setWon(true);
				}
			}
		}
	}

	public void render(Graphics2D g) {
		BufferedImage finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) finalBoard.getGraphics();
		g2d.setColor(new Color(0, 0, 0, 0));
		g2d.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
		g2d.drawImage(gameBoard, 0, 0, null);

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if (current == null) continue;
				current.render(g2d);
			}
		}

		g.drawImage(finalBoard, x, y, null);
		g2d.dispose();

		
	}

	private void resetPosition(Tile tile, int row, int col) {
		if (tile == null) return;

		int x = getTileX(col);
		int y = getTileY(row);

		int distX = tile.getX() - x;
		int distY = tile.getY() - y;

		if (Math.abs(distX) < Tile.SLIDE_SPEED) {
			tile.setX(tile.getX() - distX);
		}

		if (Math.abs(distY) < Tile.SLIDE_SPEED) {
			tile.setY(tile.getY() - distY);
		}

		if (distX < 0) {
			tile.setX(tile.getX() + Tile.SLIDE_SPEED);
		}
		if (distY < 0) {
			tile.setY(tile.getY() + Tile.SLIDE_SPEED);
		}
		if (distX > 0) {
			tile.setX(tile.getX() - Tile.SLIDE_SPEED);
		}
		if (distY > 0) {
			tile.setY(tile.getY() - Tile.SLIDE_SPEED);
		}
	}

	public int getTileX(int col) {
		return SPACING + col * Tile.WIDTH + col * SPACING;
	}

	public int getTileY(int row) {
		return SPACING + row * Tile.HEIGHT + row * SPACING;
	}

	private boolean checkOutOfBounds(int direction, int row, int col) {
		if (direction == LEFT) {
			return col < 0;
		}
		else if (direction == RIGHT) {
			return col > COLS - 1;
		}
		else if (direction == UP) {
			return row < 0;
		}
		else if (direction == DOWN) {
			return row > ROWS - 1;
		}
		return false;
	}

	private boolean move(int row, int col, int horizontalDirection, int verticalDirection, int direction) {
		boolean canMove = false;
		Tile current = board[row][col];
		if (current == null) return false;
		boolean move = true;
		int newCol = col;
		int newRow = row;
//This needs to be modified such that a check on the row/column is performed to find if there are operators
//If there are, send the row/column to a method that will operate on it
//If not, send the row/column to a method that will merge it (same as else if block code, just pull it out and edit getValue)
		while (move) {
			newCol += horizontalDirection;
			newRow += verticalDirection;
			if (checkOutOfBounds(direction, newRow, newCol)) break;
			if (board[newRow][newCol] == null) {
				board[newRow][newCol] = current;
				canMove = true;
				board[newRow - verticalDirection][newCol - horizontalDirection] = null;
				board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
			}
//Here is the code for combining tiles; if the values are equal and they can combine (IE, not equal to null), they combine
			//else if (board[newRow][newCol].getValue() == current.getValue() && board[newRow][newCol].canCombine()) {
			else if (ElementComparator.canMerge(board[newRow][newCol].getElement(), current.getElement()) && board[newRow][newCol].canCombine()) {
				board[newRow][newCol].setCanCombine(false);
				//board[newRow][newCol].setValue(board[newRow][newCol].getValue() * 2);
				mergeTiles(board[newRow][newCol], current);
				canMove = true;
				board[newRow - verticalDirection][newCol - horizontalDirection] = null;
				board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
				board[newRow][newCol].setCombineAnimation(true);
//				scores.setCurrentScore(scores.getCurrentScore() + board[newRow][newCol].getValue());
			}
			else {
				move = false;
			}
		}
		return canMove;
	}
	
	private void mergeTiles(Tile t1, Tile t2){
		Element a = t1.getElement();
		a.setCoefficient((t1.getElement().getCoefficient() + t2.getElement().getCoefficient()));
		t1.setElement(a);
	}
	
	private boolean colOperate(int col, Operators operation){
		Tile[] column = new Tile[board.length];
		for(int row = 0; row < ROWS; row++){
			column[row] = board[row][col];
		}
		ArrayList<Tile> operatedCol = null;
		if(operation == Operators.INTEGRAL){
			operatedCol = integral(column);
			if(operatedCol.size() > board.length){
				setDead(true);
			}
		}
		else{
			operatedCol = derivative(column);
		}
		if(operatedCol.size() == 0){
			return false;
		}
		for(int row = 0; row < ROWS; row++){
			if(row < operatedCol.size()){
				board[row][col] = operatedCol.get(row);
				board[row][col].redraw();
			}
			else{
				board[row][col] = null;
			}
		}
		return true;
	}

	
//fix to be able to conform to direction
	private boolean rowOperate(int r, Operators operation){
		Tile[] row = new Tile[board[r].length];
		for(int col = 0; col < COLS; col++){
			row[col] = board[r][col];
		}
		ArrayList<Tile> operatedRow = null;
		if(operation == Operators.INTEGRAL){
			operatedRow = integral(row);
			if(operatedRow.size() > board[r].length){
				setDead(true);
			}
		}
		else{
			operatedRow = derivative(row);
		}
		if(operatedRow.size() == 0){
			return false;
		}
		for(int col = 0; col < COLS; col++){
			if(col < operatedRow.size()){
				board[r][col] = operatedRow.get(col);
				board[r][col].redraw();
			}
			else{
				board[r][col] = null;
			}
		}
		return true;
	}
	
	private ArrayList<Tile> integral(Tile[] tiles){
		ArrayList<Tile> integratedTiles = new ArrayList<>();
		for(Tile each : tiles){
			if(each == null || each.getElement().isOperator()){
				continue;
			}
			if(each.getElement().getVariable() == 1){
				each.getElement().setVariable(2);
				each.getElement().setPower(1);
			}
			else{
				each.getElement().setPower(each.getElement().getPower()+1);
			}
			integratedTiles.add(each);
		}
		if(!integratedTiles.isEmpty()){
			integratedTiles.add(new Tile(new Element(1, 1, 1), integratedTiles.get(0).getX(), integratedTiles.get(0).getY()));
		}
		return integratedTiles;
	}
	
	private ArrayList<Tile> derivative(Tile[] tiles){
		ArrayList<Tile> derivedTiles = new ArrayList<>();
		for(Tile each : tiles){
			if(each == null){
				continue;
			}
			if(each.getElement().isOperator()){
				continue;
			}
			int variable = each.getElement().getVariable();
			if(variable == 1){ //constant
				continue;
			}
			if(each.getElement().getPower() == 1){
				each.getElement().setVariable(1);
			}
			each.getElement().setPower(each.getElement().getPower()-1);
			derivedTiles.add(each);
		}
		return derivedTiles;
	}
	
	public void moveTiles(int direction) {
		boolean canMove = false;
		int horizontalDirection = 0;
		int verticalDirection = 0;

		if (direction == LEFT) {
			horizontalDirection = -1;
			for (int row = 0; row < ROWS; row++) {
				boolean operate = false;
				Operators operation = null;
				//Checks to see if any operators in the row
				for(int col = 0; col < COLS; col++){
					if(board[row][col] == null){
						continue;
					}
					if(board[row][col].getElement().isOperator()){
						operate = true;
						operation = board[row][col].getElement().getOperator();
						break;
					}
				}
				if(operate){
					boolean operated = rowOperate(row, operation);
					if(operated){
						continue;
					}
				}
				for (int col = 0; col < COLS; col++) {
					if (!canMove)
						canMove = move(row, col, horizontalDirection, verticalDirection, direction);
					else move(row, col, horizontalDirection, verticalDirection, direction);
				}
			}
		}
		else if (direction == RIGHT) {
			horizontalDirection = 1;
			for (int row = 0; row < ROWS; row++) {
				boolean operate = false;
				Operators operation = null;
				//Checks to see if any operators in this row
				for(int col = COLS - 1; col >= 0; col--){
					if(board[row][col] == null){
						continue;
					}
					if(board[row][col].getElement().isOperator()){
						operate = true;
						operation = board[row][col].getElement().getOperator();
						break;
					}
				}
				if(operate){
					boolean operated = rowOperate(row, operation);
					if(operated){
						continue;
					}
				}
				for (int col = COLS - 1; col >= 0; col--) {
					if (!canMove)
						canMove = move(row, col, horizontalDirection, verticalDirection, direction);
					else move(row, col, horizontalDirection, verticalDirection, direction);
				}
			}
		}
		else if (direction == UP) {
			verticalDirection = -1;
			for (int col = 0; col < COLS; col++){
				boolean operate = false;
				Operators operation = null;
				//Checks to see if any operators in this column
				for(int row = 0; row < ROWS; row++){
					if(board[row][col] == null){
						continue;
					}
					if(board[row][col].getElement().isOperator()){
						operate = true;
						operation = board[row][col].getElement().getOperator();
						break;
					}
				}
				if(operate){
					boolean operated = colOperate(col, operation);
					if(operated){
						continue;
					}
				}
				
				for (int row = 0; row < ROWS; row++) {
					if (!canMove)
						canMove = move(row, col, horizontalDirection, verticalDirection, direction);
					else move(row, col, horizontalDirection, verticalDirection, direction);
				}
			}
		}
		else if (direction == DOWN) {
			verticalDirection = 1;
			//this used to go rows then cols, now is cols then rows. May create problems? Same on up
			for (int col = 0; col < COLS; col++){
				boolean operate = false;
				Operators operation = null;
				//Checks to see if any operators in this column
				for(int row = ROWS - 1; row >= 0; row--){
					if(board[row][col] == null){
						continue;
					}
					if(board[row][col].getElement().isOperator()){
						operate = true;
						operation = board[row][col].getElement().getOperator();
						break;
					}
				}
				if(operate){
					boolean operated = colOperate(col, operation);
					if(operated){
						continue;
					}
				}
				
				for (int row = ROWS - 1; row >= 0; row--){
					if (!canMove)
						canMove = move(row, col, horizontalDirection, verticalDirection, direction);
					else move(row, col, horizontalDirection, verticalDirection, direction);
				}
			}
		}
		else {
			System.out.println(direction + " is not a valid direction.");
		}

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if (current == null) continue;
				current.setCanCombine(true);
			}
		}

		if (canMove) {
			//audio.play("click", 0);
			spawnRandom();
			setDead(checkDead());
		}
	}

	// MODIFIED
	private boolean checkDead() {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if (board[row][col] == null) return false;
				boolean canCombine = checkSurroundingTiles(row, col, board[row][col]);
				if (canCombine) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean checkWon() {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if(board[row][col] == null) continue;
				if(board[row][col].getValue() >= 2048) return true;
			}
		}
		return false;
	}

	private boolean checkSurroundingTiles(int row, int col, Tile tile) {
		if (row > 0) {
			Tile check = board[row - 1][col];
			if (check == null) return true;
			if (tile.getValue() == check.getValue()) return true;
		}
		if (row < ROWS - 1) {
			Tile check = board[row + 1][col];
			if (check == null) return true;
			if (tile.getValue() == check.getValue()) return true;
		}
		if (col > 0) {
			Tile check = board[row][col - 1];
			if (check == null) return true;
			if (tile.getValue() == check.getValue()) return true;
		}
		if (col < COLS - 1) {
			Tile check = board[row][col + 1];
			if (check == null) return true;
			if (tile.getValue() == check.getValue()) return true;
		}
		return false;
	}

	private void spawnRandom() {
		Random random = new Random();
		boolean notValid = true;

//Optimize: store set of empty spaces and choose random from there
		while (notValid) {
			int location = random.nextInt(ROWS * COLS);
			int row = location / ROWS;
			int col = location % COLS;
			Tile current = board[row][col];
			if (current == null) {
				int type = random.nextInt(4);
				Tile tile;
				if(type < 3){
				//int value = random.nextInt(10) < 9 ? 2 : 4;
					int power = random.nextInt(5) < 4 ? 1 : 2;
					tile = new Tile(new Element(2, 1, power), getTileX(col), getTileY(row));
				}
				else{
					Operators op = random.nextInt(2) == 0 ? Operators.INTEGRAL : Operators.DERIVATIVE;
					tile = new Tile(new Element(op), getTileX(col), getTileY(row));
				}
				board[row][col] = tile;
				notValid = false;
			}
		}
	}

	private void checkKeys() {
		if (!Keys.pressed[KeyEvent.VK_LEFT] && Keys.prev[KeyEvent.VK_LEFT]) {
			moveTiles(LEFT);
			if (!hasStarted) hasStarted = !dead;
		}
		if (!Keys.pressed[KeyEvent.VK_RIGHT] && Keys.prev[KeyEvent.VK_RIGHT]) {
			moveTiles(RIGHT);
			if (!hasStarted) hasStarted = !dead;
		}
		if (!Keys.pressed[KeyEvent.VK_UP] && Keys.prev[KeyEvent.VK_UP]) {
			moveTiles(UP);
			if (!hasStarted) hasStarted = !dead;
		}
		if (!Keys.pressed[KeyEvent.VK_DOWN] && Keys.prev[KeyEvent.VK_DOWN]) {
			moveTiles(DOWN);
			if (!hasStarted) hasStarted = !dead;
		}
	}

	public int getHighestTileValue(){
		int value = 2;
		for(int row = 0; row < ROWS; row++){
			for(int col = 0; col < COLS; col++){
				if(board[row][col] == null) continue;
				if(board[row][col].getValue() > value) value = board[row][col].getValue();
			}
		}
		return value;
	}
	
	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		if(!this.dead && dead){
			lBoard.addTile(getHighestTileValue());
//			lBoard.addScore(scores.getCurrentScore());
			lBoard.saveScores();
		}
		this.dead = dead;
	}

	public Tile[][] getBoard() {
		return board;
	}
	
	public void setBoard(Tile[][] board) {
		this.board = board;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isWon() {
		return won;
	}

	public void setWon(boolean won) {
		if(!this.won && won && !dead){ 
//			lBoard.addTime(scores.getTime());
			lBoard.saveScores();
		}
		this.won = won;
	}
	
//	public ScoreManager getScores(){
//		return scores;
//	}
}
