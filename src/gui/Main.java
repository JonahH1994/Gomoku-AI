/* NetId(s): jo356, sr949. Time spent: 20 hours, 30 minutes. */

package gui;

import java.util.Enumeration;
import java.util.Scanner;

import clui.ConsoleController;
import controller.Controller;
import controller.DumbAI;
import controller.RandomAI;
import controller.SmartAI;
import model.Board;
import model.Game;
import model.GameListener;
import model.Location;
import model.NotImplementedException;
import model.Player;

import java.awt.*;
import java.awt.event.*;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

//import com.sun.jmx.snmp.Enumerated;

public class Main extends JFrame {
	
	private Controller playerX;
	private Controller playerO;
	private Player next ;
	private Game g = new Game() ;
	
	public static Controller createController(Player p, String controll ) {
		while(true) {
			switch(controll) {
			case "Human":
				return new ConsoleController(p);
			case "Dumb AI":
				return new DumbAI(p);
			case "Random AI":
				return new RandomAI(p);
			case "Smart AI":
				return new SmartAI(p);
			default:
				//JOptionPane.showMessageDialog(JFrame.getFrames()[0], "");;
			}
		}
	}
	
	public class mnu extends JMenu implements MouseListener {

		private JMenu thisMenu;
		private JPanel panel ;
		public int j = 1 ;
		
		public mnu( String s, JPanel p ) {
			setText(s);
			addMouseListener(this);
			thisMenu = this ;
			panel = p ;
		}
		
