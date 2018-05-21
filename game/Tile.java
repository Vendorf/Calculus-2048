package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.AttributedString;

import javax.imageio.ImageIO;

public class Tile {

	public static final int WIDTH = 100;
	public static final int HEIGHT = 100;
	public static final int SLIDE_SPEED = 30;
	public static final int ARC_WIDTH = 15;
	public static final int ARC_HEIGHT =15;

	private int value;
	private Element element;
	
	private BufferedImage tileImage;
	private Color background;
	private Color text;
	private Font font;
	private Point slideTo;
	private int x;
	private int y;

	private boolean beginningAnimation = true;
	private double scaleFirst = 0.1;
	private BufferedImage beginningImage;
	
	private boolean combineAnimation = false;
	private double scaleCombine = 1.2;
	private BufferedImage combineImage;
	private boolean canCombine = true;

	public Tile(Element element, int x, int y) {
		this.element = element;
		this.x = x;
		this.y = y;
		slideTo = new Point(x, y);
		tileImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		beginningImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		combineImage = new BufferedImage(WIDTH * 2, HEIGHT * 2, BufferedImage.TYPE_INT_ARGB);
		drawImage();
	}

	public void update() {
		if (beginningAnimation) {
			AffineTransform transform = new AffineTransform();
			transform.translate(WIDTH / 2 - scaleFirst * WIDTH / 2, HEIGHT / 2 - scaleFirst * HEIGHT / 2);
			transform.scale(scaleFirst, scaleFirst);
			Graphics2D g2d = (Graphics2D) beginningImage.getGraphics();
			
		    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.setColor(new Color(0, 0, 0, 0));
			g2d.fillRect(0, 0, WIDTH, HEIGHT);
			g2d.drawImage(tileImage, transform, null);
//ORIGINAL			scaleFirst += 0.1;
			scaleFirst += 0.3;
			g2d.dispose();
			if(scaleFirst >= 1) beginningAnimation = false; 
		}
		else if(combineAnimation){
			AffineTransform transform = new AffineTransform();
			transform.translate(WIDTH / 2 - scaleCombine * WIDTH / 2, HEIGHT / 2 - scaleCombine * HEIGHT / 2);
			transform.scale(scaleCombine, scaleCombine);
			Graphics2D g2d = (Graphics2D) combineImage.getGraphics();
			
		    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.setColor(new Color(0, 0, 0, 0));
			g2d.fillRect(0, 0, WIDTH, HEIGHT);
			g2d.drawImage(tileImage, transform, null);
//ORIGINAL			scaleCombine -= 0.08;
			scaleCombine -= 0.8;
			g2d.dispose();
			if(scaleCombine <= 1) combineAnimation = false;
		}
	}
	
	public void render(Graphics2D g){
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		if(beginningAnimation){
			g.drawImage(beginningImage, x, y, null);
		}
		else if(combineAnimation){
			g.drawImage(combineImage, (int)(x + WIDTH / 2 - scaleCombine * WIDTH / 2), 
													(int)(y + HEIGHT / 2 - scaleCombine * HEIGHT / 2), null);
		}
		else{
			g.drawImage(tileImage, x, y, null);
		}
	}
	
	private void drawImage() {
		Graphics2D g = (Graphics2D) tileImage.getGraphics();
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
	    
	    if(element.isOperator()){
			background = new Color(0x000000);
			text = new Color(0xffffff);
	    }
	    else if(element.getVariable() == 1){
			background = new Color(0xe9e9e9);
			text = new Color(0x000000);
	    }
	    else{
	    switch(element.getPower()){
	    case 0:
			background = new Color(0xf7e12c);
			text = new Color(0xffffff);
			break;
	    case 1:
			background = new Color(0xe6daab);
			text = new Color(0x000000);
			break;
	    case 2:
			background = new Color(0xf79d3d);
			text = new Color(0xffffff);
			break;
	    case 3:
			background = new Color(0xf28007);
			text = new Color(0xffffff);
			break;
	    case 4:
			background = new Color(0xff0000);
			text = new Color(0xffffff);
			break;
	    default:
			background = new Color(0xf7e12c);
			text = new Color(0xffffff);
			break;
	    }}
	    
//		if (value == 2) {
//			background = new Color(0xe9e9e9);
//			text = new Color(0x000000);
//		}
//		else if (value == 4) {
//			background = new Color(0xe6daab);
//			text = new Color(0x000000);
//		}
//		else if (value == 8) {
//			background = new Color(0xf79d3d);
//			text = new Color(0xffffff);
//		}
//		else if (value == 16) {
//			background = new Color(0xf28007);
//			text = new Color(0xffffff);
//		}
//		else if (value == 32) {
//			background = new Color(0xf55e3b);
//			text = new Color(0xffffff);
//		}
//		else if (value == 64) {
//			background = new Color(0xff0000);
//			text = new Color(0xffffff);
//		}
//		else if (value == 128) {
//			background = new Color(0xe9de84);
//			text = new Color(0xffffff);
//		}
//		else if (value == 256) {
//			background = new Color(0xf6e873);
//			text = new Color(0xffffff);
//		}
//		else if (value == 512) {
//			background = new Color(0xf5e455);
//			text = new Color(0xffffff);
//		}
//		else if (value == 1024) {
//			background = new Color(0xf7e12c);
//			text = new Color(0xffffff);
//		}
//		else if (value == 2048) {
//			background = new Color(0xffe400);
//			text = new Color(0xffffff);
//		}
//		else if(value == 0){
//			background = Color.lightGray;
//			text = Color.black;
//		}
//		else{
//			background = new Color(0x000000);
//			text = new Color(0xffffff);
//		}
		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);

