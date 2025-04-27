package html;

import java.io.IOException;
import java.net.URL;

public interface HtmlDocumentFetcher {
    HtmlDocument fetch(URL url) throws IOException;
}
