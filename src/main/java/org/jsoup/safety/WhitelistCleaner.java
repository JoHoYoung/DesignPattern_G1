package org.jsoup.safety;

import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public class WhitelistCleaner extends Cleaner {
    public WhitelistCleaner(Filter filter) {
        super(filter);
    }

    private final class CleaningVisitor implements NodeVisitor {
        private int numDiscarded = 0;
        private final Element root;
        private Element destination; // current element to append nodes to

        private CleaningVisitor(Element root, Element destination) {
            this.root = root;
            this.destination = destination;
        }

        public void head(Node source, int depth) {
            if (source instanceof Element) {
                Element sourceEl = (Element) source;

                if (filter.isSafeTag(sourceEl.tagName())) { // safe, clone and copy safe attrs
                    ElementMeta meta = createSafeElement(sourceEl);
                    Element destChild = meta.el;
                    destination.appendChild(destChild);

                    numDiscarded += meta.numAttribsDiscarded;
                    destination = destChild;
                } else if (source != root) { // not a safe tag, so don't add. don't count root against discarded.
                    numDiscarded++;
                }
            } else if (source instanceof TextNode) {
                TextNode sourceText = (TextNode) source;
                TextNode destText = new TextNode(sourceText.getWholeText());
                destination.appendChild(destText);
            } else if (source instanceof DataNode && filter.isSafeTag(source.parent().nodeName())) {
                DataNode sourceData = (DataNode) source;
                DataNode destData = new DataNode(sourceData.getWholeData());
                destination.appendChild(destData);
            } else { // else, we don't care about comments, xml proc instructions, etc
                numDiscarded++;
            }
        }

        public void tail(Node source, int depth) {
            if (source instanceof Element && filter.isSafeTag(source.nodeName())) {
                destination = destination.parent(); // would have descended, so pop destination stack
            }
        }
    }

    int copySafeNodes(Element source, Element dest) {
        CleaningVisitor cleaningVisitor = new CleaningVisitor(source, dest);
        NodeTraversor.traverse(cleaningVisitor, source);
        return cleaningVisitor.numDiscarded;
    }
}
