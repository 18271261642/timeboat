package net.sgztech.timeboat.ui.utils;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;


/**
 * Created by Admin
 * Date 2023/1/11
 * @author Admin
 */
public class ReadUtils {


    /**
     * 读取二进制文件
     * @param pathname 路径
     * @return
     */
    public byte[] readFromByteFile(String pathname)  {
        try {
            File filename = new File(pathname);
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(filename));
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            byte[] temp = new byte[1024];
            int size = 0;
            while((size = in.read(temp)) != -1){
                out.write(temp, 0, size);
            }
            in.close();
            byte[] content = out.toByteArray();
            return content;
        }catch (Exception e){
            e.printStackTrace();
            return new byte[0];
        }
    }

}
