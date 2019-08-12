package sudokuViewListener;

import java.awt.event.ActionEvent;

/**
 * #TODO: ADD FILE CHECKING TO MAKE SURE IT'S VALID
 */
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import sudokuModel.Cage;
import sudokuModel.KillerSudokuGrid;
import sudokuModel.ReadCages;
import sudokuModel.Solver;
import sudokuView.GridGUI;
import sudokuView.MainFrame;

public class OpenFileListener implements ActionListener {

	private GridGUI grid;
	private Solver solver;
	private MainFrame frame;

	public OpenFileListener(GridGUI grid, Solver solver, MainFrame frame) {
		this.grid = grid;
		this.solver = solver;
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		KillerSudokuGrid gridSudoku = null;
		// Choose file
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
	
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");

		fileChooser.setFileFilter(filter);

		int returnVal = fileChooser.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			Cage.resetCages();
			Set<Cage> cages = ReadCages.readCagesFromFile(file);
			
			if(cages == null){
				int confirmButton = JOptionPane.OK_CANCEL_OPTION;
				int confirmResult = JOptionPane.showConfirmDialog(frame, "Invalid Puzzle", "Error",
						confirmButton, JOptionPane.ERROR_MESSAGE);
				Cage.numberOfCages= grid.getGrid().getCages().size();
				return;
			}
			gridSudoku = new KillerSudokuGrid(cages);
			
			grid.changeGridDisplaying(gridSudoku);
			solver.changeGridForSolving(gridSudoku);

			if (!frame.getControlPnl().getDetailedHelp().isSelected())
				grid.setBlankGrid();
			
			frame.getControlPnl().getSolutionArea().setText("");
			frame.getControlPnl().getCellsSolved().setText(String.valueOf(grid.getGrid().numberOfsolvedCells()));
			frame.getControlPnl().getCandidatesRemainingInGame().setText(String.valueOf(grid.getGrid().candidatesRemaining()));
		}

	}


}
