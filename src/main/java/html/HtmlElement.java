package html;

public interface HtmlElement {
    String getTagName();

    String getText();

    String getAttribute(String attributeName);
}
