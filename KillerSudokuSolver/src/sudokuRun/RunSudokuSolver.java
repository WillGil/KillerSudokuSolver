package sudokuRun;

import java.util.Set;
import javax.swing.SwingUtilities;
import sudokuModel.Cage;
import sudokuModel.KillerSudokuGrid;
import sudokuModel.ReadCages;
import sudokuModel.Solver;
import sudokuView.MainFrame;
import sudokuViewListener.ClearListener;
import sudokuViewListener.DetailedHelpListener;
import sudokuViewListener.HelpLIstener;
import sudokuViewListener.OpenFileListener;
import sudokuViewListener.OpenSolveStepsListener;
import sudokuViewListener.SaveGridListener;
import sudokuViewListener.SaveStepsListener;
import sudokuViewListener.SolveListener;
import sudokuViewListener.SolveStepsListener;

/**
 * A class that runs the program so it can be used
 * 
 * @author William Gilgunn
 * @version 2.0
 */
public class RunSudokuSolver implements Runnable {

	@Override
	public void run() {
		Set<Cage> cages = ReadCages.readCagesFromString("Example1.txt");
		KillerSudokuGrid grid = new KillerSudokuGrid(cages);
		MainFrame mf = new MainFrame(grid);
		
		mf.getGridGUI().setBlankGrid();
		mf.getControlPnl().getSolutionArea().setEnabled(false);
		
		
		
		/*
		 * Setup action listeners
		 */

		Solver solver = new Solver(grid);
		mf.getControlPnl().getDetailedHelp().addItemListener(new DetailedHelpListener(mf.getGridGUI(), mf));
		mf.getControlPnl().getSolveSteps().addActionListener(new SolveStepsListener(mf.getGridGUI(), solver, mf));
		mf.getControlPnl().getSolveFully().addActionListener(new SolveListener(mf.getGridGUI(), solver, mf));
		mf.getOpenSudoku().addActionListener(new OpenFileListener(mf.getGridGUI(), solver, mf));
		mf.getOpenPartiallySolvedSudoku().addActionListener(new OpenSolveStepsListener(mf.getGridGUI(), solver, mf));
		mf.getSaveSudokuSolved().addActionListener(new SaveGridListener(mf, mf.getGridGUI()));
		mf.getSaveSudokuSteps().addActionListener(new SaveStepsListener(mf, mf.getGridGUI()));
		mf.getControlPnl().getClearSudoku().addActionListener(new ClearListener(mf.getGridGUI(), mf, solver));
		mf.getHelpPage().addActionListener(new HelpLIstener());
		
	}

	/**
	 * Main method to run the program
	 * 
	 * @param args
	 *            console default arguments
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new RunSudokuSolver());
	}

}
