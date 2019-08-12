package sudokuViewListener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import sudokuModel.Cell;
import sudokuView.CellGUI;
import sudokuView.GridGUI;
import sudokuView.InteractionPanel;
import sudokuView.MainFrame;

/**
 * A class that handles the clicking of the detailed help button.
 * 
 * @author William Gilgunn
 * @version 2.0
 *
 */
public class DetailedHelpListener implements ItemListener {

	private GridGUI grid;
	private MainFrame frame;

	public DetailedHelpListener(GridGUI grid, MainFrame frame) {
		this.grid = grid;
		this.frame = frame;

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		InteractionPanel controlPnl = frame.getControlPnl();
		int state = e.getStateChange();
		if (state == ItemEvent.SELECTED) { // Display cells
		
			grid.removeAll();
			grid.setupGridGUI();
			
			
			controlPnl.getEasyHelp().setEnabled(true);
			controlPnl.getMediumHelp().setEnabled(true);
			controlPnl.getExperiencedHelp().setEnabled(true);
			controlPnl.getEasyHelp().setSelected(true);
			controlPnl.getSolutionArea().setEnabled(true);

		} else {
			if (state == ItemEvent.DESELECTED) { // Dont display cells.
				for (CellGUI cGui : grid.getListCellGUI()) {
					Cell currentCell = cGui.getSudokuCell();
					if (!currentCell.isCommitted() && !(currentCell.getPossibleValues().size() == 1)) {
						cGui.cleanUp();
					}
				}
				
				grid.refresh();
				controlPnl.getSkillLevel().clearSelection();
				controlPnl.getEasyHelp().setEnabled(false);
				controlPnl.getMediumHelp().setEnabled(false);
				controlPnl.getExperiencedHelp().setEnabled(false);
				controlPnl.getSolutionArea().setEnabled(false);
				controlPnl.getSolutionArea().setText("");
			}

		}

	}

}
