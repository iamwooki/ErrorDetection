/*
    Homework - Data Communication
    name : HyunWook, Hong 2016118274
    last modification : 03:04 AM
    description : this program is Error Dection System about CRC and CheckSum

 */
import java.util.Scanner;
import java.util.Vector;

import static java.lang.System.exit;

class CRC{
    private int[] COMPARENUMBER;
    private String FCS;
    private String REMAINDER;
    private String CODEWORD;
    private String DATAWORD;
    private String DIVIDEND;
    private String DIVISOR;
    private boolean SYNDROME;

    public CRC(){
        //temp init, NullPointerException
        COMPARENUMBER = new int[1];
    }

    public void operateXOR(int index){
        if(COMPARENUMBER[0]==1){ //first number is 1
            //compare
            for(int j=0;j<COMPARENUMBER.length;++j){
                COMPARENUMBER[j]^=(DIVISOR.charAt(j)-'0'); //1X..XX XOR DVISOR
            }
        }else{ //first number is 0
            //compare
            for(int j=0;j<COMPARENUMBER.length;++j){
                COMPARENUMBER[j]^=0; //00..00 XOR DVISOR
            }
        }
        //Array arrangement
        for(int k=0;k<COMPARENUMBER.length-1;++k){
            COMPARENUMBER[k]=COMPARENUMBER[k+1];
        }
    }

    public void encode(){
        //init
        int loop = DIVIDEND.length()-DIVISOR.length()+1;
        COMPARENUMBER =new int[DIVISOR.length()];
        copyArray(DIVIDEND,COMPARENUMBER); //copy

        //OPERATING
        for(int index=0;index<loop;++index){
            //call operate function
            this.operateXOR(index);

            //remainder
            if(index==(loop-1)){
                COMPARENUMBER[COMPARENUMBER.length-1]=-1;
                break;
            }
            COMPARENUMBER[COMPARENUMBER.length-1]=(DIVIDEND.charAt(DIVISOR.length()+index)-'0');

        }

        //REMAINDER
        StringBuffer tmpRemainder = new StringBuffer();
        for(int l=0;l<COMPARENUMBER.length-1;++l) tmpRemainder.append(COMPARENUMBER[l]);
        Integer tempRemainder = Integer.parseInt(tmpRemainder.toString());
        REMAINDER = tempRemainder.toString();
        //FCS
        if(REMAINDER.length()==FCS.length()) FCS = REMAINDER;
        else this.convertRemainderToFCS();

        //CODEWORD
        StringBuffer tmpCODEWORD = new StringBuffer(DATAWORD);
        tmpCODEWORD.append(FCS);
        CODEWORD=tmpCODEWORD.toString();
    }

    public void convertRemainderToFCS(){
        StringBuffer tmpFCS = new StringBuffer();
        for(int i=0;i<(FCS.length()-REMAINDER.length());++i) tmpFCS.append(0);
        tmpFCS.append(REMAINDER);
        FCS=tmpFCS.toString();
    }

    public void printEncoding(){
        //print
        System.out.println("Remainder : "+REMAINDER);
        System.out.println("FCS       : "+FCS+"\n");
        System.out.println("Codeword  : "+CODEWORD);
    }

    public void decode(String codeword){
        this.CODEWORD=codeword;
        //init
        int loop = CODEWORD.length()-DIVISOR.length()+1;
        COMPARENUMBER =new int[DIVISOR.length()];
        copyArray(CODEWORD,COMPARENUMBER); //copy

        //OPERATING
        for(int index=0;index<loop;++index){
            //call operate function
            this.operateXOR(index);

            //remainder
            if(index==(loop-1)){
                COMPARENUMBER[COMPARENUMBER.length-1]=-1;
                break;
            }
            COMPARENUMBER[COMPARENUMBER.length-1]=(CODEWORD.charAt(DIVISOR.length()+index)-'0');
        }

        //REMAINDER
        StringBuffer tmpRemainder = new StringBuffer();
        for(int l=0;l<COMPARENUMBER.length-1;++l) tmpRemainder.append(COMPARENUMBER[l]);
        Integer tempRemainder = Integer.parseInt(tmpRemainder.toString());
        REMAINDER = tempRemainder.toString();

        //FCS
        if(REMAINDER.length()==FCS.length()) FCS = REMAINDER;
        else this.convertRemainderToFCS();

        //Check
        SYNDROME=true; //init
        for(int i=0;i<FCS.length();++i){
            if((FCS.charAt(i)-'0')==1){ //error
                SYNDROME=false;
                break;
            }
        }

        //create DATAWARD
        StringBuffer tmpDATAWORD = new StringBuffer();
        for(int i=0;i<(CODEWORD.length()-FCS.length());++i) tmpDATAWORD.append(CODEWORD.charAt(i));
        DATAWORD = tmpDATAWORD.toString();
    }

