package sudokuViewListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import sudokuView.GridGUI;
import sudokuView.MainFrame;

public class SaveGridListener implements ActionListener {

	private MainFrame frame;
	private GridGUI gridGui;

	public SaveGridListener(MainFrame frame, GridGUI gui) {
		this.frame = frame;
		this.gridGui = gui;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		final JFileChooser fileChooser = new JFileChooser();

		FileFilter ft = new FileNameExtensionFilter("Text Files", "txt");
		fileChooser.addChoosableFileFilter(ft);

		int returnVal = fileChooser.showSaveDialog(frame);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();

			try (BufferedWriter fw = new BufferedWriter(new PrintWriter(fileChooser.getSelectedFile() + ".txt"))) {
			
				String grid = frame.getGridGUI().getGrid().printGrid();
				fw.write(grid);
			

				JOptionPane.showMessageDialog(frame, "File successfully saved as " + file.getName());
			} catch (IOException io) {
				io.printStackTrace();
			}
		}

	}

}
