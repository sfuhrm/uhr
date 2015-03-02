package de.tynne.uhr;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A Swing based analog clock.
 * @author Stephan Fuhrmann
 */
public class Uhr extends JPanel {
	
	@Override
	public void paint(Graphics g) {
		
		super.paint(g);
		
		int w = getWidth();
		int h = getHeight();
		int cx = w / 2;
		int cy = h / 2;
				
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		int hour = gregorianCalendar.get(Calendar.HOUR);
		int min = gregorianCalendar.get(Calendar.MINUTE);
		int sec = gregorianCalendar.get(Calendar.SECOND);
		
		int r;
		if (w > h) {
			r = h / 2;
		} else {			
			r = w / 2;
		}
		
		Graphics2D g2d = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHints(rh);
		
		drawClock(g2d, cx, r, cy, sec, min, hour);
	}

	/**
	 * Draws the clock.
	 * @param g2d the graphics context
	 * @param cx center x coordinate.
	 * @param cy center y coordinate
	 * @param r radius of the circle.
	 * @param seconds seconds (0..59)
	 * @param minutes minutes (0..59)
	 * @param hours hours (0..11)
	 */
	private void drawClock(Graphics2D g2d, int r, int cx, int cy, int seconds, int minutes, int hours) {
		// draw background circle
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillOval(cx - r, cy - r, 2*r, 2*r);
		
		// minute/second ticks
		g2d.setColor(Color.DARK_GRAY);
		for (int i=0; i < 60; i++) {
			drawTick(g2d, r, i, 60, 2, 0.05f);
		}		
		
		// hour ticks
		g2d.setColor(Color.DARK_GRAY);
		for (int i=0; i < 12; i++) {
			drawTick(g2d, r, i, 12, 4, 0.2f);
		}
		
		// seconds
		g2d.setColor(new Color(0f, 0f , 1f, 0.6f));
		drawArm(g2d, r, seconds, 60, 2.1f);
		// minutes
		g2d.setColor(new Color(0f, 0f , 0f, 0.9f));
		drawArm(g2d, r*0.9, minutes, 60, 4.0f);
		// hours
		g2d.setColor(new Color(1f, 0f , 0f, 0.9f));
		drawArm(g2d, r*0.80, hours + minutes  / 60., 12, 4.0f);				
	}
	
	/**
	 * Draws a clocks tick. The ticks are markers giving a hint on the
	 * position of the full hour / minute.
	 * @param g graphics context to draw on
	 * @param r circle radius
	 * @param fraction fraction of <code>of</code>
	 * @param of total amount of units (for example 60 for seconds)
	 * @param width the pixel width of the ticks.
	 * @param length the l
	 * ength in fractions (0..1) 
	 */
	private void drawTick(Graphics2D g, double r, double fraction, double of, float width, float length) {
		int w = getWidth();
		int h = getHeight();
		int cx = w / 2;
		int cy = h / 2;
		
		int dx = (int) (r * Math.sin(2. * Math.PI * fraction / of));
		int dy = -(int) (r * Math.cos(2. * Math.PI * fraction / of));

		g.setStroke(new BasicStroke(width));
		g.drawLine((int)(cx + dx * (1.-length)), (int)(cy + dy * (1.-length)), cx + dx, cy + dy);
	}	
	
	/**
	 * Draws a clocks arm. The arms show the time. 
	 * @param g graphics context to draw on
	 * @param r arm length / radius
	 * @param fraction fraction of <code>of</code>
	 * @param of total amount of units (for example 60 for seconds)
	 */
	private void drawArm(Graphics2D g, double r, double fraction, double of, float width) {
		int w = getWidth();
		int h = getHeight();
		int cx = w / 2;
		int cy = h / 2;
		
		int dx = (int) (r * Math.sin(2. * Math.PI * fraction / of));
		int dy = -(int) (r * Math.cos(2. * Math.PI * fraction / of));

		g.setStroke(new BasicStroke(width));
		g.drawLine(cx, cy, cx + dx, cy + dy);
	}
	
	public static void main(String args[]) {
		JFrame frame = new JFrame("Uhr");
		final Uhr uhr = new Uhr();
		frame.setLayout(new BorderLayout());
		frame.getContentPane().add(uhr);
		frame.setSize(640, 480);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Thread updater = new Thread("Tick Thread") {
			@Override
			public void run() {
				while (true) {
					uhr.repaint();
					try {
						long now = System.currentTimeMillis();
						sleep(1000-(now % 1000));
					} catch (InterruptedException ex) {
						break;
					}
				}
			}
		};
		updater.start();
	}
}
