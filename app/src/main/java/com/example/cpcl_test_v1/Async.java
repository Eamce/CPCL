package com.example.cpcl_test_v1;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Spinner;
import android.widget.Toast;

public class Async extends AsyncTask<Void, Void, Boolean> {
    private Context ctx;
    ProgressDialog p;
    private PrintUtils mPrintUtils;
    private Spinner formatSpinner ;
    private String bluetoothAddr;

    public Async(Context ctx)
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
    protected Boolean doInBackground(Void... voids) {
        for(int a=0 ; a<1000000000 ; a++)
        {
            a++;
        }
//        mPrintUtils.setPrinter(bluetoothAddr);
//        mPrintUtils.printNavigator(((GenericTable) formatSpinner.getSelectedItem()).getKod());
//        mPrintUtils.freePriner();
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        p.dismiss();
        if(result)
        {
            // Do something awesome here
            Toast.makeText(ctx, "Download complete", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(ctx,"Download failed, network issue",Toast.LENGTH_SHORT).show();
        }
    }
}
