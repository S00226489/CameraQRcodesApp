package com.example.labcameraqrcodes;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button buttonScan;
    private TextView resultText;
    private EditText etName, etAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonScan = findViewById(R.id.buttonScan);
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Scan a QR Code");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });

        setupAddressClickListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                resultText.setText("Cancelled");
            } else {
                processScannedData(result.getContents());
            }
        }
    }

    private void processScannedData(String data) {
        try {
            JSONObject obj = new JSONObject(data);
            String title = obj.optString("title", "No title");
            String website = obj.optString("website", "");

            etName.setText(title);
            etAddress.setText(website);
        } catch (Exception e) {
            resultText.setText("Error: " + e.getMessage());
        }
    }

    private void setupAddressClickListener() {
        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = etAddress.getText().toString();
                if (url.startsWith("http")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                } else {
                    resultText.setText("No valid URL to open.");
                }
            }
        });
    }
}
