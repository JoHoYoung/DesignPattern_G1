package org.jsoup.safety;

import org.jsoup.Jsoup;
import org.jsoup.MultiLocaleRule;
import org.jsoup.MultiLocaleRule.MultiLocaleTest;
import org.jsoup.TextUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 Tests for the cleaner.

 @author Jonathan Hedley, jonathan@hedley.net */
public class CleanerWhitelistTest {
    @Rule public MultiLocaleRule rule = new MultiLocaleRule();

    @Test public void simpleBehaviourTest() {
        String h = "<div><p class=foo><a href='http://evil.com'>Hello <b id=bar>there</b>!</a></div>";
        String cleanHtml = Jsoup.cleanWhitelist(h, Filter.simpleText());

        assertEquals("Hello <b>there</b>!", TextUtil.stripNewlines(cleanHtml));
    }
    
    @Test public void simpleBehaviourTest2() {
        String h = "Hello <b>there</b>!";
        String cleanHtml = Jsoup.cleanWhitelist(h, Filter.simpleText());

        assertEquals("Hello <b>there</b>!", TextUtil.stripNewlines(cleanHtml));
    }

    @Test public void basicBehaviourTest() {
        String h = "<div><p><a href='javascript:sendAllMoney()'>Dodgy</a> <A HREF='HTTP://nice.com'>Nice</a></p><blockquote>Hello</blockquote>";
        String cleanHtml = Jsoup.cleanWhitelist(h, Filter.basic());

        assertEquals("<p><a rel=\"nofollow\">Dodgy</a> <a href=\"http://nice.com\" rel=\"nofollow\">Nice</a></p><blockquote>Hello</blockquote>",
                TextUtil.stripNewlines(cleanHtml));
    }
    
    @Test public void basicWithImagesTest() {
        String h = "<div><p><img src='http://example.com/' alt=Image></p><p><img src='ftp://ftp.example.com'></p></div>";
        String cleanHtml = Jsoup.cleanWhitelist(h, Filter.basicWithImages());
        assertEquals("<p><img src=\"http://example.com/\" alt=\"Image\"></p><p><img></p>", TextUtil.stripNewlines(cleanHtml));
    }
    
    @Test public void testRelaxed() {
        String h = "<h1>Head</h1><table><tr><td>One<td>Two</td></tr></table>";
        String cleanHtml = Jsoup.cleanWhitelist(h, Filter.relaxed());
        assertEquals("<h1>Head</h1><table><tbody><tr><td>One</td><td>Two</td></tr></tbody></table>", TextUtil.stripNewlines(cleanHtml));
    }

    @Test public void testRemoveTags() {
        String h = "<div><p><A HREF='HTTP://nice.com'>Nice</a></p><blockquote>Hello</blockquote>";
        String cleanHtml = Jsoup.cleanWhitelist(h, Filter.basic().removeTags("a"));

        assertEquals("<p>Nice</p><blockquote>Hello</blockquote>", TextUtil.stripNewlines(cleanHtml));
    }

    @Test public void testRemoveAttributes() {
        String h = "<div><p>Nice</p><blockquote cite='http://example.com/quotations'>Hello</blockquote>";
        String cleanHtml = Jsoup.cleanWhitelist(h, Filter.basic().removeAttributes("blockquote", "cite"));

        assertEquals("<p>Nice</p><blockquote>Hello</blockquote>", TextUtil.stripNewlines(cleanHtml));
    }

    @Test public void testRemoveEnforcedAttributes() {
        String h = "<div><p><A HREF='HTTP://nice.com'>Nice</a></p><blockquote>Hello</blockquote>";
        String cleanHtml = Jsoup.cleanWhitelist(h, Filter.basic().removeEnforcedAttribute("a", "rel"));

        assertEquals("<p><a href=\"http://nice.com\">Nice</a></p><blockquote>Hello</blockquote>",
                TextUtil.stripNewlines(cleanHtml));
    }

