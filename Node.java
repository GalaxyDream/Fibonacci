import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/* Fibonacci Node object */
class Node {
    int degree; /* degree of the node */
    Node child; /* child node object */
    int count; /* data of the node */
    String hashTag; /* index of the hashTag, this node corresponds to */
    
    Node left_sibling; /* left sibling node */
    Node right_sibling; /* right sibling node */
    
    Node parent; /* parent node */
    Boolean childCut; /* childCut value, default = false */
    
    
    public Node(String tag, int data) {
        degree = 0;
        child = null;
        count = data;
        hashTag = tag;
        left_sibling = null;
        right_sibling = null;
        parent = null;
        childCut = false;
        
    }
    
    void addChild(Node node2) { /* add child node 'node2' to this node. Used in removeMax, where one node can be a child of another node */
        if(this.degree == 0) {
            this.child = node2;
            node2.right_sibling = node2;
            node2.left_sibling = node2;
        }
        else {
            Node child1 = this.child;
            node2.left_sibling = child1.left_sibling;
            node2.right_sibling = child1;
            child1.left_sibling.right_sibling = node2;
            child1.left_sibling = node2;
            
        }
        this.degree = this.degree + 1;
        node2.parent = this;
        node2.childCut = false; /* Set childCut to false */
        
    }
}

