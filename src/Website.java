import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Website {

    private Document website;
    private String[] predicate;
    private List<String> content;

    public Website(Document doc, String[] input) {
        website = doc;
        predicate = input;
        content = new ArrayList();

        String bodyText = getElements(website.getElementsByTag("body").first());

        if (hasContent(bodyText)) {
            content.add(bodyText);
        }

        for(String entry : content) {
            System.out.println("FOUND: " + entry);
        }

    }

    private String getElements(Element e)
    {

        String ownText = e.ownText();
        String[] ignoreTags = {"p", "br", "a", "b", "i", "u", "em", "font", "q", "big", "small", "strong", "sub", "sup"};

        for (Element e_child : e.children()) {
            String childText = getElements(e_child);

            if (hasContent(childText)) {
                boolean noTagMatch = true;

                for(String tag : ignoreTags) {

                    if (e_child.tagName() == tag) {
                        ownText = ownText + " " + childText;
                        noTagMatch = false;
                    }
                }

                if(noTagMatch) {
                    content.add(childText);
                }
            }
        }

        return ownText;
    }

    private boolean hasContent(String input)
    {
        //The boolean variable that the function will output later
        boolean hascontent = false;

        //Set of characters that the input gets tested against
        char[] characters={'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        //Iterate through the input string and standard character set. break and return true if match is found.
        for (int i = 0; i < input.length(); i++)
        {
            for (char c : characters)
            {
                if(input.toLowerCase().charAt(i) == c)
                {
                    hascontent = true;
                    break;
                }
            }
            if(hascontent) break;
        }

        return hascontent;
    }
}
