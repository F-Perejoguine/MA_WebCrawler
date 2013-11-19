import java.util.*;

public class Queue {

    private PriorityQueue<Link> pqLinks;
    private List<Link> seedLinks;

    public Queue(String[] seeds)
    {
        seedLinks = new ArrayList();
        for (String link : seeds) {
            seedLinks.add(new Link(link, 0.0));
        }
        LinkSort ls = new LinkSort();
        pqLinks = new PriorityQueue<Link>(1, ls);
    }

    public Link get(){

        Link rlink = null;

        if(seedLinks.isEmpty()) {
            rlink = pqLinks.poll();
        } else {
            for(int i = 0; i < seedLinks.size(); i++) {
                rlink = seedLinks.get(i);
                seedLinks.remove(i);
                break;
            }
        }

        return rlink;
    }

    public void add(Link element)
    {
        pqLinks.offer(element);
    }

    public int size()
    {
        return pqLinks.size();
    }

    public boolean checkDoubles(String checkURL)
    {
        boolean isDouble = false;
        if(seedLinks.size() != 0) {
            for(Link link : seedLinks) {
                if(checkURL.equals(link.url)) isDouble = true;
            }
        }
        for (Link link : pqLinks)
        {
            if(checkURL.equals(link.url)) isDouble = true;
        }
        return isDouble;

    }
}
