package extensions;

import java.util.List;

import play.templates.JavaExtensions;

public class ListExtension extends JavaExtensions {

    public static List subList(List list, int max) {
        int size = Math.min(max, list.size());
        return list.subList(0, size);
    }
    
    public static List pageList(List list, int page, int pageSize) {
    	
    	page = Math.max(1, page);
    	
    	int start = (page - 1)*pageSize;
    	int end = page*pageSize;
        
    	start = Math.min(start, list.size());
    	end = Math.min(end, list.size());
        
        return list.subList(start, end);
    }
}