    public void printDecoding(){
        //print
        System.out.println("Remainder : "+REMAINDER);
        System.out.println("FCS       : "+FCS+"\n");
        if(SYNDROME==true){
            System.out.println("Correct!");
            System.out.println("DATAWORD : "+DATAWORD);
        }else{
            System.out.println("Error!");
        }
    }

    public void createPaddingUsing(String dataword){
        this.DATAWORD = dataword;
        StringBuffer tmpDATAWORD = new StringBuffer(dataword);
        int length = DIVISOR.length()-1;
        for(int i=0;i<length;++i) tmpDATAWORD.append(0);
        this.DIVIDEND=tmpDATAWORD.toString();
    }

    public void initialize(String divisor){
        this.DIVISOR=divisor;
        int length = divisor.length()-1;
        StringBuffer tmpFCS =new StringBuffer();
        for(int i=0;i<length;++i) tmpFCS.append(0);
        FCS=tmpFCS.toString();
    }

    public void copyArray(String origin, int[] copy){
        for(int i=0;i<copy.length;++i) copy[i] = origin.charAt(i)-'0'; //convert char to int
    }
}

class CheckSum{                                //0       1       2       3       4       5       6       7
    private final static String[] HexToBinary  = new String[]{"0000","0001","0010","0011","0100","0101","0110","0111"
                                                //8       9       A       B       C       D       E       F
                                                ,"1000","1001","1010","1011","1100","1101","1110","1111"};
    private final static String[] BinaryToHex = new String[]{"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
    private String[] BINARY;
    private String INPUTMSG;
    private String CHECKSUM;
    private int[] SUM;
    private int NUMBEROFINPUT;
    private int[] bitwiseNOT;

    public void generate(String input){
        this.INPUTMSG = input;
        this.BINARY = this.convertHexToBinary(input.toUpperCase());
        System.out.println("<Hex to Binary>");
        for(String tmp:BINARY) System.out.println(tmp);
        this.operateSUM();
    }

    public String[] convertHexToBinary(String input){
        NUMBEROFINPUT = input.length();
        StringBuffer binary = new StringBuffer();
        Vector<String> tmpBINARY = new Vector<String>();
        for(int i=0;i<input.length();++i){
            if('A'<=input.charAt(i) && input.charAt(i)<='Z'){ //is alphabetic type?
                binary.append(HexToBinary[(input.charAt(i)-'A')+10]);
            }else{
                binary.append(HexToBinary[input.charAt(i)-'0']); // is numeric type?
            }

            if( ((i+1)%4) == 0 ){
                tmpBINARY.add(binary.toString());
                binary= new StringBuffer();
            }
        }
        return tmpBINARY.toArray(new String[tmpBINARY.size()]);
    }
    public void operateSUM(){
        int TEMP=0;
        int QUOTIENT=0;
        //init SUM array
        int index = (int)(Math.ceil(Math.sqrt(NUMBEROFINPUT))+1); //carry bit additional space
        int totalIndex  = index+NUMBEROFINPUT;
        SUM = new int[totalIndex];

        //Binary addition
        for(int i=(totalIndex-1),k=(BINARY[0].length()-1); i>=0 ; --i, --k) {
            //REAR->FRONT ,i = SUM index, k=BINARY(input) index
            if (k >= 0) {
                for (int j = 0; j < BINARY.length; ++j) TEMP += (BINARY[j].charAt(k) - '0');
                TEMP+=QUOTIENT;
                QUOTIENT = TEMP / 2; //QUOTIENT
                SUM[i] = TEMP % 2; //REMAINDER
                TEMP=0; //INIT
            } else {
                if ((QUOTIENT / 2) > 0 ) { //If there is QUOTIENT
                    SUM[i] = QUOTIENT % 2;
                    QUOTIENT /= 2;
                } else {
                    SUM[i] = QUOTIENT; //last QUOTIENT
                    QUOTIENT = 0; //fill 0 in the remaining carry array
                }
            }
        }
        //end round carry
        //////////////////////////////////////////////////////////////
        // '|' : pivot symbol
        //      ↓:carry index start             ↓ : sum index start
        //0 0 0 0 | 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0   ==>SUM Array
        //0 1 2 3   4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9
        /////////////////////////////////////////////////////////////
        bitwiseNOT = new int[BINARY[0].length()];
        int PIVOT = SUM.length-BINARY[0].length(); // refer above remark
        int sumIndex = (SUM.length-1); //index of SUM
        int carryIndex = (PIVOT-1); //index of Carry
        int stdIndex = BINARY[0].length()-1;
        QUOTIENT = 0; //init

        //operate
        while(stdIndex>=0){
            if(carryIndex>=0){
                TEMP = SUM[sumIndex]+SUM[carryIndex]+QUOTIENT;
                QUOTIENT = TEMP/2;
                bitwiseNOT[stdIndex] = TEMP%2;
            }else{
                if (((SUM[sumIndex]+QUOTIENT) / 2) > 0 ) { //If there is QUOTIENT
                    bitwiseNOT[stdIndex]=(SUM[sumIndex]+QUOTIENT)%2;
                    QUOTIENT=(SUM[sumIndex]+QUOTIENT)/2;
                } else {
                    bitwiseNOT[stdIndex]= (SUM[sumIndex]+QUOTIENT); //1
                    QUOTIENT = 0; //fill 0 in the remaining carry array
                }
            }
            --sumIndex; --carryIndex; --stdIndex;
        }

        //print Sum
        System.out.print("Sum: ");
        for(int binaryNumber:bitwiseNOT) System.out.print(binaryNumber);
        System.out.println();

        //Bitwise- NOT
        for(int i=0;i<bitwiseNOT.length;++i){
            if(bitwiseNOT[i]==0) bitwiseNOT[i]=1;
            else bitwiseNOT[i]=0;
        }
    }

    public void printGeneratingCheckSum(){
        StringBuffer tmpCHECKSUM = new StringBuffer();
        int tmpSum=0;

        //print Checksum
        System.out.print("Checksum: ");
        for(int i=0;i<bitwiseNOT.length;i+=4){
                         //1000                 0100                00010               0001  ==>binary to Decimal
            tmpSum=(bitwiseNOT[i]*8)+(bitwiseNOT[i+1]*4)+(bitwiseNOT[i+2]*2)+(bitwiseNOT[i+3]*1);
            tmpCHECKSUM.append(BinaryToHex[tmpSum]);
            System.out.print(BinaryToHex[tmpSum]+" ");
        }
        System.out.println();
        CHECKSUM = tmpCHECKSUM.toString();

        //print all
        System.out.print("Transmission side :");
        for(int i=0;i<INPUTMSG.length();i+=2){
            System.out.print(INPUTMSG.charAt(i)+""+INPUTMSG.charAt(i+1)+" ");
        }
        System.out.print("[ ");
        for(int i=0;i<CHECKSUM.length();i+=2){
            System.out.print(CHECKSUM.charAt(i)+""+CHECKSUM.charAt(i+1)+" ");
        }
        System.out.println("]\n");
    }

    public void printCheckingCheckSum(){
        StringBuffer tmpCHECKSUM = new StringBuffer();
        int tmpSum=0;
        boolean ERROR=false;

        //print Checksum
        System.out.print("Calculated Checksum: ");
        for(int i=0;i<bitwiseNOT.length;i+=4){
            //1000                 0100                00010               0001  ==>binary to Decimal
            tmpSum=(bitwiseNOT[i]*8)+(bitwiseNOT[i+1]*4)+(bitwiseNOT[i+2]*2)+(bitwiseNOT[i+3]*1);
            tmpCHECKSUM.append(BinaryToHex[tmpSum]);
            System.out.print(BinaryToHex[tmpSum]+" ");
        }
        System.out.println();
        CHECKSUM = tmpCHECKSUM.toString();

        //print error state
        System.out.print("Error : ");
        for(int i=0;i<CHECKSUM.length();++i){
            if((CHECKSUM.charAt(i)-'0')!=0){
                ERROR=true;
                break;
            }
        }
        if(ERROR) System.out.println(" YES!");
        else System.out.println(" NO!");
    }


}
class Viewer{
    Scanner input;
    CRC crc;
    CheckSum cs;
    public Viewer(){
            input = new Scanner(System.in);
            crc = new CRC();
            cs = new CheckSum();
    }
    public void displayCRC(){
        String INPUTWORD;
        String DIVISOR;
        int select;

        while(true){
            System.out.println("1.Dataword to codeword");
            System.out.println("2.Codeword to Dataword");
            System.out.println("0. Exit");
            System.out.print(">"); select= input.nextInt();

            switch(select){
                case 0:
                    exit(0);
                    break;
                case 1:
                    System.out.print("Dataword  : "); INPUTWORD = input.next();
                    System.out.print("Divisor   : "); DIVISOR = input.next();
                    crc.initialize(DIVISOR);
                    crc.createPaddingUsing(INPUTWORD);
                    crc.encode();
                    crc.printEncoding();
                    break;
                case 2:
                    System.out.print("Codeword  : "); INPUTWORD = input.next();
                    System.out.print("Divisor   : "); DIVISOR = input.next();
                    crc.initialize(DIVISOR);
                    crc.decode(INPUTWORD);
                    crc.printDecoding();
                    break;
                default:
                    System.out.println("wrong number");
                    break;
            }
        }
    }

