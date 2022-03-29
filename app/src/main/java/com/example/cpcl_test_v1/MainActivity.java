package com.example.cpcl_test_v1;


/**
 * Imports packages from Android, Zebra LinkOS SDK, and Java libraries.
 * To import the Zebra SDK libraries, the ZSDK_ANDROID_API.jar must be added to the project folder
 *      and module dependency must be added via File -> Project Structure -> app -> Dependencies
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;

import org.json.JSONArray;
import org.json.JSONException;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.time.temporal.ChronoUnit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity{

    public static boolean debug =false;
    final Context context = this;
    public static String PRINTER_NAME ="ZEBRAMZ320";
    Ajax mo;
    Globalvars globalvars;
    SQLiteDatabase mydatabase;
    ProgressDialog p;
    private AlertDialog successDialog;
    private static final int PERMISSION_REQUEST_CODE = 100;
    ProgressDialog progressDialog;

    //Initialize Global Variables

    private BluetoothDeviceArrayAdapter adapter;
    private BroadcastReceiver broadcastReceiver;
    private ListView listview;
    private String bluetoothAddr;
    private String macAddress;
    private PrintUtils mPrintUtils;
    private Spinner formatSpinner ;
    private Context mContext = this;


    /**
     * onCreate() contains several processes that begin once the application is started
     * Contains EventListener/ Event procedures
     *
     * @param savedInstanceState
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        globalvars = new Globalvars((Context)this,(Activity)this);
        PrintUtils printUtils = new PrintUtils();
        printUtils.globalvars = globalvars;

        //pd = new ProgressDialog(this);
        //  Initialize the list view and its adapter
        listview = (ListView) findViewById(R.id.lvPairedDevices);
        adapter = new BluetoothDeviceArrayAdapter(context,getPairedPrinters());
        listview.setAdapter(adapter);

        formatSpinner = (Spinner) findViewById(R.id.formatSpinner);

        print_loading();

        //  Print a configuration label when a Bluetooth printer is clicked
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final BluetoothDevice item = (BluetoothDevice) parent.getItemAtPosition(position);
                if (item != null && item.getAddress() != null && isBluetoothPrinter(item)) {
                    mPrintUtils = PrintUtils.getInstance();
                    bluetoothAddr = item.getAddress();
                    if(debug)
                    {
                        createAlert(context, "Print", "Are you sure you want to print a test page?",true);
                    }
                    else
                    {
                        print_loading2();
                        read_textfile();
//                        mPrintUtils.setPrinter(bluetoothAddr);
////                        mPrintUtils.printNavigator(((GenericTable) formatSpinner.getSelectedItem()).getKod());
////                        mPrintUtils.freePriner();
                    }
                }
            }
        });
        fillFormatSpinner();
        //  Create a BroadcastReciever to refresh the ListView when device is paired/unpaired
        broadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                refreshList();
            }
        };
        //  Registers Bluetooth devices to ListView once paired
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadcastReceiver, filter);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        Date now = new Date();
        String fileName = formatter.format(now) + ".txt";//like 2016_01_12.txt
        String sBody = "Sample";
//        writeToSD(sBody);
    }

    public void print_loading() {
        Async1 async = new Async1(this);
        async.execute();
    }
    public void print_loading2() {
        Async2 async2 = new Async2(this);
        async2.execute();
    }

    public class Async1 extends AsyncTask {
        private Context ctx;

        public Async1(Context ctx)
        {
            this.ctx=ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(ctx);
            p.setMessage("Loading...Please Wait.");
            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            p.setCancelable(false);
            p.show();
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            for(int a=0 ; a<1000000000 ; a++)
            {
                a++;
            }
          //  read_textfile();
            p.dismiss();
            return true;
        }

//        protected void onPostExecute(Boolean result) {
//            super.onPostExecute(result);
//            p.dismiss();
//            if(result)
//            {
//                // Do something awesome here
//                Toast.makeText(ctx, "Download complete", Toast.LENGTH_SHORT).show();
//            }
//            else
//            {
//                Toast.makeText(ctx,"Download failed, network issue",Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    public class Async2 extends AsyncTask {
        private Context ctx;

        public Async2(Context ctx)
        {
            this.ctx=ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(ctx);
            p.setMessage("Printing...Please Wait.");
            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            p.setCancelable(false);
            p.show();
        }
        @SuppressLint("WrongThread")
        @Override
        protected Object doInBackground(Object[] objects) {
            for(int a=0 ; a<1000000000 ; a++)
            {
                a++;
            }
            mPrintUtils.setPrinter(bluetoothAddr);
            mPrintUtils.printNavigator(((GenericTable) formatSpinner.getSelectedItem()).getKod());
            mPrintUtils.freePriner();
            p.dismiss();
            finish();
            return true;
        }
    }
    public void read_textfile(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    File sdcard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdcard.getAbsolutePath()+"/Android/PayParking/");
                    if(dir.exists()) {
                        File file = new File(dir, "transaction_type.txt");
                        FileOutputStream os = null;
                        StringBuilder text = new StringBuilder();
                        StringBuilder text1 = new StringBuilder();
                        String str_trans = "";
                      //  String line= "";
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String line="";
                            while ((line = br.readLine()) != null) {
                                text.append(line);
                                File file1 = new File(dir, "transactions.txt");
                                BufferedReader br1 = new BufferedReader(new FileReader(file1));
                                String line1;
                                while ((line1 = br1.readLine()) != null) {
                                    text1.append(line1);
                                    text1.append(',');
                                }
                                text1.deleteCharAt(text1.length() - 1);
                                str_trans = text1.toString();
                                br.close();
                                br1.close();
                            }
                            br.close();
                        } catch (IOException e) {
                            //You'll need to add proper error handling here
                        }
                        finally {
                            globalvars.set("transaction_type",text.toString());
                            globalvars.set("transactions",str_trans);
                        }
                    }
                } else {
                    requestPermission(); // Code for permission
                }
//            } else {
//
//                File sdcard = Environment.getExternalStorageDirectory();
//                File dir = new File(sdcard.getAbsolutePath() + "/text/");
//                if(dir.exists()) {
//                    File file = new File(dir, "sample.log");
//
//                    FileOutputStream os = null;
//                    StringBuilder text = new StringBuilder();
//
//                    try {
//                        BufferedReader br = new BufferedReader(new FileReader(file));
//                        String line;
//
//                        while ((line = br.readLine()) != null) {
//                            text.append(line);
//                            text.append('\n');
//                        }
//                        br.close();
//                    } catch (IOException e) {
//                        //You'll need to add proper error handling here
//                    }
//                    finally {
//                        //progressDialog.dismiss();
//                    }
////                    output.setText(text);
//                }
            }
        }
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to read files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void fillFormatSpinner()
    {
        List<GenericTable> printFormats = new ArrayList<>();
        for(PrintFormats it:PrintFormats.values())
        {
            printFormats.add(new GenericTable(it.name(),it.toString()));
        }

        AdapterSpinnerGeneric formatAdapter = new AdapterSpinnerGeneric(this, printFormats);
        formatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formatSpinner.setAdapter(formatAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshList();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopForegroundDispatch();
    }

    private void showAlreadyPaired(final String serialName) {
        displayToast(String.format("%s is already paired", serialName));
    }

    private void refreshList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                adapter.addAll(getPairedPrinters());
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Checks to see if the given printer is currently paired to the Android device via bluetooth.
     *
     * @param address
     * @return true if the printer is paired
     */

    private boolean isPrinterPaired(String address) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : pairedDevices) {
            if (device.getAddress().replaceAll("[\\p{P}\\p{S}]", "").equalsIgnoreCase(address)) {
                showAlreadyPaired(address);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of all the printers currently paired to the Android device via bluetooth.
     *
     * @return a list of all the printers currently paired to the Android device via bluetooth.
     */

    private ArrayList<BluetoothDevice> getPairedPrinters() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> pairedDevicesList = new ArrayList<BluetoothDevice>();
        for (BluetoothDevice device : pairedDevices) {
            if (isBluetoothPrinter(device))
                pairedDevicesList.add(device);
        }
        return pairedDevicesList;
    }

    /**
     * Determines if the given bluetooth device is a printer
     *
     * @param bluetoothDevice bluetooth device
     * @return true if the bluetooth device is a printer
     */

    private boolean isBluetoothPrinter(BluetoothDevice bluetoothDevice) {
        return bluetoothDevice.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.IMAGING
                || bluetoothDevice.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.UNCATEGORIZED;
    }

    /**
     * findPrinterStatus() contains processes that check the connected printer's current status for two common error states, isHeadOpen and isPaperOut,
     *      and returns a boolean
     *
     * @param conn Established connection. Can be either BluetoothConnection or TcpConnection
     * @return True if no error is found. False if an error is found.
     */

    private boolean findPrinterStatus(final Connection conn){
        try {
            if (ZebraPrinterFactory.getInstance(conn).getCurrentStatus().isHeadOpen) {
                displayToast("ERROR: Printer Head is Open");
                return false;
            }

            else if (ZebraPrinterFactory.getInstance(conn).getCurrentStatus().isPaperOut) {
                displayToast("ERROR: No Media Detected");
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true; // Returns if neither of the above error states is found
    }

    /**
     * displayToast creates a Toast pop up that appears in the center of the screen containing the
     * String message parameter
     * @param message String
     */

    private void displayToast(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast;
                toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        });
    }

    /**
     * Creates an Alert Dialog
     * @param context
     */

    private void createAlert(Context context, String title, String message, final boolean print){

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (print) {
                                        BluetoothConnection conn = new BluetoothConnection(bluetoothAddr);
                                        connectAndPrint(conn);
                                    }
                                    else
                                    {
                                        BluetoothConnection conn = new BluetoothConnection(macAddress);
                                        if (!isPrinterPaired(macAddress))
                                            connectDevice(conn);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        ).start();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                })
                .show();
    }

    /**
     * Opens a connection and prints a configuration label
     * @param conn
     */


