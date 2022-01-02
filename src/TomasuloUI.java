import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.JTextArea;

public class TomasuloUI {

	Tomasulo tomasulo;

	private JFrame frame;
	private JTable addTable;
	private JTable mulTable;
	private JTable loadTable;
	private JTable storeTable;
	private JTable regTable;
	private JLabel cycleLabel;
	private JButton next;
	private JTable queueTable;
	private JTextField addTextField;
	private JTextField subTextField;
	private JTextField divTextField;
	private JTextField mulTextField;
	private JTextField storeTextField;
	private JTextField loadTextField;
	private JLabel errorLbl;
	private JTextArea AssemblyTextArea;
	private JTable memoryTable;
	private JTable busTable;
	private JLabel lblAdd;
	private JLabel lblSub;
	private JLabel lblMul;
	private JLabel lblDiv;
	private JLabel lblLoad;
	private JLabel lblStore;
	private JTable insertReg;
	private JTable insertMem;
	
	private int addL = 1;
	private int subL = 1;
	private int mulL = 1;
	private int divL = 1;
	private int loadL = 1;
	private int storeL = 1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Tomasulo tomasulo = new Tomasulo(" ");
					TomasuloUI window = new TomasuloUI(tomasulo);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TomasuloUI(Tomasulo t) {
		tomasulo = t;
		// initTomasulo();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */

	@SuppressWarnings("serial")
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 961, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		addTextField = new JTextField("1");
		addTextField.setBounds(171, 177, 152, 28);
		frame.getContentPane().add(addTextField);
		addTextField.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Addition Latency");
		lblNewLabel_3.setBounds(171, 158, 113, 16);
		frame.getContentPane().add(lblNewLabel_3);

		JLabel lblNewLabel_4 = new JLabel("Subtarction Latency");
		lblNewLabel_4.setBounds(171, 234, 122, 16);
		frame.getContentPane().add(lblNewLabel_4);

		subTextField = new JTextField("1");
		subTextField.setColumns(10);
		subTextField.setBounds(171, 253, 152, 28);
		frame.getContentPane().add(subTextField);

		divTextField = new JTextField("1");
		divTextField.setColumns(10);
		divTextField.setBounds(392, 253, 152, 28);
		frame.getContentPane().add(divTextField);

		JLabel lblNewLabel_5 = new JLabel("Division Latency");
		lblNewLabel_5.setBounds(392, 234, 122, 16);
		frame.getContentPane().add(lblNewLabel_5);

		mulTextField = new JTextField("1");
		mulTextField.setColumns(10);
		mulTextField.setBounds(392, 177, 152, 28);
		frame.getContentPane().add(mulTextField);

		JLabel lblNewLabel_6 = new JLabel("Multiplication Latency");
		lblNewLabel_6.setBounds(392, 158, 133, 16);
		frame.getContentPane().add(lblNewLabel_6);

		storeTextField = new JTextField("1");
		storeTextField.setColumns(10);
		storeTextField.setBounds(616, 253, 152, 28);
		frame.getContentPane().add(storeTextField);

		JLabel lblNewLabel_7 = new JLabel("Store Latency");
		lblNewLabel_7.setBounds(616, 234, 122, 16);
		frame.getContentPane().add(lblNewLabel_7);

		loadTextField = new JTextField("1");
		loadTextField.setColumns(10);
		loadTextField.setBounds(616, 177, 152, 28);
		frame.getContentPane().add(loadTextField);

		JLabel lblNewLabel_8 = new JLabel("Load Latency");
		lblNewLabel_8.setBounds(616, 158, 113, 16);
		frame.getContentPane().add(lblNewLabel_8);

		JButton btnNewButton = new JButton("Start");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			
				if (!getInitialReg((DefaultTableModel) insertReg.getModel())) {
					errorLbl.setText("Values in reg file must be ints or doubles");
					errorLbl.setVisible(true);
				}
				else if (!getInitialMem((DefaultTableModel) insertMem.getModel())) {
					errorLbl.setText("Values in memory must be ints or doubles");
					errorLbl.setVisible(true);
				}
				else if (setLatencies()) {
					tomasulo.getLatencies(addL, subL, mulL, divL, loadL, storeL);
					System.out.println(AssemblyTextArea.getText() + "\n\n\n");
					tomasulo.parseTextArea(AssemblyTextArea.getText());
					initTomasulo();
					setLatencyLabels();
					refreshTables();
				} else {
					errorLbl.setText("Latencies must be an integer number greater than 0");
					errorLbl.setVisible(true);
				}
			}
		});
		btnNewButton.setBounds(402, 587, 97, 25);
		frame.getContentPane().add(btnNewButton);

		errorLbl = new JLabel("Latencies must be an integer number greater than 0");
		errorLbl.setForeground(Color.RED);
		errorLbl.setVisible(false);
		errorLbl.setFont(new Font("Tahoma", Font.PLAIN, 16));
		errorLbl.setBounds(275, 555, 372, 28);
		frame.getContentPane().add(errorLbl);

		JLabel lblNewLabel_10 = new JLabel("Tomasulo Simulator");
		lblNewLabel_10.setFont(new Font("Tahoma", Font.PLAIN, 32));
		lblNewLabel_10.setBounds(336, 64, 287, 69);
		frame.getContentPane().add(lblNewLabel_10);

		JLabel lblNewLabel_4_1 = new JLabel("Assembly Instructions:");
		lblNewLabel_4_1.setBounds(171, 294, 133, 16);
		frame.getContentPane().add(lblNewLabel_4_1);

		JLabel lblNewLabel_4_1_1 = new JLabel("Should be in form ADD.D F0 F1 F2");
		lblNewLabel_4_1_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_4_1_1.setBounds(171, 530, 200, 16);
		frame.getContentPane().add(lblNewLabel_4_1_1);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(383, 315, 181, 213);
		frame.getContentPane().add(scrollPane_1);

		insertReg = new JTable();
		insertReg.setModel(
				new DefaultTableModel(new Object[][] { { null, null }, }, new String[] { "Register", "Value" }) {
					boolean[] columnEditables = new boolean[] { false, true };

					public boolean isCellEditable(int row, int column) {
						return columnEditables[column];
					}
				});
		scrollPane_1.setViewportView(insertReg);

		JLabel lblNewLabel_4_1_2 = new JLabel("Initial Reg File:");
		lblNewLabel_4_1_2.setBounds(383, 294, 133, 16);
		frame.getContentPane().add(lblNewLabel_4_1_2);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(587, 315, 181, 213);
		frame.getContentPane().add(scrollPane_2);

		insertMem = new JTable();
		insertMem.setModel(
				new DefaultTableModel(new Object[][] { { null, null }, }, new String[] { "Address", "Value" }) {
					boolean[] columnEditables = new boolean[] { false, true };

					public boolean isCellEditable(int row, int column) {
						return columnEditables[column];
					}
				});
		scrollPane_2.setViewportView(insertMem);

		JLabel lblNewLabel_4_1_2_1 = new JLabel("Initial Memory:");
		lblNewLabel_4_1_2_1.setBounds(587, 294, 133, 16);
		frame.getContentPane().add(lblNewLabel_4_1_2_1);
		
		JLabel lblNewLabel_4_1_1_1 = new JLabel("Double click on the Value in memory or reg file, set it then press enter");
		lblNewLabel_4_1_1_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_4_1_1_1.setBounds(383, 530, 388, 16);
		frame.getContentPane().add(lblNewLabel_4_1_1_1);
				
				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setBounds(171, 315, 198, 213);
				frame.getContentPane().add(scrollPane);
		
				AssemblyTextArea = new JTextArea();
				scrollPane.setViewportView(AssemblyTextArea);

		initializeInsertReg((DefaultTableModel) insertReg.getModel());
		insertReg.repaint();

		initializeInsertMem((DefaultTableModel) insertMem.getModel());
		insertMem.repaint();

	}

	private void initTomasulo() {
		// remove these---------------------------------
//		frame = new JFrame();
//		frame.setBounds(100, 100, 961, 683);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.getContentPane().setLayout(null);
		// -------------------------------------------------------

		frame.getContentPane().removeAll();
		frame.getContentPane().setLayout(null);
		frame.repaint();

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 260, 450, 71);
		frame.getContentPane().add(scrollPane_1);

		addTable = new JTable();
		addTable.setRowSelectionAllowed(false);
		addTable.setDefaultEditor(Object.class, null);
		addTable.setFocusable(false);
		scrollPane_1.setViewportView(addTable);
		addTable.setModel(new DefaultTableModel(
				new Object[][] { { null, null, null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null, null, null }, },
				new String[] { "Tag", "Busy", "op", "Vj", "Vk", "Qj", "Qk", "isExecuting", "Finish Time" }));

		JLabel lblNewLabel = new JLabel("Add Reservation Stations");
		lblNewLabel.setBounds(12, 242, 190, 16);
		frame.getContentPane().add(lblNewLabel);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(502, 260, 429, 55);
		frame.getContentPane().add(scrollPane_2);

		mulTable = new JTable();
		mulTable.setRowSelectionAllowed(false);
		mulTable.setDefaultEditor(Object.class, null);
		mulTable.setFocusable(false);
		scrollPane_2.setViewportView(mulTable);
		mulTable.setModel(new DefaultTableModel(
				new Object[][] { { null, null, null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null, null, null }, },
				new String[] { "Tag", "Busy", "op", "Vj", "Vk", "Qj", "Qk", "isExecuting", "Finish Time" }));
		mulTable.getColumnModel().getColumn(6).setPreferredWidth(76);

		JLabel lblMultiplicationReservationStations = new JLabel("Multiplication Reservation Stations");
		lblMultiplicationReservationStations.setBounds(501, 242, 248, 16);
		frame.getContentPane().add(lblMultiplicationReservationStations);

		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(12, 408, 238, 71);
		frame.getContentPane().add(scrollPane_3);

		loadTable = new JTable();
		loadTable.setRowSelectionAllowed(false);
		loadTable.setDefaultEditor(Object.class, null);
		loadTable.setFocusable(false);
		scrollPane_3.setViewportView(loadTable);
		loadTable.setModel(new DefaultTableModel(
				new Object[][] { { null, null, null, null, null }, { null, null, null, null, null },
						{ null, null, null, null, null } },
				new String[] { "Tag", "busy", "Effective Address", "Is Executing", "Finish Time" }));

		JLabel lblNewLabel_1 = new JLabel("Store Buffers");
		lblNewLabel_1.setBounds(262, 388, 190, 16);
		frame.getContentPane().add(lblNewLabel_1);

		next = new JButton("Next Cycle");
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				nextCycle();
			}
		});
		next.setBounds(419, 598, 97, 25);
		frame.getContentPane().add(next);

		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(262, 408, 304, 71);
		frame.getContentPane().add(scrollPane_4);

		storeTable = new JTable();
		storeTable.setRowSelectionAllowed(false);
		storeTable.setDefaultEditor(Object.class, null);
		storeTable.setFocusable(false);
		scrollPane_4.setViewportView(storeTable);
		storeTable.setModel(new DefaultTableModel(
				new Object[][] { { null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null }, { null, null, null, null, null, null, null }, },
				new String[] { "Tag", "Busy", "V", "Q", "Effective Address", "is Executing", "Finish Time" }));

		JLabel lblNewLabel_1_1 = new JLabel("Register File");
		lblNewLabel_1_1.setBounds(593, 388, 97, 16);
		frame.getContentPane().add(lblNewLabel_1_1);

		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setBounds(593, 408, 163, 175);
		frame.getContentPane().add(scrollPane_5);

		regTable = new JTable();
		regTable.setRowSelectionAllowed(false);
		regTable.setDefaultEditor(Object.class, null);
		regTable.setFocusable(false);
		scrollPane_5.setViewportView(regTable);
		regTable.setModel(new DefaultTableModel(new Object[][] { { null, null, null }, },
				new String[] { "Register", "Q", "Value" }));

		JLabel lblNewLabel_1_2 = new JLabel("Load Buffers");
		lblNewLabel_1_2.setBounds(12, 388, 190, 16);
		frame.getContentPane().add(lblNewLabel_1_2);

		JLabel lblNewLabel_2 = new JLabel("Cycle #");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel_2.setBounds(382, 13, 60, 31);
		frame.getContentPane().add(lblNewLabel_2);

		cycleLabel = new JLabel("0");
		cycleLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		cycleLabel.setBounds(440, 13, 38, 31);
		frame.getContentPane().add(cycleLabel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 66, 642, 151);
		frame.getContentPane().add(scrollPane);

		queueTable = new JTable();
		queueTable.setModel(new DefaultTableModel(
				new Object[][] { { null, null, null, null, null }, { null, null, null, null, null }, },
				new String[] { "Instruction", "Issue", "Start", "Finish", "Write Back" }));
		queueTable.getColumnModel().getColumn(0).setPreferredWidth(130);
		queueTable.getColumnModel().getColumn(0).setMinWidth(125);
		queueTable.setRowHeight(20);
		queueTable.setRowSelectionAllowed(false);
		queueTable.setDefaultEditor(Object.class, null);
		queueTable.setFocusable(false);
		scrollPane.setViewportView(queueTable);

		JLabel lblInstructionQueue = new JLabel("Instruction Queue");
		lblInstructionQueue.setBounds(12, 45, 190, 16);
		frame.getContentPane().add(lblInstructionQueue);

		JLabel lblNewLabel_1_1_1 = new JLabel("Memory");
		lblNewLabel_1_1_1.setBounds(768, 388, 97, 16);
		frame.getContentPane().add(lblNewLabel_1_1_1);

		JScrollPane scrollPane_6 = new JScrollPane();
		scrollPane_6.setBounds(768, 408, 163, 175);
		frame.getContentPane().add(scrollPane_6);

		memoryTable = new JTable();
		memoryTable.setRowSelectionAllowed(false);
		memoryTable.setDefaultEditor(Object.class, null);
		memoryTable.setFocusable(false);
		memoryTable.setModel(
				new DefaultTableModel(new Object[][] { { null, null }, }, new String[] { "Address", "Value" }));
		scrollPane_6.setViewportView(memoryTable);

		JScrollPane scrollPane_7 = new JScrollPane();
		scrollPane_7.setBounds(12, 534, 140, 39);
		frame.getContentPane().add(scrollPane_7);

		busTable = new JTable();
		busTable.setRowSelectionAllowed(false);
		busTable.setDefaultEditor(Object.class, null);
		busTable.setFocusable(false);
		scrollPane_7.setViewportView(busTable);
		busTable.setModel(new DefaultTableModel(new Object[][] { { null, null }, }, new String[] { "Tag", "Value" }));

		JLabel lblNewLabel_1_2_1 = new JLabel("Value on Bus");
		lblNewLabel_1_2_1.setBounds(12, 513, 109, 16);
		frame.getContentPane().add(lblNewLabel_1_2_1);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Latencies", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBounds(706, 66, 168, 151);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblNewLabel_9 = new JLabel("Addition:");
		lblNewLabel_9.setBounds(12, 20, 64, 16);
		panel.add(lblNewLabel_9);

		JLabel lblNewLabel_9_1 = new JLabel("Subtraction:");
		lblNewLabel_9_1.setBounds(12, 40, 72, 16);
		panel.add(lblNewLabel_9_1);

		JLabel lblNewLabel_9_2 = new JLabel("Multiplication:");
		lblNewLabel_9_2.setBounds(12, 60, 84, 16);
		panel.add(lblNewLabel_9_2);

		JLabel lblNewLabel_9_3 = new JLabel("Load:");
		lblNewLabel_9_3.setBounds(12, 100, 49, 16);
		panel.add(lblNewLabel_9_3);

		JLabel lblNewLabel_9_4 = new JLabel("Store:");
		lblNewLabel_9_4.setBounds(12, 120, 56, 16);
		panel.add(lblNewLabel_9_4);

		lblAdd = new JLabel("#");
		lblAdd.setBounds(108, 20, 56, 16);
		panel.add(lblAdd);
		lblAdd.setFont(new Font("Tahoma", Font.BOLD, 13));

		lblSub = new JLabel("#");
		lblSub.setBounds(108, 40, 56, 16);
		panel.add(lblSub);
		lblSub.setFont(new Font("Tahoma", Font.BOLD, 13));

		lblMul = new JLabel("#");
		lblMul.setBounds(108, 60, 56, 16);
		panel.add(lblMul);
		lblMul.setFont(new Font("Tahoma", Font.BOLD, 13));

		lblLoad = new JLabel("#");
		lblLoad.setBounds(108, 100, 56, 16);
		panel.add(lblLoad);
		lblLoad.setFont(new Font("Tahoma", Font.BOLD, 13));

		lblStore = new JLabel("#");
		lblStore.setBounds(108, 120, 56, 16);
		panel.add(lblStore);
		lblStore.setFont(new Font("Tahoma", Font.BOLD, 13));

		JLabel lblNewLabel_9_2_1 = new JLabel("Division:");
		lblNewLabel_9_2_1.setBounds(12, 80, 72, 16);
		panel.add(lblNewLabel_9_2_1);

		lblDiv = new JLabel("#");
		lblDiv.setBounds(108, 80, 56, 16);
		panel.add(lblDiv);
		lblDiv.setFont(new Font("Tahoma", Font.BOLD, 13));
	}

	public void addReservationsUI(DefaultTableModel model) {
		// DefaultTableModel model = new DefaultTableModel();
		ArrayList<String> list = new ArrayList<String>();

		HashMap[] arr = tomasulo.addReservations;

		model.setRowCount(0);

		for (int i = 0; i < tomasulo.addReservations.length; i++) {

			String op = "";
			if ((int) arr[i].get("op") == 0) {
				op = "ADD";
			}
			if ((int) arr[i].get("op") == 1) {
				op = "SUB";
			}
			int busy = (int) arr[i].get("busy");

			list.add("A" + i);
			list.add(arr[i].get("busy") + "");
			if (busy == 1) {
				list.add(op);
				list.add(arr[i].get("Vj") + "");
				list.add(arr[i].get("Vk") + "");
				list.add(arr[i].get("Qj") + "");
				list.add(arr[i].get("Qk") + "");
				list.add(arr[i].get("isExecuting") + "");
				list.add(arr[i].get("finishTime") + "");
			}

			model.addRow(list.toArray());
			list.clear();
		}
		addTable.setModel(model);
	}

	public void mulReservationsUI(DefaultTableModel model) {
		// DefaultTableModel model = new DefaultTableModel();
		ArrayList<String> list = new ArrayList<String>();

		HashMap[] arr = tomasulo.mulReservations;

		model.setRowCount(0);

		for (int i = 0; i < tomasulo.mulReservations.length; i++) {

			String op = "";
			if ((int) arr[i].get("op") == 2) {
				op = "MUL";
			}
			if ((int) arr[i].get("op") == 3) {
				op = "DIV";
			}

			int busy = (int) arr[i].get("busy");

			list.add("M" + i);
			list.add(arr[i].get("busy") + "");
			if (busy == 1) {
				list.add(op);
				list.add(arr[i].get("Vj") + "");
				list.add(arr[i].get("Vk") + "");
				list.add(arr[i].get("Qj") + "");
				list.add(arr[i].get("Qk") + "");
				list.add(arr[i].get("isExecuting") + "");
				list.add(arr[i].get("finishTime") + "");
			}

			model.addRow(list.toArray());
			list.clear();
		}
		mulTable.setModel(model);
	}

	public void loadBuffersUI(DefaultTableModel model) {
		ArrayList<String> list = new ArrayList<String>();

		HashMap[] arr = tomasulo.loadBuffers;

		model.setRowCount(0);

		for (int i = 0; i < tomasulo.loadBuffers.length; i++) {

			int busy = (int) arr[i].get("busy");

			list.add("L" + i);
			list.add(arr[i].get("busy") + "");
			if (busy == 1) {
				list.add(arr[i].get("effectiveAddress") + "");
				list.add(arr[i].get("isExecuting") + "");
				list.add(arr[i].get("finishTime") + "");
			}

			model.addRow(list.toArray());
			list.clear();
		}
		loadTable.setModel(model);
	}

	public void storeBuffersUI(DefaultTableModel model) {
		ArrayList<String> list = new ArrayList<String>();

		HashMap[] arr = tomasulo.storeBuffers;

		model.setRowCount(0);

		for (int i = 0; i < tomasulo.storeBuffers.length; i++) {

			int busy = (int) arr[i].get("busy");

			list.add("S" + i);
			list.add(arr[i].get("busy") + "");
			if (busy == 1) {
				list.add(arr[i].get("V") + "");
				list.add(arr[i].get("Q") + "");
				list.add(arr[i].get("effectiveAddress") + "");
				list.add(arr[i].get("isExecuting") + "");
				list.add(arr[i].get("finishTime") + "");
			}

			model.addRow(list.toArray());
			list.clear();
		}
		storeTable.setModel(model);
	}

	public void regFileUI(DefaultTableModel model) {
		ArrayList<String> list = new ArrayList<String>();

		HashMap[] arr = tomasulo.regFile;

		model.setRowCount(0);

		for (int i = 0; i < tomasulo.regFile.length; i++) {
			list.add("F" + i);
			list.add(arr[i].get("Q") + "");
			list.add(arr[i].get("V") + "");

			model.addRow(list.toArray());
			list.clear();
		}
		regTable.setModel(model);
	}

	public void queueUI(DefaultTableModel model) {
		// DefaultTableModel model = new DefaultTableModel();
		ArrayList<String> list = new ArrayList<String>();

		ArrayList<String> program = tomasulo.program;
		HashMap[] arr = tomasulo.queueStatus;

		model.setRowCount(0);

		for (int i = 0; i < tomasulo.queueStatus.length; i++) {

			String issue = (int) arr[i].get("issue") != -1 ? arr[i].get("issue") + "" : "";
			String start = (int) arr[i].get("start") != -1 ? arr[i].get("start") + "" : "";
			String finish = (int) arr[i].get("finish") != -1 ? arr[i].get("finish") + "" : "";
			String wb = (int) arr[i].get("writeBack") != -1 ? arr[i].get("writeBack") + "" : "";

			list.add(program.get(i));
			list.add(issue);
			list.add(start);
			list.add(finish);
			list.add(wb);

			model.addRow(list.toArray());
			list.clear();
		}
		queueTable.setModel(model);
	}

	public void memoryUI(DefaultTableModel model) {
		// DefaultTableModel model = new DefaultTableModel();
		ArrayList<String> list = new ArrayList<String>();

		double memory[] = tomasulo.memory;

		model.setRowCount(0);

		for (int i = 0; i < tomasulo.memory.length; i++) {
			list.add(i + "");
			list.add(memory[i] + "");

			model.addRow(list.toArray());
			list.clear();
		}
		memoryTable.setModel(model);
	}

	public void busUI(DefaultTableModel model) {
		// DefaultTableModel model = new DefaultTableModel();
		ArrayList<String> list = new ArrayList<String>();

		HashMap bus = tomasulo.bus;

		model.setRowCount(0);

		String tag = bus.get("Tag") + "";
		tag = tag.equals("null") ? "" : tag;

		String value = bus.get("Value") + "";
		value = value.equals("null") ? "" : value;

		list.add(tag + "");
		list.add(value + "");

		model.addRow(list.toArray());
		list.clear();

		busTable.setModel(model);
	}

	public void initializeInsertReg(DefaultTableModel model) {
		ArrayList<String> list = new ArrayList<String>();

		model.setRowCount(0);

		for (int i = 0; i < 32; i++) {
			list.add("F" + i);
			list.add(0.0 + "");
			model.addRow(list.toArray());
			list.clear();
		}
		insertReg.setModel(model);
	}

	public boolean getInitialReg(DefaultTableModel model) {

		HashMap[] regs = tomasulo.regFile;
		try {
			for (int i = 0; i < 32; i++) {
				String data = (String) model.getValueAt(i, 1);
				data.trim();
				double d = Double.parseDouble(data);
				//System.out.println(d);
				HashMap h = new HashMap();
				h.put("Q", "0");
				h.put("V", d);
				regs[i] = h;
			}

			tomasulo.regFile = regs;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void initializeInsertMem(DefaultTableModel model) {
		ArrayList<String> list = new ArrayList<String>();

		model.setRowCount(0);
		for (int i = 0; i < 1024; i++) {
			list.add(i + "");
			list.add(0.0 + "");
			model.addRow(list.toArray());
			list.clear();
		}
		insertMem.setModel(model);
	}

	public boolean getInitialMem(DefaultTableModel model) {
		double[] mem = tomasulo.memory;
		try {
			for (int i = 0; i < 1024; i++) {
				String data = (String) model.getValueAt(i, 1);
				data.trim();
				double d = Double.parseDouble(data);
				//System.out.println(d);
				mem[i] = d;
			}
			tomasulo.memory = mem;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean setLatencies() {
		try {
			addL = Integer.parseInt(addTextField.getText());
			subL = Integer.parseInt(subTextField.getText());
			mulL = Integer.parseInt(mulTextField.getText());
			divL = Integer.parseInt(divTextField.getText());
			loadL = Integer.parseInt(loadTextField.getText());
			storeL = Integer.parseInt(storeTextField.getText());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void refreshTables() {
		addReservationsUI((DefaultTableModel) addTable.getModel());
		addTable.repaint();

		mulReservationsUI((DefaultTableModel) mulTable.getModel());
		mulTable.repaint();

		loadBuffersUI((DefaultTableModel) loadTable.getModel());
		loadTable.repaint();

		storeBuffersUI((DefaultTableModel) storeTable.getModel());
		storeTable.repaint();

		regFileUI((DefaultTableModel) regTable.getModel());
		regTable.repaint();

		queueUI((DefaultTableModel) queueTable.getModel());
		queueTable.repaint();

		memoryUI((DefaultTableModel) memoryTable.getModel());
		memoryTable.repaint();

		busUI((DefaultTableModel) busTable.getModel());
		busTable.repaint();
	}

	public void setLatencyLabels() {
		lblAdd.setText(addL + "");
		lblSub.setText(subL + "");
		lblMul.setText(mulL + "");
		lblDiv.setText(divL + "");
		lblLoad.setText(loadL + "");
		lblStore.setText(storeL + "");
	}

	public void nextCycle() {

		boolean notDone = tomasulo.nextCycle();
		next.setEnabled(notDone);

		addReservationsUI((DefaultTableModel) addTable.getModel());
		addTable.repaint();
		mulReservationsUI((DefaultTableModel) mulTable.getModel());
		mulTable.repaint();
		loadBuffersUI((DefaultTableModel) loadTable.getModel());
		loadTable.repaint();
		storeBuffersUI((DefaultTableModel) storeTable.getModel());
		storeTable.repaint();
		regFileUI((DefaultTableModel) regTable.getModel());
		regTable.repaint();
		queueUI((DefaultTableModel) queueTable.getModel());
		queueTable.repaint();
		memoryUI((DefaultTableModel) memoryTable.getModel());
		memoryTable.repaint();
		busUI((DefaultTableModel) busTable.getModel());
		busTable.repaint();

		cycleLabel.setText(tomasulo.cycles - 1 + "");
	}
}