    public void displayCheckSum(){
        int select;
        String INPUT;
        while(true){
            System.out.println("1.Send MSG - make CheckSum");
            System.out.println("2.Receive MSG - check CheckSum");
            System.out.println("0. Exit");
            System.out.print(">"); select= input.nextInt();

            switch(select){
                case 0:
                    exit(0);
                    break;
                case 1:
                    System.out.print("Send Message(Hexadecimal)  : ");
                    //check number of inputs
                    if( ((INPUT = input.next()).length()%4!=0) || (INPUT.length()<12) ){
                        System.out.println("input error");
                        System.out.println("1. Transmit bit string is 48 bits or more");
                        System.out.println("2. Transmit bit string must be a multiple of four.");
                        break;
                    }
                    cs.generate(INPUT);
                    cs.printGeneratingCheckSum();
                    break;
                case 2:
                    System.out.print("Received Message(Hexadecimal)  : ");
                    if( ((INPUT = input.next()).length()%4!=0) || (INPUT.length()<12) ){
                        System.out.println("input error");
                        System.out.println("1. Transmit bit string is 48 bits or more");
                        System.out.println("2. Transmit bit string must be a multiple of four.");
                        break;
                    }
                    cs.generate(INPUT);
                    cs.printCheckingCheckSum();
                    break;
                default:
                    System.out.println("wrong number");
                    break;
            }
        }
    }
    public void start(){
        int select;
        while(true){
            System.out.println("HW Error Detection - Data Communication");
            System.out.println("2016118274 HyunWook,Hong");
            System.out.println("---------------------------------------");
            System.out.println("1.CRC MODE");
            System.out.println("2.CHECKSUM MODE");
            System.out.println("0. Exit");
            System.out.print(">"); select= input.nextInt();

            switch(select){
                case 0:
                    exit(0);
                    break;
                case 1:
                    this.displayCRC();
                    break;
                case 2:
                    this.displayCheckSum();
                default:
                    System.out.println("wrong number");
                    break;
            }
        }
    }
}

public class Main {
    public static void main(String[] args)
    {
        new Viewer().start();
    }
}