		public void selectPlayer() {
			JLabel text = new JLabel() ;
			text.setText("Select Player " + j );
			
			JRadioButton but1 = new JRadioButton() ;
			but1.setText("Human");
			but1.setName("Human");
			JRadioButton but2 = new JRadioButton() ;
			but2.setText("Dumb AI");
			but2.setName("Dumb AI");
			JRadioButton but3 = new JRadioButton() ;
			but3.setText("Random AI");
			but3.setName("Random AI");
			JRadioButton but4 = new JRadioButton() ;
			but4.setText("Smart AI");
			but4.setName("Smart AI");
			
			ButtonGroup group = new ButtonGroup() ;
			
			group.add(but1) ;
			group.add(but2) ;
			group.add(but3) ;
			group.add(but4) ;
			
			panel.add(text) ;
			panel.add(but1) ;
			panel.add(but2) ;
			panel.add(but3) ;
			panel.add(but4) ;
			panel.add(new ButtonClick("OK", group, this)) ;
			
			panel.revalidate();
			panel.repaint() ;
			j++ ;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			
			if ( e.getSource() == thisMenu) {
					selectPlayer();
				} else {
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}
	
	public class board extends JPanel implements GameListener {
		
		public board(int boardDim) {
			
			setLayout(new GridLayout(boardDim,boardDim)) ;
			for ( int i = 0; i < boardDim; i++ ) 
				for( int j = 0; j < boardDim; j++ ) {
					if ( i == 0 || j == 0 ) {
						add(new Tile(i,j)).setBackground(Color.BLUE);
					}
					else
						add(new Tile(i,j)) ;
				}
			g.addListener(this);
		}
		
		@Override
		public void gameChanged(Game gm) {
			if(gm.getBoard().getWinner() != null ) {
				JOptionPane.showMessageDialog(JFrame.getFrames()[0], next + " has won!");
			} else if (gm.getBoard().getState() == Board.State.DRAW)
				JOptionPane.showMessageDialog(JFrame.getFrames()[0], "The Game Is A Draw!");

			if (next != null) {
				Controller play;
				if (next == Player.X)
					play = playerX ;
				else
					play = playerO ;

				if(!(play instanceof ConsoleController) && play != null ) {
					for( Location loc : Board.LOCATIONS) {
						if(gm.getBoard().get(loc) != null) {
							int index = (loc.row+1) * (Board.NUM_COLS+1) + loc.col + 1;

							Tile h = (Tile)this.getComponents()[index] ;
							h.selected = true ;
							h.p = gm.getBoard().get(loc);
							h.repaint();
						}
					}
				}
				if ( next == Player.X)
					next = Player.O;
				else
					next = Player.X;
		}
		}
	}
	
	public class ButtonClick extends Button implements MouseListener {
		
		private ButtonGroup buttons;
		private mnu thisMenu ;
		
		public ButtonClick(String s, ButtonGroup b, mnu menu ) {
			setLabel(s) ;
			buttons = b ;
			thisMenu = menu ;
			addMouseListener(this) ;
		}
		
		@Override 
		public void mouseClicked(MouseEvent e) {
			AbstractButton but = buttons.getElements().nextElement();
			
			for( Enumeration<AbstractButton> but2 = buttons.getElements(); but2.hasMoreElements(); ) {
				
				if (but.isSelected()) {
					break;
				}
				but = but2.nextElement() ;
			}
			
			if (thisMenu.j == 2)
				playerX = createController(Player.X, but.getName()) ;
			else
				playerO = createController(Player.O, but.getName()) ;
			
			for(Enumeration<AbstractButton> but2 = buttons.getElements(); but2.hasMoreElements();) {
				AbstractButton but1 = but2.nextElement() ;
				if (!but1.isSelected())
					but1.setVisible(false);
			}
			
			this.setVisible(false);
			
			if (thisMenu.j == 2) {
				thisMenu.selectPlayer(); 
			} else {
				next = Player.X ;
				if ( !(playerX instanceof ConsoleController) ) {
					g.addListener(playerX);
				}
				if ( !(playerO instanceof ConsoleController)  ) {
					g.addListener(playerO);
				}
			}
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}
	
	public class Tile extends JPanel implements MouseListener, GameListener {

		private int i, j ;
		private Player p ;
		private boolean selected ;
		
		public Tile(int row, int col) {
			i = row ; 
			j = col ;
			p = null ;
			selected = false ;
			setPreferredSize(new Dimension(10,10));
			addMouseListener(this);
			g.addListener(this);
		}
		
		public Tile(int row, int col, boolean n) {
			i = row ; 
			j = col ;
			p = null ;
			selected = n ;
			setPreferredSize(new Dimension(10,10));
			addMouseListener(this);
			g.addListener(this);
		}
		
		@Override
		public void gameChanged( Game g1 ) {
			paintImmediately(0,0,getWidth(),getHeight());
		}
		
		public @Override void paint(Graphics g) {
			
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, getWidth()-1, getHeight()-1) ;
				g.setColor(Color.BLACK);
				if ( this.j == 0 ) {
					g.drawString("" + (this.i), 20, 20);
				} else if (this.i == 0 ) {
					g.drawString("" + (this.j),  20, 20);
				} 
				g.drawRect(0, 0, getWidth() -1, getHeight() -1);
			if (!selected) {
			} else {

				if (p == Player.X) {
					g.setColor(Color.RED);
					g.drawLine(0, 0, getWidth()-10, getHeight()-10);
					g.drawLine(0, getHeight()-10, getWidth()-10, 0);
					selected = false ;

				} else {
					g.setColor(Color.BLACK);
					g.fillArc(0, 0, getWidth()-10, getHeight()-10, 0, 360);
					selected = false ;	
	
				}
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if ( playerX == null || playerO == null ) {
				
			} else {
				if ( g.getBoard().getWinner() == null) {
					selected = true ;
					p = next ;
					repaint() ;
					Location loc = new Location(i-1,j-1) ;
					g.submitMove(next, loc);
				} else 
					JOptionPane.showMessageDialog(JFrame.getFrames()[0], "Game is Over");
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}
	
	public Main() {
		super("Main") ;
		
		int boardDim = 10 ; // For a 9x9 board
		
		setName("Main GUI") ;
		setLayout(new BorderLayout()) ;
		//JPanel board = new JPanel() ;
		JPanel board = new board(boardDim) ;
		JPanel infoPane = new JPanel() ;
		JPanel bPanel = new JPanel() ;
		
		JMenuBar menuBar = new JMenuBar() ;
		menuBar.add(new mnu("New Game", infoPane)) ;
		setJMenuBar(menuBar);
		
		int width = Toolkit.getDefaultToolkit().getScreenSize().width ;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height ;
		infoPane.setPreferredSize(new Dimension((3*width/10), height));
		board.setPreferredSize(new Dimension((6*width/10), 6*height/10)); 
		bPanel.setPreferredSize(new Dimension(4*height/10, 4*height/10));
		
		infoPane.setName("Info Pane");
		board.setName("Board");
		bPanel.setName("Bottom Panel");
		
		add(board, BorderLayout.CENTER) ;
		add(infoPane, BorderLayout.WEST);
		add(bPanel, BorderLayout.SOUTH) ;
		
		Dimension s = Toolkit.getDefaultToolkit().getScreenSize() ;
		setSize(s.width, s.height);
		pack() ;
		
		//throw new NotImplementedException();
	}
	
	public static void main(String[] args) {
		new Main().setVisible(true);
	}
}
