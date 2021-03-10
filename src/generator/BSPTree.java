/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author 2014130020
 */
public class BSPTree {

    final DefaultMutableTreeNode root;

    public BSPTree(DefaultMutableTreeNode root) {
        this.root = root;
    }

    void addBranch(DefaultMutableTreeNode parent, DefaultMutableTreeNode left, DefaultMutableTreeNode right) {
        parent.add(left);
        parent.add(right);
    }

    List<DefaultMutableTreeNode> leaves() {

        List<DefaultMutableTreeNode> nodes = new ArrayList<>();

        DefaultMutableTreeNode next = root.getFirstLeaf();
        while (next != null) {
            nodes.add(next);
            next = next.getNextLeaf();
        }

        return nodes;
    }

    List<DefaultMutableTreeNode> reverseBFO() {

        List<DefaultMutableTreeNode> nodes = new ArrayList<>();

        // iterate over breadth first order
        Enumeration<TreeNode> en = root.breadthFirstEnumeration();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            nodes.add(node);
        }

        // reverse list breadth first order
        Collections.reverse(nodes);

        return nodes;
    }

    DefaultMutableTreeNode getRoot() {
        return root;
    }
}
