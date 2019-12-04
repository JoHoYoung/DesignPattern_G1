package org.jsoup.select;

import org.jsoup.nodes.Node;

public interface MasterTraverse {
	public void traverse(Node root);
	public static void traverse(NodeVisitor visitor, Node root) {}
}
