package sudokuView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import sudokuModel.KillerSudokuGrid;


/**
 * The Main Frame of the GUI that will hold all the other elements of the GUI
 * within it.
 * 
 * @author William Gilgunn
 * @version 2.0
 */

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JMenuBar menuBar;
	private JMenuItem openSudoku,openPartiallySolvedSudoku, helpPage, saveSudokuSolved, saveSudokuSteps;
	private JMenu fileMenu, helpMenu;
	private GridGUI gridGUI;
	private InteractionPanel controlPnl;
	private JPanel contentsPanel;
	private final static int FRAME_SIZE = 1100;
	private KillerSudokuGrid grid;

	public MainFrame(KillerSudokuGrid grid) {
		super("Killer Sudoku Solver");

		this.grid = grid;

		contentsPanel = new JPanel(new BorderLayout());

		menuBar = new JMenuBar();

		fileMenu = new JMenu("File");
		helpMenu = new JMenu("Help");

		openSudoku = new JMenuItem("Open New Board");
		openPartiallySolvedSudoku = new JMenuItem("Open Existing Board");
		helpPage = new JMenuItem("Instructions");
		saveSudokuSolved = new JMenuItem("Save Solved Cells");
		saveSudokuSteps = new JMenuItem("Save Current Board");

		fileMenu.add(openSudoku);
		fileMenu.add(openPartiallySolvedSudoku);
		fileMenu.add(saveSudokuSolved);
		fileMenu.add(saveSudokuSteps);
		helpMenu.add(helpPage);

		menuBar.add(fileMenu);
		menuBar.add(helpMenu);

		/* Add other components */
		controlPnl = new InteractionPanel();
		gridGUI = new GridGUI(grid);

		setJMenuBar(menuBar);
		setVisible(true);
		setSize(FRAME_SIZE + 450, FRAME_SIZE - 100);

		contentsPanel.setVisible(true);
		contentsPanel.add(controlPnl, BorderLayout.EAST);
		contentsPanel.add(gridGUI, BorderLayout.CENTER);

		setMinimumSize(new Dimension(FRAME_SIZE + 250, FRAME_SIZE - 50));

		this.setContentPane(contentsPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set colour
		getContentPane().setBackground(Color.DARK_GRAY);

	}

	/**
	 * Get JMenuItem to open a new sudoku game
	 * 
	 * @return the JMenuItem
	 */
	public JMenuItem getOpenSudoku() {
		return openSudoku;
	}

	/**
	 * The JMenuItem to display help information.
	 * 
	 * @return the JMenuItem to display the help page.
	 */
	public JMenuItem getHelpPage() {
		return helpPage;
	}

	/**
	 * The JMenu to open the file menu that allows the opening of a new sudoku
	 * 
	 * @return the jMenu to open a new file
	 */
	public JMenu getFileMenu() {
		return fileMenu;
	}

	/**
	 * The JMenu toopen the help menu that allows for a user to view help
	 * information.
	 * 
	 * @return the JMenu to open the help file
	 */
	public JMenu getHelpMenu() {
		return helpMenu;
	}

	/**
	 * The GUI for the grid
	 * 
	 * @return the GUI for the grid
	 */
	public GridGUI getGridGUI() {
		return gridGUI;
	}

	/**
	 * The Interaction panel that contains all the buttons
	 * 
	 * @return the interaction panel the user uses.
	 */
	public InteractionPanel getControlPnl() {
		return controlPnl;
	}


	public JMenuItem getOpenPartiallySolvedSudoku() {
		return openPartiallySolvedSudoku;
	}

	public JMenuItem getSaveSudokuSolved() {
		return saveSudokuSolved;
	}

	public JMenuItem getSaveSudokuSteps() {
		return saveSudokuSteps;
	}

	public JPanel getContentsPanel() {
		return contentsPanel;
	}

	public KillerSudokuGrid getGrid() {
		return grid;
	}

	public void refresh() {
		gridGUI.removeAll();
		gridGUI.setupGridGUI();
		this.setContentPane(contentsPanel);

		revalidate();
		repaint();

	}
}
