package ar.com.liit.monitordw;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class monitor extends AppCompatActivity {

    private Button button1 = null;
    private boolean alert = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        button1 = (Button) findViewById(R.id.button);
        button1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TextView textView1 = (TextView) findViewById(R.id.textView1);
                textView1.setText("Actualizando...\n\n");
                update();
            }
        });
        update();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private String getValue(JSONObject json, String key1, String key2){
        try {
            if (key2.equals("")){
                if (json.get(key1) instanceof Double) {
                    return ((Double) json.get(key1)).toString();
                }
                return null;
            } else if (json.get(key1) instanceof JSONArray) {
                Object resp = ((JSONObject) ((JSONArray) json.get(key1)).get(0)).get(key2);
                if (resp instanceof Integer){
                    return ((Integer) resp).toString();
                } else if (resp instanceof Double){
                    return ((Double) resp).toString();
                } else {
                    return ((String) resp);
                }
            } else {
                Object resp = (((JSONObject) json.get(key1)).get(key2));
                if (resp instanceof Integer){
                    return ((Integer) resp).toString();
                } else if (resp instanceof Double){
                    return ((Double) resp).toString();
                } else {
                    return ((String) resp);
                }
            }
        } catch (JSONException e){
            return null;
        }
    }

    private String getDifferenceDate(String dateStart, String dateEnd) {

        Date d1 = null;
        Date d2 = null;
        //String d3 = DateFormat.getInstance().format(now);
        try {
            d1 = new SimpleDateFormat("yy-MM-dd HH:mm:ss").parse(dateStart);
            //d2 = new SimpleDateFormat("M/d/yy h:mm aa").parse(DateFormat.getInstance().format(new Date()));
            d2 = new SimpleDateFormat("yy-MM-dd HH:mm:ss").parse(dateEnd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = d2.getTime() - d1.getTime();
        long diffSeconds = (diff / 1000 % 60);
        long diffMinutes = (diff / (60 * 1000) % 60) + ((diffSeconds > 45) ? 1 : 0);
        //long diffHours = diff / (60 * 60 * 1000);
        long diffHours = ((diff / (60 * 60 * 1000)) - (24 * ((diff / (60 * 60 * 1000)) / 24))) + ((diffMinutes > 45) ? 1 : 0);
        long diffDays = ((diff / (60 * 60 * 1000)) / 24)+ ((diffHours > 45) ? 1 : 0);

        String duration = "";
        String Days = "";
        String Hours = "";
        String Minutes = "";
        String Seconds = "";
        boolean error = false;

        if (diffDays > 0) {
            Days = String.valueOf(diffDays) + " dia" + ((diffDays > 1) ? "s, ": ", ");
            this.alert = true;
        } else {
            if (diffHours > 0) {
                Hours = String.valueOf(diffHours) + " hora" + ((diffHours > 1) ? "s, ": ", ");
                this.alert = true;
            } else {
                if (diffMinutes > 0) {
                    Minutes = String.valueOf(diffMinutes) + " minuto" + ((diffMinutes > 1) ? "s, " : ", ");
                    if (diffMinutes > 5) this.alert = true;
                } else {
                    if (diffSeconds > 0) {
                        Seconds = String.valueOf(diffSeconds) + " segundo" + ((diffSeconds > 1) ? "s, " : ", ");
                        this.alert = false;
                    } else {
                        if (diffSeconds <= 0) {
                            this.alert = false;
                            error = true;
                        }
                    }
                }
            }
        }
        //Log.d("getDifferenceDate", Days + Hours + Minutes + Seconds + Instant + "(ultimo:" + ultimo + "hora: " +  dateStart + ")");
        duration = ((error) ? "Error en hora": "Hace " + (Days + Hours + Minutes + Seconds));
        return (duration.endsWith(", ")) ? duration.substring(0, duration.lastIndexOf(",")) : duration;
    }

    private void update() {
        new getInfo().execute("https://dw.liit.com.ar/monitor.php?sadewrfefref=54544jlkkjfelkjfe_Ewdsr");
    }

    class getInfo extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... strURL) {
            String output = "";
            URL url;
            HttpURLConnection urlConnection = null;
            //"https://dw.liit.com.ar/monitor.php?sadewrfefref=54544jlkkjfelkjfe_Ewdsr"
            try {
                System.out.println(strURL[0]);
                url = new URL(strURL[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                //int responseCode = urlConnection.getResponseCode();
                BufferedReader br = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));
                StringBuilder sb = new StringBuilder();
                try {
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                output = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            //textView1.setText("OUTPUT: "+output);
            return output;
        }

        protected void onPostExecute(String jsonStr){

            JSONObject json = null;
            if (!jsonStr.equals("")) {
                try {
                    json = new JSONObject(jsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Iterator<String> iterator = json.keys();
            String dateServer = null;
            Object value = new Object();

            TextView textView1 = (TextView) findViewById(R.id.textView1);
            textView1.setText("");
            TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayoutMonitor);
            tableLayout.removeAllViews();

            //Create Button y textView

            while (iterator.hasNext()) {
                String key = iterator.next();
                value = null;
                try {
                    value = json.get(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (value instanceof JSONObject && ((JSONObject) value).has("emp")) {
                    JSONObject jsonChild = (JSONObject) value;
                    try {
                        String statusEmpresa = "";
                        String horaUltimoProceso = getValue(jsonChild,"ultimosProcesos","hora");
                        //statusEmpresa += ((String)jsonChild.get("nombre")) + "\n";
                        statusEmpresa += "Ultimo Proceso: "+getValue(jsonChild,"ultimosProcesos","cual") + "\n";
                        statusEmpresa += "- "+getDifferenceDate(getValue(jsonChild,"ultimosProcesos","hora"), dateServer) + "\n";
                        statusEmpresa += "- Demora: "+getValue(jsonChild,"ultimosProcesos","demora")+" - Modifico: "+getValue(jsonChild,"ultimosProcesos","modifico")+" - Error: "+getValue(jsonChild,"ultimosProcesos","error")+"\n";
                        statusEmpresa += "Ultimo OK: "+getValue(jsonChild,"ultimoProcesoOK","cual") + "\n";
                        statusEmpresa += "- "+getDifferenceDate(getValue(jsonChild,"ultimoProcesoOK","hora"), dateServer) + "\n";
                        statusEmpresa += "- Demora: "+getValue(jsonChild,"ultimoProcesoOK","demora")+" - Modifico: "+getValue(jsonChild,"ultimoProcesoOK","modifico")+"\n";
                        statusEmpresa += "Ultimo Error "+getValue(jsonChild,"ultimoError","cual") + "\n";
                        statusEmpresa += "- "+getDifferenceDate(getValue(jsonChild,"ultimoError","hora"), dateServer) + "\n";
                        statusEmpresa += "- Demora: "+getValue(jsonChild,"ultimoError","demora")+" - Error: "+getValue(jsonChild,"ultimoError","error")+"\n";
                        statusEmpresa += "Demora promedio dia: "+getValue(jsonChild,"demoraPromedioDia", "")+"\n";
                        statusEmpresa += "Demora total dia: "+getValue(jsonChild,"demoraTotalDia", "")+"\n";
                        statusEmpresa += getDifferenceDate(horaUltimoProceso, dateServer);
                        //statusEmpresa += "\n\n";
                        //textView1.setText(textView1.getText()+statusEmpresa);

                        // --------------------- Creation row (COMPANY)
                        final TableRow trTitle = new TableRow(getBaseContext());
                        trTitle.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                        trTitle.setBackgroundColor(Color.BLUE);
                        // Creation textView
                        final TextView textCompany = new TextView(getBaseContext());
                        textCompany.setText((String)jsonChild.get("nombre"));
                        textCompany.setTypeface(null, Typeface.BOLD_ITALIC);
                        textCompany.setTextSize(18);
                        textCompany.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                        trTitle.addView(textCompany);

                        // --------------------- Creation row (Data Detail)
                        final TableRow trDataDetail = new TableRow(getBaseContext());
                        trDataDetail.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                        // Creation textView
                        final TextView textDataDetail = new TextView(getBaseContext());
                        textDataDetail.setText(statusEmpresa);
                        textDataDetail.setTextColor(Color.BLACK);
                        textDataDetail.setTypeface(null, Typeface.NORMAL);
                        textDataDetail.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                        trDataDetail.addView(textDataDetail);

                        // --------------------- Creation row (TIME PROCESSING)
                        final LinearLayout layoutProcessing = new LinearLayout(getBaseContext());
                        //layoutProcessing.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                        //Label Processing
                        final TextView textLabelProcessing = new TextView(getBaseContext());
                        textLabelProcessing.setText("Procesando: ");
                        textLabelProcessing.setTextColor(Color.BLACK);
                        textLabelProcessing.setTypeface(null, Typeface.NORMAL);
                        //textLabelProcessing.setWidth(10);
;
                        //Processing Value (Boolean + Time elapsed)
                        final TextView textProcessing = new TextView(getBaseContext());
                        textProcessing.setText(((Boolean)jsonChild.get("procesando")) ? "Si " : "No ");
                        textProcessing.setTextColor(((Boolean)jsonChild.get("procesando")) ? Color.GREEN : Color.BLACK);
                        textProcessing.setTypeface(null, Typeface.NORMAL);
                        textProcessing.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        final TextView textProcessingTime = new TextView(getBaseContext());
                        textProcessingTime.setText(getDifferenceDate((String)jsonChild.get("procesandoFecha"), dateServer));
                        textProcessingTime.setTextColor((alert) ? Color.RED : Color.BLACK);
                        textProcessingTime.setTypeface(null, Typeface.NORMAL);
                        //textProcessingTime.setWidth(100);
                        textProcessingTime.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                        layoutProcessing.addView(textLabelProcessing);
                        layoutProcessing.addView(textProcessing);
                        layoutProcessing.addView(textProcessingTime);

                        //Add rows
                        tableLayout.addView(trTitle);
                        tableLayout.addView(trDataDetail);
                        tableLayout.addView(layoutProcessing);

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                } else {
                    if (key.equals("hora")) {
                        dateServer = value.toString();
                    }
                    textView1.setText(textView1.getText()+"Hora actual: "+((String) value)+"\n\n");
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "monitor Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://ar.com.liit.monitordw/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "monitor Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://ar.com.liit.monitordw/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
