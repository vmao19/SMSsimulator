import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ScreenWindow extends Frame implements WindowListener, Runnable, KeyListener, MouseListener{

	//window stuff
	private boolean isRunning,isDone;
	private Image imgBuffer;
	
	
	private ArrayList<Trajectory> listTraj = new ArrayList<Trajectory>();
	private ArrayList<Robot> listBot = new ArrayList<Robot>();
	private boolean paused, droneLabelToggle;
	
	//used to make trajectories
	private int numRows;
	private int numCol;
	private double ang;
	private int tempDir;
	private int colDir;
	private double colAng;
	private double tempAng = ang;
	private int trajSize; 
	private ArrayList<Arc> allArcs;
	private ArrayList<Ring> ringList;
	
	
	public ScreenWindow(int r, int c, int d, double a){
		super();
		
		imgBuffer = this.createImage(800, 900);
		
		//user inputs
		paused = true;
		droneLabelToggle = false;
		numRows = r;
		numCol = c;
		ang = a;
		tempDir = d;
		
		
		
		//other initialize
		ringList = new ArrayList<Ring>();
		
		
		//resizing code
		int horCircle = 700/numCol;
		int vertCircle = 700/numRows;
		if(horCircle > vertCircle){
			trajSize = vertCircle;
		}else{
			trajSize = horCircle;
		}
		
		
		//makes list of trajectories and robots
		for(int i = 1; i <= numRows; i++){

			if(i %2 ==0){
				if(d == 1){
					tempDir = -1;
				}else{
					tempDir=1;
				}
				tempAng = 2*Math.PI-ang;
			}else{
				tempDir = d;
				tempAng = ang;
			}
			colAng = tempAng;
			colDir = tempDir;
			for(int j = 1; j <= numCol; j++ ){
				
				if(j%2 == 0){
				if(tempDir == -1){
					tempDir = 1;
				}else{
					tempDir = -1;
				}
				tempAng = Math.PI - colAng;
				}else{
					tempDir = colDir;
					tempAng = colAng;
				}
				Trajectory tempTraj = new Trajectory(new Coordinate(50+(trajSize/2) + trajSize*(j-1),50+(trajSize/2)+trajSize*(i-1)),tempDir, trajSize-4);
				Robot tempBot = new Robot(tempTraj, tempAng,j,i);
				listTraj.add(tempTraj);
				listBot.add(tempBot);
				tempTraj.addBot(tempBot);
				
				
			}
		}
		
		//adds neighbors
		for(Trajectory e : listTraj){
			
			for(Trajectory f : listTraj){
				if(f.getVertex().geti() == e.getVertex().geti() && f.getVertex().getj() == e.getVertex().getj()-trajSize){
					e.addTop(f);
				}
				if(f.getVertex().geti() == e.getVertex().geti()+trajSize && f.getVertex().getj() == e.getVertex().getj()){
					e.addRight(f);
				}
				if(f.getVertex().geti() == e.getVertex().geti()-trajSize && f.getVertex().getj() == e.getVertex().getj()){
					e.addLeft(f);
				}
				if(f.getVertex().geti() == e.getVertex().geti() && f.getVertex().getj() == e.getVertex().getj()-trajSize){
					e.addTop(f);
				}
				if(f.getVertex().geti() == e.getVertex().geti() && f.getVertex().getj() == e.getVertex().getj()+trajSize){
					e.addBottom(f);
				}
			}
		}
		
		
		allArcs = new ArrayList<Arc>();
		for(Trajectory e : listTraj){
			
			allArcs.add(e.getArc1());
			allArcs.add(e.getArc2());
			allArcs.add(e.getArc3());
			allArcs.add(e.getArc4());
		}
		
		
		
		
		
		
		
		//makew3s first ring
		Ring ring = new Ring();
		for(Arc e : allArcs){
			
		}
		
		
		//more window stuff
		this.addWindowListener(this);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.setSize(800,900);
		this.setTitle("SCS");
		isRunning = true;
		isDone = false;
		this.setVisible(true);
		
		this.setResizable(false);
		
	}
	
	public void run(){
		while(isRunning){
			draw();
			
			//sets drones to sensing if they are at a critical point
			for(Robot e : listBot){
				
				
				
				//if at sensing angle
				if(Utilities.radianEq(e.getAngle(), 2*Math.PI) || Utilities.radianEq(e.getAngle(), Math.PI/2)  || Utilities.radianEq(e.getAngle(),Math.PI )|| Utilities.radianEq(e.getAngle(),3*(Math.PI/2))|| Utilities.radianEq(e.getAngle(), -Math.PI/2)  || Utilities.radianEq(e.getAngle(),-Math.PI )|| Utilities.radianEq(e.getAngle(),-3*(Math.PI/2))||Utilities.radianEq(e.getAngle(), -2*Math.PI)){
					
					e.setSensing(true);
					//System.out.println("1 " + e.get);
					
					
					//if drone is at left position
					if( (Utilities.radianEq(e.getAngle(),Math.PI ) ||  Utilities.radianEq(e.getAngle(),-Math.PI)  ) && !e.getFlipped()){
						//if no drone to the left
						if(e.getTraj().getLeft() != null &&(!e.getTraj().getLeft().hasRightCrit())){
							e.getTraj().removeBot(e);
							e.setTrajectory(e.getTraj().getLeft());
							e.getTraj().addBot(e);
							e.setAngle(e.getAngle()+Math.PI - 2*e.getAngle());
							e.setFlipped(true);
							//e.setSensing(false);
							
						}
						
					}
					//if drone is at right
					if( (Utilities.radianEq(e.getAngle(),2*Math.PI ) ||  Utilities.radianEq(e.getAngle(),-2*Math.PI  )||  Utilities.radianEq(e.getAngle(),0 )) && !e.getFlipped()){
						//if no drone to the right
						if(e.getTraj().getRight() != null &&(!e.getTraj().getRight().hasLeftCrit())){
							e.getTraj().removeBot(e);
							e.setTrajectory(e.getTraj().getRight());
							e.getTraj().addBot(e);
							e.setAngle(e.getAngle()+Math.PI-2*e.getAngle());
							e.setFlipped(true);
							//e.setSensing(false);
						}
					}
					
					if( (Utilities.radianEq(e.getAngle(), (Math.PI/2)) ||  Utilities.radianEq(e.getAngle(),-3*(Math.PI/2))) && !e.getFlipped()){
						//if no drone to the top
						if(e.getTraj().getTop() != null &&(!e.getTraj().getTop().hasBottomCrit())){
							e.getTraj().removeBot(e);
							e.setTrajectory(e.getTraj().getTop());
							e.getTraj().addBot(e);
							e.setAngle(e.getAngle()+Math.PI+2*(3*(Math.PI/2) - e.getAngle()));
							e.setFlipped(true);
							//e.setSensing(false);
						}
					}
					
					if( (Utilities.radianEq(e.getAngle(), -(Math.PI/2)) ||  Utilities.radianEq(e.getAngle(), 3*(Math.PI/2))) && !e.getFlipped()){
						//if no drone to the bottom
						if(e.getTraj().getBottom() != null &&(!e.getTraj().getBottom().hasTopCrit())){
							e.getTraj().removeBot(e);
							e.setTrajectory(e.getTraj().getBottom());
							e.getTraj().addBot(e);
							e.setAngle(e.getAngle()+Math.PI+2*(3*(Math.PI/2) - e.getAngle()));
							e.setFlipped(true);
							//e.setSensing(false);
						}
					}
				
				
					
				}else{
					
					e.setSensing(false);
					e.setFlipped(false);
				}
			}
			
			
			
			if(!paused){
			for(Robot e : listBot){
				if(e.getTraj().getDirection() == -1){
					e.setAngle(e.getAngle()+(Math.PI/64));
				}else{
					e.setAngle(e.getAngle()-(Math.PI/64));
				}
				e.setAngle(Utilities.coterminal(e.getAngle()));
				
			}
			
			}
			try{
				Thread.sleep(10);
				}catch(InterruptedException ie){
					ie.printStackTrace();
				}
		}
		isDone = true;
	}
	
	
	public void draw(){
		imgBuffer = this.createImage(this.getWidth(), this.getHeight());
		Graphics2D g2 = (Graphics2D)imgBuffer.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//background color
		g2.setColor(Color.white);
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
				
			//draws trajectories
			for(int i = 0; i < listTraj.size(); i++)
			{
				listTraj.get(i).draw(g2);
			}
			
			//draws robots
			for(int i = 0; i < listBot.size(); i++){
				listBot.get(i).setLabelToggle(droneLabelToggle);
				listBot.get(i).draw(g2);
			}
			
			
			
			
			
			//draws pause button
			
			if(paused){
			
				g2.setColor(Color.green);
				g2.fillRect(370, 820, 60, 60);
				g2.setColor(Color.black);
				g2.setFont(new Font("Callibri", Font.PLAIN, 12));
				g2.drawString("Resume", 375, 850);
			}else{
				
				g2.setColor(Color.red);
				g2.fillRect(370, 820, 60, 60);
				g2.setColor(Color.white);
				g2.setFont(new Font("Callibri", Font.PLAIN, 12));
				g2.drawString("Pause", 380, 850);
			}
			
			//draws toggle button
			
			if(droneLabelToggle){
				g2.setColor(Color.DARK_GRAY);
				g2.fillRect(440, 820, 60, 60);
				g2.setColor(Color.white);
				g2.setFont(new Font("Callibri", Font.PLAIN, 12));
				g2.drawString("IDs off", 450, 850);
			}else{
				g2.setColor(Color.LIGHT_GRAY);
				g2.fillRect(440, 820, 60, 60);
				g2.setColor(Color.black);
				g2.setFont(new Font("Callibri", Font.PLAIN, 12));
				g2.drawString("IDs on", 450, 850);
			}
			
			
		
		g2 = (Graphics2D)this.getGraphics();
		g2.drawImage(imgBuffer, 0, 0, this.getWidth(), this.getHeight(), 0, 0, this.getWidth(), this.getHeight(), null);
		g2.dispose();
	}
	
	
	

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		while(true){
			if(isDone){
				System.exit(0);
			}try{
				Thread.sleep(100);
			}catch(InterruptedException ie){
				ie.printStackTrace();
			}
			
		}
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		this.setVisible(false);
		isRunning = false;
		this.dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		
		
		
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
		//if click is inside the button, toggles pause
		if((arg0.getX() >= 370 && arg0.getX() <= 430) && (arg0.getY() >= 820 && arg0.getY() <= 880)){
			paused = !paused;
		}
		
		//drone label toggle
		if((arg0.getX() >= 440 && arg0.getX() <= 500) && (arg0.getY() >= 820 && arg0.getY() <= 880)){
			droneLabelToggle = !droneLabelToggle;
		}
		
		for(int i = 0; i < listBot.size(); i++){
			if(listBot.get(i).contains(new Coordinate(arg0.getX(), arg0.getY()))){
				listBot.get(i).getTraj().removeBot(listBot.get(i));
				listBot.remove(i);
			}
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
}