package com.flatironschool.javacs;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;


/**
 * Represents the results of a search query.
 *
 */
public class WikiSearch {
	
	// map from URLs that contain the term(s) to relevance score
	private Map<String, Integer> map;

	/**
	 * Constructor.
	 * 
	 * @param map
	 */
	public WikiSearch(Map<String, Integer> map) {
		this.map = map;
	}
	
	/**
	 * Looks up the relevance of a given URL.
	 * 
	 * @param url
	 * @return
	 */
	public Integer getRelevance(String url) {
		Integer relevance = map.get(url);
		return relevance==null ? 0: relevance;
	}
	
	/**
	 * Prints the contents in order of term frequency.
	 * 
	 * @param map
	 */
	private  void print() {
		List<Entry<String, Integer>> entries = sort();
		for (Entry<String, Integer> entry: entries) {
			System.out.println(entry);
		}
	}
	
	/**
	 * Computes the union of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch or(WikiSearch that) {
        // FILL THIS IN!
		//return null;

		Map<String, Integer> combineSet = new HashMap<String, Integer>();
        Set<String> keyS = map.keySet();

        for(String val: keyS)
        {
        	combineSet.put(val, totalRelevance(getRelevance(val), that.getRelevance(val)));
        }

        keyS = that.map.keySet();


        for(String val: keyS)
        {
        	if(!combineSet.containsKey(val))
        		combineSet.put(val, that.getRelevance(val));
        }

        return new WikiSearch(combineSet);


	}
	
	/**
	 * Computes the intersection of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch and(WikiSearch that) {
        // FILL THIS IN!
		//return null;

		Map<String, Integer> common = new HashMap<String, Integer>();
        Set<String> keyS = map.keySet();

        for(String val: keyS)
        {
        	Boolean bool = that.map.containsKey(val);
        	if(bool)
        		common.put(val, totalRelevance(getRelevance(val), that.getRelevance(val)));
        }

        return new WikiSearch(common);

	}
	
	/**
	 * Computes the intersection of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch minus(WikiSearch that) {
        // FILL THIS IN!
		//return null;

		Map<String, Integer> common = new HashMap<String, Integer>();
        Set<String> keyS = map.keySet();
        
        for(String val: keyS)
        {
        	Boolean bool = that.map.containsKey(val);
        	if(!(bool))
        		common.put(val, totalRelevance(getRelevance(val), that.getRelevance(val)));
        }

        return new WikiSearch(common);

	}
	
	/**
	 * Computes the relevance of a search with multiple terms.
	 * 
	 * @param rel1: relevance score for the first search
	 * @param rel2: relevance score for the second search
	 * @return
	 */
	protected int totalRelevance(Integer rel1, Integer rel2) {
		// simple starting place: relevance is the sum of the term frequencies.
		return rel1 + rel2;
	}

	/**
	 * Sort the results by relevance.
	 * 
	 * @return List of entries with URL and relevance.
	 */
	public List<Entry<String, Integer>> sort() {
        // FILL THIS IN!
		//return null;

		List<Entry<String, Integer>> li = new ArrayList<Entry<String, Integer>>();
		
		for(Entry<String, Integer> en : map.entrySet())
		{
			li.add(en);
		}

		Comparator<Entry<String, Integer>> compare = new Comparator<Entry<String, Integer>>() {
	            @Override
	            public int compare(Entry<String, Integer> val1, Entry<String, Integer> val2)
	            {
	            	if (val1.getValue() < val2.getValue())
	            	{
	                    return -1;
	                }
	                if (val1.getValue() > val2.getValue())
	                {
	                    return 1;
	                }
	                return 0;
	            }
	        };

	        Collections.sort(li, compare);
        return li;

	}

	/**
	 * Performs a search and makes a WikiSearch object.
	 * 
	 * @param term
	 * @param index
	 * @return
	 */
	public static WikiSearch search(String term, JedisIndex index) {
		Map<String, Integer> map = index.getCounts(term);
		return new WikiSearch(map);
	}

	public static void main(String[] args) throws IOException {
		
		// make a JedisIndex
		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis); 
		
		// search for the first term
		String term1 = "java";
		System.out.println("Query: " + term1);
		WikiSearch search1 = search(term1, index);
		search1.print();
		
		// search for the second term
		String term2 = "programming";
		System.out.println("Query: " + term2);
		WikiSearch search2 = search(term2, index);
		search2.print();
		
		// compute the intersection of the searches
		System.out.println("Query: " + term1 + " AND " + term2);
		WikiSearch intersection = search1.and(search2);
		intersection.print();
	}
}
