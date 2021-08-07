/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spamfilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Set;
import jdk.nashorn.internal.codegen.CompilerConstants;

import org.apache.commons.io.FileUtils;
/**
 *
 * @author n18dc
 */


public class SpamFilter {

    // mảng chứa các túi từ của thư thường (non-spam)
    static ArrayList<Set<String>> listBagOfNonSpam = new ArrayList<>();
    // mảng chứa các túi từ của thư rác (spam)
    static ArrayList<Set<String>> listBagOfSpam = new ArrayList<>();
    // mảng chứa thông tin các xác suất từng từ của túi từ
    public ArrayList<String> listDetail = new ArrayList<>();

    // tinh xac xuat P(xi=x|nhan= nonspam)
    public static double pNonSpam(String x) { 
        double k = 0;
        for (int i = 0; i < listBagOfNonSpam.size(); i++) {
            // moi lan x xuat hien trong 1 thu thuong thi k++
            if (listBagOfNonSpam.get(i).contains(x))
                k++;
        }
        return (k + 1) / (listBagOfNonSpam.size() + 1);
        // P(xi|nhan= nonspam)= (k+1)/(sothuthuong+1);
        // trong do: k la so cac mail nonspam xuat hien xi
        // sothuthuong la so mail nonspam

    }

    // tinh xac xuat P(xi=x|nhan= spam)
    public static double pSpam(String x) {
        double k = 0;
        for (int i = 0; i < listBagOfSpam.size(); i++) {
            if (listBagOfSpam.get(i).contains(x))
                // moi lan x xuat hien trong 1 thu rac thi k++
                k++;
        }
        return (k + 1) / (listBagOfSpam.size() + 1);
        // P(xi|nhan= spam)= (k+1)/(sothurac+1);
        // trong do: k la so cac mail spam xuat hien xi
        // sothurac la so mail spam
    }

    @SuppressWarnings("unchecked")
    public String run(String data) throws FileNotFoundException, IOException, ClassNotFoundException {
        String result="";
        // đọc dữ liệu huấn luyện từ trước ở trong file result_training.dat ra
        
        ObjectInputStream inp = new ObjectInputStream(
                        new FileInputStream(new File("src/data/_result_training/result_training.dat")));
        //-đọc lần lượt theo thứ tự lưu vào
        listBagOfSpam = (ArrayList<Set<String>>) inp.readObject();
        listBagOfNonSpam = (ArrayList<Set<String>>) inp.readObject();
        inp.close();
        System.out.println("Hoàn load dữ liệu huấn luyện");
        
        // Tiền xử lý mail cần kiểm tra
        Set<String> bagOfTest = TrainData.toBagOfWord(data);
        
        // xác xuất là thư thường. P(xi|non-spam)
        double C_NB1 =listBagOfNonSpam.size() / ((double) listBagOfNonSpam.size() + listBagOfSpam.size()) ;
        // xác xuất là thư rác. P(xi|spam)
        double C_NB2 = listBagOfSpam.size() / ((double) listBagOfNonSpam.size() + listBagOfSpam.size());

        ArrayList<String> listStringTest = new ArrayList<>(bagOfTest);

        for (String strTest : listStringTest) {
            if (pNonSpam(strTest) != ((double) 1 / (listBagOfNonSpam.size() + 1))
                        || pSpam(strTest) != ((double) 1 / (listBagOfSpam.size() + 1))) {
                this.listDetail.add(strTest);
                this.listDetail.add(String.valueOf(Math.round(pNonSpam(strTest)*1000.0)/1000.0));
                this.listDetail.add(String.valueOf(Math.round(pSpam(strTest)*1000.0)/1000.0));
                C_NB1 *= pNonSpam(strTest);
                C_NB2 *= pSpam(strTest);
            }
        }
        System.out.println(this.listDetail.size());
        if (C_NB1 < C_NB2) {
            // Bổ sung thư vừa kiểm tra vào tập huấn luyện.
            listBagOfSpam.add(bagOfTest);
            result= "SPAM";
        } else {
            listBagOfNonSpam.add(bagOfTest);
            result= "NON_SPAM";
        }

        // Lưu lại tập huấn luyện mới.
        ObjectOutputStream out = new ObjectOutputStream(
                        new FileOutputStream(new File("src/data/_result_training/result_training.dat")));
        out.writeObject(listBagOfSpam);
        out.writeObject(listBagOfNonSpam);
        out.close();
        System.out.println("Kết thúc");
        return result;

    }

}