    @Test public void testRemoveProtocols() {
        String h = "<p>Contact me <a href='mailto:info@example.com'>here</a></p>";
        String cleanHtml = Jsoup.cleanWhitelist(h, Filter.basic().removeProtocols("a", "href", "ftp", "mailto"));

        assertEquals("<p>Contact me <a rel=\"nofollow\">here</a></p>",
                TextUtil.stripNewlines(cleanHtml));
    }

    @Test @MultiLocaleTest public void whitelistedProtocolShouldBeRetained() {
        Filter filter = Filter.none()
                .addTags("a")
                .addAttributes("a", "href")
                .addProtocols("a", "href", "something");

        String cleanHtml = Jsoup.cleanWhitelist("<a href=\"SOMETHING://x\"></a>", filter);

        assertEquals("<a href=\"SOMETHING://x\"></a>", TextUtil.stripNewlines(cleanHtml));
    }

    @Test public void testDropComments() {
        String h = "<p>Hello<!-- no --></p>";
        String cleanHtml = Jsoup.cleanWhitelist(h, Filter.relaxed());
        assertEquals("<p>Hello</p>", cleanHtml);
    }
    
    @Test public void testDropXmlProc() {
        String h = "<?import namespace=\"xss\"><p>Hello</p>";
        String cleanHtml = Jsoup.cleanWhitelist(h, Filter.relaxed());
        assertEquals("<p>Hello</p>", cleanHtml);
    }
    
    @Test public void testDropScript() {
        String h = "<SCRIPT SRC=//ha.ckers.org/.j><SCRIPT>alert(/XSS/.source)</SCRIPT>";
        String cleanHtml = Jsoup.cleanWhitelist(h, Filter.relaxed());
        assertEquals("", cleanHtml);
    }
    
    @Test public void testDropImageScript() {
        String h = "<IMG SRC=\"javascript:alert('XSS')\">";
        String cleanHtml = Jsoup.cleanWhitelist(h, Filter.relaxed());
        assertEquals("<img>", cleanHtml);
    }
    
    @Test public void testCleanJavascriptHref() {
        String h = "<A HREF=\"javascript:document.location='http://www.google.com/'\">XSS</A>";
        String cleanHtml = Jsoup.cleanWhitelist(h, Filter.relaxed());
        assertEquals("<a>XSS</a>", cleanHtml);
    }

    @Test public void testCleanAnchorProtocol() {
        String validAnchor = "<a href=\"#valid\">Valid anchor</a>";
        String invalidAnchor = "<a href=\"#anchor with spaces\">Invalid anchor</a>";

        // A Filter that does not allow anchors will strip them out.
        String cleanHtml = Jsoup.cleanWhitelist(validAnchor, Filter.relaxed());
        assertEquals("<a>Valid anchor</a>", cleanHtml);

        cleanHtml = Jsoup.cleanWhitelist(invalidAnchor, Filter.relaxed());
        assertEquals("<a>Invalid anchor</a>", cleanHtml);

        // A Filter that allows them will keep them.
        Filter relaxedWithAnchor = Filter.relaxed().addProtocols("a", "href", "#");

        cleanHtml = Jsoup.cleanWhitelist(validAnchor, relaxedWithAnchor);
        assertEquals(validAnchor, cleanHtml);

        // An invalid anchor is never valid.
        cleanHtml = Jsoup.cleanWhitelist(invalidAnchor, relaxedWithAnchor);
        assertEquals("<a>Invalid anchor</a>", cleanHtml);
    }

    @Test public void testDropsUnknownTags() {
        String h = "<p><custom foo=true>Test</custom></p>";
        String cleanHtml = Jsoup.cleanWhitelist(h, Filter.relaxed());
        assertEquals("<p>Test</p>", cleanHtml);
    }
    
    @Test public void testHandlesEmptyAttributes() {
        String h = "<img alt=\"\" src= unknown=''>";
        String cleanHtml = Jsoup.cleanWhitelist(h, Filter.basicWithImages());
        assertEquals("<img alt=\"\">", cleanHtml);
    }

