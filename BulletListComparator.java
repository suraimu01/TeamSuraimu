package TeamSuraimu;

import java.util.Comparator;
/**
 * MyClass - a class by (your name here)
 */
public class BulletListComparator implements Comparator<BulletInfo>{

	public int compare(BulletInfo a, BulletInfo b){
		
		double n1 = a.getExpectTime();
		double n2 = b.getExpectTime();
		
		if (n1 > n2) {
            return 1;

        } else if (n1 == n2) {
            return 0;

        } else {
            return -1;

        }
	}
}
