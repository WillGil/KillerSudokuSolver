package sudokuViewListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import sudokuView.GridGUI;
import sudokuView.MainFrame;

public class SaveStepsListener implements ActionListener {

	private MainFrame frame;
	private GridGUI grid;

	public SaveStepsListener(MainFrame mainFrame, GridGUI gridGUI) {
		frame = mainFrame;
		grid = gridGUI;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		final JFileChooser fileChooser = new JFileChooser();

		FileFilter ft = new FileNameExtensionFilter("Text Files", "txt");
		fileChooser.addChoosableFileFilter(ft);

		int returnVal = fileChooser.showSaveDialog(frame);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();

			try (BufferedWriter fw = new BufferedWriter(new PrintWriter(file + ".txt"))) {
				String fileGrid = grid.getGrid().printFileGrid();
				String fileCells = grid.getGrid().printCellStates();
				fw.write(fileGrid);
				fw.write(System.getProperty("line.separator"));
				fw.write("----------");
				fw.write(System.getProperty("line.separator"));
				fw.write(fileCells);
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
