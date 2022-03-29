package com.example.cpcl_test_v1;

import android.content.Intent;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;

import java.time.Period;

/**
 * Created by DTYUNLU on 14.02.2018.
 */

public class PrintUtils {

    public static PrintUtils mPrintUtils;
    public static Globalvars globalvars;
    private int pageWidth, leftMargin, rightMargin;
    private String bluetoothAddr;
    private BluetoothConnection conn;
    ZebraPrinter mPrinter ;
    String vehicle_type, rate;
    String[] splitted_transactions;
    int penalty_overnight=0;
    int penalty_lostticket=0;

    public static synchronized PrintUtils getInstance()
    {
        if (mPrintUtils == null)
        {
            mPrintUtils = new PrintUtils();
        }
        return mPrintUtils;
    }

    public PrintUtils() {
        pageWidth=500;
        leftMargin = 20;
        rightMargin = 20;
    }

    public void setPrinter( String _bluetoothAddr)
    {
        bluetoothAddr = _bluetoothAddr;
        conn = new BluetoothConnection(bluetoothAddr);
        if(!conn.isConnected())
        {
            try {
                conn.open();
                mPrinter = ZebraPrinterFactory.getInstance(conn);
//                mPrinter.sendCommand("! U1 setvar \"device.languages\" \"]\"\r\n");
//                mPrinter.sendCommand("~jc^xa^jus^xz");
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void freePriner()
    {
        if(conn.isConnected())
        {
            try {
                Thread.sleep(500);
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void printNavigator(String _toNav)
    {
        if (_toNav.equals(PrintFormats.BARCODE.name())) {
            if (globalvars.store_name.equalsIgnoreCase("ALTA")) {
                splitted_transactions = globalvars.get("transactions").split(",");
//                if (globalvars.get("transaction_type").equals("reprint_coupon")) {
//                    reprint_ticket_alta();
//                } else if (globalvars.get("transaction_type").equals("print_penalty")) {
//                    print_penalty_alta();
//                } else if (globalvars.get("transaction_type").equals("print_coupon")) {
//                    print_ticket_alta();
//                } else if (globalvars.get("transaction_type").equals("reprint_penalty")) {
//                    reprint_penalty_alta();
//                } else {
//                }
            } else {
                splitted_transactions = globalvars.get("transactions").split(",");
                if (globalvars.get("transaction_type").equals("reprint_coupon")) {
                    reprint_ticket_pm();
                } else if (globalvars.get("transaction_type").equals("print_penalty")) {
                    penalty_overnight= Integer.parseInt(splitted_transactions[7]);
                    penalty_lostticket= Integer.parseInt(splitted_transactions[11]);
                    if(penalty_overnight!=0 && penalty_lostticket==0){
                        print_penalty_overnight();
                    }
                    else if(penalty_lostticket!=0 && penalty_overnight==0){
                        print_penalty_lostTicket();
                    }
                    else if(penalty_overnight!=0 && penalty_lostticket !=0){
                        print_penalty_pm();
                    }else{
                        print_charge_pm();
                    }
                } else if (globalvars.get("transaction_type").equals("print_coupon")) {
                        print_ticket_pm();
                } else if (globalvars.get("transaction_type").equals("reprint_penalty")) {
                    penalty_overnight= Integer.parseInt(splitted_transactions[7]);
                    penalty_lostticket= Integer.parseInt(splitted_transactions[11]);
                    if(penalty_overnight!=0 && penalty_lostticket==0){
                        reprint_penalty_overnight();
                    }
                    else if(penalty_lostticket!=0 && penalty_overnight==0){
                        reprint_penalty_lostTicket();
                    }
                    else if(penalty_overnight!=0 && penalty_lostticket !=0){
                        reprint_penalty_pm();
                    }else{
                        reprint_charge_pm();
                    }
                } else if(globalvars.get("transaction_type").equals("print_delinquent")) {
                    print_delinquent();
                }else{

                }
            }
        }
    }

    private void print_delinquent() {
        String datenow      = splitted_transactions[0];
        String plateno      = splitted_transactions[1];
        String vtype        = splitted_transactions[2];
        String ticketNo     = splitted_transactions[3];
        String transCode    = splitted_transactions[4];
        String datetime     = splitted_transactions[5];
        try
        {
            if(vtype.equals("100"))
            {
                vehicle_type = "4 WHEELED";
                rate = "20";
            }
            else
            {
                vehicle_type = "2 WHEELED";
                rate = "10";
            }
            mPrinter.sendCommand("! U1 setvar \"device.languages\" \"zpl\"\r\n");
            mPrinter.sendCommand("^XA");
            mPrinter.sendCommand("^LL720");
            mPrinter.sendCommand("^POI");
            mPrinter.sendCommand("^A0N,30,22^FB550,15,0,C,0^FD\\&PLAZA MARCELA\\&" +
                    "Pamaong, Corner Belderol St, Tagbilaran City, Bohol\\&" +
                    "Prop: PLAZA MARCELA\\&" +
                    "TIN: 000-254-327-009\\&" +
                    "\\&BLACKLISTED\\&BASEMENT\\&" +
                    "PAY PARKING BILLING STATEMENT\\&" +
                    "********************************************\\&^FS");
            mPrinter.sendCommand("^FO0,290^ABN,21,10^FB550,15,0,L,0^FDDate/Time^FS");
            mPrinter.sendCommand("^FO220,290^ABN,21,10^FB550,15,0,L,0^FD: "+datenow+"^FS");
            mPrinter.sendCommand("^FO0,310^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,340^ABN,21,10^FB550,15,0,L,0^FDPlate No.^FS");
            mPrinter.sendCommand("^FO220,340^ABN,21,10^FB550,15,0,L,0^FD: "+plateno+"^FS");
            mPrinter.sendCommand("^FO0,370^ABN,21,10^FB550,15,0,L,0^FDNo. of Wheels^FS");
            mPrinter.sendCommand("^FO220,370^ABN,21,10^FB550,15,0,L,0^FD: "+vehicle_type+"^FS");
            mPrinter.sendCommand("^FO0,400^ABN,21,10^FB550,15,0,L,0^FDTicket No.^FS");
            mPrinter.sendCommand("^FO220,400^ABN,21,10^FB550,15,0,L,0^FD: "+ticketNo+"^FS");
            mPrinter.sendCommand("^FO0,430^ABN,21,10^FB550,15,0,L,0^FDTransaction Code^FS");
            mPrinter.sendCommand("^FO220,430^ABN,21,10^FB550,15,0,L,0^FD: "+transCode+"^FS");
            mPrinter.sendCommand("^FO0,460^ABN,21,10^FB550,15,0,L,0^FDDate/Time^FS");
            mPrinter.sendCommand("^FO220,460^ABN,21,10^FB550,15,0,L,0^FD: "+datetime+"^FS");
            mPrinter.sendCommand("^FO0,490^ABN,21,10^FB550,15,0,L,0^FDDate Time out^FS");
            mPrinter.sendCommand("^FO220,490^ABN,21,10^FB550,15,0,L,0^FD: ---^FS");
            mPrinter.sendCommand("^FO0,520^ABN,21,10^FB550,15,0,L,0^FDTotal No. of Hrs^FS");
            mPrinter.sendCommand("^FO220,520^ABN,21,10^FB550,15,0,L,0^FD: ---^FS");
            mPrinter.sendCommand("^FO0,550^ABN,21,10^FB550,15,0,L,0^FDTotal No. of Excess^FS");
            mPrinter.sendCommand("^FO220,550^ABN,21,10^FB550,15,0,L,0^FD: ---^FS");
            mPrinter.sendCommand("^FO0,570^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,600^ABN,21,10^FB550,15,0,L,0^FDPenalty^FS");
            mPrinter.sendCommand("^FO220,600^ABN,21,10^FB550,15,0,L,0^FD: Php 500.00^FS");
            mPrinter.sendCommand("^FO0,620^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,650^A0N,30,22^FB550,15,0,C,0^FDPLEASE ASK FOR AN OFFICIAL RECEIPT^FS");
//            mPrinter.sendCommand("^FO0,690^A0N,30,20^FB550,15,0,L,0^FDPenalty^FS");
//            mPrinter.sendCommand("^FO220,690^ABN,21,10^FB550,15,0,L,0^FD: PHP "+amountDue+".00^FS");
//            mPrinter.sendCommand("^FO0,710^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
//            mPrinter.sendCommand("^FO0,740^A0N,30,22^FB550,15,0,C,0^FDPLEASE ASK FOR AN OFFICIAL RECEIPT^FS");
            mPrinter.sendCommand("^XZ");

        } catch (Exception e) {
            // Handle communications error here.
            e.printStackTrace();
        }
    }
    private void reprint_delinquent() {
        String datenow=splitted_transactions[0];
        String plateno=splitted_transactions[1];
        String vtype = splitted_transactions[2];
        String ticketNo=splitted_transactions[3];
        String transCode=splitted_transactions[4];
        String datetime=splitted_transactions[5];
        try
        {
            if(vtype.equals("100"))
            {
                vehicle_type = "4 WHEELED";
                rate = "20";
            }
            else
            {
                vehicle_type = "2 WHEELED";
                rate = "10";
            }
            mPrinter.sendCommand("! U1 setvar \"device.languages\" \"zpl\"\r\n");
            mPrinter.sendCommand("^XA");
            mPrinter.sendCommand("^LL720");
            mPrinter.sendCommand("^POI");
            mPrinter.sendCommand("^A0N,30,22^FB550,15,0,C,0^FD\\&PLAZA MARCELA\\&" +
                    "Pamaong, Corner Belderol St, Tagbilaran City, Bohol\\&" +
                    "Prop: PLAZA MARCELA\\&" +
                    "TIN: 000-254-327-009\\&" +
                    "\\&BLACKLISTED\\&BASEMENT\\&" +
                    "PAY PARKING BILLING STATEMENT\\&" +
                    "********************************************\\&^FS");
            mPrinter.sendCommand("^FO0,290^ABN,21,10^FB550,15,0,L,0^FDDate/Time^FS");
            mPrinter.sendCommand("^FO220,290^ABN,21,10^FB550,15,0,L,0^FD: "+datenow+"^FS");
            mPrinter.sendCommand("^FO0,310^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,340^ABN,21,10^FB550,15,0,L,0^FDPlate No.^FS");
            mPrinter.sendCommand("^FO220,340^ABN,21,10^FB550,15,0,L,0^FD: "+plateno+"^FS");
            mPrinter.sendCommand("^FO0,370^ABN,21,10^FB550,15,0,L,0^FDNo. of Wheels^FS");
            mPrinter.sendCommand("^FO220,370^ABN,21,10^FB550,15,0,L,0^FD: "+vehicle_type+"^FS");
            mPrinter.sendCommand("^FO0,400^ABN,21,10^FB550,15,0,L,0^FDTicket No.^FS");
            mPrinter.sendCommand("^FO220,400^ABN,21,10^FB550,15,0,L,0^FD: "+ticketNo+"^FS");
            mPrinter.sendCommand("^FO0,430^ABN,21,10^FB550,15,0,L,0^FDTransaction Code^FS");
            mPrinter.sendCommand("^FO220,430^ABN,21,10^FB550,15,0,L,0^FD: "+transCode+"^FS");
            mPrinter.sendCommand("^FO0,460^ABN,21,10^FB550,15,0,L,0^FDDate/Time^FS");
            mPrinter.sendCommand("^FO220,460^ABN,21,10^FB550,15,0,L,0^FD: "+datetime+"^FS");
            mPrinter.sendCommand("^FO0,490^ABN,21,10^FB550,15,0,L,0^FDDate Time out^FS");
            mPrinter.sendCommand("^FO220,490^ABN,21,10^FB550,15,0,L,0^FD: ---^FS");
            mPrinter.sendCommand("^FO0,520^ABN,21,10^FB550,15,0,L,0^FDTotal No. of Hrs^FS");
            mPrinter.sendCommand("^FO220,520^ABN,21,10^FB550,15,0,L,0^FD: ---^FS");
            mPrinter.sendCommand("^FO0,550^ABN,21,10^FB550,15,0,L,0^FDTotal No. of Excess^FS");
            mPrinter.sendCommand("^FO220,550^ABN,21,10^FB550,15,0,L,0^FD: ---^FS");
            mPrinter.sendCommand("^FO0,570^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,600^ABN,21,10^FB550,15,0,L,0^FDPenalty^FS");
            mPrinter.sendCommand("^FO220,600^ABN,21,10^FB550,15,0,L,0^FD: Php 500.00^FS");
            mPrinter.sendCommand("^FO0,620^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,650^A0N,30,22^FB550,15,0,C,0^FDPLEASE ASK FOR AN OFFICIAL RECEIPT^FS");
            mPrinter.sendCommand("^XZ");

        } catch (Exception e) {
            // Handle communications error here.
            e.printStackTrace();
        }
    }


    private void printTest() {
        try{
            mPrinter.sendCommand("! U1 setvar \"device.languages\" \"zpl\"\r\n");
            mPrinter.sendCommand("^XA");
            mPrinter.sendCommand("^LL75");
            mPrinter.sendCommand("^POI");
            mPrinter.sendCommand("^FO0,10^ABN,21,10^FB550,15,0,C,0^FD^FS");
            mPrinter.sendCommand("^XZ");
        }catch (Exception e){

        }

    }

    //     <------------- Plaza Marcela ------------->
    public void print_ticket_pm()
    {
        try
        {
            if(splitted_transactions[6].equals("100"))
            {
                vehicle_type = "4 WHEELED";
            }
            else
            {
                vehicle_type = "2 WHEELED";
            }
            mPrinter.sendCommand("! U1 setvar \"device.languages\" \"zpl\"\r\n");
            mPrinter.sendCommand("^XA");
            mPrinter.sendCommand("^LL1400");
            mPrinter.sendCommand("^POI");
            mPrinter.sendCommand("^A0N,30,22^FB550,15,0,C,0^FD\\&PLAZA MARCELA\\&" +
                    "Pamaong, Corner Belderol St, Tagbilaran City, Bohol\\&" +
                    "Prop: PLAZA MARCELA\\&" +
                    "TIN: 000-254-327-009\\&" +
                    "\\&B A S E M E N T  P A R K I N G  T I C K E T\\&" +
                    "********************************************\\&^FS");
            mPrinter.sendCommand("^FO0,230^ABN,21,10^FB550,15,0,L,0^FDPlate No.^FS");
            mPrinter.sendCommand("^FO110,230^ABN,21,10^FD: "+ splitted_transactions[2]+"^FS");
            mPrinter.sendCommand("^FO0,270^ABN,21,10^FB550,15,0,L,0^FDType^FS");
            mPrinter.sendCommand("^FO110,270^ABN,21,10^FB550,15,0,L,0^FD: "+ vehicle_type+"^FS");
            mPrinter.sendCommand("^FO0,310^ABN,21,10^FB550,15,0,L,0^FDTicket No.^FS");
            mPrinter.sendCommand("^FO110,310^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[0]+"^FS");
            mPrinter.sendCommand("^FO0,350^ABN,21,10^FB550,15,0,L,0^FDTrans code^FS");
            mPrinter.sendCommand("^FO110,350^ABN,21,10^FB550,15,0,L,0^FD: "+ splitted_transactions[1]+"^FS");
            mPrinter.sendCommand("^FO0,390^ABN,21,10^FB550,15,0,L,0^FDDate^FS");
            mPrinter.sendCommand("^FO110,390^ABN,21,10^FB550,15,0,L,0^FD: "+ splitted_transactions[3]+"^FS");
            mPrinter.sendCommand("^FO0,430^ABN,21,10^FB550,15,0,L,0^FDTime^FS");
            mPrinter.sendCommand("^FO110,430^ABN,21,10^FB550,15,0,L,0^FD: "+ splitted_transactions[4]+"^FS");
            mPrinter.sendCommand("^FO0,454^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,477^A0N,30,20^FB550,15,0,L,0^FDParking Rules: ^FS");
            //System.out.println("Plate Number: "+splitted_transactions[2]);
            //Rule No.1
//            mPrinter.sendCommand("^FO0,535^ADN,10,10^FB550,15,0,L,0^FD1)The PLAZA MARCELA BASEMENT PARKING AREA is^FS");
//            mPrinter.sendCommand("^FO26,557^ADN,10,10^FDintended for our valued mall Shoppers'^FS");
//            mPrinter.sendCommand("^FO26,579^ADN,10,10^FDConvenience.^FS");
//            //Rule no. 2
//            mPrinter.sendCommand("^FO0,603^ADN,10,10^FB550,15,0,L,0^FD2)The PLAZA MARCELA BASEMENT PARKING AREA is ^FS");
//            mPrinter.sendCommand("^FO26,625^ADN,10,10^FDopen for our Valued Mall Shoppers^FS");
//            mPrinter.sendCommand("^FO26,645^ADN,10,10^FDfrom 8:00AM to 7:00PM.^FS");
//            //Rule no. 3
//            mPrinter.sendCommand("^FO0,669^ADN,10,10^FB550,15,0,L,0^FD3)Customers driving 2-Wheeled Vehicles may^FS");
//            mPrinter.sendCommand("^FO26,691^ADN,10,10^FB550,15,0,L,0^FDenjoy 2 hours of free parking. The amount^FS");
//            mPrinter.sendCommand("^FO26,713^ADN,10,10^FB550,15,0,L,0^FDPHP10.00 will be charged to the Customer^FS");
//            mPrinter.sendCommand("^FO26,735^ADN,10,10^FB550,15,0,L,0^FDfor every succeeding hour. While Customer^FS");
//            mPrinter.sendCommand("^FO26,757^ADN,10,10^FB550,15,0,L,0^FDCustomers driving 3 or 4-Wheeled Vehicles^FS");
//            mPrinter.sendCommand("^FO26,779^ADN,10,10^FB550,15,0,L,0^FDmay likewise enjoy 2 hours of free parking.^FS");
//            mPrinter.sendCommand("^FO26,801^ADN,10,10^FB550,15,0,L,0^FDThe amount of PHP 20.00 will be charged to^FS");
//            mPrinter.sendCommand("^FO26,823^ADN,10,10^FB550,15,0,L,0^FDthe Customer for every succeeding hour. In^FS");
//            mPrinter.sendCommand("^FO26,845^ADN,10,10^FB550,15,0,L,0^FDboth instances, a fraction of an hour parked^FS");
//            mPrinter.sendCommand("^FO26,867^ADN,10,10^FB550,15,0,L,0^FDshall be counted as 1 hour.^FS");
//            //Rule no. 4
//            mPrinter.sendCommand("^FO0,891^ADN,10,10^FB550,15,0,L,0^FD4)Customers who losses their parking tickets^FS");
//            mPrinter.sendCommand("^FO26,913^ADN,10,10^FB550,15,0,L,0^FDshall pay the amount of TWO HUNDRED FIFTY^FS");
//            mPrinter.sendCommand("^FO26,935^ADN,10,10^FB550,15,0,L,0^FDPESOS (PHP 250.00). Customers who losses^FS");
//            mPrinter.sendCommand("^FO26,957^ADN,10,10^FB550,15,0,L,0^FDtheir parking tickets may be subject to^FS");
//            mPrinter.sendCommand("^FO26,979^ADN,10,10^FB550,15,0,L,0^FDinvestigation by the Mall Security. The^FS");
//            mPrinter.sendCommand("^FO26,1001^ADN,10,10^FB550,15,0,L,0^FDconcerned driver must present his/her LTO^FS");
//            mPrinter.sendCommand("^FO26,1023^ADN,10,10^FB550,15,0,L,0^FDissued Driver's License, Certificate of^FS");
//            mPrinter.sendCommand("^FO26,1045^ADN,10,10^FB550,15,0,L,0^FDRegistration and the current year Official^FS");
//            mPrinter.sendCommand("^FO26,1067^ADN,10,10^FB550,15,0,L,0^FDReceipt. Failure to present said documents^FS");
//            mPrinter.sendCommand("^FO26,1089^ADN,10,10^FB550,15,0,L,0^FDmay be referred to the TCPO by the Mall^FS");
//            mPrinter.sendCommand("^FO26,1111^ADN,10,10^FB550,15,0,L,0^FDSecurity.^FS");
//            //Rule no. 5
//            mPrinter.sendCommand("^FO0,1135^ADN,10,10^FB550,15,0,L,0^FD5)All Customers shall FOLLOW APPLICABLE^FS");
//            mPrinter.sendCommand("^FO26,1157^ADN,10,10^FB550,15,0,L,0^FDLAWS and are advised to STRICTLY ADHERE TO^FS");
//            mPrinter.sendCommand("^FO26,1179^ADN,10,10^FB550,15,0,L,0^FDPARKING REGULATIONS to ensure orderly flow of^FS");
//            mPrinter.sendCommand("^FO26,1201^ADN,10,10^FB550,15,0,L,0^FDtraffic and safety of the public.^FS");
//            //Rule no. 6
//            mPrinter.sendCommand("^FO0,1225^ADN,10,10^FB550,15,0,L,0^FD6)Upon entering the parking area, Customers^FS");
//            mPrinter.sendCommand("^FO26,1247^ADN,10,10^FB550,15,0,L,0^FDallow their vehicles to be subjected to^FS");
//            mPrinter.sendCommand("^FO26,1269^ADN,10,10^FB550,15,0,L,0^FDexternal inspection by the company's^FS");
//            mPrinter.sendCommand("^FO26,1291^ADN,10,10^FB550,15,0,L,0^FDauthorized personnel.^FS");
//            //Rule no. 7
//            mPrinter.sendCommand("^FO0,1315^ADN,10,10^FB550,15,0,L,0^FD7)Closed Circuit Televisions (CCTV) cameras^FS");
//            mPrinter.sendCommand("^FO26,1337^ADN,10,10^FB550,15,0,L,0^FDare in full operation in various locations^FS");
//            mPrinter.sendCommand("^FO26,1359^ADN,10,10^FB550,15,0,L,0^FDof the parking area.^FS");
//            //Rule no. 8
//            mPrinter.sendCommand("^FO0,1383^ADN,10,10^FB550,15,0,L,0^FD8)The Management of Plaza Marcela is NOT^FS");
//            mPrinter.sendCommand("^FO26,1405^ADN,10,10^FB550,15,0,L,0^FDLIABLE for any damages to or loss of vehicle^FS");
//            mPrinter.sendCommand("^FO26,1427^ADN,10,10^FB550,15,0,L,0^FDor any of its accessories or articles left^FS");
//            mPrinter.sendCommand("^FO26,1449^ADN,10,10^FB550,15,0,L,0^FDtherein.^FS");
//            //Rule no. 9
//            mPrinter.sendCommand("^FO0,1473^ADN,10,10^FB550,15,0,L,0^FD9)All Customers are required to EXERCISE^FS");
//            mPrinter.sendCommand("^FO26,1495^ADN,10,10^FB550,15,0,L,0^FDPROPER CARE AND DILIGENCE for the safety of^FS");
//            mPrinter.sendCommand("^FO26,1517^ADN,10,10^FB550,15,0,L,0^FDtheir person and their property.^FS");
//            //Rule no. 10
//            mPrinter.sendCommand("^FO0,1541^ADN,10,10^FB550,15,0,L,0^FD10)The Customer shall be liable for any^FS");
//            mPrinter.sendCommand("^FO26,1563^ADN,10,10^FB550,15,0,L,0^FDinjury or damage which he/she may cause to^FS");
//            mPrinter.sendCommand("^FO26,1585^ADN,10,10^FB550,15,0,L,0^FDperson or property inside the carpark which^FS");
//            mPrinter.sendCommand("^FO26,1607^ADN,10,10^FB550,15,0,L,0^FDmay include the Company's properties or^FS");
//            mPrinter.sendCommand("^FO26,1629^ADN,10,10^FB550,15,0,L,0^FDits facilities or any other person's vehicle^FS");
//            //Rule no. 11
//            mPrinter.sendCommand("^FO0,1653^ADN,10,10^FB550,15,0,L,0^FD11)All Customers are prohibited from^FS");
//            mPrinter.sendCommand("^FO26,1675^ADN,10,10^FB550,15,0,L,0^FDperforming illegal and/or indecent acts in^FS");
//            mPrinter.sendCommand("^FO26,1697^ADN,10,10^FB550,15,0,L,0^FDthe parking area. Anyone caught committing^FS");
//            mPrinter.sendCommand("^FO26,1719^ADN,10,10^FB550,15,0,L,0^FDsuch acts shall be dealt with the proper^FS");
//            mPrinter.sendCommand("^FO26,1741^ADN,10,10^FB550,15,0,L,0^FDauthorities^FS");
//            //Rule no. 12
//            mPrinter.sendCommand("^FO0,1765^ADN,10,10^FB550,15,0,L,0^FD12)Customer vehicle should occupy ONE (1)^FS");
//            mPrinter.sendCommand("^FO26,1787^ADN,10,10^FB550,15,0,L,0^FDPARKING SLOT ONLY.^FS");
//            //Rule no. 13
//            mPrinter.sendCommand("^FO0,1811^ADN,10,10^FB550,15,0,L,0^FD13)Parking overnight is STRICTLY PROHIBITED. ^FS");
//            mPrinter.sendCommand("^FO26,1833^ADN,10,10^FB550,15,0,L,0^FDA penalty of FIVE HUNDRED PESOS (PHP 500.00)^FS");
//            mPrinter.sendCommand("^FO26,1855^ADN,10,10^FB550,15,0,L,0^FDwill be imposed on the violators for each^FS");
//            mPrinter.sendCommand("^FO26,1877^ADN,10,10^FB550,15,0,L,0^FDnight. The Management has the right to^FS");
//            mPrinter.sendCommand("^FO26,1899^ADN,10,10^FB550,15,0,L,0^FDrestrict the vehicle from leaving unless the^FS");
//            mPrinter.sendCommand("^FO26,1921^ADN,10,10^FB550,15,0,L,0^FDpenalty has been paid.^FS");
//            //Rule no. 14
//            mPrinter.sendCommand("^FO0,1945^ADN,10,10^FB550,15,0,L,0^FD14)Vehicles parked overnight that may be^FS");
//            mPrinter.sendCommand("^FO26,1967^ADN,10,10^FB550,15,0,L,0^FDfound suspicious by the Management or remain^FS");
//            mPrinter.sendCommand("^FO26,1989^ADN,10,10^FB550,15,0,L,0^FDto be unattended may be subjected to an^FS");
//            mPrinter.sendCommand("^FO26,2011^ADN,10,10^FB550,15,0,L,0^FDadditional external inspection or a report to^FS");
//            mPrinter.sendCommand("^FO26,2033^ADN,10,10^FB550,15,0,L,0^FDthe Philippine National Police (PNP).^FS");
//            //Rule no. 15
//            mPrinter.sendCommand("^FO0,2057^ADN,10,10^FB550,15,0,L,0^FD15)Acceptance of the parking ticket^FS");
//            mPrinter.sendCommand("^FO26,2079^ADN,10,10^FB550,15,0,L,0^FDconstitutes acknowledgement by the holder^FS");
//            mPrinter.sendCommand("^FO26,2101^ADN,10,10^FB550,15,0,L,0^FDthat he/she has read carefully, understood^FS");
//            mPrinter.sendCommand("^FO26,2123^ADN,10,10^FB550,15,0,L,0^FDfully and will willingly comply with^FS");
//            mPrinter.sendCommand("^FO26,2145^ADN,10,10^FB550,15,0,L,0^FDparking rules^FS");
//            //Rule no. 16
//            mPrinter.sendCommand("^FO0,2169^ADN,10,10^FB550,15,0,L,0^FD16)The services provided by Plaza Marcela^FS");
//            mPrinter.sendCommand("^FO26,2191^ADN,10,10^FB550,15,0,L,0^FDManagement are governed by these 'Parking^FS");
//            mPrinter.sendCommand("^FO26,2213^ADN,10,10^FB550,15,0,L,0^FDCondition'. By availing the parking area,^FS");
//            mPrinter.sendCommand("^FO26,2235^ADN,10,10^FB550,15,0,L,0^FDCustomers expressly agree, adhere,^FS");
//            mPrinter.sendCommand("^FO26,2257^ADN,10,10^FB550,15,0,L,0^FDacknowledge and recognize the conditions set^FS");
//            mPrinter.sendCommand("^FO26,2279^ADN,10,10^FB550,15,0,L,0^FDout above and likewise agree that the^FS");
//            mPrinter.sendCommand("^FO26,2301^ADN,10,10^FB550,15,0,L,0^FDManagement has the right to refuse the^FS");
//            mPrinter.sendCommand("^FO26,2323^ADN,10,10^FB550,15,0,L,0^FDrelease of the Customer's vehicle until^FS");
//            mPrinter.sendCommand("^FO26,2345^ADN,10,10^FB550,15,0,L,0^FDcharges imposed has been paid or settled.^FS");
//            mPrinter.sendCommand("^FO110,2369^FB550,15,0,C,0^BCN,60,Y,N,N,N^FD"+splitted_transactions[1]+"^FS");
//            mPrinter.sendCommand("^XZ");

//            //Rule no. 1
            mPrinter.sendCommand("^FO0,506^ADN,10,10^FB550,15,0,L,0^FD1) Regular parking day starts at 8:00am and^FS");
            mPrinter.sendCommand("^FO32,529^ADN,10,10^FDends at 7:00pm cut-off time^FS");
            //Rule no. 2
            mPrinter.sendCommand("^FO0,553^ADN,10,10^FB550,15,0,L,0^FD2) 2 Wheeled Vehicles will enjoy 2 hours free^FS");
            mPrinter.sendCommand("^FO32,576^ADN,10,10^FDparking and will be charged P10.00 for each^FS");
            mPrinter.sendCommand("^FO32,600^ADN,10,10^FDsucceeding hour.^FS");
            //Rule no. 3
            mPrinter.sendCommand("^FO0,623^ADN,10,10^FB550,15,0,L,0^FD3) 3 to 4 Wheeled Vehicles will enjoy 2 hours^FS");
            mPrinter.sendCommand("^FO32,646^ADN,10,10^FDfree parking and will be charged P20.00 for^FS");
            mPrinter.sendCommand("^FO32,670^ADN,10,10^FDeach succeeding hour.^FS");
            //Rule no. 4
            mPrinter.sendCommand("^FO0,693^ADN,10,10^FB550,15,0,L,0^FD4) A fraction of an hour parked is considered^FS");
            mPrinter.sendCommand("^FO32,716^ADN,10,10^FD1 hour.^FS");
            //Rule no. 5
//            mPrinter.sendCommand("^FO0,784^ADN,10,10^FB550,15,0,L,0^FD5) Upon entry, a consumable coupon must be^FS");
//            mPrinter.sendCommand("^FO32,807^ADN,10,10^FDpaid by the vehicle driver, as follows:^FS");
//            mPrinter.sendCommand("^FO67,830^ADN,10,10^FD> 2 wheels   -  P50.00/park^FS");
//            mPrinter.sendCommand("^FO67,853^ADN,10,10^FD> 3-4 wheels -  P100.00/park^FS");
            //Rule no. 6
            mPrinter.sendCommand("^FO0,740^ADN,10,10^FB550,15,0,L,0^FD5) Loss of parking ticket will be charged^FS");
            mPrinter.sendCommand("^FO32,763^ADN,10,10^FDP250.00 penalty and is subject to an for^FS");
            mPrinter.sendCommand("^FO32,786^ADN,10,10^FDinvestigation by the Mall Security. The^FS");
            mPrinter.sendCommand("^FO32,809^ADN,10,10^FDconcerned driver must present LTO issued^FS");
            mPrinter.sendCommand("^FO32,832^ADN,10,10^FDdrivers license, Certificate of Registration^FS");
            mPrinter.sendCommand("^FO32,855^ADN,10,10^FDand the current year Official Receipt.Failure^FS");
            mPrinter.sendCommand("^FO32,878^ADN,10,10^FDand to present said documents maybe reffered to^FS");
            mPrinter.sendCommand("^FO32,901^ADN,10,10^FDthe TCPO by the Mall Security.^FS");
            //Rule no. 7
            mPrinter.sendCommand("^FO0,925^ADN,10,10^FB550,15,0,L,0^FD6) Vehicles left overnight in the Parking^FS");
            mPrinter.sendCommand("^FO32,948^ADN,10,10^FDAREA (Surface) will be charged a penalty P500.00^FS");
            mPrinter.sendCommand("^FO32,971^ADN,10,10^FDper cut off time violation aside from the^FS");
            mPrinter.sendCommand("^FO32,994^ADN,10,10^FDreg. parking fees accumulated every parking^FS");
            mPrinter.sendCommand("^FO32,1017^ADN,10,10^FDday; and also subject to investigation.^FS");
            //Rule no. 8
            mPrinter.sendCommand("^FO0,1041^ADN,10,10^FB550,15,0,L,0^FD7) Lock your vehicle properly and do not^FS");
            mPrinter.sendCommand("^FO32,1064^ADN,10,10^FDleave your parking ticket inside.^FS");
            //Rule no. 9
            mPrinter.sendCommand("^FO0,1088^ADN,10,10^FB550,15,0,L,0^FD8) The Car Park management is not responsible^FS");
            mPrinter.sendCommand("^FO32,1111^ADN,10,10^FDfor the loss or damage to your parked vehicle^FS");
            mPrinter.sendCommand("^FO32,1134^ADN,10,10^FDits accessories and private properties inside^FS");
            //Rule no. 10
            mPrinter.sendCommand("^FO0,1158^ADN,10,10^FB550,15,0,L,0^FD9) Acceptance of this parking ticket^FS");
            mPrinter.sendCommand("^FO32,1181^ADN,10,10^FDconstitutes acknowledgement by the holder that^FS");
            mPrinter.sendCommand("^FO32,1204^ADN,10,10^FDhe/she has read carefully, understood fully ^FS");
            mPrinter.sendCommand("^FO32,1227^ADN,10,10^FDand will willingly comply with the^FS");
            mPrinter.sendCommand("^FO32,1250^ADN,10,10^FDparking rules.^FS");
            mPrinter.sendCommand("^FO110,1275^FB550,15,0,C,0^BCN,60,Y,N,N,N^FD"+splitted_transactions[1]+"^FS");
            mPrinter.sendCommand("^XZ");
//            mPrinter.sendCommand("^FO0,477^A0N,30,20^FB550,15,0,C,0^FDPLAZA MARCELA BASEMENT PARKING AREA PARKING CONDITIONS^FS");
            //Rule No.1
//            mPrinter.sendCommand("^FO0,535^ADN,10,10^FB550,15,0,L,0^FD1)The PLAZA MARCELA BASEMENT PARKING AREA is^FS");
//            mPrinter.sendCommand("^FO26,557^ADN,10,10^FDintended for our valued mall Shoppers'^FS");
//            mPrinter.sendCommand("^FO26,579^ADN,10,10^FDConvenience.^FS");
//            //Rule no. 2
//            mPrinter.sendCommand("^FO0,603^ADN,10,10^FB550,15,0,L,0^FD2)The PLAZA MARCELA BASEMENT PARKING AREA is ^FS");
//            mPrinter.sendCommand("^FO26,625^ADN,10,10^FDopen for our Valued Mall Shoppers^FS");
//            mPrinter.sendCommand("^FO26,645^ADN,10,10^FDfrom 8:00AM to 7:00PM.^FS");
//            //Rule no. 3
//            mPrinter.sendCommand("^FO0,669^ADN,10,10^FB550,15,0,L,0^FD3)Customers driving 2-Wheeled Vehicles may^FS");
//            mPrinter.sendCommand("^FO26,691^ADN,10,10^FB550,15,0,L,0^FDenjoy 2 hours of free parking. The amount^FS");
//            mPrinter.sendCommand("^FO26,713^ADN,10,10^FB550,15,0,L,0^FDPHP10.00 will be charged to the Customer^FS");
//            mPrinter.sendCommand("^FO26,735^ADN,10,10^FB550,15,0,L,0^FDfor every succeeding hour. While Customer^FS");
//            mPrinter.sendCommand("^FO26,757^ADN,10,10^FB550,15,0,L,0^FDCustomers driving 3 or 4-Wheeled Vehicles^FS");
//            mPrinter.sendCommand("^FO26,779^ADN,10,10^FB550,15,0,L,0^FDmay likewise enjoy 2 hours of free parking.^FS");
//            mPrinter.sendCommand("^FO26,801^ADN,10,10^FB550,15,0,L,0^FDThe amount of PHP 20.00 will be charged to^FS");
//            mPrinter.sendCommand("^FO26,823^ADN,10,10^FB550,15,0,L,0^FDthe Customer for every succeeding hour. In^FS");
//            mPrinter.sendCommand("^FO26,845^ADN,10,10^FB550,15,0,L,0^FDboth instances, a fraction of an hour parked^FS");
//            mPrinter.sendCommand("^FO26,867^ADN,10,10^FB550,15,0,L,0^FDshall be counted as 1 hour.^FS");
//            //Rule no. 4
//            mPrinter.sendCommand("^FO0,891^ADN,10,10^FB550,15,0,L,0^FD4)Customers who losses their parking tickets^FS");
//            mPrinter.sendCommand("^FO26,913^ADN,10,10^FB550,15,0,L,0^FDshall pay the amount of TWO HUNDRED FIFTY^FS");
//            mPrinter.sendCommand("^FO26,935^ADN,10,10^FB550,15,0,L,0^FDPESOS (PHP 250.00). Customers who losses^FS");
//            mPrinter.sendCommand("^FO26,957^ADN,10,10^FB550,15,0,L,0^FDtheir parking tickets may be subject to^FS");
//            mPrinter.sendCommand("^FO26,979^ADN,10,10^FB550,15,0,L,0^FDinvestigation by the Mall Security. The^FS");
//            mPrinter.sendCommand("^FO26,1001^ADN,10,10^FB550,15,0,L,0^FDconcerned driver must present his/her LTO^FS");
//            mPrinter.sendCommand("^FO26,1023^ADN,10,10^FB550,15,0,L,0^FDissued Driver's License, Certificate of^FS");
//            mPrinter.sendCommand("^FO26,1045^ADN,10,10^FB550,15,0,L,0^FDRegistration and the current year Official^FS");
//            mPrinter.sendCommand("^FO26,1067^ADN,10,10^FB550,15,0,L,0^FDReceipt. Failure to present said documents^FS");
//            mPrinter.sendCommand("^FO26,1089^ADN,10,10^FB550,15,0,L,0^FDmay be referred to the TCPO by the Mall^FS");
//            mPrinter.sendCommand("^FO26,1111^ADN,10,10^FB550,15,0,L,0^FDSecurity.^FS");
//            //Rule no. 5
//            mPrinter.sendCommand("^FO0,1135^ADN,10,10^FB550,15,0,L,0^FD5)All Customers shall FOLLOW APPLICABLE^FS");
//            mPrinter.sendCommand("^FO26,1157^ADN,10,10^FB550,15,0,L,0^FDLAWS and are advised to STRICTLY ADHERE TO^FS");
//            mPrinter.sendCommand("^FO26,1179^ADN,10,10^FB550,15,0,L,0^FDPARKING REGULATIONS to ensure orderly flow of^FS");
//            mPrinter.sendCommand("^FO26,1201^ADN,10,10^FB550,15,0,L,0^FDtraffic and safety of the public.^FS");
//            //Rule no. 6
//            mPrinter.sendCommand("^FO0,1225^ADN,10,10^FB550,15,0,L,0^FD6)Upon entering the parking area, Customers^FS");
//            mPrinter.sendCommand("^FO26,1247^ADN,10,10^FB550,15,0,L,0^FDallow their vehicles to be subjected to^FS");
//            mPrinter.sendCommand("^FO26,1269^ADN,10,10^FB550,15,0,L,0^FDexternal inspection by the company's^FS");
//            mPrinter.sendCommand("^FO26,1291^ADN,10,10^FB550,15,0,L,0^FDauthorized personnel.^FS");
//            //Rule no. 7
//            mPrinter.sendCommand("^FO0,1315^ADN,10,10^FB550,15,0,L,0^FD7)Closed Circuit Televisions (CCTV) cameras^FS");
//            mPrinter.sendCommand("^FO26,1337^ADN,10,10^FB550,15,0,L,0^FDare in full operation in various locations^FS");
//            mPrinter.sendCommand("^FO26,1359^ADN,10,10^FB550,15,0,L,0^FDof the parking area.^FS");
//            //Rule no. 8
//            mPrinter.sendCommand("^FO0,1383^ADN,10,10^FB550,15,0,L,0^FD8)The Management of Plaza Marcela is NOT^FS");
//            mPrinter.sendCommand("^FO26,1405^ADN,10,10^FB550,15,0,L,0^FDLIABLE for any damages to or loss of vehicle^FS");
//            mPrinter.sendCommand("^FO26,1427^ADN,10,10^FB550,15,0,L,0^FDor any of its accessories or articles left^FS");
//            mPrinter.sendCommand("^FO26,1449^ADN,10,10^FB550,15,0,L,0^FDtherein.^FS");
//            //Rule no. 9
//            mPrinter.sendCommand("^FO0,1473^ADN,10,10^FB550,15,0,L,0^FD9)All Customers are required to EXERCISE^FS");
//            mPrinter.sendCommand("^FO26,1495^ADN,10,10^FB550,15,0,L,0^FDPROPER CARE AND DILIGENCE for the safety of^FS");
//            mPrinter.sendCommand("^FO26,1517^ADN,10,10^FB550,15,0,L,0^FDtheir person and their property.^FS");
//            //Rule no. 10
//            mPrinter.sendCommand("^FO0,1541^ADN,10,10^FB550,15,0,L,0^FD10)The Customer shall be liable for any^FS");
//            mPrinter.sendCommand("^FO26,1563^ADN,10,10^FB550,15,0,L,0^FDinjury or damage which he/she may cause to^FS");
//            mPrinter.sendCommand("^FO26,1585^ADN,10,10^FB550,15,0,L,0^FDperson or property inside the carpark which^FS");
//            mPrinter.sendCommand("^FO26,1607^ADN,10,10^FB550,15,0,L,0^FDmay include the Company's properties or^FS");
//            mPrinter.sendCommand("^FO26,1629^ADN,10,10^FB550,15,0,L,0^FDits facilities or any other person's vehicle^FS");
//            //Rule no. 11
//            mPrinter.sendCommand("^FO0,1653^ADN,10,10^FB550,15,0,L,0^FD11)All Customers are prohibited from^FS");
//            mPrinter.sendCommand("^FO26,1675^ADN,10,10^FB550,15,0,L,0^FDperforming illegal and/or indecent acts in^FS");
//            mPrinter.sendCommand("^FO26,1697^ADN,10,10^FB550,15,0,L,0^FDthe parking area. Anyone caught committing^FS");
//            mPrinter.sendCommand("^FO26,1719^ADN,10,10^FB550,15,0,L,0^FDsuch acts shall be dealt with the proper^FS");
//            mPrinter.sendCommand("^FO26,1741^ADN,10,10^FB550,15,0,L,0^FDauthorities^FS");
//            //Rule no. 12
//            mPrinter.sendCommand("^FO0,1765^ADN,10,10^FB550,15,0,L,0^FD12)Customer vehicle should occupy ONE (1)^FS");
//            mPrinter.sendCommand("^FO26,1787^ADN,10,10^FB550,15,0,L,0^FDPARKING SLOT ONLY.^FS");
//            //Rule no. 13
//            mPrinter.sendCommand("^FO0,1811^ADN,10,10^FB550,15,0,L,0^FD13)Parking overnight is STRICTLY PROHIBITED. ^FS");
//            mPrinter.sendCommand("^FO26,1833^ADN,10,10^FB550,15,0,L,0^FDA penalty of FIVE HUNDRED PESOS (PHP 500.00)^FS");
//            mPrinter.sendCommand("^FO26,1855^ADN,10,10^FB550,15,0,L,0^FDwill be imposed on the violators for each^FS");
//            mPrinter.sendCommand("^FO26,1877^ADN,10,10^FB550,15,0,L,0^FDnight. The Management has the right to^FS");
//            mPrinter.sendCommand("^FO26,1899^ADN,10,10^FB550,15,0,L,0^FDrestrict the vehicle from leaving unless the^FS");
//            mPrinter.sendCommand("^FO26,1921^ADN,10,10^FB550,15,0,L,0^FDpenalty has been paid.^FS");
//            //Rule no. 14
//            mPrinter.sendCommand("^FO0,1945^ADN,10,10^FB550,15,0,L,0^FD14)Vehicles parked overnight that may be^FS");
//            mPrinter.sendCommand("^FO26,1967^ADN,10,10^FB550,15,0,L,0^FDfound suspicious by the Management or remain^FS");
//            mPrinter.sendCommand("^FO26,1989^ADN,10,10^FB550,15,0,L,0^FDto be unattended may be subjected to an^FS");
//            mPrinter.sendCommand("^FO26,2011^ADN,10,10^FB550,15,0,L,0^FDadditional external inspection or a report to^FS");
//            mPrinter.sendCommand("^FO26,2033^ADN,10,10^FB550,15,0,L,0^FDthe Philippine National Police (PNP).^FS");
//            //Rule no. 15
//            mPrinter.sendCommand("^FO0,2057^ADN,10,10^FB550,15,0,L,0^FD15)Acceptance of the parking ticket^FS");
//            mPrinter.sendCommand("^FO26,2079^ADN,10,10^FB550,15,0,L,0^FDconstitutes acknowledgement by the holder^FS");
//            mPrinter.sendCommand("^FO26,2101^ADN,10,10^FB550,15,0,L,0^FDthat he/she has read carefully, understood^FS");
//            mPrinter.sendCommand("^FO26,2123^ADN,10,10^FB550,15,0,L,0^FDfully and will willingly comply with^FS");
//            mPrinter.sendCommand("^FO26,2145^ADN,10,10^FB550,15,0,L,0^FDparking rules^FS");
//            //Rule no. 16
//            mPrinter.sendCommand("^FO0,2169^ADN,10,10^FB550,15,0,L,0^FD16)The services provided by Plaza Marcela^FS");
//            mPrinter.sendCommand("^FO26,2191^ADN,10,10^FB550,15,0,L,0^FDManagement are governed by these 'Parking^FS");
//            mPrinter.sendCommand("^FO26,2213^ADN,10,10^FB550,15,0,L,0^FDCondition'. By availing the parking area,^FS");
//            mPrinter.sendCommand("^FO26,2235^ADN,10,10^FB550,15,0,L,0^FDCustomers expressly agree, adhere,^FS");
//            mPrinter.sendCommand("^FO26,2257^ADN,10,10^FB550,15,0,L,0^FDacknowledge and recognize the conditions set^FS");
//            mPrinter.sendCommand("^FO26,2279^ADN,10,10^FB550,15,0,L,0^FDout above and likewise agree that the^FS");
//            mPrinter.sendCommand("^FO26,2301^ADN,10,10^FB550,15,0,L,0^FDManagement has the right to refuse the^FS");
//            mPrinter.sendCommand("^FO26,2323^ADN,10,10^FB550,15,0,L,0^FDrelease of the Customer's vehicle until^FS");
//            mPrinter.sendCommand("^FO26,2345^ADN,10,10^FB550,15,0,L,0^FDcharges imposed has been paid or settled.^FS");
//            mPrinter.sendCommand("^FO110,2369^FB550,15,0,C,0^BCN,60,Y,N,N,N^FD"+splitted_transactions[1]+"^FS");
////            mPrinter.sendCommand("^FO110,2369^FB550,15,0,C,0^BCN,60,Y,N,N,N^FD0214536524785^FS");
//            mPrinter.sendCommand("^XZ");
//            mPrinter.sendCommand("! U1 setvar \"device.languages\" \"zpl\"\r\n");
//            mPrinter.sendCommand("^XA");
//            mPrinter.sendCommand("^LL150");
//            mPrinter.sendCommand("^POI");
////            mPrinter.sendCommand("^FO170,20^FB550,15,0,C,0^BCN,100,Y,N,N,A^FD"+splitted_transactions[1]+"^FS");
////            mPrinter.sendCommand("^FO60,20^FB550,15,0,C,0^B3N,N,75,Y,N^FD3247562985324^FS");
//            mPrinter.sendCommand("^FO110,20^FB550,15,0,C,0^BCN,60,Y,N,N,N^FD3247562985324^FS");

//          RULES & REGULATION TICKET...
//            mPrinter.sendCommand("! 0 200 200 1850 1");
//            mPrinter.sendCommand("CENTER");
//            mPrinter.sendCommand("TEXT 5 0 48 20 PLAZA MARCELA");
//            mPrinter.sendCommand("TEXT 5 0 48 50 Add: Pamaong, Corner Belderol St, Tagbilaran City, Bohol");
//            mPrinter.sendCommand("TEXT 5 0 48 80 Prop: PLAZA MARCELA");
//            mPrinter.sendCommand("TEXT 5 0 48 110 TIN: 000-254-327-009");
//            mPrinter.sendCommand("TEXT 5 0 48 190 B A S E M E N T  P A R K I N G  T I C K E T");
//            mPrinter.sendCommand("TEXT 5 0 0 220 ********************************************");
//            mPrinter.sendCommand("LEFT");
//            mPrinter.sendCommand("TEXT 5 0 0 260 Plate No.    : " + splitted_transactions[2]);
//            mPrinter.sendCommand("TEXT 5 0 0 300 Type            : " + vehicle_type);
//            mPrinter.sendCommand("TEXT 5 0 0 340 Ticket No.  : " + splitted_transactions[0]);
//            mPrinter.sendCommand("TEXT 5 0 0 380 Trans Code : " + splitted_transactions[1]);
//            mPrinter.sendCommand("TEXT 5 0 0 420 Date              : " + splitted_transactions[3]);
//            mPrinter.sendCommand("TEXT 5 0 0 460 Time             : " + splitted_transactions[4]);
//            mPrinter.sendCommand("TEXT 5 0 0 500 --------------------------------------------");
//            mPrinter.sendCommand("TEXT 7 0 0 530 Parking Rules:");
//            mPrinter.sendCommand("TEXT 7 0 0 560 1) Regular parking day starts at 7:30am and");
//            mPrinter.sendCommand("TEXT 7 0 35 590 ends at 10:30pm cut-off time.");
//            mPrinter.sendCommand("TEXT 7 0 0 620 2) 2 Wheeled Vehicles will enjoy 2 hours free");
//            mPrinter.sendCommand("TEXT 7 0 35 650 parking and will be charged P10.00 for each");
//            mPrinter.sendCommand("TEXT 7 0 35 680 succeeding hour.");
//            mPrinter.sendCommand("TEXT 7 0 0 710 3) 3 to 4 Wheeled Vehicles will enjoy 2 hours");
//            mPrinter.sendCommand("TEXT 7 0 35 740 free parking and will be charged P20.00 for");
//            mPrinter.sendCommand("TEXT 7 0 35 770 each succeeding hour.");
//            mPrinter.sendCommand("TEXT 7 0 0 800 4) A fraction of an hour parked is considered");
//            mPrinter.sendCommand("TEXT 7 0 35 830 1 hour.");
//            mPrinter.sendCommand("TEXT 7 0 0 860 5) Upon entry, a consumable coupon must be paid");
//            mPrinter.sendCommand("TEXT 7 0 35 890 by the vehicle driver, as follows:");
//            mPrinter.sendCommand("TEXT 7 0 80 920 > 2 wheels  -  P50.00/park");
//            mPrinter.sendCommand("TEXT 7 0 80 950 > 3-4 wheels  -  P100.00/park");
//            mPrinter.sendCommand("TEXT 7 0 0 980 6) Loss of parking ticket will be charged");
//            mPrinter.sendCommand("TEXT 7 0 35 1010 P250.00 penalty and is subject to an");
//            mPrinter.sendCommand("TEXT 7 0 35 1040 investigation by the Mall Security. The");
//            mPrinter.sendCommand("TEXT 7 0 35 1070 concerned driver must present LTO issued");
//            mPrinter.sendCommand("TEXT 7 0 35 1100 drivers license, Certificate of Registration");
//            mPrinter.sendCommand("TEXT 7 0 35 1130 and the current year Official Receipt.Failure");
//            mPrinter.sendCommand("TEXT 7 0 35 1160 to present said documents maybe reffered to ");
//            mPrinter.sendCommand("TEXT 7 0 35 1190 the TCPO by the Mall Security.");
//            mPrinter.sendCommand("TEXT 7 0 0 1220 7) Vehicles left overnight in the Parking Area");
//            mPrinter.sendCommand("TEXT 7 0 35 1250 (Surface) will be charged a penalty P500.00");
//            mPrinter.sendCommand("TEXT 7 0 35 1280 per cut off time violation aside from the ");
//            mPrinter.sendCommand("TEXT 7 0 35 1310 reg. parking fees accumulated every parking");
//            mPrinter.sendCommand("TEXT 7 0 35 1340 day; and also subject to investigation.");
//            mPrinter.sendCommand("TEXT 7 0 0 1370 8) Lock your vehicle properly and do not leave");
//            mPrinter.sendCommand("TEXT 7 0 35 1400 your parking ticket inside.");
//            mPrinter.sendCommand("TEXT 7 0 0 1430 9) The Car Park management is not responsible");
//            mPrinter.sendCommand("TEXT 7 0 35 1460 for the loss or damage to your parked vehicle");
//            mPrinter.sendCommand("TEXT 7 0 35 1490 its accessories and private properties inside");
//            mPrinter.sendCommand("TEXT 7 0 0 1520 10) Acceptance of this parking ticket");
//            mPrinter.sendCommand("TEXT 7 0 35 1550 constitutes acknowledgement by the holder");
//            mPrinter.sendCommand("TEXT 7 0 35 1580 that he/she has read carefully, understood");
//            mPrinter.sendCommand("TEXT 7 0 35 1610 fully and will willingly comply with the");
//            mPrinter.sendCommand("TEXT 7 0 35 1640 parking rules.");
//            mPrinter.sendCommand("CENTER");
//            mPrinter.sendCommand("BARCODE 128 3 1 75 0 1690 "+splitted_transactions[1]);
//            mPrinter.sendCommand("TEXT 5 0 0 1770 "+splitted_transactions[1]);
//            mPrinter.sendCommand("PRINT");
        } catch (Exception e) {
            // Handle communications error here.
            e.printStackTrace();
        }
    }

    public void reprint_ticket_pm()
    {
        try
        {
            if(splitted_transactions[6].equals("100"))
            {
                vehicle_type = "4 WHEELED";
            }
            else
            {
                vehicle_type = "2 WHEELED";
            }
            if(splitted_transactions[6].equals("100"))
            {
                vehicle_type = "4 WHEELED";
            }
            else
            {
                vehicle_type = "2 WHEELED";
            }
            mPrinter.sendCommand("! U1 setvar \"device.languages\" \"zpl\"\r\n");
            mPrinter.sendCommand("^XA");
            mPrinter.sendCommand("^LL1400");
            mPrinter.sendCommand("^POI");
            mPrinter.sendCommand("^FO-1,4^A0N,25,14^FDReprinted^FS");
            mPrinter.sendCommand("^A0N,30,22^FB550,15,0,C,0^FD\\&PLAZA MARCELA\\&" +
                    "Pamaong, Corner Belderol St, Tagbilaran City, Bohol\\&" +
                    "Prop: PLAZA MARCELA\\&" +
                    "TIN: 000-254-327-009\\&" +
                    "\\&B A S E M E N T  P A R K I N G  T I C K E T\\&" +
                    "********************************************\\&^FS");
            mPrinter.sendCommand("^FO0,230^ABN,21,10^FB550,15,0,L,0^FDPlate No.^FS");
            mPrinter.sendCommand("^FO110,230^ABN,21,10^FD: "+ splitted_transactions[2]+"^FS");
            mPrinter.sendCommand("^FO0,270^ABN,21,10^FB550,15,0,L,0^FDType^FS");
            mPrinter.sendCommand("^FO110,270^ABN,21,10^FB550,15,0,L,0^FD: "+ vehicle_type+"^FS");
            mPrinter.sendCommand("^FO0,310^ABN,21,10^FB550,15,0,L,0^FDTicket No.^FS");
            mPrinter.sendCommand("^FO110,310^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[0]+"^FS");
            mPrinter.sendCommand("^FO0,350^ABN,21,10^FB550,15,0,L,0^FDTrans code^FS");
            mPrinter.sendCommand("^FO110,350^ABN,21,10^FB550,15,0,L,0^FD: "+ splitted_transactions[1]+"^FS");
            mPrinter.sendCommand("^FO0,390^ABN,21,10^FB550,15,0,L,0^FDDate^FS");
            mPrinter.sendCommand("^FO110,390^ABN,21,10^FB550,15,0,L,0^FD: "+ splitted_transactions[3]+"^FS");
            mPrinter.sendCommand("^FO0,430^ABN,21,10^FB550,15,0,L,0^FDTime^FS");
            mPrinter.sendCommand("^FO110,430^ABN,21,10^FB550,15,0,L,0^FD: "+ splitted_transactions[4]+"^FS");
            mPrinter.sendCommand("^FO0,454^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,477^A0N,30,20^FB550,15,0,L,0^FDParking Rules: ^FS");
//            //Rule No.1
//            mPrinter.sendCommand("^FO0,535^ADN,10,10^FB550,15,0,L,0^FD1)The PLAZA MARCELA BASEMENT PARKING AREA is^FS");
//            mPrinter.sendCommand("^FO26,557^ADN,10,10^FDintended for our valued mall Shoppers'^FS");
//            mPrinter.sendCommand("^FO26,579^ADN,10,10^FDConvenience.^FS");
//            //Rule no. 2
//            mPrinter.sendCommand("^FO0,603^ADN,10,10^FB550,15,0,L,0^FD2)The PLAZA MARCELA BASEMENT PARKING AREA is ^FS");
//            mPrinter.sendCommand("^FO26,625^ADN,10,10^FDopen for our Valued Mall Shoppers^FS");
//            mPrinter.sendCommand("^FO26,645^ADN,10,10^FDfrom 8:00AM to 7:00PM.^FS");
//            //Rule no. 3
//            mPrinter.sendCommand("^FO0,669^ADN,10,10^FB550,15,0,L,0^FD3)Customers driving 2-Wheeled Vehicles may^FS");
//            mPrinter.sendCommand("^FO26,691^ADN,10,10^FB550,15,0,L,0^FDenjoy 2 hours of free parking. The amount^FS");
//            mPrinter.sendCommand("^FO26,713^ADN,10,10^FB550,15,0,L,0^FDPHP10.00 will be charged to the Customer^FS");
//            mPrinter.sendCommand("^FO26,735^ADN,10,10^FB550,15,0,L,0^FDfor every succeeding hour. While Customer^FS");
//            mPrinter.sendCommand("^FO26,757^ADN,10,10^FB550,15,0,L,0^FDCustomers driving 3 or 4-Wheeled Vehicles^FS");
//            mPrinter.sendCommand("^FO26,779^ADN,10,10^FB550,15,0,L,0^FDmay likewise enjoy 2 hours of free parking.^FS");
//            mPrinter.sendCommand("^FO26,801^ADN,10,10^FB550,15,0,L,0^FDThe amount of PHP 20.00 will be charged to^FS");
//            mPrinter.sendCommand("^FO26,823^ADN,10,10^FB550,15,0,L,0^FDthe Customer for every succeeding hour. In^FS");
//            mPrinter.sendCommand("^FO26,845^ADN,10,10^FB550,15,0,L,0^FDboth instances, a fraction of an hour parked^FS");
//            mPrinter.sendCommand("^FO26,867^ADN,10,10^FB550,15,0,L,0^FDshall be counted as 1 hour.^FS");
//            //Rule no. 4
//            mPrinter.sendCommand("^FO0,891^ADN,10,10^FB550,15,0,L,0^FD4)Customers who losses their parking tickets^FS");
//            mPrinter.sendCommand("^FO26,913^ADN,10,10^FB550,15,0,L,0^FDshall pay the amount of TWO HUNDRED FIFTY^FS");
//            mPrinter.sendCommand("^FO26,935^ADN,10,10^FB550,15,0,L,0^FDPESOS (PHP 250.00). Customers who losses^FS");
//            mPrinter.sendCommand("^FO26,957^ADN,10,10^FB550,15,0,L,0^FDtheir parking tickets may be subject to^FS");
//            mPrinter.sendCommand("^FO26,979^ADN,10,10^FB550,15,0,L,0^FDinvestigation by the Mall Security. The^FS");
//            mPrinter.sendCommand("^FO26,1001^ADN,10,10^FB550,15,0,L,0^FDconcerned driver must present his/her LTO^FS");
//            mPrinter.sendCommand("^FO26,1023^ADN,10,10^FB550,15,0,L,0^FDissued Driver's License, Certificate of^FS");
//            mPrinter.sendCommand("^FO26,1045^ADN,10,10^FB550,15,0,L,0^FDRegistration and the current year Official^FS");
//            mPrinter.sendCommand("^FO26,1067^ADN,10,10^FB550,15,0,L,0^FDReceipt. Failure to present said documents^FS");
//            mPrinter.sendCommand("^FO26,1089^ADN,10,10^FB550,15,0,L,0^FDmay be referred to the TCPO by the Mall^FS");
//            mPrinter.sendCommand("^FO26,1111^ADN,10,10^FB550,15,0,L,0^FDSecurity.^FS");
//            //Rule no. 5
//            mPrinter.sendCommand("^FO0,1135^ADN,10,10^FB550,15,0,L,0^FD5)All Customers shall FOLLOW APPLICABLE^FS");
//            mPrinter.sendCommand("^FO26,1157^ADN,10,10^FB550,15,0,L,0^FDLAWS and are advised to STRICTLY ADHERE TO^FS");
//            mPrinter.sendCommand("^FO26,1179^ADN,10,10^FB550,15,0,L,0^FDPARKING REGULATIONS to ensure orderly flow of^FS");
//            mPrinter.sendCommand("^FO26,1201^ADN,10,10^FB550,15,0,L,0^FDtraffic and safety of the public.^FS");
//            //Rule no. 6
//            mPrinter.sendCommand("^FO0,1225^ADN,10,10^FB550,15,0,L,0^FD6)Upon entering the parking area, Customers^FS");
//            mPrinter.sendCommand("^FO26,1247^ADN,10,10^FB550,15,0,L,0^FDallow their vehicles to be subjected to^FS");
//            mPrinter.sendCommand("^FO26,1269^ADN,10,10^FB550,15,0,L,0^FDexternal inspection by the company's^FS");
//            mPrinter.sendCommand("^FO26,1291^ADN,10,10^FB550,15,0,L,0^FDauthorized personnel.^FS");
//            //Rule no. 7
//            mPrinter.sendCommand("^FO0,1315^ADN,10,10^FB550,15,0,L,0^FD7)Closed Circuit Televisions (CCTV) cameras^FS");
//            mPrinter.sendCommand("^FO26,1337^ADN,10,10^FB550,15,0,L,0^FDare in full operation in various locations^FS");
//            mPrinter.sendCommand("^FO26,1359^ADN,10,10^FB550,15,0,L,0^FDof the parking area.^FS");
//            //Rule no. 8
//            mPrinter.sendCommand("^FO0,1383^ADN,10,10^FB550,15,0,L,0^FD8)The Management of Plaza Marcela is NOT^FS");
//            mPrinter.sendCommand("^FO26,1405^ADN,10,10^FB550,15,0,L,0^FDLIABLE for any damages to or loss of vehicle^FS");
//            mPrinter.sendCommand("^FO26,1427^ADN,10,10^FB550,15,0,L,0^FDor any of its accessories or articles left^FS");
//            mPrinter.sendCommand("^FO26,1449^ADN,10,10^FB550,15,0,L,0^FDtherein.^FS");
//            //Rule no. 9
//            mPrinter.sendCommand("^FO0,1473^ADN,10,10^FB550,15,0,L,0^FD9)All Customers are required to EXERCISE^FS");
//            mPrinter.sendCommand("^FO26,1495^ADN,10,10^FB550,15,0,L,0^FDPROPER CARE AND DILIGENCE for the safety of^FS");
//            mPrinter.sendCommand("^FO26,1517^ADN,10,10^FB550,15,0,L,0^FDtheir person and their property.^FS");
//            //Rule no. 10
//            mPrinter.sendCommand("^FO0,1541^ADN,10,10^FB550,15,0,L,0^FD10)The Customer shall be liable for any^FS");
//            mPrinter.sendCommand("^FO26,1563^ADN,10,10^FB550,15,0,L,0^FDinjury or damage which he/she may cause to^FS");
//            mPrinter.sendCommand("^FO26,1585^ADN,10,10^FB550,15,0,L,0^FDperson or property inside the carpark which^FS");
//            mPrinter.sendCommand("^FO26,1607^ADN,10,10^FB550,15,0,L,0^FDmay include the Company's properties or^FS");
//            mPrinter.sendCommand("^FO26,1629^ADN,10,10^FB550,15,0,L,0^FDits facilities or any other person's vehicle^FS");
//            //Rule no. 11
//            mPrinter.sendCommand("^FO0,1653^ADN,10,10^FB550,15,0,L,0^FD11)All Customers are prohibited from^FS");
//            mPrinter.sendCommand("^FO26,1675^ADN,10,10^FB550,15,0,L,0^FDperforming illegal and/or indecent acts in^FS");
//            mPrinter.sendCommand("^FO26,1697^ADN,10,10^FB550,15,0,L,0^FDthe parking area. Anyone caught committing^FS");
//            mPrinter.sendCommand("^FO26,1719^ADN,10,10^FB550,15,0,L,0^FDsuch acts shall be dealt with the proper^FS");
//            mPrinter.sendCommand("^FO26,1741^ADN,10,10^FB550,15,0,L,0^FDauthorities^FS");
//            //Rule no. 12
//            mPrinter.sendCommand("^FO0,1765^ADN,10,10^FB550,15,0,L,0^FD12)Customer vehicle should occupy ONE (1)^FS");
//            mPrinter.sendCommand("^FO26,1787^ADN,10,10^FB550,15,0,L,0^FDPARKING SLOT ONLY.^FS");
//            //Rule no. 13
//            mPrinter.sendCommand("^FO0,1811^ADN,10,10^FB550,15,0,L,0^FD13)Parking overnight is STRICTLY PROHIBITED. ^FS");
//            mPrinter.sendCommand("^FO26,1833^ADN,10,10^FB550,15,0,L,0^FDA penalty of FIVE HUNDRED PESOS (PHP 500.00)^FS");
//            mPrinter.sendCommand("^FO26,1855^ADN,10,10^FB550,15,0,L,0^FDwill be imposed on the violators for each^FS");
//            mPrinter.sendCommand("^FO26,1877^ADN,10,10^FB550,15,0,L,0^FDnight. The Management has the right to^FS");
//            mPrinter.sendCommand("^FO26,1899^ADN,10,10^FB550,15,0,L,0^FDrestrict the vehicle from leaving unless the^FS");
//            mPrinter.sendCommand("^FO26,1921^ADN,10,10^FB550,15,0,L,0^FDpenalty has been paid.^FS");
//            //Rule no. 14
//            mPrinter.sendCommand("^FO0,1945^ADN,10,10^FB550,15,0,L,0^FD14)Vehicles parked overnight that may be^FS");
//            mPrinter.sendCommand("^FO26,1967^ADN,10,10^FB550,15,0,L,0^FDfound suspicious by the Management or remain^FS");
//            mPrinter.sendCommand("^FO26,1989^ADN,10,10^FB550,15,0,L,0^FDto be unattended may be subjected to an^FS");
//            mPrinter.sendCommand("^FO26,2011^ADN,10,10^FB550,15,0,L,0^FDadditional external inspection or a report to^FS");
//            mPrinter.sendCommand("^FO26,2033^ADN,10,10^FB550,15,0,L,0^FDthe Philippine National Police (PNP).^FS");
//            //Rule no. 15
//            mPrinter.sendCommand("^FO0,2057^ADN,10,10^FB550,15,0,L,0^FD15)Acceptance of the parking ticket^FS");
//            mPrinter.sendCommand("^FO26,2079^ADN,10,10^FB550,15,0,L,0^FDconstitutes acknowledgement by the holder^FS");
//            mPrinter.sendCommand("^FO26,2101^ADN,10,10^FB550,15,0,L,0^FDthat he/she has read carefully, understood^FS");
//            mPrinter.sendCommand("^FO26,2123^ADN,10,10^FB550,15,0,L,0^FDfully and will willingly comply with^FS");
//            mPrinter.sendCommand("^FO26,2145^ADN,10,10^FB550,15,0,L,0^FDparking rules^FS");
//            //Rule no. 16
//            mPrinter.sendCommand("^FO0,2169^ADN,10,10^FB550,15,0,L,0^FD16)The services provided by Plaza Marcela^FS");
//            mPrinter.sendCommand("^FO26,2191^ADN,10,10^FB550,15,0,L,0^FDManagement are governed by these 'Parking^FS");
//            mPrinter.sendCommand("^FO26,2213^ADN,10,10^FB550,15,0,L,0^FDCondition'. By availing the parking area,^FS");
//            mPrinter.sendCommand("^FO26,2235^ADN,10,10^FB550,15,0,L,0^FDCustomers expressly agree, adhere,^FS");
//            mPrinter.sendCommand("^FO26,2257^ADN,10,10^FB550,15,0,L,0^FDacknowledge and recognize the conditions set^FS");
//            mPrinter.sendCommand("^FO26,2279^ADN,10,10^FB550,15,0,L,0^FDout above and likewise agree that the^FS");
//            mPrinter.sendCommand("^FO26,2301^ADN,10,10^FB550,15,0,L,0^FDManagement has the right to refuse the^FS");
//            mPrinter.sendCommand("^FO26,2323^ADN,10,10^FB550,15,0,L,0^FDrelease of the Customer's vehicle until^FS");
//            mPrinter.sendCommand("^FO26,2345^ADN,10,10^FB550,15,0,L,0^FDcharges imposed has been paid or settled.^FS");
//            mPrinter.sendCommand("^FO110,2369^FB550,15,0,C,0^BCN,60,Y,N,N,N^FD"+splitted_transactions[1]+"^FS");
//            mPrinter.sendCommand("^XZ");

            //Rule no. 1
            mPrinter.sendCommand("^FO0,506^ADN,10,10^FB550,15,0,L,0^FD1) Regular parking day starts at 8:00am and^FS");
            mPrinter.sendCommand("^FO32,529^ADN,10,10^FDends at 7:00pm cut-off time^FS");
            //Rule no. 2
            mPrinter.sendCommand("^FO0,553^ADN,10,10^FB550,15,0,L,0^FD2) 2 Wheeled Vehicles will enjoy 2 hours free^FS");
            mPrinter.sendCommand("^FO32,576^ADN,10,10^FDparking and will be charged P10.00 for each^FS");
            mPrinter.sendCommand("^FO32,600^ADN,10,10^FDsucceeding hour.^FS");
            //Rule no. 3
            mPrinter.sendCommand("^FO0,623^ADN,10,10^FB550,15,0,L,0^FD3) 3 to 4 Wheeled Vehicles will enjoy 2 hours^FS");
            mPrinter.sendCommand("^FO32,646^ADN,10,10^FDfree parking and will be charged P20.00 for^FS");
            mPrinter.sendCommand("^FO32,670^ADN,10,10^FDeach succeeding hour.^FS");
            //Rule no. 4
            mPrinter.sendCommand("^FO0,693^ADN,10,10^FB550,15,0,L,0^FD4) A fraction of an hour parked is considered^FS");
            mPrinter.sendCommand("^FO32,716^ADN,10,10^FD1 hour.^FS");
            //Rule no. 5
//            mPrinter.sendCommand("^FO0,784^ADN,10,10^FB550,15,0,L,0^FD5) Upon entry, a consumable coupon must be^FS");
//            mPrinter.sendCommand("^FO32,807^ADN,10,10^FDpaid by the vehicle driver, as follows:^FS");
//            mPrinter.sendCommand("^FO67,830^ADN,10,10^FD> 2 wheels   -  P50.00/park^FS");
//            mPrinter.sendCommand("^FO67,853^ADN,10,10^FD> 3-4 wheels -  P100.00/park^FS");
            //Rule no. 6
            mPrinter.sendCommand("^FO0,740^ADN,10,10^FB550,15,0,L,0^FD5) Loss of parking ticket will be charged^FS");
            mPrinter.sendCommand("^FO32,763^ADN,10,10^FDP250.00 penalty and is subject to an for^FS");
            mPrinter.sendCommand("^FO32,786^ADN,10,10^FDinvestigation by the Mall Security. The^FS");
            mPrinter.sendCommand("^FO32,809^ADN,10,10^FDconcerned driver must present LTO issued^FS");
            mPrinter.sendCommand("^FO32,832^ADN,10,10^FDdrivers license, Certificate of Registration^FS");
            mPrinter.sendCommand("^FO32,855^ADN,10,10^FDand the current year Official Receipt.Failure^FS");
            mPrinter.sendCommand("^FO32,878^ADN,10,10^FDand to present said documents maybe reffered to^FS");
            mPrinter.sendCommand("^FO32,901^ADN,10,10^FDthe TCPO by the Mall Security.^FS");
            //Rule no. 7
            mPrinter.sendCommand("^FO0,925^ADN,10,10^FB550,15,0,L,0^FD6) Vehicles left overnight in the Parking^FS");
            mPrinter.sendCommand("^FO32,948^ADN,10,10^FDAREA (Surface) will be charged a penalty P500.00^FS");
            mPrinter.sendCommand("^FO32,971^ADN,10,10^FDper cut off time violation aside from the^FS");
            mPrinter.sendCommand("^FO32,994^ADN,10,10^FDreg. parking fees accumulated every parking^FS");
            mPrinter.sendCommand("^FO32,1017^ADN,10,10^FDday; and also subject to investigation.^FS");
            //Rule no. 8
            mPrinter.sendCommand("^FO0,1041^ADN,10,10^FB550,15,0,L,0^FD7) Lock your vehicle properly and do not^FS");
            mPrinter.sendCommand("^FO32,1064^ADN,10,10^FDleave your parking ticket inside.^FS");
            //Rule no. 9
            mPrinter.sendCommand("^FO0,1088^ADN,10,10^FB550,15,0,L,0^FD8) The Car Park management is not responsible^FS");
            mPrinter.sendCommand("^FO32,1111^ADN,10,10^FDfor the loss or damage to your parked vehicle^FS");
            mPrinter.sendCommand("^FO32,1134^ADN,10,10^FDits accessories and private properties inside^FS");
            //Rule no. 10
            mPrinter.sendCommand("^FO0,1158^ADN,10,10^FB550,15,0,L,0^FD9) Acceptance of this parking ticket^FS");
            mPrinter.sendCommand("^FO32,1181^ADN,10,10^FDconstitutes acknowledgement by the holder that^FS");
            mPrinter.sendCommand("^FO32,1204^ADN,10,10^FDhe/she has read carefully, understood fully ^FS");
            mPrinter.sendCommand("^FO32,1227^ADN,10,10^FDand will willingly comply with the^FS");
            mPrinter.sendCommand("^FO32,1250^ADN,10,10^FDparking rules.^FS");
            mPrinter.sendCommand("^FO110,1275^FB550,15,0,C,0^BCN,60,Y,N,N,N^FD"+splitted_transactions[1]+"^FS");
            mPrinter.sendCommand("^XZ");
//            //Rule No.1
//            mPrinter.sendCommand("^FO0,535^ADN,10,10^FB550,15,0,L,0^FD1)The PLAZA MARCELA BASEMENT PARKING AREA is^FS");
//            mPrinter.sendCommand("^FO26,557^ADN,10,10^FDintended for our valued mall Shoppers'^FS");
//            mPrinter.sendCommand("^FO26,579^ADN,10,10^FDConvenience.^FS");
//            //Rule no. 2
//            mPrinter.sendCommand("^FO0,603^ADN,10,10^FB550,15,0,L,0^FD2)The PLAZA MARCELA BASEMENT PARKING AREA is ^FS");
//            mPrinter.sendCommand("^FO26,625^ADN,10,10^FDopen for our Valued Mall Shoppers^FS");
//            mPrinter.sendCommand("^FO26,645^ADN,10,10^FDfrom 8:00AM to 7:00PM.^FS");
//            //Rule no. 3
//            mPrinter.sendCommand("^FO0,669^ADN,10,10^FB550,15,0,L,0^FD3)Customers driving 2-Wheeled Vehicles may^FS");
//            mPrinter.sendCommand("^FO26,691^ADN,10,10^FB550,15,0,L,0^FDenjoy 2 hours of free parking. The amount^FS");
//            mPrinter.sendCommand("^FO26,713^ADN,10,10^FB550,15,0,L,0^FDPHP10.00 will be charged to the Customer^FS");
//            mPrinter.sendCommand("^FO26,735^ADN,10,10^FB550,15,0,L,0^FDfor every succeeding hour. While Customer^FS");
//            mPrinter.sendCommand("^FO26,757^ADN,10,10^FB550,15,0,L,0^FDCustomers driving 3 or 4-Wheeled Vehicles^FS");
//            mPrinter.sendCommand("^FO26,779^ADN,10,10^FB550,15,0,L,0^FDmay likewise enjoy 2 hours of free parking.^FS");
//            mPrinter.sendCommand("^FO26,801^ADN,10,10^FB550,15,0,L,0^FDThe amount of PHP 20.00 will be charged to^FS");
//            mPrinter.sendCommand("^FO26,823^ADN,10,10^FB550,15,0,L,0^FDthe Customer for every succeeding hour. In^FS");
//            mPrinter.sendCommand("^FO26,845^ADN,10,10^FB550,15,0,L,0^FDboth instances, a fraction of an hour parked^FS");
//            mPrinter.sendCommand("^FO26,867^ADN,10,10^FB550,15,0,L,0^FDshall be counted as 1 hour.^FS");
//            //Rule no. 4
//            mPrinter.sendCommand("^FO0,891^ADN,10,10^FB550,15,0,L,0^FD4)Customers who losses their parking tickets^FS");
//            mPrinter.sendCommand("^FO26,913^ADN,10,10^FB550,15,0,L,0^FDshall pay the amount of TWO HUNDRED FIFTY^FS");
//            mPrinter.sendCommand("^FO26,935^ADN,10,10^FB550,15,0,L,0^FDPESOS (PHP 250.00). Customers who losses^FS");
//            mPrinter.sendCommand("^FO26,957^ADN,10,10^FB550,15,0,L,0^FDtheir parking tickets may be subject to^FS");
//            mPrinter.sendCommand("^FO26,979^ADN,10,10^FB550,15,0,L,0^FDinvestigation by the Mall Security. The^FS");
//            mPrinter.sendCommand("^FO26,1001^ADN,10,10^FB550,15,0,L,0^FDconcerned driver must present his/her LTO^FS");
//            mPrinter.sendCommand("^FO26,1023^ADN,10,10^FB550,15,0,L,0^FDissued Driver's License, Certificate of^FS");
//            mPrinter.sendCommand("^FO26,1045^ADN,10,10^FB550,15,0,L,0^FDRegistration and the current year Official^FS");
//            mPrinter.sendCommand("^FO26,1067^ADN,10,10^FB550,15,0,L,0^FDReceipt. Failure to present said documents^FS");
//            mPrinter.sendCommand("^FO26,1089^ADN,10,10^FB550,15,0,L,0^FDmay be referred to the TCPO by the Mall^FS");
//            mPrinter.sendCommand("^FO26,1111^ADN,10,10^FB550,15,0,L,0^FDSecurity.^FS");
//            //Rule no. 5
//            mPrinter.sendCommand("^FO0,1135^ADN,10,10^FB550,15,0,L,0^FD5)All Customers shall FOLLOW APPLICABLE^FS");
//            mPrinter.sendCommand("^FO26,1157^ADN,10,10^FB550,15,0,L,0^FDLAWS and are advised to STRICTLY ADHERE TO^FS");
//            mPrinter.sendCommand("^FO26,1179^ADN,10,10^FB550,15,0,L,0^FDPARKING REGULATIONS to ensure orderly flow of^FS");
//            mPrinter.sendCommand("^FO26,1201^ADN,10,10^FB550,15,0,L,0^FDtraffic and safety of the public.^FS");
//            //Rule no. 6
//            mPrinter.sendCommand("^FO0,1225^ADN,10,10^FB550,15,0,L,0^FD6)Upon entering the parking area, Customers^FS");
//            mPrinter.sendCommand("^FO26,1247^ADN,10,10^FB550,15,0,L,0^FDallow their vehicles to be subjected to^FS");
//            mPrinter.sendCommand("^FO26,1269^ADN,10,10^FB550,15,0,L,0^FDexternal inspection by the company's^FS");
//            mPrinter.sendCommand("^FO26,1291^ADN,10,10^FB550,15,0,L,0^FDauthorized personnel.^FS");
//            //Rule no. 7
//            mPrinter.sendCommand("^FO0,1315^ADN,10,10^FB550,15,0,L,0^FD7)Closed Circuit Televisions (CCTV) cameras^FS");
//            mPrinter.sendCommand("^FO26,1337^ADN,10,10^FB550,15,0,L,0^FDare in full operation in various locations^FS");
//            mPrinter.sendCommand("^FO26,1359^ADN,10,10^FB550,15,0,L,0^FDof the parking area.^FS");
//            //Rule no. 8
//            mPrinter.sendCommand("^FO0,1383^ADN,10,10^FB550,15,0,L,0^FD8)The Management of Plaza Marcela is NOT^FS");
//            mPrinter.sendCommand("^FO26,1405^ADN,10,10^FB550,15,0,L,0^FDLIABLE for any damages to or loss of vehicle^FS");
//            mPrinter.sendCommand("^FO26,1427^ADN,10,10^FB550,15,0,L,0^FDor any of its accessories or articles left^FS");
//            mPrinter.sendCommand("^FO26,1449^ADN,10,10^FB550,15,0,L,0^FDtherein.^FS");
//            //Rule no. 9
//            mPrinter.sendCommand("^FO0,1473^ADN,10,10^FB550,15,0,L,0^FD9)All Customers are required to EXERCISE^FS");
//            mPrinter.sendCommand("^FO26,1495^ADN,10,10^FB550,15,0,L,0^FDPROPER CARE AND DILIGENCE for the safety of^FS");
//            mPrinter.sendCommand("^FO26,1517^ADN,10,10^FB550,15,0,L,0^FDtheir person and their property.^FS");
//            //Rule no. 10
//            mPrinter.sendCommand("^FO0,1541^ADN,10,10^FB550,15,0,L,0^FD10)The Customer shall be liable for any^FS");
//            mPrinter.sendCommand("^FO26,1563^ADN,10,10^FB550,15,0,L,0^FDinjury or damage which he/she may cause to^FS");
//            mPrinter.sendCommand("^FO26,1585^ADN,10,10^FB550,15,0,L,0^FDperson or property inside the carpark which^FS");
//            mPrinter.sendCommand("^FO26,1607^ADN,10,10^FB550,15,0,L,0^FDmay include the Company's properties or^FS");
//            mPrinter.sendCommand("^FO26,1629^ADN,10,10^FB550,15,0,L,0^FDits facilities or any other person's vehicle^FS");
//            //Rule no. 11
//            mPrinter.sendCommand("^FO0,1653^ADN,10,10^FB550,15,0,L,0^FD11)All Customers are prohibited from^FS");
//            mPrinter.sendCommand("^FO26,1675^ADN,10,10^FB550,15,0,L,0^FDperforming illegal and/or indecent acts in^FS");
//            mPrinter.sendCommand("^FO26,1697^ADN,10,10^FB550,15,0,L,0^FDthe parking area. Anyone caught committing^FS");
//            mPrinter.sendCommand("^FO26,1719^ADN,10,10^FB550,15,0,L,0^FDsuch acts shall be dealt with the proper^FS");
//            mPrinter.sendCommand("^FO26,1741^ADN,10,10^FB550,15,0,L,0^FDauthorities^FS");
//            //Rule no. 12
//            mPrinter.sendCommand("^FO0,1765^ADN,10,10^FB550,15,0,L,0^FD12)Customer vehicle should occupy ONE (1)^FS");
//            mPrinter.sendCommand("^FO26,1787^ADN,10,10^FB550,15,0,L,0^FDPARKING SLOT ONLY.^FS");
//            //Rule no. 13
//            mPrinter.sendCommand("^FO0,1811^ADN,10,10^FB550,15,0,L,0^FD13)Parking overnight is STRICTLY PROHIBITED. ^FS");
//            mPrinter.sendCommand("^FO26,1833^ADN,10,10^FB550,15,0,L,0^FDA penalty of FIVE HUNDRED PESOS (PHP 500.00)^FS");
//            mPrinter.sendCommand("^FO26,1855^ADN,10,10^FB550,15,0,L,0^FDwill be imposed on the violators for each^FS");
//            mPrinter.sendCommand("^FO26,1877^ADN,10,10^FB550,15,0,L,0^FDnight. The Management has the right to^FS");
//            mPrinter.sendCommand("^FO26,1899^ADN,10,10^FB550,15,0,L,0^FDrestrict the vehicle from leaving unless the^FS");
//            mPrinter.sendCommand("^FO26,1921^ADN,10,10^FB550,15,0,L,0^FDpenalty has been paid.^FS");
//            //Rule no. 14
//            mPrinter.sendCommand("^FO0,1945^ADN,10,10^FB550,15,0,L,0^FD14)Vehicles parked overnight that may be^FS");
//            mPrinter.sendCommand("^FO26,1967^ADN,10,10^FB550,15,0,L,0^FDfound suspicious by the Management or remain^FS");
//            mPrinter.sendCommand("^FO26,1989^ADN,10,10^FB550,15,0,L,0^FDto be unattended may be subjected to an^FS");
//            mPrinter.sendCommand("^FO26,2011^ADN,10,10^FB550,15,0,L,0^FDadditional external inspection or a report to^FS");
//            mPrinter.sendCommand("^FO26,2033^ADN,10,10^FB550,15,0,L,0^FDthe Philippine National Police (PNP).^FS");
//            //Rule no. 15
//            mPrinter.sendCommand("^FO0,2057^ADN,10,10^FB550,15,0,L,0^FD15)Acceptance of the parking ticket^FS");
//            mPrinter.sendCommand("^FO26,2079^ADN,10,10^FB550,15,0,L,0^FDconstitutes acknowledgement by the holder^FS");
//            mPrinter.sendCommand("^FO26,2101^ADN,10,10^FB550,15,0,L,0^FDthat he/she has read carefully, understood^FS");
//            mPrinter.sendCommand("^FO26,2123^ADN,10,10^FB550,15,0,L,0^FDfully and will willingly comply with^FS");
//            mPrinter.sendCommand("^FO26,2145^ADN,10,10^FB550,15,0,L,0^FDparking rules^FS");
//            //Rule no. 16
//            mPrinter.sendCommand("^FO0,2169^ADN,10,10^FB550,15,0,L,0^FD16)The services provided by Plaza Marcela^FS");
//            mPrinter.sendCommand("^FO26,2191^ADN,10,10^FB550,15,0,L,0^FDManagement are governed by these 'Parking^FS");
//            mPrinter.sendCommand("^FO26,2213^ADN,10,10^FB550,15,0,L,0^FDCondition'. By availing the parking area,^FS");
//            mPrinter.sendCommand("^FO26,2235^ADN,10,10^FB550,15,0,L,0^FDCustomers expressly agree, adhere,^FS");
//            mPrinter.sendCommand("^FO26,2257^ADN,10,10^FB550,15,0,L,0^FDacknowledge and recognize the conditions set^FS");
//            mPrinter.sendCommand("^FO26,2279^ADN,10,10^FB550,15,0,L,0^FDout above and likewise agree that the^FS");
//            mPrinter.sendCommand("^FO26,2301^ADN,10,10^FB550,15,0,L,0^FDManagement has the right to refuse the^FS");
//            mPrinter.sendCommand("^FO26,2323^ADN,10,10^FB550,15,0,L,0^FDrelease of the Customer's vehicle until^FS");
//            mPrinter.sendCommand("^FO26,2345^ADN,10,10^FB550,15,0,L,0^FDcharges imposed has been paid or settled.^FS");
//            mPrinter.sendCommand("^FO110,2369^FB550,15,0,C,0^BCN,60,Y,N,N,N^FD"+splitted_transactions[1]+"^FS");
//            mPrinter.sendCommand("^XZ");

//            //Rule no. 1
//            mPrinter.sendCommand("^FO0,501^ADN,10,10^FB550,15,0,L,0^FD1) Regular parking day starts at 7:30am and^FS");
//            mPrinter.sendCommand("^FO32,524^ADN,10,10^FDends at 10:30pm cut-off time^FS");
//            //Rule no. 2
//            mPrinter.sendCommand("^FO0,548^ADN,10,10^FB550,15,0,L,0^FD2) 2 Wheeled Vehicles will enjoy 2 hours free^FS");
//            mPrinter.sendCommand("^FO32,671^ADN,10,10^FDparking and will be charged P10.00 for each^FS");
//            mPrinter.sendCommand("^FO32,694^ADN,10,10^FDsucceeding hour.^FS");
//            //Rule no. 3
//            mPrinter.sendCommand("^FO0,718^ADN,10,10^FB550,15,0,L,0^FD3) 3 to 4 Wheeled Vehicles will enjoy 2 hours^FS");
//            mPrinter.sendCommand("^FO32,741^ADN,10,10^FDfree parking and will be charged P20.00 for^FS");
//            mPrinter.sendCommand("^FO32,764^ADN,10,10^FDeach succeeding hour.^FS");
//            //Rule no. 4
//            mPrinter.sendCommand("^FO0,788^ADN,10,10^FB550,15,0,L,0^FD4) A fraction of an hour parked is considered^FS");
//            mPrinter.sendCommand("^FO32,811^ADN,10,10^FD1 hour.^FS");
//            //Rule no. 5
////            mPrinter.sendCommand("^FO0,784^ADN,10,10^FB550,15,0,L,0^FD5) Upon entry, a consumable coupon must be^FS");
////            mPrinter.sendCommand("^FO32,807^ADN,10,10^FDpaid by the vehicle driver, as follows:^FS");
////            mPrinter.sendCommand("^FO67,830^ADN,10,10^FD> 2 wheels   -  P50.00/park^FS");
////            mPrinter.sendCommand("^FO67,853^ADN,10,10^FD> 3-4 wheels -  P100.00/park^FS");
//            //Rule no. 6
//            mPrinter.sendCommand("^FO0,835^ADN,10,10^FB550,15,0,L,0^FD5) Loss of parking ticket will be charged^FS");
//            mPrinter.sendCommand("^FO32,858^ADN,10,10^FDP250.00 penalty and is subject to an for^FS");
//            mPrinter.sendCommand("^FO32,881^ADN,10,10^FDinvestigation by the Mall Security. The^FS");
//            mPrinter.sendCommand("^FO32,904^ADN,10,10^FDconcerned driver must present LTO issued^FS");
//            mPrinter.sendCommand("^FO32,927^ADN,10,10^FDdrivers license, Certificate of Registration^FS");
//            mPrinter.sendCommand("^FO32,950^ADN,10,10^FDand the current year Official Receipt.Failure^FS");
//            mPrinter.sendCommand("^FO32,973^ADN,10,10^FDand to present said documents maybe reffered to^FS");
//            mPrinter.sendCommand("^FO32,996^ADN,10,10^FDthe TCPO by the Mall Security.^FS");
//            //Rule no. 7
//            mPrinter.sendCommand("^FO0,1020^ADN,10,10^FB550,15,0,L,0^FD6) Vehicles left overnight in the Parking^FS");
//            mPrinter.sendCommand("^FO32,1043^ADN,10,10^FDAREA (Surface) will be charged a penalty P500.00^FS");
//            mPrinter.sendCommand("^FO32,1066^ADN,10,10^FDper cut off time violation aside from the^FS");
//            mPrinter.sendCommand("^FO32,1089^ADN,10,10^FDreg. parking fees accumulated every parking^FS");
//            mPrinter.sendCommand("^FO32,1112^ADN,10,10^FDday; and also subject to investigation.^FS");
//            //Rule no. 8
//            mPrinter.sendCommand("^FO0,1136^ADN,10,10^FB550,15,0,L,0^FD7) Lock your vehicle properly and do not^FS");
//            mPrinter.sendCommand("^FO32,1159^ADN,10,10^FDleave your parking ticket inside.^FS");
//            //Rule no. 9
//            mPrinter.sendCommand("^FO0,1183^ADN,10,10^FB550,15,0,L,0^FD8) The Car Park management is not responsible^FS");
//            mPrinter.sendCommand("^FO32,1206^ADN,10,10^FDfor the loss or damage to your parked vehicle^FS");
//            mPrinter.sendCommand("^FO32,1229^ADN,10,10^FDits accessories and private properties inside^FS");
//            //Rule no. 10
//            mPrinter.sendCommand("^FO0,1253^ADN,10,10^FB550,15,0,L,0^FD9) Acceptance of this parking ticket^FS");
//            mPrinter.sendCommand("^FO32,1275^ADN,10,10^FDconstitutes acknowledgement by the holder that^FS");
//            mPrinter.sendCommand("^FO32,1298^ADN,10,10^FDhe/she has read carefully, understood fully ^FS");
//            mPrinter.sendCommand("^FO32,1321^ADN,10,10^FDand will willingly comply with the^FS");
//            mPrinter.sendCommand("^FO32,1344^ADN,10,10^FDparking rules.^FS");
//            mPrinter.sendCommand("^FO110,1368^FB550,15,0,C,0^BCN,60,Y,N,N,N^FD"+splitted_transactions[1]+"^FS");
//            mPrinter.sendCommand("^XZ");

//          RULES & REGULATION TICKET...
//            mPrinter.sendCommand("! 0 200 200 1860 1");
//            mPrinter.sendCommand("LEFT");
//            mPrinter.sendCommand("TEXT 5 0 0 10 REPRINTED");
//            mPrinter.sendCommand("CENTER");
//            mPrinter.sendCommand("TEXT 5 0 48 30 PLAZA MARCELA");
//            mPrinter.sendCommand("TEXT 5 0 48 60 Add: Pamaong, Corner Belderol St, Tagbilaran City, Bohol");
//            mPrinter.sendCommand("TEXT 5 0 48 90 Prop: PLAZA MARCELA");
//            mPrinter.sendCommand("TEXT 5 0 48 120 TIN: 000-254-327-009");
//            mPrinter.sendCommand("TEXT 5 0 48 200 B A S E M E N T  P A R K I N G  T I C K E T");
//            mPrinter.sendCommand("TEXT 5 0 0 230 ********************************************");
//            mPrinter.sendCommand("LEFT");
//            mPrinter.sendCommand("TEXT 5 0 0 270 Plate No.    : " + splitted_transactions[2]);
//            mPrinter.sendCommand("TEXT 5 0 0 310 Type            : " + vehicle_type);
//            mPrinter.sendCommand("TEXT 5 0 0 350 Ticket No.  : " + splitted_transactions[0]);
//            mPrinter.sendCommand("TEXT 5 0 0 390 Trans Code : " + splitted_transactions[1]);
//            mPrinter.sendCommand("TEXT 5 0 0 430 Date              : " + splitted_transactions[3]);
//            mPrinter.sendCommand("TEXT 5 0 0 470 Time             : " + splitted_transactions[4]);
//            mPrinter.sendCommand("TEXT 5 0 0 510 --------------------------------------------");
//            mPrinter.sendCommand("TEXT 7 0 0 540 Parking Rules:");
//            mPrinter.sendCommand("TEXT 7 0 0 570 1) Regular parking day starts at 7:30am and");
//            mPrinter.sendCommand("TEXT 7 0 35 600 ends at 10:30pm cut-off time.");
//            mPrinter.sendCommand("TEXT 7 0 0 630 2) 2 Wheeled Vehicles will enjoy 2 hours free");
//            mPrinter.sendCommand("TEXT 7 0 35 660 parking and will be charged P10.00 for each");
//            mPrinter.sendCommand("TEXT 7 0 35 690 succeeding hour.");
//            mPrinter.sendCommand("TEXT 7 0 0 720 3) 3 to 4 Wheeled Vehicles will enjoy 2 hours");
//            mPrinter.sendCommand("TEXT 7 0 35 750 free parking and will be charged P20.00 for");
//            mPrinter.sendCommand("TEXT 7 0 35 780 each succeeding hour.");
//            mPrinter.sendCommand("TEXT 7 0 0 810 4) A fraction of an hour parked is considered");
//            mPrinter.sendCommand("TEXT 7 0 35 840 1 hour.");
//            mPrinter.sendCommand("TEXT 7 0 0 870 5) Upon entry, a consumable coupon must be paid");
//            mPrinter.sendCommand("TEXT 7 0 35 900 by the vehicle driver, as follows:");
//            mPrinter.sendCommand("TEXT 7 0 80 930 > 2 wheels  -  P50.00/park");
//            mPrinter.sendCommand("TEXT 7 0 80 960 > 3-4 wheels  -  P100.00/park");
//            mPrinter.sendCommand("TEXT 7 0 0 990 6) Loss of parking ticket will be charged");
//            mPrinter.sendCommand("TEXT 7 0 35 1020 P250.00 penalty and is subject to an");
//            mPrinter.sendCommand("TEXT 7 0 35 1050 investigation by the Mall Security. The");
//            mPrinter.sendCommand("TEXT 7 0 35 1080 concerned driver must present LTO issued");
//            mPrinter.sendCommand("TEXT 7 0 35 1110 drivers license, Certificate of Registration");
//            mPrinter.sendCommand("TEXT 7 0 35 1140 and the current year Official Receipt.Failure");
//            mPrinter.sendCommand("TEXT 7 0 35 1170 to present said documents maybe reffered to");
//            mPrinter.sendCommand("TEXT 7 0 35 1200 the TCPO by the Mall Security.");
//            mPrinter.sendCommand("TEXT 7 0 0 1230 7) Vehicles left overnight in the Parking Area");
//            mPrinter.sendCommand("TEXT 7 0 35 1260 (Surface) will be charged a penalty P500.00");
//            mPrinter.sendCommand("TEXT 7 0 35 1290 per cut off time violation aside from the ");
//            mPrinter.sendCommand("TEXT 7 0 35 1320 reg. parking fees accumulated every parking");
//            mPrinter.sendCommand("TEXT 7 0 35 1350 day; and also subject to investigation.");
//            mPrinter.sendCommand("TEXT 7 0 0 1380 8) Lock your vehicle properly and do not leave");
//            mPrinter.sendCommand("TEXT 7 0 35 1410 your parking ticket inside.");
//            mPrinter.sendCommand("TEXT 7 0 0 1440 9) The Car Park management is not responsible");
//            mPrinter.sendCommand("TEXT 7 0 35 1470 for the loss or damage to your parked vehicle");
//            mPrinter.sendCommand("TEXT 7 0 35 1500 its accessories and private properties inside");
//            mPrinter.sendCommand("TEXT 7 0 0 1530 10) Acceptance of this parking ticket");
//            mPrinter.sendCommand("TEXT 7 0 35 1560 constitutes acknowledgement by the holder");
//            mPrinter.sendCommand("TEXT 7 0 35 1590 that he/she has read carefully, understood");
//            mPrinter.sendCommand("TEXT 7 0 35 1620 fully and will willingly comply with the");
//            mPrinter.sendCommand("TEXT 7 0 35 1650 parking rules.");
//            mPrinter.sendCommand("CENTER");
//            mPrinter.sendCommand("BARCODE 128 3 1 75 0 1700 "+splitted_transactions[1]);
//            mPrinter.sendCommand("TEXT 5 0 0 1780 "+splitted_transactions[1]);
//            mPrinter.sendCommand("PRINT");
        } catch (Exception e) {
            // Handle communications error here.
            e.printStackTrace();
        }
    }

    public void  print_penalty_overnight(){
        try
        {
            if(splitted_transactions[2].equals("100"))
            {
                vehicle_type = "4 WHEELED";
                rate = "20";
            }
            else
            {
                vehicle_type = "2 WHEELED";
                rate = "10";
            }
            int total_penalty = Integer.parseInt(splitted_transactions[7]);
            int total_charge = Integer.parseInt(splitted_transactions[9]);
            int lost_ticket=Integer.parseInt(splitted_transactions[11]);
            int amountDue=total_charge+total_penalty+lost_ticket;
            int total_hrs = Integer.parseInt(splitted_transactions[8]);
            int total_no_of_hours =total_hrs+2;
            String str_total_no_of_hours = String.valueOf(total_no_of_hours);
            mPrinter.sendCommand("! U1 setvar \"device.languages\" \"zpl\"\r\n");
            mPrinter.sendCommand("^XA");
            mPrinter.sendCommand("^LL810");
            mPrinter.sendCommand("^POI");
            mPrinter.sendCommand("^A0N,30,22^FB550,15,0,C,0^FD\\&PLAZA MARCELA\\&" +
                    "Pamaong, Corner Belderol St, Tagbilaran City, Bohol\\&" +
                    "Prop: PLAZA MARCELA\\&" +
                    "TIN: 000-254-327-009\\&" +
                    "\\&\\&BASEMENT\\&" +
                    "PAY PARKING BILLING STATEMENT\\&" +
                    "********************************************\\&^FS");
            mPrinter.sendCommand("^FO0,290^ABN,21,10^FB550,15,0,L,0^FDDate/Time^FS");
            mPrinter.sendCommand("^FO240,290^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[0]+"^FS");
            mPrinter.sendCommand("^FO0,310^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,340^ABN,21,10^FB550,15,0,L,0^FDPlate No.^FS");
            mPrinter.sendCommand("^FO240,340^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[1]+"^FS");
            mPrinter.sendCommand("^FO0,370^ABN,21,10^FB550,15,0,L,0^FDNo. of Wheels^FS");
            mPrinter.sendCommand("^FO240,370^ABN,21,10^FB550,15,0,L,0^FD: "+vehicle_type+"^FS");
            mPrinter.sendCommand("^FO0,400^ABN,21,10^FB550,15,0,L,0^FDTicket No.^FS");
            mPrinter.sendCommand("^FO240,400^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[3]+"^FS");
            mPrinter.sendCommand("^FO0,430^ABN,21,10^FB550,15,0,L,0^FDDate/Time IN^FS");
            mPrinter.sendCommand("^FO240,430^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[4]+"^FS");
            mPrinter.sendCommand("^FO0,460^ABN,21,10^FB550,15,0,L,0^FDDate/Time OUT^FS");
            mPrinter.sendCommand("^FO240,460^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[5]+"^FS");
            mPrinter.sendCommand("^FO0,490^ABN,21,10^FB550,15,0,L,0^FDRate per Hour^FS");
            mPrinter.sendCommand("^FO240,490^ABN,21,10^FB550,15,0,L,0^FD: "+rate+".00^FS");
            mPrinter.sendCommand("^FO0,520^ABN,21,10^FB550,15,0,L,0^FDTotal No. of Hrs^FS");
            mPrinter.sendCommand("^FO240,520^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[10]+"hr(s).^FS");
            mPrinter.sendCommand("^FO0,550^ABN,21,10^FB550,15,0,L,0^FDTotal No. of hrs in Excess^FS");
            mPrinter.sendCommand("^FO240,550^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[8]+"hr(s)^FS");
            //=====start
            mPrinter.sendCommand("^FO0,580^ABN,21,10^FB550,15,0,L,0^FDCharge^FS");
            mPrinter.sendCommand("^FO240,580^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[9]+".00^FS");
            mPrinter.sendCommand("^FO0,610^ABN,21,10^FB550,15,0,L,0^FDPenalty: PR No. 6^FS");
            mPrinter.sendCommand("^FO240,610^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[7]+".00^FS");
            mPrinter.sendCommand("^FO0,640^ABN,21,10^FB550,15,0,L,0^FDOvernight Parking^FS");
            mPrinter.sendCommand("^FO0,660^A0N,30,22^FB550,15,0,L,0^FD---------------------------^FS");
            mPrinter.sendCommand("^FO0,690^A0N,30,20^FB550,15,0,L,0^FDAmount Due^FS");
            mPrinter.sendCommand("^FO240,690^ABN,21,10^FB550,15,0,L,0^FD: PHP "+amountDue+".00^FS");
            mPrinter.sendCommand("^FO0,710^A0N,30,20^FB550,15,0,L,0^FD---------------------------^FS");
            mPrinter.sendCommand("^FO0,740^A0N,30,2 2^FB550,15,0,C,0^FDPLEASE ASK FOR AN OFFICIAL RECEIPT^FS");
            mPrinter.sendCommand("^XZ");

        } catch (Exception e) {
            // Handle communications error here.
            e.printStackTrace();
        }
    }
    public void  reprint_penalty_overnight(){
        try
        {
            if(splitted_transactions[2].equals("100"))
            {
                vehicle_type = "4 WHEELED";
                rate = "20";
            }
            else
            {
                vehicle_type = "2 WHEELED";
                rate = "10";
            }
            int total_penalty = Integer.parseInt(splitted_transactions[7]);
            int total_charge = Integer.parseInt(splitted_transactions[9]);
            int lost_ticket=Integer.parseInt(splitted_transactions[11]);
            int amountDue=total_charge+total_penalty+lost_ticket;
            int total_hrs = Integer.parseInt(splitted_transactions[8]);
            int total_no_of_hours =total_hrs+2;
            String str_total_no_of_hours = String.valueOf(total_no_of_hours);
            mPrinter.sendCommand("! U1 setvar \"device.languages\" \"zpl\"\r\n");
            mPrinter.sendCommand("^XA");
            mPrinter.sendCommand("^LL810");
            mPrinter.sendCommand("^POI");
            mPrinter.sendCommand("^FO-1,4^A0N,25,14^FDReprinted^FS");
            mPrinter.sendCommand("^A0N,30,22^FB550,15,0,C,0^FD\\&PLAZA MARCELA\\&" +
                    "Pamaong, Corner Belderol St, Tagbilaran City, Bohol\\&" +
                    "Prop: PLAZA MARCELA\\&" +
                    "TIN: 000-254-327-009\\&" +
                    "\\&\\&BASEMENT\\&" +
                    "PAY PARKING BILLING STATEMENT\\&" +
                    "********************************************\\&^FS");
            mPrinter.sendCommand("^FO0,290^ABN,21,10^FB550,15,0,L,0^FDDate/Time^FS");
            mPrinter.sendCommand("^FO240,290^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[0]+"^FS");
            mPrinter.sendCommand("^FO0,310^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,340^ABN,21,10^FB550,15,0,L,0^FDPlate No.^FS");
            mPrinter.sendCommand("^FO240,340^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[1]+"^FS");
            mPrinter.sendCommand("^FO0,370^ABN,21,10^FB550,15,0,L,0^FDNo. of Wheels^FS");
            mPrinter.sendCommand("^FO240,370^ABN,21,10^FB550,15,0,L,0^FD: "+vehicle_type+"^FS");
            mPrinter.sendCommand("^FO0,400^ABN,21,10^FB550,15,0,L,0^FDTicket No.^FS");
            mPrinter.sendCommand("^FO240,400^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[3]+"^FS");
            mPrinter.sendCommand("^FO0,430^ABN,21,10^FB550,15,0,L,0^FDDate/Time IN^FS");
            mPrinter.sendCommand("^FO240,430^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[4]+"^FS");
            mPrinter.sendCommand("^FO0,460^ABN,21,10^FB550,15,0,L,0^FDDate/Time OUT^FS");
            mPrinter.sendCommand("^FO240,460^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[5]+"^FS");
            mPrinter.sendCommand("^FO0,490^ABN,21,10^FB550,15,0,L,0^FDRate per Hour^FS");
            mPrinter.sendCommand("^FO240,490^ABN,21,10^FB550,15,0,L,0^FD: "+rate+".00^FS");
            mPrinter.sendCommand("^FO0,520^ABN,21,10^FB550,15,0,L,0^FDTotal No. of Hrs^FS");
            mPrinter.sendCommand("^FO240,520^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[10]+"hr(s).^FS");
            mPrinter.sendCommand("^FO0,550^ABN,21,10^FB550,15,0,L,0^FDTotal No. of hrs in Excess^FS");
            mPrinter.sendCommand("^FO240,550^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[8]+"hr(s)^FS");
            //=====start
            mPrinter.sendCommand("^FO0,580^ABN,21,10^FB550,15,0,L,0^FDCharge^FS");
            mPrinter.sendCommand("^FO240,580^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[9]+".00^FS");
            mPrinter.sendCommand("^FO0,610^ABN,21,10^FB550,15,0,L,0^FDPenalty: PR No. 6^FS");
            mPrinter.sendCommand("^FO240,610^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[7]+".00^FS");
            mPrinter.sendCommand("^FO0,640^ABN,21,10^FB550,15,0,L,0^FDOvernight Parking^FS");
            mPrinter.sendCommand("^FO0,660^A0N,30,22^FB550,15,0,L,0^FD---------------------------^FS");
            mPrinter.sendCommand("^FO0,690^A0N,30,20^FB550,15,0,L,0^FDAmount Due^FS");
            mPrinter.sendCommand("^FO240,690^ABN,21,10^FB550,15,0,L,0^FD: PHP "+amountDue+".00^FS");
            mPrinter.sendCommand("^FO0,710^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,740^A0N,30,22^FB550,15,0,C,0^FDPLEASE ASK FOR AN OFFICIAL RECEIPT^FS");
            mPrinter.sendCommand("^XZ");

        } catch (Exception e) {
            // Handle communications error here.
            e.printStackTrace();
        }
    }
    public void print_penalty_lostTicket(){
        try
        {
            if(splitted_transactions[2].equals("100"))
            {
                vehicle_type = "4 WHEELED";
                rate = "20";
            }
            else
            {
                vehicle_type = "2 WHEELED";
                rate = "10";
            }
            int total_penalty = Integer.parseInt(splitted_transactions[7]);
            int total_charge = Integer.parseInt(splitted_transactions[9]);
            int lost_ticket=Integer.parseInt(splitted_transactions[11]);
            int amountDue=total_charge+total_penalty+lost_ticket;
            int total_hrs = Integer.parseInt(splitted_transactions[8]);
            int total_no_of_hours =total_hrs+2;
            String str_total_no_of_hours = String.valueOf(total_no_of_hours);
            mPrinter.sendCommand("! U1 setvar \"device.languages\" \"zpl\"\r\n");
            mPrinter.sendCommand("^XA");
            mPrinter.sendCommand("^LL810");
            mPrinter.sendCommand("^POI");
            mPrinter.sendCommand("^A0N,30,22^FB550,15,0,C,0^FD\\&PLAZA MARCELA\\&" +
                    "Pamaong, Corner Belderol St, Tagbilaran City, Bohol\\&" +
                    "Prop: PLAZA MARCELA\\&" +
                    "TIN: 000-254-327-009\\&" +
                    "\\&\\&BASEMENT\\&" +
                    "PAY PARKING BILLING STATEMENT\\&" +
                    "********************************************\\&^FS");
            mPrinter.sendCommand("^FO0,290^ABN,21,10^FB550,15,0,L,0^FDDate/Time^FS");
            mPrinter.sendCommand("^FO240,290^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[0]+"^FS");
            mPrinter.sendCommand("^FO0,310^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,340^ABN,21,10^FB550,15,0,L,0^FDPlate No.^FS");
            mPrinter.sendCommand("^FO240,340^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[1]+"^FS");
            mPrinter.sendCommand("^FO0,370^ABN,21,10^FB550,15,0,L,0^FDNo. of Wheels^FS");
            mPrinter.sendCommand("^FO240,370^ABN,21,10^FB550,15,0,L,0^FD: "+vehicle_type+"^FS");
            mPrinter.sendCommand("^FO0,400^ABN,21,10^FB550,15,0,L,0^FDTicket No.^FS");
            mPrinter.sendCommand("^FO240,400^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[3]+"^FS");
            mPrinter.sendCommand("^FO0,430^ABN,21,10^FB550,15,0,L,0^FDDate/Time IN^FS");
            mPrinter.sendCommand("^FO240,430^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[4]+"^FS");
            mPrinter.sendCommand("^FO0,460^ABN,21,10^FB550,15,0,L,0^FDDate/Time OUT^FS");
            mPrinter.sendCommand("^FO240,460^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[5]+"^FS");
            mPrinter.sendCommand("^FO0,490^ABN,21,10^FB550,15,0,L,0^FDRate per Hour^FS");
            mPrinter.sendCommand("^FO240,490^ABN,21,10^FB550,15,0,L,0^FD: "+rate+".00^FS");
            mPrinter.sendCommand("^FO0,520^ABN,21,10^FB550,15,0,L,0^FDTotal No. of Hrs^FS");
            mPrinter.sendCommand("^FO240,520^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[10]+"hr(s).^FS");
            mPrinter.sendCommand("^FO0,550^ABN,21,10^FB550,15,0,L,0^FDTotal No. of hrs in Excess^FS");
            mPrinter.sendCommand("^FO240,550^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[8]+"hr(s)^FS");
            //=====start
            mPrinter.sendCommand("^FO0,580^ABN,21,10^FB550,15,0,L,0^FDCharge^FS");
            mPrinter.sendCommand("^FO240,580^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[9]+".00^FS");
            mPrinter.sendCommand("^FO0,610^ABN,21,10^FB550,15,0,L,0^FDPenalty: PR No. 5^FS");
            mPrinter.sendCommand("^FO240,610^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[11]+".00^FS");
            mPrinter.sendCommand("^FO0,640^ABN,21,10^FB550,15,0,L,0^FDPenalty Lost Parking Ticket^FS");
            mPrinter.sendCommand("^FO0,660^A0N,30,22^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,690^A0N,30,20^FB550,15,0,L,0^FDAmount Due^FS");
            mPrinter.sendCommand("^FO240,690^ABN,21,10^FB550,15,0,L,0^FD: PHP "+amountDue+".00^FS");
            mPrinter.sendCommand("^FO0,710^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,740^A0N,30,22^FB550,15,0,C,0^FDPLEASE ASK FOR AN OFFICIAL RECEIPT^FS");
            mPrinter.sendCommand("^XZ");

        } catch (Exception e) {
            // Handle communications error here.
            e.printStackTrace();
        }
    }
    public void reprint_penalty_lostTicket(){
        try
        {
            if(splitted_transactions[2].equals("100"))
            {
                vehicle_type = "4 WHEELED";
                rate = "20";
            }
            else
            {
                vehicle_type = "2 WHEELED";
                rate = "10";
            }
            int total_penalty = Integer.parseInt(splitted_transactions[7]);
            int total_charge = Integer.parseInt(splitted_transactions[9]);
            int lost_ticket=Integer.parseInt(splitted_transactions[11]);
            int amountDue=total_charge+total_penalty+lost_ticket;
            int total_hrs = Integer.parseInt(splitted_transactions[8]);
            int total_no_of_hours =total_hrs+2;
            String str_total_no_of_hours = String.valueOf(total_no_of_hours);
            mPrinter.sendCommand("! U1 setvar \"device.languages\" \"zpl\"\r\n");
            mPrinter.sendCommand("^XA");
            mPrinter.sendCommand("^LL810");
            mPrinter.sendCommand("^POI");
            mPrinter.sendCommand("^FO-1,4^A0N,25,14^FDReprinted^FS");
            mPrinter.sendCommand("^A0N,30,22^FB550,15,0,C,0^FD\\&PLAZA MARCELA\\&" +
                    "Pamaong, Corner Belderol St, Tagbilaran City, Bohol\\&" +
                    "Prop: PLAZA MARCELA\\&" +
                    "TIN: 000-254-327-009\\&" +
                    "\\&\\&BASEMENT\\&" +
                    "PAY PARKING BILLING STATEMENT\\&" +
                    "********************************************\\&^FS");
            mPrinter.sendCommand("^FO0,290^ABN,21,10^FB550,15,0,L,0^FDDate/Time^FS");
            mPrinter.sendCommand("^FO240,290^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[0]+"^FS");
            mPrinter.sendCommand("^FO0,310^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,340^ABN,21,10^FB550,15,0,L,0^FDPlate No.^FS");
            mPrinter.sendCommand("^FO240,340^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[1]+"^FS");
            mPrinter.sendCommand("^FO0,370^ABN,21,10^FB550,15,0,L,0^FDNo. of Wheels^FS");
            mPrinter.sendCommand("^FO240,370^ABN,21,10^FB550,15,0,L,0^FD: "+vehicle_type+"^FS");
            mPrinter.sendCommand("^FO0,400^ABN,21,10^FB550,15,0,L,0^FDTicket No.^FS");
            mPrinter.sendCommand("^FO240,400^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[3]+"^FS");
            mPrinter.sendCommand("^FO0,430^ABN,21,10^FB550,15,0,L,0^FDDate/Time IN^FS");
            mPrinter.sendCommand("^FO240,430^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[4]+"^FS");
            mPrinter.sendCommand("^FO0,460^ABN,21,10^FB550,15,0,L,0^FDDate/Time OUT^FS");
            mPrinter.sendCommand("^FO240,460^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[5]+"^FS");
            mPrinter.sendCommand("^FO0,490^ABN,21,10^FB550,15,0,L,0^FDRate per Hour^FS");
            mPrinter.sendCommand("^FO240,490^ABN,21,10^FB550,15,0,L,0^FD: "+rate+".00^FS");
            mPrinter.sendCommand("^FO0,520^ABN,21,10^FB550,15,0,L,0^FDTotal No. of Hrs^FS");
            mPrinter.sendCommand("^FO240,520^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[10]+"hr(s).^FS");
            mPrinter.sendCommand("^FO0,550^ABN,21,10^FB550,15,0,L,0^FDTotal No. of Hrs in Excess^FS");
            mPrinter.sendCommand("^FO240,550^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[8]+"hr(s)^FS");
            //=====start
            mPrinter.sendCommand("^FO0,580^ABN,21,10^FB550,15,0,L,0^FDCharge^FS");
            mPrinter.sendCommand("^FO240,580^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[9]+".00^FS");
            mPrinter.sendCommand("^FO0,610^ABN,21,10^FB550,15,0,L,0^FDPenalty: PR No. 5^FS");
            mPrinter.sendCommand("^FO240,610^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[11]+".00^FS");
            mPrinter.sendCommand("^FO0,640^ABN,21,10^FB550,15,0,L,0^FDPenalty Lost Parking Ticket^FS");
            mPrinter.sendCommand("^FO0,660^A0N,30,22^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,690^A0N,30,20^FB550,15,0,L,0^FDAmount Due^FS");
            mPrinter.sendCommand("^FO240,690^ABN,21,10^FB550,15,0,L,0^FD: PHP "+amountDue+".00^FS");
            mPrinter.sendCommand("^FO0,710^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,740^A0N,30,22^FB550,15,0,C,0^FDPLEASE ASK FOR AN OFFICIAL RECEIPT^FS");
            mPrinter.sendCommand("^XZ");
        } catch (Exception e) {
            // Handle communications error here.
            e.printStackTrace();
        }
    }
    public void print_charge_pm(){
        try
        {
            if(splitted_transactions[2].equals("100"))
            {
                vehicle_type = "4 WHEELED";
                rate = "20";
            }
            else
            {
                vehicle_type = "2 WHEELED";
                rate = "10";
            }
            int total_penalty = Integer.parseInt(splitted_transactions[7]);
            int total_charge = Integer.parseInt(splitted_transactions[9]);
            int lost_ticket=Integer.parseInt(splitted_transactions[11]);
            int amountDue=total_charge+total_penalty+lost_ticket;
            int total_hrs = Integer.parseInt(splitted_transactions[8]);
            int total_no_of_hours =total_hrs+2;
            String str_total_no_of_hours = String.valueOf(total_no_of_hours);
            mPrinter.sendCommand("! U1 setvar \"device.languages\" \"zpl\"\r\n");
            mPrinter.sendCommand("^XA");
            mPrinter.sendCommand("^LL750");
            mPrinter.sendCommand("^POI");
            mPrinter.sendCommand("^A0N,30,22^FB550,15,0,C,0^FD\\&PLAZA MARCELA\\&" +
                    "Pamaong, Corner Belderol St, Tagbilaran City, Bohol\\&" +
                    "Prop: PLAZA MARCELA\\&" +
                    "TIN: 000-254-327-009\\&" +
                    "\\&\\&BASEMENT\\&" +
                    "PAY PARKING BILLING STATEMENT\\&" +
                    "********************************************\\&^FS");
            mPrinter.sendCommand("^FO0,290^ABN,21,10^FB550,15,0,L,0^FDDate/Time^FS");
            mPrinter.sendCommand("^FO240,290^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[0]+"^FS");
            mPrinter.sendCommand("^FO0,310^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,340^ABN,21,10^FB550,15,0,L,0^FDPlate No.^FS");
            mPrinter.sendCommand("^FO240,340^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[1]+"^FS");
            mPrinter.sendCommand("^FO0,370^ABN,21,10^FB550,15,0,L,0^FDNo. of Wheels^FS");
            mPrinter.sendCommand("^FO240,370^ABN,21,10^FB550,15,0,L,0^FD: "+vehicle_type+"^FS");
            mPrinter.sendCommand("^FO0,400^ABN,21,10^FB550,15,0,L,0^FDTicket No.^FS");
            mPrinter.sendCommand("^FO240,400^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[3]+"^FS");
            mPrinter.sendCommand("^FO0,430^ABN,21,10^FB550,15,0,L,0^FDDate/Time IN^FS");
            mPrinter.sendCommand("^FO240,430^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[4]+"^FS");
            mPrinter.sendCommand("^FO0,460^ABN,21,10^FB550,15,0,L,0^FDDate/Time OUT^FS");
            mPrinter.sendCommand("^FO240,460^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[5]+"^FS");
            mPrinter.sendCommand("^FO0,490^ABN,21,10^FB550,15,0,L,0^FDRate per Hour^FS");
            mPrinter.sendCommand("^FO240,490^ABN,21,10^FB550,15,0,L,0^FD: "+rate+".00^FS");
            mPrinter.sendCommand("^FO0,520^ABN,21,10^FB550,15,0,L,0^FDTotal No. of Hrs^FS");
            mPrinter.sendCommand("^FO240,520^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[10]+"hr(s).^FS");
            mPrinter.sendCommand("^FO0,550^ABN,21,10^FB550,15,0,L,0^FDTotal No. of hrs in Excess^FS");
            mPrinter.sendCommand("^FO240,550^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[8]+"hr(s)^FS");
            //=====start
            mPrinter.sendCommand("^FO0,580^ABN,21,10^FB550,15,0,L,0^FDCharge^FS");
            mPrinter.sendCommand("^FO240,580^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[9]+".00^FS");
            mPrinter.sendCommand("^FO0,600^A0N,30,22^FB550,15,0,L,0^FD---------------------------^FS");
            mPrinter.sendCommand("^FO0,630^ABN,21,10^FB550,15,0,L,0^FDAmount Due^FS");
            mPrinter.sendCommand("^FO240,630^ABN,21,10^FB550,15,0,L,0^FD: PHP "+amountDue+".00^FS");
            mPrinter.sendCommand("^FO0,650^A0N,30,22^FB550,15,0,L,0^FD---------------------------^FS");
            mPrinter.sendCommand("^FO0,680^A0N,30,22^FB550,15,0,C,0^FDPLEASE ASK FOR AN OFFICIAL RECEIPT^FS");
            mPrinter.sendCommand("^XZ");
        } catch (Exception e) {
            // Handle communications error here.
            e.printStackTrace();
        }
    }
    public void reprint_charge_pm(){
        try
        {
            if(splitted_transactions[2].equals("100"))
            {
                vehicle_type = "4 WHEELED";
                rate = "20";
            }
            else
            {
                vehicle_type = "2 WHEELED";
                rate = "10";
            }
            int total_penalty = Integer.parseInt(splitted_transactions[7]);
            int total_charge = Integer.parseInt(splitted_transactions[9]);
            int lost_ticket=Integer.parseInt(splitted_transactions[11]);
            int amountDue=total_charge+total_penalty+lost_ticket;
            int total_hrs = Integer.parseInt(splitted_transactions[8]);
            int total_no_of_hours =total_hrs+2;
            String str_total_no_of_hours = String.valueOf(total_no_of_hours);
            mPrinter.sendCommand("! U1 setvar \"device.languages\" \"zpl\"\r\n");
            mPrinter.sendCommand("^XA");
            mPrinter.sendCommand("^LL750");
            mPrinter.sendCommand("^POI");
            mPrinter.sendCommand("^FO-1,4^A0N,25,14^FDReprinted^FS");
            mPrinter.sendCommand("^A0N,30,22^FB550,15,0,C,0^FD\\&PLAZA MARCELA\\&" +
                    "Pamaong, Corner Belderol St, Tagbilaran City, Bohol\\&" +
                    "Prop: PLAZA MARCELA\\&" +
                    "TIN: 000-254-327-009\\&" +
                    "\\&\\&BASEMENT\\&" +
                    "PAY PARKING BILLING STATEMENT\\&" +
                    "********************************************\\&^FS");
            mPrinter.sendCommand("^FO0,290^ABN,21,10^FB550,15,0,L,0^FDDate/Time^FS");
            mPrinter.sendCommand("^FO240,290^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[0]+"^FS");
            mPrinter.sendCommand("^FO0,310^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,340^ABN,21,10^FB550,15,0,L,0^FDPlate No.^FS");
            mPrinter.sendCommand("^FO240,340^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[1]+"^FS");
            mPrinter.sendCommand("^FO0,370^ABN,21,10^FB550,15,0,L,0^FDNo. of Wheels^FS");
            mPrinter.sendCommand("^FO240,370^ABN,21,10^FB550,15,0,L,0^FD: "+vehicle_type+"^FS");
            mPrinter.sendCommand("^FO0,400^ABN,21,10^FB550,15,0,L,0^FDTicket No.^FS");
            mPrinter.sendCommand("^FO240,400^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[3]+"^FS");
            mPrinter.sendCommand("^FO0,430^ABN,21,10^FB550,15,0,L,0^FDDate/Time IN^FS");
            mPrinter.sendCommand("^FO240,430^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[4]+"^FS");
            mPrinter.sendCommand("^FO0,460^ABN,21,10^FB550,15,0,L,0^FDDate/Time OUT^FS");
            mPrinter.sendCommand("^FO240,460^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[5]+"^FS");
            mPrinter.sendCommand("^FO0,490^ABN,21,10^FB550,15,0,L,0^FDRate per Hour^FS");
            mPrinter.sendCommand("^FO240,490^ABN,21,10^FB550,15,0,L,0^FD: "+rate+".00^FS");
            mPrinter.sendCommand("^FO0,520^ABN,21,10^FB550,15,0,L,0^FDTotal No. of Hrs^FS");
            mPrinter.sendCommand("^FO240,520^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[10]+"hr(s).^FS");
            mPrinter.sendCommand("^FO0,550^ABN,21,10^FB550,15,0,L,0^FDTotal No. of hrs in Excess^FS");
            mPrinter.sendCommand("^FO240,550^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[8]+"hr(s)^FS");
            //=====start
            mPrinter.sendCommand("^FO0,580^ABN,21,10^FB550,15,0,L,0^FDCharge^FS");
            mPrinter.sendCommand("^FO240,580^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[9]+".00^FS");
            mPrinter.sendCommand("^FO0,600^A0N,30,22^FB550,15,0,L,0^FD---------------------------^FS");
            mPrinter.sendCommand("^FO0,630^ABN,21,10^FB550,15,0,L,0^FDAmount Due^FS");
            mPrinter.sendCommand("^FO240,630^ABN,21,10^FB550,15,0,L,0^FD: PHP "+amountDue+".00^FS");
            mPrinter.sendCommand("^FO0,650^A0N,30,22^FB550,15,0,L,0^FD---------------------------^FS");
            mPrinter.sendCommand("^FO0,680^A0N,30,22^FB550,15,0,C,0^FDPLEASE ASK FOR AN OFFICIAL RECEIPT^FS");
            mPrinter.sendCommand("^XZ");
        } catch (Exception e) {
            // Handle communications error here.
            e.printStackTrace();
        }
    }
    public void print_penalty_pm()
    {
        try
        {
            if(splitted_transactions[2].equals("100"))
            {
                vehicle_type = "4 WHEELED";
                rate = "20";
            }
            else
            {
                vehicle_type = "2 WHEELED";
                rate = "10";
            }
            int total_penalty = Integer.parseInt(splitted_transactions[7]);
            int total_charge = Integer.parseInt(splitted_transactions[9]);
            int lost_ticket=Integer.parseInt(splitted_transactions[11]);
            int amountDue=total_charge+total_penalty+lost_ticket;
            int total_hrs = Integer.parseInt(splitted_transactions[8]);
            int total_no_of_hours =total_hrs+2;
            String str_total_no_of_hours = String.valueOf(total_no_of_hours);
            mPrinter.sendCommand("! U1 setvar \"device.languages\" \"zpl\"\r\n");
            mPrinter.sendCommand("^XA");
            mPrinter.sendCommand("^LL870");
            mPrinter.sendCommand("^POI");
            mPrinter.sendCommand("^A0N,30,22^FB550,15,0,C,0^FD\\&PLAZA MARCELA\\&" +
                    "Pamaong, Corner Belderol St, Tagbilaran City, Bohol\\&" +
                    "Prop: PLAZA MARCELA\\&" +
                    "TIN: 000-254-327-009\\&" +
                    "\\&\\&BASEMENT\\&" +
                    "PAY PARKING BILLING STATEMENT\\&" +
                    "********************************************\\&^FS");
            mPrinter.sendCommand("^FO0,290^ABN,21,10^FB550,15,0,L,0^FDDate/Time^FS");
            mPrinter.sendCommand("^FO240,290^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[0]+"^FS");
            mPrinter.sendCommand("^FO0,310^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,340^ABN,21,10^FB550,15,0,L,0^FDPlate No.^FS");
            mPrinter.sendCommand("^FO240,340^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[1]+"^FS");
            mPrinter.sendCommand("^FO0,370^ABN,21,10^FB550,15,0,L,0^FDNo. of Wheels^FS");
            mPrinter.sendCommand("^FO240,370^ABN,21,10^FB550,15,0,L,0^FD: "+vehicle_type+"^FS");
            mPrinter.sendCommand("^FO0,400^ABN,21,10^FB550,15,0,L,0^FDTicket No.^FS");
            mPrinter.sendCommand("^FO240,400^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[3]+"^FS");
            mPrinter.sendCommand("^FO0,430^ABN,21,10^FB550,15,0,L,0^FDDate/Time IN^FS");
            mPrinter.sendCommand("^FO240,430^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[4]+"^FS");
            mPrinter.sendCommand("^FO0,460^ABN,21,10^FB550,15,0,L,0^FDDate/Time OUT^FS");
            mPrinter.sendCommand("^FO240,460^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[5]+"^FS");
            mPrinter.sendCommand("^FO0,490^ABN,21,10^FB550,15,0,L,0^FDRate per Hour^FS");
            mPrinter.sendCommand("^FO240,490^ABN,21,10^FB550,15,0,L,0^FD: "+rate+".00^FS");
            mPrinter.sendCommand("^FO0,520^ABN,21,10^FB550,15,0,L,0^FDTotal No. of Hrs^FS");
            mPrinter.sendCommand("^FO240,520^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[10]+"hr(s).^FS");
            mPrinter.sendCommand("^FO0,550^ABN,21,10^FB550,15,0,L,0^FDTotal No. of hrs in Excess^FS");
            mPrinter.sendCommand("^FO240,550^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[8]+"hr(s)^FS");
            //=====start
            mPrinter.sendCommand("^FO0,580^ABN,21,10^FB550,15,0,L,0^FDCharge^FS");
            mPrinter.sendCommand("^FO240,580^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[9]+".00^FS");
            mPrinter.sendCommand("^FO0,610^ABN,21,10^FB550,15,0,L,0^FDPenalty: PR No. 6^FS");
            mPrinter.sendCommand("^FO240,610^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[7]+".00^FS");
            mPrinter.sendCommand("^FO0,640^ABN,21,10^FB550,15,0,L,0^FDOvernight Parking^FS");
            mPrinter.sendCommand("^FO0,670^ABN,21,10^FB550,15,0,L,0^FDPenalty: PR No.5^FS");
            mPrinter.sendCommand("^FO240,670^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[11]+".00^FS");
            mPrinter.sendCommand("^FO0,700^ABN,21,10^FB550,15,0,L,0^FDLost Parking Ticket^FS");
            mPrinter.sendCommand("^FO0,720^A0N,30,22^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,750^A0N,30,20^FB550,15,0,L,0^FDAmount Due^FS");
            mPrinter.sendCommand("^FO240,750^ABN,21,10^FB550,15,0,L,0^FD: PHP "+amountDue+".00^FS");
            mPrinter.sendCommand("^FO0,770^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,800^A0N,30,22^FB550,15,0,C,0^FDPLEASE ASK FOR AN OFFICIAL RECEIPT^FS");
            mPrinter.sendCommand("^XZ");

        } catch (Exception e) {
            // Handle communications error here.
            e.printStackTrace();
        }
    }
    public void reprint_penalty_pm()
    {
        try
        {
            if(splitted_transactions[2].equals("100"))
            {
                vehicle_type = "4 WHEELED";
                rate = "20";
            }
            else
            {
                vehicle_type = "2 WHEELED";
                rate = "10";
            }
            int total_penalty = Integer.parseInt(splitted_transactions[7]);
            int total_charge = Integer.parseInt(splitted_transactions[9]);
            int lost_ticket=Integer.parseInt(splitted_transactions[11]);
            int amountDue=total_charge+total_penalty+lost_ticket;
            int total_hrs = Integer.parseInt(splitted_transactions[8]);
            int total_no_of_hours =total_hrs+2;
            String str_total_no_of_hours = String.valueOf(total_no_of_hours);
            mPrinter.sendCommand("! U1 setvar \"device.languages\" \"zpl\"\r\n");
            mPrinter.sendCommand("^XA");
            mPrinter.sendCommand("^LL870");
            mPrinter.sendCommand("^POI");
            mPrinter.sendCommand("^FO-1,4^A0N,25,14^FDReprinted^FS");
            mPrinter.sendCommand("^A0N,30,22^FB550,15,0,C,0^FD\\&PLAZA MARCELA\\&" +
                    "Pamaong, Corner Belderol St, Tagbilaran City, Bohol\\&" +
                    "Prop: PLAZA MARCELA\\&" +
                    "TIN: 000-254-327-009\\&" +
                    "\\&\\&BASEMENT\\&" +
                    "PAY PARKING BILLING STATEMENT\\&" +
                    "********************************************\\&^FS");
            mPrinter.sendCommand("^FO0,290^ABN,21,10^FB550,15,0,L,0^FDDate/Time^FS");
            mPrinter.sendCommand("^FO240,290^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[0]+"^FS");
            mPrinter.sendCommand("^FO0,310^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,340^ABN,21,10^FB550,15,0,L,0^FDPlate No.^FS");
            mPrinter.sendCommand("^FO240,340^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[1]+"^FS");
            mPrinter.sendCommand("^FO0,370^ABN,21,10^FB550,15,0,L,0^FDNo. of Wheels^FS");
            mPrinter.sendCommand("^FO240,370^ABN,21,10^FB550,15,0,L,0^FD: "+vehicle_type+"^FS");
            mPrinter.sendCommand("^FO0,400^ABN,21,10^FB550,15,0,L,0^FDTicket No.^FS");
            mPrinter.sendCommand("^FO240,400^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[3]+"^FS");
            mPrinter.sendCommand("^FO0,430^ABN,21,10^FB550,15,0,L,0^FDDate/Time IN^FS");
            mPrinter.sendCommand("^FO240,430^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[4]+"^FS");
            mPrinter.sendCommand("^FO0,460^ABN,21,10^FB550,15,0,L,0^FDDate/Time OUT^FS");
            mPrinter.sendCommand("^FO240,460^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[5]+"^FS");
            mPrinter.sendCommand("^FO0,490^ABN,21,10^FB550,15,0,L,0^FDRate per Hour^FS");
            mPrinter.sendCommand("^FO240,490^ABN,21,10^FB550,15,0,L,0^FD: "+rate+".00^FS");
            mPrinter.sendCommand("^FO0,520^ABN,21,10^FB550,15,0,L,0^FDTotal No. of Hrs^FS");
            mPrinter.sendCommand("^FO240,520^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[10]+"hr(s).^FS");
            mPrinter.sendCommand("^FO0,550^ABN,21,10^FB550,15,0,L,0^FDTotal No. of hrs in Excess^FS");
            mPrinter.sendCommand("^FO240,550^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[8]+"hr(s)^FS");
            //=====start
            mPrinter.sendCommand("^FO0,580^ABN,21,10^FB550,15,0,L,0^FDCharge^FS");
            mPrinter.sendCommand("^FO240,580^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[9]+".00^FS");
            mPrinter.sendCommand("^FO0,610^ABN,21,10^FB550,15,0,L,0^FDPenalty: PR No. 6^FS");
            mPrinter.sendCommand("^FO240,610^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[7]+".00^FS");
            mPrinter.sendCommand("^FO0,640^ABN,21,10^FB550,15,0,L,0^FDOvernight Parking^FS");
            mPrinter.sendCommand("^FO0,670^ABN,21,10^FB550,15,0,L,0^FDPenalty: PR No.5^FS");
            mPrinter.sendCommand("^FO240,670^ABN,21,10^FB550,15,0,L,0^FD: "+splitted_transactions[11]+".00^FS");
            mPrinter.sendCommand("^FO0,700^ABN,21,10^FB550,15,0,L,0^FDLost Parking Ticket^FS");
            mPrinter.sendCommand("^FO0,720^A0N,30,22^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,750^A0N,30,20^FB550,15,0,L,0^FDAmount Due^FS");
            mPrinter.sendCommand("^FO240,750^ABN,21,10^FB550,15,0,L,0^FD: PHP "+amountDue+".00^FS");
            mPrinter.sendCommand("^FO0,770^A0N,30,20^FB550,15,0,L,0^FD------------------------------^FS");
            mPrinter.sendCommand("^FO0,800^A0N,30,22^FB550,15,0,C,0^FDPLEASE ASK FOR AN OFFICIAL RECEIPT^FS");
            mPrinter.sendCommand("^XZ");
        } catch (Exception e) {
            // Handle communications error here.
            e.printStackTrace();
        }
    }

    //     <------------- Plaza Marcela ------------->

    //     <------------- ALTA CITTA ------------->

//    public void print_ticket_alta()
//    {
//        try
//        {
//            if(splitted_transactions[6].equals("100"))
//            {
//                vehicle_type = "4 WHEELED";
//            }
//            else
//            {
//                vehicle_type = "2 WHEELED";
//            }
//
//            //COUPON TICKET...
//            mPrinter.sendCommand("! 0 200 200 560 1");
//            mPrinter.sendCommand("CENTER");
//            mPrinter.sendCommand("TEXT 5 0 48 30 ALTA CITTA");
//            mPrinter.sendCommand("TEXT 5 0 48 60 SURFACE PAY PARKING");
//            mPrinter.sendCommand("LEFT");
//            mPrinter.sendCommand("TEXT 5 0 0 120 Ticket#: " + splitted_transactions[0]);
//            mPrinter.sendCommand("TEXT 5 0 0 160 Date Issued: " + splitted_transactions[3]+" / "+splitted_transactions[4]);
//            mPrinter.sendCommand("TEXT 5 0 0 200 Valid Until: " + splitted_transactions[5]);
//            mPrinter.sendCommand("CENTER");
//            mPrinter.sendCommand("TEXT 5 0 0 270 ********************************************");
//            mPrinter.sendCommand("TEXT 5 0 0 320 CONSUMABLE COUPON");
//            mPrinter.sendCommand("TEXT 5 0 0 350" + "Php " + splitted_transactions[6] + ".00");
//            mPrinter.sendCommand("BARCODE 128 3 1 75 0 400 " +splitted_transactions[1]);
//            mPrinter.sendCommand("TEXT 5 0 0 480 " + splitted_transactions[1]);
//            mPrinter.sendCommand("WAIT 24");
//            mPrinter.sendCommand("SETFF 10 0");
//            mPrinter.sendCommand("FORM");
//            mPrinter.sendCommand("PRINT");
//
//
////            RULES & REGULATION TICKET...
//            mPrinter.sendCommand("! 0 200 200 1850 1");
//            mPrinter.sendCommand("CENTER");
//            mPrinter.sendCommand("TEXT 5 0 48 20 ALTA CITTA");
//            mPrinter.sendCommand("TEXT 5 0 48 50 Add: CPG Avenue, Tagbilaran City, Bohol");
//            mPrinter.sendCommand("TEXT 5 0 48 80 Prop: Alta Citta Mall");
//            mPrinter.sendCommand("TEXT 5 0 48 110 TIN: 000-254-327-009");
//            mPrinter.sendCommand("TEXT 5 0 48 190 S U R F A C E  P A R K I N G  T I C K E T");
//            mPrinter.sendCommand("TEXT 5 0 0 220 ********************************************");
//            mPrinter.sendCommand("LEFT");
//            mPrinter.sendCommand("TEXT 5 0 0 260 Plate No.    : " + splitted_transactions[2]);
//            mPrinter.sendCommand("TEXT 5 0 0 300 Type            : " + vehicle_type);
//            mPrinter.sendCommand("TEXT 5 0 0 340 Ticket No.  : " + splitted_transactions[0]);
//            mPrinter.sendCommand("TEXT 5 0 0 380 Trans Code : " + splitted_transactions[1]);
//            mPrinter.sendCommand("TEXT 5 0 0 420 Date              : " + splitted_transactions[3]);
//            mPrinter.sendCommand("TEXT 5 0 0 460 Time             : " + splitted_transactions[4]);
//            mPrinter.sendCommand("TEXT 5 0 0 500 --------------------------------------------");
//            mPrinter.sendCommand("TEXT 7 0 0 530 Parking Rules:");
//            mPrinter.sendCommand("TEXT 7 0 0 560 1) Regular parking day starts at 7:30am and");
//            mPrinter.sendCommand("TEXT 7 0 35 590 ends at 10:30pm cut-off time.");
//            mPrinter.sendCommand("TEXT 7 0 0 620 2) 2 Wheeled Vehicles will enjoy 2 hours free");
//            mPrinter.sendCommand("TEXT 7 0 35 650 parking and will be charged P10.00 for each");
//            mPrinter.sendCommand("TEXT 7 0 35 680 succeeding hour.");
//            mPrinter.sendCommand("TEXT 7 0 0 710 3) 3 to 4 Wheeled Vehicles will enjoy 2 hours");
//            mPrinter.sendCommand("TEXT 7 0 35 740 free parking and will be charged P20.00 for");
//            mPrinter.sendCommand("TEXT 7 0 35 770 each succeeding hour.");
//            mPrinter.sendCommand("TEXT 7 0 0 800 4) A fraction of an hour parked is considered");
//            mPrinter.sendCommand("TEXT 7 0 35 830 1 hour.");
//            mPrinter.sendCommand("TEXT 7 0 0 860 5) Upon entry, a consumable coupon must be paid");
//            mPrinter.sendCommand("TEXT 7 0 35 890 by the vehicle driver, as follows:");
//            mPrinter.sendCommand("TEXT 7 0 80 920 > 2 wheels  -  P50.00/park");
//            mPrinter.sendCommand("TEXT 7 0 80 950 > 3-4 wheels  -  P100.00/park");
//            mPrinter.sendCommand("TEXT 7 0 0 980 6) Loss of parking ticket will be charged");
//            mPrinter.sendCommand("TEXT 7 0 35 1010 P250.00 penalty and is subject to an");
//            mPrinter.sendCommand("TEXT 7 0 35 1040 investigation by the Mall Security. The");
//            mPrinter.sendCommand("TEXT 7 0 35 1070 concerned driver must present LTO issued");
//            mPrinter.sendCommand("TEXT 7 0 35 1100 drivers license, Certificate of Registration");
//            mPrinter.sendCommand("TEXT 7 0 35 1130 and the current year Official Receipt.Failure");
//            mPrinter.sendCommand("TEXT 7 0 35 1160 to present said documents maybe reffered to");
//            mPrinter.sendCommand("TEXT 7 0 35 1190 the TCPO by the Mall Security.");
//            mPrinter.sendCommand("TEXT 7 0 0 1220 7) Vehicles left overnight in the Parking Area");
//            mPrinter.sendCommand("TEXT 7 0 35 1250 (Surface) will be charged a penalty P500.00");
//            mPrinter.sendCommand("TEXT 7 0 35 1280 per cut off time violation aside from the ");
//            mPrinter.sendCommand("TEXT 7 0 35 1310 reg. parking fees accumulated every parking");
//            mPrinter.sendCommand("TEXT 7 0 35 1340 day; and also subject to investigation.");
//            mPrinter.sendCommand("TEXT 7 0 0 1370 8) Lock your vehicle properly and do not leave");
//            mPrinter.sendCommand("TEXT 7 0 35 1400 your parking ticket inside.");
//            mPrinter.sendCommand("TEXT 7 0 0 1430 9) The Car Park management is not responsible");
//            mPrinter.sendCommand("TEXT 7 0 35 1460 for the loss or damage to your parked vehicle");
//            mPrinter.sendCommand("TEXT 7 0 35 1490 its accessories and private properties inside");
//            mPrinter.sendCommand("TEXT 7 0 0 1520 10) Acceptance of this parking ticket");
//            mPrinter.sendCommand("TEXT 7 0 35 1550 constitutes acknowledgement by the holder");
//            mPrinter.sendCommand("TEXT 7 0 35 1580 that he/she has read carefully, understood");
//            mPrinter.sendCommand("TEXT 7 0 35 1610 fully and will willingly comply with the");
//            mPrinter.sendCommand("TEXT 7 0 35 1640 parking rules.");
//            mPrinter.sendCommand("CENTER");
//            mPrinter.sendCommand("BARCODE 128 3 1 75 0 1690 "+splitted_transactions[1]);
//            mPrinter.sendCommand("TEXT 5 0 0 1770 "+splitted_transactions[1]);
//            mPrinter.sendCommand("PRINT");
//        } catch (Exception e) {
//            // Handle communications error here.
//            e.printStackTrace();
//        }
//    }
//
//    public void reprint_ticket_alta()
//    {
//        try
//        {
//            if(splitted_transactions[6].equals("100"))
//            {
//                vehicle_type = "4 WHEELED";
//            }
//            else
//            {
//                vehicle_type = "2 WHEELED";
//            }
//
//            //COUPON TICKET...
//            mPrinter.sendCommand("! 0 200 200 560 1");
//            mPrinter.sendCommand("LEFT");
//            mPrinter.sendCommand("TEXT 5 0 0 10 REPRINTED");
//            mPrinter.sendCommand("CENTER");
//            mPrinter.sendCommand("TEXT 5 0 48 30 ALTA CITTA");
//            mPrinter.sendCommand("TEXT 5 0 48 60 SURFACE PAY PARKING");
//            mPrinter.sendCommand("LEFT");
//            mPrinter.sendCommand("TEXT 5 0 0 120 Ticket#: " + splitted_transactions[0]);
//            mPrinter.sendCommand("TEXT 5 0 0 160 Date Issued: " + splitted_transactions[3] + " / " + splitted_transactions[4]);
//            mPrinter.sendCommand("TEXT 5 0 0 200 Valid Until: " + splitted_transactions[5]);
//            mPrinter.sendCommand("CENTER");
//            mPrinter.sendCommand("TEXT 5 0 0 270 ********************************************");
//            mPrinter.sendCommand("TEXT 5 0 0 320 CONSUMABLE COUPON");
//            mPrinter.sendCommand("TEXT 5 0 0 350" + "Php " + splitted_transactions[6] + ".00");
//            mPrinter.sendCommand("BARCODE 128 3 1 75 0 400 "+splitted_transactions[1]);
//            mPrinter.sendCommand("TEXT 5 0 0 480 "+splitted_transactions[1]);
//            mPrinter.sendCommand("WAIT 24");
//            mPrinter.sendCommand("SETFF 10 0");
//            mPrinter.sendCommand("FORM");
//            mPrinter.sendCommand("PRINT");
////          RULES & REGULATION TICKET...
//            mPrinter.sendCommand("! 0 200 200 1860 1");
//            mPrinter.sendCommand("LEFT");
//            mPrinter.sendCommand("TEXT 5 0 0 10 REPRINTED");
//            mPrinter.sendCommand("CENTER");
//            mPrinter.sendCommand("TEXT 5 0 48 30 ALTA CITTA");
//            mPrinter.sendCommand("TEXT 5 0 48 60 Add: CPG Avenue, Tagbilaran City, Bohol");
//            mPrinter.sendCommand("TEXT 5 0 48 90 Prop: Alta Citta Mall");
//            mPrinter.sendCommand("TEXT 5 0 48 120 TIN: 000-254-327-009");
//            mPrinter.sendCommand("TEXT 5 0 48 200 S U R F A C E  P A R K I N G  T I C K E T");
//            mPrinter.sendCommand("TEXT 5 0 0 230 ********************************************");
//            mPrinter.sendCommand("LEFT");
//            mPrinter.sendCommand("TEXT 5 0 0 270 Plate No.    : " + splitted_transactions[2]);
//            mPrinter.sendCommand("TEXT 5 0 0 310 Type            : " + vehicle_type);
//            mPrinter.sendCommand("TEXT 5 0 0 350 Ticket No.  : " + splitted_transactions[0]);
//            mPrinter.sendCommand("TEXT 5 0 0 390 Trans Code : " + splitted_transactions[1]);
//            mPrinter.sendCommand("TEXT 5 0 0 430 Date              : " + splitted_transactions[3]);
//            mPrinter.sendCommand("TEXT 5 0 0 470 Time             : " + splitted_transactions[4]);
//            mPrinter.sendCommand("TEXT 5 0 0 510 --------------------------------------------");
//            mPrinter.sendCommand("TEXT 7 0 0 540 Parking Rules:");
//            mPrinter.sendCommand("TEXT 7 0 0 570 1) Regular parking day starts at 7:30am and");
//            mPrinter.sendCommand("TEXT 7 0 35 600 ends at 10:30pm cut-off time.");
//            mPrinter.sendCommand("TEXT 7 0 0 630 2) 2 Wheeled Vehicles will enjoy 2 hours free");
//            mPrinter.sendCommand("TEXT 7 0 35 660 parking and will be charged P10.00 for each");
//            mPrinter.sendCommand("TEXT 7 0 35 690 succeeding hour.");
//            mPrinter.sendCommand("TEXT 7 0 0 720 3) 3 to 4 Wheeled Vehicles will enjoy 2 hours");
//            mPrinter.sendCommand("TEXT 7 0 35 750 free parking and will be charged P20.00 for");
//            mPrinter.sendCommand("TEXT 7 0 35 780 each succeeding hour.");
//            mPrinter.sendCommand("TEXT 7 0 0 810 4) A fraction of an hour parked is considered");
//            mPrinter.sendCommand("TEXT 7 0 35 840 1 hour.");
//            mPrinter.sendCommand("TEXT 7 0 0 870 5) Upon entry, a consumable coupon must be paid");
//            mPrinter.sendCommand("TEXT 7 0 35 900 by the vehicle driver, as follows:");
//            mPrinter.sendCommand("TEXT 7 0 80 930 > 2 wheels  -  P50.00/park");
//            mPrinter.sendCommand("TEXT 7 0 80 960 > 3-4 wheels  -  P100.00/park");
//            mPrinter.sendCommand("TEXT 7 0 0 990 6) Loss of parking ticket will be charged");
//            mPrinter.sendCommand("TEXT 7 0 35 1020 P250.00 penalty and is subject to an");
//            mPrinter.sendCommand("TEXT 7 0 35 1050 investigation by the Mall Security. The");
//            mPrinter.sendCommand("TEXT 7 0 35 1080 concerned driver must present LTO issued");
//            mPrinter.sendCommand("TEXT 7 0 35 1110 drivers license, Certificate of Registration");
//            mPrinter.sendCommand("TEXT 7 0 35 1140 and the current year Official Receipt.Failure");
//            mPrinter.sendCommand("TEXT 7 0 35 1170 to present said documents maybe reffered to");
//            mPrinter.sendCommand("TEXT 7 0 35 1200 the TCPO by the Mall Security.");
//            mPrinter.sendCommand("TEXT 7 0 0 1230 7) Vehicles left overnight in the Parking Area");
//            mPrinter.sendCommand("TEXT 7 0 35 1260 (Surface) will be charged a penalty P500.00");
//            mPrinter.sendCommand("TEXT 7 0 35 1290 per cut off time violation aside from the ");
//            mPrinter.sendCommand("TEXT 7 0 35 1320 reg. parking fees accumulated every parking");
//            mPrinter.sendCommand("TEXT 7 0 35 1350 day; and also subject to investigation.");
//            mPrinter.sendCommand("TEXT 7 0 0 1380 8) Lock your vehicle properly and do not leave");
//            mPrinter.sendCommand("TEXT 7 0 35 1410 your parking ticket inside.");
//            mPrinter.sendCommand("TEXT 7 0 0 1440 9) The Car Park management is not responsible");
//            mPrinter.sendCommand("TEXT 7 0 35 1470 for the loss or damage to your parked vehicle");
//            mPrinter.sendCommand("TEXT 7 0 35 1500 its accessories and private properties inside");
//            mPrinter.sendCommand("TEXT 7 0 0 1530 10) Acceptance of this parking ticket");
//            mPrinter.sendCommand("TEXT 7 0 35 1560 constitutes acknowledgement by the holder");
//            mPrinter.sendCommand("TEXT 7 0 35 1590 that he/she has read carefully, understood");
//            mPrinter.sendCommand("TEXT 7 0 35 1620 fully and will willingly comply with the");
//            mPrinter.sendCommand("TEXT 7 0 35 1650 parking rules.");
//            mPrinter.sendCommand("CENTER");
//            mPrinter.sendCommand("BARCODE 128 3 1 75 0 1700 "+splitted_transactions[1]);
//            mPrinter.sendCommand("TEXT 5 0 0 1780 "+splitted_transactions[1]);
//            mPrinter.sendCommand("PRINT");
//
//        } catch (Exception e) {
//            // Handle communications error here.
//            e.printStackTrace();
//        }
//    }
//
//    public void print_penalty_alta()
//    {
//        try
//        {
//            if(splitted_transactions[2].equals("100"))
//            {
//                vehicle_type = "4 WHEELED";
//                rate = "20";
//            }
//            else
//            {
//                vehicle_type = "2 WHEELED";
//                rate = "10";
//            }
//
//            int total_penalty = Integer.parseInt(splitted_transactions[6]);
//            int parking_per_hour = Integer.parseInt(rate);
//
//            int total_no_of_hours = total_penalty / parking_per_hour;
//            String str_total_no_of_hours = String.valueOf(total_no_of_hours);
//            //Billing Statement...
//            mPrinter.sendCommand("! 0 200 200 850 1");
//            mPrinter.sendCommand("CENTER");
//            mPrinter.sendCommand("TEXT 5 0 48 20 ALTA CITTA");
//            mPrinter.sendCommand("TEXT 5 0 48 50 Add: CPG Avenue, Tagbilaran City, Bohol");
//            mPrinter.sendCommand("TEXT 5 0 48 80 Prop: Alta Citta Mall");
//            mPrinter.sendCommand("TEXT 5 0 48 110 TIN: 000-254-327-009");
//
//            mPrinter.sendCommand("TEXT 5 0 48 190 SURFACE");
//            mPrinter.sendCommand("TEXT 5 0 48 220 PAY PARKING BILLING STATEMENT");
//            mPrinter.sendCommand("TEXT 5 0 0 250 ********************************************");
//            mPrinter.sendCommand("LEFT");
//            mPrinter.sendCommand("TEXT 5 0 0 290 Date/Time              : "+splitted_transactions[0]);
//            mPrinter.sendCommand("TEXT 5 0 0 320 --------------------------------------------");
//            mPrinter.sendCommand("TEXT 5 0 0 360 Plate No.                : "+splitted_transactions[1]);
//            mPrinter.sendCommand("TEXT 5 0 0 400 No. of Wheels        : "+vehicle_type);
//            mPrinter.sendCommand("TEXT 5 0 0 440 Ticket No.              : "+splitted_transactions[3]);
//            mPrinter.sendCommand("TEXT 5 0 0 480 Date/Time IN         : "+splitted_transactions[4]);
//            mPrinter.sendCommand("TEXT 5 0 0 520 Date/Time OUT     : "+splitted_transactions[5]);
//            mPrinter.sendCommand("TEXT 5 0 0 560 Total No. of Hrs    : "+str_total_no_of_hours+"hr(s).");
//            mPrinter.sendCommand("TEXT 5 0 0 600 Parking Rate/Hr    : "+rate+".00");
//            mPrinter.sendCommand("TEXT 5 0 0 630 --------------------------------------------");
//
//            mPrinter.sendCommand("TEXT 5 0 0 680 Amount Due           : Php "+splitted_transactions[6]+".00");
//            mPrinter.sendCommand("TEXT 5 0 0 720 --------------------------------------------");
//
//            mPrinter.sendCommand("TEXT 5 0 48 760 PLEASE ASK FOR AN OFFICIAL RECEIPT");
//            mPrinter.sendCommand("SETFF 10 0");
//            mPrinter.sendCommand("FORM");
//            mPrinter.sendCommand("PRINT");
//
//        } catch (Exception e) {
//            // Handle communications error here.
//            e.printStackTrace();
//        }
//    }
//
//    public void reprint_penalty_alta()
//    {
//        try
//        {
//            if((splitted_transactions[2].equals("100")))
//            {
//                vehicle_type = "4 WHEELED";
//                rate = "20";
//            }
//            else
//            {
//                vehicle_type = "2 WHEELED";
//                rate = "10";
//            }
//
//            int total_penalty = Integer.parseInt(splitted_transactions[6]);
//            int parking_per_hour = Integer.parseInt(rate);
//
//            int total_no_of_hours = total_penalty / parking_per_hour;
//            String str_total_no_of_hours = String.valueOf(total_no_of_hours);
//
//            //Billing Statement...
//            mPrinter.sendCommand("! 0 200 200 860 1");
//            mPrinter.sendCommand("LEFT");
//            mPrinter.sendCommand("TEXT 5 0 0 10 REPRINTED");
//            mPrinter.sendCommand("CENTER");
//            mPrinter.sendCommand("TEXT 5 0 48 30 ALTA CITTA");
//            mPrinter.sendCommand("TEXT 5 0 48 60 Add: CPG Avenue, Tagbilaran City, Bohol");
//            mPrinter.sendCommand("TEXT 5 0 48 90 Prop: Alta Citta Mall");
//            mPrinter.sendCommand("TEXT 5 0 48 120 TIN: 000-254-327-009");
//
//            mPrinter.sendCommand("TEXT 5 0 48 200 SURFACE");
//            mPrinter.sendCommand("TEXT 5 0 48 230 PAY PARKING BILLING STATEMENT");
//            mPrinter.sendCommand("TEXT 5 0 0 260 ********************************************");
//
//            mPrinter.sendCommand("LEFT");
//            mPrinter.sendCommand("TEXT 5 0 0 300 Date/Time              : "+splitted_transactions[0]);
//            mPrinter.sendCommand("TEXT 5 0 0 330 --------------------------------------------");
//
//            mPrinter.sendCommand("TEXT 5 0 0 370 Plate No.                : "+splitted_transactions[1]);
//            mPrinter.sendCommand("TEXT 5 0 0 410 No. of Wheels        : "+vehicle_type);
//            mPrinter.sendCommand("TEXT 5 0 0 450 Ticket No.              : "+splitted_transactions[3]);
//            mPrinter.sendCommand("TEXT 5 0 0 490 Date/Time IN         : "+splitted_transactions[4]);
//            mPrinter.sendCommand("TEXT 5 0 0 530 Date/Time OUT     : "+splitted_transactions[5]);
//            mPrinter.sendCommand("TEXT 5 0 0 570 Total No. of Hrs    : "+str_total_no_of_hours+"hr(s).");
//            mPrinter.sendCommand("TEXT 5 0 0 610 Parking Rate/Hr    : "+rate+".00");
//            mPrinter.sendCommand("TEXT 5 0 0 640 --------------------------------------------");
//
//            mPrinter.sendCommand("TEXT 5 0 0 690 Amount Due           : Php "+splitted_transactions[6]+".00");
//            mPrinter.sendCommand("TEXT 5 0 0 730 --------------------------------------------");
//
//            mPrinter.sendCommand("TEXT 5 0 48 770 PLEASE ASK FOR AN OFFICIAL RECEIPT");
//            mPrinter.sendCommand("SETFF 10 0");
//            mPrinter.sendCommand("FORM");
//            mPrinter.sendCommand("PRINT");
//
//        } catch (Exception e) {
//            // Handle communications error here.
//            e.printStackTrace();
//        }
//    }

    //     <------------- ALTA CITTA ------------->
}
