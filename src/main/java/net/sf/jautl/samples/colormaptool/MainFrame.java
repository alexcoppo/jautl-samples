/*
    Copyright (c) 2000-2012 Alessandro Coppo
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:
    1. Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
    2. Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
    3. The name of the author may not be used to endorse or promote products
       derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
    IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
    OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
    IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package net.sf.jautl.samples.colormaptool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Locale;
import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.sf.jautl.graphics.colormaps.IColorMapRGBAF;
import net.sf.jautl.graphics.colormaps.greys.GreyContinuos;
import net.sf.jautl.graphics.colormaps.greys.GreyContinuosTagged;
import net.sf.jautl.graphics.colormaps.greys.GreySteps;
import net.sf.jautl.graphics.colormaps.greys.GreyStepsTagged;
import net.sf.jautl.graphics.colormaps.terrain.DEMPoster;
import net.sf.jautl.graphics.colormaps.terrain.DEMPrint;
import net.sf.jautl.graphics.colormaps.terrain.DEMScreen;
import net.sf.jautl.graphics.colormaps.terrain.Landserf;
import net.sf.jautl.graphics.colormaps.terrain.MOLA;
import net.sf.jautl.graphics.colormaps.terrain.MicroDEM;
import net.sf.jautl.graphics.colormaps.terrain.ThreeDEM;
import net.sf.jautl.graphics.colormaps.terrain.Wikipedia1;
import net.sf.jautl.graphics.colormaps.terrain.Wikipedia2;
import net.sf.jautl.graphics.colormaps.terrain.Wikipedia3;
import net.sf.jautl.graphics.colormaps.warmbody.WarmBody;
import net.sf.jautl.graphics.colormaps.warmbody.WarmBodyEx;
import net.sf.jautl.graphics.colors.ColorConverter;
import net.sf.jautl.graphics.colors.ColorRGBAF;
import net.sf.jautl.swing.DoubleBufferfedCanvas;
import net.sf.jautl.swing.WindowCenterer;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	private JComboBox cboColorModel;
	private JSlider sldPosition;
	private JLabel lblPosition;
	private JLabel lblValueX;
	private JLabel lblValueY;
	private JLabel lblValueZ;
	private JLabel lblValueA;
	private DoubleBufferfedCanvas pnlGraphX;
	private DoubleBufferfedCanvas pnlGraphY;
	private DoubleBufferfedCanvas pnlGraphZ;
	private DoubleBufferfedCanvas pnlGraphA;
	private JList lstColorMaps;

	private ColorRGBAF currentColorRGBA = new ColorRGBAF();
	private IColorMapRGBAF currentCMAP;
	private DoubleBufferfedCanvas pnlColorMapDisplay;

	private ColorConverter cc = new ColorConverter();

	public MainFrame() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("ColorMap Tool");
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mnFileExit = new JMenuItem("Exit");
		mnFileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		mnFileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.this.dispose();
			}
		});
		mnFile.add(mnFileExit);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel pnlQQQ = new JPanel();
		pnlQQQ.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(pnlQQQ, BorderLayout.CENTER);
		GridBagLayout gbl_pnlQQQ = new GridBagLayout();
		gbl_pnlQQQ.columnWidths = new int[]{150, 500, 75, 0};
		gbl_pnlQQQ.rowHeights = new int[]{35, 100, 25, 100, 100, 100, 100, 0};
		gbl_pnlQQQ.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_pnlQQQ.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		pnlQQQ.setLayout(gbl_pnlQQQ);
		
		lstColorMaps = new JList();
		lstColorMaps.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		lstColorMaps.setModel(new AbstractListModel() {
			String[] values = new String[] {
				"Grey-Continuos", "Grey-Continuos-1.8", "Grey-Continuos-2.2",
				"Grey-Continuos-Tagged",
				"Grey-Steps-16", "Grey-Steps-64",
				"Grey-Steps-Tagged-16", "Grey-Steps-Tagged-64",
				
				"DEMPoster", "DEMPrint", "DEMScreen",
				"Landserf", "MicroDEM", "MOLA", "3DEM",
				"Wikipedia-1", "Wikipedia-2", "Wikipedia-3",
				
				"WarmBody", "WarmBodyEx"
			};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		lstColorMaps.setSelectedIndex(0);
		lstColorMaps.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				onColorMapChanged((String)lstColorMaps.getSelectedValue());
			}
		});
		GridBagConstraints gbc_lstColorMaps = new GridBagConstraints();
		gbc_lstColorMaps.fill = GridBagConstraints.BOTH;
		gbc_lstColorMaps.insets = new Insets(0, 0, 0, 5);
		gbc_lstColorMaps.gridheight = 7;
		gbc_lstColorMaps.gridx = 0;
		gbc_lstColorMaps.gridy = 0;
		pnlQQQ.add(lstColorMaps, gbc_lstColorMaps);
		
		sldPosition = new JSlider();
		sldPosition.setMaximum(1000);
		sldPosition.setValue(500);
		sldPosition.setMinorTickSpacing(10);
		sldPosition.setPaintTicks(true);
		sldPosition.setMajorTickSpacing(100);
		sldPosition.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				onSliderMove(sldPosition.getValue());
			}
		});
		GridBagConstraints gbc_sldPosition = new GridBagConstraints();
		gbc_sldPosition.anchor = GridBagConstraints.SOUTH;
		gbc_sldPosition.fill = GridBagConstraints.HORIZONTAL;
		gbc_sldPosition.insets = new Insets(0, 0, 5, 5);
		gbc_sldPosition.gridx = 1;
		gbc_sldPosition.gridy = 0;
		pnlQQQ.add(sldPosition, gbc_sldPosition);
		
		lblPosition = new JLabel("0.000");
		lblPosition.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblPosition.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblPosition = new GridBagConstraints();
		gbc_lblPosition.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblPosition.insets = new Insets(0, 0, 5, 0);
		gbc_lblPosition.gridx = 2;
		gbc_lblPosition.gridy = 0;
		pnlQQQ.add(lblPosition, gbc_lblPosition);
		
		pnlColorMapDisplay = new DoubleBufferfedCanvas();
		GridBagConstraints gbc_pnlColorMapDisplay = new GridBagConstraints();
		gbc_pnlColorMapDisplay.fill = GridBagConstraints.BOTH;
		gbc_pnlColorMapDisplay.insets = new Insets(0, 0, 5, 5);
		gbc_pnlColorMapDisplay.gridx = 1;
		gbc_pnlColorMapDisplay.gridy = 1;
		pnlQQQ.add(pnlColorMapDisplay, gbc_pnlColorMapDisplay);
		
		cboColorModel = new JComboBox();
		cboColorModel.setModel(new DefaultComboBoxModel(new String[] {"RGBA", "HSVA", "HLSA", "YPbPrA"}));
		cboColorModel.setSelectedIndex(0);
		cboColorModel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onSliderMove(sldPosition.getValue());
				plotGraphs();
			}
		});
		GridBagConstraints gbc_cboColorModel = new GridBagConstraints();
		gbc_cboColorModel.insets = new Insets(0, 0, 5, 5);
		gbc_cboColorModel.gridx = 1;
		gbc_cboColorModel.gridy = 2;
		pnlQQQ.add(cboColorModel, gbc_cboColorModel);
		
		pnlGraphX = new DoubleBufferfedCanvas();
		GridBagConstraints gbc_pnlGraphX = new GridBagConstraints();
		gbc_pnlGraphX.fill = GridBagConstraints.BOTH;
		gbc_pnlGraphX.insets = new Insets(0, 0, 5, 5);
		gbc_pnlGraphX.gridx = 1;
		gbc_pnlGraphX.gridy = 3;
		pnlQQQ.add(pnlGraphX, gbc_pnlGraphX);
		
		lblValueX = new JLabel("0.000");
		lblValueX.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblValueX.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblValueX = new GridBagConstraints();
		gbc_lblValueX.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblValueX.insets = new Insets(0, 0, 5, 0);
		gbc_lblValueX.gridx = 2;
		gbc_lblValueX.gridy = 3;
		pnlQQQ.add(lblValueX, gbc_lblValueX);
		
		pnlGraphY = new DoubleBufferfedCanvas();
		GridBagConstraints gbc_pnlGraphY = new GridBagConstraints();
		gbc_pnlGraphY.fill = GridBagConstraints.BOTH;
		gbc_pnlGraphY.insets = new Insets(0, 0, 5, 5);
		gbc_pnlGraphY.gridx = 1;
		gbc_pnlGraphY.gridy = 4;
		pnlQQQ.add(pnlGraphY, gbc_pnlGraphY);
		
		lblValueY = new JLabel("0.000");
		lblValueY.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblValueY.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblValueY = new GridBagConstraints();
		gbc_lblValueY.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblValueY.insets = new Insets(0, 0, 5, 0);
		gbc_lblValueY.gridx = 2;
		gbc_lblValueY.gridy = 4;
		pnlQQQ.add(lblValueY, gbc_lblValueY);
		
		pnlGraphZ = new DoubleBufferfedCanvas();
		GridBagConstraints gbc_pnlGraphZ = new GridBagConstraints();
		gbc_pnlGraphZ.fill = GridBagConstraints.BOTH;
		gbc_pnlGraphZ.insets = new Insets(0, 0, 5, 5);
		gbc_pnlGraphZ.gridx = 1;
		gbc_pnlGraphZ.gridy = 5;
		pnlQQQ.add(pnlGraphZ, gbc_pnlGraphZ);
		
		lblValueZ = new JLabel("0.000");
		lblValueZ.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblValueZ.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblValueZ = new GridBagConstraints();
		gbc_lblValueZ.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblValueZ.insets = new Insets(0, 0, 5, 0);
		gbc_lblValueZ.gridx = 2;
		gbc_lblValueZ.gridy = 5;
		pnlQQQ.add(lblValueZ, gbc_lblValueZ);
		
		pnlGraphA = new DoubleBufferfedCanvas();
		GridBagConstraints gbc_pnlGraphA = new GridBagConstraints();
		gbc_pnlGraphA.fill = GridBagConstraints.BOTH;
		gbc_pnlGraphA.insets = new Insets(0, 0, 0, 5);
		gbc_pnlGraphA.gridx = 1;
		gbc_pnlGraphA.gridy = 6;
		pnlQQQ.add(pnlGraphA, gbc_pnlGraphA);
		
		lblValueA = new JLabel("0.000");
		lblValueA.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblValueA.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblValueA = new GridBagConstraints();
		gbc_lblValueA.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblValueA.gridx = 2;
		gbc_lblValueA.gridy = 6;
		pnlQQQ.add(lblValueA, gbc_lblValueA);
		
		pack();

		WindowCenterer.center(this);
		
		lstColorMaps.setSelectedIndex(0);
	}
	
	private void onSliderMove(int newPosition) {
		double pos = newPosition / (double)sldPosition.getMaximum();
		lblPosition.setText(String.format(Locale.US, "%.3f", pos));
		
		currentCMAP.lookup(pos, currentColorRGBA);
		cc.set(currentColorRGBA);
		String colorModel = (String)cboColorModel.getSelectedItem();
		
		lblValueX.setText(String.format(Locale.US, "%.3f", cc.getX(colorModel)));
		lblValueY.setText(String.format(Locale.US, "%.3f", cc.getY(colorModel)));
		lblValueZ.setText(String.format(Locale.US, "%.3f", cc.getZ(colorModel)));
		lblValueA.setText(String.format(Locale.US, "%.3f", cc.getA(colorModel)));
	}
	
	private void onColorMapChanged(String cmapName) {
		if (cmapName.equals("Grey-Continuos")) {
			currentCMAP = new GreyContinuos(1.0);
		} else if (cmapName.equals("Grey-Continuos-1.8")) {
			currentCMAP = new GreyContinuos(1.8);
		} else if (cmapName.equals("Grey-Continuos-2.2")) {
			currentCMAP = new GreyContinuos(2.2);
		} if (cmapName.equals("Grey-Continuos-Tagged")) {
			currentCMAP = new GreyContinuosTagged(2.2, .05);
		} if (cmapName.equals("Grey-Steps-16")) {
			currentCMAP = new GreySteps(2.2, 16);
		} if (cmapName.equals("Grey-Steps-64")) {
			currentCMAP = new GreySteps(2.2, 64);
		} if (cmapName.equals("Grey-Steps-Tagged-16")) {
			currentCMAP = new GreyStepsTagged(2.2, 16);
		} if (cmapName.equals("Grey-Steps-Tagged-64")) {
			currentCMAP = new GreyStepsTagged(2.2, 64);

		} if (cmapName.equals("DEMPoster")) {
			currentCMAP = new DEMPoster(1.0);
		} if (cmapName.equals("DEMPrint")) {
			currentCMAP = new DEMPrint(1.0);
		} if (cmapName.equals("DEMScreen")) {
			currentCMAP = new DEMScreen(1.0);
		} if (cmapName.equals("Landserf")) {
			currentCMAP = new Landserf(1.0);
		} if (cmapName.equals("MicroDEM")) {
			currentCMAP = new MicroDEM(1.0);
		} if (cmapName.equals("MOLA")) {
			currentCMAP = new MOLA(1.0);
		} if (cmapName.equals("3DEM")) {
			currentCMAP = new ThreeDEM(1.0);
		} if (cmapName.equals("Wikipedia-1")) {
			currentCMAP = new Wikipedia1(1.0);
		} if (cmapName.equals("Wikipedia-2")) {
			currentCMAP = new Wikipedia2(1.0);
		} if (cmapName.equals("Wikipedia-3")) {
			currentCMAP = new Wikipedia3(1.0);
			
			
		} if (cmapName.equals("WarmBody")) {
			currentCMAP = new WarmBody(1.0);
		} if (cmapName.equals("WarmBodyEx")) {
			currentCMAP = new WarmBodyEx(1.0);
		}
		
		plotColorMap();
		plotGraphs();

		onSliderMove(sldPosition.getValue());
	}
	
	private void plotColorMap() {
		ColorRGBAF rgba = new ColorRGBAF();
		
		BufferedImage bi = pnlColorMapDisplay.getDrawingSurface();
		Graphics g = bi.createGraphics();
		
		for (int x = 0; x < pnlColorMapDisplay.getWidth(); x++) {
			double pos = x / (double)(pnlColorMapDisplay.getWidth() - 1);
			currentCMAP.lookup(pos, rgba);
			g.setColor(rgba.toAWTColor());
			g.drawLine(x, 0, x, pnlColorMapDisplay.getHeight());
		}
		
		pnlColorMapDisplay.repaint();
	}
	
	private void plotGraphs() {
		ColorRGBAF rgba = new ColorRGBAF();
		
		Graphics2D gX = pnlGraphX.getDrawingSurface().createGraphics();
		Graphics2D gY = pnlGraphY.getDrawingSurface().createGraphics();
		Graphics2D gZ = pnlGraphZ.getDrawingSurface().createGraphics();
		Graphics2D gA = pnlGraphA.getDrawingSurface().createGraphics();

		int ctlHeight = pnlGraphX.getHeight();
		int ctlWidth = pnlGraphX.getWidth();
		gX.translate(0, ctlHeight - 1); gX.scale(1, -1);
		gY.translate(0, ctlHeight - 1); gY.scale(1, -1);
		gZ.translate(0, ctlHeight - 1); gZ.scale(1, -1);
		gA.translate(0, ctlHeight - 1); gA.scale(1, -1);
		
		drawGrid(gX, ctlWidth, ctlHeight);
		drawGrid(gY, ctlWidth, ctlHeight);
		drawGrid(gZ, ctlWidth, ctlHeight);
		drawGrid(gA, ctlWidth, ctlHeight);
		
		gX.setColor(Color.RED);
		gY.setColor(Color.RED);
		gZ.setColor(Color.RED);
		gA.setColor(Color.RED);

		String colorModel = (String)cboColorModel.getSelectedItem();
		int y;

		for (int x = 0; x < pnlGraphX.getWidth(); x++) {
			double pos = x / (double)(pnlGraphX.getWidth() - 1);
			currentCMAP.lookup(pos, rgba);
			cc.set(rgba);
			
			y = (int)(cc.get(0, colorModel) * (ctlHeight - 2)); gX.drawLine(x, y, x, y + 1);
			y = (int)(cc.get(1, colorModel) * (ctlHeight - 2)); gY.drawLine(x, y, x, y + 1);
			y = (int)(cc.get(2, colorModel) * (ctlHeight - 2)); gZ.drawLine(x, y, x, y + 1);
			y = (int)(cc.get(3, colorModel) * (ctlHeight - 2)); gA.drawLine(x, y, x, y + 1);
		}

		pnlGraphX.repaint();
		pnlGraphY.repaint();
		pnlGraphZ.repaint();
		pnlGraphA.repaint();
	}
	
	private void drawGrid(Graphics2D g, int width, int height) {
		g.setColor(Color.WHITE);

		g.fillRect(0, 0, width, height);

		g.setColor(Color.BLACK);
		
		for (int iy = 0; iy <= 5; iy++) {
			int y = (int)((height - 1) / 5.0 * iy);
			g.drawLine(0, y, width, y);
		}
		for (int ix = 0; ix <= 5; ix++) {
			int x = (int)((width - 1) / 5.0 * ix);
			g.drawLine(x, 0, x, height);
		}
	}
}
