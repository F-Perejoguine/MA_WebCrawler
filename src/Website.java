import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Website {

    private Document website;
    private List<String> textcontent;
    private String[] k_topical;
    private String[] k_abstract;
    private String[] k_specific;
    private double rating;

    public Website(Document doc, String sURL) {
        website = doc;
        k_topical = Config.getTopical();
        k_abstract = Config.getAbstract();
        k_specific = Config.getSpecific();
        textcontent = new ArrayList<String>();

        try {
            String bodyText = getElements(website.getElementsByTag("body").first());

            if (hasContent(bodyText)) {
                textcontent.add(bodyText);
            }
            calculateRating();

        } catch (Exception e) {
            System.out.println(e.getMessage() + " while parsing " + sURL);
            rating = 0.0;
        }
    }

    //Generates a sort of "rating" for the website based on how well it matches to the user-defined input.
    public void calculateRating() {

        String token_delimiter = "(\\s|[^a-zA-Z_0-9_äöü])+";
        double srating = 0.0;
        final double PAR_U = 0.012;
        final double PAR_V = 0.09;
        final double PAR_A = 80.0;
        final double PAR_B = 10.0;
        int KTcount = k_topical.length;

        for(String element : textcontent) {
            int[] matches = new int[KTcount];

            String[] text_tokens = element.split(token_delimiter);

            for (String token : text_tokens) {
                for (int i = 0; i < KTcount; i++) {
                    if (match(token, k_topical[i])) matches[i]++;
                }
            }

            double element_trating = 0.0;
            for(int KTmatchcount : matches) {
                double x = (double)KTmatchcount/(double)text_tokens.length + PAR_U;
                double topicalrating = (double) text_tokens.length * (1.0 - (1.0/(PAR_A * x)) + x/PAR_B - PAR_V) / (double)KTcount;
                element_trating = element_trating + ((topicalrating > 0) ? topicalrating : 0);
            }
            srating = srating + element_trating;
        }

        rating = srating;
    }

    public void parseLinks() {

        Elements links = website.select("a[href]");

        for (Element link : links)
        {
            boolean notfailed = true;
            boolean notcrawled = false;
            Link foundlink = new Link(link.attr("abs:href"), 0.0);

            for (String element : Config.flinks)
                if(element.equals(foundlink.url)) notfailed = false;

            if(notfailed) {
                foundlink.addRef(new Dataset(rating, 0));
                notcrawled = !Config.core.checkCollection(foundlink);
            }

            if(notcrawled) {
                if(!Config.lQueue.checkQueue(foundlink)) Config.lQueue.add(foundlink);
            }
        }
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

                    if (e_child.tagName().equals(tag)) {
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
        for (int i = 0; i < input.length(); i++) {
            for (char c : characters) {
                if(input.toLowerCase().charAt(i) == c) {
                    hascontent = true;
                    break;
                }
            }
            if(hascontent) break;
        }

        return hascontent;
    }


    private static boolean match(String keyword, String subject) {
        int matches = 0;
        char[] chars1 = keyword.toLowerCase().toCharArray();
        char[] chars2 = subject.toLowerCase().toCharArray();

        for (int i = 0; i < Math.min(chars1.length, chars2.length); i++) {
            if (chars1[i] == chars2[i]) {
                matches++;
            } else {
                break;
            }
        }

        if (chars1.length <= 3 && chars2.length == chars1.length && matches == 3) {
            return true;
        }
        else if(chars1.length == 4 && matches == 4) {
            return true;
        }
        else return chars1.length > 4 && matches >= Math.round((chars1.length - 4) / 2.0) + 3;
    }

    public double getRating() {
        return rating;
    }
}