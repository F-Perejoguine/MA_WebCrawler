import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Website {

    private Document website;
    private List<String> textcontent;
    private String[] k_topical;
    private String[] k_abstract;
    private String[] k_specific;

    public Website(Document doc, String[] ktopical, String[] kabstract, String[] kspecific) {
        website = doc;
        k_topical = ktopical;
        k_abstract = kabstract;
        k_specific = kspecific;
        textcontent = new ArrayList();

        String bodyText = getElements(website.getElementsByTag("body").first());

        if (hasContent(bodyText)) {
            textcontent.add(bodyText);
        }
    }

    //Generates a sort of "rating" for the website based on how well it matches to the user-defined input.
    public double getRating() {

        String token_delimiter = "(\\s|[^a-zA-Z_0-9_äöü])+";
        double rating = 0.0;
        for(String element : textcontent) {

            String[] tokens = element.split(token_delimiter);
            int t_matches = 0;
            int a_matches = 0;
            int s_matches = 0;

            for (String token : tokens) {

                for (String keyword : k_topical) {
                    if (match(token, keyword)) t_matches++;
                }
                for (String keyword : k_abstract) {
                    if (match(token, keyword)) a_matches++;
                }
                for (String keyword : k_specific) {
                    if (match(token, keyword)) s_matches++;
                }
            }

            double topicalrating = (double)t_matches * tokens.length;
            rating = rating + topicalrating;
        }

        return rating;
    }

    public String[] getTextContent() {
        return textcontent.toArray(new String[textcontent.size()]);
    }

    private String getElements(Element e) {

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
                    textcontent.add(childText);
                }
            }
        }

        return ownText;
    }

    private boolean hasContent(String input) //Checks the input string for alphanumeric content
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


    private static boolean match(String keyword, String subject)
    {
        int matches = 0;
        char[] chars1 = keyword.toLowerCase().toCharArray();
        char[] chars2 = subject.toLowerCase().toCharArray();

        for (int i = 0; i < Math.min(chars1.length, chars2.length); i++)
        {
            if (chars1[i] == chars2[i])
            {
                matches++;
            }
            else
            {
                break;
            }
        }

        if (chars1.length <= 3 && chars2.length == chars1.length && matches == 3)
        {
            return true;
        }
        else if(chars1.length == 4 && matches == 4)
        {
            return true;
        }
        else if(chars1.length > 4 && matches >= Math.round((chars1.length - 4) / 2.0) + 3)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}