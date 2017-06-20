import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class Robot {

	private boolean sensing, hasFlipped;
	private Trajectory t;
	private double angle;
	private int sizeR;
	private int startingX;
	private int startingY;
	private boolean labelToggle;
	private boolean starving;
	
	
	public Robot( Trajectory traj, double ang, int x, int y)
	{
		labelToggle = false;
		hasFlipped = false;
		sensing = false;
		t = traj;
		angle = ang;
		sizeR = t.getSize()/5;
		startingX = x;
		startingY = y;
		starving = false;
	}
	
	public void setFlipped(boolean h){
		hasFlipped = h;
	}
	
	public boolean getFlipped(){
		return hasFlipped;
	}
	
	public Trajectory getTraj()
	{
		return t;
	}
	public void setTrajectory(Trajectory tr){
		t=tr;
	}
	
	public void setAngle(double a){
		angle = a;
	}
	
	public double getAngle()
	{
		return angle;
	}
	
	public void setSensing(boolean state){
		sensing = state;
	}
	
	public boolean getSensing(){
		return sensing;
	}
	
	public boolean isStarving(){
		return starving;
	}
	
	public void setStarving(boolean s){
		starving = s;
	}

	
	public boolean contains(Coordinate c){
		double centerX = t.getVertex().geti() + t.getSize()/2*Math.cos(angle);
		double centerY = t.getVertex().getj() - t.getSize()/2*Math.sin(angle);
		double leg1 = Math.abs(centerX - c.geti());
		double leg2 = Math.abs(centerY - c.getj());
		double hypotenuse = Math.sqrt(leg1 * leg1 + leg2 * leg2);
		return hypotenuse < sizeR / 2;
	}
	
	public void setLabelToggle(boolean b){
		labelToggle = b;
	}
	
	public void draw(Graphics2D g2)
	{
		
		
		
		
		if(starving){
			g2.setColor(Color.yellow);
		}else{
			g2.setColor(Color.black);
		}
		//g2.setColor(Color.black);
	
		g2.fill(new Ellipse2D.Double(t.getVertex().geti() + t.getSize()/2*Math.cos(angle) - sizeR/2, t.getVertex().getj() - t.getSize()/2*Math.sin(angle) - sizeR/2, sizeR, sizeR));//t.getSize()*Math.cos(angle)
	
		g2.setColor(Color.white);
		Font font = new Font("Callibri", Font.PLAIN, sizeR/3);//font.getStringBounds("Waller's Triangle Game", g2.getFontRenderContext()).getWidth())/2)
		
		g2.setFont(font);
		//g2.setStroke(new BasicStroke(sizeR/4));
		if(labelToggle){
		g2.drawString(startingX +","+startingY, (int)(t.getVertex().geti() + t.getSize()/2*Math.cos(angle)-sizeR/6), (int)(t.getVertex().getj() - t.getSize()/2*Math.sin(angle)+sizeR/6));
		}
	
	}
	
	public Coordinate getPosition(){
		return new Coordinate(t.getVertex().geti() + t.getSize()/2*Math.cos(angle) - sizeR/2,t.getVertex().getj() + t.getSize()/2*Math.sin(angle) - sizeR/2);
	}
	
}
