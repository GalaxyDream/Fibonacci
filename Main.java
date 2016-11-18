import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
public class Main {
	
	//map contains list of hashtags and the pointer to nodes in FibonacciHeap
	HashMap<String, Node> map = new HashMap<String, Node>();
	FHeap fib = new FHeap();
	BufferedOutputStream fw;
	boolean debug = false;
	boolean debug_output = false;
	
	Main(String input, String outputPath) throws FileNotFoundException{    //If there is no input file exist
		fw = new BufferedOutputStream(new FileOutputStream(outputPath));
		//initialize map
		initMap(input);		
	}
	
	public void output(int number) throws IOException{
		Queue<Node> q = new LinkedList<Node>();
		int num = number;
		while(num > 0){
			if(num != number)
				fw.write(",".getBytes());
			Node node = fib.removeMax();
			if(node != null){
				if(debug_output){
				System.out.println("Parent \n------");
				fib.print(node);
				System.out.println("------");
//				System.out.println("Child \n------");
//				fib.print(node.child);
				System.out.println("------");
				}
				fw.write(node.hashTag.getBytes());
			}
			else{
				fw.close();
				return;
			}
			node.degree = 0;
			node.childCut = false;
            node.parent = null;
            node.left_sibling = null;
            node.right_sibling = null;
            node.child = null;
			q.add(node);
			num--;
		}
		fw.write('\n');
		q.forEach(node -> fib.topLevelMerge(node));
	}
	
	public void updateMap(String s){
		String[] arr = s.split("\\s");
		String hashTag = arr[0].substring(1);
		int count = Integer.parseInt(arr[1]);
		if(map.containsKey(hashTag)){
			if(debug) System.out.println("Increase Node:" + hashTag +" by: " + count);
			fib.increaseKey(map.get(hashTag), count);
		}
		else{
			Node node = new Node(hashTag, count);
			map.put(hashTag, node);
			if(debug) System.out.println("Add Node: " + hashTag);
			fib.addNode(node);
		}
	}
	//Create Fibonacci Heap based on the input file
	public void initMap(String input){
		 try {
			Files.lines(Paths.get(input)).forEach(str -> {
				if(debug) System.out.println(str);
				if(str.charAt(0) =='S'){}     //if encounter STOP sign
				else
				{
				if(str.charAt(0) != '#'){
					int outputNum = Integer.parseInt(str);
					try {
						output(outputNum);
					} catch (Exception e) {
						if(debug) System.out.println("output() I/O");
						e.printStackTrace();
					}
				}
				else
					updateMap(str);}
			});
		} catch (IOException e) {
			if(debug) System.out.println("initMap() i/o error");
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) {
		try{
		Main ob = new Main(args[0],"ourput.txt");
		//ob.map.forEach((map,value) -> System.out.println(map + value.count));
		ob.fw.close();	
	}catch (IOException e) {
			System.out.println("initMap() i/o error");
			e.printStackTrace();
		}
	}

}