		g.setColor(background);
		g.fillRoundRect(0, 0, WIDTH, HEIGHT, ARC_WIDTH, ARC_HEIGHT);

		g.setColor(text);

		if (value <= 64) {
			font = Game.main.deriveFont(36f);
			g.setFont(font);
		}
		else {
			font = Game.main;
			g.setFont(font);
		}
//Change from value to element.toString()
		if(element.isOperator()){
			if(element.getOperator() == Operators.INTEGRAL){
				int drawX = WIDTH / 2 - DrawUtils.getMessageWidth("∫dx", font, g) / 2;
				int drawY = HEIGHT / 2 + DrawUtils.getMessageHeight("∫dx" + value, font, g) /2;
				g.drawString("∫dx", drawX, drawY);
			}else{
				int drawX = WIDTH / 2 - DrawUtils.getMessageWidth("d/dx", font, g) / 2;
				int drawY = HEIGHT / 2 + DrawUtils.getMessageHeight("d/dx" + value, font, g) /2;
				g.drawString("d/dx", drawX, drawY);
			}
		}
		else{
		Font powerFont = new Font("Bebas Neue Regular", Font.PLAIN, 14);
		//g.drawString("" + value, drawX, drawY);
		String coefficient = "+" + (element.getCoefficient() == 1 ? "" : element.getCoefficient());
coefficient = "+C"; //overwrites a mess of numbers
		String variable = element.getVariable() == 2 ? "x" : element.getVariable() == 1 ? "C" : "";
variable = element.getVariable() == 2 ? "x" : ""; //more mess overriding
		String power = "" + (element.getPower() == 1 || element.getPower() == 0 ? "" : element.getPower());
		
		int drawX = WIDTH / 2 - (DrawUtils.getMessageWidth(coefficient + variable, font, g) + DrawUtils.getMessageWidth(power, powerFont, g)) / 2;
		int drawY = HEIGHT / 2 + (DrawUtils.getMessageHeight("" + value, font, g) + DrawUtils.getMessageHeight(power, powerFont, g)) / 2;
//		AttributedString draw = new AttributedString("" + coefficient + variable + element.getPower());
//		draw.addAttribute(TextAttribute.FONT, font);
//		draw.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, (coefficient.length() + variable.length()), (coefficient.length() + variable.length())+1);
		//draw.addAttribute(TextAttribute.SIZE, 12);
		g.setFont(font);
		g.drawString(coefficient + variable, drawX, drawY);
		int width = DrawUtils.getMessageWidth(coefficient + variable, font, g);
		g.setFont(powerFont);
		g.drawString(power, drawX+width, drawY-14);
		}
		g.dispose();
	}
	
//????
	public void print(){
		try {
			// Creates an image file
			ImageIO.write(tileImage, "gif", new File(value + ".gif"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to export the image.");
		}
	}

//Remove
	public int getValue() {
		return value;
	}

	public Element getElement(){
		return element;
	}
	
//Remove
	public void setValue(int value) {
		this.value = value;
		drawImage();
	}

	public void setElement(Element element){
		this.element = element;
		drawImage();
	}
	
	public void redraw(){
		drawImage();
	}
	
	public Point getSlideTo() {
		return slideTo;
	}

	public void setSlideTo(Point slideTo) {
		this.slideTo = slideTo;
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
	
	public void setCombineAnimation(boolean combineAnimation){
		this.combineAnimation = combineAnimation;
		if(combineAnimation) scaleCombine = 1.2;
	}
	
	public boolean isCombineAnimation(){
		return combineAnimation;
	}

	public boolean canCombine() {
		return canCombine;
	}

	public void setCanCombine(boolean canCombine) {
		this.canCombine = canCombine;
	}
}