    @Test public void testIsValidBodyHtml() {
        String ok = "<p>Test <b><a href='http://example.com/' rel='nofollow'>OK</a></b></p>";
        String ok1 = "<p>Test <b><a href='http://example.com/'>OK</a></b></p>"; // missing enforced is OK because still needs run thru cleaner
        String nok1 = "<p><script></script>Not <b>OK</b></p>";
        String nok2 = "<p align=right>Test Not <b>OK</b></p>";
        String nok3 = "<!-- comment --><p>Not OK</p>"; // comments and the like will be cleaned
        String nok4 = "<html><head>Foo</head><body><b>OK</b></body></html>"; // not body html
        String nok5 = "<p>Test <b><a href='http://example.com/' rel='nofollowme'>OK</a></b></p>";
        String nok6 = "<p>Test <b><a href='http://example.com/'>OK</b></p>"; // missing close tag
        String nok7 = "</div>What";
        assertTrue(Jsoup.isValidWhitelist(ok, Filter.basic()));
        assertTrue(Jsoup.isValidWhitelist(ok1, Filter.basic()));
        assertFalse(Jsoup.isValidWhitelist(nok1, Filter.basic()));
        assertFalse(Jsoup.isValidWhitelist(nok2, Filter.basic()));
        assertFalse(Jsoup.isValidWhitelist(nok3, Filter.basic()));
        assertFalse(Jsoup.isValidWhitelist(nok4, Filter.basic()));
        assertFalse(Jsoup.isValidWhitelist(nok5, Filter.basic()));
        assertFalse(Jsoup.isValidWhitelist(nok6, Filter.basic()));
        assertFalse(Jsoup.isValidWhitelist(ok, Filter.none()));
        assertFalse(Jsoup.isValidWhitelist(nok7, Filter.basic()));
    }

    @Test public void testIsValidDocument() {
        String ok = "<html><head></head><body><p>Hello</p></body><html>";
        String nok = "<html><head><script>woops</script><title>Hello</title></head><body><p>Hello</p></body><html>";

        Filter relaxed = Filter.relaxed();
        Cleaner cleaner = new WhitelistCleaner(relaxed);
        Document okDoc = Jsoup.parse(ok);
        assertTrue(cleaner.isValid(okDoc));
        assertFalse(cleaner.isValid(Jsoup.parse(nok)));
        assertFalse(new WhitelistCleaner(Filter.none()).isValid(okDoc));
    }
    
    @Test public void resolvesRelativeLinks() {
        String html = "<a href='/foo'>Link</a><img src='/bar'>";
        String clean = Jsoup.cleanWhitelist(html, "http://example.com/", Filter.basicWithImages());
        assertEquals("<a href=\"http://example.com/foo\" rel=\"nofollow\">Link</a>\n<img src=\"http://example.com/bar\">", clean);
    }

    @Test public void preservesRelativeLinksIfConfigured() {
        String html = "<a href='/foo'>Link</a><img src='/bar'> <img src='javascript:alert()'>";
        String clean = Jsoup.cleanWhitelist(html, "http://example.com/", Filter.basicWithImages().preserveRelativeLinks(true));
        assertEquals("<a href=\"/foo\" rel=\"nofollow\">Link</a>\n<img src=\"/bar\"> \n<img>", clean);
    }
    
    @Test public void dropsUnresolvableRelativeLinks() {
        String html = "<a href='/foo'>Link</a>";
        String clean = Jsoup.cleanWhitelist(html, Filter.basic());
        assertEquals("<a rel=\"nofollow\">Link</a>", clean);
    }

    @Test public void handlesCustomProtocols() {
        String html = "<img src='cid:12345' /> <img src='data:gzzt' />";
        String dropped = Jsoup.cleanWhitelist(html, Filter.basicWithImages());
        assertEquals("<img> \n<img>", dropped);

        String preserved = Jsoup.cleanWhitelist(html, Filter.basicWithImages().addProtocols("img", "src", "cid", "data"));
        assertEquals("<img src=\"cid:12345\"> \n<img src=\"data:gzzt\">", preserved);
    }

