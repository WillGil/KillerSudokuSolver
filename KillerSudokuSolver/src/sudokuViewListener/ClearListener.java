package sudokuViewListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import sudokuModel.Cage;
import sudokuModel.KillerSudokuGrid;
import sudokuModel.ReadCages;
import sudokuModel.Solver;
import sudokuView.GridGUI;
import sudokuView.MainFrame;

public class ClearListener implements ActionListener {

	private GridGUI grid;
	private MainFrame frame;
	private Solver solver;

	public ClearListener(GridGUI grid, MainFrame frame, Solver solver) {
		this.grid = grid;
		this.frame = frame;
		this.solver = solver;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		int dialogButton = JOptionPane.YES_NO_OPTION;
		int dialogResult = JOptionPane.showConfirmDialog(frame, "Would you like the clear the sudoku?", "Are you sure?",
				dialogButton, JOptionPane.WARNING_MESSAGE);

		if (dialogResult == JOptionPane.YES_OPTION) {
			if (!grid.getGrid().isGridDefault()) {
				if (!(ReadCages.getFilePath() == null)) {
					Cage.resetCages();
					File fileUsed = new File(ReadCages.getFilePath());
					Set<Cage> cages = ReadCages.readCagesFromFile(fileUsed);
					KillerSudokuGrid newGrid = new KillerSudokuGrid(cages);
					grid.changeGridDisplaying(newGrid);
					solver.changeGridForSolving(newGrid);
					frame.getControlPnl().getSolutionArea().setText("");
					frame.getControlPnl().getCellsSolved().setText(String.valueOf(grid.getGrid().numberOfsolvedCells()));
					frame.getControlPnl().getCandidatesRemainingInGame().setText(String.valueOf(grid.getGrid().candidatesRemaining()));
				} else {
					Cage.resetCages();
					Set<Cage> cages = ReadCages.readCagesFromString("Example1.txt");
					KillerSudokuGrid newGrid = new KillerSudokuGrid(cages);
					grid.changeGridDisplaying(newGrid);
					solver.changeGridForSolving(newGrid);
					frame.getControlPnl().getSolutionArea().setText("");
					frame.getControlPnl().getCellsSolved().setText(String.valueOf(grid.getGrid().numberOfsolvedCells()));
					frame.getControlPnl().getCandidatesRemainingInGame().setText(String.valueOf(grid.getGrid().candidatesRemaining()));
				}

				if (!frame.getControlPnl().getDetailedHelp().isSelected())
					grid.setBlankGrid();
			} else {
				int confirmButton = JOptionPane.OK_CANCEL_OPTION;
				int confirmResult = JOptionPane.showConfirmDialog(frame, "Sudoku is already clear.", "Error",
						confirmButton, JOptionPane.ERROR_MESSAGE);
				//JOptionPane.showMessageDialog(frame, "Nothing to clear!");
			}
		}
	}
}
