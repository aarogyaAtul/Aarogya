package com.creative.tool;

import android.annotation.SuppressLint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author fangrf 2018-05-16
 */
public class SCPFile {
	
	private static final int  ECG_SCP_LEN =  9280;
	private byte ECG_SCP[];
	private int mYear;
	private int mMonth;
	private int mDay;
	private int[] ecg_dat;
	private String mFilePath,mFileName;
	private int mECGResult,mHR;
	
	/**
	 * @param filePath  save path of .scp 
	 * @param datas   raw datas of ECG wave 
	 * @param result  result of measure ECG
	 * @param HR  heart rate
	 */
	@SuppressLint("SimpleDateFormat")
	public SCPFile(String filePath,int[] datas,int result, int HR) {
		ECG_SCP = new byte[ECG_SCP_LEN]; 
		//ecg_dat = new int[4500];
		ecg_dat = datas;
		mFilePath = filePath;
		mECGResult = result;
		mHR = HR;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh_mm_ss");
		String day = sdf.format(new Date());
		String[] arr = day.split(" "); 
		String[] arrDay = arr[0].split("-");
		mYear = Integer.valueOf(arrDay[0]);
		mMonth = Integer.valueOf(arrDay[1]);
		mDay = Integer.valueOf(arrDay[2]);
		//System.out.println(mYear+","+mMonth+","+mDay);
		mFileName = arr[1]+".scp";//hh_mm_ss.scp
	}
	
