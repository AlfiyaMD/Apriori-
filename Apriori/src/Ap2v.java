import java.io.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Ap2v {
   public static void main(String[] args) {
        Scanner terminal = new Scanner(System.in);
        ArrayList<ArrayList<String>> transactions = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> trans = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> prevItemSetsWithMinSupportCount = new ArrayList<ArrayList<String>>();
        int minSupportCount = 0;
        
        try{
            //File 3 is the input file to the apriori
            FileReader f=new FileReader("C:\\Users\\Khatun\\Desktop\\Apriori\\src\\apriori\\file5.txt");
            
            //previous output file is deleted
            File file = new File("C:\\Users\\Khatun\\Desktop\\Apriori\\src\\apriori\\file4.txt");
            file.delete();
            
            //Output file is created to store recommendations
            Path p1 = Paths.get("C:\\Users\\Khatun\\Desktop\\Apriori\\src\\apriori\\file4.txt");
            Path p = Files.createFile(p1);
            
            //reading input file
            BufferedReader br=new BufferedReader(f);
            String g=br.readLine();
            int k=1;
            while(g!=null)
            {   
                if(k!=1){
                    ArrayList<String> transaction = new ArrayList<String>();
                    String arr[] = g.split(" ");
                    for (int j = 0; j < arr.length; j++) 
                        transaction.add(arr[j]);
                    
                    //trans store all the data from the input file
                    trans.add(transaction);
                    g=br.readLine();
                }
                else{
                    minSupportCount=Integer.parseInt(g);
                    k=0;
                    g=br.readLine();
                }
            }
            f.close();
            br.close();
        }
        catch(IOException e) {}
        
        //convert ArrayList to Array, so that each single element can be accessed easily
        String[][] a1 = new String [5000][2];
        for(int m = 0; m<a1.length; m++){
            for(int n = 0; n<a1[m].length; n++){
                a1[m][n]="";
            }
        }
        int j=1;
        int i=0;
        for (ArrayList<String> transaction : trans) {
            for (String tra : transaction) {
                if(j==0){
                    a1[i][1]=tra;
                    j=1;
                }
                else{
                    a1[i][0]=tra;
                    j=0;
                }
            }i++;
        }
        
        //add food id of single transaction as one row in ArrayList
        int q=0;
        String cid=a1[q][0];
        String fid=a1[q][1];
        while(true){
            ArrayList<String> tran = new ArrayList<String>();
            if(cid.equals(a1[q+1][0])){
                fid=fid+" "+a1[q+1][1];
                cid=a1[q+1][0];
                q=q+1;
            }
            else{
                if(!fid.isEmpty()){
                    String arr[] = fid.split(" ");
                    for(int m=0;m<arr.length;m++){
                        tran.add(arr[m]);
                    }
                    transactions.add(tran);
                }
                cid=a1[q+1][0];
                fid=a1[q+1][1];
                q=q+1;
                
            }
            if(fid.isEmpty())
                break;
        }
        
        System.out.println("The itemset(s) that are the most frequent itemset(s): ");
		
        // Get all unique items
        ArrayList<String> items = getUniqueItems(transactions);
        
	int x = 0; // x is the number of elements in the combinations
	while (true) {
            x++;
            
            // List of support count of each itemset
            ArrayList<Integer> supportCountList = new ArrayList<Integer>();

            // Get permuted itemsets
            ArrayList<ArrayList<String>> itemSets = getItemSets(items, x);
            
            if(x==1){
                for (ArrayList<String> itemSet : itemSets) {
                    for (String item : itemSet) {
                        int count = 0;
                        for (ArrayList<String> transaction : transactions) {
                            for (int m = 0; m < transaction.size(); m++)
                                if (transaction.get(m).equals(item))
                                    count++;
                        }
                        supportCountList.add(count);
                    }
                }
            }
            else{ 
                // Calculate each itemset's support count
                for (ArrayList<String> itemSet : itemSets) {
                    int count = 0;
                    for (ArrayList<String> transaction : transactions) {
                        if (existsInTransaction(itemSet, transaction)) 
                            count++;
                    }
                    supportCountList.add(count);
                }
            }
            
            //prune itemsets that do not satisfy the support count
            ArrayList<ArrayList<String>> itemSetsWithMinSupportCount = getItemSetsWithMinSupportCount(itemSets, supportCountList, minSupportCount);
            String a[]=getCombo(itemSetsWithMinSupportCount);
            
            //recommendations are written to file
            writeUsingFiles(a);
            
            
            // No itemSetsWithMinSupportCount exist
            if (itemSetsWithMinSupportCount.size() == 0) {
                break;
            }
            
            items = getUniqueItems(itemSetsWithMinSupportCount);
            prevItemSetsWithMinSupportCount = itemSetsWithMinSupportCount;
        }

   }
    
    private static ArrayList<String> getUniqueItems (ArrayList<ArrayList<String>> data) {
	ArrayList<String> toReturn = new ArrayList<String>();
        
	for (ArrayList<String> transaction : data) {
            for (String item : transaction) {
                if (!toReturn.contains(item)) toReturn.add(item);
            }
	}
	Collections.sort(toReturn);
	return toReturn;
    }

	
    private static ArrayList<ArrayList<String>> getItemSets (ArrayList<String> items, int number) {
        if (number == 1) {
            ArrayList<ArrayList<String>> toReturn = new ArrayList<ArrayList<String>>();
            for (String item : items) {
                ArrayList<String> aList = new ArrayList<String>();
		aList.add(item);
		toReturn.add(aList);
            }
	return toReturn;
        } 
        else {
            int size = items.size();
            ArrayList<ArrayList<String>> toReturn = new ArrayList<ArrayList<String>>();

            for (int i = 0; i < size; i++) {
                // Copy items to _items
		ArrayList<String> _items = new ArrayList<String>();
		for (String item : items) {
                    _items.add(item);
		}
                
                // Get item at i-th position
                String thisItem = items.get(i);

		// Remove items upto i, inclusive
		for (int j = 0; j <= i; j++) {
                    _items.remove(0);
		}
				
                // Get permutations of the remaining items
		ArrayList<ArrayList<String>> permutationsBelow = getItemSets(_items, number - 1);

		// Add thisItem to each permutation and add the permutation to toReturn
		for (ArrayList<String> aList : permutationsBelow) {
                    aList.add(thisItem);
                    Collections.sort(aList);
                    toReturn.add(aList);
		}
            }
            return toReturn;
        }
    }

    // Check if all items exist in a transaction
    private static boolean existsInTransaction (ArrayList<String> items, ArrayList<String> transaction) {
	for (String item : items) {
            if (!transaction.contains(item)) return false;
	}
	return true;
    }

    // Returns itemsets with support count greater than or equal to minimum support count
    private static ArrayList<ArrayList<String>> getItemSetsWithMinSupportCount (ArrayList<ArrayList<String>> itemSets, ArrayList<Integer> count, int minSupportCount) {
        ArrayList<ArrayList<String>> toReturn = new ArrayList<ArrayList<String>>();
                
        for (int i = 0; i < count.size(); i++) {
            int c = count.get(i);
            if (c >= minSupportCount) {
                ArrayList<String> asd = new ArrayList<String>();
                
                String as;
                System.out.println(itemSets.get(i)+" -> "+c);
                toReturn.add(itemSets.get(i));
                as=Integer.toString(c);
                asd.add(as);
                toReturn.add(asd);
            }
        }
	return toReturn;
    }

    private static String[] getCombo(ArrayList<ArrayList<String>> toReturn) {
        String a[]={};
        String q="";
        int p=1;
        for (ArrayList<String> trans : toReturn){
            if(p==0){
                for (String tr : trans)
                    q = q.concat("->"+tr+"\n");
                
                p=1;
            }
            else{
                for (String tr : trans){
                    q = q.concat(tr+" ");
                }
                p=0;
            }
        }
        
        for (int i = 0; i < q.length(); i++){
                a= q.split("\n");
        }
        return a;
    }

    private static void writeUsingFiles(String[] a) {
        try{
            File file = new File("C:\\Users\\Khatun\\Desktop\\Apriori\\src\\apriori\\file4.txt");
            Path p = Paths.get("C:\\Users\\Khatun\\Desktop\\Apriori\\src\\apriori\\file4.txt");
            
             for (int i = 0; i < a.length; i++){
                byte[] b = Files.readAllBytes(p);
                String c="\r\n";
                byte[] apdata = new byte[b.length + a[i].getBytes().length];
                byte[] apdata1 = new byte[apdata.length+c.getBytes().length];
                System.arraycopy(b, 0, apdata, 0, b.length);
                System.arraycopy(a[i].getBytes(), 0, apdata, b.length, a[i].getBytes().length);
                System.arraycopy(apdata, 0, apdata1, 0, apdata.length);
                System.arraycopy(c.getBytes(), 0, apdata1, apdata.length, c.getBytes().length);
                Files.write(Paths.get("C:\\Users\\Khatun\\Desktop\\Apriori\\src\\apriori\\file4.txt"), apdata1);
                
            }
        }
        catch(IOException e){System.out.println(e);}
    }
}