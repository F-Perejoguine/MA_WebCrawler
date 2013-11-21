import java.util.*;
import java.util.logging.Level;

public class Queue {

    private PriorityQueue<Link> pqLinks;
    private List<Link> seedLinks;

    public Queue(String[] seeds)
    {
        seedLinks = new ArrayList<Link>();
        for (String link : seeds) {
            seedLinks.add(new Link(link, 0.0));
        }
        LinkSort ls = new LinkSort();
        pqLinks = new PriorityQueue<Link>(1, ls);
    }

    public Link get(){

        Link rlink;

        if(seedLinks.isEmpty()) {
            rlink = pqLinks.poll();
        } else {
            rlink = seedLinks.get(0);
            seedLinks.remove(0);
        }

        return rlink;
    }

    public int calculateMaxSize() {
        final double FACTOR_ERRORRANGE = 2.0;
        double flinksize = (double)Config.flinks.size();
        double totalcrawled = (double)Config.core.getCollectionTotal();
        double crawlnumber = (double)Config.getCrawlNumber();
        int result = (int)(Config.FACTOR_RESERVE * crawlnumber);

        if (totalcrawled != 0) {
            result = (int)Math.round(((FACTOR_ERRORRANGE * flinksize + totalcrawled) / totalcrawled) * (crawlnumber - totalcrawled));
        }
        return result;
    }

    public void add(Link element) {
        Config.core.calculatePriority(element);

        if(size() < calculateMaxSize()) {
            pqLinks.offer(element);
        } else {
            Link leastpriorityelement = element;
            for (Link li : pqLinks) {
                if(li.rating < leastpriorityelement.rating) leastpriorityelement = li;
            }
            if(element.rating > leastpriorityelement.rating){
                pqLinks.remove(leastpriorityelement);
                pqLinks.offer(element);
            }
        }
    }

    public int size() {
        return pqLinks.size() + seedLinks.size();
    }

    public boolean checkQueue(Link li)
    {
        boolean isDouble = false;

        if(seedLinks.size() != 0) {
            for(Link link : seedLinks) {
                if(li.url.equals(link.url)) {  //If element with same URL as link found in queue, check for insertion
                    isDouble = true;
                    if (!link.checkDatapoints(li.getRef(0))) link.addRef(li.getRef(0), "CheckQueue 1st");
                }
            }
        }

        for (Link link : pqLinks) {
            if(li.url.equals(link.url)) {
                isDouble = true;
                if(!link.checkDatapoints(li.getRef(0))) link.addRef(li.getRef(0), "CheckQueue 2nd");
            }
        }

        return isDouble;
    }
}
