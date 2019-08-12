package sudokuViewListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import sudokuModel.Logic;
import sudokuModel.Solver;
import sudokuView.GridGUI;
import sudokuView.MainFrame;

/**
 * 
 * 
 * #TODO: Are you sure
 * 
 * @author Willi
 *
 */
public class SolveListener implements ActionListener {

	private GridGUI grid;
	private Solver solver;
	private MainFrame frame;
	private int helpLevel;

	public SolveListener(GridGUI grid, Solver solver, MainFrame frame) {

		this.grid = grid;
		this.solver = solver;
		this.frame = frame;

		helpLevel = 0;

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		int dialogButton = JOptionPane.YES_NO_OPTION;
		int dialogResult = JOptionPane.showConfirmDialog(frame, "Would you like to solve the sudoku?", "Are you sure?",
				dialogButton, JOptionPane.WARNING_MESSAGE);

		if (dialogResult == JOptionPane.YES_OPTION) {

			boolean helpAvailable = frame.getControlPnl().getDetailedHelp().isSelected();
			boolean canRun = true;
			boolean fullySolved = grid.getGrid().isGridSolved();

			JTextArea solutionArea = frame.getControlPnl().getSolutionArea();

			if (!fullySolved) {
				if (helpAvailable) { // Find help level
					boolean easyHelp = (frame.getControlPnl().getEasyHelp().isSelected());
					boolean medHelp = (frame.getControlPnl().getMediumHelp().isSelected());
					boolean expHelp = (frame.getControlPnl().getExperiencedHelp().isSelected());

					if (easyHelp)
						helpLevel = 1;

					if (medHelp)
						helpLevel = 2;

					if (expHelp)
						helpLevel = 3;

					grid.removeAll();
					grid.setupGridGUI();
				}
			}

			int states = 0;
			long startTime = System.nanoTime();
			while (!grid.getGrid().isGridSolved() && canRun) {
				states++;
				if (solver.singleCagesPresent(grid.getGrid())) {
					Logic logic = solver.checkSingleCages(grid.getGrid());
					if (logic != null) {
						grid.removeAll();
						grid.setupGridGUI();
					}
				} else {
					Logic logic = solver.nakedSingles();
					if (logic != null) {
						grid.removeAll();
						grid.setupGridGUI();

					} else {

						logic = solver.ruleOfOne(grid.getGrid());
						if (logic != null) {
							grid.removeAll();
							grid.setupGridGUI();

						} else {
							logic = solver.hiddenSingles(grid.getGrid());
							if (logic != null) {
								grid.removeAll();
								grid.setupGridGUI();

							} else {
								logic = solver.nakedPairs(grid.getGrid());
								if (logic != null) {
									grid.removeAll();
									grid.setupGridGUI();

								} else {
									logic = solver.nakedTriples(grid.getGrid());
									if (logic != null) {
										grid.removeAll();
										grid.setupGridGUI();

									} else {
										logic = solver.hiddenPairs(grid.getGrid());
										if (logic != null) {
											grid.removeAll();
											grid.setupGridGUI();

										} else {
											logic = solver.hiddenTriples(grid.getGrid());
											if (logic != null) {
												grid.removeAll();
												grid.setupGridGUI();

											} else {
												// Quads
												logic = solver.nakedQuads(grid.getGrid());
												if (logic != null) {
													grid.removeAll();
													grid.setupGridGUI();

												} else {
													logic = solver.hiddenQuads(grid.getGrid());
													if (logic != null) {
														grid.removeAll();
														grid.setupGridGUI();

													} else {
														logic = solver.killerCageCombination(grid.getGrid());
														if (logic != null) {
															grid.removeAll();
															grid.setupGridGUI();

														} else {
															logic = solver.solveInniesAndOuties(grid.getGrid());
															if (logic != null) {
																grid.removeAll();
																grid.setupGridGUI();

															} else {
																logic = solver.pointingPairsAndTriples(grid.getGrid());
																if (logic != null) {
																	grid.removeAll();
																	grid.setupGridGUI();

																} else {
																	logic = solver.boxLineReduction(grid.getGrid());
																	if (logic != null) {
																		grid.removeAll();
																		grid.setupGridGUI();

																	} else {
																		logic = solver
																				.killerCageSumsHarder(grid.getGrid());
																		if (logic != null) {
																			grid.removeAll();
																			grid.setupGridGUI();

																		} else {
																			logic = solver.solveInniesAndOutiesTwoCells(
																					grid.getGrid());
																			if (logic != null) {
																				grid.removeAll();
																				grid.setupGridGUI();

																			} else {
																				canRun = false;
																				JOptionPane.showMessageDialog(frame,
																						"Killer Sudoku cannot be solved!");
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

			if (grid.getGrid().isGridSolved()) {
				long endTime = System.nanoTime();
				long timeElapsed = endTime - startTime;
				double elapsedTimeInSecond = (double) timeElapsed / 1_000_000_000;
				System.out.println(elapsedTimeInSecond);
				System.out.println(states);
				fullySolved = true;
				JOptionPane.showMessageDialog(frame, "Killer Sudoku has been solved!");
			}

		}

		frame.getControlPnl().getCellsSolved().setText(String.valueOf(grid.getGrid().numberOfsolvedCells()));
		frame.getControlPnl().getCandidatesRemainingInGame()
				.setText(String.valueOf(grid.getGrid().candidatesRemaining()));
	}

}
