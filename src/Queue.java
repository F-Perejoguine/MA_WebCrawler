import java.util.*;

public class Queue {

    private List<String> qLinks;

    public Queue(String[] seeds)
    {
        qLinks = new ArrayList();
        Collections.addAll(qLinks, seeds);
    }

    public String get(){
        String link = qLinks.get(0);
        int lSize = qLinks.size();

        for (int i = 0; i < lSize; i++)
        {
            if (i != lSize-1) qLinks.set(i, qLinks.get(i+1));
        }
        qLinks.remove(lSize - 1);

        return link;
    }

    public void add(String element)
    {
        qLinks.add(element);
    }

    public int size()
    {
        return qLinks.size();
    }

    public boolean checkDoubles(String checkURL)
    {
        boolean isDouble = false;
        for (String link : qLinks)
        {
            if(checkURL.equals(link)) isDouble = true;
        }
        return isDouble;
    }
}
