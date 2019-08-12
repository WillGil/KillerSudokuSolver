package sudokuViewListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import sudokuModel.Location;
import sudokuModel.Logic;
import sudokuModel.Solver;
import sudokuView.CellGUI;
import sudokuView.GridGUI;
import sudokuView.MainFrame;

public class SolveStepsListener implements ActionListener {

	private GridGUI grid;
	private Solver solver;
	private MainFrame frame;
	private boolean fullySolved;
	private int helpLevel;

	/* Constructor */
	public SolveStepsListener(GridGUI grid, Solver solver, MainFrame frame) {
		this.grid = grid;
		this.solver = solver;
		this.frame = frame;

		fullySolved = false;
		helpLevel = 0;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JTextArea solutionArea = frame.getControlPnl().getSolutionArea();
		solutionArea.setText("");
		boolean helpAvailable, easyHelp, medHelp, expHelp, verification;
		helpAvailable = frame.getControlPnl().getDetailedHelp().isSelected();
		verification = false;
		fullySolved = (grid.getGrid().isGridSolved()) ? true : false;

		if (!fullySolved) {
			if (helpAvailable) { // Find help level
				easyHelp = (frame.getControlPnl().getEasyHelp().isSelected());
				medHelp = (frame.getControlPnl().getMediumHelp().isSelected());
				expHelp = (frame.getControlPnl().getExperiencedHelp().isSelected());

				if (easyHelp)
					helpLevel = 1;

				if (medHelp)
					helpLevel = 2;

				if (expHelp)
					helpLevel = 3;

			}
			grid.removeAll();
			grid.setupGridGUI();

		}

		if (!fullySolved) {
			if (solver.singleCagesPresent(grid.getGrid())) {
				Logic logic = solver.checkSingleCages(grid.getGrid());
				if (logic != null) {
					if (helpAvailable && appropriateSkillLevel(logic)) {
						String helpMsg = logic.getHelpText();
						solutionArea.setText("SINGLE CAGES:" + "\n");
						solutionArea.setText(solutionArea.getText() + helpMsg);
					}
					verification = true;
					grid.DisplaySingleCommittedCell(logic);
				}
			} else {
				Logic logic = solver.nakedSingles();
				if (logic != null) {
					if (helpAvailable && appropriateSkillLevel(logic)) {
						String helpMsg = logic.getHelpText();
						solutionArea.setText("NAKED SINGLES:" + "\n");
						solutionArea.setText(solutionArea.getText() + helpMsg);
					}
					verification = true;
					grid.DisplaySingleCommittedCell(logic);

				} else {

					logic = solver.ruleOfOne(grid.getGrid());
					if (logic != null) {
						if (helpAvailable && appropriateSkillLevel(logic)) {
							String helpMsg = logic.getHelpText();
							solutionArea.setText("RULE OF ONE:" + "\n");
							solutionArea.setText(solutionArea.getText() + helpMsg);
						}
						verification = true;
						grid.DisplayGridChanges(logic);
					} else {
						logic = solver.hiddenSingles(grid.getGrid());
						if (logic != null) {
							if (helpAvailable && appropriateSkillLevel(logic)) {
								String helpMsg = logic.getHelpText();
								solutionArea.setText("HIDDEN SINGLES" + "\n");
								solutionArea.setText(solutionArea.getText() + helpMsg);
							}
							verification = true;
							grid.DisplaySingleCommittedCell(logic);

						} else {
							logic = solver.nakedPairs(grid.getGrid());
							if (logic != null) {
								if (helpAvailable && appropriateSkillLevel(logic)) {
									String helpMsg = logic.getHelpText();
									solutionArea.setText("NAKED PAIRS" + "\n");
									solutionArea.setText(solutionArea.getText() + helpMsg);
								}
								verification = true;
								grid.DisplayGridChanges(logic);

							} else {
								logic = solver.nakedTriples(grid.getGrid());
								if (logic != null) {
									if (helpAvailable && appropriateSkillLevel(logic)) {
										String helpMsg = logic.getHelpText();
										solutionArea.setText("NAKED TRIPLES" + "\n");
										solutionArea.setText(solutionArea.getText() + helpMsg);
									}
									verification = true;
									grid.DisplayGridChanges(logic);

								} else {
									logic = solver.hiddenPairs(grid.getGrid());
									if (logic != null) {
										if (helpAvailable && appropriateSkillLevel(logic)) {
											String helpMsg = logic.getHelpText();
											solutionArea.setText("HIDDEN PAIRS" + "\n");
											solutionArea.setText(solutionArea.getText() + helpMsg);
										}
										verification = true;
										grid.DisplayGridChanges(logic);
									} else {
										logic = solver.hiddenTriples(grid.getGrid());
										if (logic != null) {
											if (helpAvailable && appropriateSkillLevel(logic)) {
												String helpMsg = logic.getHelpText();
												solutionArea.setText("HIDDEN TRIPLES" + "\n");
												solutionArea.setText(solutionArea.getText() + helpMsg);
											}
											verification = true;
											grid.DisplayGridChanges(logic);
										} else {
											// Quads
											logic = solver.nakedQuads(grid.getGrid());
											if (logic != null) {
												if (helpAvailable && appropriateSkillLevel(logic)) {
													String helpMsg = logic.getHelpText();
													solutionArea.setText("NAKED QUADS" + "\n");
													solutionArea.setText(solutionArea.getText() + helpMsg);
												}
												verification = true;
												grid.DisplayGridChanges(logic);
											} else {
												logic = solver.hiddenQuads(grid.getGrid());
												if (logic != null) {

													if (helpAvailable && appropriateSkillLevel(logic)) {
														String helpMsg = logic.getHelpText();
														solutionArea.setText("HIDDEN QUADS" + "\n");
														solutionArea.setText(solutionArea.getText() + helpMsg);
													}
													verification = true;
													grid.DisplayGridChanges(logic);
												} else {
													logic = solver.killerCageCombination(grid.getGrid());
													if (logic != null) {
														if (helpAvailable && appropriateSkillLevel(logic)) {
															String helpMsg = logic.getHelpText();
															solutionArea.setText("CAGE COMBINATIONS (EASY)" + "\n");
															solutionArea.setText(solutionArea.getText() + helpMsg);
														}
														verification = true;
														grid.DisplayGridChanges(logic);

													} else {
														logic = solver.solveInniesAndOuties(grid.getGrid());
														if (logic != null) {
															if (helpAvailable && appropriateSkillLevel(logic)) {
																String helpMsg = logic.getHelpText();
																solutionArea.setText("INNIES/OUTIES" + "\n");
																solutionArea.setText(solutionArea.getText() + helpMsg);
															}
															verification = true;

															grid.DisplaySingleCommittedCell(logic);

														} else {
															logic = solver.pointingPairsAndTriples(grid.getGrid());
															if (logic != null) {
																if (helpAvailable && appropriateSkillLevel(logic)) {
																	String helpMsg = logic.getHelpText();
																	solutionArea.setText("POITING PAIRS" + "\n");
																	solutionArea
																			.setText(solutionArea.getText() + helpMsg);
																}
																verification = true;
																grid.DisplayGridChanges(logic);

															} else {
																logic = solver.boxLineReduction(grid.getGrid());
																if (logic != null) {
																	if (helpAvailable && appropriateSkillLevel(logic)) {
																		String helpMsg = logic.getHelpText();
																		solutionArea
																				.setText("BOX LINE REDUCTION" + "\n");
																		solutionArea.setText(
																				solutionArea.getText() + helpMsg);
																	}
																	verification = true;
																	grid.DisplayGridChanges(logic);

																} else {
																	logic = solver.killerCageSumsHarder(grid.getGrid());
																	if (logic != null) {
																		if (helpAvailable
																				&& appropriateSkillLevel(logic)) {
																			String helpMsg = logic.getHelpText();
																			solutionArea.setText(
																					"KILLER CAGES (HARD)" + "\n");
																			solutionArea.setText(
																					solutionArea.getText() + helpMsg);
																		}
																		verification = true;
																		grid.DisplayGridChanges(logic);

																	} else {
																		logic = solver.solveInniesAndOutiesTwoCells(
																				grid.getGrid());
																		if (logic != null) {
																			if (helpAvailable
																					&& appropriateSkillLevel(logic)) {
																				String helpMsg = logic.getHelpText();
																				solutionArea.setText(
																						"INNIES AND OUTIES (2 CELLS)"
																								+ "\n");
																				solutionArea
																						.setText(solutionArea.getText()
																								+ helpMsg);
																			}
																			verification = true;
																			grid.DisplayGridChanges(logic);
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}

							}
						}
					}
				}
			}
		}
		frame.getControlPnl().getCellsSolved().setText(String.valueOf(grid.getGrid().numberOfsolvedCells()));
		frame.getControlPnl().getCandidatesRemainingInGame()
				.setText(String.valueOf(grid.getGrid().candidatesRemaining()));

		if (!helpAvailable)

		{// Update numbers on grid
			Set<CellGUI> cellsSolved = new HashSet<CellGUI>();
			for (CellGUI cell : grid.getListCellGUI()) {
				if (cell.getSudokuCell().isCommitted())
					cellsSolved.add(cell);
			}
			for (CellGUI cell : grid.getListCellGUI()) {
				if (!cellsSolved.contains(cell))
					cell.cleanUp();
			}
		}
		if (grid.getGrid().isGridSolved()) {
			JOptionPane.showMessageDialog(frame, "Killer Sudoku has been solved!");
			grid.removeAll();
			grid.setupGridGUI();
			fullySolved = true;

		} else {
			if (verification == false) {
				JOptionPane.showMessageDialog(frame, "Killer Sudoku cannot be solved!");
			}
		}
	}

	private boolean appropriateSkillLevel(Logic logic) {
		if (helpLevel <= logic.getHelpLevel())
			return true;

		return false;
	}

}
