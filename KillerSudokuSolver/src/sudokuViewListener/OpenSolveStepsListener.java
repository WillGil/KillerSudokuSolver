package sudokuViewListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import sudokuModel.Cage;
import sudokuModel.Cell;
import sudokuModel.KillerSudokuGrid;
import sudokuModel.Pair;
import sudokuModel.ReadCages;
import sudokuModel.Solver;
import sudokuView.GridGUI;
import sudokuView.MainFrame;

public class OpenSolveStepsListener implements ActionListener {

	private GridGUI grid;
	private Solver solver;
	private MainFrame frame;

	public OpenSolveStepsListener(GridGUI gridGUI, Solver solver, MainFrame mainFrame) {
		this.grid = gridGUI;
		this.solver = solver;
		this.frame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		KillerSudokuGrid gridSudoku = null;
		// Choose file
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");

		fileChooser.setFileFilter(filter);

		int returnVal = fileChooser.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			Cage.resetCages();

			Pair<Set<Cage>, Cell[][]> gridSpec = ReadCages.readCellsIn(file);

			
			if(gridSpec == null){
				int confirmButton = JOptionPane.OK_CANCEL_OPTION;
				int confirmResult = JOptionPane.showConfirmDialog(frame, "Invalid Puzzle", "Error",
						confirmButton, JOptionPane.ERROR_MESSAGE);
				Cage.numberOfCages= grid.getGrid().getCages().size();
				return;
			}
			
			for(int row=1;row<=9;row++){
				for (int col=1;col<=9;col++){
					System.out.println(gridSpec.getSecond()[row-1][col-1]+ " "+gridSpec.getSecond()[row-1][col-1].getPossibleValues());
				}
			}
			
			
			
			gridSudoku = new KillerSudokuGrid(gridSpec.getFirst());
			gridSudoku.setSudokuCells(gridSpec.getSecond());
			
		
			grid.changeGridDisplaying(gridSudoku);
			solver.changeGridForSolving(gridSudoku);

			if (!frame.getControlPnl().getDetailedHelp().isSelected())
				grid.setBlankGrid();

			frame.getControlPnl().getSolutionArea().setText("");
			frame.getControlPnl().getCellsSolved().setText(String.valueOf(grid.getGrid().numberOfsolvedCells()));
			frame.getControlPnl().getCandidatesRemainingInGame()
					.setText(String.valueOf(grid.getGrid().candidatesRemaining()));
		}

	}

}
