import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/* Heap Object */
public class FHeap {
    
    private Node max; /* Root node in the heap which has most 'count' value */
    boolean debug = false;
    
    FHeap() {
        max = null;
    }
    
    public void increaseKey(Node node, int value) { /* increaseKey operation of fibonacci heap. increase node's count value */
        if(node == null) {
            System.out.println("increaseKey called, but node is null");
            return;
        }
        Node parent = node.parent;
        //System.out.println("increaseKey called on " + node.hashTag + " Value = " + value);
        if(node.parent == null) { /* If node is a root node, increase the key directly, change max if required */
            node.count += value;
            if(node != max && node.count > max.count)
                max = node;
        }
        else {
            if((value != -1) && (node.count + value <= node.parent.count)) { /* If increased value is less than parent's value, directly increase, nothing else to do */
                node.count += value;
            }
            else {
                if(node.parent.degree == 1) { /* If parent has 1 child, set child to null since this child is to be cut from the tree */
                    node.parent.child = null;
                }
                else if(node.parent.degree > 1) {
                    /* Remove node from its sibling list */
                    node.left_sibling.right_sibling = node.right_sibling; 
                    node.right_sibling.left_sibling = node.left_sibling;
                    
                    /* Reset parent's child object if required */
                    if(node.parent.child == node)
                        node.parent.child = node.right_sibling;  //Set child node to the right sibling of the original one
                }
                else {
                    System.out.println("Inside increaseKey ... Parent's degree information error");
                    return;
                }
                node.parent.degree = node.parent.degree - 1; /* Parent's degree decreased by 1 */
                
                node.left_sibling = null;
                node.right_sibling = null;
                node.parent = null;
                node.childCut = false;
                
                /* -1 is a special value used by modifyChildCut to forcefully cut nodes from tree whose childCut is already true */
                if(value != -1)
                    node.count += value; /* We dont want to change values of nodes that are cut from the tree due to childChut = true */
                topLevelMerge(node); /* add Node as a root node to the heap */
                modifyChildCut(parent); /* Recursively check childCut values of parent nodes, and cut the parent nodes from the tree if required */
            }
        }
    }
    
    /* func modifyChildCut: check childCut value. If false, and its not a root node, set it to true. If true, and its not a root node,
        cut the node from the tree and modifyChildCut of its parent using increaseKey function */
    private void modifyChildCut(Node node) {
        if(node.childCut == false && node.parent != null) 
            node.childCut = true;
        else if(node.childCut == true && node.parent != null) {
            increaseKey(node, -1); /* This will force the node to be cut from the tree and recursively call modifyChildCut on its parent, but the count value of the node will not be changed */
        }
        
    }
    
    /* func removeMax: removeMax operation of Fibonacci Heap */
    public Node removeMax() {
        if(max == null) {
            System.out.println("removeMax called, but max is null.");
            return null;
        }
        Queue<Node> q = new LinkedList<Node>(); /* Queue to store all root nodes */
        Node max_node = max;
        //if(true) System.out.println("max: " + max.hashTag);
        Node child = max_node.child;
//        System.out.println("---------");
//        print(max);
//    	print(child);
//    	System.out.println("---------");
        /* Convert all child nodes of the max node to root nodes, and add to the queue */
        for(int i=0;i < max_node.degree;i++) {
            if(child == null) {
            	System.out.println(max_node.degree+"Inside removeMax ... Degree > 0 but Child is NULL ... Exiting ... "+i);
                System.exit(1);
            }
            
            if(debug) System.out.println("Max-child :" + child.hashTag);
            Node temp = child.right_sibling;
            child.childCut = false;
            child.parent = null;
            child.left_sibling = null;
            child.right_sibling = null;
            q.add(child);
            
            child = temp;
        }
        
        /* Add all the siblings of max node to the queue */
        Node temp = max.right_sibling;
        
        while((temp != null) && (temp != max)){
        	if(debug) System.out.println("Sib :" +temp.hashTag+temp.degree);
            q.add(temp);
            temp = temp.right_sibling;
        }
        
        max = null;
        //q.forEach(node -> System.out.print(node.hashTag + ", "));
        //System.out.println("");
        combineAndMerge(q); /* Pair wise combine all the root nodes in the queue */
        return max_node;
    }
    
    /* func combineAndMerge: Pairwise combine all root nodes in the queue */
    private void combineAndMerge(Queue<Node> q) {
        HashMap<Integer, Node> map = new HashMap<Integer, Node>();
        int maxDegree = 0; 
        while(!q.isEmpty()) {
            Node node1 = q.poll(); /* Get nodes from the queue */
            if(node1 == null){
                System.out.println("Error in combineAndMerge ... Node 1 is NULL");
            }
            else {
                while(map.containsKey(node1.degree)) { /* If hashmap already contains a root node of same degree, combine the 2 nodes */
                    Node node2 = map.get(node1.degree);
                    map.remove(node1.degree);
                    if(node1.count >= node2.count) {
                        node1.addChild(node2);
                    }
                    else {
                        node2.addChild(node1);
                        node1 = node2;
                    }
                }
                map.put(node1.degree, node1); /* Put the rootnode of the combined tree to the hashmap */
                if(node1.degree > maxDegree)
                    maxDegree = node1.degree;
            }
        }
        //System.out.println(map);
        for(int i=0;i<=maxDegree;i++) {
            if(map.containsKey(i)) {
            	topLevelMerge(map.get(i)); /* add root node to the heap */
                map.remove(i);
            }
        }
    }
    
    /* func addNode: add a node to the heap */
    public Node addNode(Node node) {
        this.topLevelMerge(node); /* add this root node to the heap */        
        return node;
    }
    
    /* func topLevelMerge: add root node to the heap and change max if required */
    public void topLevelMerge(Node node) {
    	if((node.degree > 0) && (node.child == null)){
        	System.out.println("topLevelMerge() - incorrect node");
        	System.exit(2);
        }
    	if(max == null) { /* No nodes in the heap */
            max = node;
            max.right_sibling = max;
            max.left_sibling = max;
            if((max.degree > 0) && (max.child == null)){
            	System.out.println("topLevelMerge() - incorrect node");
            	System.exit(2);
            }
        }
        else { /* Add node to the sibling list of max */
        node.left_sibling = max.left_sibling;
        node.right_sibling = max;
        max.left_sibling.right_sibling = node;
        max.left_sibling = node;
        if(node.count > max.count)
            max = node; /* Modify max */
        }
    }
    
    public void print(Node node){
    	if(node == null){
    		System.out.println("Node is null");
    		return;
    	}
    	Node head = node, sib = head.right_sibling, tmp = null;
    	int numSib = 0;
    	System.out.println("Node: " + head.hashTag + " deg: "+ head.degree + " count: "+ head.count+ " child: " + head.child);
    	while((sib != head) && (tmp != sib)){
    		numSib++;
    		System.out.println("Sib#" + numSib +" : " + sib.hashTag + " deg: "+ sib.degree + " count: "+ sib.count + " child: " + sib.child);
    		tmp = sib.right_sibling;
    		sib = tmp;
    	}
    	/*System.out.println("Child---");
    	print(node.child);*/
    }
}
