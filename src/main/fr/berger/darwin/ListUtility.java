package main.fr.berger.darwin;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;

class ListUtility {
	
	public static int deleteNullItems(@NotNull ArrayList<?> list) {
		if (list == null)
			return 0;
		
		int numberNullItemsFound = 0;
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == null) {
				list.remove(i);
				i--;
				numberNullItemsFound++;
			}
		}
		
		return numberNullItemsFound;
	}
}
