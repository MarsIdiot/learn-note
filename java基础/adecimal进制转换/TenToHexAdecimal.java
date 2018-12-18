package adecimal;

import org.junit.Test;

public class TenToHexAdecimal {
    public static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    /**
     *十进制转十六进制
     * 除基取余，直到商为0，最后，余数反转就是结果
     * @param valueTen
     * @return
     */
    public String tenToHexAdceimal(Integer valueTen){
        int baseValue=16;
        int beiChuValue=valueTen;
        StringBuffer buf=new StringBuffer("");//接受余数
        while (beiChuValue!=0){
           int yuShuValue = beiChuValue % baseValue;
           if(yuShuValue>=0 && yuShuValue<=15){
               buf.append(HEX_DIGITS[yuShuValue]);
           }
           beiChuValue=(beiChuValue-yuShuValue)/baseValue;
        }
        //将buf反向输出
        String hexValue = buf.reverse().toString();
        return hexValue;
    }
    @Test
    public void test(){
        String hexValue = this.tenToHexAdceimal(78);
        System.out.println("十六进制："+hexValue);
    }


    /**
     * Integer.toHexString(num));// java自带的十进制转换十六进制方法
     * Integer.parseInt(hex,16);//java自带的十六进制转换十进制方法
     */
}
