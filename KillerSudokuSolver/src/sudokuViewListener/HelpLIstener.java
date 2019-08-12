package sudokuViewListener;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class HelpLIstener implements ActionListener {

	private static int SIZE_FRAME_WIDTH = 775;
	private static int SIZE_FRAME_HEIGHT = 600;

	private static Font fontTitle = new Font("TimesRoman", Font.BOLD, 20);
	private static Font fontSubtitle = new Font("TimesRoman", Font.PLAIN, 18);
	private static Font fontText =new Font("TimesRoman", Font.PLAIN, 16);

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JFrame frame = new JFrame("Help");
		JPanel panel = new JPanel();
		JTextArea area = new JTextArea();
		area.setEditable(false);
		
		FileReader reader = null;
		try {
			reader = new FileReader("HelpPage.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			area.read(reader, "HelpPage.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //Object of JTextArea

		
		panel.add(area);
		frame.setContentPane(panel);
		frame.pack();
		frame.setMinimumSize(new Dimension(SIZE_FRAME_WIDTH, SIZE_FRAME_HEIGHT));
		frame.setVisible(true);

	}

}
