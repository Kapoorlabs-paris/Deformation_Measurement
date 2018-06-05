package hashMapSorter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SortTimeorZ {

	
	/**
	 * Sort Z or T hashmap by comparing the order in Z or T
	 * 
	 * @param map
	 * @return
	 */
	public static HashMap<String, Integer> sortByValues(HashMap<String, Integer> map) {
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap<String, Integer> sortedHashMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}
	
}
