package com.jingtuo.android.nos;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * Basic XML Writer helper library. It does not do full XML Well-Formedness
 * checks, but provides basic element and attribute value escaping, and tracks
 * open tags to simplify use.
 * <p>
 * Sampe usage:
 * <pre>
 * <code>
 * XmlWriter w = new XmlWriter();
 * w.start(DOC_TAG, "xmlns", DOC_NAMESPACE);
 * if (foo != null)
 *   w.start(FOO_TAG).value(foo).end();
 * w.end();
 * </code>
 * </pre>
 */
public class XmlWriter {
    List<String> tags = new ArrayList<String>();
    StringBuilder sb = new StringBuilder();

    public XmlWriter start(String name) {
        sb.append("<").append(name).append(">");
        tags.add(name);
        return this;
    }

    public XmlWriter start(String name, String attr, String value) {
        sb.append("<").append(name);
        writeAttr(attr, value);
        sb.append(">");
        tags.add(name);
        return this;
    }
    
    public XmlWriter start(String name, String[] attrs, String[] values) {
        sb.append("<").append(name);
        for (int i = 0; i < Math.min(attrs.length, values.length); i++) {
            writeAttr(attrs[i], values[i]);
        }
        sb.append(">");
        tags.add(name);
        return this;
    }

    public XmlWriter end() {
        assert(tags.size() > 0);
        String name = tags.remove(tags.size() - 1);
        sb.append("</").append(name).append(">");
        return this;
    }

    public byte[] getBytes() {
        assert(tags.size() == 0);
        return this.toString().getBytes(StandardCharsets.UTF_8);
    }
    
    public String toString() {
        return sb.toString();
    }

    public XmlWriter value(String value) {
        appendEscapedString(value, sb);
        return this;
    }

    private void writeAttr(String name, String value) {
        sb.append(' ').append(name).append("=\"");
        appendEscapedString(value, sb);
        sb.append("\"");
    }

    /**
     * Appends the specified string (with any non-XML-compatible characters
     * replaced with the corresponding escape code) to the specified
     * StringBuilder.
     * 
     * @param s
     *            The string to escape and append to the specified
     *            StringBuilder.
     * @param builder
     *            The StringBuilder to which the escaped string should be
     *            appened.
     */
    private void appendEscapedString(String s, StringBuilder builder) {
        int pos;
        int start = 0;
        int len = s.length();
        for (pos = 0; pos < len; pos++) {
            char ch = s.charAt(pos);
            String escape;
            switch (ch) {
            case '\t':
                escape = "&#9;";
                break;
            case '\n':
                escape = "&#10;";
                break;
            case '\r':
                escape = "&#13;";
                break;
            case '&':
                escape = "&amp;";
                break;
            case '"':
                escape = "&quot;";
                break;
            case '<':
                escape = "&lt;";
                break;
            case '>':
                escape = "&gt;";
                break;
            default:
                escape = null;
                break;
            }
            
            // If we found an escape character, write all the characters up to that
            // character, then write the escaped char and get back to scanning
            if (escape != null) {
                if (start < pos)
                    builder.append(s, start, pos);
                sb.append(escape);
                start = pos + 1;
            }
        }
        
        // Write anything that's left 
        if (start < pos) sb.append(s, start, pos);
    }

}
