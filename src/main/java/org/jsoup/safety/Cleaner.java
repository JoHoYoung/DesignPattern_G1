package org.jsoup.safety;

import org.jsoup.helper.Validate;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.ParseErrorList;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.util.List;


/**
 The whitelist based HTML cleaner. Use to ensure that end-user provided HTML contains only the elements and attributes
 that you are expecting; no junk, and no cross-site scripting attacks!
 <p>
 The HTML cleaner parses the input as HTML and then runs it through a white-list, so the output HTML can only contain
 HTML that is allowed by the whitelist.
 </p>
 <p>
 It is assumed that the input HTML is a body fragment; the clean methods only pull from the source's body, and the
 canned white-lists only allow body contained tags.
 </p>
 <p>
 Rather than interacting directly with a Cleaner object, generally see the {@code clean} methods in {@link org.jsoup.Jsoup}.
 </p>
 */
public abstract class Cleaner {
    protected Filter filter;

    /**
     Create a new cleaner, that sanitizes documents using the supplied whitelist.
     @param filter white-list to clean with
     */
    public Cleaner(Filter filter) {
        Validate.notNull(filter);
        this.filter = filter;
    }

    /**
     Creates a new, clean document, from the original dirty document, containing only elements allowed by the whitelist.
     The original document is not modified. Only elements from the dirt document's <code>body</code> are used.
     @param dirtyDocument Untrusted base document to clean.
     @return cleaned document.
     */
    public Document clean(Document dirtyDocument) {
        Validate.notNull(dirtyDocument);

        Document clean = Document.createShell(dirtyDocument.baseUri());
        if (dirtyDocument.body() != null) // frameset documents won't have a body. the clean doc will have empty body.
            copySafeNodes(dirtyDocument.body(), clean.body());

        return clean;
    }

    /**
     Determines if the input document <b>body</b>is valid, against the whitelist. It is considered valid if all the tags and attributes
     in the input HTML are allowed by the whitelist, and that there is no content in the <code>head</code>.
     <p>
     This method can be used as a validator for user input. An invalid document will still be cleaned successfully
     using the {@link #clean(Document)} document. If using as a validator, it is recommended to still clean the document
     to ensure enforced attributes are set correctly, and that the output is tidied.
     </p>
     @param dirtyDocument document to test
     @return true if no tags or attributes need to be removed; false if they do
     */
    public boolean isValid(Document dirtyDocument) {
        Validate.notNull(dirtyDocument);

        Document clean = Document.createShell(dirtyDocument.baseUri());
        int numDiscarded = copySafeNodes(dirtyDocument.body(), clean.body());
        return numDiscarded == 0
            && dirtyDocument.head().childNodes().isEmpty(); // because we only look at the body, but we start from a shell, make sure there's nothing in the head
    }

    public boolean isValidBodyHtml(String bodyHtml) {
        Document clean = Document.createShell("");
        Document dirty = Document.createShell("");
        ParseErrorList errorList = ParseErrorList.tracking(1);
        List<Node> nodes = Parser.parseFragment(bodyHtml, dirty.body(), "", errorList);
        dirty.body().insertChildren(0, nodes);
        int numDiscarded = copySafeNodes(dirty.body(), clean.body());
        return numDiscarded == 0 && errorList.isEmpty();
    }

    abstract int copySafeNodes(Element source, Element dest);

    protected ElementMeta createSafeElement(Element sourceEl) {
        String sourceTag = sourceEl.tagName();
        Attributes destAttrs = new Attributes();
        Element dest = new Element(Tag.valueOf(sourceTag), sourceEl.baseUri(), destAttrs);
        int numDiscarded = 0;

        Attributes sourceAttrs = sourceEl.attributes();
        for (Attribute sourceAttr : sourceAttrs) {
            if (filter.isSafeAttribute(sourceTag, sourceEl, sourceAttr))
                destAttrs.put(sourceAttr);
            else
                numDiscarded++;
        }
        Attributes enforcedAttrs = filter.getEnforcedAttributes(sourceTag);
        destAttrs.addAll(enforcedAttrs);

        return new ElementMeta(dest, numDiscarded);
    }

    protected static class ElementMeta {
        Element el;
        int numAttribsDiscarded;

        ElementMeta(Element el, int numAttribsDiscarded) {
            this.el = el;
            this.numAttribsDiscarded = numAttribsDiscarded;
        }
    }

}
