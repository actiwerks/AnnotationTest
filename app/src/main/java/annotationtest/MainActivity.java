package annotationtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import annotationtest.processor.Contract;
import annotationtest.processor.ContractArray;
import annotationtest.processor.generated.ContractAuxContract;

public class MainActivity extends AppCompatActivity implements ContractAuxContract {

    final String AUX_CONTRACT = "AuxContract";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contractMethod();
    }

    @Contract("MainContract")
    public void contractMethod() {
        Log.i("MainActivity", "contractMethod");
    }

    @Contract(AUX_CONTRACT)
    public void auxContractMethod() {

    }

    @ContractArray({@Contract("MainContract"), @Contract(AUX_CONTRACT)})
    public void doubleAgent() {

    }

    @Contract
    public void unnamedContractMethod() {

    }

 }
