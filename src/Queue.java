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
        if(size() < Config.getCrawlNumber() * Config.FACTOR_RESERVE)
        Config.core.calculatePriority(element);
        pqLinks.offer(element);
    }

    public int size()
    {
        return pqLinks.size() + seedLinks.size();
    }

    public boolean checkQueue(Link li)
    {
        boolean isDouble = false;
        if(seedLinks.size() != 0) {
            for(Link link : seedLinks) {
                if(li.url.equals(link.url)) {
                    isDouble = true;
                    link.addRef(li.getRef(0));
                }
            }
        }

        for (Link link : pqLinks) {
            if(li.url.equals(link.url)) isDouble = true;
            link.addRef(li.getRef(0));
        }

        return isDouble;
    }
}