    @Test public void handlesAllPseudoTag() {
        String html = "<p class='foo' src='bar'><a class='qux'>link</a></p>";
        Filter filter = new Filter()
                .addAttributes(":all", "class")
                .addAttributes("p", "style")
                .addTags("p", "a");

        String clean = Jsoup.cleanWhitelist(html, filter);
        assertEquals("<p class=\"foo\"><a class=\"qux\">link</a></p>", clean);
    }

    @Test public void addsTagOnAttributesIfNotSet() {
        String html = "<p class='foo' src='bar'>One</p>";
        Filter filter = new Filter()
            .addAttributes("p", "class");
        // ^^ filter does not have explicit tag add for p, inferred from add attributes.
        String clean = Jsoup.cleanWhitelist(html, filter);
        assertEquals("<p class=\"foo\">One</p>", clean);
    }

    @Test public void supplyOutputSettings() {
        // test that one can override the default document output settings
        Document.OutputSettings os = new Document.OutputSettings();
        os.prettyPrint(false);
        os.escapeMode(Entities.EscapeMode.extended);
        os.charset("ascii");

        String html = "<div><p>&bernou;</p></div>";
        String customOut = Jsoup.cleanWhitelist(html, "http://foo.com/", Filter.relaxed(), os);
        String defaultOut = Jsoup.cleanWhitelist(html, "http://foo.com/", Filter.relaxed());
        assertNotSame(defaultOut, customOut);

        assertEquals("<div><p>&Bscr;</p></div>", customOut); // entities now prefers shorted names if aliased
        assertEquals("<div>\n" +
            " <p>ℬ</p>\n" +
            "</div>", defaultOut);

        os.charset("ASCII");
        os.escapeMode(Entities.EscapeMode.base);
        String customOut2 = Jsoup.cleanWhitelist(html, "http://foo.com/", Filter.relaxed(), os);
        assertEquals("<div><p>&#x212c;</p></div>", customOut2);
    }

    @Test public void handlesFramesets() {
        String dirty = "<html><head><script></script><noscript></noscript></head><frameset><frame src=\"foo\" /><frame src=\"foo\" /></frameset></html>";
        String clean = Jsoup.cleanWhitelist(dirty, Filter.basic());
        assertEquals("", clean); // nothing good can come out of that

        Document dirtyDoc = Jsoup.parse(dirty);
        Document cleanDoc = new WhitelistCleaner(Filter.basic()).clean(dirtyDoc);
        assertNotNull(cleanDoc);
        assertEquals(0, cleanDoc.body().childNodeSize());
    }

    @Test public void cleansInternationalText() {
        assertEquals("привет", Jsoup.cleanWhitelist("привет", Filter.none()));
    }

    @Test
    public void testScriptTagInWhiteList() {
        Filter filter = Filter.relaxed();
        filter.addTags( "script" );
        assertTrue( Jsoup.isValidWhitelist("Hello<script>alert('Doh')</script>World !", filter) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void bailsIfRemovingProtocolThatsNotSet() {
        // a case that came up on the email list
        Filter w = Filter.none();

        // note no add tag, and removing protocol without adding first
        w.addAttributes("a", "href");
        w.removeProtocols("a", "href", "javascript"); // with no protocols enforced, this was a noop. Now validates.
    }

    @Test public void handlesControlCharactersAfterTagName() {
        String html = "<a/\06>";
        String clean = Jsoup.cleanWhitelist(html, Filter.basic());
        assertEquals("<a rel=\"nofollow\"></a>", clean);
    }

    @Test public void handlesAttributesWithNoValue() {
        // https://github.com/jhy/jsoup/issues/973
        String clean = Jsoup.cleanWhitelist("<a href>Clean</a>", Filter.basic());

        assertEquals("<a rel=\"nofollow\">Clean</a>", clean);
    }

    @Test public void handlesNoHrefAttribute() {
        String dirty = "<a>One</a> <a href>Two</a>";
        Filter relaxedWithAnchor = Filter.relaxed().addProtocols("a", "href", "#");
        String clean = Jsoup.cleanWhitelist(dirty, relaxedWithAnchor);
        assertEquals("<a>One</a> \n<a>Two</a>", clean);
    }
}
