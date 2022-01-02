import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.*;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Tomasulo {

	int cycles = 1;
	int addLatency = 0;
	int subLatency = 0;
	int mulLatency = 0;
	int divLatency = 0;
	int loadLatency = 0;
	int storeLatency = 0;

	Queue<HashMap> instructionQueue = new LinkedList<HashMap>();
	HashMap[] regFile = new HashMap[32]; // Q, val
	double[] memory = new double[1024];

	HashMap[] addReservations = new HashMap[3];
	HashMap[] mulReservations = new HashMap[2];
	HashMap[] loadBuffers = new HashMap[3];
	HashMap[] storeBuffers = new HashMap[3];
	HashMap bus = new HashMap(); // Tag: A1, Value:1.111

	int finishTime = 0;

	ArrayList<String> program = new ArrayList();
	HashMap[] queueStatus;

	public void initializeStations() {
		for (int i = 0; i < addReservations.length; i++) {
			HashMap h = new HashMap();
			h.put("busy", 0);
			h.put("op", -1);
			h.put("Qj", "0");
			h.put("Qk", "0");
			h.put("Vj", 0.0);
			h.put("Vk", 0.0);
			h.put("isExecuting", 0);

			h.put("id", -1);
			addReservations[i] = h;
		}
		for (int i = 0; i < mulReservations.length; i++) {
			HashMap h = new HashMap();
			h.put("busy", 0);
			h.put("op", -1);
			h.put("Qj", "0");
			h.put("Qk", "0");
			h.put("Vj", 0.0);
			h.put("Vk", 0.0);
			h.put("isExecuting", 0);
			h.put("id", -1);
			mulReservations[i] = h;

		}
		for (int i = 0; i < loadBuffers.length; i++) {
			HashMap h = new HashMap();
			h.put("busy", 0);
			h.put("op", -1);
			h.put("effectiveAddress", 0);
			h.put("isExecuting", 0);
			h.put("id", -1);
			loadBuffers[i] = h;
		}
		for (int i = 0; i < storeBuffers.length; i++) {
			HashMap h = new HashMap();
			h.put("busy", 0);
			h.put("op", -1);
			h.put("effectiveAddress", 0);
			h.put("V", 0.0);
			h.put("Q", "0");
			h.put("isExecuting", 0);
			h.put("id", -1);
			storeBuffers[i] = h;
		}
		for (int i = 0; i < regFile.length; i++) {
			HashMap h = new HashMap();
			h.put("Q", "0");
			h.put("V", 0.0);
			regFile[i] = h;
		}

	}

	public void parse(String programFileName) {
		String dir = System.getProperty("user.dir");

		try {
			BufferedReader reader = new BufferedReader(new FileReader(dir + "/src/" + programFileName + ".txt"));
			String row;

			while ((row = reader.readLine()) != null) {
				instructionQueue.add(parseInstruction(row));
				program.add(row);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		queueStatus = new HashMap[program.size()];
		for (int i = 0; i < program.size(); i++) {
			HashMap h = new HashMap();
			h.put("id", i);
			h.put("issue", -1);
			h.put("start", -1);
			h.put("finish", -1);
			h.put("writeBack", -1);
			h.put("done", 0);
			queueStatus[i] = h;
		}

	}

	public HashMap<String, Integer> parseInstruction(String instruction) {
		int op = 0;
		int r1 = 0;
		int r2 = 0;
		int r3 = 0;
		int effectiveAddress = 0;
		String[] res = instruction.split(" ");

		for (int i = 0; i < res.length; i++) {
			System.out.print(res[i] + " ");
		}
		System.out.println();
		switch (res[0]) {
		case "ADD.D":
			op = 0;
			r1 = Integer.parseInt(res[1].substring(1));
			r2 = Integer.parseInt(res[2].substring(1));
			r3 = Integer.parseInt(res[3].substring(1));
			break;
		case "SUB.D":
			op = 1;
			r1 = Integer.parseInt(res[1].substring(1));
			r2 = Integer.parseInt(res[2].substring(1));
			r3 = Integer.parseInt(res[3].substring(1));
			break;
		case "MUL.D":
			op = 2;
			r1 = Integer.parseInt(res[1].substring(1));
			r2 = Integer.parseInt(res[2].substring(1));
			r3 = Integer.parseInt(res[3].substring(1));
			break;
		case "DIV.D":
			op = 3;
			r1 = Integer.parseInt(res[1].substring(1));
			r2 = Integer.parseInt(res[2].substring(1));
			r3 = Integer.parseInt(res[3].substring(1));
			break;
		case "L.D":
			op = 4;
			r1 = Integer.parseInt(res[1].substring(1));
			effectiveAddress = Integer.parseInt(res[2]);
			break;
		case "S.D":
			op = 5;
			r1 = Integer.parseInt(res[1].substring(1));
			effectiveAddress = Integer.parseInt(res[2]);
			break;

		default:
			System.out.println("Error: Unknown Instruction! 404 NOT FOUND!");
		}

		HashMap<String, Integer> inst = new HashMap<String, Integer>();
		inst.put("op", op);
		inst.put("r1", r1);
		inst.put("r2", r2);
		inst.put("r3", r3);
		inst.put("effectiveAddress", effectiveAddress);
		inst.put("id", instructionQueue.size());
		return inst;
	}

	public boolean canIssue(int op) {
		switch (op) {
		case 0:
		case 1:
			for (int i = 0; i < addReservations.length; i++) {
				if ((int) addReservations[i].get("busy") == 0) {
					return true;
				}
			}
			break;
		case 2:
		case 3:
			for (int i = 0; i < mulReservations.length; i++) {
				if ((int) mulReservations[i].get("busy") == 0) {
					return true;
				}
			}
			break;
		case 4:
			for (int i = 0; i < loadBuffers.length; i++) {
				if ((int) loadBuffers[i].get("busy") == 0) {
					return true;
				}
			}
			break;
		case 5:
			for (int i = 0; i < storeBuffers.length; i++) {
				if ((int) storeBuffers[i].get("busy") == 0) {
					return true;
				}
			}
			break;
		default:
			System.out.println("Error: Unknown Instruction! 404 NOT FOUND!");
		}
		return false;
	}

	public void issueAdd(HashMap<String, Integer> inst) {
		int op = inst.get("op");
		int r1 = inst.get("r1");
		int r2 = inst.get("r2");
		int r3 = inst.get("r3");
		int id = inst.get("id");
		for (int i = 0; i < addReservations.length; i++) {
			if ((int) addReservations[i].get("busy") == 0) {
				addReservations[i].put("busy", 1);
				addReservations[i].put("op", op);

				if ((regFile[r2].get("Q") + "").equals("0")) { // check this issue when running the code
					addReservations[i].put("Qj", 0);
					addReservations[i].put("Vj", (double) regFile[r2].get("V"));
				} else {
					addReservations[i].put("Qj", regFile[r2].get("Q"));
				}

				if ((regFile[r3].get("Q") + "").equals("0")) { // check this issue when running the code
					addReservations[i].put("Qk", 0);
					addReservations[i].put("Vk", (double) regFile[r3].get("V"));
				} else {
					addReservations[i].put("Qk", regFile[r3].get("Q"));
				}
				regFile[r1].put("Q", "A" + i);
				addReservations[i].put("isExecuting", 0);
				addReservations[i].put("id", id);
				break;
			}
		}
	}

	public void issueMul(HashMap<String, Integer> inst) {
		int op = inst.get("op");
		int r1 = inst.get("r1");
		int r2 = inst.get("r2");
		int r3 = inst.get("r3");
		int id = inst.get("id");
		for (int i = 0; i < mulReservations.length; i++) {
			if ((int) mulReservations[i].get("busy") == 0) {
				mulReservations[i].put("busy", 1);
				mulReservations[i].put("op", op);

				if ((regFile[r2].get("Q") + "").equals("0")) { // check this issue when running the code
					mulReservations[i].put("Qj", 0);
					mulReservations[i].put("Vj", (double) regFile[r2].get("V"));
				} else {
					mulReservations[i].put("Qj", regFile[r2].get("Q"));
				}

				if ((regFile[r3].get("Q") + "").equals("0")) { // check this issue when running the code
					mulReservations[i].put("Qk", 0);
					mulReservations[i].put("Vk", (double) regFile[r3].get("V"));
				} else {
					mulReservations[i].put("Qk", regFile[r3].get("Q"));
				}
				regFile[r1].put("Q", "M" + i);
				mulReservations[i].put("isExecuting", 0);
				mulReservations[i].put("id", id);
				break;
			}
		}
	}

	public void issueLoad(HashMap<String, Integer> inst) {
		int r1 = inst.get("r1");
		int effectiveAddress = inst.get("effectiveAddress");
		int op = inst.get("op");
		int id = inst.get("id");

		for (int i = 0; i < loadBuffers.length; i++) {
			if ((int) loadBuffers[i].get("busy") == 0) {
				loadBuffers[i].put("busy", 1);
				loadBuffers[i].put("op", op);
				loadBuffers[i].put("effectiveAddress", effectiveAddress);
				loadBuffers[i].put("id", id);
				regFile[r1].put("Q", "L" + i);
				break;
			}
		}
	}

	public void issueStore(HashMap<String, Integer> inst) {
		int r1 = inst.get("r1");
		int effectiveAddress = inst.get("effectiveAddress");
		int op = inst.get("op");
		int id = inst.get("id");

		for (int i = 0; i < storeBuffers.length; i++) {
			if ((int) storeBuffers[i].get("busy") == 0) {
				storeBuffers[i].put("busy", 1);
				storeBuffers[i].put("op", op);
				if ((regFile[r1].get("Q") + "").equals("0")) { // check this issue when running the code
					storeBuffers[i].put("Q", 0);
					storeBuffers[i].put("V", (double) regFile[r1].get("V"));
				} else {
					storeBuffers[i].put("Q", regFile[r1].get("Q"));
				}
				storeBuffers[i].put("effectiveAddress", effectiveAddress);
				storeBuffers[i].put("isExecuting", 0);
				storeBuffers[i].put("id", id);
				break;
			}
		}
	}

	public void issue(HashMap instruction) {
		int op = (int) instruction.get("op");
		queueStatus[(int) instruction.get("id")].put("issue", cycles);

		switch (op) {
		case 0:
		case 1:
			issueAdd(instruction);
			break;
		case 2:
		case 3:
			issueMul(instruction);
			break;
		case 4:
			issueLoad(instruction);
			break;
		case 5:
			issueStore(instruction);
			break;

		default:
			System.out.println("fe moshkela fl issue");
		}

	}

	public void passToCanExecute() {
		for (int i = 0; i < addReservations.length; i++) {
			if ((int) addReservations[i].get("busy") == 1 && (int) addReservations[i].get("op") == 0) {
				canExecute("A" + i);
				finishedExecution("A" + i);
			} else if ((int) addReservations[i].get("busy") == 1 && (int) addReservations[i].get("op") == 1) {
				canExecute("S" + i);
				finishedExecution("S" + i);
			}
		}
		for (int i = 0; i < mulReservations.length; i++) {
			if ((int) mulReservations[i].get("busy") == 1 && (int) mulReservations[i].get("op") == 2) {
				canExecute("M" + i);
				finishedExecution("M" + i);
			} else if ((int) mulReservations[i].get("busy") == 1 && (int) mulReservations[i].get("op") == 3) {
				canExecute("D" + i);
				finishedExecution("D" + i);
			}

		}
		for (int i = 0; i < loadBuffers.length; i++) {
			if ((int) loadBuffers[i].get("busy") == 1) {
				canExecute("L" + i);
				finishedExecution("L" + i);
			}
		}
		for (int i = 0; i < storeBuffers.length; i++) {
			if ((int) storeBuffers[i].get("busy") == 1) {
				canExecute("T" + i);
				finishedExecution("T" + i);
			}
		}

	}

	public void canExecute(String id) { // checks if you can execute and adds finishTime and isExecuting
		String station = id.charAt(0) + "";
		int i = Integer.parseInt(id.charAt(1) + "");

		switch (station) {
		case "A":
		case "S":
			if ((int) addReservations[i].get("busy") == 1 && (int) addReservations[i].get("isExecuting") == 0
					&& (addReservations[i].get("Qj") + "").equals("0")
					&& (addReservations[i].get("Qk") + "").equals("0")) {
				addReservations[i].put("isExecuting", 1);
				if (station.equals("A")) {
					addReservations[i].put("finishTime", cycles + (addLatency - 1));
					queueStatus[(int) addReservations[i].get("id")].put("finish", cycles + (addLatency - 1));
				} else if (station.equals("S")) {
					addReservations[i].put("finishTime", cycles + (subLatency - 1));
					queueStatus[(int) addReservations[i].get("id")].put("finish", cycles + (subLatency - 1));
				}
				queueStatus[(int) addReservations[i].get("id")].put("start", cycles);
			}
			break;
		case "M":
		case "D":
			if ((int) mulReservations[i].get("busy") == 1 && (int) mulReservations[i].get("isExecuting") == 0
					&& (mulReservations[i].get("Qj") + "").equals("0")
					&& (mulReservations[i].get("Qk") + "").equals("0")) {
				mulReservations[i].put("isExecuting", 1);
				if (station.equals("M")) {
					mulReservations[i].put("finishTime", cycles + (mulLatency - 1));
					queueStatus[(int) mulReservations[i].get("id")].put("finish", cycles + (mulLatency - 1));
				} else if (station.equals("D")) {
					mulReservations[i].put("finishTime", cycles + (divLatency - 1));
					queueStatus[(int) mulReservations[i].get("id")].put("finish", cycles + (divLatency - 1));
				}
				queueStatus[(int) mulReservations[i].get("id")].put("start", cycles);
			}
			break;
		case "L":
			if ((int) loadBuffers[i].get("busy") == 1 && (int) loadBuffers[i].get("isExecuting") == 0) {
				loadBuffers[i].put("isExecuting", 1);
				loadBuffers[i].put("finishTime", cycles + (loadLatency - 1));
				queueStatus[(int) loadBuffers[i].get("id")].put("finish", cycles + (loadLatency - 1));
				queueStatus[(int) loadBuffers[i].get("id")].put("start", cycles);
			}
			break;
		case "T":
			if ((int) storeBuffers[i].get("busy") == 1 && (int) storeBuffers[i].get("isExecuting") == 0
					&& (storeBuffers[i].get("Q") + "").equals("0")) {
				storeBuffers[i].put("isExecuting", 1);
				storeBuffers[i].put("finishTime", cycles + (storeLatency - 1));

				queueStatus[(int) storeBuffers[i].get("id")].put("finish", cycles + (storeLatency - 1));
				queueStatus[(int) storeBuffers[i].get("id")].put("start", cycles);
			}
			break;

		default:
			System.out.println("We are going to lose marks if this gets called");
		}

	}

	public void finishedExecution(String id) { // actually executes
		// check current cycle against finished time and IF finished: actually execute,
		// store the result
		// and return it next cycle !!!!

		String station = id.charAt(0) + "";
		int i = Integer.parseInt(id.charAt(1) + "");

		switch (station) {
		case "A":
		case "S":
			if ((int) addReservations[i].get("isExecuting") == 1
					&& (int) addReservations[i].get("finishTime") == cycles) {
				if (station.equals("A")) {
					double r1 = (double) addReservations[i].get("Vj");
					double r2 = (double) addReservations[i].get("Vk");
					addReservations[i].put("valueToPublish", (r1 + r2));

				} else if (station.equals("S")) {
					double r1 = (double) addReservations[i].get("Vj");
					double r2 = (double) addReservations[i].get("Vk");
					addReservations[i].put("valueToPublish", (r1 - r2));
				}
			}
			break;
		case "M":
		case "D":
			if ((int) mulReservations[i].get("isExecuting") == 1
					&& (int) mulReservations[i].get("finishTime") == cycles) {
				if (station.equals("M")) {
					double r1 = (double) mulReservations[i].get("Vj");
					double r2 = (double) mulReservations[i].get("Vk");
					mulReservations[i].put("valueToPublish", (r1 * r2));

				} else if (station.equals("D")) {
					double r1 = (double) mulReservations[i].get("Vj");
					double r2 = (double) mulReservations[i].get("Vk");
					mulReservations[i].put("valueToPublish", (r1 / r2));
				}
			}
			break;
		case "L":
			if ((int) loadBuffers[i].get("isExecuting") == 1 && (int) loadBuffers[i].get("finishTime") == cycles) {
				int effectiveAddress = (int) loadBuffers[i].get("effectiveAddress");

				loadBuffers[i].put("valueToPublish", memory[effectiveAddress]);
			}
			break;
		case "T":
			if ((int) storeBuffers[i].get("isExecuting") == 1 && (int) storeBuffers[i].get("finishTime") == cycles) {
//				int effectiveAddress = (int)storeBuffers[i].get("effectiveAddress");
				double v = (double) storeBuffers[i].get("V");
				storeBuffers[i].put("valueToStore", v);
			}
			break;

		default:
			System.out.println("We are going to lose marks if this gets called");
		}

	}

	public void passToWriteOnBus() {
		for (int i = 0; i < addReservations.length; i++) {
			if ((int) addReservations[i].get("op") == 0 && (int) addReservations[i].get("busy") == 1) {
				if (writeOnBus("A" + i))
					return;

			} else if ((int) addReservations[i].get("op") == 1 && (int) addReservations[i].get("busy") == 1) {
				if (writeOnBus("S" + i))
					return;

			}
		}
		for (int i = 0; i < mulReservations.length; i++) {
			if ((int) mulReservations[i].get("op") == 2 && (int) mulReservations[i].get("busy") == 1) {
				if (writeOnBus("M" + i))
					return;

			} else if ((int) mulReservations[i].get("op") == 3 && (int) mulReservations[i].get("busy") == 1) {
				if (writeOnBus("D" + i))
					return;

			}

		}
		for (int i = 0; i < loadBuffers.length; i++) {
			if ((int) loadBuffers[i].get("busy") == 1) {
				if (writeOnBus("L" + i))
					return;
			}
		}

		storeInMemory();
	}

	public boolean writeOnBus(String id) {

		String station = id.charAt(0) + "";
		int i = Integer.parseInt(id.charAt(1) + "");

		switch (station) {
		case "A":
		case "S":
			if ((int) addReservations[i].get("busy") == 1 && (int) addReservations[i].get("isExecuting") == 1
					&& ((int) addReservations[i].get("finishTime") + 1) <= cycles) { // this may cause errors
																						// -Kersha2022
				if (station.equals("A")) {
					bus.put("Tag", "A" + i);
					bus.put("Value", addReservations[i].get("valueToPublish"));
					addReservations[i].replace("busy", 0);
					addReservations[i].replace("finishTime", null);
					queueStatus[(int) addReservations[i].get("id")].put("writeBack", cycles);
					return true;

				} else if (station.equals("S")) {
					bus.put("Tag", "A" + i);
					bus.put("Value", addReservations[i].get("valueToPublish"));
					addReservations[i].replace("busy", 0);
					addReservations[i].replace("finishTime", null);
					queueStatus[(int) addReservations[i].get("id")].put("writeBack", cycles);
					return true;
				}
			}
			break;
		case "M":
		case "D":
			if ((int) mulReservations[i].get("busy") == 1 && (int) mulReservations[i].get("isExecuting") == 1
					&& ((int) mulReservations[i].get("finishTime") + 1) <= cycles) {
				if (station.equals("M")) {
					bus.put("Tag", "M" + i);
					bus.put("Value", mulReservations[i].get("valueToPublish"));
					mulReservations[i].replace("busy", 0);
					mulReservations[i].replace("finishTime", null);
					queueStatus[(int) mulReservations[i].get("id")].put("writeBack", cycles);
					return true;
				} else if (station.equals("D")) {
					bus.put("Tag", "M" + i);
					bus.put("Value", mulReservations[i].get("valueToPublish"));
					mulReservations[i].replace("busy", 0);
					mulReservations[i].replace("finishTime", null);
					queueStatus[(int) mulReservations[i].get("id")].put("writeBack", cycles);
					return true;
				}
			}
			break;
		case "L":
			if ((int) loadBuffers[i].get("busy") == 1 && (int) loadBuffers[i].get("isExecuting") == 1
					&& ((int) loadBuffers[i].get("finishTime") + 1) <= cycles) {
				bus.put("Tag", "L" + i);
				bus.put("Value", loadBuffers[i].get("valueToPublish"));
				loadBuffers[i].replace("busy", 0);
				loadBuffers[i].replace("finishTime", null);
				queueStatus[(int) loadBuffers[i].get("id")].put("writeBack", cycles);
				return true;
			}
			break;
//		case "T":
//			if (((int) storeBuffers[i].get("finishTime")+1) <= cycles) {
//				double v = (double) storeBuffers[i].get("V");
//				storeBuffers[i].put("valueToStore", v);
//			}
//			break;

		default:
			System.out.println("Hi I am write on bus you made a mistake");
		}
		return false;
	}

	public void storeInMemory() {
		for (int i = 0; i < storeBuffers.length; i++) {
			if ((int) storeBuffers[i].get("busy") == 1 && (int) storeBuffers[i].get("isExecuting") == 1
					&& ((int) storeBuffers[i].get("finishTime") + 1) <= cycles) {
				memory[(int) storeBuffers[i].get("effectiveAddress")] = (double) storeBuffers[i].get("valueToStore");
				storeBuffers[i].put("busy", 0);
				queueStatus[(int) storeBuffers[i].get("id")].put("writeBack", cycles);
				return;

			}
		}
	}

	public void checkIfNeedValStations(HashMap station) {

		if ((station.get("Qj")).equals(bus.get("Tag") + "")) {
			// System.out.println("eq " +(station.get("Qj"))+ " " +(bus.get("Tag")));
			station.replace("Qj", 0);
			station.replace("Vj", (double) bus.get("Value"));

		}
		if ((station.get("Qk")).equals(bus.get("Tag"))) {
			// System.out.println("eq " +(station.get("Qk"))+ " " +(bus.get("Tag")));
			station.replace("Qk", 0);
			station.replace("Vk", (double) bus.get("Value"));
		}

	}

	public void checkIfNeedValBuffers(HashMap station, String type) {
		if ((station.get("Q") + "").equals(bus.get("Tag") + "") && type.equals("S")) {
			station.replace("Q", 0);
			station.put("V", (double) bus.get("Value"));

		}
		if ((station.get("Q") + "").equals(bus.get("Tag") + "") && type.equals("R")) {
			station.replace("Q", 0);
			station.put("V", (double) bus.get("Value"));
		}

	}

	public void readBus() {
		// remember to check if busy
		for (int i = 0; i < addReservations.length; i++) {
			checkIfNeedValStations(addReservations[i]);

		}
		for (int i = 0; i < mulReservations.length; i++) {
			checkIfNeedValStations(mulReservations[i]);

		}
		for (int i = 0; i < storeBuffers.length; i++) {
			checkIfNeedValBuffers(storeBuffers[i], "S");

		}
		for (int i = 0; i < regFile.length; i++) {
			checkIfNeedValBuffers(regFile[i], "R");

		}

//		bus.clear();
	}

	public void getLatencies() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter addition latency:");
		addLatency = sc.nextInt();
		System.out.println("Please enter subtract latency:");
		subLatency = sc.nextInt();
		System.out.println("Please enter multiply latency:");
		mulLatency = sc.nextInt();
		System.out.println("Please enter divide latency:");
		divLatency = sc.nextInt();
		System.out.println("Please enter load latency:");
		loadLatency = sc.nextInt();
		System.out.println("Please enter store latency:");
		storeLatency = sc.nextInt();
		sc.close();
	}

	public boolean areWeDone() {
		for (int i = 0; i < addReservations.length; i++) {
			if ((int) addReservations[i].get("busy") == 1) {
				return false;
			}
		}
		for (int i = 0; i < mulReservations.length; i++) {
			if ((int) mulReservations[i].get("busy") == 1) {
				return false;
			}

		}
		for (int i = 0; i < loadBuffers.length; i++) {
			if ((int) loadBuffers[i].get("busy") == 1) {
				return false;
			}
		}
		for (int i = 0; i < storeBuffers.length; i++) {
			if ((int) storeBuffers[i].get("busy") == 1) {
				return false;
			}
		}
		return true;
	}

	public void printEverything() {

		System.out.println("==================Cycle " + cycles +"=================");
		System.out.println();
		System.out.println("==================Queue=================");
		for (int i = 0; i < queueStatus.length; i++) {

			System.out.print(program.get(i) + "     ||");
			System.out.print(queueStatus[i].get("issue") + "  ");
			System.out.print(queueStatus[i].get("start") + "  ");
			System.out.print(queueStatus[i].get("finish") + "  ");
			System.out.print(queueStatus[i].get("writeBack") + "  ");
			System.out.println();
		}

		//System.out.println("====================================================");
		System.out.println();

		System.out.println("==================ADD reservation stations=============");
		for (int i = 0; i < addReservations.length; i++) {
			System.out.print("A" + i + "  ");
			System.out.print(addReservations[i].get("busy") + "  ");
			System.out.print(((int) addReservations[i].get("op") == 0 ? "ADD" : "SUB") + "  ");
			System.out.print(addReservations[i].get("Vj") + "  ");
			System.out.print(addReservations[i].get("Vk") + "  ");
			System.out.print(addReservations[i].get("Qj") + "  ");
			System.out.print(addReservations[i].get("Qk") + "  ");
			System.out.print(addReservations[i].get("finishTime") + "  ");
			System.out.println();
		}
		//System.out.println("====================================================");
		System.out.println();

		System.out.println("==================MUL reservation stations=============");
		for (int i = 0; i < mulReservations.length; i++) {
			System.out.print("M" + i + "  ");
			System.out.print(mulReservations[i].get("busy") + "  ");
			System.out.print(((int) mulReservations[i].get("op") == 2 ? "MUL" : "DIV") + "  ");
			System.out.print(mulReservations[i].get("Vj") + "  ");
			System.out.print(mulReservations[i].get("Vk") + "  ");
			System.out.print(mulReservations[i].get("Qj") + "  ");
			System.out.print(mulReservations[i].get("Qk") + "  ");
			System.out.print(mulReservations[i].get("finishTime") + "  ");
			System.out.println();
		}
		//System.out.println("====================================================");
		System.out.println();

		System.out.println("==================Load Buffers=============");
		for (int i = 0; i < loadBuffers.length; i++) {
			System.out.print("L" + i + "  ");
			System.out.print(loadBuffers[i].get("busy") + "  ");
			System.out.print(loadBuffers[i].get("effectiveAddress") + "  ");
			System.out.print(loadBuffers[i].get("finishTime") + "  ");
			System.out.println();
		}
		//System.out.println("====================================================");
		System.out.println();

		System.out.println("==================Reg File=============");
		for (int i = 0; i < regFile.length; i++) {
			if ((double) regFile[i].get("V") != 0.0 || !(regFile[i].get("Q") + "").equals("0")) {
				System.out.print("F" + i + "  ");
				System.out.print(regFile[i].get("Q") + "  ");
				System.out.print(regFile[i].get("V") + "  ");
				System.out.println();
			}
		}
		//System.out.println("====================================================");
		System.out.println();

		System.out.println("==================Bus Values=============");

		System.out.print(bus.get("Tag") + "  ");
		System.out.print(bus.get("Value") + "  ");
		System.out.println();
		System.out.println("====================================================");
		System.out.println();
	}

	// UI
	// stuff===========================================================================================
	public Tomasulo() {
	}

	public Tomasulo(String s) {
		initializeStations();
		// setInitialState();
		// getLatencies();
		// parse("file3");
	}

	public boolean nextCycle() {
		bus.clear();
		if (!instructionQueue.isEmpty() || !areWeDone()) {

			HashMap peek = instructionQueue.peek();

			passToCanExecute();
			if (peek != null && canIssue((int) peek.get("op"))) {
				HashMap currInst = instructionQueue.remove();
				issue(currInst);
				// System.out.println("Issued " + currInst.get("op"));
			}
			// tomasulo.passToFinishedExecution();
			passToWriteOnBus();
			readBus();
			cycles++;
			System.out.println(cycles);
			printEverything();
			if (areWeDone()) {
				return false;
			} else {
				return true;
			}

		} else {
			return false;
		}
	}

	public void setInitialState() {

		memory[0] = 10.0;
		memory[1] = 3.0;
		HashMap h1 = new HashMap();
		h1.put("Q", "0");
		h1.put("V", 1.0);

		HashMap h2 = new HashMap();
		h2.put("Q", "0");
		h2.put("V", 2.0);
		HashMap h3 = new HashMap();
		h3.put("Q", "0");
		h3.put("V", 4.0);

		HashMap h4 = new HashMap();
		h4.put("Q", "0");
		h4.put("V", 6.0);

		HashMap h5 = new HashMap();
		h5.put("Q", "0");
		h5.put("V", 8.0);
		HashMap h6 = new HashMap();
		h6.put("Q", "0");
		h6.put("V", 9.0);

		regFile[1] = h1;
		regFile[2] = h2;
		regFile[4] = h3;
		regFile[6] = h4;
		regFile[8] = h5;
		regFile[9] = h6;
	}

	public void getLatencies(int add, int sub, int mul, int div, int load, int store) {
		addLatency = add;
		subLatency = sub;
		mulLatency = mul;
		divLatency = div;
		loadLatency = load;
		storeLatency = store;
	}

	public void parseTextArea(String programText) {

		String[] instructions = programText.split("\n");
		String row;

		for (int i = 0; i < instructions.length; i++) {
			row = instructions[i];
			instructionQueue.add(parseInstruction(row));
			program.add(row);
		}

		queueStatus = new HashMap[program.size()];
		for (int i = 0; i < program.size(); i++) {
			HashMap h = new HashMap();
			h.put("id", i);
			h.put("issue", -1);
			h.put("start", -1);
			h.put("finish", -1);
			h.put("writeBack", -1);
			h.put("done", 0);
			queueStatus[i] = h;
		}

	}

	// =====================================================================================================

	public static void main(String[] args) {
		Tomasulo tomasulo = new Tomasulo();
		tomasulo.initializeStations();

		tomasulo.memory[0] = 10.0;
		tomasulo.memory[1] = 3.0;
		HashMap h1 = new HashMap();
		h1.put("Q", "0");
		h1.put("V", 1.0);

		HashMap h2 = new HashMap();
		h2.put("Q", "0");
		h2.put("V", 2.0);
		HashMap h3 = new HashMap();
		h3.put("Q", "0");
		h3.put("V", 4.0);

		HashMap h4 = new HashMap();
		h4.put("Q", "0");
		h4.put("V", 6.0);

		HashMap h5 = new HashMap();
		h5.put("Q", "0");
		h5.put("V", 8.0);
		HashMap h6 = new HashMap();
		h6.put("Q", "0");
		h6.put("V", 9.0);

		tomasulo.regFile[1] = h1;
		tomasulo.regFile[2] = h2;
		tomasulo.regFile[4] = h3;
		tomasulo.regFile[6] = h4;
		tomasulo.regFile[8] = h5;
		tomasulo.regFile[9] = h6;

		
		tomasulo.getLatencies();
		tomasulo.parse("test");

		while (!tomasulo.instructionQueue.isEmpty() || !tomasulo.areWeDone()) {
			HashMap peek = tomasulo.instructionQueue.peek();

			tomasulo.passToCanExecute();
			if (peek != null && tomasulo.canIssue((int) peek.get("op"))) {
				HashMap currInst = tomasulo.instructionQueue.remove();
				tomasulo.issue(currInst);
				// System.out.println("Issued " + currInst.get("op"));
			}
			// tomasulo.passToFinishedExecution();
			tomasulo.passToWriteOnBus();
			tomasulo.readBus();
			//System.out.println(tomasulo.cycles);
			tomasulo.printEverything();
			tomasulo.cycles++;
			tomasulo.bus.clear();
		}
		System.out.println("Done! " + tomasulo.memory[2]);
	}
}

//STORES ARE GOING TO BE 'T' !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!