	public void packSCP(){
		int i;
		short tmp_crc;	
		
		/***********ECG-SCP CRC (2 Byte)***********/
		ECG_SCP[0] = 0;
		ECG_SCP[1] = 0;

		/***********ECG-SCP Length (4 Byte) 9280 ********/
		ECG_SCP[2] = 0x40;		//low byte  
		ECG_SCP[3] = 0x24;
		ECG_SCP[4] = 0;
		ECG_SCP[5] = 0;		//high byte

/*************SECTION 0*******************/
		//Section 0 crc (2 byte)
		ECG_SCP[6] = 0;
		ECG_SCP[7] = 0;
		//Section 0 ID (2 byte)
		ECG_SCP[8] = 0;;	//low byte
		ECG_SCP[9] = 0;		//high byte
		//Section 0 Length (4 byte)  136
		ECG_SCP[10] = (byte) 0x88;	//low byte
		ECG_SCP[11] = 0;
		ECG_SCP[12] = 0;
		ECG_SCP[13] = 0;	//high byte
		//Version (1 byte)
		ECG_SCP[14] = 0x0D;		//Version: v1.3
		//Protocol Version (1byte)
		ECG_SCP[15] = 0x0D;		//Version: v1.3
		//Reserved "SCPECG" (6 byte)
		ECG_SCP[16] = 83;//s
		ECG_SCP[17] = 67;//c
		ECG_SCP[18] = 80;//p
		ECG_SCP[19] = 69;//e
		ECG_SCP[20] = 67;//c
		ECG_SCP[21] = 71;//g
		//Pointer Field: Section 0
		//Section ID
		ECG_SCP[22] = 0;
		ECG_SCP[23] = 0;
		//Section 0 Length  136
		ECG_SCP[24] = (byte) 0x88; //total: ((ECG_SCP[25]<<8)|ECG_SCP[24]) & 0xff = 136
		ECG_SCP[25] = 0;
		ECG_SCP[26] = 0;
		ECG_SCP[27] = 0;
		//Index to Section 0
		ECG_SCP[28] = 7;
		ECG_SCP[29] = 0;
		ECG_SCP[30] = 0;
		ECG_SCP[31] = 0;
		//Pointer Field: Section 1
		//Section ID (2 byte)
		ECG_SCP[32]  = 1;
		ECG_SCP[33]  = 0;
		//Section 1 Length (4 byte) 135
		ECG_SCP[34]  = 87;
		ECG_SCP[35]  = 0;
		ECG_SCP[36]  = 0;
		ECG_SCP[37]  = 0;
		//Index to Section 1 (2 byte)  143
		ECG_SCP[38]  = (byte) 0x8f;
		ECG_SCP[39]  = 0;
		ECG_SCP[40]  = 0;
		ECG_SCP[41]  = 0;
		//Pointer Field: Section 2
		//Section 2 ID (2 byte)
		ECG_SCP[42]  = 2;	//low byte
		ECG_SCP[43]  = 0;	//high byte
		//Section 2 Length (4 byte)
		ECG_SCP[44]  = 0;	//low byte
		ECG_SCP[45]  = 0;
		ECG_SCP[46]  = 0;
		ECG_SCP[47]  = 0;	//high byte
		//Index to Section 2 (2 byte)
		ECG_SCP[48]  = 0;		//low byte
		ECG_SCP[49]  = 0;
		ECG_SCP[50]  = 0;
		ECG_SCP[51]  = 0;		//high byte
		//Pointer Field: Section 3
		//Section 3 ID (2 byte)
		ECG_SCP[52]  = 3;	//low byte
		ECG_SCP[53]  = 0;	//high byte
		//Section 3 Length (4 byte)
		ECG_SCP[54]  = 27;	//low byte
		ECG_SCP[55]  = 0;
		ECG_SCP[56]  = 0;
		ECG_SCP[57]  = 0;	//high byte
		//Index to Section 3 (2 byte) 230
		ECG_SCP[58]  = (byte) 0xe6;		//low byte
		ECG_SCP[59]  = 0;
		ECG_SCP[60]  = 0;
		ECG_SCP[61]  = 0;		//high byte
		//Pointer Field: Section 4
		//Section 4 ID (2 byte)
		ECG_SCP[62]  = 4;	//low byte
		ECG_SCP[63]  = 0;	//high byte
		//Section 4 Length (4 byte)
		ECG_SCP[64]  = 0;	//low byte
		ECG_SCP[65]  = 0;
		ECG_SCP[66]  = 0;
		ECG_SCP[67]  = 0;	//high byte
		//Index to Section 4 (4 byte)
		ECG_SCP[68]  = 0;		//low byte
		ECG_SCP[69]  = 0;
		ECG_SCP[70]  = 0;
		ECG_SCP[71]  = 0;		//high byte
		//Pointer Field: Section 5
		//Section 5 ID (2 byte)
		ECG_SCP[72]  = 5;	//low byte
		ECG_SCP[73]  = 0;	//high byte
		//Section 5 Length (4 byte)
		ECG_SCP[74]  = 0;	//low byte
		ECG_SCP[75]  = 0;
		ECG_SCP[76]  = 0;
		ECG_SCP[77]  = 0;	//high byte
		//Index to Section 5 (4 byte)
		ECG_SCP[78]  = 0;		//low byte
		ECG_SCP[79]  = 0;
		ECG_SCP[80]  = 0;
		ECG_SCP[81]  = 0;		//high byte
		//Pointer Field: Section 6
		//Section 6 ID (2 byte)
		ECG_SCP[82]  = 6;	//low byte
		ECG_SCP[83]  = 0;	//high byte
		//Section 6 Length (4 byte)
		ECG_SCP[84]  = 0x40;	//low byte
		ECG_SCP[85]  = 0x23;
		ECG_SCP[86]  = 0;
		ECG_SCP[87]  = 0;	//high byte
		//Index to Section 6 (4 byte)
		ECG_SCP[88]  = 1;		//low byte
		ECG_SCP[89]  = 1;
		ECG_SCP[90]  = 0;
		ECG_SCP[91]  = 0;		//high byte
		//Pointer Field: Section 7
		//Section 7 ID (2 byte)
		ECG_SCP[92]  = 7;	//low byte
		ECG_SCP[93]  = 0;	//high byte
		//Section 7 Length (4 byte)
		ECG_SCP[94]  = 0;	//low byte
		ECG_SCP[95]  = 0;
		ECG_SCP[96]  = 0;
		ECG_SCP[97]  = 0;	//high byte
		//Index to Section 7 (4 byte)
		ECG_SCP[98]  = 0;		//low byte
		ECG_SCP[99]  = 0;
		ECG_SCP[100]  = 0;
		ECG_SCP[101]  = 0;		//high byte
		//Pointer Field: Section 8
		//Section 8 ID (2 byte)
		ECG_SCP[102]  = 8;	//low byte
		ECG_SCP[103]  = 0;	//high byte
		//Section 8 Length (4 byte)
		ECG_SCP[104]  = 0;	//low byte
		ECG_SCP[105]  = 0;
		ECG_SCP[106]  = 0;
		ECG_SCP[107]  = 0;	//high byte
		//Index to Section 8 (4 byte)
		ECG_SCP[108]  = 0;		//low byte
		ECG_SCP[109]  = 0;
		ECG_SCP[100]  = 0;
		ECG_SCP[111]  = 0;		//high byte
		//Pointer Field: Section 9
		//Section 9 ID (2 byte)
		ECG_SCP[112]  = 9;	//low byte
		ECG_SCP[113]  = 0;	//high byte
		//Section 9 Length (4 byte)
		ECG_SCP[114]  = 0;	//low byte
		ECG_SCP[115]  = 0;
		ECG_SCP[116]  = 0;
		ECG_SCP[117]  = 0;	//high byte
		//Index to Section 9 (4 byte)
		ECG_SCP[118]  = 0;		//low byte
		ECG_SCP[119]  = 0;
		ECG_SCP[120]  = 0;
		ECG_SCP[121]  = 0;		//high byte
		//Pointer Field: Section 10
		//Section 10 ID (2 byte)
		ECG_SCP[122]  = 10;	//low byte
		ECG_SCP[123]  = 0;	//high byte
		//Section 10 Length (4 byte)
		ECG_SCP[124]  = 0;	//low byte
		ECG_SCP[125]  = 0;
		ECG_SCP[126]  = 0;
		ECG_SCP[127]  = 0;	//high byte
		//Index to Section 10 (4 byte)
		ECG_SCP[128]  = 0;		//low byte
		ECG_SCP[129]  = 0;
		ECG_SCP[130]  = 0;
		ECG_SCP[131]  = 0;		//high byte
		//Pointer Field: Section 11
		//Section 11 ID (2 byte)
		ECG_SCP[132]  = 11;	//low byte
		ECG_SCP[133]  = 0;	//high byte
		//Section 11 Length (4 byte)
		ECG_SCP[134]  = 0;	//low byte
		ECG_SCP[135]  = 0;
		ECG_SCP[136]  = 0;
		ECG_SCP[137]  = 0;	//high byte
		//Index to Section 11 (4 byte)
		ECG_SCP[138]  = 0;		//low byte
		ECG_SCP[139]  = 0;
		ECG_SCP[140]  = 0;
		ECG_SCP[141]  = 0;		//high byte

		//CRC of Section 0
		tmp_crc = (short) 0xFFFF;
		for (i=8; i<=141; i++)
		{
			tmp_crc = SCP_CRC_Calculate(ECG_SCP[i], tmp_crc);
		}
		ECG_SCP[6] = (byte) (tmp_crc & 0xFF);
		ECG_SCP[7] = (byte) ((tmp_crc>>8)& 0xFF);
		/*************The End of SECTION 0*******************/


/*************SECTION 1*******************/
		//Section 1 crc (2 byte)
		ECG_SCP[142] = 0;
		ECG_SCP[143] = 0;
		//Section 1 ID (2 byte)
		ECG_SCP[144] = 1;	//low byte
		ECG_SCP[145] = 0;		//high byte
		//Section 1 Length (4 byte)
		ECG_SCP[146] = 87;	//low byte
		ECG_SCP[147] = 0;
		ECG_SCP[148] = 0;
		ECG_SCP[149] = 0;	//high byte
		//Version (1 byte)
		ECG_SCP[150] = 0x0D;		//Version: v1.3
		//Protocol Version (1 byte)
		ECG_SCP[151] = 0x0D;		//Version: v1.3
		//Reserved  (6 byte)
		ECG_SCP[152] = 0;
		ECG_SCP[153] = 0;
		ECG_SCP[154] = 0;
		ECG_SCP[155] = 0;
		ECG_SCP[156] = 0;
		ECG_SCP[157] = 0;
		//Header Field : 2 Patient ID
		//Field Tag (1 byte)
		ECG_SCP[158] = 2;
		//Field 2 Length (2 byte)
		ECG_SCP[159] = 2;	//low byte
		ECG_SCP[160] = 0;	//high byte
		//Field 2 Value 
		ECG_SCP[161] = 1;	
		ECG_SCP[162] = 0;	
		//Header Field : 14 ID of the Acquiring Device
		//Field Tag (1 byte)
		ECG_SCP[163] = 14;
		//Field 14 Length (2 byte)
		ECG_SCP[164] = 48;	//low byte
		ECG_SCP[165] = 0;	//high byte
		//Field 14 Value
		//-------Institution number (2 byte)
		ECG_SCP[166] = 0;	
		ECG_SCP[167] = 0;
		//-------Department number (2 byte)
		ECG_SCP[168] = 0;	
		ECG_SCP[169] = 0;
		//-------Device ID (2 byte)
		ECG_SCP[170] = 0;	
		ECG_SCP[171] = 0;
		//-------Device Type (1 byte)
		ECG_SCP[172] = 0;
		//--------------(1 byte)
		ECG_SCP[173] = (byte) 0xff;//255
		//----------Text characters (5 byte): Up to 5 bytes of text and NULL terminator
		ECG_SCP[174] = 32;
		ECG_SCP[175] = 32;
		ECG_SCP[176] = 32;
		ECG_SCP[177] = 32;
		ECG_SCP[178] = 32;
		ECG_SCP[179] = 0;		//NULL terminator
		//---------SCP-ECG protocol revision number: v1.3
		ECG_SCP[180] = 13;
		//---------SCP-ECG Protocol Compatibility Level
		ECG_SCP[181] = (byte) 0xa0;//160
		//----------Language Support Code
		ECG_SCP[182] = 0;
		//----------Capabilities of the ECG Device
		ECG_SCP[183] = (byte) 0x80;
		//----------AC Mains Frequency Environment
		ECG_SCP[184] = 1;
		//----------(20 to 35) Reserved for future use (16 byte)
		for (i=0; i<16; i++)
		{
			ECG_SCP[185+i] = 0;
		}
		//---------Length of the string for Analysing Program Revision Number
		ECG_SCP[201] = 1;
		//----------Character String: Analysing Program Revision Number.
		ECG_SCP[202] = 0;	//NULL terminated.
		//----------Character string: Serial number of the Acquisition Device.
		ECG_SCP[203] = 49;
		ECG_SCP[204] = 50;
		ECG_SCP[205] = 56;
		ECG_SCP[206] = 0;	//NULL terminated.
		//----------Character string: Acquisition device system software identifier.
		ECG_SCP[207] = 1;
		ECG_SCP[208] = 0;	//NULL terminated.
		//----------Character string: Acquisition device SCP implementation software identifier
		ECG_SCP[209] = 1;
		ECG_SCP[210] = 0;	//NULL terminated.
		//-----------Character string: Manufacturer of the Acquisition Device.
		ECG_SCP[211] = 1;
		ECG_SCP[212] = 0;	//NULL terminated.
		//Header Field : 25 Date of Acquisition
		//Field 25 Tag (1 byte)
		ECG_SCP[213] = 25;
		//Field 25 Length (2 byte)
		ECG_SCP[214] = 4;	//low byte
		ECG_SCP[215] = 0;	//high byte
		//Field 25 Value 
		//------------year
		ECG_SCP[216] = (byte) (mYear&0xff);	//low byte
		ECG_SCP[217] = (byte) ((mYear<<8)&0xff);	//high byte
		//------------month
		ECG_SCP[218] = (byte) mMonth;	//low byte
		//------------day
		ECG_SCP[219] = (byte) mDay;	//low byte
		//Header Field : 26 Time of Acquisition
		//Field 26 Tag (1 byte)
		ECG_SCP[220] = 26;
		//Field 26 Length (2 byte)
		ECG_SCP[221] = 3;	//low byte
		ECG_SCP[222] = 0;	//high byte
		//Field 26 Value 
		//------------hour
		ECG_SCP[223] = 12;	//low byte
		//------------minute
		ECG_SCP[224] = 12;	//low byte
		//------------second
		ECG_SCP[225] = 4;	//low byte
		//Header Field : 255 The End
		//Field 255 Tag (1 byte)
		ECG_SCP[226] = (byte) 0xff;//255
		//Field 255 Length (2 byte)
		ECG_SCP[227] = 0;	//low byte
		ECG_SCP[228] = 0;	//high byte
		//CRC of Section 1
		tmp_crc = (short) 0xFFFF;
		for (i=144; i<=228; i++)
		{
			tmp_crc = SCP_CRC_Calculate(ECG_SCP[i], tmp_crc);
		}
		ECG_SCP[142] = (byte) (tmp_crc & 0xFF);
		ECG_SCP[143] = (byte) ((tmp_crc>>8) & 0xFF);
		/************The End of SECTION 1*******************/

/*************SECTION 3*******************/
		//Section 3 crc (2 byte)
		ECG_SCP[229] = 0;
		ECG_SCP[230] = 0;
		//Section 3 ID (2 byte)
		ECG_SCP[231] = 3;	//low byte
		ECG_SCP[232] = 0;		//high byte
		//Section 3 Length (4 byte)
		ECG_SCP[233] = 27;	//low byte
		ECG_SCP[234] = 0;
		ECG_SCP[235] = 0;
		ECG_SCP[236] = 0;	//high byte
		//Version (1 byte)
		ECG_SCP[237] = 0x0D;		//Version: v1.3
		//Protocol Version (1 byte)
		ECG_SCP[238] = 0x0D;		//Version: v1.3
		//Reserved  (6 byte)
		ECG_SCP[239] = 0;
		ECG_SCP[240] = 0;
		ECG_SCP[241] = 0;
		ECG_SCP[242] = 0;
		ECG_SCP[243] = 0;
		ECG_SCP[244] = 0;
		//Number of leads enclosed (1 byte)
		ECG_SCP[245] = 1;
		//flag (1 byte)
		ECG_SCP[246] = 12;
		/***Detail for first lead***/
		//Starting sample number (4 byte)
		ECG_SCP[247] = 1;		//low byte
		ECG_SCP[248] = 0;
		ECG_SCP[249] = 0;
		ECG_SCP[250] = 0;		//high byte
		//Ending sample number (4 byte)
		ECG_SCP[251] = 0x28;		//low byte (9000 point)
		ECG_SCP[252] = 0x23;
		ECG_SCP[253] = 0;
		ECG_SCP[254] = 0;		//high byte
		//Lead identification (1 byte)
		ECG_SCP[255] = 1;
		/***************************/
		//CRC of Section 0
		tmp_crc = (short) 0xFFFF;
		for (i=231; i<=255; i++)
		{
			tmp_crc = SCP_CRC_Calculate(ECG_SCP[i], tmp_crc);
		}
		ECG_SCP[229] = (byte) (tmp_crc & 0xFF);
		ECG_SCP[230] = (byte) ((tmp_crc>>8) & 0xFF);
		/*************The End of Section 3**************/


/*************SECTION 6*******************/
		//Section 6 crc (2 byte)
		ECG_SCP[256] = 0;
		ECG_SCP[257] = 0;
		//Section 6 ID (2 byte)
		ECG_SCP[258] = 6;	//low byte
		ECG_SCP[259] = 0;		//high byte
		//Section 6 Length (4 byte)
		ECG_SCP[260] = 0x40;	//low byte
		ECG_SCP[261] = 0x23;
		ECG_SCP[262] = 0;
		ECG_SCP[263] = 0;	//high byte
		//Version (1 byte)
		ECG_SCP[264] = 0x0D;		//Version: v1.3
		//Protocol Version (1 byte)
		ECG_SCP[265] = 0x0D;		//Version: v1.3
		//Reserved  (6 byte)
		ECG_SCP[266] = 0;
		ECG_SCP[267] = 0;
		ECG_SCP[268] = 0;
		ECG_SCP[269] = 0;
		ECG_SCP[270] = 0;
		ECG_SCP[271] = 0;

		//AVM (2 byte)  969
		ECG_SCP[272] = 0x69; //0X05;
		ECG_SCP[273] = 0x09;//0XD;
		//The Sample Time Interval (2 byte) 6666
//		ECG_SCP[274] = 0x6;		//150hz
//		ECG_SCP[275] = 0xD;
		ECG_SCP[274] = 0x0a;		//150hz
		ECG_SCP[275] = 0x1a;
		//the encoding of the sample data (1 byte)
		ECG_SCP[276] = 0;
		//how rhythm data is compressed
		ECG_SCP[277] = 0;
		//number of bytes for the first lead (2 byte)
		ECG_SCP[278] = 0x28;
		ECG_SCP[279] = 0x23;
		// the data of the first lead (4500 point: 9000 byte sign char type)
		for (i=0; i<4500; i++)
		{
			ECG_SCP[280 + 2*i] = (byte) (ecg_dat[i] & 0xFF);
			ECG_SCP[280 + 2*i + 1] = (byte) ((ecg_dat[i] & 0xFF00) >> 8);
		}
		//CRC of Section 6
		tmp_crc = (short) 0xFFFF;
		for (i=258; i<=9279; i++)
		{
			tmp_crc = SCP_CRC_Calculate(ECG_SCP[i], tmp_crc);
		}
		ECG_SCP[256] = (byte) (tmp_crc & 0xFF);
		ECG_SCP[257] = (byte) ((tmp_crc>>8) & 0xFF);
		
		//Packet CRC
		tmp_crc = (short) 0xFFFF;
		for (i=2; i<=9279; i++)
		{
			tmp_crc = SCP_CRC_Calculate(ECG_SCP[i], tmp_crc);
		}
		ECG_SCP[0] = (byte) (tmp_crc & 0xFF);
		ECG_SCP[1] = (byte) ((tmp_crc>>8) & 0xFF);		
		
		writeFile(ECG_SCP);	
	}
		