//    private void connectAndPrint (Connection conn,String _params){
//        try {
//            // Instantiate connection for given Bluetooth&reg; MAC Address.
//
//            conn.open();
//
//            // Open the connection - physical connection is established here.
//            ZebraPrinter zPrinterIns = ZebraPrinterFactory.getInstance(conn);
//            zPrinterIns.sendCommand("! U1 setv \"device.languages\" \"zpl\"\r\n");
//            Thread.sleep(500);
//
//            zPrinterIns.sendCommand(_params);
//            zPrinterIns.sendCommand("FORM");
//            zPrinterIns.sendCommand("PRINT");
//
//            Thread.sleep(500);
//            conn.close();
//
//        } catch (Exception e) {
//            // Handle communications error here.
//            e.printStackTrace();
//        }
//    }
    private void connectAndPrint (Connection conn){
        try {
            // Instantiate connection for given Bluetooth&reg; MAC Address.

            conn.open();

            // Open the connection - physical connection is established here.
            ZebraPrinter zPrinterIns = ZebraPrinterFactory.getInstance(conn);
            zPrinterIns.sendCommand("! U1 setvar \"device.languages\" \"line_print\"\r\n");
//            zPrinterIns.sendCommand("! U1 setvar \"device.languages\" \"zpl\"\r\n");
//            zPrinterIns.sendCommand("~jc^xa^jus^xz");
            Thread.sleep(500);

            zPrinterIns.sendCommand("! 0 200 200 600 1");
            zPrinterIns.sendCommand("ENCODING GB18030");
            zPrinterIns.sendCommand("TEXT GBUNSG24.CPF 0 20 30 Font: GBUNSG24 ‚t‚u");
            zPrinterIns.sendCommand("ENCODING ASCII");
            zPrinterIns.sendCommand("TEXT 7 0 20 80 Font 7, Size 0");
            zPrinterIns.sendCommand("TEXT 6 0 20 200 Font 6, Size 0");
            zPrinterIns.sendCommand("TEXT 5 0 20 320 Font 5, Size 0");

            zPrinterIns.sendCommand("FORM");
            zPrinterIns.sendCommand("PRINT");


            // Make sure the data got to the printer before closing the connection
            Thread.sleep(500);

            // Close the connection to release resources.
            conn.close();

        } catch (Exception e) {
            // Handle communications error here.
            e.printStackTrace();
        }
    }

    /**
     * Opens a connection to the printer and pulls information
     * @param conn
     */
    private void connectDevice (Connection conn){
        try{
            conn.open();
            Thread.sleep(500);
            conn.close();

        } catch (ConnectionException e) {
            displayToast("ERROR: Unable to connect to Printer");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
