
package spamfilter;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;


/**
 *
 * @author n18dc
 */
/**
 * @author stackjava.com
 */
public class TrainData {
    //làm sạch 
    public static Set<String> toBagOfWord(String s) {
        HashSet<String> bag = new HashSet<>();
        // tách các từ phân cách bởi các dấu ,.!*"'()
        //-dùng StringTokenizer để phân tách một chuỗi thành các phần tử token của nó.
        StringTokenizer s1 = new StringTokenizer(s, " ,.!*\"\'()");
        while (s1.hasMoreTokens()) {
            bag.add(s1.nextToken());
        }
        // trả về một mảng các từ đã đc làm sạch
        return bag;
    }
    
    //huấn luyện
    public static void main(String[] args) throws IOException {
        // Đọc tất cả các file thư spam, chuyển thành danh sách các túi từ
        File folderSpam = new File("src/data/spam");
        ArrayList<Set<String>> listBagOfSpam = new ArrayList<>();
        File[] spams = folderSpam.listFiles();
        for (File file : spams) {
            String fileData = FileUtils.readFileToString(file, "UTF-16");
            
            //chuyển dữ liệu thô thành mảng các dữ liệu đc làm sạch = toBagOfWord
            Set<String> bagOfWord = toBagOfWord(fileData);
            listBagOfSpam.add(bagOfWord);
        }

        // Đọc tất cả các file thư non-spam, chuyển thành danh sách các túi từ
        File folderNonSpam = new File("src/data/non-spam");
        ArrayList<Set<String>> listBagOfNonSpam = new ArrayList<>();
        File[] nonSpams = folderNonSpam.listFiles();
        for (File file : nonSpams) {
            String fileData = FileUtils.readFileToString(file, "UTF-16");
            Set<String> bagOfWord = toBagOfWord(fileData);
            listBagOfNonSpam.add(bagOfWord);
        }
        
        // Ghi ra lần lượt 2 các list ra file
        ObjectOutputStream out = new ObjectOutputStream(
                        new FileOutputStream(new File("src/data/_result_training/result_training.dat")));
        out.writeObject(listBagOfSpam);
        out.writeObject(listBagOfNonSpam);
        out.close();
    }
}