	private short SCP_CRC_Calculate(byte data,int crc){
		byte crc_h;
		byte crc_l;
		byte a,b;
		byte tmp_h,tmp_l;

		crc_h = (byte) ((crc >> 8) & 0xff);
		crc_l = (byte) (crc & 0xff);

		a = data;
		a = (byte) (a ^ crc_h);
		crc_h = a;
		a = (byte) (a >> 4);
		a = (byte) (a ^ crc_l);

		crc_h = crc_l;
		crc_l = a;

		// a 循环右移4位
		tmp_h = (byte) ((a & 0xf0) >> 4);
		tmp_l = (byte) (a & 0x0f);
		a = (byte) ((tmp_l << 4) | tmp_h);

		b = a;

		// a 循环左移 1位
		tmp_h = (byte) (((a & 0x7f) << 1) & 0xfe);
		tmp_l = (byte) (((a & 0x80) >> 7) & 0x01);
		a = (byte) (tmp_h | tmp_l);

		a = (byte) (a & 0x1f);
		crc_h = (byte) (a ^ crc_h);
		a = (byte) (b & 0xf0);
		crc_h = (byte) (a ^ crc_h);

		// b 循环左移一位
		tmp_h = (byte) (((b & 0x7f) << 1) & 0xfe);
		tmp_l = (byte) (((b & 0x80) >> 7) & 0x01);
		b = (byte) (tmp_h | tmp_l);

		b = (byte) (b & 0xE0);
		crc_l = (byte) (b ^ crc_l);

		return (short) (((crc_h<< 8)|(crc_l))&0xffff);
	}

    private void writeFile(byte[] datas) {  	
		File dir = new File(mFilePath);
        if (!dir.exists()) {
        	dir.mkdir(); //创建文件夹
        }
        
        mFilePath = mFilePath+"/"+mFileName;
        
        File file = new File(mFilePath);
		if (file.exists()) {		 
			file.delete();
		}
		try {
			file.createNewFile();//在文件夹中创建文件
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 			
        try {  
            FileOutputStream out = new FileOutputStream(file);
            out.write(datas);  
            out.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }       
		      
    }  
    
     
}
