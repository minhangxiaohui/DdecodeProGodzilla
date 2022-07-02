package priv.ga0weI.forprogodzilla;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;

import java.io.*;
import java.util.logging.Logger;

/**
 * @author  ga0weI
 * @time 2022 7 20
 * @work in cipher data excel , out text
 *
 */

public class MyDecoder {
    public static final char[] hexCode = "0123456789abcdef".toCharArray();
    public static void main(String[] args) throws Exception{
        System.out.println("desctiption : decode for Godzilla modified by red team  in 2022.   \r\n_____________________________________________ ____________________by ga0weI");

        if (args.length != 1) {
            System.err.println("please use it in the right way ,eg: \r\n     java -jar DecoderProGodzilla.jar mydata.xlsx");
            System.exit(-1);
        }
//        String excleFilename="datas.xlsx";
        // 获取excel文件名
        String excleFilename=args[0];
        //调用python 冲
        try {
            String path = System.getProperty("user.dir");
            Process process = Runtime.getRuntime().exec("C:/Windows/System32/cmd.exe /c python ./getdata.py " + excleFilename, null, new File(path));
            process.waitFor();
            Logger.getGlobal().info("利用getdata.py 成功提取datas");
        }catch ( Exception e){
            Logger.getGlobal().severe("在调用getdata.py过程中出现问题:请检查输入的日志文件格式是否正确，并确认其存在\"数据(data)\"列，否则请将请求体内容所在列修改为该列名"+e);
        }
        //调解码方法冲
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader("data.txt"));
        FileOutputStream fileOutputStream = new FileOutputStream("result.txt"))
        {
            String line;
            String result;
            line = bufferedReader.readLine();
            while (line != null) {
                try{
                    result = run(line);
                    fileOutputStream.write(result.getBytes());
                }catch (Exception e){
                    line = "failed_________________________________________________________________________\r\n";
                    fileOutputStream.write(line.getBytes());
                }
                line = bufferedReader.readLine();
            }
        }
        Logger.getGlobal().info("批量解码成功，输出result.txt");

    }

    private static String run(String yuan) throws Exception{
        String datas="";
        String dd[]=yuan.split("&");
        for(int i=0;i<dd.length;i++)
        {
            String tmp=dd[i].substring(dd[i].indexOf("=")+1);
            datas=datas+tmp;
        }
        datas=datas.replace("m","%");
        datas = datas.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        datas= java.net.URLDecoder.decode(datas);
        byte[] poc_data=datas.getBytes();
        String todecode = new String(poc_data);
        String uu_head = "begin 644 encoder.buf\n";
        String uu_foot = " \nend\n";
//        if (!todecode.endsWith("b"))
//        {
//            todecode=todecode+"b";
//        }
        todecode = todecode.replaceAll("b", "\n").replaceAll("a", "=").replaceAll("c", "&").replaceAll("e", "'").replaceAll("d", "\"").replaceAll("f", "<").replaceAll("g", ">").replaceAll("h", ";").replaceAll("i", ":").replace("j", "$").replaceAll("k", "%").replace("l", "^");
        todecode = uu_head + todecode + uu_foot;
        byte[] var1 = null;
        byte[] uu_tmp = Uudecode(todecode.getBytes());
        uu_tmp = deleteZero(uu_tmp).getBytes();
        var1 = base64Decode((new String(uu_tmp)).substring(13));
        String res = unSerialize(var1);

        return res+"_________________________________________________________________________\r\n";
    }

    public static byte[] Uudecode(byte[] todecode) throws Exception {

        InputStream inputStream = new ByteArrayInputStream(todecode);
        InputStream inputStreams = MimeUtility.decode(inputStream, "uuencode");
        byte[] bytes = new byte[inputStreams.available()];
        inputStreams.read(bytes);
        return bytes;
    }
    public static String deleteZero(byte[] todelete) throws Exception {
        int length = 0;

        for(int i = 0; i < todelete.length; ++i) {
            if (todelete[i] == 0) {
                length = i;
                break;
            }
        }

        return new String(todelete, 0, length);
    }
    public static byte[] base64Decode(String bs) throws Exception {
        Object var1 = null;

        byte[] value;
        try {
            Class<?> z = Class.forName("com.sun.org.apache.xml.internal.security.utils.Base64");
            value = (byte[])((byte[])z.getMethod("decoder", String.class).invoke((Object)null, bs));
        } catch (Exception var6) {
            try {
                Class<?> zz = Class.forName("java.util.Base64");
                Object zzd = zz.getMethod("getDecoder", (Class[])null).invoke(zz, (Object[])null);
                value = (byte[])((byte[])zzd.getClass().getMethod("decode", String.class).invoke(zzd, bs));
            } catch (Exception var5) {
                Class<?> zz = Class.forName("sun.misc.BASE64Decoder");
                value = (byte[])((byte[])zz.getMethod("decodeBuffer", String.class).invoke(zz.newInstance(), bs));
            }
        }

        return value;
    }
    public static int bytesToInt(byte[] var0) {
        int var1 = var0[0] & 255 | (var0[1] & 255) << 8 | (var0[2] & 255) << 16 | (var0[3] & 255) << 24;
        return var1;
    }
    /**
     Godzilla客户端请求流量反格式化
     输入：Gzip解压之后的原始流量
     输出：哥斯拉构造的明文命令流量
     */
    public static String unSerialize(byte[] parameterByte) {
        StringBuilder result =new StringBuilder("");
        ByteArrayInputStream tStream = new ByteArrayInputStream(parameterByte);
        ByteArrayOutputStream tp = new ByteArrayOutputStream();
        String key = null;
        byte[] lenB = new byte[4];
//        Object var6 = null;

        try {
            ByteArrayInputStream inputStream = tStream;

            while(true) {
                while(true) {
                    byte t = (byte)inputStream.read();
                    if (t == -1) {
                        tp.close();
                        tStream.close();
                        inputStream.close();
                        return result.toString();
                    }

                    if (t == 2) {
                        key = tp.toString();
                        inputStream.read(lenB);//读后面四个字节
                        int len = (lenB[0] & 255) | ((lenB[1] & 255) << 8) | ((lenB[2] & 255) << 16) | ((lenB[3] & 255) << 24);//读取“2”后面四个字节里面的内容，获取data的长度
                        byte[] data = new byte[len];
                        int readOneLen = 0;

                        while((readOneLen += inputStream.read(data, readOneLen, data.length - readOneLen)) < data.length) {
                        }

                        //data存在包含class文件的情况 如：加载内存马，此时还原data中的class文件
                        String henxStrings = bytesTohexString(data);
//                        System.out.println("class字节码文件的16进制信息："+henxStrings);
                        if(henxStrings.startsWith("cafe"))
                        {
                            FileOutputStream fos = new FileOutputStream(key+"Eval.class");
                            //字节数组data转换成16进制然后正则匹配cafe 来获取class的字节码
                            fos.write(data);
                            System.out.println("检查到非首次请求流量里面有class文件信息，已还原"+key+"Eval.class文件");
                            fos.flush();
                            fos.close();
                            result.append(key+"="+"还原文件根目录下:"+key+"Eval.class"+"\n");
                            tp.reset();
                        }
                        else {
                            result.append(key+"="+new String(data)+"\n");
                            tp.reset();
                        }

                    } else {
                        tp.write(t);
                    }
                }
            }
        } catch (Exception var11) {
//            var11.printStackTrace();
            return "格式错误";
        }
    }
    /**
     * 字节数组转16进制string
     * @param data
     * @throws Exception
     * @return hexString
     */
    public static String bytesTohexString(byte[] data ){
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }
}
