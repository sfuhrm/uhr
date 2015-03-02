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
import javax.swing.SwingUtilities;

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
		
		drawClock(g2d, r, cx, cy, sec, min, hour);
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
		g2d.setStroke(new BasicStroke(1f));
		for (int i=0; i < 60; i++) {
			drawCircleLine(g2d, r, cx, cy, i, 60, 1f-0.05f, 1f);
		}		
		
		// hour ticks
		g2d.setColor(Color.DARK_GRAY);
		g2d.setStroke(new BasicStroke(4f));
		for (int i=0; i < 12; i++) {
			drawCircleLine(g2d, r, cx, cy, i, 12, 1f-0.2f, 1f);
		}
		
		// seconds
		g2d.setColor(new Color(0f, 0f , 1f, 0.6f));
		g2d.setStroke(new BasicStroke(2.1f));
		drawCircleLine(g2d, r, cx, cy, seconds, 60, 0., 1f);
		// minutes
		g2d.setColor(new Color(0f, 0f , 0f, 0.9f));
		g2d.setStroke(new BasicStroke(4));
		drawCircleLine(g2d, r, cx, cy, minutes, 60, 0., 0.9);
		// hours
		g2d.setColor(new Color(1f, 0f , 0f, 0.9f));
		g2d.setStroke(new BasicStroke(4));
		drawCircleLine(g2d, r, cx, cy, hours + minutes  / 60., 12, 0., 0.8);
	}
	
	/**
	 * Draws a line with a given angle in the circle.
	 * @param g graphics context to draw on.
	 * @param r the total circle radius.
	 * @param cx the circle center x coordinate in the JPanel component.
	 * @param cy the circle center y coordinate in the JPanel component.
	 * @param fraction fraction of <code>of</code>. Could be 5 for 5 hours of 12.
	 * @param of total amount of units (for example 12 for 12 hours)
	 * @param start the start position of the line in the given angle. Must be between 0 and 1. Can be 0.0 if it is in the center or 1.0 if it is on the radius.
	 * @param end the end position of the line in the given angle. Must be between 0 and 1. Can be 0.0 if it is in the center or 1.0 if it is on the radius.
	 */
	private static void drawCircleLine(Graphics2D g, double r, int cx, int cy, double fraction, double of, double start, double end) {
		double dx =   (r * Math.sin(2. * Math.PI * fraction / of));
		double dy = - (r * Math.cos(2. * Math.PI * fraction / of));

		int sx = (int)(cx + start * dx);
		int sy = (int)(cy + start * dy);
		int ex = (int)(cx + end * dx);
		int ey = (int)(cy + end * dy);
		
		g.drawLine(sx, sy, ex, ey);
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
					SwingUtilities.invokeLater(uhr::repaint);
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
